package org.osgi.service.indexer.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.osgi.resource.Resource;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.impl.util.Indent;
import org.osgi.service.indexer.impl.util.Tag;

/**
 * Writer using OSGi Enterprise R5 index format.
 */
public class DefaultIndexWriter implements IndexWriter {

	private final PrintWriter pw;

	private final Indent indent;

	public DefaultIndexWriter(OutputStream out, Map<String, String> config) throws IOException {
		if (config == null)
			config = new HashMap<String, String>(0);
		
		if (config.get(PRETTY) != null) {
			indent = Indent.PRETTY;
			pw = new PrintWriter(out);
		} else {
			indent = Indent.NONE;
			pw = new PrintWriter(new GZIPOutputStream(out, Deflater.BEST_COMPRESSION));
		}

		pw.print(Schema.XML_PROCESSING_INSTRUCTION);
		Tag repoTag = new Tag(Schema.ELEM_REPOSITORY);

		String repoName = config.get(REPOSITORY_NAME);
		if (repoName == null)
			repoName = REPOSITORYNAME_DEFAULT;
		repoTag.addAttribute(Schema.ATTR_NAME, repoName);

		String increment = config.get(REPOSITORY_INCREMENT_OVERRIDE);
		if (increment == null)
			increment = Long.toString(System.currentTimeMillis());
		repoTag.addAttribute(Schema.ATTR_INCREMENT, increment);

		repoTag.addAttribute(Schema.ATTR_XML_NAMESPACE, Schema.NAMESPACE);

		repoTag.printOpen(indent, pw, false);
	}

	public void write(Resource resource) throws IOException {
		DefaultFragmentWriter.write(resource, indent.next(), pw);
	}


	public void close() throws IOException {
		Tag repoTag = new Tag(Schema.ELEM_REPOSITORY);
		repoTag.printClose(indent, pw);

		pw.flush();
		pw.close();
	}
}
