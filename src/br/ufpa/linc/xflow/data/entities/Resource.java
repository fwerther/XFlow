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
 *  Resource.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

@MappedSuperclass
public abstract class Resource implements Serializable {
	
	private static final long serialVersionUID = 1569099270212350012L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RESOURCE_ID")
	protected long id;
	
	@OneToOne(optional = true)
	@JoinColumn(name = "PARENT_FOLDER")
	protected Folder parentFolder;
	
	@Column(name = "RESOURCE_NAME", nullable = false)
	private String name;
	
	@OneToOne
	@JoinColumn(name = "DELETED_ON")
	private Entry deletedOn = null;
	
	public Resource() {
	}
	
	public Resource(final int id) {
		this.id = id;
	}
	
	public Resource(final String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
	
	public Folder getParent() {
		return parentFolder;
	}

	public void setParent(final Folder parent) {
		this.parentFolder = parent;
	}

	public Entry getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(final Entry deletedOn) {
		this.deletedOn = deletedOn;
	}
	
}
