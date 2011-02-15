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
 *  =================
 *  PrefuseGraph.java
 *  =================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.representation.prefuse;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;

public class PrefuseGraph{

	private Graph prefuseGraph;
	private Table vertexes;
	private Table edges;
	
	public PrefuseGraph(){
		createDefaultTables();
		setPrefuseGraph(new Graph(vertexes, edges, true, "id1", "id2"));
	}
	
	public PrefuseGraph(final String[] vertexFields, final String[] vertexClasses, final String[] edgeFields, final String[] edgeClasses){
		createCustomTables(vertexFields, vertexClasses, edgeFields, edgeClasses);
		setPrefuseGraph(new Graph(vertexes, edges, true, "id1", "id2"));
	}
	
	public void setPrefuseGraph(final Graph prefuseGraph) {
		this.prefuseGraph = prefuseGraph;
	}

	public Graph getPrefuseGraph() {
		return prefuseGraph;
	}

	public Node createNode(long id, String name){

		Node n = prefuseGraph.addNode();  
		n.set("id", id);  
		n.set("name", name);
		return n;  

	}
	
	private void createDefaultTables(){
		vertexes = new Table();
		vertexes.addColumn("id", int.class);
		vertexes.addColumn("name", String.class);
		
		edges = new Table();
		edges.addColumn("id1", int.class);
		edges.addColumn("id2", int.class);
		edges.addColumn("weight", long.class);
	}
	
	
	
	public Node createCustomNode(final String[] vertexValues, final String[] vertexClasses) {  
		Node n = prefuseGraph.addNode();  
			for (int i = 0; i < vertexValues.length; i++) {
				vertexes.addColumn(vertexValues[i], vertexClasses[i]);
			}
	     return n;  
	 }
	
	private void createCustomTables(final String[] vertexFields, final String[] vertexClasses, final String[] edgeFields, final String[] edgeClasses){

		vertexes = new Table();
		for (int i = 0; i < vertexFields.length; i++) {
			vertexes.addColumn(vertexFields[i], vertexClasses[i]);
		}
		
		edges = new Table();
		for (int i = 0; i < edgeFields.length; i++) {
			edges.addColumn(edgeFields[i], edgeClasses[i]);
		}
	}
	
}
