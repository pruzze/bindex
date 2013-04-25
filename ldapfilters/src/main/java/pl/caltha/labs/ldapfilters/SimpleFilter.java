package pl.caltha.labs.ldapfilters;

public class SimpleFilter implements Filter {
	final private String attribute;

	final private Operator operator;

	final private String value;

	SimpleFilter(String attribute, Operator operator, String value) {
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

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append('(');
		buff.append(attribute);
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
			buff.append(value.replace("//", "////").replace("(", "\\(").replace(")", "\\)"));
		}
		buff.append(')');
		return buff.toString();
	}
}
