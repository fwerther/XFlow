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
 *  AddedFiles.java
 *  ===============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.entry;

import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.dao.EntryMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;


public final class AddedFiles extends EntryMetricModel {

	@Override
	public final void evaluate(Entry entry, EntryMetricValues table) throws DatabaseException {
		table.setEntryAddedFiles(new EntryDAO().countEntryAddedFiles(entry.getId()));
	}

	@Override
	public final String getMetricName() {
		return "Added Files";
	}

	@Override
	public final double getAverageValue(final Analysis analysis) throws DatabaseException {
		return new EntryMetricsDAO().getAddedFilesAverageValue(analysis);
	}
	
	@Override
	public final double getStdDevValue(final Analysis analysis) throws DatabaseException {
		return new EntryMetricsDAO().getAddedFilesDeviationValue(analysis);
	}

	@Override
	public final double getMetricValue(final Analysis analysis, final Entry entry) throws DatabaseException {
		return new EntryMetricsDAO().getAddedFilesValueByEntry(analysis, entry);
	}
}
