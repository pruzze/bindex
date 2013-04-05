package org.osgi.service.indexer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * A factory of IndexWriter objects.
 * 
 * @author rafal.krzewski@caltha.pl
 */
public interface IndexWriterFactory {

	/**
	 * Creates an IndexWriter for writing repository index in XML format.
	 * 
	 * Calling this method will cause index header to be immediately written to
	 * the output stream.
	 * 
	 * @param outputStream
	 *            to write the OBR XML representation
	 * @param config
	 *            a set of optional parameters (use the interface constants as
	 *            keys)
	 * @return and IndexWriter implementation
	 * @throws IOException
	 *             if writing the index header fails.
	 */
	IndexWriter newIndexWriter(OutputStream outputStream,
			Map<String, String> config) throws IOException;

	/**
	 * Creates a writer that generates a fragment of index file in OSGi
	 * Enterprise R5 format.
	 * 
	 * Note that the result will be one or more XML <code>resource</code>
	 * elements <em>without</em> a top-level surrounding <code>repository</code>
	 * element. The resulting XML is therefore not well-formed. This method may
	 * be useful for repository managers that wish to (re-)index individual
	 * resources and assemble the XML fragments into a complete repository
	 * document later.
	 * 
	 * @param writer
	 * @return
	 */
	IndexWriter newFragmentIndexWriter(Writer writer);

	/**
	 * Create an IndexWriter decorator allowing safe concurrent access from
	 * multiple threads.
	 * 
	 * Returned writer's {@link IndexWriter#write(Resource)} method will not
	 * block, but rather schedule the resource representation to be eventually
	 * written to the wrapped writer. It's {@link IndexWriter#close()} method
	 * will block until all pending writes are completed and the underlying
	 * writer is closed.
	 * 
	 * @param target
	 *            IndexWriter to be wrapped
	 * @param executor
	 *            an {@code ExecutorService} to be used for spawning a
	 *            background task.
	 * @return an IndexWriter decorator.
	 * @throws IOException
	 */
	IndexWriter newConcurrentIndexWriter(IndexWriter target,
			ExecutorService executor) throws IOException;
}
