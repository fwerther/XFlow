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
 *  =============
 *  LineView.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.line;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.line.controls.LineChartZoomer;
import br.ufpa.linc.xflow.presentation.visualizations.line.controls.LineViewAxesUpdater;
import br.ufpa.linc.xflow.presentation.visualizations.line.controls.RangeMarkersControl;

public class LineView extends AbstractVisualization {

	private LineChartRenderer lineChartRenderer;
	
	//Controls
	private LineChartZoomer zoomer;
	private LineViewAxesUpdater metricPicker;
	private RangeMarkersControl rangeMarkersControls;
	
	@Override
	public JPanel composeVisualizationPanel() throws DatabaseException {

		lineChartRenderer = new LineChartRenderer();
		zoomer = new LineChartZoomer(Visualizer.getAnalysisInfoBar().getSliderControl().getMinimum(), Visualizer.getAnalysisInfoBar().getSliderControl().getMaximum());
		metricPicker = new LineViewAxesUpdater();
		rangeMarkersControls = new RangeMarkersControl();
		
		JPanel lineViewPanel = new JPanel(new BorderLayout());
		
		JSplitPane chartPanel = setupChartPanel();
		lineViewPanel.add(chartPanel, BorderLayout.CENTER);
		lineViewPanel.add(zoomer.getControlComponent(), BorderLayout.SOUTH);
		
		return lineViewPanel;
	}

	private JSplitPane setupChartPanel() {
		final JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(7);
		
		final JPanel lineChartPanel = lineChartRenderer.draw();
		
		final JPanel lineChartControls = new JPanel();
		lineChartControls.setBorder(BorderFactory.createTitledBorder("Controls"));
		lineChartControls.add(rangeMarkersControls.getControlComponent());
		lineChartControls.setMaximumSize(lineChartControls.getPreferredSize());
		lineChartControls.setMinimumSize(lineChartControls.getPreferredSize());
		
		lineChartPanel.setMinimumSize(new Dimension((int) (914 - lineChartControls.getPreferredSize().getWidth()), 754));
		
		JPanel leftComponent = new JPanel(new BorderLayout());
		leftComponent.add(lineChartPanel, BorderLayout.CENTER);
		leftComponent.add(metricPicker.getControlComponent(), BorderLayout.NORTH);
		
		splitPane.setLeftComponent(leftComponent);
		splitPane.setRightComponent(lineChartControls);
		splitPane.setDividerLocation(1.0);
		splitPane.addComponentListener(new ComponentListener(){

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentResized(ComponentEvent e) {
				lineChartPanel.setMinimumSize(new Dimension((int) (splitPane.getSize().getWidth() - lineChartControls.getPreferredSize().getWidth()), splitPane.getHeight()));
				lineChartPanel.setPreferredSize(new Dimension((int) (splitPane.getSize().getWidth() - lineChartControls.getPreferredSize().getWidth()), splitPane.getHeight()));
				splitPane.setDividerLocation(splitPane.getDividerLocation() < splitPane.getSize().getWidth() - lineChartControls.getPreferredSize().getWidth() ? (int) (splitPane.getSize().getWidth() - lineChartControls.getPreferredSize().getWidth()) : splitPane.getDividerLocation());
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		return splitPane;
	}

	public LineChartRenderer getLineChartRenderer() {
		return lineChartRenderer;
	}

	public LineChartZoomer getZoomer() {
		return zoomer;
	}

	public LineViewAxesUpdater getMetricPicker() {
		return metricPicker;
	}

	public RangeMarkersControl getRangeMarkersControls() {
		return rangeMarkersControls;
	}
	
	
}
