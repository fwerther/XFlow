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
 *  ==============
 *  JUNGGraph.java
 *  ==============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.representation.jung;

import java.util.HashMap;

import br.ufpa.linc.xflow.data.dao.core.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

//TODO: Ainda tem coisa pra fazer aqui!
public class JUNGGraph {

	private AbstractTypedGraph<JUNGVertex, JUNGEdge> graph;
	
	private static HashMap<Long, JUNGVertex> verticesCache;
	private static HashMap<Integer, FileDependencyObject> stampsCache;
	
	public JUNGGraph(){
		this.graph = new UndirectedSparseGraph<JUNGVertex, JUNGEdge>();
	}
	
	public AbstractTypedGraph<JUNGVertex, JUNGEdge> getGraph() {
		return this.graph;
	}
	
	public static HashMap<Long, JUNGVertex> getVerticesCache() {
		return verticesCache;
	}

	public void setGraph(final AbstractTypedGraph<JUNGVertex, JUNGEdge> graph) {
		this.graph = graph;
	}
	
	public static JUNGGraph convertMatrixToJUNGGraph(final Matrix matrix, final Dependency dependency) throws DatabaseException{
		
		final AbstractTypedGraph<JUNGVertex,JUNGEdge> graph;
		
		if(dependency.isDirectedDependency()){
			if(dependency.getType() == Dependency.COORD_REQUIREMENTS){
				graph = transformCoordinationRequirementMatrixToDirectedGraph(matrix, dependency.getAssociatedAnalysis());
			}
			else if(dependency.getType() == Dependency.TASK_ASSIGNMENT){
				graph = transformTaskAssignmentMatrixToDirectedGraph(matrix, dependency.getAssociatedAnalysis());
			}
			else {
				graph = transformTaskDependencyToDirectedGraph(matrix, dependency.getAssociatedAnalysis());
			}
		}
		else{
			if(dependency.getType() == Dependency.COORD_REQUIREMENTS){
				graph = transformCoordinationRequirementMatrixToUndirectedGraph(matrix, dependency.getAssociatedAnalysis());
			}
			else if(dependency.getType() == Dependency.TASK_ASSIGNMENT){
				graph = transformTaskAssignmentMatrixToUndirectedGraph(matrix, dependency.getAssociatedAnalysis());
			}
			else {
				graph = transformTaskDependencyToUndirectedGraph(matrix, dependency.getAssociatedAnalysis());
			}
		}
		
		final JUNGGraph jungGraph = new JUNGGraph();
		jungGraph.setGraph(graph);
		return jungGraph;
	}
	
	public static JUNGGraph convertMatrixToJUNGGraph(final Matrix matrix, final Dependency dependency, final JUNGGraph graph) throws DatabaseException{
		
		if(dependency.isDirectedDependency()){
		}
		else{
			if(dependency.getType() == Dependency.COORD_REQUIREMENTS){
			}
			else if(dependency.getType() == Dependency.TASK_ASSIGNMENT){
			}
			else {
				transformTaskDependencyToUndirectedGraph(matrix, dependency.getAssociatedAnalysis(), graph);
			}
		}
		
		return graph;
	}
	
	private static AbstractTypedGraph<JUNGVertex, JUNGEdge> transformCoordinationRequirementMatrixToDirectedGraph(Matrix matrix, Analysis associatedAnalysis) {
		return null;
	}

	private static AbstractTypedGraph<JUNGVertex, JUNGEdge> transformTaskAssignmentMatrixToDirectedGraph(Matrix matrix, Analysis associatedAnalysis) {
		return null;
	}

	private static AbstractTypedGraph<JUNGVertex, JUNGEdge> transformTaskDependencyToDirectedGraph(Matrix matrix, Analysis associatedAnalysis) {
		return null;
	}

