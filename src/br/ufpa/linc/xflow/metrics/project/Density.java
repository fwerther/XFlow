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
 *  ============
 *  Density.java
 *  ============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.project;

import br.ufpa.linc.xflow.data.dao.ProjectMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.jung.JUNGEdge;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.jung.JUNGVertex;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import edu.uci.ics.jung.graph.Graph;

public class Density extends ProjectMetricModel {

	@Override
	public final void evaluate(final JUNGGraph graph, final ProjectMetricValues table) {
		if(graph == null){
			table.setDensity(0);
		}
		else {
			final Graph<JUNGVertex, JUNGEdge> dependencyGraph = graph.getGraph(); 

			double density = 0;
			final double nodes = dependencyGraph.getVertexCount();
			final double edges = dependencyGraph.getEdgeCount();

			density = 2*edges/(nodes*(nodes-1));
			if (Double.valueOf(density).isNaN()){
				density = 0;
			}

			table.setDensity(density);
		}		
	}

	@Override
	public final String getMetricName() {
		return "Density";
	}

	@Override
	public final double getAverageValue(final Analysis analysis) throws DatabaseException {
		return new ProjectMetricsDAO().getDensityAverageValue(analysis);
	}
	
	@Override
	public final double getStdDevValue(final Analysis analysis) throws DatabaseException {
		return new ProjectMetricsDAO().getDensityDeviationValue(analysis);
	}

	@Override
	public final double getMetricValue(final Analysis analysis, final Entry entry) throws DatabaseException {
		return new ProjectMetricsDAO().getDensityMetricValueByEntry(analysis, entry);
	}

}
