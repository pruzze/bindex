package pl.caltha.labs.ldapfilters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class SimpleFilter<T> implements Filter {

	private static Pattern LIST = Pattern.compile("List<([^>]+)>");

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

	static SimpleFilter<?> newFilter(String attrName, String attrType, Operator operator,
			String value) {
		if (attrType == null || attrType.equals("String")) {
			if (operator == Operator.EQUAL
					&& (value.startsWith("*") || value.endsWith("*")))
				return new StringFilter(attrName, Operator.SUBSTRING, value);
			else
				return new StringFilter(attrName, operator, value);
		} else if (attrType.equals("Double")) {
			return new DoubleFilter(attrName, operator, value);
		} else if (attrType.equals("Long")) {
			return new LongFilter(attrName, operator, value);
		} else if (attrType.equals("Version")) {
			return new VersionFilter(attrName, operator, value);
		} else {
			Matcher m = LIST.matcher(attrType);
			if (m.matches()) {
				String el = m.group(1);
				AttributeType elType;
				if (el.equals("String")) {
					elType = AttributeType.STRING;
				} else if (el.equals("Double")) {
					elType = AttributeType.DOUBLE;
				} else if (el.equals("Long")) {
					elType = AttributeType.LONG;
				} else if (el.equals("Version")) {
					elType = AttributeType.VERSION;
				} else {
					throw new IllegalArgumentException(
							"invalid list element type " + el);
				}
				return new ListFilter(attrName, operator, elType, value);
			} else {
				throw new IllegalArgumentException("Invalid attribute type "
						+ attrType);
			}
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
