package org.osgi.service.indexer.impl;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.Resource;
import org.osgi.service.log.LogService;

/**
 * A decorator for IndexWriter ensuring safe access from multiple threads.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public class ConcurrentIndexWriter implements IndexWriter {

	private final IndexWriter target;

	private final LogService log;

	private final BlockingQueue<Resource> queue = new LinkedBlockingQueue<Resource>();

	private final Worker worker = new Worker();

	private volatile boolean open = true;

	private volatile IOException ex = null;

	/**
	 * Creates a thread-safe wrapper for the provided IndexWriter.
	 * 
	 * Invoking this method will submit a task to the provided ExecutorService.
	 * The task will remain active until {@link #close()} is called.
	 * 
	 * @param target
	 *            the IndexWriter to be wrapped.
	 * @param executor
	 *            ExecutorService that will run the background task.
	 * @param log
	 *            a log service for reporting write errors.
	 * @throws IOException
	 *             when the thread invoking the constructor is interrupted while
	 *             waiting for background task startup.
	 */
	public ConcurrentIndexWriter(IndexWriter target, ExecutorService executor, LogService log) throws IOException {
		this.target = target;
		this.log = log;
		synchronized (worker) {
			executor.submit(worker);
			try {
				worker.wait();
			} catch (InterruptedException e) {
				throw new IOException("interrupted while awaiting worker thread start");
			}
		}
	}

	/**
	 * Schedule a resource to be eventually written to the wrapped writer.
	 * 
	 * Resource is written to the wrapped writer by the background task. If an
	 * write error occurs, it will be thrown on all subsequent invocations of
	 * {@link #write(Resource)} method, and additionally on {@link #close()}
	 * method invocation.
	 * 
	 * @param resource
	 *            the resource to be written
	 * @throws IOException
	 *             if this writer is closed, or the wrapped writer reported an
	 *             error on a preceding write operation.
	 */
	public void write(Resource resource) throws IOException {
		IOException e = ex;
		if (e != null)
			throw e;
		if (!open)
			throw new IOException("writer is closed");
		queue.add(resource);
	}

	/**
	 * Write out all pending resources, and close wrapped writer.
	 * 
	 * This method will block until all pending writes are completed and wrapped
	 * writer is closed.
	 * 
	 * @throws IOException
	 *             when an IOException the wrapped writer reported an error on a
	 *             preceding write operation, or on {@link #close()} operation.
	 */
	public void close() throws IOException {
		boolean suppress = false;
		try {
			open = false;
			worker.terminate();
			IOException e = ex;
			if (e != null) {
				suppress = true;
				throw e;
			}
		} finally {
			try {
				target.close();
			} catch (IOException e) {
				// write exceptions are usually more interesting than close
				// exceptions
				if (!suppress)
					throw e;
				else
					log.log(LogService.LOG_ERROR, "exception while closing target writer", e);
			}
		}
	}

	private class Worker implements Runnable {

		private volatile boolean finished = false;

		private volatile Thread thread;

		public void run() {
			synchronized (worker) {
				thread = Thread.currentThread();
				worker.notifyAll();
			}
			try {
				handleQueue();
				synchronized (worker) {
					drainQueue();
					worker.notifyAll();
					finished = true;
				}
			} finally {
				// flip the flag when abandoning thread for whatever reason
				finished = true;
				synchronized (worker) {
					// just in case another thread is already awaiting worker
					// termination
					worker.notifyAll();
				}
			}
		}

		private void handleQueue() {
			while (!Thread.interrupted()) {
				try {
					Resource r = queue.take();
					target.write(r);
				} catch (InterruptedException e) {
					break;
				} catch (IOException e) {
					open = false;
					log.log(LogService.LOG_ERROR, "write error", e);
					ex = e;
					throw new RuntimeException("write error occured, aborting");
				}
			}
		}

		private void drainQueue() {
			Resource r;
			do {
				r = queue.poll();
				if (r != null) {
					try {
						target.write(r);
					} catch (IOException e) {
						log.log(LogService.LOG_ERROR, "write error", e);
						ex = e;
						break;
					}
				}
			} while (r != null);
		}

		private void terminate() throws IOException {
			if (!finished) {
				synchronized (worker) {
					thread.interrupt();
					try {
						worker.wait();
					} catch (InterruptedException e) {
						throw new IOException("interrupted while awaiting worker thread termination");
					}
				}
			}
		}
	}

	// used in ConcurrentIndexWriterTest.testTremination()
	boolean isFinished() {
		return worker.finished;
	}
}
