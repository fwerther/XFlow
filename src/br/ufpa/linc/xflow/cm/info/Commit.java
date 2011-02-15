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
 *  Commit.java
 *  ===========
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.cm.info;

import java.util.Comparator;
import java.util.Date;
import java.util.List;


public final class Commit implements Comparator<Object> {

	private List<Artifact> artifacts;
	private long revision;
	private String authorName;
	private Date date;
	private String comment;

	public Commit(){
		// Empty constructor.
	}

	public Commit(final long revision, final String authorName, final Date date, final String comment){
		this.revision = revision;
		this.date = date;
		this.comment = comment;
		this.authorName = authorName;
	}


	public final List<Artifact> getArtifacts() {
		return artifacts;
	}

	public final void setArtifacts(final List<Artifact> artifactsList) {
		this.artifacts = artifactsList;
	}

	public final long getRevisionNbr() {
		return revision;
	}

	public final void setRevisionNbr(final long revisionNbr) {
		this.revision = revisionNbr;
	}

	public final String getAuthorName() {
		return authorName;
	}

	public final void setAuthorName(final String authorName) {
		this.authorName = authorName;
	}   

	public final String getLogMessage() {
		return comment;
	}

	public final void setLogMessage(final String logMessage) {
		this.comment = logMessage;
	}

	public final Date getDate() {
		return date;
	}

	public final void setDate(final Date date) {
		this.date = date;
	}

	public final int compare(final Object o1, final Object o2) {
		Long m1 = ((Commit) o1).getRevisionNbr();
		Long m2 = ((Commit) o2).getRevisionNbr();
		return m1.compareTo(m2);
	}
}
