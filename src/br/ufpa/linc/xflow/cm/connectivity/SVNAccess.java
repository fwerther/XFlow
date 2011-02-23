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
 *  ==============
 *  SVNAccess.java
 *  ==============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  Jean Costa, Pedro Treccani;
 *  
 */


package br.ufpa.linc.xflow.cm.connectivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import br.ufpa.linc.xflow.cm.info.Artifact;
import br.ufpa.linc.xflow.cm.info.Commit;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.cm.svn.SVNAuthenticationException;
import br.ufpa.linc.xflow.exception.cm.svn.SVNConnectionException;
import br.ufpa.linc.xflow.exception.cm.svn.SVNProtocolNotSupportedException;
import br.ufpa.linc.xflow.exception.cm.svn.SVNUnreachableArtifactException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.Filter;


public class SVNAccess extends Access {

	private SVNRepository repository;
	private ISVNAuthenticationManager authManager;
	private HashMap<String, String> nodeKindCache;
	
	private boolean forceTemporalConsistencyEnabled;
	private List<SVNLogEntry> chronologicalOrderedEntries;
	
	public SVNAccess(){
		// Empty constructor.
	}
	
	public SVNAccess(final String url, final String username, final String password, final Filter filter) {
		super(url, username, password, filter);
	}
	
	public boolean isForceTemporalConsistencyEnabled() {
		return forceTemporalConsistencyEnabled;
	}

	public void setForceTemporalConsistencyEnabled(final boolean forceTemporalConsistencyEnabled) {
		this.forceTemporalConsistencyEnabled = forceTemporalConsistencyEnabled;
	}

	private void setupLibrary() {
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
	}


