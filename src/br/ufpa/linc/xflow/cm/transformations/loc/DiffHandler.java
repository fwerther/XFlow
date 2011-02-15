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
 *  ================
 *  DiffHandler.java
 *  ================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.cm.transformations.loc;

import br.ufpa.linc.xflow.data.entities.ObjFile;

public abstract class DiffHandler {

	protected int addedLines;
	protected int modifiedLines;
	protected int deletedLines;
	
	public abstract void gatherFileChanges(final ObjFile file);

	public int getAddedLines() {
		return addedLines;
	}

	public void setAddedLines(final int addedLines) {
		this.addedLines = addedLines;
	}

	public int getModifiedLines() {
		return modifiedLines;
	}

	public void setModifiedLines(final int modifiedLines) {
		this.modifiedLines = modifiedLines;
	}

	public int getDeletedLines() {
		return deletedLines;
	}

	public void setDeletedLines(final int deletedLines) {
		this.deletedLines = deletedLines;
	}
	
}
