package pl.caltha.labs.ldapfilters;


public class StringFilter extends SimpleFilter<String> {
	StringFilter(String attribute, Operator operator, String value) {
		super(attribute, operator, value);
	}
	
	@Override
	public AttributeType getAttributeType() {		
		return AttributeType.STRING;
	}
	
	protected void appendType(StringBuilder buff) {
		buff.append("String");
	}
	
	protected void appendValue(StringBuilder buff) {
		buff.append(getValue().replace("//", "////").replace("(", "\\(").replace(")", "\\)"));
	}
}
