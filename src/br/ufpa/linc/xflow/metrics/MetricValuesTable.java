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
 *  ========================
 *  AbstractMetricTable.java
 *  ========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;

@MappedSuperclass
public abstract class MetricValuesTable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "METRICS_ID")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "METRICS_ENTRY")
	private Entry entry;
	
	@OneToOne
	@JoinColumn(name = "METRICS_ANALYSIS")
	private Analysis associatedAnalysis;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public Entry getEntry() {
		return this.entry;
	}

	public void setEntry(final Entry entry) {
		this.entry = entry;
	}

	public Analysis getAssociatedAnalysis() {
		return associatedAnalysis;
	}

	public void setAssociatedAnalysis(final Analysis associatedAnalysis) {
		this.associatedAnalysis = associatedAnalysis;
	}
}
