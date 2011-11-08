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

import java.util.List;

import br.ufpa.linc.xflow.cm.info.Artifact;
import br.ufpa.linc.xflow.cm.info.Commit;
import br.ufpa.linc.xflow.cm.transformations.artifact.ArtifactTransformer;
import br.ufpa.linc.xflow.data.dao.cm.AuthorDAO;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class EntriesTransformer {

	public void transformData(final Project project, final List<Commit> commits) throws DatabaseException {
		
		final ArtifactTransformer artifactTransformer = ArtifactTransformer.createInstance(project.isCodeDownloadEnabled());
		final EntryDAO entryDAO = new EntryDAO();
		
		for (int i = 0; i < commits.size(); i++) {

			final Commit commit = commits.get(i);
			if(commit.getRevisionNbr() != 0){
				System.out.print("Transforming commit "+commit.getRevisionNbr()+"\n");
			} else {
				System.out.println("Transforming commit "+commit.getLogMessage()+" "+commit.getDate()+"\n");
			}

			if(commit.getArtifacts().isEmpty()){
				System.out.println("Commit does not match data filter");
				System.out.println("Ignoring commit...");
			}
			else{		
				/*
				 *  AUTHOR INFO
				 */
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
	
				/*
				 * ENTRY INFO
				 */
				final Entry currentlyProcessedEntry = new Entry();
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
				entryDAO.insert(currentlyProcessedEntry);
				
				/*
				 * ARTIFACTS INFO
				 */
				artifactTransformer.setProcessedEntry(currentlyProcessedEntry);
				for (Artifact node : commit.getArtifacts()) {
					if(node.getArtifactKind().equals("FILE")){
						ObjFile file = artifactTransformer.gatherArtifactInfo(node);
						new ObjFileDAO().insert(file);
					} else {
						artifactTransformer.gatherFolderInfo(node);
					}
				}
				
				//Update entry (due to added files)
				entryDAO.update(currentlyProcessedEntry);
			}
		}
	}

}
