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
 *  ================
 *  MetricsUtil.java
 *  ================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics;

import br.ufpa.linc.xflow.metrics.entry.AddedFiles;
import br.ufpa.linc.xflow.metrics.entry.DeletedFiles;
import br.ufpa.linc.xflow.metrics.entry.EntryLOC;
import br.ufpa.linc.xflow.metrics.entry.ModifiedFiles;
import br.ufpa.linc.xflow.metrics.file.Betweenness;
import br.ufpa.linc.xflow.metrics.file.Centrality;
import br.ufpa.linc.xflow.metrics.file.LOC;
import br.ufpa.linc.xflow.metrics.project.ClusterCoefficient;
import br.ufpa.linc.xflow.metrics.project.Density;

public abstract class MetricsUtil {

	public static MetricModel discoverMetricTypeByName(final String selectedMetric) {
		if(selectedMetric.equalsIgnoreCase("Density")){
			return new Density();
		}
		else if(selectedMetric.equalsIgnoreCase("Cluster Coefficient")){
			return new ClusterCoefficient();
		}
		else if(selectedMetric.equalsIgnoreCase("Entry LOC")){
			return new EntryLOC();
		}
		else if(selectedMetric.equalsIgnoreCase("Added Files")){
			return new AddedFiles();
		}
		else if(selectedMetric.equalsIgnoreCase("Modified Files")){
			return new ModifiedFiles();
		}
		else if(selectedMetric.equalsIgnoreCase("Deleted Files")){
			return new DeletedFiles();
		}	
		else if(selectedMetric.equalsIgnoreCase("Betweenness Centrality")){
			return new Betweenness();
		}
		else if(selectedMetric.equalsIgnoreCase("Centrality")){
			return new Centrality();
		}
		else if(selectedMetric.equalsIgnoreCase("LOC")){
			return new LOC();
		}
		return null;
	}
	
}
