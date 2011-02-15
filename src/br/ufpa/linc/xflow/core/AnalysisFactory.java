package br.ufpa.linc.xflow.core;

import java.util.List;

import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesAnalysis;
import br.ufpa.linc.xflow.data.dao.AnalysisDAO;
import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.exception.core.analysis.AnalysisRangeException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public abstract class AnalysisFactory {

	public static final int COCHANGES_ANALYSIS = 1;
	public static final int CALLGRAPH_ANALYSIS = 2;
	public static final int COCHANGES_CALLGRAPH_ANALYSIS = 3;
	
	public final static Analysis createAnalysis(final int analysisType){
		switch (analysisType) {
		case COCHANGES_ANALYSIS:
			return new CoChangesAnalysis();

		default:
			return null;
		}
	}
	
	public final static void startNewCoChangesAnalysis(final CoChangesAnalysis analysis, final long startRevision, final long endRevision, final int supportValue, final int confidenceValue, final int maxFilesPerRevision, final boolean persistCoordinationRequirements) throws DatabaseException, AnalysisRangeException {
		analysis.setConfidenceValue(confidenceValue);
		analysis.setSupportValue(supportValue);
		analysis.setMaxFilesPerRevision(maxFilesPerRevision);
		analysis.setCoordinationRequirementPersisted(persistCoordinationRequirements);
		
		final Entry initialEntry;
		final Entry finalEntry;
		
		if(analysis.isTemporalConsistencyForced()){
			initialEntry = new EntryDAO().findEntryFromSequence(analysis.getProject(), startRevision);
			finalEntry = new EntryDAO().findEntryFromSequence(analysis.getProject(), endRevision);
		}
		else{
			initialEntry = new EntryDAO().findEntryFromRevision(analysis.getProject(), startRevision);
			finalEntry = new EntryDAO().findEntryFromRevision(analysis.getProject(), endRevision);
		}
		
		if(checkForValidInterval(initialEntry, finalEntry)){
			analysis.setInterval(initialEntry, finalEntry);
			new AnalysisDAO().insert(analysis);
		}
	}
	
	private final static boolean checkForValidInterval(final Entry initialEntry, final Entry finalEntry) throws DatabaseException, AnalysisRangeException {
		final List<Entry> entries = new EntryDAO().getAllEntriesWithinEntries(initialEntry, finalEntry);
		if(entries == null) throw new AnalysisRangeException();
		else return true;
	}

	public final static void startNewCoChangesAnalysis(final CoChangesAnalysis analysis, final Entry startEntry, final Entry finalEntry, final int supportValue, final int confidenceValue, final int maxFilesPerRevision, final boolean forceTemporalConsistency) throws DatabaseException, AnalysisRangeException {
		
		if(checkForValidInterval(startEntry, finalEntry)){
			analysis.setConfidenceValue(confidenceValue);
			analysis.setSupportValue(supportValue);
			analysis.setMaxFilesPerRevision(maxFilesPerRevision);
			analysis.setInterval(startEntry, finalEntry);

			new AnalysisDAO().insert(analysis);
		}
	}
}
