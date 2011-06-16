/* 
 * 
 * XFlow
 * _______
 * 
 *  
 *  (C) Copyright 2010, by Universidade Federal do Pará (UFPA), Francisco Santana, Jean Costa, Pedro Treccani AND Cleidson de Souza.
 * 
 *  This file is part of XFlow.
 *
 *  XFlow is free software: you can redistribute it AND/or modify
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
 *  EntryDAO.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.dao.cm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.ufpa.linc.xflow.data.dao.BaseDAO;
import br.ufpa.linc.xflow.data.database.DatabaseManager;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.AccessDeniedException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.exception.persistence.UnableToReachDatabaseException;


public class EntryDAO extends BaseDAO<Entry>{

	@Override
	public Entry findById(final Class<Entry> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	public boolean insert(final Entry entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final Entry entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final Entry entity) throws DatabaseException {
		return super.update(entity);
	}
	
	@Override
	protected Entry findUnique(final Class<Entry> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<Entry> findByQuery(final Class<Entry> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	public Collection<Entry> findAll(final Class<? extends Entry> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	public Entry findEntryFromRevision(final Project project, final long revision) throws DatabaseException {
		final String query = "SELECT entry FROM entry entry WHERE entry.revision = :revision AND entry.project.id = :project";
		
		//Sliding time window
		//final String query = "SELECT entry FROM entry entry WHERE entry.revision = :revision AND entry.project.id = :project AND entry.id > 1541";
		
		final Object[] parameter1 = new Object[]{"revision", revision};
		final Object[] parameter2 = new Object[]{"project", project.getId()};
		
		return findUnique(Entry.class, query, parameter1, parameter2);
	}
	
	public long findEntryIdFromRevision(final Project project, final long revision) throws DatabaseException {
		final String query = "SELECT entry.id FROM entry entry WHERE entry.revision = :revision AND entry.project = :project";
		final Object[] parameter1 = new Object[]{"revision", revision};
		final Object[] parameter2 = new Object[]{"project", project};
		
		return getLongValueByQuery(query, parameter1, parameter2);
	}
	
	public Entry findEntryFromSequence(final Project project, final long sequence) throws DatabaseException {
		final String query = "SELECT entry FROM entry entry WHERE entry.project = :project";
		final Object[] parameter1 = new Object[]{"project", project};
		
		final List<Entry> entries = (List<Entry>) findByQuery(Entry.class, query, parameter1);
		
		return entries.get((int) (sequence-1));
	}
	
	public List<Entry> getAllProjectEntries(final Project project) throws DatabaseException {
		final String query = "select e FROM entry e where e.project = :project";
		final Object[] parameter1 = new Object[]{"project", project};
		return (List<Entry>) findByQuery(Entry.class, query, parameter1);
	}
	
	public long findEntrySequence(final Project project, final Entry entry) throws DatabaseException {
		final String query = "SELECT COUNT(*) FROM entry entry WHERE entry.project = :project AND entry.id <= :entryID";
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"entryID", entry.getId()};
		
		return getLongValueByQuery(query, parameter1, parameter2);
	}
	
	public ArrayList<Entry> getAllEntriesWithinRevisions(final Project project, final long startRevision, final long endRevision) throws DatabaseException {
		final String query = "select e FROM entry e where e.project = :project AND e.revision between :startrevision AND :endrevision order by e.revision";
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"startrevision", startRevision};
		final Object[] parameter3 = new Object[]{"endrevision", endRevision};

		return (ArrayList<Entry>) findByQuery(Entry.class, query, parameter1, parameter2, parameter3);
	}
	
	public List<Long> getAllRevisionsWithinRevisions(final Project project, final long startRevision, final long endRevision) throws DatabaseException {
	
		final String query = 
			"select e.revision FROM entry e WHERE " +
			"e.project = :project AND " +
			"e.revision between :startrevision AND :endrevision " +
			"ORDER BY e.revision";
				
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"startrevision", startRevision};
		final Object[] parameter3 = new Object[]{"endrevision", endRevision};

		return (List<Long>) findObjectsByQuery(query, parameter1, parameter2, parameter3);
	}
	
	public List<Long> getAllRevisionsWithinRevisions(final Project project, final long startRevision, final long endRevision, int maxFiles) throws DatabaseException {
		
		String query = 
			"select e.revision FROM entry e WHERE " +
			"e.project = :project AND " +
			"e.revision BETWEEN :startrevision AND :endrevision AND " +
			"e.entryFiles IS NOT EMPTY";
			//Sliding time window
			//"e.entryFiles IS NOT EMPTY AND e.id > 1541";
		
		
			
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"startrevision", startRevision};
		final Object[] parameter3 = new Object[]{"endrevision", endRevision};
		
		//TODO: Use criteria API
		if (maxFiles == 0){
			query = query.concat(" ORDER BY e.revision");
			return (List<Long>) findObjectsByQuery(query, parameter1, parameter2, parameter3);	
		}
		else{
			query = query.concat(" AND e.entryFiles.size <= :maxFiles ORDER BY e.revision");
			final Object[] parameter4 = new Object[]{"maxFiles", maxFiles};	
			return (List<Long>) findObjectsByQuery(query, parameter1, parameter2, parameter3, parameter4);
		}
	}
	
	
	//TODO: not finished yet.
	public ArrayList<Entry> getAllEntriesWithinDates(final Project project, final Date startDate, final Date finalDate) throws DatabaseException {
		if(project.isTemporalConsistencyForced()){
			final String query = "select e FROM entry e where e.project = :project order by e.revision";
			final Object[] parameter1 = new Object[]{"project", project};
			return null;
		}
		else{
			final String query = "select distinct e FROM entry e where e.project = :project AND e.revision between :startrevision AND :endrevision";
			final Object[] parameter1 = new Object[]{"project", project};
			return null;
		}
	}
	
	public int countEntryChangedFiles(final long entryID) throws DatabaseException{
		final String query = "SELECT COUNT(*) FROM file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntryModifiedFiles(final long entryID) throws DatabaseException{
		final String query = "SELECT COUNT(*) FROM file f where f.operationType = 'M' AND f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntryAddedFiles(final long entryID) throws DatabaseException{
		final String query = "SELECT COUNT(*) FROM file f where f.operationType = 'A' AND f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}

	public int countEntryDeletedFiles(final long entryID) throws DatabaseException {
		final String query = "SELECT COUNT(*) FROM file f where f.operationType = 'D' AND f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntryTotalLOC(final long entryID) throws DatabaseException{
		final String query = "SELECT SUM(f.totalLinesOfCode) FROM file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntryAddedLOC(final long entryID) throws DatabaseException{
		final String query = "SELECT SUM(f.addedLinesOfCode) FROM file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntryRemovedLOC(final long entryID) throws DatabaseException{
		final String query = "SELECT SUM(f.removedLinesOfCode) FROM file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntryModifiedLOC(final long entryID) throws DatabaseException{
		final String query = "SELECT SUM(f.modifiedLinesOfCode) FROM file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		int result = getIntegerValueByQuery(query, parameter1);
		result = (result >= 0 ? result : 0); 
		return result;
	}
	
	public int countEntriesByEntriesLimit(final Entry firstEntry, final Entry lastEntry) throws DatabaseException {
		final String query = "SELECT COUNT(*) FROM entry e where e.project = :project AND e.id >= :minorID AND e.id <= :highestID";
		final Object[] parameter1 = new Object[]{"project", firstEntry.getProject()};
		final Object[] parameter2 = new Object[]{"minorID", firstEntry.getId()};
		final Object[] parameter3 = new Object[]{"highestID", lastEntry.getId()};
		
		return getIntegerValueByQuery(query, parameter1, parameter2, parameter3);
	}
	
	public int countEntriesByRevisionsLimit(final Entry firstEntry, final Entry lastEntry) throws DatabaseException {
		final String query = "SELECT COUNT(*) FROM entry e where e.project = :project AND e.revision >= :minorID AND e.revision <= :highestID";
		final Object[] parameter1 = new Object[]{"project", firstEntry.getProject()};
		final Object[] parameter2 = new Object[]{"minorID", firstEntry.getRevision()};
		final Object[] parameter3 = new Object[]{"highestID", lastEntry.getRevision()};
		
		return getIntegerValueByQuery(query, parameter1, parameter2, parameter3);
	}
	
	public int getEntrySequenceNumber(final Entry entry) throws DatabaseException{
		final String query = "SELECT COUNT(*) FROM entry e where e.author = :author AND e.id <= :entryID";
		final Object[] parameter1 = new Object[]{"author", entry.getAuthor()};
		final Object[] parameter2 = new Object[]{"entryID", entry.getId()};
		
		return getIntegerValueByQuery(query, parameter1, parameter2);
	}
	
	public Date getEntryDateFromRevision(final Project project, final long revision) throws DatabaseException {
		
		final EntityManager manager = DatabaseManager.getDatabaseSession();
		
		final Query q = manager.createQuery("SELECT entry FROM entry entry WHERE entry.revision = :revision AND entry.project = :project");
		q.setParameter("revision", revision);
		q.setParameter("project", project);
		try {
			final Entry entry = (Entry) q.getSingleResult();
			return entry.getDate();
		} catch (NoResultException e) {
			return null;
		} catch (javax.persistence.PersistenceException e){
			if(e.getCause().getCause().getMessage().equalsIgnoreCase("Unknown database")){
				throw new UnableToReachDatabaseException(e.getCause().getCause().getMessage());
			}

			if(e.getCause().getCause().getMessage().contains("Access denied for user")){
				throw new AccessDeniedException(e.getCause().getCause().getMessage());
			}
			
			return null;
		}
	}

	public List<Entry> getAllEntriesWithinEntries(final Entry firstEntry, final Entry lastEntry) throws DatabaseException {
		final String query = "SELECT entry FROM entry entry WHERE entry.project = :project AND entry.id >= :lowestID AND entry.id <= :highestID";
		final Object[] parameter1 = new Object[]{"project", firstEntry.getProject()};
		final Object[] parameter2 = new Object[]{"lowestID", firstEntry.getId()};
		final Object[] parameter3 = new Object[]{"highestID", lastEntry.getId()};
		
		return (List<Entry>) findByQuery(Entry.class, query, parameter1, parameter2, parameter3);
	}
	
	public List<Long> getAllRevisionsWithinEntries(final Entry firstEntry, final Entry lastEntry, int maxFiles) throws DatabaseException {
		String query = "SELECT entry.revision FROM entry entry WHERE " +
				"entry.project = :project AND " +
				"entry.id >= :lowestID AND " +
				"entry.id <= :highestID AND " +
				"entry.entryFiles IS NOT EMPTY";
		
		final Object[] parameter1 = new Object[]{"project", firstEntry.getProject()};
		final Object[] parameter2 = new Object[]{"lowestID", firstEntry.getId()};
		final Object[] parameter3 = new Object[]{"highestID", lastEntry.getId()};
		
		//TODO: Use Criteria instead of HQL (dynamic query)
		if (maxFiles == 0){
			return (List<Long>) findObjectsByQuery(query, parameter1, parameter2, parameter3);
		}
		else{
			query = query.concat(" AND entry.entryFiles.size <= :maxFiles");
			final Object[] parameter4 = new Object[]{"maxFiles", maxFiles};
			return (List<Long>) findObjectsByQuery(query, parameter1, parameter2, parameter3, parameter4);
		}
	}

	public Date getHighestEntryDateByEntries(final Entry firstEntry, final Entry lastEntry) throws DatabaseException {
		final String query = "SELECT entry FROM entry entry WHERE entry.date = (select max(entry.date) FROM entry entry where entry.project = :project AND entry.id >= :lowestID AND entry.id <= :highestID)";
		final Object[] parameter1 = new Object[]{"project", firstEntry.getProject()};
		final Object[] parameter2 = new Object[]{"lowestID", firstEntry.getId()};
		final Object[] parameter3 = new Object[]{"highestID", lastEntry.getId()};
		
		return findUnique(Entry.class, query, parameter1, parameter2, parameter3).getDate();	
	}

	public Date getMinorEntryDateByEntries(final Entry firstEntry, final Entry lastEntry) throws DatabaseException {
		final String query = "SELECT entry FROM entry entry WHERE entry.date = (select min(entry.date) FROM entry entry where entry.project = :project AND entry.id >= :lowestID AND entry.id <= :highestID)";
		final Object[] parameter1 = new Object[]{"project", firstEntry.getProject()};
		final Object[] parameter2 = new Object[]{"lowestID", firstEntry.getId()};
		final Object[] parameter3 = new Object[]{"highestID", lastEntry.getId()};
		Entry entry = findUnique(Entry.class, query, parameter1, parameter2, parameter3); 
		return entry.getDate();	
	}
	
	public List<Entry> getNonBlankEntriesByAuthorSortedByDate(final Project project, final Author author) throws DatabaseException{
		final String query = "SELECT entry FROM entry entry WHERE " +
				"entry.project.id = :projectID AND " +
				"entry.author.id = :authorID AND " +
				"entry.entryFiles IS NOT EMPTY " + 
				"ORDER BY entry.date";
		final Object[] parameter1 = new Object[]{"projectID", project.getId()};
		final Object[] parameter2 = new Object[]{"authorID", author.getId()};
		
		return (List<Entry>) findByQuery(Entry.class, query, parameter1, parameter2);
	}
	
	public List<Entry> getEntriesLimitedByNumFiles(final Project project, final int numFiles) throws DatabaseException{
		final String query = "SELECT entry FROM entry entry WHERE " +
		"entry.project.id = :projectID AND " +
		"entry.entryFiles IS NOT EMPTY AND " +
		"entry.entryFiles.size <= :size";
		
		final Object[] parameter1 = new Object[]{"projectID", project.getId()};
		final Object[] parameter2 = new Object[]{"size", numFiles};

		return (List<Entry>) findByQuery(Entry.class, query, parameter1, parameter2);
	}

	public int getAuthorEntrySequenceNumber(final Entry entry) throws DatabaseException {
		final String query = "SELECT COUNT(*) FROM entry e where e.author = :author AND e.id <= :entryID";
		final Object[] parameter1 = new Object[]{"author", entry.getAuthor()};
		final Object[] parameter2 = new Object[]{"entryID", entry.getId()};
		
		return getIntegerValueByQuery(query, parameter1, parameter2);
	}

}