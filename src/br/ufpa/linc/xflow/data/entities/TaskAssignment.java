package br.ufpa.linc.xflow.data.entities;

import javax.persistence.Entity;

@Entity(name="task_assignment")
public class TaskAssignment extends Dependency<AuthorDependencyObject, FileDependencyObject>  {

	public TaskAssignment() {
		// Empty constructor.
		this.setDirectedDependency(true);
		this.setType(TASK_ASSIGNMENT);
	}
	
}
