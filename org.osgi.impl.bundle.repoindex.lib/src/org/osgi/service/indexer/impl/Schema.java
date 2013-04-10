package org.osgi.service.indexer.impl;

public final class Schema {

	public static final Object XML_PROCESSING_INSTRUCTION = "<?xml version='1.0' encoding='utf-8'?>";
	public static final String XSL_PROCESSING_INSTRUCTION = "<?xml-stylesheet type='text/xsl' href='%s'?>";
	public static final String ATTR_XML_NAMESPACE = "xmlns";

	public final class R5 {
		public static final String NAMESPACE = "http://www.osgi.org/xmlns/repository/v1.0.0";

		public static final String ELEM_REPOSITORY = "repository";
		public static final String ELEM_RESOURCE = "resource";
		public static final String ELEM_CAPABILITY = "capability";
		public static final String ELEM_REQUIREMENT = "requirement";
		public static final String ELEM_ATTRIBUTE = "attribute";
		public static final String ELEM_DIRECTIVE = "directive";

		public static final String ATTR_REPOSITORY_NAME = "name";		
		public static final String ATTR_REPOSITORY_INCREMENT = "increment";

		public static final String ATTR_NAMESPACE = "namespace";

		public static final String ATTR_ATTRIBUTE_NAME = "name";
		public static final String ATTR_ATTRIBUTE_TYPE = "type";
		public static final String ATTR_ATTRIBUTE_VALUE = "value";
	}
	
	public final class RFC112 {
		public static final String NAMESPACE = "http://www.osgi.org/xmlns/obr/v1.0.0";
		public static final String STYLESHEET_DEFAULT = "http://www.osgi.org/www/obr2html.xsl";

		public static final String ELEM_REPOSITORY = "repository";
		public static final String ELEM_RESOURCE = "resource";
		public static final String ELEM_CATEGORY = "category";
		public static final String ELEM_CAPABILITY = "capability";
		public static final String ELEM_REQUIRE = "require";
		public static final String ELEM_ATTRIBUTE = "p";
		
		public static final String ATTR_REPOSITORY_NAME = "name";
		public static final String ATTR_REPOSITORY_TIME = "time";
		
		public static final String ATTR_RESOURCE_ID = "id";
		public static final String ATTR_RESOURCE_SYMBOLIC_NAME = "symbolicname";
		public static final String ATTR_RESOURCE_PRESENTATION_NAME = "presentationname";
		public static final String ATTR_RESOURCE_VERSION = "version";
		public static final String ATTR_RESOURCE_URI = "uri";
		
		public static final String ATTR_CATEGORY_ID = "id";

		public static final String ATTR_CAPABILITY_NAME = "name";
		
		public static final String CAPREQ_BUNDLE = "bundle";
		public static final String ATTR_BUNDLE_SYMBOLIC_NAME = "symbolicname";
		public static final String ATTR_BUNDLE_PRESENTATION_NAME = "presentationname";
		public static final String ATTR_BUNDLE_VERSION = "version";
		public static final String ATTR_BUNDLE_MANIFEST_VERSION = "manifestversion";
		
		public static final String CAPREQ_PACKAGE = "package";
		public static final String ATTR_PACKAGE_VERSION = "version";

		public static final String CAP_FRAGMENT = "fragment";
		public static final String ATTR_FRAGMENT_HOST = "host";
		public static final String ATTR_FRAGMENT_HOST_VERSION = "version";
		
		public static final String REQUIRE_HOST = "host";
		
		public static final String REQUIRE_EE = "ee";
		
		public static final String CAPREQ_SERVICE = "service";
		public static final String ATTR_SERVICE_OBJECTCLASS = "service";
		
		public static final String ATTR_REQUIRE_OPTIONAL = "optional";
		public static final String ATTR_REQUIRE_MULTIPLE = "multiple";
		public static final String ATTR_REQUIRE_EXTEND = "extend";
		public static final String ATTR_REQUIRE_FILTER = "filter";
		
		public static final String ATTR_ATTRIBUTE_NAME = "n";
		public static final String ATTR_ATTRIBUTE_TYPE = "t";
		public static final String ATTR_ATTRIBUTE_VALUE = "v";
	}
}
