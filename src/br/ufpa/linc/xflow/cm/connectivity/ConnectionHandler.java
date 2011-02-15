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
 *  ======================
 *  ConnectionHandler.java
 *  ======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */


package br.ufpa.linc.xflow.cm.connectivity;

import java.util.List;

import br.ufpa.linc.xflow.cm.info.Commit;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.cm.CMException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;


public class ConnectionHandler {

	private final Access access;
	private boolean downloadCodeEnabled;
	
	public ConnectionHandler(final Project project){
		access = AccessFactory.createAccess(project);
	}
	
	public List<Commit> gatherData(final long startRevision, final long endRevision) throws CMException, DatabaseException {
		return access.collectData(startRevision, endRevision, this.downloadCodeEnabled);
	}
	
	public Access getAccess() {
		return access;
	}

	public boolean isDownloadCodeEnabled() {
		return downloadCodeEnabled;
	}

	public void setDownloadCodeEnabled(final boolean downloadCodeEnabled) {
		this.downloadCodeEnabled = downloadCodeEnabled;
	}
	
}
