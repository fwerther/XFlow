package br.ufpa.linc.xflow.presentation.view;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.AnalysisInfoPanel;
import br.ufpa.linc.xflow.presentation.commons.DevelopersPanelControl;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;

public class SingleProjectView extends ProjectViewer {

	public static DevelopersPanelControl developersPanelControl;
	
	@Override
	public JComponent displayVisualizations() throws DatabaseException {
		JComponent analysisInfoBar = new AnalysisInfoPanel(this.metrics.iterator().next().getAssociatedAnalysis()).createInfoPanel();
		developersPanelControl = new DevelopersPanelControl(this.metrics.iterator().next());
		JComponent developersPaneControl = developersPanelControl.createControlPanel();
		JComponent visualizationsComponent = new JPanel(new BorderLayout());
		JTabbedPane visualizationsTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		for (Visualization visualization : this.visualizations) {
			JPanel visualizationPanel = new JPanel(new BorderLayout());
			JComponent visualizationComponent = visualization.buildVisualizationGUI();
			visualizationPanel.add(visualizationComponent, BorderLayout.CENTER);
			visualizationsTabbedPane.addTab(visualization.getName(), visualizationPanel);
		}
		
		visualizationsComponent.add(visualizationsTabbedPane, BorderLayout.CENTER);
		visualizationsComponent.add(analysisInfoBar, BorderLayout.SOUTH);
		visualizationsComponent.add(developersPaneControl, BorderLayout.WEST);
		visualizationsComponent.putClientProperty("Visualizations", this.visualizations.toArray(new Visualization[]{}));
		return visualizationsComponent;
	}

}
