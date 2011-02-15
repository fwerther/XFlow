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
 *  ====================
 *  ScatterPlotView.java
 *  ====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.scatterplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.ReferenceValuesControl;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.ScatterPlotMetricPicker;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.ScatterPlotTimescalePicking;

public class ScatterPlotView extends AbstractVisualization {

	private ScatterPlotRenderer scatterPlotRenderer;
	private ScatterPlotMetricPicker metricPicker;
	private ScatterPlotTimescalePicking scalePicker;
	private ReferenceValuesControl referenceDisplay;
	
	@Override
	public JPanel composeVisualizationPanel() throws DatabaseException {
		this.scatterPlotRenderer = new ScatterPlotRenderer();
		
		JPanel northControlsPanel = new JPanel();
		northControlsPanel.setBackground(Color.white);
		northControlsPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		this.metricPicker = new ScatterPlotMetricPicker();
		this.scalePicker = new ScatterPlotTimescalePicking();
		this.referenceDisplay = new ReferenceValuesControl();
		northControlsPanel.add(metricPicker.getControlComponent());
		northControlsPanel.add(scalePicker.getControlComponent());
		northControlsPanel.add(referenceDisplay.getControlComponent());
		northControlsPanel.setPreferredSize(new Dimension(1, 38));	
		JPanel scatterplotPanel = new JPanel(new BorderLayout());
		scatterplotPanel.add(this.scatterPlotRenderer.draw(), BorderLayout.CENTER);
		scatterplotPanel.add(northControlsPanel, BorderLayout.NORTH);

		return scatterplotPanel;
	}

	public void setScatterPlotRenderer(ScatterPlotRenderer scatterPlotRenderer) {
		this.scatterPlotRenderer = scatterPlotRenderer;
	}

	public ScatterPlotRenderer getScatterPlotRenderer() {
		return scatterPlotRenderer;
	}
	
}
