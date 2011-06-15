package br.ufpa.linc.xflow.presentation.visualizations.activity;

import javax.swing.JComponent;
import javax.swing.JSplitPane;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;

@SuppressWarnings("unchecked")
public class ActivityVisualization extends Visualization {

	private final VisualizationRenderer<ActivityVisualization>[] renderers = new VisualizationRenderer[]{new BarsChartRenderer(), new StackedAreaRenderer()};
	private final VisualizationControl<ActivityVisualization>[] controls = new VisualizationControl[]{};
	
	public ActivityVisualization(Metrics metricsSession) {
		super(metricsSession);
	}
	
	@Override
	public JComponent buildVisualizationGUI() throws DatabaseException {
		JSplitPane activityVisualizationPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		activityVisualizationPanel.setOneTouchExpandable(true);
		activityVisualizationPanel.setDividerSize(7);
		activityVisualizationPanel.setDividerLocation(400);
		this.visualizationGUIComponent.add(activityVisualizationPanel);
		for (VisualizationRenderer<ActivityVisualization> renderer : this.renderers) {
			renderer.composeVisualization(activityVisualizationPanel);
		}
		
		for (VisualizationControl<ActivityVisualization> control : this.controls) {
			control.buildControlGUI(this.visualizationGUIComponent);
		}
		
		return this.visualizationGUIComponent;
	}

	@Override
	public String getName() {
		return "Activity Visualization";
	}

	@Override
	public void toggleQualitySettings(int qualityParameter) {
		switch (qualityParameter) {
		case VisualizationRenderer.HIGH_QUALITY:
			for (VisualizationRenderer<ActivityVisualization> renderer : this.renderers) {
				renderer.setHighQuality();
			}
			break;

		case VisualizationRenderer.LOW_QUALITY:
			for (VisualizationRenderer<ActivityVisualization> renderer : this.renderers) {
				renderer.setLowerQuality();
			}
		}
	}

	@Override
	public void updateDisplayedData(int inferiorLimit, int superiorLimit) throws DatabaseException {
		for (VisualizationRenderer<ActivityVisualization> renderer : this.renderers) {
			renderer.updateVisualizationLimits(inferiorLimit, superiorLimit);
		}
	}

	@Override
	public void updateAuthorsVisibility(String selectedAuthorsQuery) throws DatabaseException {
		((BarsChartRenderer) this.renderers[0]).updateSeriesVisibility(selectedAuthorsQuery);
		((StackedAreaRenderer) this.renderers[1]).getAuthorsSearchPanel().setQuery(selectedAuthorsQuery);
	}
}