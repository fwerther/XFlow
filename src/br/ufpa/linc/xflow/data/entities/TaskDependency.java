package br.ufpa.linc.xflow.data.entities;

import java.util.Set;

import javax.persistence.Entity;

@Entity(name="task_dependency")
public class TaskDependency extends Dependency {
	
	public TaskDependency(){
		//Empty constructor.
		this.setType(Dependency.FILE_FILE_DEPENDENCY);
		this.setDirectedDependency(false);
	}
	
	public TaskDependency(Analysis analysis, Entry entry, Set<DependencyObject> fileDependencies){
		this.setAssociatedAnalysis(analysis);
		this.setAssociatedEntry(entry);
		this.setDependencies(fileDependencies);
	}
	
}
