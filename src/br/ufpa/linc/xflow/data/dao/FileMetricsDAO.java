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
 *  ===================
 *  FileMetricsDAO.java
 *  ===================
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
import br.ufpa.linc.xflow.metrics.file.FileMetricValues;

public class FileMetricsDAO extends BaseDAO<FileMetricValues>{

	@Override
	public FileMetricValues findById(final Class<FileMetricValues> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	public boolean insert(final FileMetricValues entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final FileMetricValues entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final FileMetricValues entity) throws DatabaseException {
		return super.update(entity);
	}
	
	@Override
	protected FileMetricValues findUnique(final Class<FileMetricValues> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<FileMetricValues> findByQuery(final Class<FileMetricValues> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	public Collection<FileMetricValues> findAll(final Class<? extends FileMetricValues> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	public ArrayList<FileMetricValues> getFileMetricValues(final Analysis analysis) throws DatabaseException {
		final String query = "select values from file_metrics f where values.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return (ArrayList<FileMetricValues>) findByQuery(FileMetricValues.class, query, parameter1);
	}
		
	public FileMetricValues findFileMetricValuesByRevision(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from file_metrics f where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(FileMetricValues.class, query, parameter1, parameter2);
	}

	public double getCentralityAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(f.centrality) from file_metrics f where f.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getCentralityDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(f.centrality) from file_metrics f where f.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}
	
	public int getCentralityValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from file_metrics f where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(FileMetricValues.class, query, parameter1, parameter2).getCentrality();
	}
	
	public double getBetweennessValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select values from file_metrics f where values.associatedAnalysis = :analysis and values.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(FileMetricValues.class, query, parameter1, parameter2).getBetweennessCentrality();
	}
	
	public double getBetweennessAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(f.betweennessCentrality) from file_metrics f where f.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getBetweennessDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(f.betweennessCentrality) from file_metrics f where f.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}
}
