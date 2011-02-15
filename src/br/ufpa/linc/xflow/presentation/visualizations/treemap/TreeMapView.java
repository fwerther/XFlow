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
 *  TreeMapView.java
 *  ================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.treemap;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.treemap.controls.EntryPointsControl;

public class TreeMapView extends AbstractVisualization {

	private TreeMapRenderer treeMapRenderer;
	private TreeMapNewLayout treeMapNewLayout;
	
	//Controls.
	private EntryPointsControl entryPointsControl;
	
	@Override
	public JPanel composeVisualizationPanel() throws DatabaseException {
		this.treeMapRenderer = new TreeMapRenderer();
		return this.treeMapRenderer.draw();
	}
	
	public JPanel composeVisualizationPanel2() throws DatabaseException {
		this.treeMapNewLayout = new TreeMapNewLayout();
		
		JPanel treemapPanel = new JPanel(new BorderLayout());
		treemapPanel.add(this.treeMapNewLayout.draw(), BorderLayout.CENTER);
		
		entryPointsControl = new EntryPointsControl();
		treemapPanel.add(this.entryPointsControl.getControlComponent(), BorderLayout.SOUTH);
		
		return treemapPanel;
	}

	public void setTreeMapRenderer(TreeMapRenderer treeMapRenderer) {
		this.treeMapRenderer = treeMapRenderer;
	}

	public TreeMapRenderer getTreeMapRenderer() {
		return treeMapRenderer;
	}

	public TreeMapNewLayout getTreeMapNewLayout() {
		return treeMapNewLayout;
	}

	public void setTreeMapNewLayout(TreeMapNewLayout treeMapNewLayout) {
		this.treeMapNewLayout = treeMapNewLayout;
	}
	
}
