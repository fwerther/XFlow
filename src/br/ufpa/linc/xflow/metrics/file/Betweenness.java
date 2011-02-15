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
 *  Betweenness.java
 *  ================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.file;

import br.ufpa.linc.xflow.data.dao.FileMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.jung.JUNGEdge;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.jung.JUNGVertex;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.Graph;

public class Betweenness extends FileMetricModel {

	@Override
	public void evaluate(final JUNGGraph graph, final long fileID, final FileMetricValues table) {
		final Graph<JUNGVertex, JUNGEdge> dependencyGraph = graph.getGraph();
		final BetweennessCentrality<JUNGVertex, JUNGEdge> ranker = new BetweennessCentrality<JUNGVertex, JUNGEdge>(graph.getGraph());
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.evaluate();

		JUNGVertex vertex = null;
		if(FileMetricModel.verticesCache.containsKey(fileID)){
			vertex = FileMetricModel.verticesCache.get(fileID);
		} else {
			for (JUNGVertex graphVertex : dependencyGraph.getVertices()) {
				if(graphVertex.getId() == fileID){
					vertex = graphVertex;
					FileMetricModel.verticesCache.put(fileID, vertex);
					break;
				}
			}
		}

		final double betweenness = ranker.getVertexRankScore(vertex);
		table.setBetweennessCentrality(betweenness);
	}

	@Override
	public final String getMetricName() {
		return "Betweenness centrality";
	}

	@Override
	public final double getAverageValue(final Analysis analysis) throws DatabaseException {
		return new FileMetricsDAO().getBetweennessAverageValue(analysis);
	}
	
	@Override
	public final double getStdDevValue(final Analysis analysis) throws DatabaseException {
		return new FileMetricsDAO().getBetweennessDeviationValue(analysis);
	}

	@Override
	public final double getMetricValue(final Analysis analysis, final Entry entry) throws DatabaseException {
		return new FileMetricsDAO().getBetweennessValueByEntry(analysis, entry);
	}
}
