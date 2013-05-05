package pl.caltha.labs.ldapfilters;

public enum AttributeType {
	STRING, LONG, DOUBLE, VERSION, LIST, REQUIREMENT;

	private static String[] repr = { "String", "Long", "Double", "Version",
			"List" };

	public static AttributeType parse(String str, int pos) {
		for (int i = 0; i < repr.length; i++) {
			if (repr[i].equals(str)) {
				return values()[i];
			}
		}
		throw new ParseException("Invalid attribute type " + str
				+ " at position " + pos);
	}

	public String toString() {
		for (int i = 0; i < repr.length; i++) {
			if (values()[i] == this) {
				return repr[i];
			}
		}
		throw new RuntimeException("Unsupported attribute type " + this);
	}

	public static void appendTo(AttributeType attrType, AttributeType elemType,
			StringBuilder buff) {
		switch (attrType) {
		case STRING:
			break;
		case LONG:
		case DOUBLE:
		case VERSION:
			buff.append(':');
			buff.append(attrType.toString());
			break;
		case LIST:
			buff.append(':');
			buff.append(LIST.toString());
			buff.append('<');
			buff.append(elemType.toString());
			buff.append('>');
			break;
		default:
			throw new RuntimeException("Unsupported attribute type " + attrType);
		}
	}
}
