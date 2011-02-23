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
 *  ===============
 *  Visualizer.java
 *  ===============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import br.ufpa.linc.xflow.data.dao.metrics.MetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.AddedFiles;
import br.ufpa.linc.xflow.metrics.entry.DeletedFiles;
import br.ufpa.linc.xflow.metrics.entry.EntryLOC;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.entry.ModifiedFiles;
import br.ufpa.linc.xflow.metrics.file.Betweenness;
import br.ufpa.linc.xflow.metrics.file.Centrality;
import br.ufpa.linc.xflow.metrics.file.FileMetricModel;
import br.ufpa.linc.xflow.metrics.project.ClusterCoefficient;
import br.ufpa.linc.xflow.metrics.project.Density;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.presentation.commons.AnalysisInfoPanel;
import br.ufpa.linc.xflow.presentation.commons.DevelopersPanelControl;
import br.ufpa.linc.xflow.presentation.commons.util.ColorPalette;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.activity.ActivityView;
import br.ufpa.linc.xflow.presentation.visualizations.graph.GraphView;
import br.ufpa.linc.xflow.presentation.visualizations.line.LineView;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.ScatterPlotView;
import br.ufpa.linc.xflow.presentation.visualizations.treemap.TreeMapView;

public class Visualizer {

	public static final int LINE_VIEW = 0;
	public static final int GRAPH_VIEW = 1;
	public static final int SCATTERPLOT_VIEW = 2;
	public static final int TREEMAP_VIEW = 3;
	public static final int ACTIVITY_VIEW = 4;
	
	private boolean[] visibleVisualizations;
	
	private static LineView lineView = null;
	private static GraphView graphView = null;
	private static ScatterPlotView scatterPlotView = null;
	private static TreeMapView treeMapView = null;
	private static ActivityView activityView = null;
	
	private static DevelopersPanelControl developersPanel;
	private static AnalysisInfoPanel analysisInfoBar;
	
	private static ProjectMetricModel[] availableProjectMetrics = new ProjectMetricModel[]{
		new Density(), new ClusterCoefficient()
	};
	private static EntryMetricModel[] availableEntryMetrics = new EntryMetricModel[]{
		new AddedFiles(), new ModifiedFiles(), new DeletedFiles(), new EntryLOC()
	};
	private static FileMetricModel[] availableFileMetrics = new FileMetricModel[]{
		new Centrality(), new Betweenness()
//		, new EntryLOC()
	};
	
	private static Metrics metricsSession;
	
	private JPanel visualizationsPanel;
	
	public JPanel composeVisualizationsPane(Metrics metrics) throws DatabaseException{
		metricsSession = metrics;
		JTabbedPane visualizationsPlacer = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		AbstractVisualization.setCurrentAnalysis(metrics.getAssociatedAnalysis());
		List<Author> validAuthors = metrics.getAssociatedAnalysis().getProject().getAuthorsListByEntries(metrics.getAssociatedAnalysis().getFirstEntry(), metrics.getAssociatedAnalysis().getLastEntry());
		ColorPalette.initiateColors(validAuthors.size());

		/*
		 * COMMON CONTROLERS
		 */
		analysisInfoBar = new AnalysisInfoPanel(metrics.getAssociatedAnalysis());
		Component analysisInfoPanel = analysisInfoBar.createInfoPanel();
		
		developersPanel = new DevelopersPanelControl(validAuthors);
		Component developersPane = developersPanel.createControlPanel();
		
		
		/*
		 * VISUALIZATIONS
		 */
		System.out.println("generating line view");
		if(visibleVisualizations[LINE_VIEW]){
			lineView = new LineView();
			JPanel lineViewPanel = lineView.composeVisualizationPanel();
			visualizationsPlacer.addTab("Line view", lineViewPanel);
		}
		
		System.out.println("generating graph view");
		if(visibleVisualizations[GRAPH_VIEW]){
			graphView = new GraphView();
			JPanel graphViewPanel = graphView.composeVisualizationPanel();
			visualizationsPlacer.addTab("Graph View", graphViewPanel);
		}
		
		System.out.println("generating scatterplot");
		if(visibleVisualizations[SCATTERPLOT_VIEW]){
			scatterPlotView = new ScatterPlotView();
			JPanel scatterPlotPanel = scatterPlotView.composeVisualizationPanel();
			visualizationsPlacer.addTab("Scatter Plot View", scatterPlotPanel);
		}
		
		System.out.println("generating treemap");
		if(visibleVisualizations[TREEMAP_VIEW]){
			treeMapView = new TreeMapView();
//			JPanel treeMapPanel = treeMapView.composeVisualizationPanel();
//			visualizationsPlacer.addTab("Treemap View", treeMapPanel);

			JPanel treeMapPanel2 = treeMapView.composeVisualizationPanel2();
			visualizationsPlacer.addTab("Treemap View - new layout", treeMapPanel2);

		}

		if(visibleVisualizations[ACTIVITY_VIEW]){
			activityView = new ActivityView();
			JComponent activityPanel = activityView.composeVisualizationPanel();
			visualizationsPlacer.addTab("Activity View", activityPanel);
		}
		
		/*
		 * PANEL CONSTRUCTION
		 */
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setLeftComponent(developersPane);
		splitPane.setRightComponent(visualizationsPlacer);
		splitPane.setOneTouchExpandable(true);

		visualizationsPanel = new JPanel(new BorderLayout());
		visualizationsPanel.add(analysisInfoPanel, BorderLayout.SOUTH);
		visualizationsPanel.add(splitPane, BorderLayout.CENTER);
		
		return visualizationsPanel;
	}

