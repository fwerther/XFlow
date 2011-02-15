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
 *  ========================
 *  ScatterPlotRenderer.java
 *  ========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.scatterplot;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.AxisRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;
import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.dao.EntryMetricsDAO;
import br.ufpa.linc.xflow.data.dao.ProjectMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricValues;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricValues;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.presentation.commons.util.ColorPalette;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.ToolTipControl;

public class ScatterPlotRenderer {

	private Display display;
	private Table dataTable;
	private VisualTable visualTable;
	

	private Rectangle2D dataContainer = new Rectangle2D.Double();
    private Rectangle2D xAxisLabelsContainer = new Rectangle2D.Double();
    private Rectangle2D yAxisLabelsContainer = new Rectangle2D.Double();

    
    private AndPredicate filter = new AndPredicate();
    private JSearchPanel authorsSearchPanel;
    private SearchQueryBinding authorsListQueryBinding;
    
    private RangeQueryBinding xAxisQueryBinding;
    private AxisLayout xAxis;
    private AxisLabelLayout xLabels;
    private RangeQueryBinding yAxisQueryBinding;
    private AxisLayout yAxis;
    private AxisLabelLayout yLabels;
    private JRangeSlider verticalSlider;
    private JRangeSlider horizontalSlider;
    
    
	public ScatterPlotRenderer() throws DatabaseException {
		createDataTable();
	}
	
	
	public JPanel draw() {
		Visualization visualization = new Visualization();
		visualTable = visualization.addTable("commits", dataTable);

		/*
		 * CREATE DATA RENDERER
		 */
		createRenderer(visualization);

		/*
		 * CREATE AXES
		 */
		createAxes(visualization);

		/*
		 * CREATE ACTIONS
		 */
		createActions(visualization);
		
		/*
		 * CREATE PREDICATES
		 */
		createPredicates(visualization);
		
		/*
		 * DISPLAY SETUP
		 */
		setupDisplay(visualization);
		
		/*
		 * CREATE CONTROLS
		 */
		createToolTip(visualization);
		
		
        visualization.run("draw");
        visualization.repaint();
        
        JPanel scatterplotPanel = new JPanel(new BorderLayout());
        scatterplotPanel.add(display, BorderLayout.CENTER);
        scatterplotPanel.add(createHorizontalSlider(), BorderLayout.SOUTH);
        scatterplotPanel.add(createVerticalSlider(), BorderLayout.EAST);
		return scatterplotPanel;
	}


