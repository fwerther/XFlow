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
 *  ======================
 *  LineChartRenderer.java
 *  ======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.line;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;
import br.ufpa.linc.xflow.metrics.MetricValuesTable;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class LineChartRenderer {

	private JFreeChart chart;
	private XYSeriesCollection numericDataset;
	private HashMap<String, Integer> seriesMap;
	
	private String selectedMetric;

	public LineChartRenderer() throws DatabaseException {
		createDatasets();
	}

	public JPanel draw() {
		if(Visualizer.getAvailableProjectMetrics().length > 0){
			selectedMetric = Visualizer.getAvailableProjectMetrics()[0].getMetricName();
		}
		else if(Visualizer.getAvailableEntryMetrics().length > 0){
			selectedMetric = Visualizer.getAvailableEntryMetrics()[0].getMetricName();
		}

		JPanel numericChartPanel = createNumericChart();
		return numericChartPanel;
	}

	private JPanel createNumericChart() {
		XYDataset plottedData = numericDataset;
		chart = ChartFactory.createXYLineChart("", null, "", plottedData, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyplot = (XYPlot) chart.getPlot();

		/*
		 * SETUP BACKGROUND AND GRID COLORS.
		 */
		setupGeneralChartAppearence(xyplot);

		
		/*
		 * SETUP SERIES APPEARENCE.
		 * (e.g. line shape and color)
		 */
		setupSeriesRenderer(xyplot);

		
		/*
		 * SETUP AXES. 
		 */
		setupAxes(xyplot);


		return new ChartPanel(chart);	
	}
	
	private void setupGeneralChartAppearence(XYPlot xyplot) {
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.black);
		xyplot.setRangeGridlinePaint(Color.black);
	}

	private void setupSeriesRenderer(XYPlot xyplot) {		XYLineAndShapeRenderer lineAndShapeRenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();

		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			lineAndShapeRenderer.setSeriesPaint(i, Color.red);
			lineAndShapeRenderer.setSeriesStroke(i, new BasicStroke((float)1));
			lineAndShapeRenderer.setSeriesFillPaint(i, Color.white);
			lineAndShapeRenderer.setSeriesOutlinePaint(i, Color.black);
			lineAndShapeRenderer.setUseOutlinePaint(true);
			lineAndShapeRenderer.setUseFillPaint(true);
			
			xyplot.getRenderer().setSeriesVisible(i, new Boolean(false));
		}
		
		xyplot.getRenderer().setSeriesVisible(0, new Boolean(true));
	}
	
	private void setupAxes(XYPlot xyplot) {
		xyplot.setDomainAxis(0, createNumericDomainAxis());
		xyplot.setRangeAxis(0, createRangeAxis());

		List<Integer> localList = Arrays.asList(new Integer[] { new Integer(0), new Integer(1) });
		xyplot.mapDatasetToDomainAxes(0, localList);
		xyplot.mapDatasetToRangeAxes(0, localList);
	}

	private ValueAxis createRangeAxis() {
		NumberAxis rangeAxis = new NumberAxis(null);
		rangeAxis.setLowerMargin(0.05);
		rangeAxis.setUpperMargin(0.05);
//		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//		rangeAxis.setLabelFont(new Font("Helvetica", Font.BOLD, 15));
//		rangeAxis.setLabelInsets(new RectangleInsets(20,20,20,20));
		rangeAxis.setLabel("Density");
		return rangeAxis;
	}

	private ValueAxis createNumericDomainAxis(){
		NumberAxis integerDomainAxis = new NumberAxis(null);
		integerDomainAxis.setLowerMargin(0.05);
		integerDomainAxis.setUpperMargin(0.05);
		integerDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//		rangeAxis.setLabelFont(new Font("Helvetica", Font.BOLD, 15));
//		rangeAxis.setLabelInsets(new RectangleInsets(20,20,20,20));
		integerDomainAxis.setLabel("Revisions");
		return integerDomainAxis;
	}

	private void createDatasets() throws DatabaseException{

		Analysis currentAnalysis = AbstractVisualization.getCurrentAnalysis();
		this.numericDataset = new XYSeriesCollection();
		this.seriesMap = new HashMap<String, Integer>();

		/*
		 * COLLECT PROJECT METRICS VALUES AND POPULATES SERIES.
		 */
		ProjectMetricModel[] projectMetrics = Visualizer.getAvailableProjectMetrics();
		for (MetricModel metric : projectMetrics) {

			seriesMap.put(metric.getMetricName(), seriesMap.size());			
			ArrayList<? extends MetricValuesTable> metricValuesTable = metric.getAllMetricsTables(currentAnalysis);

			final XYSeries revisionsDataset = new XYSeries(metric.getMetricName(), false);
			for (MetricValuesTable metricValue : metricValuesTable) {
				revisionsDataset.add(metricValue.getEntry().getRevision(), metric.getMetricValue(currentAnalysis, metricValue.getEntry()));
			}
			this.numericDataset.addSeries(revisionsDataset);
		}
		
		/*
		 * COLLECT ENTRY METRICS VALUES AND POPULATES SERIES.
		 */
		EntryMetricModel[] entryMetrics = Visualizer.getAvailableEntryMetrics();
		for (MetricModel metric : entryMetrics) {

			seriesMap.put(metric.getMetricName(), seriesMap.size());			
			ArrayList<? extends MetricValuesTable> metricValuesTable = metric.getAllMetricsTables(currentAnalysis);

			final XYSeries sequentialDataset = new XYSeries(metric.getMetricName(), false);
			for (MetricValuesTable metricValue : metricValuesTable) {
				sequentialDataset.add(metricValue.getEntry().getRevision(), metric.getMetricValue(currentAnalysis, metricValue.getEntry()));
			}
			this.numericDataset.addSeries(sequentialDataset);
		}
		
	}
	
	public void updateYAxis(String newMetric){
		
		int currentSerie = this.seriesMap.get(selectedMetric);
		int newSerie = this.seriesMap.get(newMetric);
		
		this.getChart().getXYPlot().getRenderer().setSeriesVisible(currentSerie, new Boolean(false));
		this.getChart().getXYPlot().getRenderer().setSeriesVisible(newSerie, new Boolean(true));
		this.getChart().getXYPlot().getRangeAxis().setLabel(newMetric);
		
		
		this.selectedMetric = newMetric;
	}

	public JFreeChart getChart() {
		return chart;
	}
	
	public HashMap<String, Integer> getSeriesMap() {
		return seriesMap;
	}

	public String getSelectedMetric() {
		return selectedMetric;
	}
}
