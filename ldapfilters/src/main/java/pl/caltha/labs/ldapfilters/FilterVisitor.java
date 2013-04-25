package pl.caltha.labs.ldapfilters;

public class FilterVisitor<T> {

	public T visit(AndFilter f, T data) {
		return data;
	}

	public T visit(OrFilter f, T data) {
		return data;
	}

	public T visit(NotFilter f, T data) {
		return data;
	}

	public T visit(StringFilter f, T data) {
		return data;
	}

	public T visit(DoubleFilter f, T data) {
		return data;
	}

	public T visit(LongFilter f, T data) {
		return data;
	}

	public T visit(VersionFilter f, T data) {
		return data;
	}

	public T visit(ListFilter f, T data) {
		return data;
	}

	public T visit(RequirementFilter f, T data) {
		return data;
	}
}
