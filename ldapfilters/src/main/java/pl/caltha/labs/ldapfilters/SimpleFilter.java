package pl.caltha.labs.ldapfilters;

public abstract class SimpleFilter<T> implements Filter {

	final private String attribute;

	final private Operator operator;

	final private T value;

	SimpleFilter(String attribute, Operator operator, T value) {
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}

	public String getAttribute() {
		return attribute;
	}

	public Operator getOperator() {
		return operator;
	}

	public abstract AttributeType getAttributeType();

	public T getValue() {
		return value;
	}

	static SimpleFilter<?> newFilter(String attrName, AttributeType attrType,
			AttributeType elemType, Operator operator, String value) {
		switch (attrType) {
		case STRING:
			if (operator == Operator.EQUAL
					&& (value.startsWith("*") || value.endsWith("*")))
				return new StringFilter(attrName, Operator.SUBSTRING, value);
			else
				return new StringFilter(attrName, operator, value);
		case LONG:
			return new LongFilter(attrName, operator, value);
		case DOUBLE:
			return new DoubleFilter(attrName, operator, value);
		case VERSION:
			return new VersionFilter(attrName, operator, value);
		case LIST:
			return new ListFilter(attrName, operator, elemType, value);
		default:
			throw new ParseException("Invalid attribute type " + attrType);
		}
	}

	protected void appendType(StringBuilder buff) {
	}

	protected void appendValue(StringBuilder buff) {
		buff.append(getValue().toString());
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append('(');
		buff.append(attribute);
		appendType(buff);
		switch (operator) {
		case EQUAL:
		case SUBSTRING:
			buff.append('=');
			break;
		case APPROX:
			buff.append("~=");
			break;
		case PRESENT:
			buff.append("=*");
			break;
		case LESS_EQ:
			buff.append("<=");
			break;
		case GREATER_EQ:
			buff.append(">=");
			break;
		}
		if (operator != Operator.PRESENT) {
			appendValue(buff);
		}
		buff.append(')');
		return buff.toString();
	}
}
