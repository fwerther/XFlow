package br.ufpa.linc.xflow.data.entities;

import javax.persistence.Entity;

@Entity(name="task_dependency")
public class TaskDependency extends Dependency<FileDependencyObject, FileDependencyObject>  {
	
	public TaskDependency(){
		super();
		this.setType(TASK_DEPENDENCY);
	}
	
	public TaskDependency(boolean isDirected){
		super(isDirected);
	}
	
}
