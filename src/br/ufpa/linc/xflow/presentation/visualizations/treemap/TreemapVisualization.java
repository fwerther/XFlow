package br.ufpa.linc.xflow.presentation.visualizations.treemap;

import javax.swing.JComponent;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.treemap.controls.EntryPointsControl;

@SuppressWarnings("unchecked")
public class TreemapVisualization extends Visualization {

	private final VisualizationRenderer<TreemapVisualization>[] renderers = new VisualizationRenderer[]{new TreemapRenderer()};
	private final VisualizationControl<TreemapVisualization>[] controls = new VisualizationControl[]{new EntryPointsControl()};

	public TreemapVisualization(Metrics metricsSession) {
		super(metricsSession);
	}
	
	@Override
	public JComponent buildVisualizationGUI() throws DatabaseException {
		for (VisualizationRenderer<TreemapVisualization> renderer : renderers) {
			renderer.composeVisualization(this.visualizationGUIComponent);
		}
		
		for (VisualizationControl<TreemapVisualization> control : controls) {
			control.buildControlGUI(this.visualizationGUIComponent);
		}
		
		return this.visualizationGUIComponent;
	}

	@Override
	public String getName() {
		return "Treemap Visualization";
	}

	@Override
	public void toggleQualitySettings(int qualityParameter) {
		switch (qualityParameter) {
		case VisualizationRenderer.HIGH_QUALITY:
			for (VisualizationRenderer<TreemapVisualization> renderer : this.renderers) {
				renderer.setHighQuality();
			}			
			break;

		case VisualizationRenderer.LOW_QUALITY:
			for (VisualizationRenderer<TreemapVisualization> renderer : this.renderers) {
				renderer.setLowerQuality();
			}			
			break;
		}
	}

	@Override
	public void updateDisplayedData(int inferiorLimit, int superiorLimit) throws DatabaseException {
		for (VisualizationRenderer<TreemapVisualization> renderer : this.renderers) {
			renderer.updateVisualizationLimits(inferiorLimit, superiorLimit);
		}	
	}

	public VisualizationRenderer<TreemapVisualization>[] getRenderers() {
		return renderers;
	}

	@Override
	public void updateAuthorsVisibility(String selectedAuthorsQuery) throws DatabaseException {
		((TreemapRenderer) this.renderers[0]).mapAuthorFilesVisibility(selectedAuthorsQuery);
	}

}