	public static Metrics getMetricsSession() {
		return metricsSession;
	}

	public static LineView getLineView() {
		return lineView;
	}

	public static GraphView getGraphView() {
		return graphView;
	}

	public static ScatterPlotView getScatterPlotView() {
		return scatterPlotView;
	}

	public static TreeMapView getTreeMapView() {
		return treeMapView;
	}

	public static ActivityView getActivityView() {
		return activityView;
	}

	public static DevelopersPanelControl getDevelopersPanel() {
		return developersPanel;
	}

	public static AnalysisInfoPanel getAnalysisInfoBar() {
		return analysisInfoBar;
	}

	public static ProjectMetricModel[] getAvailableProjectMetrics() {
		return availableProjectMetrics;
	}

	public static void setAvailableProjectMetrics(
			ProjectMetricModel[] availableProjectMetrics) {
		Visualizer.availableProjectMetrics = availableProjectMetrics;
	}

	public static EntryMetricModel[] getAvailableEntryMetrics() {
		return availableEntryMetrics;
	}

	public static void setAvailableEntryMetrics(
			EntryMetricModel[] availableEntryMetrics) {
		Visualizer.availableEntryMetrics = availableEntryMetrics;
	}

	public static FileMetricModel[] getAvailableFileMetrics() {
		return availableFileMetrics;
	}

	public static void setAvailableFileMetrics(
			FileMetricModel[] availableFileMetrics) {
		Visualizer.availableFileMetrics = availableFileMetrics;
	}

	public static List<String> getAvailableProjectMetricsNames() {
		List<String> projectMetricsNames = new Vector<String>();
		for (int i = 0; i < availableProjectMetrics.length; i++) {
			projectMetricsNames.add(availableProjectMetrics[i].getMetricName());
		}
		
		return projectMetricsNames;
	}
	
	public static List<String> getAvailableEntryMetricsNames() {
		List<String> entryMetricsNames = new Vector<String>();
		for (int i = 0; i < availableEntryMetrics.length; i++) {
			entryMetricsNames.add(availableEntryMetrics[i].getMetricName()); 
		}
		
		return entryMetricsNames;
	}
	
	public static List<String> getAvailableFileMetricsNames() {
		List<String> fileMetricsNames = new Vector<String>();
		for (int i = 0; i < availableFileMetrics.length; i++) {
			fileMetricsNames.add(availableFileMetrics[i].getMetricName());
		}
		
		return fileMetricsNames;
	}
	
	public void setEnabledVisualizations(boolean[] visualizationsList){
		this.visibleVisualizations = visualizationsList;
	}

	public static void main(String[] args) {
		try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	    } catch (Exception e) {
	           e.printStackTrace();
	    }

		Visualizer vis = new Visualizer();
		vis.setEnabledVisualizations(new boolean[]{false, false, true, false, false});
		
		JFrame jframe = new JFrame();
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			jframe.add(vis.composeVisualizationsPane(new MetricsDAO().findById(Metrics.class, 3L)));
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jframe.setVisible(true);
		jframe.pack();
	}	
	
}
