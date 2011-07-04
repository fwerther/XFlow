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
 *  =============
 *  Launcher.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow;
import java.util.Date;

import br.ufpa.linc.xflow.cm.DataExtractor;
import br.ufpa.linc.xflow.core.DataProcessor;
import br.ufpa.linc.xflow.data.dao.cm.ProjectDAO;
import br.ufpa.linc.xflow.data.dao.core.AnalysisDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.cm.CMException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.exception.persistence.ProjectNameAlreadyExistsException;
import br.ufpa.linc.xflow.metrics.MetricsEvaluator;
import br.ufpa.linc.xflow.metrics.entry.AddedFiles;
import br.ufpa.linc.xflow.metrics.entry.DeletedFiles;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.entry.ModifiedFiles;
import br.ufpa.linc.xflow.metrics.file.Betweenness;
import br.ufpa.linc.xflow.metrics.file.Centrality;
import br.ufpa.linc.xflow.metrics.file.FileMetricModel;
import br.ufpa.linc.xflow.metrics.project.ClusterCoefficient;
import br.ufpa.linc.xflow.metrics.project.Density;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.util.Filter;


public class Launcher {

	public Project startNewProject(String projectName, String user, String password, String repositoryURL, int repositoryType, String details, boolean downloadCode) throws ProjectNameAlreadyExistsException, DatabaseException {

		ProjectDAO projectDAO = new ProjectDAO();
			if (projectDAO.findByName(projectName) != null){
				throw new ProjectNameAlreadyExistsException();
			}
			else {
				Project project = new Project(projectName);
				project.setUrl(repositoryURL);
				project.setRepositoryType(repositoryType);
				project.setDate(new Date());
				project.setCodeDownloadEnabled(downloadCode);
				project.setUsername(user);
				project.setPassword(password);
				project.setDetails(details);
				project.setFirstRevision(0);
				project.setLastRevision(0);

				projectDAO.insert(project);
				return project;
			}
	}
	
	public Project startNewProject(String projectName, String user, String password, String repositoryURL, int repositoryType, String details, boolean downloadCode, boolean forceTemporalConsistency) throws ProjectNameAlreadyExistsException, DatabaseException {

		ProjectDAO projectDAO = new ProjectDAO();
			if (projectDAO.findByName(projectName) != null){
				throw new ProjectNameAlreadyExistsException();
			}
			else {
				Project project = new Project(projectName);
				project.setUrl(repositoryURL);
				project.setRepositoryType(repositoryType);
				project.setDate(new Date());
				project.setCodeDownloadEnabled(downloadCode);
				project.setUsername(user);
				project.setPassword(password);
				project.setDetails(details);
				project.setFirstRevision(0);
				project.setLastRevision(0);
				project.setTemporalConsistencyForced(forceTemporalConsistency);

				projectDAO.insert(project);
				return project;
			}
	}
	
	public void downloadProjectData(Project project, long firstRevision, long lastRevision, Filter filter) throws CMException, DatabaseException {
		DataExtractor dataExtractor = new DataExtractor();
		dataExtractor.extractData(project, firstRevision, lastRevision, filter);
	}
	
	public void resumeProjectDownload(Project project, int newFinalCommit, Filter filter) throws CMException, DatabaseException{
		int lastDownloadedCommit = (int) project.getLastRevision();
		DataExtractor extractor = new DataExtractor();
		extractor.extractData(project, lastDownloadedCommit+1, newFinalCommit, filter);
	}

