package org.osgi.service.indexer.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;

import org.osgi.service.indexer.Capability;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.Requirement;
import org.osgi.service.indexer.Resource;
import org.osgi.service.indexer.impl.types.TypedAttribute;
import org.osgi.service.indexer.impl.util.Indent;
import org.osgi.service.indexer.impl.util.Tag;

/**
 * Writer using OSGi Enterprise R5 index format.
 */
public class DefaultIndexWriter implements IndexWriter {

	private volatile Thread owner;

	private PrintWriter pw;

	private Indent indent;

	public void open(OutputStream out, Map<String, String> config) throws IOException {
		if (owner != null || pw != null)
			throw new RuntimeException("writer is already in use");
		owner = Thread.currentThread();
		
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
		checkThread();
		write(resource, indent.next(), pw);
	}
	
	public static void write(Resource resource, Indent indent, PrintWriter pw) throws IOException {

		Tag resourceTag = new Tag(Schema.ELEM_RESOURCE);
		for (Capability cap : resource.getCapabilities(null)) {
			Tag capTag = new Tag(Schema.ELEM_CAPABILITY);
			capTag.addAttribute(Schema.ATTR_NAMESPACE, cap.getNamespace());

			appendAttributeAndDirectiveTags(capTag, cap.getAttributes(), cap.getDirectives());

			resourceTag.addContent(capTag);
		}

		for (Requirement req : resource.getRequirements(null)) {
			Tag reqTag = new Tag(Schema.ELEM_REQUIREMENT);
			reqTag.addAttribute(Schema.ATTR_NAMESPACE, req.getNamespace());

			appendAttributeAndDirectiveTags(reqTag, req.getAttributes(), req.getDirectives());

			resourceTag.addContent(reqTag);
		}

		resourceTag.print(indent, pw);
	}

	private static void appendAttributeAndDirectiveTags(Tag parentTag, Map<String, Object> attribs, Map<String, String> directives) {
		for (Entry<String, Object> attribEntry : attribs.entrySet()) {
			Tag attribTag = new Tag(Schema.ELEM_ATTRIBUTE);
			attribTag.addAttribute(Schema.ATTR_NAME, attribEntry.getKey());

			TypedAttribute typedAttrib = TypedAttribute.create(attribEntry.getKey(), attribEntry.getValue());
			parentTag.addContent(typedAttrib.toXML());
		}

		for (Entry<String, String> directiveEntry : directives.entrySet()) {
			Tag directiveTag = new Tag(Schema.ELEM_DIRECTIVE);
			directiveTag.addAttribute(Schema.ATTR_NAME, directiveEntry.getKey());
			directiveTag.addAttribute(Schema.ATTR_VALUE, directiveEntry.getValue());
			parentTag.addContent(directiveTag);
		}
	}

	public void close() throws IOException {
		checkThread();

		Tag repoTag = new Tag(Schema.ELEM_REPOSITORY);
		repoTag.printClose(indent, pw);

		pw.flush();
		pw.close();
		pw = null;
		owner = null;
	}

	private void checkThread() {
		if (Thread.currentThread() != owner)
			throw new RuntimeException("writer used from wrong thread");
	}

}
