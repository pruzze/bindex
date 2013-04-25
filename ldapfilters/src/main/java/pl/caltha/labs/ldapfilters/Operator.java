package pl.caltha.labs.ldapfilters;

public enum Operator {
	EQUAL, APPROX, GREATER_EQ, LESS_EQ, PRESENT, SUBSTRING,
	/** Only applicable to {@link AttributeType#REQUIREMENT} */
	MATCHES
}
