package br.ufpa.linc.xflow.presentation.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.MultiSplitLayout.Divider;
import org.jdesktop.swingx.MultiSplitLayout.Leaf;
import org.jdesktop.swingx.MultiSplitLayout.Split;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.AnalysisInfoPanel;
import br.ufpa.linc.xflow.presentation.commons.DevelopersPanelControl;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;

public class TabbedProjectViewSX extends EcosystemView implements ChangeListener {

	private List<JPanel> cards;
	
	@Override
	public JComponent displayVisualizations() throws DatabaseException {
		cards = new ArrayList<JPanel>();
		
		final JXMultiSplitPane visualizationsMultiSplitPane = new JXMultiSplitPane();
		List<MultiSplitLayout.Node> children = new ArrayList<MultiSplitLayout.Node>();
		children.add(new Leaf("VISUALIZATION "+0));
		for (int i = 1; i < metrics.size(); i++) {
			children.add(new Divider());
			children.add(new Leaf("VISUALIZATION "+i));
		}		
		Split modelRoot = new Split();
		modelRoot.setRowLayout(false);
		modelRoot.setChildren(children);
		visualizationsMultiSplitPane.getMultiSplitLayout().setModel(modelRoot);
		
//		JPanel visualizationsPanel = new JPanel(new GridLayout(this.metrics.size(), 1));

		// SETUP FIST PROJECT'S VISUALIZATIONS (including the JTabbedPanel component)
		Metrics firstMetricSession = this.metrics.iterator().next();
		
		JPanel firstVisualizationPanel = new JPanel(new BorderLayout());
		
		JComponent controlPanel = new DevelopersPanelControl(firstMetricSession).createControlPanel();
		JComponent analysisInfoBar = new AnalysisInfoPanel(firstMetricSession.getAssociatedAnalysis()).createInfoPanel();
		createTabbedPane(firstMetricSession, firstVisualizationPanel);
		firstVisualizationPanel.add(controlPanel, BorderLayout.WEST);
		firstVisualizationPanel.add(analysisInfoBar, BorderLayout.SOUTH);
//		JTabbedPane testeeee = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
//		testeeee.addTab(firstMetricSession.getAssociatedAnalysis().getProject().getName(), firstVisualizationPanel);
		visualizationsMultiSplitPane.add(firstVisualizationPanel, "VISUALIZATION 0");
//		visualizationsPanel.add(testeeee);
		
		
		// DEFINE CARD LAYOUT ON OTHERS PROJECTS, RE-USING COMMON COMPONENTS
		List<Visualization> visualizations = (List<Visualization>) this.visualizations;
		List<Metrics> metrics = (List<Metrics>) this.metrics;
		final int totalVisualizations = this.visualizations.size() / this.metrics.size();
		for (int i = 1; i < this.metrics.size(); i++) {
			
			JPanel subsequentVisualizationsPanel = new JPanel(new BorderLayout());
			JPanel visualizationCardsPanel = new JPanel(new CardLayout());
			List<Visualization> validVisualizationsList = new ArrayList<Visualization>();
			for (int j = i*totalVisualizations; j < (i*totalVisualizations)+totalVisualizations; j++) {
//			for (Visualization visualization : this.visualizations) {
				visualizationCardsPanel.add(visualizations.get(j).buildVisualizationGUI(), visualizations.get(j).getName());
				validVisualizationsList.add(visualizations.get(j));
			}
			cards.add(visualizationCardsPanel);
			
			JComponent developersControlPanel = new DevelopersPanelControl(metrics.get(i)).createControlPanel();
			analysisInfoBar = new AnalysisInfoPanel(metrics.get(i).getAssociatedAnalysis()).createInfoPanel();
			subsequentVisualizationsPanel.add(developersControlPanel, BorderLayout.WEST);
			subsequentVisualizationsPanel.add(visualizationCardsPanel, BorderLayout.CENTER);
			subsequentVisualizationsPanel.add(analysisInfoBar, BorderLayout.SOUTH);
			subsequentVisualizationsPanel.putClientProperty("Visualizations", validVisualizationsList.toArray(new Visualization[]{}));
			
//			JTabbedPane testeeeee = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
//			testeeeee.addTab(metrics.get(i).getAssociatedAnalysis().getProject().getName(), subsequentVisualizationsPanel);
			visualizationsMultiSplitPane.add(subsequentVisualizationsPanel, "VISUALIZATION "+i);
//			visualizationsPanel.add(testeeeee);
			
		}
		
		return visualizationsMultiSplitPane;
	}

	private void createTabbedPane(Metrics metricsSession, JPanel firstVisualizationPanel) throws DatabaseException {
		JTabbedPane visualizationsTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		visualizationsTabbedPane.addChangeListener(this);
		
		List<Visualization> visualizations = (List<Visualization>) this.visualizations;
		List<Visualization> firstVisualizations = new ArrayList<Visualization>();
		for (int i = 0; i < this.visualizations.size()/this.metrics.size(); i++) {
			JComponent visualizationComponent = visualizations.get(i).buildVisualizationGUI();
//			visualizationsTabbedPane.addTab(visualizations.get(i).getName(), visualizationPanel);
			visualizationsTabbedPane.addTab(visualizations.get(i).getName(), visualizationComponent);
			firstVisualizations.add(visualizations.get(i));
		}
		
		firstVisualizationPanel.putClientProperty("Visualizations", firstVisualizations.toArray(new Visualization[]{}));
		firstVisualizationPanel.add(visualizationsTabbedPane, BorderLayout.CENTER);
	}

	@Override
	public void stateChanged(ChangeEvent evt) {
	    JTabbedPane sourceTabbedPane = (JTabbedPane) evt.getSource();
		int selectedTabIndex = sourceTabbedPane.getSelectedIndex();
		for (JPanel cardsPanel : this.cards) {
		    CardLayout cardsLayout = (CardLayout)(cardsPanel.getLayout());
		    cardsLayout.show(cardsPanel, sourceTabbedPane.getTitleAt(selectedTabIndex));
		}
	}
}
