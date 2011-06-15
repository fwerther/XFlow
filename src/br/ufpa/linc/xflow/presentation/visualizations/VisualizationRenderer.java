package br.ufpa.linc.xflow.presentation.visualizations;

import javax.swing.JComponent;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public interface VisualizationRenderer<ConcreteVisualization extends Visualization> {

	public static final int LOW_QUALITY = 0;
	public static final int HIGH_QUALITY = 1;
	
	public void composeVisualization(JComponent visualizationComponent) throws DatabaseException;
	public void updateVisualizationLimits(int inferiorLimit, int superiorLimit) throws DatabaseException;
	public void setLowerQuality();
	public void setHighQuality();
	
}
