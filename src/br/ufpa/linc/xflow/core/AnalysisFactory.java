package br.ufpa.linc.xflow.core;

import java.util.Date;

import br.ufpa.linc.xflow.core.processors.callgraph.CallGraphAnalysis;
import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesAnalysis;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.core.AnalysisDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.core.analysis.AnalysisRangeException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public abstract class AnalysisFactory {

	//FIXME: Criar enums e matar isto
	public static final int COCHANGES_ANALYSIS = 1;
	public static final int CALLGRAPH_ANALYSIS = 2;
	
	public static Analysis createCoChangesAnalysis(Project project, 
			String details, boolean temporalConsistencyForced, 
			final long startRevision, final long endRevision, 
			final int supportValue, final int confidenceValue, 
			final int maxFilesPerRevision, 
			final boolean persistCoordinationRequirements) 
			throws DatabaseException, AnalysisRangeException {
		
		CoChangesAnalysis analysis = new CoChangesAnalysis();
		
		//Sets up analysis attributes
		setupAnalysis(analysis, project, details, temporalConsistencyForced,
				startRevision, endRevision);
		
		//Sets up specific Co-Changes Attributes
		analysis.setConfidenceValue(confidenceValue);
		analysis.setSupportValue(supportValue);
		analysis.setMaxFilesPerRevision(maxFilesPerRevision);
		analysis.setCoordinationRequirementPersisted(persistCoordinationRequirements);
		
		new AnalysisDAO().insert(analysis);
		
		return analysis;
	}
	
	public static Analysis createCallGraphAnalysis(Project project, 
			String details, boolean temporalConsistencyForced, 
			final long startRevision, final long endRevision, 
			final int maxFilesPerRevision) throws DatabaseException, 
			AnalysisRangeException {
		
		CallGraphAnalysis analysis = new CallGraphAnalysis();
		
		//Sets up analysis attributes
		setupAnalysis(analysis, project, details, temporalConsistencyForced,
				startRevision, endRevision);
		
		//Sets up specific Call-Graph Attributes
		analysis.setMaxFilesPerRevision(maxFilesPerRevision);
		
		new AnalysisDAO().insert(analysis);
		
		return analysis;
	}

	private static void setupAnalysis(Analysis analysis, 
			Project project, String details, boolean temporalConsistencyForced, 
			final long startRevision, final long endRevision) throws DatabaseException, 
			AnalysisRangeException {
	
		//Analysis attributes
		analysis.setProject(project);
		analysis.setDetails(details);
		analysis.setDate(new Date());
		analysis.setTemporalConsistencyForced(temporalConsistencyForced);
		
		final Entry initialEntry;
		final Entry finalEntry;
		final EntryDAO entryDAO = new EntryDAO();
		
		if(analysis.isTemporalConsistencyForced()){
			initialEntry = entryDAO.findEntryFromSequence(project, startRevision);
			finalEntry = entryDAO.findEntryFromSequence(project, endRevision);
		}
		else{
			initialEntry = entryDAO.findEntryFromRevision(project, startRevision);
			finalEntry = entryDAO.findEntryFromRevision(project, endRevision);
		}
		
		if(isIntervalValid(initialEntry, finalEntry)){
			analysis.setInterval(initialEntry, finalEntry);
		}
	}
	
	private static final boolean isIntervalValid(final Entry initialEntry, final Entry finalEntry) throws DatabaseException, AnalysisRangeException {
		int entries = new EntryDAO().countEntriesByEntriesLimit(initialEntry, finalEntry);
		if(entries <= 0) throw new AnalysisRangeException();
		else return true;
	}
	
}