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
 *  ================================
 *  ScatterPlotTimescalePicking.java
 *  ================================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.presentation.Visualizer;

public class ScatterPlotTimescalePicking implements ScatterPlotViewController, ActionListener {

	private JComboBox timescalePicker;
	
	@Override
	public JComponent getControlComponent() {
		JPanel timescalePickerPanel = new JPanel();
		JLabel timescaleLabel = new JLabel("Temporal scale (X Axis):");
		
		timescalePicker = createMetricsComboBox();
		timescalePicker.addActionListener(this);
		
		timescalePickerPanel.add(timescaleLabel);
		timescalePickerPanel.add(timescalePicker);
		timescalePickerPanel.setBackground(Color.white);
		timescalePickerPanel.setOpaque(false);
		return timescalePickerPanel;
	}

	private JComboBox createMetricsComboBox() {
		String[] options = new String[]{"Revision Number", "Sequence Number"};
		JComboBox metricPickerComboBox = new JComboBox(options);
		return metricPickerComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Visualizer.getScatterPlotView().getScatterPlotRenderer().updateXAxis((String) timescalePicker.getSelectedItem());
//		Visualizer.getScatterPlotView().getScatterPlotRenderer().getDisplay().getVisualization().run("update");
	}
	
}
