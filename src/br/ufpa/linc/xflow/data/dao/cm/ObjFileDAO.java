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
 *  ===============
 *  ObjFileDAO.java
 *  ===============
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

import br.ufpa.linc.xflow.data.dao.BaseDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Folder;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;


public class ObjFileDAO extends BaseDAO<ObjFile> {

	@Override
	public ObjFile findById(final Class<ObjFile> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	public boolean insert(final ObjFile file) throws DatabaseException {
		return super.insert(file);
	}

	@Override
	public boolean remove(final ObjFile file) throws DatabaseException {
		return super.remove(file);
	}

	@Override
	public boolean update(final ObjFile file) throws DatabaseException {
		return super.update(file);
	}
	
	@Override
	protected ObjFile findUnique(final Class<ObjFile> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<ObjFile> findByQuery(final Class<ObjFile> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	public Collection<ObjFile> findAll(final Class<? extends ObjFile> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	public ObjFile findFileByPathUntilRevision(final Project project, final long revision, final String filePath) throws DatabaseException {
		final String query = "SELECT f from file f where f.id = (select max(f.id) from file f where f.path = :filePath and f.entry.project = :project and f.entry.revision <= :revision)";
		final Object[] parameter1 = new Object[]{"filePath", filePath};
		final Object[] parameter2 = new Object[]{"project", project};
		final Object[] parameter3 = new Object[]{"revision", revision};
		
		return findUnique(ObjFile.class, query, parameter1, parameter2, parameter3);
	}
	
	public ObjFile findFileByPathUntilDate(final Project project, final Date date, final String filePath) throws DatabaseException {
		final String query = "SELECT f from file f where f.id = (select max(f.id) from file f where f.path = :filePath and f.entry.project = :project and f.entry.date <= :date)";
		final Object[] parameter1 = new Object[]{"filePath", filePath};
		final Object[] parameter2 = new Object[]{"project", project};
		final Object[] parameter3 = new Object[]{"date", date};
		
		return findUnique(ObjFile.class, query, parameter1, parameter2, parameter3);
	}
	
	public ObjFile findFileByPathUntilEntry(final Project project, final Entry entry, final String filePath) throws DatabaseException {
		final String query = "SELECT f from file f where f.id = (select max(f.id) from file f where f.path = :filePath and f.entry.project = :project and f.entry.id <= :entryID)";
		final Object[] parameter1 = new Object[]{"filePath", filePath};
		final Object[] parameter2 = new Object[]{"project", project};
		final Object[] parameter3 = new Object[]{"entryID", entry.getId()};
		
		return findUnique(ObjFile.class, query, parameter1, parameter2, parameter3);
	}
	
	public ObjFile findAddedFileByPathUntilEntry(final Project project, final Entry entry, final String filePath) throws DatabaseException {
		final String query = "SELECT f FROM file f WHERE f.id = " +
				"(SELECT MAX(f.id) FROM file f " +
				"JOIN f.entry AS entry " +
				"WHERE f.path = :filePath " +
				"AND entry.project.id = :project " +
				"AND entry.id <= :entryID " +
				"AND f.operationType = 'A')";
		
		final Object[] parameter1 = new Object[]{"filePath", filePath};
		final Object[] parameter2 = new Object[]{"project", project.getId()};
		final Object[] parameter3 = new Object[]{"entryID", entry.getId()};
		
		return findUnique(ObjFile.class, query, parameter1, parameter2, parameter3);
	}
	
	public int getFileLOCUntilRevision(final Project project, final long revision, final String filePath) throws DatabaseException{
		final String query = "SELECT f.totalLinesOfCode FROM file f WHERE f.id = " +
				"(SELECT MAX(f.id) FROM file f " +
				"JOIN f.entry AS entry " +
				"WHERE f.path = :filePath " +
				"AND entry.project.id = :project " +
				"AND entry.revision <= :revision)";

		final Object[] parameter1 = new Object[]{"filePath", filePath};
		final Object[] parameter2 = new Object[]{"project", project.getId()};
		final Object[] parameter3 = new Object[]{"revision", revision};
		
		return getIntegerValueByQuery(query,parameter1,parameter2,parameter3);
	}
	
	public int getFileLOCUntilDate(final Project project, final Date date, final String filePath) throws DatabaseException{
		final String query = "SELECT f from file f where f.id = (select max(f.id) from file f where f.path = :filePath and f.entry.project = :project and f.entry.date <= :date)";
		final Object[] parameter1 = new Object[]{"filePath", filePath};
		final Object[] parameter2 = new Object[]{"project", project};
		final Object[] parameter3 = new Object[]{"date", date};
		
		return findUnique(ObjFile.class, query, parameter1, parameter2, parameter3).getTotalLinesOfCode();
	}
	
	public ObjFile findFileByPath(final long projectID, final String path) throws DatabaseException {
		//final String query = "SELECT MAX(file.entry.id) from file file WHERE file.operationType = 'A' and file.path = :path and file.entry.project.id = :project";
		
		//This query is more efficient, since it does not require a
		//table join on project 
		final String query = "SELECT MAX(entry.id) FROM file AS file " +
				"JOIN file.entry as entry " +
				"WHERE file.operationType = 'A' " +
				"AND file.path = :path " +
				"AND entry.project.id = :project";
		final Object[] parameter1 = new Object[]{"path", path};
		final Object[] parameter2 = new Object[]{"project", projectID};
		final long entryID = getLongValueByQuery(query, parameter1, parameter2);
	
		final String subquery = "SELECT file from file file WHERE file.id = (select max(file.id) from file file where file.entry.id = :max and file.path = :path and file.operationType != 'D')";
		final Object[] parameter3 = new Object[]{"max", entryID};
		final Object[] parameter4 = new Object[]{"path", path};
	
		return findUnique(ObjFile.class, subquery, parameter3, parameter4);
	}
	
	public ArrayList<ObjFile> getFilesFromEntryID(final long entryID) throws DatabaseException {
		final String query = "select distinct f from file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		return (ArrayList<ObjFile>) findByQuery(ObjFile.class, query, parameter1);
	}
	
	public ArrayList<ObjFile> getFilesFromRevision(final long revision) throws DatabaseException {
		final String query = "select distinct f from file f where f.entry.revision = :revision";
		final Object[] parameter1 = new Object[]{"revision", revision};
		
		return (ArrayList<ObjFile>) findByQuery(ObjFile.class, query, parameter1);
	}
	
	public ArrayList<ObjFile> getFilesFromFolderUntilRevision(final long id, final long revision) throws DatabaseException {
		final String query = "select f from file f where f.parentFolder.id = :parentId and f.operationType = 'A' and (f.deletedOn is null OR f.deletedOn.id > :revision)";
		final Object[] parameter1 = new Object[]{"parentId", id};
		final Object[] parameter2 = new Object[]{"revision", revision};
		
		return (ArrayList<ObjFile>) findByQuery(ObjFile.class, query, parameter1, parameter2);
	}

	public ArrayList<ObjFile> getFilesFromFolderUntilSequence(Folder folder, long sequence) throws DatabaseException {
		final Entry entry = new EntryDAO().findEntryFromSequence(folder.getEntry().getProject(), sequence);
		final String query = "select f from file f where f.parentFolder.id = :parentID and f.operationType = 'A' and f.entry.id <= :entryID and (f.deletedOn is null OR f.deletedOn.id > :entryID)";
		final Object[] parameter1 = new Object[]{"parentID", folder.getId()};
		final Object[] parameter2 = new Object[]{"entryID", entry.getId()};
		
		return (ArrayList<ObjFile>) findByQuery(ObjFile.class, query, parameter1, parameter2);
	}
	
	public int getLoCByFile(final ObjFile file) throws DatabaseException{
		final String query = "SELECT MAX(f.id) from file f where f = :file";
		final Object[] parameter1 = new Object[]{"file", file};
		
		return findUnique(ObjFile.class, query, parameter1).getTotalLinesOfCode();
	}

	public ArrayList<ObjFile> getFilesFromSequenceNumber(final Project project, final long sequenceNumber) throws DatabaseException {
		final String query = "select f from file f where f.entry.project = :project and f.entry.sequenceNumber = :sequenceNumber";
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"sequenceNumber", sequenceNumber};
		
		return (ArrayList<ObjFile>) findByQuery(ObjFile.class, query, parameter1, parameter2);
	}
	
	public List<String> getFilesPathFromSequenceNumberOrderedByRevision(final Project project, final long sequenceNumber) throws DatabaseException {
		final String query = "select DISTINCT f.path from file f where f.entry.project = :project and f.entry = (select entry from entry e where e = (select count(*)))";
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"sequenceNumber", sequenceNumber};
		
		return (List<String>) findObjectsByQuery(query, parameter1, parameter2);
	}
	
	public List<String> getFilesPathFromSequenceNumberOrderedBySequence(final Project project, final long sequenceNumber) throws DatabaseException {
		final String query = "select DISTINCT f.path from file f where f.entry.project = :project and f.entry = (select entry from entry e where )";
		final Object[] parameter1 = new Object[]{"project", project};
		final Object[] parameter2 = new Object[]{"sequenceNumber", sequenceNumber};
		
		return (List<String>) findObjectsByQuery(query, parameter1, parameter2);
	}
	
	public ArrayList<ObjFile> getEntryChangedFiles(final long entryID) throws DatabaseException {
		final String query = "select f from file f where f.entry.id = :entryID";
		final Object[] parameter1 = new Object[]{"entryID", entryID};
		
		return (ArrayList<ObjFile>) findByQuery(ObjFile.class, query, parameter1);
	}

}