	private void createRenderer(Visualization visualization) {
        visualization.setRendererFactory(new RendererFactory() {
            AbstractShapeRenderer shapeRenderer = new ShapeRenderer(10);
            Renderer yAxisRenderer = new AxisRenderer(Constants.RIGHT, Constants.TOP);
            Renderer xAxisRenderer = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);
            
            public Renderer getRenderer(VisualItem item) {
                return item.isInGroup("xAxis") ? xAxisRenderer :
                       item.isInGroup("yAxis") ? yAxisRenderer : shapeRenderer;
            }
        });
	}
	
	
	//TODO: TESTAR E AJEITAR AQUI!
	private void createAxes(Visualization visualization) {
	
		ActionList xAxisActions = new ActionList();
		
		xAxisQueryBinding = new RangeQueryBinding(visualTable, "Commit Sequence");
		xAxis = new AxisLayout("commits", "Revision Number", Constants.X_AXIS, VisiblePredicate.TRUE);
		xAxis.setLayoutBounds(dataContainer);
		xLabels = new AxisLabelLayout("xAxis", xAxis, xAxisLabelsContainer);
		xAxis.setRangeModel(xAxisQueryBinding.getModel());
		xLabels.setRangeModel(xAxisQueryBinding.getModel());
		
		xAxisActions.add(xAxis);
		xAxisActions.add(xLabels);
		
		ActionList yAxisActions = new ActionList();
		
		yAxisQueryBinding = new RangeQueryBinding(visualTable, "Density");
		yAxis = new AxisLayout("commits", "Density", Constants.Y_AXIS, VisiblePredicate.TRUE);
		yAxis.setLayoutBounds(dataContainer);
		yLabels = new AxisLabelLayout("yAxis", yAxis, yAxisLabelsContainer);
		yAxis.setRangeModel(yAxisQueryBinding.getModel());
		yLabels.setRangeModel(yAxisQueryBinding.getModel());
		
		yAxisActions.add(yAxis);
		yAxisActions.add(yLabels);
		
		
		visualization.putAction("xAxisActions", xAxisActions);
		visualization.putAction("yAxisActions", yAxisActions);
	}
	
	private void createActions(Visualization visualization) {
        DataColorAction color = new DataColorAction("commits", "AuthorID",
                Constants.ORDINAL, VisualItem.STROKECOLOR, new int[]{0,0,0});
		
        ColorAction fill = new DataColorAction("commits", "AuthorID",
                Constants.ORDINAL, VisualItem.FILLCOLOR, ColorPalette.getAuthorsColorPalette());
        
        ActionList draw = new ActionList();
        draw.add(color);
        draw.add(fill);
        draw.add(visualization.getAction("xAxisActions"));
        draw.add(visualization.getAction("yAxisActions"));
		visualization.putAction("draw", draw);
		
		
		ActionList update = new ActionList();
		update.add(new VisibilityFilter("commits", filter));
		update.add(visualization.getAction("xAxisActions"));
		update.add(visualization.getAction("yAxisActions"));
		update.add(new RepaintAction());
		visualization.putAction("update", update);
	}
	
	private void createToolTip(Visualization visualization) {
		
		ToolTipControl toolTipController = new ToolTipControl();
		display.addControlListener(toolTipController);
		
	}
	
	private void setupDisplay(final Visualization visualization) {
		
		display = new Display(visualization);
		display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		display.setSize(700,450);
        display.setHighQuality(true);
        
		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Insets i = display.getInsets();
				int w = display.getWidth();
				int h = display.getHeight();
				int iw = i.left+i.right;
				int ih = i.top+i.bottom;
				int aw = 85;
				int ah = 15;

				dataContainer.setRect(i.left, i.top, w-iw-aw, h-ih-ah);
				xAxisLabelsContainer.setRect(i.left, h-ah-i.bottom, w-iw-aw, ah-10);
				yAxisLabelsContainer.setRect(i.left, i.top, w-iw, h-ih-ah);
				
				visualization.run("update");
			}
		});
        
		Insets i = display.getInsets();
		int w = display.getWidth();
		int h = display.getHeight();
		int iw = i.left+i.right;
		int ih = i.top+i.bottom;
		int aw = 85;
		int ah = 15;

		
		dataContainer.setRect(i.left, i.top, w-iw-aw, h-ih-ah);
		xAxisLabelsContainer.setRect(i.left, h-ah-i.bottom, w-iw-aw, ah-10);
		yAxisLabelsContainer.setRect(i.left, i.top, w-iw, h-ih-ah);
	}
	
	private Component createHorizontalSlider() {
        int maxValue = Visualizer.getAnalysisInfoBar().getSliderControl().getMaximum();
        int minValue = Visualizer.getAnalysisInfoBar().getSliderControl().getMinimum();
		
        xAxisQueryBinding.getNumberModel().setValueRange(minValue, maxValue, minValue, maxValue);
		        
		horizontalSlider = xAxisQueryBinding.createRangeSlider(JRangeSlider.HORIZONTAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
//		horizontalSlider.setMaximum(maxCommit);
//		horizontalSlider.setMinimum(minCommit);
		
//		horizontalSlider.setRange(minCommit, maxCommit);
		horizontalSlider.setMinExtent(1);
//		horizontalSlider.getModel().setMaximum(maxCommit);
//		horizontalSlider.getModel().setMinimum(minCommit);
        
        horizontalSlider.setThumbColor(null);
        horizontalSlider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                display.setHighQuality(false);
            }
            public void mouseReleased(MouseEvent e) {
                display.setHighQuality(true);
                display.repaint();
            }
        });
		
        return horizontalSlider;
	}
	
	private Component createVerticalSlider() {
        verticalSlider = yAxisQueryBinding.createVerticalRangeSlider();
        verticalSlider.setThumbColor(null);
        verticalSlider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                display.setHighQuality(false);
            }
            public void mouseReleased(MouseEvent e) {
                display.setHighQuality(true);
                display.repaint();
            }
        });
		
        return verticalSlider;
	}

	private void createPredicates(final Visualization visualization) {
		
		UpdateListener lstnr = new UpdateListener() {
			public void update(Object src) {
				visualization.run("update");
			}
		};
		
		authorsListQueryBinding = new SearchQueryBinding(visualTable, "Author");
		authorsSearchPanel = authorsListQueryBinding.createSearchPanel();
		authorsSearchPanel.setVisible(false);
		
		filter.add(xAxisQueryBinding.getPredicate());
		filter.add(yAxisQueryBinding.getPredicate());
		filter.add(authorsListQueryBinding.getPredicate());
		filter.addExpressionListener(lstnr);
	}
	
	private void createDataTable() throws DatabaseException{
		createDataTableFields();
		addDataTableData();
	}

	private void createDataTableFields() {
		dataTable = new Table();
		
		dataTable.addColumn("Author", String.class);
		dataTable.addColumn("AuthorID", long.class);
		dataTable.addColumn("Commit Sequence", long.class);
		dataTable.addColumn("Revision Number", int.class);
		dataTable.addColumn("Author Sequence Number", int.class);
		dataTable.addColumn("Comment", String.class);
		dataTable.addColumn("Density", double.class);
		dataTable.addColumn("Cluster Coefficient", double.class);
		dataTable.addColumn("Files", String.class);
		dataTable.addColumn("Added Files", int.class);
		dataTable.addColumn("Modified Files", int.class);
		dataTable.addColumn("Deleted Files", int.class);
		dataTable.addColumn("Entry Lines of Code", int.class);
	}
	