	private void authentication() throws SVNProtocolNotSupportedException {
		setupLibrary();     
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(this.getUrl()));
		} catch (SVNException svne) {
			throw new SVNProtocolNotSupportedException();
		}
		authManager = SVNWCUtil.createDefaultAuthenticationManager(this.getUsername(), this.getPassword());
		repository.setAuthenticationManager(authManager);
	}


	private String getFileOnRepository(final String filePath, final long revision) throws SVNException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		repository.getFile(filePath, revision, null, baos);
		return baos.toString();
	}


	private String doDiff(final String url, final String filePath, final long revision1, final long revision2, final boolean recursive) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		SVNDiffClient diffClient = new SVNDiffClient(authManager, null);
		try {
			diffClient.doDiff(SVNURL.parseURIEncoded(url + filePath), SVNRevision.create(revision1), SVNURL.parseURIEncoded(url + filePath), SVNRevision.create(revision2), org.tmatesoft.svn.core.SVNDepth.UNKNOWN, true, result);
			repository.closeSession();
		} catch (SVNException ex) {
			repository.closeSession();
		}

		return result.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Commit> collectData(final long startRevision, long endRevision, final boolean downloadFiles) throws SVNProtocolNotSupportedException, SVNUnreachableArtifactException, SVNConnectionException, SVNAuthenticationException {
		authentication();
		List<SVNLogEntry> logEntries = null;
		if(nodeKindCache == null){
			nodeKindCache = new HashMap<String, String>();
		}
		ArrayList<Commit> commitList = new ArrayList<Commit>();
		try {
			if(forceTemporalConsistencyEnabled){
				if(chronologicalOrderedEntries == null){
					chronologicalOrderedEntries = (List<SVNLogEntry>) repository.log(new String[]{""}, null, 0, -1, true, true);
					Collections.sort(chronologicalOrderedEntries);
				}
				logEntries = new ArrayList<SVNLogEntry>();
				endRevision = (endRevision > chronologicalOrderedEntries.size()-1 ? chronologicalOrderedEntries.size()-1 : endRevision); 
				for (long i = startRevision; i <= endRevision; i++) {
					logEntries.add(chronologicalOrderedEntries.get((int) i));
				}
			}
			else logEntries = (List<SVNLogEntry>) repository.log(new String[]{""}, null, startRevision, endRevision, true, true);

			for (SVNLogEntry logEntry : logEntries) {
				System.out.print("Downloading entry "+logEntry.getRevision()+"\n");
				Commit commit = new Commit();
				commit.setArtifacts(new ArrayList<Artifact>());
				commit.setRevisionNbr(logEntry.getRevision());
				commit.setDate(logEntry.getDate());
				commit.setLogMessage(logEntry.getMessage());
				if(logEntry.getAuthor() == null){
					commit.setAuthorName("null");
				}
				else {
					commit.setAuthorName(logEntry.getAuthor());
				}

				for (SVNLogEntryPath entryPath : (Collection<SVNLogEntryPath>) logEntry.getChangedPaths().values()) {
					final String nodeKind;
					final Artifact node;

					try {
						if(nodeKindCache.containsKey(entryPath.getPath())){
							nodeKind = nodeKindCache.get(entryPath.getPath());
						}
						else{
							SVNNodeKind svnNodeKind = repository.checkPath(entryPath.getPath(), logEntry.getRevision());
							nodeKind = svnNodeKind.toString();
							nodeKindCache.put(entryPath.getPath(), svnNodeKind.toString());
						}

						if (nodeKind.equals("dir")) {
							node = new Artifact();
							node.setArtifactKind("DIR");
							node.setTargetPath(entryPath.getPath());
							node.setChangeType(entryPath.getType());
							commit.getArtifacts().add(node);
						}
						else {
							if (getFilter().match(entryPath.getPath())){
								node = new Artifact();
								node.setArtifactKind("FILE");
								node.setTargetPath(entryPath.getPath());
								node.setChangeType(entryPath.getType());
								if (downloadFiles){
									Filter sourceCodeFilter = new Filter("java|c|cpp|h");
									if(sourceCodeFilter.match(entryPath.getPath())){
										String sourceCode;
										String diffCode;
										try {

											switch (node.getChangeType()) {

											case 'M':
												diffCode = doDiff(this.getUrl(), entryPath.getPath(), commit.getRevisionNbr()-1, commit.getRevisionNbr(), false);
												node.setDiffCode(diffCode);

											case 'A':
												sourceCode = getFileOnRepository(entryPath.getPath(), logEntry.getRevision());
												node.setSourceCode(sourceCode);
												break;

											case 'R':
												node.setChangeType('A');
												sourceCode = getFileOnRepository(entryPath.getPath(), logEntry.getRevision());
												node.setSourceCode(sourceCode);
												break;

											case 'D':
												node.setSourceCode(null);
												break;
											}

										} catch (SVNException svne){
											System.out.println(svne.getCause());
											System.err.println("error while fetching the file contents and properties: " + svne.getMessage());
											System.err.println("error on " + entryPath.getPath() + " revision " + logEntry.getRevision() + " ;");
											continue;
										}
									}
								}
								commit.getArtifacts().add(node);
							}
						}
					} catch (SVNException ex) {
						System.out.println(ex.getCause());
						System.err.println("Error while collecting node kind " + ex.getMessage());
						collectData(commit.getRevisionNbr(), endRevision, downloadFiles);
					}
				}
				commitList.add(commit);
			}
			repository.closeSession();
		} catch (SVNException svne) {
			Class<? extends Throwable> exceptionClass = svne.getClass();

			// AUTHENTICATION PROBLEMS
			if(exceptionClass == org.tmatesoft.svn.core.SVNAuthenticationException.class){
				throw new SVNAuthenticationException();
			}

			// CONNECTION PROBLEMS (e.g.: wrong repository path)
			if(svne.getCause() != null){
				if(svne.getCause().getClass() == IOException.class){
					throw new SVNConnectionException();
				}
			}

			System.out.println(svne.getCause());
			System.err.println("error while collecting log information for '" + this.getUrl() + "': " + svne.getMessage());
			repository.closeSession();
		}
		finally{
			logEntries = null;
			System.gc();
		}
		return commitList;
	}

	//TODO: TERMINANDO!!
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Commit> collectData(final Date startDate, final Date endDate, final boolean downloadFiles) throws SVNProtocolNotSupportedException, SVNUnreachableArtifactException, SVNConnectionException, SVNAuthenticationException {
		authentication();
		List<SVNLogEntry> logEntries = null;
		if(nodeKindCache == null){
			nodeKindCache = new HashMap<String, String>();
		}
		ArrayList<Commit> commitList = new ArrayList<Commit>();
		try {
			logEntries = (List<SVNLogEntry>) repository.log(new String[]{""}, null, 0, -1, true, true);
			Collections.sort(logEntries);
			for (Iterator<SVNLogEntry> entries = logEntries.iterator(); entries.hasNext();) {
				SVNLogEntry logEntry = entries.next();
				if(logEntry.getDate().compareTo(startDate) < 0){
					continue;
				}
				else if (logEntry.getDate().compareTo(endDate) > 0){
					break;
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(logEntry.getDate());
				System.out.print("Downloading changes made on "+logEntry.getDate()+"\n");
				Commit commit = new Commit();
				commit.setArtifacts(new ArrayList<Artifact>());
				commit.setRevisionNbr(logEntry.getRevision());
				commit.setDate(logEntry.getDate());
				commit.setLogMessage(logEntry.getMessage());
				if(logEntry.getAuthor() == null){
					commit.setAuthorName("null");
				}
				else {
					commit.setAuthorName(logEntry.getAuthor());
				}
				if (logEntry.getChangedPaths().size() > 0) {
					Set<SVNLogEntryPath> changedPathsSet = logEntry.getChangedPaths().keySet();
					for (Iterator<SVNLogEntryPath> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
						String nodeKind = null;
						Artifact node = null;
						SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
						try {
							if(nodeKindCache.containsKey(entryPath.getPath())){
								nodeKind = nodeKindCache.get(entryPath.getPath());
							}
							else{
								SVNNodeKind svnNodeKind = repository.checkPath(entryPath.getPath(), logEntry.getRevision());
								nodeKind = svnNodeKind.toString();
								nodeKindCache.put(entryPath.getPath(), svnNodeKind.toString());
							}
							
							if (nodeKind == "dir") {
								node = new Artifact();
								node.setArtifactKind("DIR");
								node.setTargetPath(entryPath.getPath());
								node.setChangeType(entryPath.getType());
								commit.getArtifacts().add(node);
							}
							else {
								if (getFilter().match(entryPath.getPath())){
									node = new Artifact();
									node.setArtifactKind("FILE");
									node.setTargetPath(entryPath.getPath());
									node.setChangeType(entryPath.getType());
									if (downloadFiles){
										Filter sourceCodeFilter = new Filter("java|c|cpp|h");
										if(sourceCodeFilter.match(entryPath.getPath())){
											String sourceCode;
											String diffCode;
											try {

												switch (node.getChangeType()) {

												case 'M':
													diffCode = doDiff(this.getUrl(), entryPath.getPath(), commit.getRevisionNbr()-1, commit.getRevisionNbr(), false);
													node.setDiffCode(diffCode);

												case 'A':
													sourceCode = getFileOnRepository(entryPath.getPath(), logEntry.getRevision());
													node.setSourceCode(sourceCode);
													break;

												case 'R':
													node.setChangeType('A');
													sourceCode = getFileOnRepository(entryPath.getPath(), logEntry.getRevision());
													node.setSourceCode(sourceCode);
													break;

												case 'D':
													node.setSourceCode(null);
													break;
												}

											} catch (SVNException svne){
												System.out.println(svne.getCause());
												System.err.println("error while fetching the file contents and properties: " + svne.getMessage());
												System.err.println("error on " + entryPath.getPath() + " revision " + logEntry.getRevision() + " ;");
												continue;
											}
										}
									}
									commit.getArtifacts().add(node);
								}
							}
						} catch (SVNException ex) {
							System.out.println(ex.getCause());
							System.err.println("Error while collecting node kind " + ex.getMessage());
//							collectData(commit.getRevisionNbr(), endRevision, downloadFiles);
						}
						finally{
							node = null;
						}
					}
					commitList.add(commit);
				}
			}
			repository.closeSession();
		} catch (SVNException svne) {
			Class<? extends Throwable> exceptionClass = svne.getClass();
			
			// AUTHENTICATION PROBLEMS
			if(exceptionClass == org.tmatesoft.svn.core.SVNAuthenticationException.class){
				throw new SVNAuthenticationException();
			}

			// CONNECTION PROBLEMS (e.g.: wrong repository path)
			if(svne.getCause() != null){
				if(svne.getCause().getClass() == IOException.class){
					throw new SVNConnectionException();
				}
			}
			
			System.out.println(svne.getCause());
			System.err.println("error while collecting log information for '" + this.getUrl() + "': " + svne.getMessage());
			repository.closeSession();
		}
		finally{
			logEntries = null;
			System.gc();
		}
		return commitList;
	}
	
	@SuppressWarnings("unchecked")
	public boolean checkForDateInconsistencies(final Project project) throws DatabaseException, SVNException, SVNProtocolNotSupportedException{
		authentication();
		
		boolean inconsistency = false;
		final Collection<SVNLogEntry> logEntries = repository.log(new String[]{""}, null, project.getLastRevision()+1, -1, true, true);
		final Entry lastEntry = new EntryDAO().findEntryFromRevision(project, project.getLastRevision());

		for (SVNLogEntry svnLogEntry : logEntries) {
			if(svnLogEntry.getDate().compareTo(lastEntry.getDate()) < 0){
				inconsistency = true;
				break;
			}
		}
		
		return inconsistency;
	}
}
