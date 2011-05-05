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
 *  ==========
 *  Entry.java
 *  ==========
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity(name = "entry")
public class Entry {

	@Id
	@Column(name = "ENTRY_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "ENTRY_REV", nullable = false)
	private long revision;
	
	@ManyToOne
	@JoinColumn(name = "ENTRY_PROJECT", nullable = false)
	private Project project;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ENTRY_DATE", nullable = false)
	private Date date;
	
	@ManyToOne
	@JoinColumn(name = "ENTRY_AUTHOR", nullable = false)
	private Author author;
	
	@Lob
	@Column(name = "ENTRY_COMMENT", columnDefinition="LONGTEXT", nullable = false)
	private String comment;
	
	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL)
	private List<ObjFile> entryFiles;
	
	@Transient
	private List<Folder> entryFolders;
	
	
	public Entry() {
		this.entryFiles = new ArrayList<ObjFile>();
	}
	
	public Entry(final Project project) {
		this.entryFiles = new ArrayList<ObjFile>();
		this.project = project;
	}
	
	public Entry(final long revision, final Date date, final String comment, final Author author){
		this.revision = revision;
		this.date = date;
		this.author = author;
		this.comment = comment;
		this.entryFiles = new ArrayList<ObjFile>();
	}

	
	public long getId() {
		return id;
	}
	
	public void setId(long id){
		this.id = id;
	}

	public long getRevision() {
		return revision;
	}
	
	public void setRevision(final long revision) {
		this.revision = revision;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(final Date date) {
		this.date = date;
	}
	
	public Author getAuthor() {
		return author;
	}
	
	public void setAuthor(final Author author) {
		this.author = author;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Project getProject() {
		return project;
	}
	
	public void setProject(final Project project) {
		this.project = project;
	}

	public void setComment(final String comment) {
		this.comment = comment;
	}
	
	public List<ObjFile> getEntryFiles() {
		return entryFiles;
	}
	
	public void setEntryFiles(final List<ObjFile> modifiedFiles) {
		this.entryFiles = modifiedFiles;
	}
	
	public List<Folder> getEntryFolders() {
		return entryFolders;
	}
	
	public void setEntryFolders(final List<Folder> entryFolders) {
		this.entryFolders = entryFolders;
	}

	public String getListOfEntryFiles(){
		final String listOfFiles = new String();
		for (ObjFile file : entryFiles) {
			listOfFiles.concat(file.getPath()+"\n");
		}
		
		return listOfFiles;
	}
	
	public void addFiles(List<ObjFile> files){
		if (entryFiles.isEmpty()){
			entryFiles = new ArrayList<ObjFile>();
		}
		entryFiles.addAll(files);
	}
	
}
