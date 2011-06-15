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

import br.ufpa.linc.xflow.presentation.view.ProjectViewer;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.ScatterplotRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.ScatterplotVisualization;

public class ReferenceValuesControl implements VisualizationControl<ScatterplotVisualization>, ActionListener {

	private JComboBox referenceMetricPicker;
	private JButton showReferenceValuesButton;
	private ScatterplotVisualization visualizationControlled;
	
	@Override
	public void buildControlGUI(JComponent visualizationComponent) {
		JPanel metricPickerPanel = new JPanel();
		
		JLabel chooseMetricLabel = new JLabel("Reference metric:");
		referenceMetricPicker = createMetricsComboBox();
		showReferenceValuesButton = new JButton("Show");
		showReferenceValuesButton.addActionListener(this);
		
		metricPickerPanel.add(chooseMetricLabel);
		metricPickerPanel.add(referenceMetricPicker);
		metricPickerPanel.add(showReferenceValuesButton);
		metricPickerPanel.setOpaque(false);
		visualizationComponent.add(metricPickerPanel);
		this.visualizationControlled = (ScatterplotVisualization) ((JComponent) visualizationComponent.getParent()).getClientProperty("Visualization Instance");
	}

	private JComboBox createMetricsComboBox() {
		List<String> metricsNames = ProjectViewer.getAvailableProjectMetricsNames();
		metricsNames.addAll(ProjectViewer.getAvailableEntryMetricsNames());
		metricsNames.addAll(ProjectViewer.getAvailableFileMetricsNames());
		
		JComboBox metricPickerComboBox = new JComboBox((Vector<String>) metricsNames);
		return metricPickerComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(showReferenceValuesButton.getText().contains("Show")){
			showReferenceValuesButton.setText("Hide");
			String query = ((ScatterplotRenderer) visualizationControlled.getRenderers()[0]).getAuthorsSearchPanel().getQuery()+" | Reference";
			((ScatterplotRenderer) visualizationControlled.getRenderers()[0]).getAuthorsSearchPanel().setQuery(query);
		}
		else{
			showReferenceValuesButton.setText("Show");
			String query = ((ScatterplotRenderer) visualizationControlled.getRenderers()[0]).getAuthorsSearchPanel().getQuery()+" | Reference";
			int index = query.indexOf("| Reference");
			String newQuery = query.substring(0, index);
			newQuery += query.substring(index+12);
			((ScatterplotRenderer) visualizationControlled.getRenderers()[0]).getAuthorsSearchPanel().setQuery(query);
			
		}
	}
}
