package org.osgi.service.indexer.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.impl.types.TypedAttribute;
import org.osgi.service.indexer.impl.util.Indent;
import org.osgi.service.indexer.impl.util.Tag;

/**
 * A writer that generates a fragment of index file in OSGi Enterprise R5
 * format.
 * 
 * Note that the result will be one or more XML <code>resource</code> elements
 * <em>without</em> a top-level surrounding <code>repository</code> element. The
 * resulting XML is therefore not well-formed. This method may be useful for
 * repository managers that wish to (re-)index individual resources and assemble
 * the XML fragments into a complete repository document later.
 * 
 * @param out
 *            the Writer to write the Repository XML representation
 * @param config
 *            a set of optional parameters (use the interface constants as keys)
 */
public class DefaultFragmentWriter implements IndexWriter {

	private final PrintWriter pw;

	public DefaultFragmentWriter(Writer out) {
		if (out instanceof PrintWriter)
			pw = (PrintWriter) out;
		else
			pw = new PrintWriter(out);
	}

	public void write(Resource resource) throws IOException {
		write(resource, Indent.PRETTY, pw);
	}

	public void close() throws IOException {
		pw.close();
	}

	static void write(Resource resource, Indent indent, PrintWriter pw) throws IOException {

		Tag resourceTag = new Tag(Schema.R5.ELEM_RESOURCE);
		for (Capability cap : resource.getCapabilities(null)) {
			Tag capTag = new Tag(Schema.R5.ELEM_CAPABILITY);
			capTag.addAttribute(Schema.R5.ATTR_NAMESPACE, cap.getNamespace());

			appendAttributeAndDirectiveTags(capTag, cap.getAttributes(), cap.getDirectives());

			resourceTag.addContent(capTag);
		}

		for (Requirement req : resource.getRequirements(null)) {
			Tag reqTag = new Tag(Schema.R5.ELEM_REQUIREMENT);
			reqTag.addAttribute(Schema.R5.ATTR_NAMESPACE, req.getNamespace());

			appendAttributeAndDirectiveTags(reqTag, req.getAttributes(), req.getDirectives());

			resourceTag.addContent(reqTag);
		}

		resourceTag.print(indent, pw);
	}

	private static void appendAttributeAndDirectiveTags(Tag parentTag, Map<String, Object> attribs, Map<String, String> directives) {
		for (Entry<String, Object> attribEntry : attribs.entrySet()) {
			Tag attribTag = new Tag(Schema.R5.ELEM_ATTRIBUTE);
			attribTag.addAttribute(Schema.R5.ATTR_ATTRIBUTE_NAME, attribEntry.getKey());

			TypedAttribute typedAttrib = TypedAttribute.create(attribEntry.getKey(), attribEntry.getValue());
			parentTag.addContent(typedAttrib.toXML());
		}

		for (Entry<String, String> directiveEntry : directives.entrySet()) {
			Tag directiveTag = new Tag(Schema.R5.ELEM_DIRECTIVE);
			directiveTag.addAttribute(Schema.R5.ATTR_ATTRIBUTE_NAME, directiveEntry.getKey());
			directiveTag.addAttribute(Schema.R5.ATTR_ATTRIBUTE_VALUE, directiveEntry.getValue());
			parentTag.addContent(directiveTag);
		}
	}
}
