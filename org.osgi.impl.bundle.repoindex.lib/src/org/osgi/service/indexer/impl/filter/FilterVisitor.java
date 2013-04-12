package org.osgi.service.indexer.impl.filter;

public abstract class FilterVisitor<T> {
	
	public final T visit(CompoundFilter f, T data) {
		throw new Error("unexpected " + f.getClass() + " object");
	}
	
	public final T visit(SimpleFilter f, T data) {
		throw new Error("unexpected " + f.getClass() + " object");
	}
	
	public abstract T visit(And f, T data);
	
	public abstract T visit(Or f, T data);
	
	public abstract T visit(Not f, T data);
	
	public abstract T visit(TextFilter f, T data);
	
	public abstract T visit(VersionFilter f, T data);
}
