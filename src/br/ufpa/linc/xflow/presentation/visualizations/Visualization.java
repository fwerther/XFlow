package br.ufpa.linc.xflow.presentation.visualizations;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;


public abstract class Visualization {

	protected final JComponent visualizationGUIComponent;
	
	public Visualization(Metrics metricsSession) {
		this.visualizationGUIComponent = new JPanel(new BorderLayout());
		this.visualizationGUIComponent.putClientProperty("Metrics Session", metricsSession);
		this.visualizationGUIComponent.putClientProperty("Visualization Instance", this);
	}
	
	protected void registerVisualization(){
		this.visualizationGUIComponent.putClientProperty(this.getName(), this);
	}
	
	abstract public JComponent buildVisualizationGUI() throws DatabaseException;
	abstract public void toggleQualitySettings(int qualityParameter);
	abstract public void updateDisplayedData(int inferiorLimit, int superiorLimit) throws DatabaseException;
	abstract public String getName();

	abstract public void updateAuthorsVisibility(String selectedAuthorsQuery) throws DatabaseException;

}
