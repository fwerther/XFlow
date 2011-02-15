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
 *  Author.java
 *  ===========
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity(name = "author")
public class Author {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "AUTH_ID")
	private long id;
	
	@OneToOne
	@JoinColumn(name = "AUTH_PROJECT", nullable = false)
	private Project project;
	
	@Column(name = "AUTH_NAME", nullable = false)
	private String name;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "AUTH_STARTDATE", nullable = false)
	private Date startDate;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "AUTH_LASTCONTRIB", nullable = false)
	private Date lastContribution;
	
	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Entry> entries = new ArrayList<Entry>();

	public Author() {
	}
	
	public Author(final String name){
		this.name = name;
	}
	
	public Author(final String name, final Date startDate){
		this.name = name;
		this.startDate = startDate;
		this.lastContribution = startDate;
	}
	

	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Date getLastContribution() {
		return lastContribution;
	}
	
	public List<Entry> getEntries() {
		return entries;
	}
	
	public void setLastContribution(final Date date) {
		this.lastContribution = date;
	}
	
	public void setProject(final Project project) {
		this.project = project;
	}

	public Project getProject() {
		return project;
	}
}
