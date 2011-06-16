package br.ufpa.linc.xflow.presentation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import br.ufpa.linc.xflow.data.dao.metrics.MetricsDAO;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;
import br.ufpa.linc.xflow.metrics.entry.AddedFiles;
import br.ufpa.linc.xflow.metrics.entry.DeletedFiles;
import br.ufpa.linc.xflow.metrics.entry.EntryLOC;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.entry.ModifiedFiles;
import br.ufpa.linc.xflow.metrics.file.Betweenness;
import br.ufpa.linc.xflow.metrics.file.Centrality;
import br.ufpa.linc.xflow.metrics.file.FileMetricModel;
import br.ufpa.linc.xflow.metrics.project.ClusterCoefficient;
import br.ufpa.linc.xflow.metrics.project.Density;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.presentation.view.ProjectViewer;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;
import br.ufpa.linc.xflow.presentation.visualizations.activity.ActivityVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.graph.GraphVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.line.LineVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.ScatterplotVisualization;
import br.ufpa.linc.xflow.presentation.visualizations.treemap.TreemapVisualization;


public class Visualizer {

	public static final int LINE_VISUALIZATION = 0;
	public static final int GRAPH_VISUALIZATION = 1;
	public static final int SCATTERPLOT_VISUALIZATION = 2;
	public static final int TREEMAP_VISUALIZATION = 3;
	public static final int ACTIVITY_VISUALIZATION = 4;
	
	private static ProjectViewer projectsViewer;
	
	public Visualizer(){
		//Empty constructor.
	}
	
	public Visualizer(int viewType){
		this.projectsViewer = ProjectViewer.createInstance(viewType);
	}
	
	public static ProjectViewer getProjectsViewer() {
		return projectsViewer;
	}

	public static void setProjectsViewer(ProjectViewer projectsViewer) {
		Visualizer.projectsViewer = projectsViewer;
	}

	
	public JComponent composeVisualizations(boolean[] selectedVisualizations, MetricModel[] selectedMetrics, Metrics ... metricsSession) throws DatabaseException{
		Collection<Visualization> visualizationsRequired = new Vector<Visualization>();
		for (Metrics metrics : metricsSession) {
			Collection<Visualization> visualizations = identifySelectedVisualizations(selectedVisualizations, metrics);
			visualizationsRequired.addAll(visualizations);
		}
		
		Collection<ProjectMetricModel> projectMetrics = new Vector<ProjectMetricModel>();
		Collection<EntryMetricModel> entryMetrics = new Vector<EntryMetricModel>();
		Collection<FileMetricModel> fileMetrics = new Vector<FileMetricModel>();
		for (MetricModel metric : selectedMetrics) {
			if (metric instanceof ProjectMetricModel) {
				projectMetrics.add((ProjectMetricModel) metric);
			} else if (metric instanceof EntryMetricModel) {
				entryMetrics.add((EntryMetricModel) metric);
			} else if (metric instanceof FileMetricModel) {
				fileMetrics.add((FileMetricModel) metric);
			}
		}
		this.projectsViewer.setVisualizations(visualizationsRequired);
		this.projectsViewer.setMetrics(Arrays.asList(metricsSession));
		ProjectViewer.setEntryMetrics(entryMetrics.toArray(new EntryMetricModel[]{}));
		ProjectViewer.setFileMetrics(fileMetrics.toArray(new FileMetricModel[]{}));
		ProjectViewer.setProjectMetrics(projectMetrics.toArray(new ProjectMetricModel[]{}));
		
		return this.projectsViewer.displayVisualizations();
	}

	private Collection<Visualization> identifySelectedVisualizations(boolean[] selectedVisualizations, Metrics metricsSession) {
		Collection<Visualization> validVisualizations = new Vector<Visualization>();
		if(selectedVisualizations[LINE_VISUALIZATION]){
			validVisualizations.add(new LineVisualization(metricsSession));
		}
		if(selectedVisualizations[GRAPH_VISUALIZATION]){
			validVisualizations.add(new GraphVisualization(metricsSession));
		}
		if(selectedVisualizations[SCATTERPLOT_VISUALIZATION]){
			validVisualizations.add(new ScatterplotVisualization(metricsSession));
		}
		if(selectedVisualizations[TREEMAP_VISUALIZATION]){
			validVisualizations.add(new TreemapVisualization(metricsSession));
		}
		if(selectedVisualizations[ACTIVITY_VISUALIZATION]){
			validVisualizations.add(new ActivityVisualization(metricsSession));
		}
		
		return validVisualizations;
	}
	
	public static void main(String[] args) throws DatabaseException {
//		try {
//	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
//	    } catch (Exception e) {
//	           e.printStackTrace();
//	    }
	    
		Visualizer vis = new Visualizer(ProjectViewer.TABBED_PROJECTS_VIEW);
        boolean[] visualizations = new boolean[]{true, false, false, false, false};
        MetricModel[] metrics = new MetricModel[]{
        		new Density(), new ClusterCoefficient(), new AddedFiles(), new ModifiedFiles(), new DeletedFiles(), new EntryLOC(),
        		new Centrality()
        		, new Betweenness()
        };
        Metrics[] metricsSession = new Metrics[]{new MetricsDAO().findById(Metrics.class, 1L), new MetricsDAO().findById(Metrics.class, 2L), new MetricsDAO().findById(Metrics.class, 4L)};
//        Metrics[] metricsSession = new Metrics[]{new MetricsDAO().findById(Metrics.class, 2L), new MetricsDAO().findById(Metrics.class, 4L)};
//        Metrics[] metricsSession = new Metrics[]{new MetricsDAO().findById(Metrics.class, 4L), new MetricsDAO().findById(Metrics.class, 4L), new MetricsDAO().findById(Metrics.class, 4L)};
//        Metrics[] metricsSession = new Metrics[]{new MetricsDAO().findById(Metrics.class, 4L)};
//      Metrics[] metricsSession = new Metrics[]{new MetricsDAO().findById(Metrics.class, 10L)};
        
        
//        Metrics metric = new Metrics();
//        metric.setAssociatedAnalysis(new AnalysisDAO().findById(Analysis.class, 1L));
//        System.out.println(metric.getAssociatedAnalysis().getFirstEntry());
//        System.out.println(metric.getAssociatedAnalysis().getLastEntry());
////        Entry entry = new EntryDAO().findById(Entry.class, 83399);
////        metric.getAssociatedAnalysis().setLastEntry(entry);
//        Metrics[] metricsSession = new Metrics[1];
//        metricsSession[0] = metric;
        
		JComponent component = vis.composeVisualizations(visualizations, metrics, metricsSession);
        JFrame frame = new JFrame();
        frame.add(component);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
