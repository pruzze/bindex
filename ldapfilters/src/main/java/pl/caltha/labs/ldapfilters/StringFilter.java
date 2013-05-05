package pl.caltha.labs.ldapfilters;

public class StringFilter extends SimpleFilter<String> {
	StringFilter(String attribute, Operator operator, String value) {
		super(attribute, operator, value == null ? null : value
				.replace("\\(", "(").replace("\\)", ")").replace("\\\\", "\\"));
	}

	@Override
	public AttributeType getAttributeType() {
		return AttributeType.STRING;
	}

	public <V> V accept(FilterVisitor<V> visitor, V data) {
		return visitor.visit(this, data);
	}

	protected void appendValue(StringBuilder buff) {
		buff.append(getValue().replace("\\", "\\\\").replace("(", "\\(")
				.replace(")", "\\)"));
	}
}
