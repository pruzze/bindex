package pl.caltha.labs.ldapfilters;

import java.util.ArrayList;
import java.util.List;

public abstract class CompoundFilter implements Filter {
	private List<Filter> terms = new ArrayList<Filter>();

	CompoundFilter() {
	}

	CompoundFilter(Filter... initTerms) {
		for (Filter term : initTerms) {
			terms.add(term);
		}
	}

	void addTerm(Filter term) {
		terms.add(term);
	}

	public List<Filter> getTerms() {
		return terms;
	}

	protected String toString(char op) {
		StringBuilder buff = new StringBuilder();
		buff.append("(").append(op);
		for (Filter term : terms) {
			buff.append(term.toString());
		}
		buff.append(")");
		return buff.toString();
	}
}