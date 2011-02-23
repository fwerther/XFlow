/* 
 * XFlow:
 * ___________________________________________________________
 * 
 *  
 *  (C) Copyright 2010, by Francisco Santana.
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
 *  ====================
 *  TreeMapRenderer.java
 *  ====================
 *  
 *  (C) Copyright 2010, by Francisco Santana.
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.treemap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JPanel;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.controls.ControlAdapter;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.util.TreeHierarchyBuilder;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.BorderColorAction;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.FillColorAction;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.LabelLayout;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.NodeRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class TreeMapRenderer {

	private Display display; 
	private Tree treemap;
//	private VisualTree visualTree;
	
	public String teste = "tree";
	
	final ColorAction borderColor = new BorderColorAction(teste+".nodes");
	
	private SearchQueryBinding entryPointsRevision;
	private SearchQueryBinding highlightedFiles;
	
	public TreeMapRenderer() throws DatabaseException {
		treemap = TreeHierarchyBuilder.createTreeMapGraph(AbstractVisualization.getCurrentAnalysis(), 1551);
	}

	public JPanel draw() {
		
		Visualization visualization = new Visualization();
//		visualTree = visualization.addTree(teste, treemap);
		visualization.addTree(teste, treemap);
		
		
		/*
		 * CREATE DATA RENDERER
		 */
		createRenderer(visualization);
		

		/*
		 * CREATE DECORATORS
		 * (e.g. labels settings)
		 */
		createDecorators(visualization);
		
		
		/*
		 * CREATE ACTIONS
		 */
		createActions(visualization);
		
		
		/*
		 * CREATE PREDICATES
		 */
//		createPredicates(visualization);
		
		
		/*
		 * DISPLAY SETUP
		 */
		setupDisplay(visualization);
		
		
		/*
		 * CREATE CONTROLS
		 */
		createControls();
		
		visualization.run("draw");
		
        JPanel treemapPanel = new JPanel(new BorderLayout());
        treemapPanel.add(display, BorderLayout.CENTER);
        UILib.setColor(treemapPanel, Color.BLACK, Color.GRAY);
		
        return treemapPanel;
	}

	private void createRenderer(Visualization visualization) {
		DefaultRendererFactory rf = new DefaultRendererFactory();
		
		rf.add(new InGroupPredicate(teste+".nodes"), new NodeRenderer(0));
		rf.add(new InGroupPredicate("folderLabel"), new LabelRenderer("name"));
		rf.add(new InGroupPredicate("fileLabel"), new LabelRenderer("name"));
		
		visualization.setRendererFactory(rf);
	}
	
	private void createDecorators(Visualization visualization) {
		Predicate folderCheck = (Predicate)ExpressionParser.parse("type = 'folder' and childcount()>0");
		Predicate fileCheck = (Predicate)ExpressionParser.parse("type = 'file'");
		visualization.setVisible(teste+".edges", null, false);
		visualization.addDecorators("folderLabel", teste+".nodes", folderCheck, createFolderLabelSchema());
		visualization.addDecorators("fileLabel", teste+".nodes", fileCheck, createFileLabelSchema());
	}
	
	private void createActions(Visualization visualization) {
		visualization.putAction("colors", createColorAction());
		visualization.putAction("animatePaint", createAnimatePaintAction());
		visualization.putAction("layout", createLayout(visualization));
		
		ActionList draw = new ActionList();
		draw.add(visualization.getAction("colors"));
		draw.add(visualization.getAction("layout"));
		visualization.putAction("draw", draw);
	}
	
