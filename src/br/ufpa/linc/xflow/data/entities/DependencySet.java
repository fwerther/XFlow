package br.ufpa.linc.xflow.data.entities;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyJoinColumn;

@Entity(name = "dependency_set")
public class DependencySet<Dependable extends DependencyObject, Dependent extends DependencyObject> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DEPENDENCYSET_ID")
	public long id;
	
	@ManyToOne(cascade = {CascadeType.MERGE,CascadeType.REMOVE}, targetEntity = DependencyObject.class)
	@JoinColumn(name = "DEPENDED_OBJECT_ID")
	private Dependable dependedObject;

	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Dependency.class)
	@JoinColumn(name = "DEPENDENCY_ID")
	private Dependency<Dependable, Dependent> associatedDependency;
	
	//See http://download.oracle.com/javaee/6/api/javax/persistence/MapKeyJoinColumn.html
	@ElementCollection(targetClass = Integer.class)
	@CollectionTable(name = "DEPENDENT_OBJECT_DEPENDENCIES", 
	    joinColumns = @JoinColumn(name = "DEPENDENCY_SET_ID"))
	@MapKeyJoinColumn(name="dependency_object", 
			referencedColumnName = "DEPENDENCY_OBJECT_ID")
	@Column(name = "DEPENDENCY_DEGREE")
	private Map<DependencyObject, Integer> dependenciesMap;

	public Dependable getDependedObject() {
		return dependedObject;
	}

	public void setDependedObject(Dependable dependedObject) {
		this.dependedObject = dependedObject;
	}

	public void setAssociatedDependency(Dependency<Dependable, Dependent> associatedDependency) {
		this.associatedDependency = associatedDependency;
	}

	public Dependency<Dependable, Dependent> getAssociatedDependency() {
		return associatedDependency;
	}

	public Map<? extends DependencyObject, Integer> getDependenciesMap() {
		return dependenciesMap;
	}

	public void setDependenciesMap(Map<Dependent, Integer> dependenciesMap) {
		this.dependenciesMap = (Map<DependencyObject, Integer>) dependenciesMap;
	}

	public long getId() {
		return id;
	} 
	
}
