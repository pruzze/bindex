package pl.caltha.labs.ldapfilters;

public class LongFilter extends SimpleFilter<Long> {
	LongFilter(String attribute, Operator operator, Long value) {
		super(attribute, operator, value);
	}

	LongFilter(String attribute, Operator operator, String value) {
		super(attribute, operator, Long.parseLong(value.trim()));
	}

	@Override
	public AttributeType getAttributeType() {
		return AttributeType.LONG;
	}

	public <V> V accept(FilterVisitor<V> visitor, V data) {
		return visitor.visit(this, data);
	}

	protected void appendType(StringBuilder buff) {
		buff.append(":Long");
	}
}
