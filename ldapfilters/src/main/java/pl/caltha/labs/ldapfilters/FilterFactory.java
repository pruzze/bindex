package pl.caltha.labs.ldapfilters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;

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
		return new StringFilter(attribute, operator, value);
	}
	
	public static Filter filter(String attribute, Operator operator, Double value) {
		return new DoubleFilter(attribute, operator, value);
	}
	
	public static Filter filter(String attribute, Operator operator, Long value) {
		return new LongFilter(attribute, operator, value);
	}
	
	public static Filter filter(String attribute, Operator operator, Version value) {
		return new VersionFilter(attribute, operator, value);
	}
	
	public static Filter filter(String attribute, Operator operator, AttributeType elementType, List<?> values) {
		return new ListFilter(attribute, operator, elementType, values);
	}
	
	public static Filter filter(String attribute, Operator operator, AttributeType elementType, Object ... values) {
		return new ListFilter(attribute, operator, elementType, Arrays.asList(values));
	}
	
	public static Filter filter(String attribute, Operator operator) {
		if(operator != Operator.PRESENT) 
			throw new IllegalArgumentException("value required for opreator " + operator);
		return SimpleFilter.newFilter(attribute, operator, null);
	}
	
	public static Filter requirement(String namespace, Filter filter) {
		return new RequirementFilter(namespace, filter, Collections.<String, String>emptyMap());
	}

	public static Filter requirement(String namespace, Filter filter, Map<String, String> properties) {
		return new RequirementFilter(namespace, filter, properties);
	}
}