//	private void addDataTableData() {
//		for (long i = new AnalysisDAO().findById(Analysis.class, 1L).getFirstCommit(); i < new AnalysisDAO().findById(Analysis.class, 1L).getLastCommit(); i++) {
//			ProjectMetricValues metric = new ProjectMetricsDAO().findProjectMetricValuesByRevision(new AnalysisDAO().findById(Analysis.class, 1L), i);
//			if(metric != null){
//				dataTable.addRow();
//				dataTable.set(dataTable.getRowCount()-1, "Density", metric.getDensity());
//				dataTable.set(dataTable.getRowCount()-1, "Revision", (int) i);
//				dataTable.set(dataTable.getRowCount()-1, "Author", new EntryDAO().findEntryFromRevision(new AnalysisDAO().findById(Analysis.class, 1L).getProject(), metric.getRevision()).getAuthor().getName());
//			}
//		}
//	}

	private void addDataTableData() throws DatabaseException {

		ProjectMetricModel[] availableProjectMetrics = (ProjectMetricModel[]) Visualizer.getAvailableProjectMetrics();
		EntryMetricModel[] availableEntryMetrics = (EntryMetricModel[]) Visualizer.getAvailableEntryMetrics();
//		FileMetricModel[] availableFileMetrics = (FileMetricModel[]) Visualizer.getAvailableFileMetrics();
		
		/* 
		 * ###################################
		 * INITIATING METRICS REFERENCE VALUES
		 * ###################################
		 */
		
		double[] higherProjectMetricValues = new double[availableProjectMetrics.length];
		double[] higherEntryMetricValues = new double[availableEntryMetrics.length];
//		double[] higherFileMetricValues = new double[availableFileMetrics.length];
		
		List<Entry> entries = new EntryDAO().getAllEntriesWithinEntries(AbstractVisualization.getCurrentAnalysis().getFirstEntry(), AbstractVisualization.getCurrentAnalysis().getLastEntry());
		
		for (int i = 0; i < entries.size(); i++) {
			
			final Entry entry = entries.get(i);

			dataTable.addRow();
			dataTable.set(dataTable.getRowCount()-1, "Revision Number", entry.getRevision());
			dataTable.set(dataTable.getRowCount()-1, "Commit Sequence", (i+1));
			dataTable.set(dataTable.getRowCount()-1, "Author", entry.getAuthor().getName());
			dataTable.set(dataTable.getRowCount()-1, "AuthorID", entry.getAuthor().getId());
			dataTable.set(dataTable.getRowCount()-1, "Comment", entry.getComment());
			dataTable.set(dataTable.getRowCount()-1, "Author Sequence Number", new EntryDAO().getEntrySequenceNumber(entry));
			dataTable.set(dataTable.getRowCount()-1, "Files", entry.getListOfEntryFiles());

			if(availableProjectMetrics.length > 0){
				ProjectMetricValues projectMetricsValues = new ProjectMetricsDAO().findProjectMetricValuesByEntry(AbstractVisualization.getCurrentAnalysis(), entry);
				if(projectMetricsValues != null){
					for (int j = 0; j < availableProjectMetrics.length; j++) {
						double metricValue = projectMetricsValues.getValueByName(availableProjectMetrics[j].getMetricName());
						dataTable.set(dataTable.getRowCount()-1, availableProjectMetrics[j].getMetricName(), metricValue);
						higherProjectMetricValues[j] = Math.max(higherProjectMetricValues[j], metricValue);
					}
				}
			}

			if(availableEntryMetrics.length > 0){
				EntryMetricValues entryMetricsValues = new EntryMetricsDAO().findEntryMetricValuesByEntry(AbstractVisualization.getCurrentAnalysis(), entry);
				if(entryMetricsValues != null){
					for (int j = 0; j < availableEntryMetrics.length; j++) {
						double metricValue = entryMetricsValues.getValueByName(availableEntryMetrics[j].getMetricName());
						dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), metricValue);
						higherEntryMetricValues[j] = Math.max(higherEntryMetricValues[j], metricValue);
					}
				}
			}

