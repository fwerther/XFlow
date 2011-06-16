/* 
 * 
 * XFlow
 * _______
 * 
 *  
 *  (C) Copyright 2010, by Universidade Federal do Pará (UFPA), Francisco Santana, Jean Costa, Pedro Treccani and Cleidson de Souza.
 * 
 *  This file is part of XFlow.
 *
 *  XFlow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XFlow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XFlow.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  =====================
 *  MetricEvaluation.java
 *  =====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics;

import java.util.List;

import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesAnalysis;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.metrics.EntryMetricsDAO;
import br.ufpa.linc.xflow.data.dao.metrics.FileMetricsDAO;
import br.ufpa.linc.xflow.data.dao.metrics.MetricsDAO;
import br.ufpa.linc.xflow.data.dao.metrics.ProjectMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.jung.JUNGVertex;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.AddedFiles;
import br.ufpa.linc.xflow.metrics.entry.DeletedFiles;
import br.ufpa.linc.xflow.metrics.entry.EntryLOC;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricValues;
import br.ufpa.linc.xflow.metrics.entry.ModifiedFiles;
import br.ufpa.linc.xflow.metrics.file.Betweenness;
import br.ufpa.linc.xflow.metrics.file.Centrality;
import br.ufpa.linc.xflow.metrics.file.FileMetricModel;
import br.ufpa.linc.xflow.metrics.file.FileMetricValues;
import br.ufpa.linc.xflow.metrics.file.LOC;
import br.ufpa.linc.xflow.metrics.project.ClusterCoefficient;
import br.ufpa.linc.xflow.metrics.project.Density;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricValues;


public class MetricsEvaluator {

	private FileMetricModel[] fileMetrics = new FileMetricModel[]{
			new Centrality(), new Betweenness(), new LOC()
	};

	private EntryMetricModel[] entryMetrics = new EntryMetricModel[]{
			new AddedFiles(), new ModifiedFiles(), new EntryLOC(), new DeletedFiles()
	};

	private ProjectMetricModel[] projectMetrics = new ProjectMetricModel[]{
			new Density(), new ClusterCoefficient()
	};
	
	private Metrics metricsSession;
	
	private Dependency initialDependency;

	public MetricsEvaluator(){
		JUNGGraph.clearVerticesCache();
	}

	public void evaluateMetrics(final Analysis analysis) throws DatabaseException {
		System.out.println("Metric evaluation started.\n");
		Metrics metrics = new Metrics();
		metrics.setAssociatedAnalysis(analysis);
		new MetricsDAO().insert(metrics);
		this.metricsSession = metrics;
		
		initiateCaches();
		
		final List<Entry> entries;
		if(analysis.isTemporalConsistencyForced()){
			entries = new EntryDAO().getAllEntriesWithinEntries(analysis.getFirstEntry(), analysis.getLastEntry());
		} else {
			entries = new EntryDAO().getAllEntriesWithinRevisions(analysis.getProject(), analysis.getFirstEntry().getRevision(), analysis.getLastEntry().getRevision());
		}
		
		for (Entry entry : entries) {
			System.out.print("Evaluating revision "+entry.getRevision()+"\n");
			Dependency<FileDependencyObject, FileDependencyObject> entryDependency = new DependencyDAO().findDependencyByEntry(analysis.getId(), entry.getId(), Dependency.TASK_DEPENDENCY);
			if(entryDependency != null){
				if(analysis.checkCutoffValues(entry)){
					calculateEntryMetrics(entry);
					System.out.print("Entry metrics done!\n");
					calculateGraphMetrics(entryDependency);
					System.out.print("Graph metrics done!\n");
				} else {
					System.out.println("Revision skipped. Entry is not valid for current analysis.");
				}
			} else {
				if(analysis.checkCutoffValues(entry)){
					calculateEntryMetrics(entry);
					System.out.print("Entry metrics done!\n");
				}
				System.out.println("Graph metrics evaluation skipped. No dependencies collected for selected entry.");
			}
		}

		clearCaches();
	}
	
	public void evaluateMetrics(final Analysis analysis, List<Entry> entries) throws DatabaseException {
		System.out.println("Metric evaluation started.\n");
		Metrics metrics = new Metrics();
		metrics.setAssociatedAnalysis(analysis);
		new MetricsDAO().insert(metrics);
		this.metricsSession = metrics;
//		this.metricsSession = new MetricsDAO().findById(Metrics.class, 4L);
		
		initiateCaches();
		
		for (Entry entry : entries) {
			System.out.print("Evaluating revision "+entry.getRevision()+"\n");
			Dependency<FileDependencyObject, FileDependencyObject> entryDependency = new DependencyDAO().findDependencyByEntry(analysis.getId(), entry.getId(), Dependency.TASK_DEPENDENCY);
			if(entryDependency != null){
				if(analysis.checkCutoffValues(entry)){
					calculateEntryMetrics(entry);
					System.out.print("Entry metrics done!\n");
					calculateGraphMetrics3(entryDependency);
					System.out.print("Graph metrics done!\n");
				} else {
					System.out.println("Revision skipped. Entry is not valid for current analysis.");
				}
			} else {
				if(analysis.checkCutoffValues(entry)){
					calculateEntryMetrics(entry);
					System.out.print("Entry metrics done!\n");
				}
				System.out.println("Graph metrics evaluation skipped. No dependencies collected for selected entry.");
			}
		}

		clearCaches();
	}

	private void calculateGraphMetrics(final Dependency<FileDependencyObject, FileDependencyObject> entryDependency) throws DatabaseException {
		final Entry entry = entryDependency.getAssociatedEntry();
		final JUNGGraph dependencyGraph = metricsSession.getAssociatedAnalysis().processDependencyGraph(entryDependency);
		
		for (DependencySet<FileDependencyObject, FileDependencyObject> dependencySet : entryDependency.getDependencies()) {
			FileDependencyObject fileDependency = dependencySet.getDependedObject();
			
			final FileMetricValues fileMetricTable = new FileMetricValues();
			fileMetricTable.setAssociatedMetricsObject(metricsSession);
			fileMetricTable.setEntry(entry);
			fileMetricTable.setFile(fileDependency.getFile());

			calculateFileMetrics(dependencyGraph, fileDependency.getFile().getId(), fileMetricTable);

			new FileMetricsDAO().insert(fileMetricTable);
		}


		final ProjectMetricValues projectMetricTable = new ProjectMetricValues();
		projectMetricTable.setAssociatedMetricsObject(metricsSession);
		projectMetricTable.setEntry(entry);

		calculateProjectMetrics(dependencyGraph, projectMetricTable);
		new ProjectMetricsDAO().insert(projectMetricTable);
	}
	
	private void calculateGraphMetrics2(final Dependency<FileDependencyObject, FileDependencyObject> entryDependency) throws DatabaseException {
		final Entry entry = entryDependency.getAssociatedEntry();
		final JUNGGraph dependencyGraph = ((CoChangesAnalysis) metricsSession.getAssociatedAnalysis()).processDependencyGraph2(entryDependency);
		
		for (JUNGVertex vertex : dependencyGraph.getGraph().getVertices()) {
			final ObjFile matrixFile = new ObjFileDAO().findById(ObjFile.class, vertex.getId());
			final FileMetricValues fileMetricTable = new FileMetricValues();
			fileMetricTable.setAssociatedMetricsObject(metricsSession);
			fileMetricTable.setEntry(entry);
			fileMetricTable.setFile(matrixFile);
			calculateFileMetrics(dependencyGraph, vertex.getId(), fileMetricTable);
			new FileMetricsDAO().insert(fileMetricTable);
		}
		
		final ProjectMetricValues projectMetricTable = new ProjectMetricValues();
		projectMetricTable.setAssociatedMetricsObject(metricsSession);
		projectMetricTable.setEntry(entry);

		calculateProjectMetrics(dependencyGraph, projectMetricTable);
		new ProjectMetricsDAO().insert(projectMetricTable);
	}
	
	private void calculateGraphMetrics3(final Dependency<FileDependencyObject, FileDependencyObject> entryDependency) throws DatabaseException {
		if(initialDependency == null){
			initialDependency = entryDependency;
			return;
		}
		
		final Entry entry = entryDependency.getAssociatedEntry();
		final JUNGGraph dependencyGraph = ((CoChangesAnalysis) metricsSession.getAssociatedAnalysis()).processDependencyGraph3(initialDependency, entryDependency);
		
		for (JUNGVertex vertex : dependencyGraph.getGraph().getVertices()) {
			final ObjFile matrixFile = new ObjFileDAO().findById(ObjFile.class, vertex.getId());
			final FileMetricValues fileMetricTable = new FileMetricValues();
			fileMetricTable.setAssociatedMetricsObject(metricsSession);
			fileMetricTable.setEntry(entry);
			fileMetricTable.setFile(matrixFile);
			calculateFileMetrics(dependencyGraph, vertex.getId(), fileMetricTable);
			new FileMetricsDAO().insert(fileMetricTable);
		}
		
		final ProjectMetricValues projectMetricTable = new ProjectMetricValues();
		projectMetricTable.setAssociatedMetricsObject(metricsSession);
		projectMetricTable.setEntry(entry);

		calculateProjectMetrics(dependencyGraph, projectMetricTable);
		new ProjectMetricsDAO().insert(projectMetricTable);
	}


	private void calculateFileMetrics(final JUNGGraph dependencyGraph, final long fileID, final FileMetricValues fileMetricTable) throws DatabaseException {
		for (FileMetricModel fileMetric : fileMetrics) {
			fileMetric.evaluate(dependencyGraph, fileID, fileMetricTable);
		}
	}

	private void calculateProjectMetrics(final JUNGGraph dependencyGraph, final ProjectMetricValues projectMetricTable) {
		for (ProjectMetricModel projectMetric : projectMetrics) {
			projectMetric.evaluate(dependencyGraph, projectMetricTable);
		}
	}

	private void calculateEntryMetrics(final Entry entry) throws DatabaseException {
		final EntryMetricValues metricValues = new EntryMetricValues();
		metricValues.setAuthor(entry.getAuthor());
		metricValues.setAssociatedMetricsObject(metricsSession);
		metricValues.setEntry(entry);
		for (EntryMetricModel entryMetric : entryMetrics) {
			entryMetric.evaluate(entry, metricValues);
		}
		new EntryMetricsDAO().insert(metricValues);
	}

	public void setEntryMetrics(final EntryMetricModel[] entryMetrics) {
		this.entryMetrics = entryMetrics;
	}

	public void setFileMetrics(final FileMetricModel[] fileMetrics) {
		this.fileMetrics = fileMetrics;
	}

	public void setProjectMetrics(final ProjectMetricModel[] projectMetrics) {
		this.projectMetrics = projectMetrics;
	}

	private void clearCaches() {
		FileMetricModel.clearVerticesCache();
	}
	

	private void initiateCaches() {
		FileMetricModel.initiateCache();
	}
}
