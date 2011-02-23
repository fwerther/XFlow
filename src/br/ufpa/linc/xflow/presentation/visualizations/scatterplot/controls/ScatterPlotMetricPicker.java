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
 *  ============================
 *  ScatterPlotMetricPicker.java
 *  ============================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.presentation.Visualizer;

public class ScatterPlotMetricPicker implements ScatterPlotViewController, ActionListener {

	private JComboBox metricPicker;
	
	@Override
	public JComponent getControlComponent() {
		JPanel metricPickerPanel = new JPanel();
		
		metricPicker = createMetricsComboBox();
		metricPicker.addActionListener(this);

		JLabel chooseMetricLabel = new JLabel("Selected metric (Y Axis):");
		metricPickerPanel.add(chooseMetricLabel);
		metricPickerPanel.add(metricPicker);
		metricPickerPanel.setOpaque(false);
		return metricPickerPanel;
	}

	private JComboBox createMetricsComboBox() {
		List<String> metricsNames = Visualizer.getAvailableProjectMetricsNames();
		metricsNames.addAll(Visualizer.getAvailableEntryMetricsNames());
		final String[] metricNamesVariations = new String[]{"Higher ", "Average ", "Max "};
		for (String string : Visualizer.getAvailableFileMetricsNames()) {
			for (int i = 0; i < metricNamesVariations.length; i++) {
				metricsNames.add(metricNamesVariations[i]+string);
			}
		}
		
		JComboBox metricPickerComboBox = new JComboBox((Vector<String>) metricsNames);
		return metricPickerComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Visualizer.getScatterPlotView().getScatterPlotRenderer().updateYAxis((String) metricPicker.getSelectedItem());
//		Visualizer.getScatterPlotView().getScatterPlotRenderer().getDisplay().getVisualization().run("update");
	}
	
}
