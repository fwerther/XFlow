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

import br.ufpa.linc.xflow.presentation.view.ProjectViewer;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.line.LineRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.line.LineVisualization;

public class AxesChooserControl implements VisualizationControl<LineVisualization>, ActionListener {


	private JLabel metricPickerLabel;
	private JLabel timescalePickerLabel;
	private JComboBox metricPicker;
	private JComboBox timescalePicker;
	
	private LineVisualization visualizationControlled;
	
	@Override
	public void buildControlGUI(JComponent visualizationComponent) {
		visualizationControlled = (LineVisualization) ((JComponent) visualizationComponent.getParent()).getClientProperty("Visualization Instance");
		
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

		visualizationComponent.add(panel);
	}

	private JComboBox createMetricsComboBox() {
		List<String> metricsNames = ProjectViewer.getAvailableProjectMetricsNames();
		metricsNames.addAll(ProjectViewer.getAvailableEntryMetricsNames());

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
				((LineRenderer)(visualizationControlled.getRenderers()[0])).updateYAxis("Revision "+(String) metricPicker.getSelectedItem());
			} else {
				((LineRenderer)(visualizationControlled.getRenderers()[0])).updateYAxis("Sequential "+(String) metricPicker.getSelectedItem());
			}
		}
		//        else{
		//        	System.out.println("time");
		//        }
		//      String selectedMetric = (String)cb.getSelectedItem();
		//		Visualizer.getLineView().getLineChart().updateAxis("selectedMetric");
	}

}
