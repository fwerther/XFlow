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
 *  ==================
 *  DataProcessor.java
 *  ==================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.core;

import java.util.List;

import br.ufpa.linc.xflow.core.processors.DependenciesIdentifier;
import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesCollector;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.core.AnalysisDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.Filter;


public final class DataProcessor {

	public static final void processEntries(final Analysis analysis, final Filter filter) throws DatabaseException{
		EntryDAO entryDAO = new EntryDAO();
		final DependenciesIdentifier[] contexts;

		switch (analysis.getType()) {
		
		case AnalysisFactory.COCHANGES_ANALYSIS:
			contexts = new DependenciesIdentifier[]{new CoChangesCollector()};
			break;
			
		default:
			contexts = null;
		}

		final List<Long> revisions;


		if(analysis.isTemporalConsistencyForced()){
			//Retrieving all entries is crazy, so we just look for revision numbers
			revisions = entryDAO.getAllRevisionsWithinEntries(analysis.getFirstEntry(), analysis.getLastEntry());
		}
		else{
			//Same from above
			revisions = entryDAO.getAllRevisionsWithinRevisions(analysis.getProject(), analysis.getFirstEntry().getRevision(), analysis.getLastEntry().getRevision());
		}
		 
		for (int i = 0; i < contexts.length; i++) {
			contexts[i].dataCollect(revisions, analysis, filter);
		}
	}
	
	public static final void resumeProcess(final Analysis analysis, final long finalRevision, final Filter filter, final String details) throws DatabaseException{
		
		EntryDAO entryDAO = new EntryDAO();
		final DependenciesIdentifier[] contexts;

		switch (analysis.getType()) {
		
		case AnalysisFactory.COCHANGES_ANALYSIS:
			contexts = new DependenciesIdentifier[]{new CoChangesCollector()};
			break;
			
		default:
			contexts = null;
		}

		final List<Long> revisions;
		if(analysis.isTemporalConsistencyForced()){
			long previousLastEntrySequence = entryDAO.findEntrySequence(analysis.getProject(), analysis.getLastEntry());
			Entry initialEntry = entryDAO.findEntryFromSequence(analysis.getProject(), (previousLastEntrySequence+1));
			Entry finalEntry = entryDAO.findEntryFromSequence(analysis.getProject(), finalRevision);
			revisions = entryDAO.getAllRevisionsWithinEntries(initialEntry, finalEntry);
			analysis.setLastEntry(finalEntry);
		}
		else{
			revisions = entryDAO.getAllRevisionsWithinRevisions(analysis.getProject(), analysis.getLastEntry().getRevision()+1, finalRevision);
			Entry finalEntry = entryDAO.findEntryFromRevision(analysis.getProject(), finalRevision);
			analysis.setLastEntry(finalEntry);
		}
		 
		for (int i = 0; i < contexts.length; i++) {
			contexts[i].dataCollect(revisions, analysis, filter);
		}
		
		if(!details.equals(analysis.getDetails())){
			analysis.setDetails(details);
		}
		
		new AnalysisDAO().update(analysis);
	}
	
	public static final void resumeProcess(final Analysis analysis, final Entry finalEntry, final Filter filter, final String details) throws DatabaseException{
		
		EntryDAO entryDAO = new EntryDAO();
		DependenciesIdentifier[] contexts = null;
		
		switch (analysis.getType()) {
		
		case AnalysisFactory.COCHANGES_ANALYSIS:
			contexts = new DependenciesIdentifier[]{new CoChangesCollector()};
			break;
			
		default:
			contexts = null;
		}
		
		long previousLastEntrySequence = entryDAO.findEntrySequence(analysis.getProject(), analysis.getLastEntry());
		Entry initialEntry = entryDAO.findEntryFromSequence(analysis.getProject(), (previousLastEntrySequence+1));
		List<Long> revisions = entryDAO.getAllRevisionsWithinEntries(initialEntry, finalEntry);
		
		for (int i = 0; i < contexts.length; i++) {
			contexts[i].dataCollect(revisions, analysis, filter);
		}
		
		Long lastRevision = revisions.get(revisions.size()-1);
		Entry lastEntry = entryDAO.findEntryFromRevision(analysis.getProject(), lastRevision);
		analysis.setLastEntry(lastEntry);
		
		if(!details.equals(analysis.getDetails())){
			analysis.setDetails(details);
		}
		
		new AnalysisDAO().update(analysis);
	}

}
