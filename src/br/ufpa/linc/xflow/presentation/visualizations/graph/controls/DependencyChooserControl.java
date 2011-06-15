package br.ufpa.linc.xflow.presentation.visualizations.graph.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.graph.GraphRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.graph.GraphVisualization;

public class DependencyChooserControl implements VisualizationControl<GraphVisualization>, ActionListener {

	private JComboBox dependencyChooserComboBox;
	private GraphVisualization visualizationControlled;
	
	@Override
	public void buildControlGUI(JComponent visualizationComponent) {
		this.visualizationControlled = (GraphVisualization) ((JComponent) visualizationComponent.getParent()).getClientProperty("Visualization Instance");
		JPanel metricPickerPanel = new JPanel();
		
		dependencyChooserComboBox = createChooserComboBox();
		dependencyChooserComboBox.addActionListener(this);

		JLabel chooseRepresentationLabel = new JLabel("Displaying:");
		metricPickerPanel.add(chooseRepresentationLabel);
		metricPickerPanel.add(dependencyChooserComboBox);
		metricPickerPanel.setOpaque(false);
		
		visualizationComponent.add(metricPickerPanel);
	}

	private JComboBox createChooserComboBox() {
		final String[] representationPossibilites = new String[]{"Coordination Requirements", "Task Assignment", "Task Dependency"};
		JComboBox metricPickerComboBox = new JComboBox(representationPossibilites);
		return metricPickerComboBox;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final int selectedRepresentation;
		if(((String) dependencyChooserComboBox.getSelectedItem()).equals("Coordination Requirements")){
			selectedRepresentation = Dependency.COORD_REQUIREMENTS;
		} else if (((String) dependencyChooserComboBox.getSelectedItem()).equals("Task Assignment")){
			selectedRepresentation = Dependency.TASK_ASSIGNMENT;
		} else {
			selectedRepresentation = Dependency.TASK_DEPENDENCY;
		}
		try {
			((GraphRenderer) visualizationControlled.getRenderers()[0]).changeGraphType(selectedRepresentation);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
//		Visualizer.getScatterPlotView().getScatterPlotRenderer().updateYAxis((String) metricPicker.getSelectedItem());
//		Visualizer.getScatterPlotView().getScatterPlotRenderer().getDisplay().getVisualization().run("update");
	}

}