//	private void createPredicates(final Visualization visualization) {
//		entryPointsRevision = new SearchQueryBinding(visualTree.getNodeTable(), "sequence");
//		
//		highlightedFiles = new SearchQueryBinding(visualTree.getNodeTable(), "author");
//		visualization.addFocusGroup("highlitedAuthors", highlightedFiles.getSearchSet());
//
//		highlightedFiles.getPredicate().addExpressionListener(new UpdateListener(){
//			public void update(Object object){
//				visualization.run("colors");
//				visualization.run("animatePaint");
//			}
//		});
//		
//	}
	
	private void setupDisplay(final Visualization visualization) {
		this.display = new Display(visualization);
		display.setSize(700,600);
		display.setHighQuality(true);
		display.setItemSorter(new TreeDepthItemSorter(true));
		
		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				display.setHighQuality(false);
				visualization.run("layout");
				display.setHighQuality(true);
			}
		});
	}
	
	private void createControls() {
		display.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				item.setStrokeColor(borderColor.getColor(item));
				item.getVisualization().repaint();
			}
			public void itemExited(VisualItem item, MouseEvent e) {
				item.setStrokeColor(item.getEndStrokeColor());
				item.getVisualization().repaint();
			}           
		});
	}

	private ActionList createColorAction() {
		
		int treeDepth = calculateTreeDepth(this.treemap);
		
		ColorMap cmap = new ColorMap(
				ColorLib.getInterpolatedPalette(10,ColorLib.rgb(213,213,213),ColorLib.rgb(104,104,104)),0,9);
//						ColorLib.rgb(60,60,60), ColorLib.rgb(30,30,30)), 0, treeDepth);

		ColorMap smap = new ColorMap(
				ColorLib.getInterpolatedPalette(treeDepth+1,
						ColorLib.rgb(111,11,11),ColorLib.rgb(244,76,67)), 0, treeDepth);
		
		final ColorAction borderColor = new BorderColorAction(teste+".nodes");
		final ColorAction fillColor = new FillColorAction(teste+".nodes", cmap, smap);
		
		ActionList colors = new ActionList();
		colors.add(fillColor);
		colors.add(borderColor);
		return colors;
	}
	
	private ActionList createAnimatePaintAction() {
		ActionList animatePaint = new ActionList(1);
		animatePaint.add(new ColorAnimator(teste+".nodes"));
		animatePaint.add(new RepaintAction());
		
		return animatePaint;
	}
	
	private ActionList createLayout(Visualization visualization) {
		ActionList layout = new ActionList();
		layout.add(new SquarifiedTreeMapLayout(teste));
		layout.add(new LabelLayout("folderLabel", 10));
//		layout.add(new LabelLayout("fileLabel", 0));
		layout.add(visualization.getAction("colors"));
		layout.add(new RepaintAction());
		
		return layout;
	}

	private int calculateTreeDepth(Tree tree) {
		int depth = 0;
		Iterator<?> iter = tree.nodes();
		while (iter.hasNext()) {
			Node n = (Node) iter.next();
			int d = n.getDepth();
			if (d>depth) {
				depth=d;
			}
		}
		return depth;
	}

	private Schema createFolderLabelSchema() {
		Schema FOLDER_LABELS = PrefuseLib.getVisualItemSchema();
		FOLDER_LABELS.setDefault(VisualItem.INTERACTIVE, false);
		FOLDER_LABELS.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(200));
		FOLDER_LABELS.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",16));
		return FOLDER_LABELS;
	}
	
	private Schema createFileLabelSchema() {
		Schema FOLDER_LABELS = PrefuseLib.getVisualItemSchema();
		FOLDER_LABELS.setDefault(VisualItem.INTERACTIVE, false);
		FOLDER_LABELS.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(200));
		FOLDER_LABELS.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",12));
		return FOLDER_LABELS;
	}
	
	public Display getDisplay() {
		return display;
	}

	public void updateTree(long newRevision){
//		this.treemap = TreeHierarchyBuilder.createTreeMapGraph(AbstractVisualization.getCurrentAnalysis(), newRevision);
//		this.getDisplay().getVisualization().addTree("tree2", treemap);
//		this.teste = "tree2";
////		VisualTree vt = vis.addTree("tree", treemap);
//		this.display.getVisualization().run("draw");
	}

	public SearchQueryBinding getEntryPointsRevision() {
		return entryPointsRevision;
	}

	public void setEntryPointsRevision(SearchQueryBinding entryPointsRevision) {
		this.entryPointsRevision = entryPointsRevision;
	}

	public SearchQueryBinding getHighlightedFiles() {
		return highlightedFiles;
	}

	public void setHighlightedFiles(SearchQueryBinding highlightedFiles) {
		this.highlightedFiles = highlightedFiles;
	}
}
