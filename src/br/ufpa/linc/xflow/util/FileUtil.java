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
 *  FileUtil.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.util;

import javax.persistence.EntityNotFoundException;

import br.ufpa.linc.xflow.data.dao.FolderDAO;
import br.ufpa.linc.xflow.data.dao.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Folder;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;




public abstract class FileUtil {
   
    public static ObjFile checkFile(final long projectID, final String filePath) throws DatabaseException{
        try{
        	final ObjFile file = new ObjFileDAO().findFileByPath(projectID, filePath);
            return file;
        } catch(EntityNotFoundException e){
            return null;
        }
    }
    
    public static Folder checkFolder(final Entry entry, final String path){
    	final FolderDAO folderDAO = new FolderDAO();
    	Folder folder = null;
		try {
			folder = folderDAO.findFolderByPath(entry.getProject(), path);
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
    	if(folder == null){
    		folder = new Folder();
    		folder.setFullPath(path);
    		final int lastSlash = path.lastIndexOf("/");
    		folder.setEntry(entry);
    		if(lastSlash == 0){
    			folder.setName(path.substring(1, path.length()));
    			folder.setParent(null);
    		}
    		else{
    			folder.setName(path.substring(lastSlash+1, path.length()));
    			folder.setParent(checkFolder(entry, path.substring(0, lastSlash)));
    		}
    		try {
				folderDAO.insert(folder);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
    	}
    	return folder;
    }
    
    public static void buildFilePath(final Project project, final ObjFile file) throws DatabaseException{
    	final String filePath = file.getPath();
    	if(filePath.lastIndexOf("/") == 0){
    		file.setParent(null);
    	}
    	else{
    		final int slashIndex = filePath.lastIndexOf("/");
    		final String directoryPath = filePath.substring(0, slashIndex);
    		file.setParent(checkFolder(file.getEntry(), directoryPath));
    	}
    }
   
	public static void extractNameAndExtension(ObjFile file) {

		final String[] fullPath = file.getPath().split("/");
		file.setName(fullPath[fullPath.length-1]);
		
		final String[] extension = fullPath[fullPath.length-1].split("\\.");
		file.setExtesion(extension[extension.length-1]);
		
	}
   
}