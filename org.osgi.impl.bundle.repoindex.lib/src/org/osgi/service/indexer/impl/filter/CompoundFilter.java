package org.osgi.service.indexer.impl.filter;

import java.util.ArrayList;
import java.util.List;

public abstract class CompoundFilter {
	private List<Filter> terms = new ArrayList<Filter>();

	CompoundFilter() {		
	}
	
	void addTerm(Filter term) {
		terms.add(term);
	}
	
	public List<Filter> terms() {
		return terms;
	}

	public <T> T visit(FilterVisitor<T> visitor, T data) {
		data = visitor.visit(this, data);
		for (Filter term : terms()) {
			data = term.visit(visitor, data);
		}
		return data;
	}
}
