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
	
	protected void appendType(StringBuilder buff) {
		buff.append("Version");
	}
}
