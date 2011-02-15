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
 *  =======================
 *  EntriesTransformer.java
 *  =======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  Pedro Treccani, Jean Costa;
 *  
 */


package br.ufpa.linc.xflow.cm.transformations;

import java.util.HashSet;
import java.util.List;

import br.ufpa.linc.xflow.cm.info.Artifact;
import br.ufpa.linc.xflow.cm.info.Commit;
import br.ufpa.linc.xflow.cm.transformations.loc.LOCProcessor;
import br.ufpa.linc.xflow.data.dao.AuthorDAO;
import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.dao.FolderDAO;
import br.ufpa.linc.xflow.data.dao.ObjFileDAO;
import br.ufpa.linc.xflow.data.database.DatabaseManager;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Folder;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.FileUtil;


public class EntriesTransformer {

	private HashSet<String> filesCache;	
	private Entry currentlyProcessedEntry;
	
	private void checkOperationType(final Artifact node) throws DatabaseException{

		if (node.getArtifactKind().equals("FILE")){
			switch (node.getChangeType()) {
			

			/*
			 * Replaced files are not mapped anymore. They're considered ADDED FILES.
			 */
			
//		case 'R':
//			
//			ObjFile originalFile = FileUtil.checkFile()
//			
//			ObjFile replacedFile = new ObjFile();
//			replacedFile.setTotalLinesOfCode(originalFile.getTotalLinesOfCode());
//			replacedFile.setRemovedLinesOfCode(0);
//			replacedFile.setAddedLinesOfCode(0);
//			replacedFile.setModifiedLinesOfCode(0);
//			replacedFile.setPath(node.getTargetPath());
//			replacedFile.setOperationType('R');
//			replacedFile.setEntry(entry);
//			FileUtil.extractNameAndExtension(replacedFile);
//			new ObjFileDAO().insert(replacedFile);
//			entry.getEntryFiles().add(replacedFile);
//			
//			break;
			
			case 'A':
				
				filesCache.add(node.getTargetPath());
				
				ObjFile addedFile = new ObjFile();
				addedFile.setPath(node.getTargetPath());
				addedFile.setEntry(currentlyProcessedEntry);
				addedFile.setOperationType('A');
				addedFile.setSourceCode(node.getSourceCode());
				FileUtil.extractNameAndExtension(addedFile);
				LOCProcessor.extractCodeInfo(addedFile);
				FileUtil.buildFilePath(currentlyProcessedEntry.getProject(), addedFile);
				new ObjFileDAO().insert(addedFile);
				break;

			case 'M':
				
				boolean newFileFlag = true;
				ObjFile file = null;
				
				if(filesCache.contains(node.getTargetPath())){
					newFileFlag = false;
				}
				else{
					file = FileUtil.checkFile(currentlyProcessedEntry.getProject().getId(), node.getTargetPath());
					if(file != null){
						newFileFlag = false;
					}
				}
				
				if (newFileFlag){
					filesCache.add(node.getTargetPath());
					
					file = new ObjFile();
					file.setPath(node.getTargetPath());
					file.setOperationType('A');
					file.setEntry(currentlyProcessedEntry);
					file.setSourceCode(node.getSourceCode());
					file.setDiffCode(node.getDiffCode());
					FileUtil.extractNameAndExtension(file);
					LOCProcessor.extractCodeInfo(file);
					FileUtil.buildFilePath(currentlyProcessedEntry.getProject(), file);
					new ObjFileDAO().insert(file);
					break;
				}
				else {
					ObjFile modifiedFile = new ObjFile();
					modifiedFile.setPath(node.getTargetPath());
					modifiedFile.setOperationType('M');
					modifiedFile.setEntry(currentlyProcessedEntry);
					modifiedFile.setSourceCode(node.getSourceCode());
					modifiedFile.setDiffCode(node.getDiffCode());
					FileUtil.extractNameAndExtension(modifiedFile);
					LOCProcessor.extractCodeInfo(modifiedFile);
					new ObjFileDAO().insert(modifiedFile);
					break;
				}

			case 'D':
				ObjFile deletedFile = new ObjFile();
				ObjFile existingFile = FileUtil.checkFile(currentlyProcessedEntry.getProject().getId(), node.getTargetPath());
				if(existingFile != null){
					existingFile.setDeletedOn(currentlyProcessedEntry);
					new ObjFileDAO().update(existingFile);
					deletedFile.setRemovedLinesOfCode(existingFile.getTotalLinesOfCode());
				}
				else{
					deletedFile.setRemovedLinesOfCode(0);
				}
				deletedFile.setPath(node.getTargetPath());
				deletedFile.setTotalLinesOfCode(0);
				deletedFile.setOperationType('D');
				deletedFile.setAddedLinesOfCode(0);
				deletedFile.setModifiedLinesOfCode(0);
				deletedFile.setEntry(currentlyProcessedEntry);
				FileUtil.extractNameAndExtension(deletedFile);
				new ObjFileDAO().insert(deletedFile);
				break;
			}
		}
		else{
			if (node.getArtifactKind().equals("DIR")){
				switch (node.getChangeType()) {
				case 'D':
					Folder deletedFolder = new FolderDAO().findFolderByPath(currentlyProcessedEntry.getProject(), node.getTargetPath());
					if(deletedFolder != null){
						deletedFolder.setDeletedOn(currentlyProcessedEntry);
						new FolderDAO().update(deletedFolder);
					}
					break;
				}
			}
		}
	}


