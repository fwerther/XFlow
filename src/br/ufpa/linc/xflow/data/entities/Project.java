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
 *  ============
 *  Project.java
 *  ============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "project")
public class Project implements Serializable, Comparable<Project>{

	private static final long serialVersionUID = 7907150525824812352L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PRJ_ID")
	private long id;

	@Column(name = "PRJ_NAME", nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	private List<Author> authors;
	
	@Column(name = "PRJ_ANALYSIS")
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
	private List<Analysis> analysis;
	
	@Column(name = "PRJ_URL", nullable = false)
	private String url;
	
	@Column(name = "PRJ_USER", nullable = false)
	private String username;
	
	@Column(name = "PRJ_PASSWD", nullable = false)
	private String password;
	
	@Column(name = "PRJ_TYPE", nullable = false)
	private int repositoryType;
	
	@Column(name = "PRJ_FIRST_REVISION", nullable = false)
	private long firstRevision;
	
	@Column(name = "PRJ_LAST_REVISION", nullable = false)
	private long lastRevision;

	@Column(name = "PRJ_DETAILS")
	private String details;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "PRJ_DATE", nullable = false)
	private Date date;
	
	@Column(name = "CODE_DOWNLOAD_ENABLE", nullable = false)
	private boolean codeDownloadEnabled;
	
	@Column(name = "TEMPORAL_CONSISTENCY_FORCED", nullable = false)
	private boolean temporalConsistencyForced = false;
	
	public Project(){
		// Empty constructor.
	}
	
	public Project(final String name){
		this.authors = new ArrayList<Author>();
		this.name = name;
	}
	
	public long getId() {
		return id;
	}

        public void setName(final String name) {
            this.name = name;
        }
	
	public String getName() {
		return name;
	}
	
	public void setAuthors(final List<Author> authors) {
		this.authors = authors;
	}

	public List<Author> getAuthors() {
		return authors;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public int getRepositoryType() {
		return repositoryType;
	}

	public void setRepositoryType(final int repositoryType) {
		this.repositoryType = repositoryType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public long getFirstRevision() {
		return firstRevision;
	}

	public void setFirstRevision(final long firstRevision) {
		this.firstRevision = firstRevision;
	}

	public long getLastRevision() {
		return lastRevision;
	}

	public void setLastRevision(final long lastRevision) {
		this.lastRevision = lastRevision;
	}

	public void setDetails(final String projectDetails) {
		this.details = projectDetails;
	}

	public String getDetails() {
		return details;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setAnalysis(final List<Analysis> analysis) {
		this.analysis = analysis;
	}

	public List<Analysis> getAnalysis() {
		return analysis;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public boolean isCodeDownloadEnabled() {
		return codeDownloadEnabled;
	}

	public void setCodeDownloadEnabled(final boolean codeDownloadEnabled) {
		this.codeDownloadEnabled = codeDownloadEnabled;
	}
	
	public boolean isTemporalConsistencyForced() {
		return temporalConsistencyForced;
	}

	public void setTemporalConsistencyForced(final boolean temporalConsistencyForced) {
		this.temporalConsistencyForced = temporalConsistencyForced;
	}
	
	public List<Author> getAuthorsListByEntries(Entry initialEntry, Entry finalEntry) {
		final List<Author> authorsList = new ArrayList<Author>();
		if(isTemporalConsistencyForced()){
			for (Author author : this.getAuthors()) {
				if((author.getEntries().get(0).getId() >= initialEntry.getId()) && (author.getEntries().get(0).getId() <= finalEntry.getId())){
					authorsList.add(author);
				}
			}
		} else {
			for (Author author : this.getAuthors()) {
				if((author.getEntries().get(0).getRevision() >= initialEntry.getRevision()) && (author.getEntries().get(0).getRevision() <= finalEntry.getId())){
					authorsList.add(author);
				}
			}
		}
		
		return authorsList;
	}
	
	public Collection<String> getAuthorsStringList() {
		final ArrayList<String> authorsList = new ArrayList<String>();
		for (Author author : this.getAuthors()) {
			System.out.println(author.getName());
			authorsList.add(author.getName());	
		}
		
		return authorsList;
	}

	@Override
	public int compareTo(final Project compared) {
		if(this.id < compared.getId()){
			return -1;
		}
		else if(this.id == compared.getId()){
			return 0;
		}
		return 1;
	}


}
