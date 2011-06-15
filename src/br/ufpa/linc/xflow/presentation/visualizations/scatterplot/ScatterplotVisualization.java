package br.ufpa.linc.xflow.presentation.visualizations.scatterplot;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.MetricChooserControl;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.ReferenceValuesControl;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.TimescaleChooserControl;

@SuppressWarnings("unchecked")
public class ScatterplotVisualization extends Visualization {

	private final VisualizationRenderer<ScatterplotVisualization>[] renderers = new VisualizationRenderer[]{new ScatterplotRenderer()};
	private final VisualizationControl<ScatterplotVisualization>[] northControls = new VisualizationControl[]{new MetricChooserControl(), new ReferenceValuesControl(), new TimescaleChooserControl()};
	
	public ScatterplotVisualization(Metrics metricsSession) {
		super(metricsSession);
	}
	
	@Override
	public JComponent buildVisualizationGUI() throws DatabaseException {
		for (VisualizationRenderer<ScatterplotVisualization> renderer : renderers) {
			renderer.composeVisualization(this.visualizationGUIComponent);
		}
		
		JPanel northPanel = new JPanel();
		this.visualizationGUIComponent.add(northPanel, BorderLayout.NORTH);
		for (VisualizationControl<ScatterplotVisualization> control : northControls) {
			control.buildControlGUI(northPanel);
		}
		
		return this.visualizationGUIComponent;
	}

	@Override
	public String getName() {
		return "Scatterplot Visualization";
	}

	@Override
	public void toggleQualitySettings(int qualityParameter) {
		switch (qualityParameter) {
		case VisualizationRenderer.HIGH_QUALITY:
			for (VisualizationRenderer<ScatterplotVisualization> renderer : this.renderers) {
				renderer.setHighQuality();
			}
			break;

		case VisualizationRenderer.LOW_QUALITY:
			for (VisualizationRenderer<ScatterplotVisualization> renderer : this.renderers) {
				renderer.setLowerQuality();
			}
		}
	}

	@Override
	public void updateDisplayedData(int inferiorLimit, int superiorLimit) throws DatabaseException {
		for (VisualizationRenderer<ScatterplotVisualization> renderer : this.renderers) {
			renderer.updateVisualizationLimits(inferiorLimit, superiorLimit);
		}
	}

	public VisualizationRenderer<ScatterplotVisualization>[] getRenderers() {
		return renderers;
	}

	@Override
	public void updateAuthorsVisibility(String selectedAuthorsQuery) throws DatabaseException {
		if(selectedAuthorsQuery.isEmpty()){
			if(((ScatterplotRenderer) this.renderers[0]).getAuthorsSearchPanel().getQuery().contains("Reference")){
				selectedAuthorsQuery = new String("Reference | ");
			}
		}
		
		((ScatterplotRenderer) this.renderers[0]).getAuthorsSearchPanel().setQuery(selectedAuthorsQuery);
	}
}
