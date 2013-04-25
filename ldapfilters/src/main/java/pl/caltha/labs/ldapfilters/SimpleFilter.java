package pl.caltha.labs.ldapfilters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class SimpleFilter<T> implements Filter {
	
	private static Pattern ATTR = Pattern.compile("([^:]+):(.*)");

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

	static SimpleFilter<?> newFilter(String attr, Operator operator,
			String value) {
		String attrName;
		String attrType;
		Matcher m = ATTR.matcher(attr);
		if (m.matches()) {
			attrName = m.group(1);
			attrType = m.group(2);
		} else {
			attrName = attr;
			attrType = "String";
		}
		if (attrType.equals("String")) {
			return new StringFilter(attrName, operator, value);
		} else if (attrType.equals("Double")) {
			return new DoubleFilter(attrName, operator, value);
		} else if (attrType.equals("Long")) {
			return new LongFilter(attrName, operator, value);
		} else if (attrType.equals("Version")) {
			return new VersionFilter(attrName, operator, value);
		} else {
			m = LIST.matcher(attrType);
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
				throw new IllegalArgumentException("invalid attribute type "
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
		if(operator != Operator.PRESENT) {
			appendValue(buff);
		}
		buff.append(')');
		return buff.toString();
	}
}