	private void checkOperationTypeWithoutLOC(final Artifact node) throws DatabaseException{

		if (node.getArtifactKind().equals("FILE")){
			switch (node.getChangeType()) {

			case 'R':

			case 'A':
				ObjFile addedFile = new ObjFile();
				addedFile.setPath(node.getTargetPath());
				addedFile.setEntry(currentlyProcessedEntry);
				addedFile.setOperationType('A');
				FileUtil.extractNameAndExtension(addedFile);
				FileUtil.buildFilePath(currentlyProcessedEntry.getProject(), addedFile);
				new ObjFileDAO().insert(addedFile);
				break;

			case 'M':
				
				boolean newFileFlag = true;
				ObjFile file = null;
				
				if(filesCache.contains(node.getTargetPath())){
					newFileFlag = false;
				}
				else{
					file = FileUtil.checkFile(currentlyProcessedEntry.getProject().getId(), node.getTargetPath());
					if(file != null){
						newFileFlag = false;
					}
				}
				
				if (newFileFlag){
					file = new ObjFile();
					file.setPath(node.getTargetPath());
					file.setOperationType('A');
					file.setEntry(currentlyProcessedEntry);
					FileUtil.extractNameAndExtension(file);
					FileUtil.buildFilePath(currentlyProcessedEntry.getProject(), file);
					new ObjFileDAO().insert(file);
					break;
				}
				else {
					ObjFile modifiedFile = new ObjFile();
					modifiedFile.setPath(node.getTargetPath());
					modifiedFile.setOperationType('M');
					modifiedFile.setEntry(currentlyProcessedEntry);
					FileUtil.extractNameAndExtension(modifiedFile);
					new ObjFileDAO().insert(modifiedFile);
					break;
				}

			case 'D':
				ObjFile deletedFile = new ObjFile();
				ObjFile existingFile = FileUtil.checkFile(currentlyProcessedEntry.getProject().getId(), node.getTargetPath());
				if(existingFile != null){
					existingFile.setDeletedOn(currentlyProcessedEntry);
					new ObjFileDAO().update(existingFile);
				}
				deletedFile.setPath(node.getTargetPath());
				deletedFile.setOperationType('D');
				deletedFile.setEntry(currentlyProcessedEntry);
				FileUtil.extractNameAndExtension(deletedFile);
				new ObjFileDAO().insert(deletedFile);
				break;

			}
		}
		else{
			if (node.getArtifactKind().equals("DIR")){
				switch (node.getChangeType()) {
				case 'D':
					Folder deletedFolder = new FolderDAO().findFolderByPath(currentlyProcessedEntry.getProject(), node.getTargetPath());
					if(deletedFolder != null){
						deletedFolder.setDeletedOn(currentlyProcessedEntry);
						new FolderDAO().update(deletedFolder);
					}
					break;
				}
			}
		}
	}


	public void transformData(final Project project, final List<Commit> commits) throws DatabaseException {
		
		filesCache = new HashSet<String>();
		for (int i = 0; i < commits.size(); i++) {

			final Commit commit = commits.get(i);

			System.out.print("Transforming commit "+commit.getRevisionNbr()+"\n");

			Author author = new AuthorDAO().findAuthorByName(project, commit.getAuthorName());

			if (author == null){
				author = new Author(commit.getAuthorName(), commit.getDate());
				author.setProject(project);
				new AuthorDAO().insert(author);
			}
			else{
				author.setLastContribution(commit.getDate());
				new AuthorDAO().update(author);
			}

			currentlyProcessedEntry = new Entry();
			currentlyProcessedEntry.setRevision(commit.getRevisionNbr());
			currentlyProcessedEntry.setDate(commit.getDate());
			if(commit.getLogMessage() == null){
				currentlyProcessedEntry.setComment(" ");
			}
			else{
				currentlyProcessedEntry.setComment(commit.getLogMessage());
			}
			currentlyProcessedEntry.setAuthor(author);
			currentlyProcessedEntry.setProject(project);
			new EntryDAO().insert(currentlyProcessedEntry);
			DatabaseManager.getDatabaseSession().clear();

			if(project.isCodeDownloadEnabled()){
				for (Artifact node : commit.getArtifacts()) {
					if(node != null){
						checkOperationType(node);
					}
					DatabaseManager.getDatabaseSession().clear();
				}
			}
			else{
				for (Artifact node : commit.getArtifacts()) {
					if(node != null){
						checkOperationTypeWithoutLOC(node);
					}
					DatabaseManager.getDatabaseSession().clear();
				}
			}
		}
	}

}
