package pl.caltha.labs.ldapfilters;

public interface Filter {
	<T> T accept(FilterVisitor<T> visitor, T data);
}
