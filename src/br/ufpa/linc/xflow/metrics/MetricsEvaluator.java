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

import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.dao.EntryMetricsDAO;
import br.ufpa.linc.xflow.data.dao.FileMetricsDAO;
import br.ufpa.linc.xflow.data.dao.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.ProjectMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
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

	public MetricsEvaluator(){
		JUNGGraph.clearVerticesCache();
	}

	public void evaluateMetrics(final Analysis analysis) throws DatabaseException {
		System.out.println("Metric evaluation started.\n");
		initiateCaches();
		
		final List<Entry> entries = new EntryDAO().getAllEntriesWithinEntries(analysis.getFirstEntry(), analysis.getLastEntry());
		
		for (Entry entry : entries) {
			System.out.print("Evaluating revision "+entry.getRevision()+"\n");
			if(analysis.checkCutoffValues(entry)){
				calculateGraphMetrics(analysis, entry);
				System.out.print("Graph metrics done!\n");
				calculateEntryMetrics(analysis, entry);
				System.out.print("Entry metrics done!\n");
			} else {
				System.out.println("Revision skipped. Entry is not valid for current analysis.");
			}
		}

		clearCaches();
	}

	private void calculateGraphMetrics(final Analysis analysis, final Entry entry) throws DatabaseException {
		final JUNGGraph dependencyGraph = analysis.processEntryDependencyGraph(entry, DependencyObject.FILE_DEPENDENCY);
		
		for (ObjFile file : entry.getEntryFiles()) {
			final ObjFile fileDTO = new ObjFileDAO().findAddedFileByPathUntilEntry(analysis.getProject(), entry, file.getPath());

			final FileMetricValues fileMetricTable = new FileMetricValues();
			fileMetricTable.setAssociatedAnalysis(analysis);
			fileMetricTable.setEntry(entry);
			fileMetricTable.setFileID(fileDTO.getId());

			calculateFileMetrics(dependencyGraph, fileDTO.getId(), fileMetricTable);

			new FileMetricsDAO().insert(fileMetricTable);
		}

		final ProjectMetricValues projectMetricTable = new ProjectMetricValues();
		projectMetricTable.setAssociatedAnalysis(analysis);
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

	private void calculateEntryMetrics(final Analysis analysis, final Entry entry) throws DatabaseException {
		final EntryMetricValues metricValues = new EntryMetricValues();
		metricValues.setAuthor(entry.getAuthor());
		metricValues.setAssociatedAnalysis(analysis);
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
