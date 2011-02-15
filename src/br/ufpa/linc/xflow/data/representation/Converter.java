package br.ufpa.linc.xflow.data.representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;

import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.representation.jung.JUNGEdge;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.jung.JUNGVertex;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.data.representation.prefuse.PrefuseGraph;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import edu.uci.ics.jung.graph.AbstractTypedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

//TODO: COISAS A FAZER!
public final class Converter {

	/*
	 * LIST<DependencyObject> TO MATRIX
	 */
	public static final Matrix convertDependenciesToMatrix(final List<? extends DependencyObject> dependencies, final boolean isDependencyDirected) throws DatabaseException {
		Matrix matrix = new Matrix(1);
		convertDependenciesToMatrix(dependencies, isDependencyDirected, matrix);
		
		return matrix;
	}
	
	public static final void convertDependenciesToMatrix(final List<? extends DependencyObject> dependencies, final boolean isDependencyDirected, Matrix matrix) throws DatabaseException {

		Matrix resultMatrix = new Matrix(matrix.getRows(), matrix.getColumns());

		if(isDependencyDirected){
			for (DependencyObject dependedObject : dependencies) {
				if(dependedObject.getAssignedStamp() > resultMatrix.getRows()-1){
					resultMatrix = resultMatrix.incrementMatrixRowsTo(dependedObject.getAssignedStamp()+1);
				}

				resultMatrix.increment(1, dependedObject.getAssignedStamp(), 
						dependedObject.getAssignedStamp());
				
				for (DependencyObject dependentObject : dependencies) {
					if(dependentObject.getAssignedStamp() > resultMatrix.getColumns()-1){
						resultMatrix = resultMatrix.incrementMatrixColumnsTo(dependentObject.getAssignedStamp()+1);
					}
					if(dependentObject.getAssignedStamp() > resultMatrix.getRows()-1){
						resultMatrix = resultMatrix.incrementMatrixRowsTo(dependentObject.getAssignedStamp()+1);
					}
					
					resultMatrix.increment(dependentObject.getDependenceDegree(),
							dependentObject.getAssignedStamp(), 
							dependedObject.getAssignedStamp());
				}
			}
		}
		else{
			for (DependencyObject dependedObject : dependencies) {
				if(dependedObject.getAssignedStamp() > (resultMatrix.getRows()-1)){
					resultMatrix = resultMatrix.incrementMatrixRowsTo(dependedObject.getAssignedStamp()+1);
				}
				if(dependedObject.getAssignedStamp() > (resultMatrix.getColumns()-1)){
					resultMatrix = resultMatrix.incrementMatrixColumnsTo(dependedObject.getAssignedStamp()+1);
				}

				resultMatrix.increment(1, dependedObject.getAssignedStamp(), 
						dependedObject.getAssignedStamp());

				for (DependencyObject dependentObject : dependedObject.getDependentObjects()) {
					if(dependentObject.getAssignedStamp() > resultMatrix.getColumns()-1){
						resultMatrix = resultMatrix.incrementMatrixColumnsTo(dependentObject.getAssignedStamp()+1);
					}
					if(dependentObject.getAssignedStamp() > resultMatrix.getRows()-1){
						resultMatrix = resultMatrix.incrementMatrixRowsTo(dependentObject.getAssignedStamp()+1);
					}
					
					resultMatrix.increment(dependentObject.getDependenceDegree(), dependedObject.getAssignedStamp(), dependentObject.getAssignedStamp());
					//matrix.getMatrix()[dependentObject.getAssignedStamp()][dependedObject.getAssignedStamp()]+=dependentObject.getDependenceDegree();
				}
			}
		}
		
		matrix.setSparseMatrix(resultMatrix.getSparseMatrix());
	}


	/*
	 * Dependency TO Matrix
	 */
	
	
	
	/*
	 * LIST<DependencyObject> TO JUNGGraph
	 */
	
	
	/*
	 * LIST<Dependency> TO JUNGGraph
	 */
	
	public static final JUNGGraph convertDependenciesToJUNGGraph(final List<? extends DependencyObject> dependencies, final boolean isDirected) throws DatabaseException {

		final AbstractTypedGraph<JUNGVertex,JUNGEdge> graph;

		if(isDirected){
			graph = new DirectedSparseGraph();
		}
		else {
			graph = new UndirectedSparseGraph();
		}
		
		final JUNGGraph jungGraph = new JUNGGraph();
		jungGraph.setGraph(graph);
		return jungGraph;
	}
	
	
	/*
	 * JUNGGraph TO PrefuseGraph
	 */
	
	public static void convertJungToPrefuseGraph(final JUNGGraph convertee, final PrefuseGraph converted){
		final HashMap<Long,Node> nodeList = new HashMap<Long,Node>();

		for (JUNGEdge edge : convertee.getGraph().getEdges()) {

			final Collection<JUNGVertex> vertexes = convertee.getGraph().getIncidentVertices(edge);
			for (Iterator<JUNGVertex> iterator = vertexes.iterator(); iterator.hasNext();) {
				final JUNGVertex v1 = (JUNGVertex) iterator.next();
				final JUNGVertex v2 = (JUNGVertex) iterator.next();

				Node n1 = nodeList.get(v1.getId());
				Node n2 = nodeList.get(v2.getId());

				if(n1 == null){
					if(v1.getName() == null){
						n1 = converted.createNode(v1.getId(), "null");
					}
					else{
						n1 = converted.createNode(v1.getId(), v1.getName());
					}
					nodeList.put(v1.getId(), n1);
				}
				if(n2 == null){
					if(v2.getName() == null){
						n2 = converted.createNode(v2.getId(), "null");
					}
					else{
						n2 = converted.createNode(v2.getId(), v2.getName());
					}
					nodeList.put(v2.getId(), n2);
				}

				final Edge prefuseEdge = converted.getPrefuseGraph().addEdge(n1, n2);
				prefuseEdge.setLong("weight", edge.getWeight());

			}
		}
	}
	
	public static PrefuseGraph convertJungToPrefuseGrapha(JUNGGraph convertee){
		
		final PrefuseGraph prefuseGraph = new PrefuseGraph();
		final HashMap<Long,Node> nodeList = new HashMap<Long,Node>();

		for (JUNGEdge edge : convertee.getGraph().getEdges()) {

			final Collection<JUNGVertex> vertexes = convertee.getGraph().getIncidentVertices(edge);
			for (Iterator<JUNGVertex> iterator = vertexes.iterator(); iterator.hasNext();) {
				final JUNGVertex v1 = (JUNGVertex) iterator.next();
				final JUNGVertex v2 = (JUNGVertex) iterator.next();

				Node n1 = nodeList.get(v1.getId());
				Node n2 = nodeList.get(v2.getId());

				if(n1 == null){
					if(v1.getName() == null){
						n1 = prefuseGraph.createNode(v1.getId(), "null");
					}
					else{
						n1 = prefuseGraph.createNode(v1.getId(), v1.getName());
					}
					nodeList.put(v1.getId(), n1);
				}
				if(n2 == null){
					if(v2.getName() == null){
						n2 = prefuseGraph.createNode(v2.getId(), "null");
					}
					else{
						n2 = prefuseGraph.createNode(v2.getId(), v2.getName());
					}
					nodeList.put(v2.getId(), n2);
				}

				final Edge prefuseEdge = prefuseGraph.getPrefuseGraph().addEdge(n1, n2);
				prefuseEdge.setLong("weight", edge.getWeight());

			}
		}
		
		return prefuseGraph;
	}
}