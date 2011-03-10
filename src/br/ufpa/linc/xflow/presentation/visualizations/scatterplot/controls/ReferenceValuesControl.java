package br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
			showReferenceValuesButton.setText("Hide");
			String query = Visualizer.getScatterPlotView().getScatterPlotRenderer().getAuthorsSearchPanel().getQuery()+" | Reference";
			Visualizer.getScatterPlotView().getScatterPlotRenderer().getAuthorsSearchPanel().setQuery(query);
		}
		else{
			showReferenceValuesButton.setText("Show");
			String query = Visualizer.getScatterPlotView().getScatterPlotRenderer().getAuthorsSearchPanel().getQuery()+" | Reference";
			int index = query.indexOf("| Reference");
			String newQuery = query.substring(0, index);
			newQuery += query.substring(index+12);
			Visualizer.getScatterPlotView().getScatterPlotRenderer().getAuthorsSearchPanel().setQuery(query);
			
		}
	}

}
