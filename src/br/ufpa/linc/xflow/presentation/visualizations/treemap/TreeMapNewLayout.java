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
 *  =====================
 *  TreeMapNewLayout.java
 *  =====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.treemap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

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
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTree;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import br.ufpa.linc.xflow.data.dao.cm.AuthorDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.util.TreeHierarchyBuilder;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.BorderColorAction;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.LabelLayout;
import br.ufpa.linc.xflow.presentation.commons.util.prefuse.NodeRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class TreeMapNewLayout {

	private final double nodeGap = 2;
	
	private Display display; 
	private Tree treemap;
	
	private JSearchPanel filesNameSearchPanel;
	private SearchQueryBinding filesNameListQueryBinding;
	private JCustomSearchPanel filesSequenceSearchPanel;
	private SearchQueryBinding filesSequenceListQueryBinding;

	public TreeMapNewLayout() throws DatabaseException {
		treemap = TreeHierarchyBuilder.createTreeMapGraph(AbstractVisualization.getCurrentAnalysis(), 4000);
	}

	public JPanel draw() {
		
		Visualization visualization = new Visualization();
		VisualTree visualtable = visualization.addTree("tree", treemap);
		
		
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
		createPredicates(visualization, visualtable);
		
		
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
        UILib.setColor(treemapPanel, Color.WHITE, Color.GRAY);
		
        return treemapPanel;
	}

	private void createRenderer(Visualization visualization) {
		DefaultRendererFactory rf = new DefaultRendererFactory();
		
		rf.add(new InGroupPredicate("tree.nodes"), new NodeRenderer(nodeGap));
		rf.add(new InGroupPredicate("folderLabel"), new LabelRenderer("name"));
		rf.add(new InGroupPredicate("fileLabel"), new LabelRenderer("name"));
		
		visualization.setRendererFactory(rf);
	}
	
	private void createDecorators(Visualization visualization) {
		Predicate folderCheck = (Predicate)ExpressionParser.parse("type = 'folder' and childcount()>0");
//		Predicate fileCheck = (Predicate)ExpressionParser.parse("type = 'file'");
		visualization.setVisible("tree.edges", null, false);
//		visualization.addDecorators("folderLabel", "tree.nodes", folderCheck, createFolderLabelSchema());
//		visualization.addDecorators("fileLabel", "tree.nodes", fileCheck, createFileLabelSchema());
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
	
	private void createPredicates(final Visualization visualization, VisualTree visualtable) {
	
		filesNameListQueryBinding = new SearchQueryBinding(visualtable.getNodeTable(), "name");
		filesNameSearchPanel = filesNameListQueryBinding.createSearchPanel();
		visualization.addFocusGroup(Visualization.SEARCH_ITEMS, filesNameListQueryBinding.getSearchSet());
		
		filesNameListQueryBinding.getPredicate().addExpressionListener(new UpdateListener() {
			public void update(Object src) {
				visualization.cancel("animatePaint");
				visualization.run("colors");                        
				visualization.run("animatePaint");
			}
		});
		
		filesSequenceListQueryBinding = new SearchQueryBinding(visualtable.getNodeTable(), "name");
		visualization.addFocusGroup("search2", filesSequenceListQueryBinding.getSearchSet());
		filesSequenceSearchPanel = new JCustomSearchPanel(filesSequenceListQueryBinding.getSearchSet(), "name");
		
		filesSequenceListQueryBinding.getPredicate().addExpressionListener(new UpdateListener() {
			public void update(Object src) {
				visualization.cancel("animatePaint");
				visualization.run("colors");
				visualization.run("animatePaint");
			}
		});
	}
	
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
			}
			public void itemExited(VisualItem item, MouseEvent e) {
			}           
		});
	}

	private ActionList createColorAction() {
		
		int treeDepth = calculateTreeDepth(this.treemap);
		
//		ColorMap cmap = new ColorMap(
//				ColorLib.getInterpolatedPalette(treeDepth+1,
//						ColorLib.rgb(160,160,160),ColorLib.rgb(104,104,104)), 0, treeDepth);
//						ColorLib.rgb(60,60,60), ColorLib.rgb(30,30,30)), 0, treeDepth);
//
//		ColorMap smap = new ColorMap(
//				ColorLib.getInterpolatedPalette(treeDepth+1,
//						ColorLib.rgb(111,11,11),ColorLib.rgb(244,76,67)), 0, treeDepth);
		
		final ColorAction borderColor = new BorderColorAction("tree.nodes");
		final ColorAction fillColor = new TreeMapColorAction("tree.nodes", treeDepth);
		
		ActionList colors = new ActionList();
		colors.add(fillColor);
		colors.add(borderColor);
		return colors;
	}
	
	private ActionList createAnimatePaintAction() {
		ActionList animatePaint = new ActionList(500);
		animatePaint.add(new ColorAnimator("tree.nodes"));
		animatePaint.add(new RepaintAction());
		
		return animatePaint;
	}
	
	private ActionList createLayout(Visualization visualization) {
		ActionList layout = new ActionList();
		layout.add(new SquarifiedTreeMapLayout("tree", 3));
		layout.add(new LabelLayout("folderLabel", nodeGap));
		layout.add(new LabelLayout("fileLabel", nodeGap));
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
		FOLDER_LABELS.setDefault(VisualItem.TEXTCOLOR, ColorLib.rgb(200,200,200));
		FOLDER_LABELS.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",Font.BOLD,12));
		return FOLDER_LABELS;
	}
	
