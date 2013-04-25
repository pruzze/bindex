package pl.caltha.labs.ldapfilters;

public class FilterVisitor<T> {

	public T visit(And f, T data) {
		return data;
	}

	public T visit(Or f, T data) {
		return data;
	}

	public T visit(Not f, T data) {
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
