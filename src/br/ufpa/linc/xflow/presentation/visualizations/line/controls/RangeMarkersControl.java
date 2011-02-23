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
 *  RangeMarkersControl.java
 *  ========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

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
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;
import org.jfree.ui.TextAnchor;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;
import br.ufpa.linc.xflow.metrics.MetricsUtil;
import br.ufpa.linc.xflow.presentation.Visualizer;

public class RangeMarkersControl implements LineViewController, ItemListener {

	private JCheckBox showAverageMarkerCBox;
	private JCheckBox showFirstDevMarkerCBox;
	private JCheckBox showSecDevMarkerCBox;
	
	
	private ValueMarker averageMarker;
	private ValueMarker firstDeviationTop;
	private ValueMarker firstDeviationBot;
	private ValueMarker secondDeviationTop;
	private ValueMarker secondDeviationBot;
	
	
	@Override
	public JComponent getControlComponent() {
		
		showAverageMarkerCBox = new JCheckBox("Show average value");
		showAverageMarkerCBox.setSelected(false);
		showAverageMarkerCBox.addItemListener(this);
		
		showFirstDevMarkerCBox = new JCheckBox("Show first deviation");
		showFirstDevMarkerCBox.setSelected(false);
		showFirstDevMarkerCBox.addItemListener(this);
		
		showSecDevMarkerCBox = new JCheckBox("Show second deviation");
		showSecDevMarkerCBox.setSelected(false);
		showSecDevMarkerCBox.addItemListener(this);
		
		JPanel rangeMarkersPanel = new JPanel();
		rangeMarkersPanel.setLayout(setupLayout(rangeMarkersPanel));
		rangeMarkersPanel.setBorder(BorderFactory.createTitledBorder("Reference values"));
		//rangeMarkersPanel.setPreferredSize(new Dimension(207, 116));
		
		return rangeMarkersPanel; 
	}
	
	private LayoutManager setupLayout(JPanel rangeMarkersPanel) {
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

		XYPlot plotter = Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot();
		
		MetricModel selectedMetric = MetricsUtil.discoverMetricTypeByName(Visualizer.getLineView().getLineChartRenderer().getSelectedMetric());
		double averageValue = selectedMetric.getAverageValue(Visualizer.getMetricsSession());

		averageMarker = new ValueMarker(averageValue);
		averageMarker.setPaint(Color.black);
		averageMarker.setLabel("average");
		averageMarker.setLabelFont(new Font("Helvetica", Font.BOLD, 10));
		averageMarker.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		averageMarker.setStroke(new BasicStroke((float)1));
		plotter.addRangeMarker(0, averageMarker, Layer.BACKGROUND);
	}

	public void drawFirstDeviationLineMarkers() throws DatabaseException {

		XYPlot plotter = Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot();
		
		MetricModel selectedMetric = MetricsUtil.discoverMetricTypeByName(Visualizer.getLineView().getLineChartRenderer().getSelectedMetric());
		double averageValue = selectedMetric.getAverageValue(Visualizer.getMetricsSession());
		double deviationValue = selectedMetric.getStdDevValue(Visualizer.getMetricsSession());
		
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

		XYPlot plotter = Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot();
		
		MetricModel selectedMetric = MetricsUtil.discoverMetricTypeByName(Visualizer.getLineView().getLineChartRenderer().getSelectedMetric());
		double averageValue = selectedMetric.getAverageValue(Visualizer.getMetricsSession());
		double deviationValue = selectedMetric.getStdDevValue(Visualizer.getMetricsSession());
		
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
		XYPlot plotter = Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot();
		plotter.removeRangeMarker(0, this.averageMarker, Layer.BACKGROUND);
	}

	private void hideFirstDeviationLineMarkers() {
		XYPlot plotter = Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot();
		plotter.removeRangeMarker(0, this.firstDeviationTop, Layer.BACKGROUND);
		plotter.removeRangeMarker(0, this.firstDeviationBot, Layer.BACKGROUND);
	}

	private void hideSecondDeviationLineMarkers() {
		XYPlot plotter = Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if(source == showFirstDevMarkerCBox){
				try {
					drawFirstDeviationLineMarkers();
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if(source == showSecDevMarkerCBox){
				try {
					drawSecondDeviationLineMarkers();
				} catch (DatabaseException e) {
					// TODO Auto-generated catch block
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
