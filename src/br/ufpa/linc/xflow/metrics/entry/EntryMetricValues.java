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
 *  ======================
 *  EntryMetricValues.java
 *  ======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.metrics.MetricValuesTable;


@Entity(name = "entry_metrics")
public class EntryMetricValues extends MetricValuesTable {
	
	@Column(name = "ADDED_FILES")
	private int entryAddedFiles;
	
	@Column(name = "MODIFIED_FILES")
	private int entryModifiedFiles;
	
	@Column(name = "DELETED_FILES")
	private int entryDeletedFiles;
	
	@Column(name = "LOC")
	private int entryLOC;
	
	@OneToOne
	@JoinColumn(name = "ENTRY_AUTHOR")
	private Author author;


	public int getEntryAddedFiles() {
		return entryAddedFiles;
	}
	
	public void setEntryAddedFiles(final int entryAddedFiles) {
		this.entryAddedFiles = entryAddedFiles;
	}
	
	public int getEntryModifiedFiles() {
		return entryModifiedFiles;
	}
	
	public void setEntryModifiedFiles(final int entryModifiedFiles) {
		this.entryModifiedFiles = entryModifiedFiles;
	}
	
	public int getEntryDeletedFiles() {
		return entryDeletedFiles;
	}

	public void setEntryDeletedFiles(final int entryDeletedFiles) {
		this.entryDeletedFiles = entryDeletedFiles;
	}

	public int getEntryLOC() {
		return entryLOC;
	}
	
	public void setEntryLOC(final int entryLOC) {
		this.entryLOC = entryLOC;
	}
	
	public Author getAuthor() {
		return author;
	}
	
	public void setAuthor(final Author author) {
		this.author = author;
	}

	public double getValueByName(final String metricName) {
		if(metricName == "Added Files"){
			return this.getEntryAddedFiles();
		}
		else if(metricName == "Modified Files"){
			return this.getEntryModifiedFiles();
		}
		else if(metricName == "Deleted Files"){
			return this.getEntryDeletedFiles();
		}
		else if(metricName == "Entry Lines of Code"){
			return this.getEntryLOC();
		}
		return 0;
	}
}
