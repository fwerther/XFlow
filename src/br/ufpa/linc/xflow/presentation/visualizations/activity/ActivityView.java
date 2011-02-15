package br.ufpa.linc.xflow.presentation.visualizations.activity;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class ActivityView extends AbstractVisualization {

	private BarChartRenderer barChartRenderer;
	private StackedAreaRenderer stackedAreaRenderer;
	
	@Override
	public JComponent composeVisualizationPanel() throws DatabaseException {
		this.barChartRenderer = new BarChartRenderer();
		this.stackedAreaRenderer = new StackedAreaRenderer();

		JPanel stackedAreaPanel = stackedAreaRenderer.draw();
		JPanel barsPanel = barChartRenderer.draw();
		JSplitPane activitySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, stackedAreaPanel, barsPanel);
		activitySplitPane.setOneTouchExpandable(true);
		activitySplitPane.setDividerSize(7);
		activitySplitPane.setDividerLocation(400);
		
		return activitySplitPane;
	}

	public BarChartRenderer getBarChartRenderer() {
		return barChartRenderer;
	}

	public StackedAreaRenderer getStackedAreaRenderer() {
		return stackedAreaRenderer;
	}

}