//	private Schema createFileLabelSchema() {
//		Schema FOLDER_LABELS = PrefuseLib.getVisualItemSchema();
//		FOLDER_LABELS.setDefault(VisualItem.INTERACTIVE, false);
//		FOLDER_LABELS.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(200));
//		FOLDER_LABELS.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",12));
//		return FOLDER_LABELS;
//	}

	public Display getDisplay() {
		return display;
	}

	public JSearchPanel getFilesNameSearchPanel() {
		return filesNameSearchPanel;
	}

	public SearchQueryBinding getFilesNameListQueryBinding() {
		return filesNameListQueryBinding;
	}

	public JCustomSearchPanel getFilesSequenceSearchPanel() {
		return filesSequenceSearchPanel;
	}

	public SearchQueryBinding getFilesSequenceListQueryBinding() {
		return filesSequenceListQueryBinding;
	}

	public void updateTree(long newSequence) throws DatabaseException{
		this.treemap = TreeHierarchyBuilder.createTreeMapGraph(AbstractVisualization.getCurrentAnalysis(), newSequence);
		this.getDisplay().getVisualization().reset();
		this.getDisplay().getVisualization().removeGroup(Visualization.SEARCH_ITEMS);
		this.getDisplay().getVisualization().removeGroup("search2");
		VisualTree newVisualTree = this.getDisplay().getVisualization().addTree("tree", treemap);
		this.createDecorators(this.getDisplay().getVisualization());
		this.createPredicates(this.getDisplay().getVisualization(), newVisualTree);
		this.display.getVisualization().run("draw");
	}
	
	public void mapAuthorFilesVisibility(String authorNames) throws DatabaseException{
		String[] lastSelectedAuthor = authorNames.split("\\|");
		if(lastSelectedAuthor.length >=2){
			String lastAuthor = lastSelectedAuthor[lastSelectedAuthor.length-2];
			AuthorDAO authorDAO = new AuthorDAO();
			Author author = authorDAO.findAuthorByName(TreeMapView.getCurrentAnalysis().getProject(), lastAuthor);
			List<String> filesNamesList = authorDAO.getAuthorChangedFilesPath(author, AbstractVisualization.getCurrentAnalysis().getLastEntry());

			StringBuffer highlightFilesQuery = new StringBuffer();
			for (String fileName : filesNamesList) {
				highlightFilesQuery.append(fileName+" | ");
			}
			
			this.getFilesNameSearchPanel().setQuery(highlightFilesQuery.toString());
		}
	}
	
	public void mapFilesSequenceVisibility(String revision) throws DatabaseException {
		/**
		List<String> filesChangedOnSequenceRevision = new ObjFileDAO().getFilesPathFromSequenceNumber(TreeMapView.getCurrentAnalysis().getProject(), Long.parseLong(revision));
		StringBuilder highlightFilesQuery = new StringBuilder();
		for (String filePath : filesChangedOnSequenceRevision) {
			highlightFilesQuery.append(filePath+" | ");
		}
		this.getFilesSequenceSearchPanel().setQuery(highlightFilesQuery.toString());
		*/
	}
}
