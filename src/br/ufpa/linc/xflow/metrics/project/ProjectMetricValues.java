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
 *  ProjectMetricValues.java
 *  ========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.project;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.ufpa.linc.xflow.metrics.MetricValuesTable;


@Entity(name = "project_metrics")
public class ProjectMetricValues extends MetricValuesTable {
	
	@Column(name = "DENSITY")
	private double density;
	
	@Column(name = "CLUSTERCOEFF")
	private double clusterCoefficient;
	
	
	public double getDensity() {
		return density;
	}
	
	public void setDensity(double density) {
		this.density = density;
	}
	
	public double getClusterCoefficient() {
		return clusterCoefficient;
	}
	
	public void setClusterCoefficient(double clusterCoefficient) {
		this.clusterCoefficient = clusterCoefficient;
	}

	public double getValueByName(String metricName) {
		if(metricName == "Cluster Coefficient"){
			return this.getClusterCoefficient();
		}
		else if(metricName == "Density"){
			return this.getDensity();
		}
		return 0;
	}
}
