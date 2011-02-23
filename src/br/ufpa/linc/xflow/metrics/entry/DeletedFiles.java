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
 *  =================
 *  DeletedFiles.java
 *  =================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.entry;

import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.metrics.EntryMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public final class DeletedFiles extends EntryMetricModel {

	@Override
	public final void evaluate(final Entry entry, final EntryMetricValues table) throws DatabaseException {
		table.setEntryDeletedFiles(new EntryDAO().countEntryDeletedFiles(entry.getId()));
	}
	
	@Override
	public final double getAverageValue(final Metrics metrics) throws DatabaseException {
		return new EntryMetricsDAO().getDeletedFilesAverageValue(metrics);
	}

	@Override
	public final double getStdDevValue(final Metrics metrics) throws DatabaseException {
		return new EntryMetricsDAO().getDeletedFilesDeviationValue(metrics);
	}

	@Override
	public final String getMetricName() {
		return "Deleted Files";
	}

	@Override
	public final double getMetricValue(final Metrics metrics, final Entry entry) throws DatabaseException {
		return new EntryMetricsDAO().getDeletedFilesValueByEntry(metrics, entry);
	}
	
}
