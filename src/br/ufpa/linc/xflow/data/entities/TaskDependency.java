package br.ufpa.linc.xflow.data.entities;

import javax.persistence.Entity;

@Entity(name="task_dependency")
public class TaskDependency extends Dependency<FileDependencyObject, FileDependencyObject>  {
	
	public TaskDependency(){
		// Empty constructor.
		this.setDirectedDependency(false);
		this.setType(TASK_DEPENDENCY);
	}
	
}
