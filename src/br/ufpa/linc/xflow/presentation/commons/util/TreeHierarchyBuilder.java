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
 *  =========================
 *  TreeHierarchyBuilder.java
 *  =========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.commons.util;

import java.util.ArrayList;

import prefuse.data.Node;
import prefuse.data.Tree;
import br.ufpa.linc.xflow.data.dao.FolderDAO;
import br.ufpa.linc.xflow.data.dao.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Folder;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class TreeHierarchyBuilder {
	
	public static Tree createTreeMapGraph(final Analysis analysis, long sequence) throws DatabaseException{

		final ArrayList<Folder> folders;
		
		if(AbstractVisualization.getCurrentAnalysis().isTemporalConsistencyForced()){
			folders = new FolderDAO().findRootFoldersUntilSequence(analysis.getProject(), sequence);
		} else {
			folders = new FolderDAO().findRootFoldersUntilRevision(analysis.getProject(), sequence);
		}
		
		Tree tree = new Tree();
	    tree.addColumn("name", String.class);
	    tree.addColumn("type", String.class);
	    tree.addColumn("id", long.class);
		
		Node root = tree.addRoot();
		for (Folder folder : folders) {
			Node leaf = tree.addChild(root);
			leaf.set("name", folder.getName());
			leaf.set("type", "folder");
			leaf.set("id", folder.getId());
			ArrayList<Folder> subfolders = new FolderDAO().findSubFoldersUntilRevision(folder.getId(), sequence);
			ArrayList<ObjFile> files = new ObjFileDAO().getFilesFromFolderUntilRevision(folder.getId(), sequence);
			extractLeafs(tree, leaf, subfolders, sequence);
			extractFiles(tree, leaf, files);
		}
		
		return tree;
	}
	
	private static void extractLeafs(Tree tree, Node parent, ArrayList<Folder> folders, long sequence) throws DatabaseException {
		for (Folder folder : folders) {
			Node leaf = tree.addChild(parent);
			leaf.set("name", folder.getName());
			leaf.set("type", "folder");
			leaf.set("id", folder.getId());
			
			final ArrayList<Folder> subfolders;
			final ArrayList<ObjFile> files;
			
			if(AbstractVisualization.getCurrentAnalysis().isTemporalConsistencyForced()){
				subfolders = new FolderDAO().findSubFoldersUntilSequence(folder.getId(), sequence);
				files = new ObjFileDAO().getFilesFromFolderUntilSequence(folder.getId(), sequence);
			} else {
				subfolders = new FolderDAO().findSubFoldersUntilRevision(folder.getId(), sequence);
				files = new ObjFileDAO().getFilesFromFolderUntilRevision(folder.getId(), sequence);
			}
			
			if(subfolders.size() > 0){
				extractLeafs(tree, leaf, subfolders, sequence);
			}
			
			extractFiles(tree, leaf, files);
		}
	}
	
	private static void extractFiles(Tree tree, Node parent, ArrayList<ObjFile> files){
		for (ObjFile file : files) {
			Node fileLeaf = tree.addChild(parent);
			fileLeaf.set("name", file.getPath());
			fileLeaf.set("type", "file");
			fileLeaf.set("id", file.getId());
		}
	}

}
