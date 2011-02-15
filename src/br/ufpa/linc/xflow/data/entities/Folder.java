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
 *  ===========
 *  Folder.java
 *  ===========
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name = "folder")
public class Folder extends Resource implements Comparable<Folder>{

	private static final long serialVersionUID = 8302324702620512851L;

	@Column(name = "FULL_PATH")
	private String fullPath;

	@ManyToOne
	@JoinColumn(name = "FOLDER_ENTRY")
	private Entry entry;

	public Folder(){

	}

	public Folder(final String name) {
		super(name);
	}

	public Folder(final String name, final Folder parent) {
		super(name);
		this.parentFolder = parent;
	}

	public void setFullPath(final String fullPath) {
		this.fullPath = fullPath;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setEntry(final Entry entry) {
		this.entry = entry;
	}

	public Entry getEntry() {
		return entry;
	}

	@Override
	public int compareTo(final Folder compared) {
		if(this.id < compared.getId()){
			return -1;
		}
		else if(this.id == compared.getId()){
			return 0;
		}
		return 1;
	}
}
