package br.ufpa.linc.xflow.data.entities;

import java.util.Set;

import javax.persistence.Column;
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

@Entity(name="dependency")
@Inheritance(strategy=InheritanceType.JOINED)
public class Dependency {

	final public static int FILE_FILE_DEPENDENCY = 1;
	final public static int AUTHOR_FILE_DEPENDENCY = 2;
	final public static int AUTHOR_AUTHOR_DEPENDENCY = 3;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "DEPENDENCY_ID")
	private long id;

	@ManyToMany
    @JoinTable(
            name="dependency_dependency_object",
            joinColumns = @JoinColumn( name="DEPENDENCY_ID"),
            inverseJoinColumns = @JoinColumn( name="DEPENDENCY_OBJECT_ID")
    )
	private Set<DependencyObject> dependencies;
	
	@OneToOne
	@JoinColumn(name = "ENTRY_ID", nullable = false)
	private Entry associatedEntry;
	
	@OneToOne
	@JoinColumn(name = "ANALYSIS_ID", nullable = false)
	private Analysis associatedAnalysis;
	
	@Column(name = "DIRECTED_DEPENDENCY")
	private boolean directedDependency;
	
	@Column(name = "DEPENDENCY_TYPE")
	private int type;

	public long getId() {
		return id;
	}

	public Set<DependencyObject> getDependencies() {
		return dependencies;
	}

	public void setDependencies(final Set<DependencyObject> dependencies) {
		this.dependencies = dependencies;
	}

	public Entry getAssociatedEntry() {
		return associatedEntry;
	}

	public void setAssociatedEntry(final Entry associatedEntry) {
		this.associatedEntry = associatedEntry;
	}

	public Analysis getAssociatedAnalysis() {
		return associatedAnalysis;
	}

	public void setAssociatedAnalysis(final Analysis associatedAnalysis) {
		this.associatedAnalysis = associatedAnalysis;
	}
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isDirectedDependency() {
		return directedDependency;
	}

	public void setDirectedDependency(final boolean directedDependency) {
		this.directedDependency = directedDependency;
	}
}
