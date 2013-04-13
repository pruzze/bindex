package org.osgi.service.indexer.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;
import org.osgi.service.indexer.IndexWriter;
import org.osgi.service.indexer.Namespaces;
import org.osgi.service.indexer.impl.types.RFC112TypedAttribute;
import org.osgi.service.indexer.impl.types.VersionRange;
import org.osgi.service.indexer.impl.util.Indent;
import org.osgi.service.indexer.impl.util.Tag;

public class RFC112FragmentWriter implements IndexWriter {
	private final PrintWriter pw;

	public RFC112FragmentWriter(Writer out) {
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
		Tag resourceTag = new Tag(Schema.RFC112.ELEM_RESOURCE);

		String symbolicName = null;
		String presentatioName = null;
		Version version = null;
		String url = null;
		for (Capability identity : resource.getCapabilities(Namespaces.NS_IDENTITY)) {
			symbolicName = (String) identity.getAttributes().get(Namespaces.NS_IDENTITY);
			presentatioName = (String) identity.getAttributes().get(Namespaces.ATTR_IDENTITY_NAME);
			version = (Version) identity.getAttributes().get(Namespaces.ATTR_VERSION);
		}
		for (Capability content : resource.getCapabilities(Namespaces.NS_CONTENT)) {
			url = (String) content.getAttributes().get(Namespaces.ATTR_CONTENT_URL);
		}
		resourceTag.addAttribute(Schema.RFC112.ATTR_RESOURCE_ID, symbolicName + (version != null ? ("/" + version.toString()) : ""));
		resourceTag.addAttribute(Schema.RFC112.ATTR_RESOURCE_SYMBOLIC_NAME, symbolicName);
		if (presentatioName != null)
			resourceTag.addAttribute(Schema.RFC112.ATTR_RESOURCE_PRESENTATION_NAME, presentatioName);
		resourceTag.addAttribute(Schema.RFC112.ATTR_RESOURCE_VERSION, version);
		resourceTag.addAttribute(Schema.RFC112.ATTR_RESOURCE_URI, url);

		appendResourceAttrs(resourceTag, resource);
		appendCapabilities(resourceTag, resource);
		appendRequirements(resourceTag, resource);

		resourceTag.print(indent, pw);
	}

	private static void appendResourceAttrs(Tag parentTag, Resource resource) throws IOException {
		for (Capability identity : resource.getCapabilities(Namespaces.NS_IDENTITY)) {
			maybeAppendCategoryAttr(parentTag, identity.getAttributes());
			maybeAppendResourceAttr(parentTag, identity.getAttributes(), Namespaces.ATTR_IDENTITY_COPYRIGHT);
			maybeAppendResourceAttr(parentTag, identity.getAttributes(), Namespaces.ATTR_IDENTITY_DESCRIPTION);
			maybeAppendResourceAttr(parentTag, identity.getAttributes(), Namespaces.ATTR_IDENTITY_DOCUMENTATION);
			maybeAppendResourceAttr(parentTag, identity.getAttributes(), Namespaces.ATTR_IDENTITY_LICENSE);
		}
		for (Capability content : resource.getCapabilities(Namespaces.NS_CONTENT)) {
			maybeAppendResourceAttr(parentTag, content.getAttributes(), Namespaces.ATTR_CONTENT_SIZE);
		}
	}

	private static void maybeAppendCategoryAttr(Tag parentTag, Map<String, Object> attrs) {
		Object value = attrs.get(Namespaces.ATTR_IDENTITY_CATEGORY);
		if (value != null) {
			if (!(value instanceof Collection<?>)) {
				value = Collections.singleton(value);
			}
			@SuppressWarnings("unchecked")
			Collection<String> ids = (Collection<String>) value;
			for (String id : ids) {
				Tag tag = new Tag(Schema.RFC112.ELEM_CATEGORY);
				tag.addAttribute(Schema.RFC112.ATTR_CATEGORY_ID, id);
				parentTag.addContent(tag);
			}
		}
	}

	private static void maybeAppendResourceAttr(Tag parentTag, Map<String, Object> attrs, String name) {
		Object value = attrs.get(name);
		if (value != null) {
			RFC112TypedAttribute attr = RFC112TypedAttribute.createResourceAttribute(name, value);
			parentTag.addContent(attr.toXML());
		}
	}

