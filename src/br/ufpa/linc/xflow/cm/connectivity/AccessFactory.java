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
 *  AccessFactory.java
 *  ==================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  Pedro Treccani, Jean Costa;
 *  
 */

package br.ufpa.linc.xflow.cm.connectivity;

import br.ufpa.linc.xflow.data.entities.Project;


public abstract class AccessFactory {

	public final static int SVN_REPOSITORY = 0;
	public final static int CVS_REPOSITORY = 1;
	
	public static Access createAccess(final Project project) {
		
		final Access access;
		
		switch(project.getRepositoryType()) {
		case SVN_REPOSITORY: access = new SVNAccess();
			((SVNAccess) access).setForceTemporalConsistencyEnabled(project.isTemporalConsistencyForced());
			break;
		default:
			access = null;
//		case 1: access = new CVSAccess(url,name,password); break;
		}
		
		return access;
	}
	
}
