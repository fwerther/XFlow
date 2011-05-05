/* 
 * 
 * XFlow
 * _______
 * 
 *  
 *  (C) Copyright 2010, by Universidade Federal do Pará (UFPA), Francisco Santana, Jean Costa, Pedro Treccani and Cleidson de Souza.
 * 
 *  This file is part of XFlow.
 *
 *  XFlow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XFlow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XFlow.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  =============
 *  Analysis.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.entities;

import java.util.Date;

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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

@Entity(name = "analysis")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="ANALYSIS_TYPE", discriminatorType=DiscriminatorType.INTEGER)
public abstract class Analysis {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ANALYSIS_ID")
	protected long id;

	@OneToOne
	@JoinColumn(name = "ANALYSIS_FIRST_ENTRY", nullable = false)
	private Entry firstEntry;

	@OneToOne
	@JoinColumn(name = "ANALYSIS_LAST_ENTRY", nullable = false)
	private Entry lastEntry;

	@Temporal(TemporalType.DATE)
	@Column(name = "ANALYSIS_DATE", nullable = false)
	private Date date;

	@Column(name = "ANALYSIS_DETAILS", columnDefinition="LONGTEXT", nullable = true)
	private String details;

	@Column(name = "ANALYSIS_TYPE", nullable = false)
	private int type;

	@Column(name = "ANALYSIS_CONFIDENCE", nullable = false)
	private double confidenceValue;

	@Column(name = "ANALYSIS_SUPPORT", nullable = false)
	private int supportValue;
	
	@Column(name = "COORD_REQ_PERSISTED", nullable = false)
	private boolean coordinationRequirementPersisted;
	
	@OneToOne
	@JoinColumn(name = "ANALYSIS_PROJECT", nullable = false)
	private Project project;

	@Column(name = "TEMPORAL_CONSISTENCY_FORCED", nullable = false)
	private boolean temporalConsistencyForced = false;
	
	@Column(name = "ANALYSIS_FILE_LIMIT_PER_REVISION", nullable = false)
	private int maxFilesPerRevision;
	
	public final int getMaxFilesPerRevision() {
		return maxFilesPerRevision;
	}

	public final void setMaxFilesPerRevision(final int maxFilesPerRevision) {
		this.maxFilesPerRevision = maxFilesPerRevision;
	}

	public long getId() {
		return id;
	}

	public Entry getFirstEntry() {
		return firstEntry;
	}

	public void setFirstEntry(final Entry firstEntry) {
		this.firstEntry = firstEntry;
	}

	public Entry getLastEntry() {
		return lastEntry;
	}

	public void setLastEntry(final Entry lastEntry) {
		this.lastEntry = lastEntry;
	}

	public void setInterval(final Entry firstEntry, final Entry lastEntry){
		this.firstEntry = firstEntry;
		this.lastEntry = lastEntry;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(final String details) {
		this.details = details;
	}

	public int getType() {
		return type;
	}

	public void setType(final int type) {
		this.type = type;
	}

	public double getConfidenceValue() {
		return confidenceValue;
	}

	public void setConfidenceValue(final double confidenceValue) {
		this.confidenceValue = confidenceValue;
	}

	public int getSupportValue() {
		return supportValue;
	}

	public void setSupportValue(final int supportValue) {
		this.supportValue = supportValue;
	}
	
	public boolean isCoordinationRequirementPersisted() {
		return coordinationRequirementPersisted;
	}

	public void setCoordinationRequirementPersisted(
			boolean coordinationRequirementPersisted) {
		this.coordinationRequirementPersisted = coordinationRequirementPersisted;
	}

	public void setProject(final Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}

	public boolean isTemporalConsistencyForced() {
		return temporalConsistencyForced;
	}

	public void setTemporalConsistencyForced(final boolean temporalConsistencyForced) {
		this.temporalConsistencyForced = temporalConsistencyForced;
	}

	public abstract JUNGGraph processEntryDependencyGraph(Entry entry, int dependencyType) throws DatabaseException;
	
	public abstract JUNGGraph processDependencyGraph(Dependency entryDependency) throws DatabaseException;
	
	public abstract Matrix processEntryDependencyMatrix(Entry entry, int dependencyType) throws DatabaseException;

	public abstract boolean checkCutoffValues(Entry entry);

}
