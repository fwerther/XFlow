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
 *  Access.java
 *  ===========
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  Jean Costa, Pedro Treccani;
 *  
 */

package br.ufpa.linc.xflow.cm.connectivity;

import java.util.ArrayList;
import java.util.Date;

import br.ufpa.linc.xflow.cm.info.Commit;
import br.ufpa.linc.xflow.exception.cm.CMException;
import br.ufpa.linc.xflow.util.Filter;


public abstract class Access {

	private String url;
	private String username;
	private String password;
	private Filter filter;


	public Access(){
		// Empty constructor.
	}
	
	public Access(final String url, final String username, final String password, final Filter filter) {
		this.url = url;
		this.username = username;
		this.password = password;
		this.filter = filter;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(final Filter filter) {
		this.filter = filter;
	}


	abstract public ArrayList<Commit> collectData(long startRevision, long endRevision, boolean downloadCode) throws CMException;

	abstract public ArrayList<Commit> collectData(Date startDate, Date endDate, boolean downloadCode) throws CMException;

}
