package org.osgi.service.indexer.impl.filter;

public interface Filter {
	<T> T visit(FilterVisitor<T> visitor, T data);
}