	private final static AbstractTypedGraph<JUNGVertex, JUNGEdge> transformCoordinationRequirementMatrixToUndirectedGraph(final Matrix matrix, final Analysis associatedAnalysis) throws DatabaseException {
		final AuthorDependencyObjectDAO fileDependencyDAO = new AuthorDependencyObjectDAO();
		final UndirectedSparseGraph<JUNGVertex, JUNGEdge> graph = new UndirectedSparseGraph<JUNGVertex, JUNGEdge>();
		
		if(verticesCache == null){
			verticesCache = new HashMap<Long, JUNGVertex>();
		}
		
		for (int i = 0; i < matrix.getRows(); i++) {
			final AuthorDependencyObject dependedFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, i);
			final JUNGVertex vertex1;
			if(verticesCache.containsKey(dependedFile.getAuthor().getId())){
				vertex1 = verticesCache.get(dependedFile.getAuthor().getId());
			} else {
				vertex1 = new JUNGVertex();
				vertex1.setId(dependedFile.getAuthor().getId());
				vertex1.setName(dependedFile.getDependencyObjectName());
				verticesCache.put(dependedFile.getAuthor().getId(), vertex1);
				graph.addVertex(vertex1);
			}
			for (int j = i+1; j < matrix.getColumns(); j++) {
				final int edgeWeight = matrix.get(i,j);
				if(edgeWeight > 0){
					final AuthorDependencyObject dependentFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, j);
					final JUNGEdge edge = new JUNGEdge(edgeWeight);
					final JUNGVertex vertex2;
					if(verticesCache.containsKey(dependentFile.getAuthor().getId())){
						vertex2 = verticesCache.get(dependentFile.getAuthor().getId());
					} else {
						vertex2 = new JUNGVertex();
						vertex2.setId(dependentFile.getAuthor().getId());
						vertex2.setName(dependentFile.getDependencyObjectName());
						verticesCache.put(dependentFile.getAuthor().getId(), vertex2);
						graph.addVertex(vertex2);
					}
					graph.addEdge(edge, vertex1, vertex2);
				}
			}
		}
		return graph;
	}

	private static AbstractTypedGraph<JUNGVertex, JUNGEdge> transformTaskAssignmentMatrixToUndirectedGraph(final Matrix matrix, final Analysis associatedAnalysis) throws DatabaseException {
		
		final AuthorDependencyObjectDAO authorDependencyDAO = new AuthorDependencyObjectDAO();
		final FileDependencyObjectDAO fileDependencyDAO = new FileDependencyObjectDAO();
		final UndirectedSparseGraph<JUNGVertex, JUNGEdge> graph = new UndirectedSparseGraph<JUNGVertex, JUNGEdge>();
		
		for (int i = 0; i < matrix.getRows(); i++) {
			final AuthorDependencyObject dependedAuthor = authorDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, i);
			final JUNGVertex vertex1 = new JUNGVertex();
			vertex1.setId(dependedAuthor.getId());
			vertex1.setName(dependedAuthor.getDependencyObjectName());
			graph.addVertex(vertex1);
			for (int j = 0; j < matrix.getColumns(); j++) {
				final int edgeWeight = matrix.get(i, j);
				if(edgeWeight > 0){
					final FileDependencyObject dependentFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, j); 
					final JUNGEdge edge = new JUNGEdge(edgeWeight);
					final JUNGVertex vertex2 = new JUNGVertex();
					vertex2.setId(dependentFile.getId());
					vertex2.setName(dependentFile.getDependencyObjectName());
					graph.addVertex(vertex2);
					graph.addEdge(edge, vertex1, vertex2);
				}
			}
		}
		return graph;
	}

	private static AbstractTypedGraph<JUNGVertex, JUNGEdge> transformTaskDependencyToUndirectedGraph(final Matrix matrix, final Analysis associatedAnalysis) throws DatabaseException {
		
		final FileDependencyObjectDAO fileDependencyDAO = new FileDependencyObjectDAO();
		final UndirectedSparseGraph<JUNGVertex, JUNGEdge> graph = new UndirectedSparseGraph<JUNGVertex, JUNGEdge>();
		
		for (int i = 0; i < matrix.getRows(); i++) {
			final FileDependencyObject dependedFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, i);
			final JUNGVertex vertex1;
			if(verticesCache.containsKey(dependedFile.getFile().getId())){
				vertex1 = verticesCache.get(dependedFile.getFile().getId());
			} else {
				vertex1 = new JUNGVertex();
				vertex1.setId(dependedFile.getFile().getId());
				vertex1.setName(dependedFile.getDependencyObjectName());
				verticesCache.put(dependedFile.getFile().getId(), vertex1);
				graph.addVertex(vertex1);
			}
			for (int j = i+1; j < matrix.getColumns(); j++) {
				final int edgeWeight = matrix.get(i,j);
				if(edgeWeight > 0){
					final FileDependencyObject dependentFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, j);
					final JUNGEdge edge = new JUNGEdge(edgeWeight);
					final JUNGVertex vertex2;
					if(verticesCache.containsKey(dependentFile.getFile().getId())){
						vertex2 = verticesCache.get(dependentFile.getFile().getId());
					} else {
						vertex2 = new JUNGVertex();
						vertex2.setId(dependentFile.getFile().getId());
						vertex2.setName(dependentFile.getDependencyObjectName());
						verticesCache.put(dependentFile.getFile().getId(), vertex2);
						graph.addVertex(vertex2);
					}
					graph.addEdge(edge, vertex1, vertex2);
				}
			}
		}
		return graph;
	}
	
	private static void transformTaskDependencyToUndirectedGraph(final Matrix matrix, final Analysis associatedAnalysis, JUNGGraph graph) throws DatabaseException {
		
		final FileDependencyObjectDAO fileDependencyDAO = new FileDependencyObjectDAO();
		final UndirectedSparseGraph<JUNGVertex, JUNGEdge> dependencyGraph = (UndirectedSparseGraph<JUNGVertex, JUNGEdge>) graph.getGraph();
		
		for (int i = 0; i < matrix.getRows(); i++) {
			final FileDependencyObject dependedFile;
			if(stampsCache.containsKey(i)){
				dependedFile = stampsCache.get(i);
			} else {
				dependedFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, i);
				stampsCache.put(i, dependedFile);
			}

			if(dependedFile != null){
				final JUNGVertex vertex1;
				if(verticesCache.containsKey(dependedFile.getFile().getId())){
					vertex1 = verticesCache.get(dependedFile.getFile().getId());
				} else {
					vertex1 = new JUNGVertex();
					vertex1.setId(dependedFile.getFile().getId());
					vertex1.setName(dependedFile.getDependencyObjectName());
					verticesCache.put(dependedFile.getFile().getId(), vertex1);
					dependencyGraph.addVertex(vertex1);
				}
				for (int j = i+1; j < matrix.getColumns(); j++) {
					final int edgeWeight = matrix.get(i,j);
					if(edgeWeight > 0){
						final FileDependencyObject dependentFile = fileDependencyDAO.findDependencyObjectByStamp(associatedAnalysis, j);
						final JUNGEdge edge = new JUNGEdge(edgeWeight);
						final JUNGVertex vertex2;
						if(verticesCache.containsKey(dependentFile.getFile().getId())){
							vertex2 = verticesCache.get(dependentFile.getFile().getId());
						} else {
							vertex2 = new JUNGVertex();
							vertex2.setId(dependentFile.getFile().getId());
							vertex2.setName(dependentFile.getDependencyObjectName());
							verticesCache.put(dependentFile.getFile().getId(), vertex2);
							dependencyGraph.addVertex(vertex2);
						}
						dependencyGraph.addEdge(edge, vertex1, vertex2);
					}
				}
			}
		}

		graph.setGraph(dependencyGraph);
	}

	public static void clearVerticesCache() {
		verticesCache = new HashMap<Long, JUNGVertex>();
		stampsCache = new HashMap<Integer, FileDependencyObject>();
	}	

}