	public void deleteProject(Project project){
		try {
			new ProjectDAO().remove(project);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}
	
	public void processProject(Analysis analysis, Filter filter) throws DatabaseException {
		DataProcessor.processEntries(analysis, filter);
	}	
	
	public void resumeProjectProcessing(Analysis analysis, long endRevision, Filter filter, String details) throws DatabaseException {
		DataProcessor.resumeProcess(analysis, endRevision, filter, details);
	}
	
	public void resumeProjectProcessing(Analysis analysis, Entry finalEntry, Filter filter, String details) throws DatabaseException {
		DataProcessor.resumeProcess(analysis, finalEntry, filter, details);
	}

	public void evaluateMetrics(Analysis analysis, ProjectMetricModel[] selectedProjectMetrics, EntryMetricModel[] selectedEntryMetrics, FileMetricModel[] selectedFileMetrics) throws DatabaseException {
		MetricsEvaluator dataEvaluator = new MetricsEvaluator();
		dataEvaluator.setProjectMetrics(selectedProjectMetrics);
		dataEvaluator.setEntryMetrics(selectedEntryMetrics);
		dataEvaluator.setFileMetrics(selectedFileMetrics);
		dataEvaluator.evaluateMetrics(analysis);
	}

	public void drawVisualizations(Analysis analysis){
		Visualizer dataVisualizer = new Visualizer();
//		try {
//			dataVisualizer.composeVisualizationsPane(analysis);
//			JFrame jframe = new JFrame();
//			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			jframe.add(dataVisualizer.composeVisualizationsPane(new AnalysisDAO().findById(Analysis.class, 1L)));
//			jframe.setVisible(true);
//			jframe.pack();
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args) throws DatabaseException {
		
		/**
		try{
			//CallGraphAnalysis callGraphAnalysis = (CallGraphAnalysis)new AnalysisDAO().findById(Analysis.class, 7L);
			//StructuralCouplingCalculator couplingCalculator = new StructuralCouplingCalculator();
			//couplingCalculator.calculate(callGraphAnalysis);
			
			CoChangesAnalysis coChangesAnalysis = (CoChangesAnalysis)new AnalysisDAO().findById(Analysis.class, 1L);
			CoChangeCalculator coChangeCalculator = new CoChangeCalculator();
			coChangeCalculator.calculate(coChangesAnalysis);
		}catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		/**
		Project p = null;
	
		try {
			p = new ProjectDAO().findById(Project.class, 2L);
			a.resumeProjectDownload(p, 200000, new Filter(".*?\\.java"));
		} catch (DatabaseException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		//XXX: CÓDIGO PARA COLETA DE DADOS DO PROJETO
		Launcher a = new Launcher();
		Project p = null;
		
//		try {
//			p = a.startNewProject("Easyclinica", "anonymous", "anonymous", "/Users/mauricioaniche/dev/easyclinica/easyclinica", AccessFactory.GIT_REPOSITORY, "EasyClinica", false, false);
//		} catch (ProjectNameAlreadyExistsException e) {
//			e.printStackTrace();
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//		}
	
//		try {
//			a.downloadProjectData(p, 1, 1541, new Filter(".*?"));
//		} catch (CMException e1) {
//			e1.printStackTrace();
//		} catch (DatabaseException e1) {
//			e1.printStackTrace();
//		}
		
		
//		Launcher a = new Launcher();
//		Project p = null;
//		
//		try {
//			p = new ProjectDAO().findById(Project.class, 1L);
//			//a.resumeProjectDownload(p, 7185, new Filter(".*?/trunk/.*?(\\.(java|html|h|c|cpp))"));
//			a.resumeProjectDownload(p, 884143, new Filter(".*?\\.java"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		/**
		//Aplicação do Sliding time window algorithm
		try{
			SlidingTimeWindowProcessor.process(p, 200);
		} catch(Exception e){
			e.printStackTrace();
		}
		*/
		
		//XXX: CÓDIGO PARA PROCESSAMENTO DO PROJETO
//		try {
//			//CoChange
//			//Analysis analysis = AnalysisFactory.createCoChangesAnalysis(p, "CoChanges - All ASF (*.java)", false, 1, 150000, 0, 0, 13, false);
//			Analysis analysis = AnalysisFactory.createCoChangesAnalysis(p, "CoChanges - Easy Clinica", false, 3, 1541, 0, 0, 0, false);
//			//CallGraph
//			//Analysis analysis = AnalysisFactory.createCallGraphAnalysis(p, "CallGraph - All ASF (*.java)", false, 1, 150000, 13);
//			a.processProject(analysis, new Filter(".*?"));
//		} catch (DatabaseException e) {
//			e.printStackTrace();
//		} catch (AnalysisRangeException e) {
//			e.printStackTrace();
//		}
		
		FileMetricModel[] fileMetrics = new FileMetricModel[]{
	            new Centrality(), new Betweenness()
		};

		EntryMetricModel[] entryMetrics = new EntryMetricModel[]{
	            new AddedFiles(), new ModifiedFiles(), new DeletedFiles()
		};

		ProjectMetricModel[] projectMetrics = new ProjectMetricModel[]{
	            new Density(), new ClusterCoefficient()
		};
//		
//		//XXX: CÓDIGO PARA CALCULDO DAS MÉTRICAS
		try {
			a.evaluateMetrics(new AnalysisDAO().findById(Analysis.class, 10L), projectMetrics, entryMetrics, fileMetrics);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
//		//XXX: CÓDIGO PARA GERAR VISUALIZAÇÕES
		try {
			a.drawVisualizations(new AnalysisDAO().findById(Analysis.class, 1L));
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}