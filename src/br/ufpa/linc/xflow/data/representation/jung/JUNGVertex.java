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
 *  ===============
 *  JUNGVertex.java
 *  ===============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.representation.jung;


public class JUNGVertex {

	private long id;
	private String name;
	
	public JUNGVertex(){
		// Empty constructor.
	}
	
	public JUNGVertex(final int id){
		this.id = id;
	}
	
	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString(){
		return name;
	}

}
