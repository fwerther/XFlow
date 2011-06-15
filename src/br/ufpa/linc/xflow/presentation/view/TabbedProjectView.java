package br.ufpa.linc.xflow.presentation.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.DevelopersPanelControl;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;

public class TabbedProjectView extends EcosystemView implements ChangeListener {

	@Override
	public JComponent displayVisualizations() throws DatabaseException {
		final JComponent visualizationGUI = new JPanel(new BorderLayout());
		final JTabbedPane visualizationPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		visualizationPane.addChangeListener(this);
		
		final int totalVisualizations = this.metrics.size() * (this.visualizations.size()/2);
		final GridLayout layout = new GridLayout(this.metrics.size(), 0);
		
		for (int i = 0; i < this.visualizations.size()/this.metrics.size(); i++) {
			final JPanel visualizationPanel = new JPanel(layout);
			visualizationPanel.setLayout(layout);
			for (int j = 0; j < totalVisualizations; j+=(visualizations.size()/2)) {
				visualizationPanel.add(((List<Visualization>) this.visualizations).get(j).buildVisualizationGUI());
			}
			visualizationPane.addTab(((List<Visualization>) this.visualizations).get(i).getName(), visualizationPanel);
		}
		visualizationGUI.add(visualizationPane, BorderLayout.CENTER);
		
		final JPanel developersControlPanel = new JPanel(layout);
		for (Metrics metricsSession : this.metrics) {
			JComponent controlPanel = new DevelopersPanelControl(metricsSession).createControlPanel();
			developersControlPanel.add(controlPanel);
		}
		visualizationGUI.add(developersControlPanel, BorderLayout.WEST);
		
					
		return visualizationGUI;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		
	}

}
