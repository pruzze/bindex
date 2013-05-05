package pl.caltha.labs.ldapfilters;

import org.osgi.framework.Version;

public class VersionFilter extends SimpleFilter<Version> {
	VersionFilter(String attribute, Operator operator, Version value) {
		super(attribute, operator, value);
	}

	VersionFilter(String attribute, Operator operator, String value) {
		super(attribute, operator, new Version(value));
	}

	@Override
	public AttributeType getAttributeType() {
		return AttributeType.VERSION;
	}

	public <V> V accept(FilterVisitor<V> visitor, V data) {
		return visitor.visit(this, data);
	}
}
