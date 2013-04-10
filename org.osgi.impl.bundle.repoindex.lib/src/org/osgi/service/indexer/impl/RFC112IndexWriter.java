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

public class RFC112IndexWriter implements IndexWriter {

	private final PrintWriter pw;

	private final Indent indent;

	public RFC112IndexWriter(OutputStream out, Map<String, String> config) throws IOException {
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
		indent.print(pw);

		String stylesheet = config.get(STYLESHEET);
		if (stylesheet == null)
			stylesheet = Schema.RFC112.STYLESHEET_DEFAULT;
		pw.print(String.format(Schema.XSL_PROCESSING_INSTRUCTION, stylesheet));

		Tag repoTag = new Tag(Schema.RFC112.ELEM_REPOSITORY);

		String repoName = config.get(REPOSITORY_NAME);
		if (repoName == null)
			repoName = REPOSITORYNAME_DEFAULT;
		repoTag.addAttribute(Schema.RFC112.ATTR_REPOSITORY_NAME, repoName);

		String increment = config.get(REPOSITORY_INCREMENT_OVERRIDE);
		if (increment == null)
			increment = Long.toString(System.currentTimeMillis());
		repoTag.addAttribute(Schema.RFC112.ATTR_REPOSITORY_TIME, increment);

		repoTag.addAttribute(Schema.ATTR_XML_NAMESPACE, Schema.RFC112.NAMESPACE);

		repoTag.printOpen(indent, pw, false);
	}

	public void write(Resource resource) throws IOException {
		RFC112FragmentWriter.write(resource, indent.next(), pw);
	}

	public void close() throws IOException {
		Tag repoTag = new Tag(Schema.RFC112.ELEM_REPOSITORY);
		repoTag.printClose(indent, pw);

		pw.flush();
		pw.close();
	}
}
