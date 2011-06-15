package br.ufpa.linc.xflow.presentation.visualizations.line;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationControl;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.line.controls.AxesChooserControl;
import br.ufpa.linc.xflow.presentation.visualizations.line.controls.RangeMarkersControl;
import br.ufpa.linc.xflow.presentation.visualizations.line.controls.ZoomControl;

@SuppressWarnings("unchecked")
public class LineVisualization extends Visualization {

	private final VisualizationRenderer<LineVisualization>[] renderers = new VisualizationRenderer[]{new LineRenderer()};
	private final VisualizationControl<LineVisualization>[] northControls = new VisualizationControl[]{new AxesChooserControl()};
	private final VisualizationControl<LineVisualization>[] eastControls = new VisualizationControl[]{new RangeMarkersControl()};
	private final VisualizationControl<LineVisualization>[] southControls = new VisualizationControl[]{new ZoomControl()}; 
	
	public LineVisualization(Metrics metricsSession) {
		super(metricsSession);
	}
	
	@Override
	public JComponent buildVisualizationGUI() throws DatabaseException {
		
		for (VisualizationRenderer<LineVisualization> renderer : renderers) {
			renderer.composeVisualization(this.visualizationGUIComponent);
		}
		
		final JPanel northPanel = new JPanel();
		final JPanel southPanel = new JPanel();
		final JPanel eastPanel = new JPanel();
		this.visualizationGUIComponent.add(northPanel, BorderLayout.NORTH);
		this.visualizationGUIComponent.add(southPanel, BorderLayout.SOUTH);
		this.visualizationGUIComponent.add(eastPanel, BorderLayout.EAST);
		
		for (VisualizationControl<LineVisualization> control : northControls) {
			control.buildControlGUI(northPanel);
		}
		for (VisualizationControl<LineVisualization> control : southControls) {
			control.buildControlGUI(southPanel);
		}
		for (VisualizationControl<LineVisualization> control : eastControls) {
			control.buildControlGUI(eastPanel);
		}
		
		return this.visualizationGUIComponent;
	}

	@Override
	public String getName() {
		return "Line Visualization";
	}

	@Override
	public void toggleQualitySettings(int qualityParameter) {
		//Do nothing. Not applicable.		
	}

	@Override
	public void updateDisplayedData(int inferiorLimit, int superiorLimit) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAuthorsVisibility(String selectedAuthorsQuery) throws DatabaseException {
		// Not applicable.
	}

	public VisualizationRenderer<LineVisualization>[] getRenderers() {
		return renderers;
	}
}
