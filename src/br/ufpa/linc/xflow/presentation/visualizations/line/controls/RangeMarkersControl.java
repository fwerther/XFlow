package br.ufpa.linc.xflow.presentation.visualizations.line.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;
import org.jfree.ui.TextAnchor;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;
import br.ufpa.linc.xflow.metrics.MetricsUtil;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.line.LineRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.line.LineVisualization;

public class RangeMarkersControl extends JComponent implements VisualizationControl<LineVisualization>, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1710072879759208305L;
	
	private JCheckBox showAverageMarkerCBox;
	private JCheckBox showFirstDevMarkerCBox;
	private JCheckBox showSecDevMarkerCBox;
	
	
	private ValueMarker averageMarker;
	private ValueMarker firstDeviationTop;
	private ValueMarker firstDeviationBot;
	private ValueMarker secondDeviationTop;
	private ValueMarker secondDeviationBot;
	
	private LineVisualization visualizationControlled;
	
	@Override
	public void buildControlGUI(JComponent visualizationComponent) {
		
		showAverageMarkerCBox = new JCheckBox("Show average value");
		showAverageMarkerCBox.setSelected(false);
		showAverageMarkerCBox.addItemListener(this);
		
		showFirstDevMarkerCBox = new JCheckBox("Show first deviation");
		showFirstDevMarkerCBox.setSelected(false);
		showFirstDevMarkerCBox.addItemListener(this);
		
		showSecDevMarkerCBox = new JCheckBox("Show second deviation");
		showSecDevMarkerCBox.setSelected(false);
		showSecDevMarkerCBox.addItemListener(this);
		
		this.setLayout(setupLayout(this));
		this.setBorder(BorderFactory.createTitledBorder("Reference values"));
//		rangeMarkersPanel.setPreferredSize(new Dimension(207, 116));
		
		visualizationComponent.add(this);
		this.visualizationControlled = (LineVisualization) ((JComponent) visualizationComponent.getParent()).getClientProperty("Visualization Instance");
	}
	private LayoutManager setupLayout(JComponent rangeMarkersPanel) {
		GroupLayout layout = new GroupLayout(rangeMarkersPanel);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addGroup(layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(layout.createParallelGroup(Alignment.LEADING)
							.addComponent(showAverageMarkerCBox)
							.addComponent(showFirstDevMarkerCBox)
							.addComponent(showSecDevMarkerCBox)
					)
					.addContainerGap(24, Short.MAX_VALUE)
				)
		);

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(showAverageMarkerCBox)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(showFirstDevMarkerCBox)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addComponent(showSecDevMarkerCBox)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		
		return layout;
	}

	public void drawAverageLineMarker() throws DatabaseException {

		XYPlot plotter = ((LineRenderer) visualizationControlled.getRenderers()[0]).getChart().getXYPlot();
		
		MetricModel selectedMetric = MetricsUtil.discoverMetricTypeByName(((LineRenderer) visualizationControlled.getRenderers()[0]).getSelectedMetric());
		double averageValue = selectedMetric.getAverageValue((Metrics) ((JComponent) this.getParent()).getClientProperty("Metrics Session"));

		averageMarker = new ValueMarker(averageValue);
		averageMarker.setPaint(Color.black);
		averageMarker.setLabel("average");
		averageMarker.setLabelFont(new Font("Helvetica", Font.BOLD, 10));
		averageMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		averageMarker.setStroke(new BasicStroke((float)1));
		plotter.addRangeMarker(0, averageMarker, Layer.BACKGROUND);
	}

	public void drawFirstDeviationLineMarkers() throws DatabaseException {

		final Metrics metricsSession = (Metrics) ((JComponent) this.getParent()).getClientProperty("Metrics Session");
		final LineRenderer lineRenderer = ((LineRenderer) visualizationControlled.getRenderers()[0]);
		XYPlot plotter = lineRenderer.getChart().getXYPlot();
		
		MetricModel selectedMetric = MetricsUtil.discoverMetricTypeByName(lineRenderer.getSelectedMetric());
		double averageValue = selectedMetric.getAverageValue(metricsSession);
		double deviationValue = selectedMetric.getStdDevValue(metricsSession);
		
		firstDeviationTop = new ValueMarker(averageValue + deviationValue);
		firstDeviationTop.setPaint(Color.RED);
		firstDeviationTop.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1F, new float[] {4, 4}, 0));
		firstDeviationTop.setLabel("first deviation");
		firstDeviationTop.setLabelFont(new Font("Helvetica", Font.BOLD, 10));
		firstDeviationTop.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		plotter.addRangeMarker(0, firstDeviationTop, Layer.BACKGROUND);

		firstDeviationBot = new ValueMarker(averageValue - deviationValue);
		firstDeviationBot.setPaint(Color.BLUE);
		firstDeviationBot.setStroke(new BasicStroke(BasicStroke.CAP_BUTT, 1, BasicStroke.JOIN_BEVEL, 1F, new float[] {4, 4}, 0));
		firstDeviationBot.setLabel("first deviation");
		firstDeviationBot.setLabelFont(new Font("Helvetica", Font.BOLD, 10));
		firstDeviationBot.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		plotter.addRangeMarker(0, firstDeviationBot, Layer.BACKGROUND);
	}

	public void drawSecondDeviationLineMarkers() throws DatabaseException {

		final Metrics metricsSession = (Metrics) ((JComponent) this.getParent()).getClientProperty("Metrics Session");
		final LineRenderer lineRenderer = ((LineRenderer) visualizationControlled.getRenderers()[0]);
		
		XYPlot plotter = lineRenderer.getChart().getXYPlot();
		
		MetricModel selectedMetric = MetricsUtil.discoverMetricTypeByName(lineRenderer.getSelectedMetric());
		double averageValue = selectedMetric.getAverageValue(metricsSession);
		double deviationValue = selectedMetric.getStdDevValue(metricsSession);
		
		secondDeviationTop = new ValueMarker(averageValue + (deviationValue * 2));
		secondDeviationTop.setPaint(Color.YELLOW);
		secondDeviationTop.setStroke(new BasicStroke(BasicStroke.CAP_BUTT, 1, BasicStroke.JOIN_BEVEL, 1F, new float[] {4, 4}, 0));
		secondDeviationTop.setLabelFont(new Font("Helvetica", Font.BOLD, 10));
		secondDeviationTop.setLabel("second deviation");
		secondDeviationTop.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);

		secondDeviationBot = new ValueMarker(averageValue - (deviationValue * 2));
		secondDeviationBot.setPaint(Color.BLACK);
		secondDeviationBot.setStroke(new BasicStroke(BasicStroke.CAP_BUTT, 1, BasicStroke.JOIN_BEVEL, 1F, new float[] {4, 4}, 0));
		secondDeviationBot.setLabelFont(new Font("Helvetica", Font.BOLD, 10));
		secondDeviationBot.setLabel("second deviation");
		secondDeviationBot.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);

		plotter.addRangeMarker(0, secondDeviationBot, Layer.BACKGROUND);
		plotter.addRangeMarker(0, secondDeviationTop, Layer.BACKGROUND);
	}
	

	private void hideAverageLineMarker() {
		XYPlot plotter = ((LineRenderer) visualizationControlled.getRenderers()[0]).getChart().getXYPlot();
		plotter.removeRangeMarker(0, this.averageMarker, Layer.BACKGROUND);
	}

	private void hideFirstDeviationLineMarkers() {
		XYPlot plotter = ((LineRenderer) visualizationControlled.getRenderers()[0]).getChart().getXYPlot();
		plotter.removeRangeMarker(0, this.firstDeviationTop, Layer.BACKGROUND);
		plotter.removeRangeMarker(0, this.firstDeviationBot, Layer.BACKGROUND);
	}

	private void hideSecondDeviationLineMarkers() {
		XYPlot plotter = ((LineRenderer) visualizationControlled.getRenderers()[0]).getChart().getXYPlot();
		plotter.removeRangeMarker(0, this.secondDeviationTop, Layer.BACKGROUND);
		plotter.removeRangeMarker(0, this.secondDeviationBot, Layer.BACKGROUND);
	}

	public void itemStateChanged(ItemEvent checkBox) {
		Object source = checkBox.getItemSelectable();

		switch (checkBox.getStateChange()) {
		case ItemEvent.SELECTED:

			if(source == showAverageMarkerCBox){
				try {
					drawAverageLineMarker();
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
			}

			else if(source == showFirstDevMarkerCBox){
				try {
					drawFirstDeviationLineMarkers();
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
			}

			else if(source == showSecDevMarkerCBox){
				try {
					drawSecondDeviationLineMarkers();
				} catch (DatabaseException e) {
					e.printStackTrace();
				}
			}

			break;

		case ItemEvent.DESELECTED:

			if(source == showAverageMarkerCBox){
				hideAverageLineMarker();
			}

			else if(source == showFirstDevMarkerCBox){
				hideFirstDeviationLineMarkers();
			}

			else if(source == showSecDevMarkerCBox){
				hideSecondDeviationLineMarkers();
			}

			break;
		}
	}

}
