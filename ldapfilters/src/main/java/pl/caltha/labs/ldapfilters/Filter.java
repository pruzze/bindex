package pl.caltha.labs.ldapfilters;

public interface Filter {
	/**
	 * Returns the parent term in the expression, or {@code null} if this is the top level term. 
	 */
	Filter getParent();
	
	<T> T accept(FilterVisitor<T> visitor, T data);
}
