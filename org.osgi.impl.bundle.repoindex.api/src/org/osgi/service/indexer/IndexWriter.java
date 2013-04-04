package org.osgi.service.indexer;

import java.io.IOException;

public interface IndexWriter {
	
	/** enable pretty-printing: non-gzipped, indented XML */
	public static final String PRETTY = "pretty";

	/** the default repository name */
	public static final String REPOSITORYNAME_DEFAULT = "Untitled";

	/** the name of the OBR XML representation */
	public static final String REPOSITORY_NAME = "repository.name";

	/** the default stylesheet for the OBR XML representation */
	public static final String STYLESHEET_DEFAULT = "http://www.osgi.org/www/obr2html.xsl";

	/** the stylesheet of the OBR XML representation */
	public static final String STYLESHEET = "stylesheet";
	
	String REPOSITORY_INCREMENT_OVERRIDE = "-repository.increment.override";

	/**
	 * Write out the representation of the given resource.
	 * 
	 * @param resource
	 *            a Resource
	 * @throws IOException
	 */
	void write(Resource resource) throws IOException;

	/**
	 * Closes the writer, flushing and closing underlying stream if applicable.
	 * 
	 * {@link IndexWriter#write(Resource)} may not be called after the writer is
	 * closed.
	 * 
	 * @throws IOException
	 */
	void close() throws IOException;
}
