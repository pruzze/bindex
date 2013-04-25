package pl.caltha.labs.ldapfilters;

public class FilterFactory {
	public static Filter and(Filter... terms) {
		return new And(terms);
	}

	public static Filter or(Filter... terms) {
		return new Or(terms);
	}

	public static Filter not(Filter term) {
		return new Not(term);
	}
	
	public static Filter filter(String attribute, Operator operator, String value) {
		return new SimpleFilter(attribute, operator, value);
	}
	
	public static Filter filter(String attribute, Operator operator) {
		if(operator != Operator.PRESENT) 
			throw new IllegalArgumentException("value required for opreator " + operator);
		return new SimpleFilter(attribute, operator, null);
	}
}