	private static void appendCapabilities(Tag parentTag, Resource resource) {
		for (Capability identity : resource.getCapabilities(Namespaces.NS_IDENTITY)) {
			if (resource.getCapabilities(Namespaces.NS_WIRING_BUNDLE).size() + resource.getRequirements(Namespaces.NS_WIRING_HOST).size() > 0) {
				Tag tag = new Tag(Schema.RFC112.ELEM_CAPABILITY);
				tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_BUNDLE);
				maybeAppendCapReqAttr(tag, identity.getAttributes(), Namespaces.NS_IDENTITY, Schema.RFC112.ATTR_BUNDLE_SYMBOLIC_NAME);
				maybeAppendCapReqAttr(tag, identity.getAttributes(), Namespaces.ATTR_IDENTITY_NAME, Schema.RFC112.ATTR_BUNDLE_PRESENTATION_NAME);
				maybeAppendCapReqAttr(tag, identity.getAttributes(), Namespaces.ATTR_VERSION, Schema.RFC112.ATTR_BUNDLE_VERSION);
				maybeAppendCapReqAttr(tag, identity.getAttributes(), Namespaces.ATTR_IDENTITY_MANIFESTVERSION, Schema.RFC112.ATTR_BUNDLE_MANIFEST_VERSION);
				for(Capability bundle : resource.getCapabilities(Namespaces.NS_WIRING_BUNDLE)) {
					maybeAppendCapReqAttr(tag, bundle.getDirectives(), Constants.FRAGMENT_ATTACHMENT_DIRECTIVE, Constants.FRAGMENT_ATTACHMENT_DIRECTIVE);
				}
				appendCapDirectives(tag, identity);
				parentTag.addContent(tag);
			}
		}
		for (Capability pkg : resource.getCapabilities(Namespaces.NS_WIRING_PACKAGE)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_CAPABILITY);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_PACKAGE);
			maybeAppendCapReqAttr(tag, pkg.getAttributes(), Namespaces.NS_WIRING_PACKAGE, Schema.RFC112.CAPREQ_PACKAGE);
			maybeAppendCapReqAttr(tag, pkg.getAttributes(), Namespaces.ATTR_VERSION, Schema.RFC112.ATTR_PACKAGE_VERSION);
			appendCapDirectives(tag, pkg);
			parentTag.addContent(tag);
		}
		for (Capability service : resource.getCapabilities(Namespaces.NS_SERVICE)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_CAPABILITY);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_SERVICE);
			maybeAppendCapReqAttr(tag, service.getAttributes(), Constants.OBJECTCLASS, Schema.RFC112.ATTR_SERVICE_OBJECTCLASS);
			parentTag.addContent(tag);
		}
		for (Requirement host : resource.getRequirements(Namespaces.NS_WIRING_HOST)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_CAPABILITY);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAP_FRAGMENT);
			String filter = host.getDirectives().get(Namespaces.DIRECTIVE_FILTER);
			VersionedItem hostSpec = VersionedItem.parse(filter, Namespaces.NS_WIRING_HOST);
			Tag h = new Tag(Schema.RFC112.ELEM_ATTRIBUTE);
			h.addAttribute(Schema.RFC112.ATTR_ATTRIBUTE_NAME, Schema.RFC112.ATTR_FRAGMENT_HOST);
			h.addAttribute(Schema.RFC112.ATTR_ATTRIBUTE_VALUE, hostSpec.getAttributeValue());
			Tag v = new Tag(Schema.RFC112.ELEM_ATTRIBUTE);
			v.addAttribute(Schema.RFC112.ATTR_ATTRIBUTE_NAME, Schema.RFC112.ATTR_FRAGMENT_HOST_VERSION);
			v.addAttribute(Schema.RFC112.ATTR_ATTRIBUTE_VALUE, hostSpec.getVersion().getLow().toString());
			tag.addContent(h);
			tag.addContent(v);
			parentTag.addContent(tag);
		}
	}

	private static void appendCapDirectives(Tag tag, Capability pkg) {
		for (Map.Entry<String, String> dir : pkg.getDirectives().entrySet()) {
			Tag dirTag = new Tag(Schema.RFC112.ELEM_ATTRIBUTE);
			dirTag.addAttribute(Schema.RFC112.ATTR_ATTRIBUTE_NAME, dir.getKey() + ":");
			dirTag.addAttribute(Schema.RFC112.ATTR_ATTRIBUTE_VALUE, dir.getValue());
			tag.addContent(dirTag);
		}
	}

	private static void appendRequirements(Tag parentTag, Resource resource) {
		for (Requirement bundle : resource.getRequirements(Namespaces.NS_WIRING_BUNDLE)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_REQUIRE);
			appendRequirementDirs(tag, bundle.getDirectives(), false);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_BUNDLE);
			String filter = bundle.getDirectives().get(Namespaces.DIRECTIVE_FILTER);
			filter = filter.replaceAll("osgi.wiring.bundle", "symbolicname").replaceAll("bundle-version", "version");
			tag.addAttribute(Schema.RFC112.ATTR_REQUIRE_FILTER, filter);
			parentTag.addContent(tag);
		}
		for (Requirement pkg : resource.getRequirements(Namespaces.NS_WIRING_PACKAGE)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_REQUIRE);
			appendRequirementDirs(tag, pkg.getDirectives(), false);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_PACKAGE);
			String filter = pkg.getDirectives().get(Namespaces.DIRECTIVE_FILTER);
			filter = filter.replaceAll("osgi.wiring.package", "package");
			tag.addAttribute(Schema.RFC112.ATTR_REQUIRE_FILTER, filter);
			parentTag.addContent(tag);
		}
		for (Requirement ee : resource.getRequirements(Namespaces.NS_EE)) {
			String filter = ee.getDirectives().get(Namespaces.DIRECTIVE_FILTER);
			List<BREE> brees = BREE.parse(filter);
			StringBuilder b = new StringBuilder();
			for (BREE bree : brees) {
				if (bree.getVersion() != null) {
					b.append("(").append("ee=").append(bree.getEe()).append("-").append(bree.getVersion()).append(")");
				} else {
					b.append("(").append("ee=").append(bree.getEe()).append(")");
				}
			}
			filter = brees.size() > 1 ? "(|" + b.toString() + ")" : b.toString();
			Tag tag = new Tag(Schema.RFC112.ELEM_REQUIRE);
			appendRequirementDirs(tag, ee.getDirectives(), false);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.REQUIRE_EE);
			tag.addAttribute(Schema.RFC112.ATTR_REQUIRE_FILTER, filter);
			parentTag.addContent(tag);
		}
		for (Requirement service : resource.getRequirements(Namespaces.NS_SERVICE)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_REQUIRE);
			appendRequirementDirs(tag, service.getDirectives(), false);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_SERVICE);
			String filter = service.getDirectives().get(Namespaces.DIRECTIVE_FILTER);
			tag.addAttribute(Schema.RFC112.ATTR_REQUIRE_FILTER, filter);
			parentTag.addContent(tag);
		}
		for (Requirement host : resource.getRequirements(Namespaces.NS_WIRING_HOST)) {
			Tag tag = new Tag(Schema.RFC112.ELEM_REQUIRE);
			appendRequirementDirs(tag, host.getDirectives(), true);
			tag.addAttribute(Schema.RFC112.ATTR_CAPABILITY_NAME, Schema.RFC112.CAPREQ_BUNDLE);
			String filter = host.getDirectives().get(Namespaces.DIRECTIVE_FILTER);
			filter = filter.replaceAll("osgi.wiring.host", "symbolicname").replaceAll("bundle-version", "version");
			tag.addAttribute(Schema.RFC112.ATTR_REQUIRE_FILTER, filter);
			parentTag.addContent(tag);
		}
	}

	private static void maybeAppendCapReqAttr(Tag parentTag, Map<String, ?> attrs, String attrName, String xmlName) {
		Object value = attrs.get(attrName);
		if (value != null) {
			RFC112TypedAttribute attr = RFC112TypedAttribute.createCapabilityAttribute(xmlName, value);
			parentTag.addContent(attr.toXML());
		}
	}

	private static void appendRequirementDirs(Tag tag, Map<String, String> dirs, boolean extend) {
		String resolution = dirs.get(Namespaces.DIRECTIVE_RESOLUTION);
		maybeAddBooleanAttibute(tag, Schema.RFC112.ATTR_REQUIRE_OPTIONAL, resolution, Namespaces.RESOLUTION_OPTIONAL);
		String cardinality = dirs.get(Namespaces.DIRECTIVE_CARDINALITY);
		maybeAddBooleanAttibute(tag, Schema.RFC112.ATTR_REQUIRE_MULTIPLE, cardinality, Namespaces.CARDINALITY_MULTIPLE);
		tag.addAttribute(Schema.RFC112.ATTR_REQUIRE_EXTEND, extend ? "true" : "false");
	}

	private static void maybeAddBooleanAttibute(Tag tag, String name, String value, String expValue) {
		if (value != null && value.equals(expValue))
			tag.addAttribute(name, "true");
		else
			tag.addAttribute(name, "false");
	}

	/**
	 * Naive filter parser
	 */
	private static class VersionedItem {
		final private String attributeValue;
		final private VersionRange versionRange;

		private static final Pattern FORM1 = Pattern.compile("\\(&\\(([a-z.]+)=([a-z.]+)\\)\\([a-z-]*version>=([^)]+)\\)\\)");
		private static final Pattern FORM2 = Pattern.compile("\\(&\\(([a-z.]+)=([a-z.]+)\\)\\([a-z-]*version>=([^)]+)\\)\\(version<=([^)]+)\\)\\)");
		private static final Pattern FORM3 = Pattern.compile("\\(&\\(([a-z.]+)=([a-z.]+)\\)\\([a-z-]*version>=([^)]+)\\)\\(!\\(version<=([^)]+)\\)\\)\\)");

		private VersionedItem(String attributeValue, VersionRange versionRange) {
			this.attributeValue = attributeValue;
			this.versionRange = versionRange;
		}

		public String getAttributeValue() {
			return attributeValue;
		}

		public VersionRange getVersion() {
			return versionRange;
		}

		public static VersionedItem parse(String filter, String attribute) {
			String matchedAttribute = null;
			String matchedValue = null;
			VersionRange range = null;
			Matcher m = FORM1.matcher(filter);
			if (m.matches()) {
				matchedAttribute = m.group(1);
				matchedValue = m.group(2);
				range = new VersionRange(m.group(3));
			}
			m = FORM2.matcher(filter);
			if (m.matches()) {
				matchedAttribute = m.group(1);
				matchedValue = m.group(2);
				range = new VersionRange("[" + m.group(3) + "," + m.group(4) + ")");
			}
			m = FORM3.matcher(filter);
			if (m.matches()) {
				matchedAttribute = m.group(1);
				matchedValue = m.group(2);
				range = new VersionRange("[" + m.group(3) + "," + m.group(4) + ")");
			}
			if (matchedAttribute != null && matchedAttribute.equals(attribute))
				return new VersionedItem(matchedValue, range);
			else
				return null;
		}
	}

	private static class BREE {

		private final String ee;

		private final String version;

		private BREE(String ee, String version) {
			this.ee = ee;
			this.version = version;
		}

		public String getEe() {
			return ee;
		}

		public String getVersion() {
			return version;
		}

		private static final Pattern EV = Pattern.compile("\\(&\\(osgi\\.ee=([A-Za-z/]+)\\)\\(version=([1-9.]+)\\)\\)");
		private static final Pattern EVS = Pattern.compile("\\(\\|(\\(&\\(osgi\\.ee=[A-Za-z/]+\\)\\(version=[1-9.]+\\)\\))*\\)");

		public static List<BREE> parse(String filter) {
			List<BREE> ees = new ArrayList<BREE>();
			Matcher m = EV.matcher(filter);
			if (m.matches())
				ees.add(parseSegment(filter));
			m = EVS.matcher(filter);
			if (m.matches()) {
				m = EV.matcher(filter);
				int last = 0;
				while (m.find(last)) {
					ees.add(parseSegment(m.group()));
					last = last + m.group().length();
				}
			}
			return ees;
		}

		private static BREE parseSegment(String segment) {
			Matcher m = EV.matcher(segment);
			if (m.matches())
				return new BREE(m.group(1), m.group(2));
			return null;
		}
	}
}
