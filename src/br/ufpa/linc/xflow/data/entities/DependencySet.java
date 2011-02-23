package br.ufpa.linc.xflow.data.entities;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

@Entity(name = "dependency_set")
public class DependencySet<Dependable extends DependencyObject, Dependent extends DependencyObject> {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DEPENDENCYSET_ID")
	public long id;
	
	@ManyToOne(cascade = CascadeType.ALL, targetEntity = DependencyObject.class)
	@JoinColumn(name = "DEPENDED_OBJECT_ID")
	private Dependable dependedObject;

	@ManyToOne(cascade = CascadeType.ALL, targetEntity = Dependency.class)
	@JoinColumn(name = "DEPENDENCY_ID")
	private Dependency<Dependable, Dependent> associatedDependency;
	
	
//	@ElementCollection(targetClass = Long.class)
//	@JoinTable(name="DEPENDENT_OBJECT_DEPENDENCIES", 
//			joinColumns = @JoinColumn(name="DEPENDENT_OBJECT"),
//			inverseJoinColumns = @JoinColumn(name="DEPENDENCIES")
//	)
//	@javax.persistence.MapKeyClass(value = DependencyObject.class)
//	@javax.persistence.MapKey(name = "id")
	
//	@OneToMany(targetEntity = DependencyObject.class)
//	@JoinTable(name="DEPENDENT_OBJECT_DEPENDENCIES", 
//			joinColumns = @JoinColumn(name="DEPENDENT_OBJECT"),
//			inverseJoinColumns = @JoinColumn(name="DEPENDENCIES")
//	)
//	@MapKeyColumn(name = "DEPENDENCY_SET")
//	@javax.persistence.MapKeyClass(value = Long.class)
	@javax.persistence.ElementCollection(targetClass = Integer.class)
	@JoinTable(name = "DEPENDENT_OBJECT_DEPENDENCIES", 
	    joinColumns = @JoinColumn(name = "DEPENDENCY_SET_ID"))
	@org.hibernate.annotations.MapKey(targetElement = DependencyObject.class, 
	    columns = @Column(name = "DEPENDENCY_OBJECT"))
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
