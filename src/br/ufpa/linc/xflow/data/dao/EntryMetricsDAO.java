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
 *  ====================
 *  EntryMetricsDAO.java
 *  ====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.data.dao;

import java.util.ArrayList;
import java.util.Collection;

import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricValues;

public class EntryMetricsDAO extends BaseDAO<EntryMetricValues> {

	@Override
	public EntryMetricValues findById(final Class<EntryMetricValues> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	public boolean insert(final EntryMetricValues entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final EntryMetricValues entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final EntryMetricValues entity) throws DatabaseException {
		return super.update(entity);
	}
	
	@Override
	protected EntryMetricValues findUnique(final Class<EntryMetricValues> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<EntryMetricValues> findByQuery(final Class<EntryMetricValues> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	public Collection<EntryMetricValues> findAll(final Class<? extends EntryMetricValues> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	public ArrayList<EntryMetricValues> getEntryMetricValues(final Analysis analysis) throws DatabaseException {
		final String query = "select values from entry_metrics values where values.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return (ArrayList<EntryMetricValues>) findByQuery(EntryMetricValues.class, query, parameter1);
	}
	
	public EntryMetricValues findEntryMetricValuesByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from entry_metrics values where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(EntryMetricValues.class, query, parameter1, parameter2);
	}
	
	public int getAddedFilesValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from entry_metrics values where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(EntryMetricValues.class, query, parameter1, parameter2).getEntryAddedFiles();
	}
	
	public double getAddedFilesAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(e.entryAddedFiles) from entry_metrics e where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getAddedFilesDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(e.entryAddedFiles) from entry_metrics e where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}
	
	public int getModifiedFilesValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from entry_metrics values where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(EntryMetricValues.class, query, parameter1, parameter2).getEntryModifiedFiles();
	}
	
	public double getModifiedFilesAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(e.entryModifiedFiles) from entry_metrics e where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getModifiedFilesDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(e.entryModifiedFiles) from entry_metrics e where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}
	
	public int getDeletedFilesValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from entry_metrics values where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(EntryMetricValues.class, query, parameter1, parameter2).getEntryDeletedFiles();
	}
	
	public double getDeletedFilesAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(e.entryDeletedFiles) from entry_metrics e where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getDeletedFilesDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(e.entryDeletedFiles) from entry_metrics e where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public int getEntryLOCValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from entry_metrics values where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(EntryMetricValues.class, query, parameter1, parameter2).getEntryLOC();
	}
	
	public double getEntryLOCAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(e.entryLOC) from entry_metrics values where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getEntryLOCDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(e.entryLOC) from entry_metrics values where e.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}
}
