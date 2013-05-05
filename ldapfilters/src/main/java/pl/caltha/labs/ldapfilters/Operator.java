package pl.caltha.labs.ldapfilters;

public enum Operator {
	EQUAL, APPROX, GREATER_EQ, LESS_EQ, PRESENT, SUBSTRING,
	/** Only applicable to {@link AttributeType#REQUIREMENT} */
	MATCHES;

	private static String[] repr = { "=", "~=", ">=", "<=", "=*", "=" };

	public static Operator parse(String str, int pos) {
		for (int i = 0; i < repr.length; i++) {
			if(repr[i].equals(str))
				return Operator.values()[i];
		}
		throw new ParseException("Invalid operator at position " + pos);
	}
	
	public static void appendTo(Operator value, StringBuilder buff) {
		for(int i = 0; i < repr.length; i++) {
			if(values()[i] == value) {
				buff.append(repr[i]);
				return;
			}
		}
		throw new RuntimeException("Unsupported operator " + value);
	}
}
