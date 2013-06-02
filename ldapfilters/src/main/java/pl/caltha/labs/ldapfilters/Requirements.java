package pl.caltha.labs.ldapfilters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Requirements implements Filter {
	
	private final List<Requirement> requirements = new ArrayList<Requirement>();
	
	public Requirements() {		
	}
	
	public Requirements(List<Requirement> requirements) {
		this.requirements.addAll(requirements);
	}
	
	public Filter getParent() {
		return null;
	}
	
	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void addRequirement(Requirement requirement) {
		requirements.add(requirement);
	}

	public <T> T accept(FilterVisitor<T> visitor, T data) {
		for(Requirement requirement : requirements) {
			data = requirement.accept(visitor, data);
		}
		return visitor.visit(this, data);
	}
	
	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder();
		Iterator<Requirement> i = requirements.iterator();
		while(i.hasNext()) {
			buff.append(i.next().toString());
			if(i.hasNext())
				buff.append(',');
		}
		return buff.toString();
	}
}
