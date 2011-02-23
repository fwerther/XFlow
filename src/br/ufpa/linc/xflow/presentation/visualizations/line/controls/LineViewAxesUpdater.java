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
 *  LineViewAxesUpdater.java
 *  ========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.line.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.presentation.Visualizer;

public class LineViewAxesUpdater implements LineViewController, ActionListener {

	private JLabel metricPickerLabel;
	private JLabel timescalePickerLabel;
	private JComboBox metricPicker;
	private JComboBox timescalePicker;
	
	@Override
	public JComponent getControlComponent() {

		metricPickerLabel = new JLabel("Selected metric:");
		timescalePickerLabel = new JLabel("Selected temporal axis:");
		
		metricPicker = createMetricsComboBox();
		metricPicker.addActionListener(this);
		
		timescalePicker = createTimescaleComboBox();
		timescalePicker.addActionListener(this);


		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
		setupLayout(panel);

		return panel;
	}

	private JComboBox createMetricsComboBox() {
		List<String> metricsNames = Visualizer.getAvailableProjectMetricsNames();
		metricsNames.addAll(Visualizer.getAvailableEntryMetricsNames());

		JComboBox metricPickerComboBox = new JComboBox((Vector<String>) metricsNames);
		metricPickerComboBox.setPreferredSize(new Dimension(140, 25));
		return metricPickerComboBox;
	}
	
	private JComboBox createTimescaleComboBox() {
		final String[] timescaleOptions = new String[]{"Sequence Number", "Revision Number"};

		JComboBox timescaleComboBox = new JComboBox(timescaleOptions);
		timescaleComboBox.setPreferredSize(new Dimension(140, 25));
		return timescaleComboBox;
	}

	private void setupLayout(JPanel panel) {
		GroupLayout layout = new GroupLayout(panel);
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addContainerGap(298, Short.MAX_VALUE)
				.addComponent(metricPickerLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(metricPicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(timescalePickerLabel)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(timescalePicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(317, Short.MAX_VALUE)
		);

		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(metricPicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(metricPickerLabel)
								.addComponent(timescalePicker, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(timescalePickerLabel)
						)
						.addContainerGap()
				)
		);

		panel.setLayout(layout);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JComboBox cb = (JComboBox) event.getSource();
		if((cb.equals(metricPicker)) || (cb.equals(timescalePicker))){
			if(((String) timescalePicker.getSelectedItem()).contains("Revision")){
				Visualizer.getLineView().getLineChartRenderer().updateYAxis("Revision "+(String) metricPicker.getSelectedItem());
			} else {
				Visualizer.getLineView().getLineChartRenderer().updateYAxis("Sequential "+(String) metricPicker.getSelectedItem());
			}
		}
		//        else{
		//        	System.out.println("time");
		//        }
		//      String selectedMetric = (String)cb.getSelectedItem();
		//		Visualizer.getLineView().getLineChart().updateAxis("selectedMetric");
	}
}
