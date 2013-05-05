package pl.caltha.labs.ldapfilters;

public enum AttributeType {
	STRING, LONG, DOUBLE, VERSION, LIST, REQUIREMENT;

	private static String[] repr = { "String", "Long", "Double", "Version",
			"List" };

	public static AttributeType parse(String str, int pos) {
		for (int i = 0; i < repr.length; i++) {
			if (repr[i].equals(str))
				return values()[i];
		}
		throw new ParseException("Invalid attribute type " + str
				+ " at position " + pos);
	}
}
