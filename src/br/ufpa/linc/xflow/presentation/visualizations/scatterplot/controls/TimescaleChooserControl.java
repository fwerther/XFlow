package br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.ScatterplotRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.ScatterplotVisualization;

public class TimescaleChooserControl implements VisualizationControl<ScatterplotVisualization>, ActionListener {

	private JComboBox timescalePicker;
	private ScatterplotVisualization visualizationControlled;
	
	@Override
	public void buildControlGUI(JComponent visualizationComponent) {
		JPanel timescalePickerPanel = new JPanel();
		JLabel timescaleLabel = new JLabel("Temporal scale (X Axis):");
		
		timescalePicker = createMetricsComboBox();
		timescalePicker.addActionListener(this);
		
		timescalePickerPanel.add(timescaleLabel);
		timescalePickerPanel.add(timescalePicker);
		timescalePickerPanel.setBackground(Color.white);
		timescalePickerPanel.setOpaque(false);
		visualizationComponent.add(timescalePickerPanel);
		this.visualizationControlled = (ScatterplotVisualization) ((JComponent) visualizationComponent.getParent()).getClientProperty("Visualization Instance");
	}
	
	private JComboBox createMetricsComboBox() {
		String[] options = new String[]{"Revision Number", "Commit Sequence", "Author Sequence Number"};
		JComboBox metricPickerComboBox = new JComboBox(options);
		return metricPickerComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		((ScatterplotRenderer) visualizationControlled.getRenderers()[0]).updateYAxis((String) timescalePicker.getSelectedItem());
	}

}
