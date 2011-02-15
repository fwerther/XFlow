package br.ufpa.linc.xflow.data.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

@Entity(name="dependency_object")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="OBJECT_TYPE", discriminatorType=DiscriminatorType.INTEGER)
public abstract class DependencyObject {

	public static final int FILE_DEPENDENCY = 1;
	public static final int AUTHOR_DEPENDENCY = 2;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DEPENDENCY_OBJECT_ID")
	private long id;
	
	@Column(name = "DEPENDENCY_STAMP", nullable = false)
	private int assignedStamp;
	
	@OneToOne
	@JoinColumn(name = "ANALYSIS_ID", nullable = false)
	private Analysis analysis;
	
	@Column(name = "OBJECT_TYPE", nullable = false)
	final private int objectType;
	
	@ManyToMany(cascade=CascadeType.ALL)
	   @JoinTable(
	           name="dependency_dependency_object",
	           joinColumns = @JoinColumn( name="DEPENDENCY_OBJECT_ID"),
	           inverseJoinColumns = @JoinColumn( name="DEPENDENCY_ID")
	   )
	private Set<Dependency> dependencies;

	@ManyToMany(cascade=CascadeType.ALL)
	   @JoinTable(
	           name="dependency_object_dependent_objects",
	           joinColumns = @JoinColumn( name="DEPENDENCY_ID"),
	           inverseJoinColumns = @JoinColumn( name="OBJECT_ID")
	   )
	private Set<DependencyObject> dependentObjects;
	
	@Column(name = "DEPENDENCE_DEGREE", nullable = true)
	private int dependenceDegree;
	
	
	public DependencyObject(final int objectType) {
		this.objectType = objectType;
	}
	
	
	public abstract String getDependencyObjectName();
	

	public long getId() {
		return id;
	}

	public int getAssignedStamp() {
		return assignedStamp;
	}

	public void setAssignedStamp(final int assignedStamp) {
		this.assignedStamp = assignedStamp;
	}

	public Analysis getAnalysis() {
		return analysis;
	}

	public void setAnalysis(final Analysis analysis) {
		this.analysis = analysis;
	}

	public int getObjectType() {
		return objectType;
	}
	
	public int getDependenceDegree() {
		return dependenceDegree;
	}

	public void setDependenceDegree(final int dependenceDegree) {
		this.dependenceDegree = dependenceDegree;
	}

	public Set<Dependency> getDependencies() {
		return dependencies;
	}

	public void setDependencies(final Set<Dependency> dependencies) {
		this.dependencies = dependencies;
	}

	public Set<DependencyObject> getDependentObjects() {
		return dependentObjects;
	}

	public void setDependentObjects(final Set<DependencyObject> dependentObjects) {
		this.dependentObjects = dependentObjects;
	}
	
}