//			if(availableFileMetrics.length > 0){
//				FileMetricValues fileMetricsValues = new FileMetricsDAO().findFileMetricValuesByRevision(currentAnalysis, i);
//				if(fileMetricsValues != null){
//					for (int j = 0; j < availableEntryMetrics.length; j++) {
//						if(fileMetricsValues != null){
//							dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), entryMetricsValues.getValueByName(availableEntryMetrics[j].getMetricName()));
//
//						}
//					}
//				}
//			}

			dataTable.addRow();
			dataTable.set(dataTable.getRowCount()-1, "Author", "Reference");
			dataTable.set(dataTable.getRowCount()-1, "AuthorID", -1);
			for (int j = 0; j < higherProjectMetricValues.length; j++) {
				dataTable.set(dataTable.getRowCount()-1, availableProjectMetrics[j].getMetricName(), higherProjectMetricValues[j]);
			}
			for (int j = 0; j < higherEntryMetricValues.length; j++) {
				dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), higherEntryMetricValues[j]);
			}
		}
	}

	
	public void updateYAxis(String newMetric){
		this.yAxis.setDataField(newMetric);
		this.yAxisQueryBinding = new RangeQueryBinding(this.visualTable, newMetric);

		this.verticalSlider = new JRangeSlider(yAxisQueryBinding.getModel(), JRangeSlider.VERTICAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
		yAxis.setRangeModel(yAxisQueryBinding.getModel());
		yLabels.setRangeModel(yAxisQueryBinding.getModel());
		
		this.display.getVisualization().run("update");
	}
	
	
	public void updateXAxis(String newScale){
		this.xAxis.setDataField(newScale);
		this.xAxisQueryBinding = new RangeQueryBinding(this.visualTable, newScale);

		this.verticalSlider = new JRangeSlider(xAxisQueryBinding.getModel(), JRangeSlider.VERTICAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
		xAxis.setRangeModel(xAxisQueryBinding.getModel());
		xLabels.setRangeModel(xAxisQueryBinding.getModel());
		
		this.display.getVisualization().run("update");
	}
	
	public Display getDisplay() {
		return display;
	}

	public RangeQueryBinding getxAxisQueryBinding() {
		return xAxisQueryBinding;
	}


	public JRangeSlider getVerticalSlider() {
		return verticalSlider;
	}


	public JRangeSlider getHorizontalSlider() {
		return horizontalSlider;
	}

	public JSearchPanel getAuthorsSearchPanel() {
		return authorsSearchPanel;
	}

}