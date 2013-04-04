package org.osgi.service.indexer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

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

	void open(OutputStream out, Map<String, String> config) throws IOException;

	void write(Resource resource) throws IOException;

	void close() throws IOException;
	
}
