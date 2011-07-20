package br.ufpa.linc.xflow.data.representation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import prefuse.data.Edge;
import prefuse.data.Node;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.representation.jung.JUNGEdge;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.jung.JUNGVertex;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.data.representation.matrix.MatrixFactory;
import br.ufpa.linc.xflow.data.representation.prefuse.PrefuseGraph;

//TODO: COISAS A FAZER!
public final class Converter {

	/*
	 * LIST<DependencyObject> TO MATRIX
	 */
	
	public static Matrix convertDependenciesToMatrix(List<DependencySet> dependencies, boolean isDependencyDirected) {
		Matrix matrix = MatrixFactory.createMatrix();
		matrix = convertDependenciesToMatrix(dependencies, isDependencyDirected, matrix);
		
		return matrix;
	}
	
	private static Matrix convertDependenciesToMatrix(List<DependencySet> dependencies, boolean isDependencyDirected, Matrix matrix) {

		Matrix resultMatrix = MatrixFactory.createMatrix();

		if(isDependencyDirected){
			for (DependencySet dependencySet : dependencies) {
				if(dependencySet.getDependedObject().getAssignedStamp() > (resultMatrix.getColumns()-1)){
					resultMatrix.incrementMatrixRowsTo(dependencySet.getDependedObject().getAssignedStamp()-resultMatrix.getColumns()+1);
				}

				Set<DependencyObject> dependentObjects = dependencySet.getDependenciesMap().keySet();
				for (DependencyObject dependentObject : dependentObjects) {
					if(dependentObject.getAssignedStamp() > (resultMatrix.getRows()-1)){
						resultMatrix.incrementMatrixColumnsTo(dependencySet.getDependedObject().getAssignedStamp()-resultMatrix.getRows()+1);
					}
					
					resultMatrix.incrementValueAt((Integer) dependencySet.getDependenciesMap().get(dependentObject), dependencySet.getDependedObject().getAssignedStamp(), dependentObject.getAssignedStamp());
				}
			}
		}
		else{
			for (DependencySet dependencySet : dependencies) {
				if(dependencySet.getDependedObject().getAssignedStamp() > (resultMatrix.getRows()-1)){
					resultMatrix.incrementMatrixRowsTo(dependencySet.getDependedObject().getAssignedStamp()-resultMatrix.getRows()+1);
				}
				if(dependencySet.getDependedObject().getAssignedStamp() > (resultMatrix.getColumns()-1)){
					resultMatrix.incrementMatrixColumnsTo(dependencySet.getDependedObject().getAssignedStamp()-resultMatrix.getColumns()+1);
				}

				Set<DependencyObject> dependentObjects = dependencySet.getDependenciesMap().keySet();
				for (DependencyObject dependentObject : dependentObjects) {
					if(dependentObject.getAssignedStamp() > (resultMatrix.getRows()-1)){
						resultMatrix.incrementMatrixRowsTo(dependentObject.getAssignedStamp()-resultMatrix.getRows()+1);
					}
					if(dependentObject.getAssignedStamp() > (resultMatrix.getColumns()-1)){
						resultMatrix.incrementMatrixColumnsTo(dependentObject.getAssignedStamp()-resultMatrix.getColumns()+1);
					}
					
					if(dependencySet.getDependedObject().getAssignedStamp() == dependentObject.getAssignedStamp()){
						resultMatrix.incrementValueAt((Integer) dependencySet.getDependenciesMap().get(dependentObject), dependencySet.getDependedObject().getAssignedStamp(), dependentObject.getAssignedStamp());
						continue;
					}
					
					int x = dependencySet.getDependedObject().getAssignedStamp();
					int y = dependentObject.getAssignedStamp();
					int v = (Integer) dependencySet.getDependenciesMap().get(dependentObject);
					
					resultMatrix.incrementValueAt(v, x, y);
					resultMatrix.incrementValueAt(v, y, x);
					
				}
			}
		}

		return resultMatrix;
	}
	
	
	/*
	 * JUNGGraph TO PrefuseGraph
	 */
	
	public static void convertJungToPrefuseGraph(final JUNGGraph convertee, PrefuseGraph converted){
		converted = Converter.convertJungToPrefuseGraph(convertee);
	}
	
	public static PrefuseGraph convertJungToPrefuseGraph(JUNGGraph convertee){
		
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