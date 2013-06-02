package pl.caltha.labs.ldapfilters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Version;

public class FilterFactory {
	public static Filter and(Filter... terms) {
		AndFilter filter = new AndFilter(terms);
		setParent(filter, terms);
		return filter;
	}

	public static Filter or(Filter... terms) {
		OrFilter filter = new OrFilter(terms);
		setParent(filter, terms);
		return filter;
	}

	public static Filter not(Filter term) {
		NotFilter filter = new NotFilter(term);
		setParent(filter, term);
		return filter;
	}

	public static Filter filter(String attribute, Operator operator,
			String value) {
		return new StringFilter(attribute, operator, value);
	}

	public static Filter filter(String attribute, Operator operator,
			Double value) {
		return new DoubleFilter(attribute, operator, value);
	}

	public static Filter filter(String attribute, Operator operator, Long value) {
		return new LongFilter(attribute, operator, value);
	}

	public static Filter filter(String attribute, Operator operator,
			Version value) {
		return new VersionFilter(attribute, operator, value);
	}

	public static Filter filter(String attribute, Operator operator,
			AttributeType elementType, List<?> values) {
		return new ListFilter(attribute, operator, elementType, values);
	}

	public static Filter filter(String attribute, Operator operator,
			AttributeType elementType, Object... values) {
		return new ListFilter(attribute, operator, elementType,
				Arrays.asList(values));
	}

	public static Filter filter(String attribute, Operator operator) {
		if (operator != Operator.PRESENT)
			throw new IllegalArgumentException("value required for opreator "
					+ operator);
		return new VoidFilter(attribute, operator);
	}
	
	public static Filter filter(String attribute, Filter filter) {
		NestedFilter nestedFilter = new NestedFilter(attribute, filter);
		setParent(nestedFilter, filter);
		return nestedFilter;
	}

	public static Requirement requirement(String namespace, Filter filter) {
		Requirement requirement = new Requirement(namespace, filter,
				Collections.<String, String> emptyMap());
		return requirement;
	}

	public static Requirement requirement(String namespace, Filter filter,
			Map<String, String> properties) {
		Requirement requirement = new Requirement(namespace, filter, properties);
		setParent(requirement, filter);
		return requirement;
	}
	
	public static Requirements requirements(Requirement ...requirements) {
		Requirements reqs = new Requirements(Arrays.asList(requirements));
		setParent(reqs, requirements);
		return reqs;
	}
	
	static void setParent(Filter parent, Filter ... children) {
		for(Filter child : children) {
			if(child instanceof SimpleFilter) {
				((SimpleFilter<?>)child).setParent(parent);
			}
			if(child instanceof CompoundFilter) {
				((CompoundFilter)child).setParent(parent);
			}
			if(child instanceof Requirement) {
				((Requirement)child).setParent(parent);
			}
		}
	}
}
