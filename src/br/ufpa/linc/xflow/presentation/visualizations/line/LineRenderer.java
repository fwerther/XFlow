package br.ufpa.linc.xflow.presentation.visualizations.line;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;
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

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;
import br.ufpa.linc.xflow.metrics.MetricValuesTable;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.presentation.view.ProjectViewer;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;

public class LineRenderer implements VisualizationRenderer<LineVisualization> {

	private JFreeChart chart;
	private XYSeriesCollection numericDataset;
//	private TimePeriodValuesCollection temporalDataset;
	
	private HashMap<String, Integer> seriesMap;
	
	private String selectedMetric;
	
	private Metrics metricsSession;

	@Override
	public void composeVisualization(JComponent visualizationComponent) throws DatabaseException {
		this.metricsSession = (Metrics) visualizationComponent.getClientProperty("Metrics Session");
		createDatasets();
		JPanel lineChartPanel = this.draw();
		lineChartPanel.setVisible(true);
		visualizationComponent.add(lineChartPanel, BorderLayout.CENTER);
	}

	public JPanel draw() {
		if(ProjectViewer.getProjectMetrics().length > 0){
			selectedMetric = "Revision "+ProjectViewer.getProjectMetrics()[0].getMetricName();
		}
		else if(ProjectViewer.getEntryMetrics().length > 0){
			selectedMetric = "Revision "+ProjectViewer.getEntryMetrics()[0].getMetricName();
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

	private void setupSeriesRenderer(XYPlot xyplot) {
		XYLineAndShapeRenderer lineAndShapeRenderer = (XYLineAndShapeRenderer)xyplot.getRenderer();

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
		if(selectedMetric.contains("Revision")){
			integerDomainAxis.setLabel("Revisions");
		} else {
			integerDomainAxis.setLabel("Sequence");
		}
		return integerDomainAxis;
	}

	private void createDatasets() throws DatabaseException{
		this.numericDataset = new XYSeriesCollection();
		this.seriesMap = new HashMap<String, Integer>();

		/*
		 * COLLECT PROJECT METRICS VALUES AND POPULATE SERIES.
		 */
		ProjectMetricModel[] projectMetrics = ProjectViewer.getProjectMetrics();
		for (MetricModel metric : projectMetrics) {

			seriesMap.put("Sequential "+metric.getMetricName(), seriesMap.size());			
			seriesMap.put("Revision "+metric.getMetricName(), seriesMap.size());
			ArrayList<? extends MetricValuesTable> metricValuesTable = metric.getAllMetricsTables(this.metricsSession);

			final XYSeries sequentialDataset = new XYSeries("Sequential "+metric.getMetricName());
			final XYSeries revisionsDataset = new XYSeries("Revision "+metric.getMetricName());
			for (int i = 0; i < metricValuesTable.size(); i++) {
				sequentialDataset.add(i, metric.getMetricValue(this.metricsSession, metricValuesTable.get(i).getEntry()));
				revisionsDataset.add(metricValuesTable.get(i).getEntry().getRevision(), metric.getMetricValue(this.metricsSession, metricValuesTable.get(i).getEntry()));
			}
			this.numericDataset.addSeries(sequentialDataset);
			this.numericDataset.addSeries(revisionsDataset);
		}
		
		/*
		 * COLLECT ENTRY METRICS VALUES AND POPULATE SERIES.
		 */
		EntryMetricModel[] entryMetrics = ProjectViewer.getEntryMetrics();
		for (MetricModel metric : entryMetrics) {

			seriesMap.put("Sequential "+metric.getMetricName(), seriesMap.size());
			seriesMap.put("Revision "+metric.getMetricName(), seriesMap.size());
			ArrayList<? extends MetricValuesTable> metricValuesTable = metric.getAllMetricsTables(this.metricsSession);

			final XYSeries sequentialDataset = new XYSeries("Sequential "+metric.getMetricName());
			final XYSeries revisionsDataset = new XYSeries("Revision "+metric.getMetricName());
			for (int i = 0; i < metricValuesTable.size(); i++) {
				sequentialDataset.add(i, metric.getMetricValue(this.metricsSession, metricValuesTable.get(i).getEntry()));
				revisionsDataset.add(metricValuesTable.get(i).getEntry().getRevision(), metric.getMetricValue(this.metricsSession, metricValuesTable.get(i).getEntry()));
			}
			this.numericDataset.addSeries(sequentialDataset);
			this.numericDataset.addSeries(revisionsDataset);
		}
	}
	
	public void updateYAxis(String newMetric){
		int currentSerie = this.seriesMap.get(selectedMetric);
		int newSerie = this.seriesMap.get(newMetric);
		
		this.getChart().getXYPlot().getRenderer().setSeriesVisible(currentSerie, new Boolean(false));
		this.getChart().getXYPlot().getRenderer().setSeriesVisible(newSerie, new Boolean(true));
		
		final String seriesLabel = selectedMetric.substring(selectedMetric.indexOf(" "));
		this.getChart().getXYPlot().getRangeAxis().setLabel(seriesLabel);
		
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

	@Override
	public void setLowerQuality() {
		//Do nothing.
		
	}

	@Override
	public void setHighQuality() {
		//Do nothing.
		
	}

	@Override
	public void updateVisualizationLimits(int inferiorLimit, int superiorLimit) throws DatabaseException {
		
	}
}
