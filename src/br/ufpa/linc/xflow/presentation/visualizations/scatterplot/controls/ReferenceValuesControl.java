package br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.presentation.Visualizer;

public class ReferenceValuesControl implements ScatterPlotViewController, ActionListener {

	private JComboBox referenceMetricPicker;
	private JButton showReferenceValuesButton;
	
	@Override
	public JComponent getControlComponent() {
		JPanel metricPickerPanel = new JPanel();
		
		JLabel chooseMetricLabel = new JLabel("Reference metric:");
		referenceMetricPicker = createMetricsComboBox();
		showReferenceValuesButton = new JButton("Show");
		showReferenceValuesButton.addActionListener(this);
		
		metricPickerPanel.add(chooseMetricLabel);
		metricPickerPanel.add(referenceMetricPicker);
		metricPickerPanel.add(showReferenceValuesButton);
		metricPickerPanel.setOpaque(false);
		return metricPickerPanel;
	}

	private JComboBox createMetricsComboBox() {
		List<String> metricsNames = Visualizer.getAvailableProjectMetricsNames();
		metricsNames.addAll(Visualizer.getAvailableEntryMetricsNames());
		metricsNames.addAll(Visualizer.getAvailableFileMetricsNames());
		
		JComboBox metricPickerComboBox = new JComboBox((Vector<String>) metricsNames);
		return metricPickerComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(showReferenceValuesButton.getText().contains("Show")){
			//TODO: Mais código aqui.
			showReferenceValuesButton.setText("Hide");
		}
		else{
			showReferenceValuesButton.setText("Show");
		}
	}

}
