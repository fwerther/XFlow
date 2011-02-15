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
 *  AnalysisDAO.java
 *  ================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.dao;

import java.util.Collection;

import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class AnalysisDAO extends BaseDAO<Analysis> {


	@Override
	public final Analysis findById(final Class<Analysis> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	public final boolean insert(final Analysis analysis) throws DatabaseException {
		return super.insert(analysis);
	}

	@Override
	public final boolean remove(final Analysis analysis) throws DatabaseException {
		return super.remove(analysis);
	}

	@Override
	public final boolean update(final Analysis analysis) throws DatabaseException {
		return super.update(analysis);
	}

	@Override
	public final Collection<Analysis> findAll(final Class<? extends Analysis> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

}
