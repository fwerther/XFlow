package br.ufpa.linc.xflow.data.entities;

import javax.persistence.Entity;

@Entity(name="coordination_requirements")
public class CoordinationRequirements extends Dependency<AuthorDependencyObject, AuthorDependencyObject> {
	
	public CoordinationRequirements() {
		//Empty constructor.
		this.setDirectedDependency(true);
		this.setType(COORD_REQUIREMENTS);
	}
	
}
