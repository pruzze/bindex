package org.osgi.service.indexer.impl;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.osgi.resource.Resource;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.log.LogService;

public class ConcurrentWriterTest extends TestCase {

	private LogService mockLog = mock(LogService.class);
	
	private ExecutorService executor = Executors.newCachedThreadPool();

	public void testTermination() throws Exception {
		IndexWriter nullWriter = mock(IndexWriter.class);
		for (int i = 0; i < 100; i++) {
			ConcurrentIndexWriter conc = new ConcurrentIndexWriter(nullWriter, executor, mockLog);
			conc.close();
			assertTrue(conc.isFinished());
		}
	}

	public void testSingleThreaded() throws Exception {
		Resource resource = mock(Resource.class);
		Collector coll = new Collector();
		ConcurrentIndexWriter conc = new ConcurrentIndexWriter(coll, executor, mockLog);
		final int numSteps = 100;
		for (int j = 0; j < numSteps; j++) {
			conc.write(resource);
		}
		conc.close();
		assertTrue(coll.isClosed());
		assertEquals(numSteps, coll.items().size());
	}

	public void testMultiThreaded() throws Exception {
		final int numThreads = 4;
		final int numSteps = 100;
		final CyclicBarrier start = new CyclicBarrier(numThreads);
		final CyclicBarrier stop = new CyclicBarrier(numThreads + 1);
		final Resource resource = mock(Resource.class);
		final Collector coll = new Collector();
		final ConcurrentIndexWriter conc = new ConcurrentIndexWriter(coll, executor, mockLog);
		for (int i = 0; i < numThreads; i++) {
			executor.submit(new Callable<Void>() {
				public Void call() throws Exception {
					start.await();
					for (int j = 0; j < numSteps; j++) {
						conc.write(resource);
					}
					stop.await();
					return null;
				}
			});
		}
		stop.await();
		conc.close();
		assertTrue(coll.isClosed());
		assertEquals(numThreads * numSteps, coll.items().size());
	}

	public void testWriteException() throws Exception {
		Resource resource = mock(Resource.class);
		IndexWriter errorWriter = mock(IndexWriter.class);
		doThrow(new IOException()).when(errorWriter).write(resource);
		ConcurrentIndexWriter conc = new ConcurrentIndexWriter(errorWriter, executor, mockLog);
		conc.write(resource);
		try {
			conc.close();
			fail("expected IOException");
		} catch (IOException e) {
			// OK
		}
	}

	public void testCloseException() throws Exception {
		Resource resource = mock(Resource.class);
		IndexWriter errorWriter = mock(IndexWriter.class);
		doThrow(new IOException()).when(errorWriter).close();
		ConcurrentIndexWriter conc = new ConcurrentIndexWriter(errorWriter, executor, mockLog);
		conc.write(resource);
		try {
			conc.close();
			fail("expected IOException");
		} catch (IOException e) {
			// OK
		}
	}

	private static class Collector implements IndexWriter {
		private final Queue<Resource> queue = new ConcurrentLinkedQueue<Resource>();

		private boolean closed = false;

		public void write(Resource resource) throws IOException {
			queue.add(resource);
		}

		public void close() throws IOException {
			closed = true;
		}

		public Collection<Resource> items() {
			return queue;
		}

		public boolean isClosed() {
			return closed;
		}
	}
}
