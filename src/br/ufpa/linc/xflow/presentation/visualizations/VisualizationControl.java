package br.ufpa.linc.xflow.presentation.visualizations;

import javax.swing.JComponent;

public interface VisualizationControl<ConcreteVisualization extends Visualization> {

	public void buildControlGUI(JComponent visualizationComponent);
	
}
