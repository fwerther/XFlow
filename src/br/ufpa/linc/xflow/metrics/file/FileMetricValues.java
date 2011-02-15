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
 *  =====================
 *  FileMetricValues.java
 *  =====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.file;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.ufpa.linc.xflow.metrics.MetricValuesTable;


@Entity(name = "file_metrics")
public class FileMetricValues extends MetricValuesTable {
	
	@Column(name = "CENTRALITY")
	private int centrality;
	
	@Column(name = "BETWEENNESS")
	private double betweennessCentrality;
	
	@Column(name = "LOC")
	private int LOC;
	
	@Column(name = "FILE_ID")
	private long fileID;
	
	
	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public long getFileID() {
		return fileID;
	}
	
	public int getCentrality() {
		return centrality;
	}
	
	public void setCentrality(int centrality) {
		this.centrality = centrality;
	}
	
	public double getBetweennessCentrality() {
		return betweennessCentrality;
	}
	
	public void setBetweennessCentrality(double betweennessCentrality) {
		this.betweennessCentrality = betweennessCentrality;
	}
	
	public int getLOC() {
		return LOC;
	}
	
	public void setLOC(int lOC) {
		LOC = lOC;
	}
}
