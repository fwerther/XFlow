package br.ufpa.linc.xflow.presentation.view;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.file.FileMetricModel;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.presentation.visualizations.Visualization;

public abstract class ProjectViewer {

	public static final int SINGLE_PROJECT_VIEW = 0;
	public static final int TABBED_PROJECTS_VIEW = 1;
	public static final int MULTI_PROJECT_VIEW = 2;
	
	protected Collection<Visualization> visualizations;
	protected Collection<Metrics> metrics;
	
	protected static ProjectMetricModel[] projectMetrics;
	protected static FileMetricModel[] fileMetrics;
	protected static EntryMetricModel[] entryMetrics;
	
	public static ProjectViewer createInstance(int viewType) {
		switch (viewType) {
		case SINGLE_PROJECT_VIEW:
			return new SingleProjectView();

		case TABBED_PROJECTS_VIEW:
			return new TabbedProjectViewSX();
			
		case MULTI_PROJECT_VIEW:
			return new MultiProjectView();
			
		default:
			//Never reached case.
			return null;
		}
	}

	
	public abstract JComponent displayVisualizations() throws DatabaseException;


	public Collection<Visualization> getVisualizations() {
		return visualizations;
	}

	public void setVisualizations(Collection<Visualization> visualizations) {
		this.visualizations = visualizations;
	}

	public static ProjectMetricModel[] getProjectMetrics() {
		return projectMetrics;
	}

	public static void setProjectMetrics(ProjectMetricModel[] projectMetrics) {
		ProjectViewer.projectMetrics = projectMetrics;
	}

	public static FileMetricModel[] getFileMetrics() {
		return fileMetrics;
	}

	public static void setFileMetrics(FileMetricModel[] fileMetrics) {
		ProjectViewer.fileMetrics = fileMetrics;
	}

	public static EntryMetricModel[] getEntryMetrics() {
		return entryMetrics;
	}
	
	public static void setEntryMetrics(EntryMetricModel[] entryMetrics) {
		ProjectViewer.entryMetrics = entryMetrics;
	}

	public void setMetrics(Collection<Metrics> metrics) {
		this.metrics = metrics;
	}

	public Collection<Metrics> getMetrics() {
		return metrics;
	}
	
	public static List<String> getAvailableProjectMetricsNames() {
		List<String> projectMetricsNames = new Vector<String>();
		for (int i = 0; i < projectMetrics.length; i++) {
			projectMetricsNames.add(projectMetrics[i].getMetricName());
		}
		
		return projectMetricsNames;
	}
	
	public static List<String> getAvailableEntryMetricsNames() {
		List<String> entryMetricsNames = new Vector<String>();
		for (int i = 0; i < entryMetrics.length; i++) {
			entryMetricsNames.add(entryMetrics[i].getMetricName()); 
		}
		
		return entryMetricsNames;
	}
	
	public static List<String> getAvailableFileMetricsNames() {
		List<String> fileMetricsNames = new Vector<String>();
		for (int i = 0; i < fileMetrics.length; i++) {
			fileMetricsNames.add(fileMetrics[i].getMetricName());
		}
		
		return fileMetricsNames;
	}
}
