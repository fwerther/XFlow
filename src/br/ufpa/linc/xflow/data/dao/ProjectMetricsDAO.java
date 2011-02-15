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
 *  ======================
 *  ProjectMetricsDAO.java
 *  ======================
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
import br.ufpa.linc.xflow.metrics.project.ProjectMetricValues;

public class ProjectMetricsDAO extends BaseDAO<ProjectMetricValues> {

	@Override
	public ProjectMetricValues findById(final Class<ProjectMetricValues> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	public boolean insert(final ProjectMetricValues entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final ProjectMetricValues entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final ProjectMetricValues entity) throws DatabaseException {
		return super.update(entity);
	}
	
	@Override
	protected ProjectMetricValues findUnique(final Class<ProjectMetricValues> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<ProjectMetricValues> findByQuery(final Class<ProjectMetricValues> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	public Collection<ProjectMetricValues> findAll(final Class<? extends ProjectMetricValues> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	public ArrayList<ProjectMetricValues> getProjectMetricValues(final Analysis analysis) throws DatabaseException {
		final String query = "select p from project_metrics p where p.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return (ArrayList<ProjectMetricValues>) findByQuery(ProjectMetricValues.class, query, parameter1);
	}
	
	public ProjectMetricValues findProjectMetricValuesByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select p from project_metrics p where p.associatedAnalysis = :analysis and p.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(ProjectMetricValues.class, query, parameter1, parameter2);
	}
	
	public double getDensityMetricValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select p from project_metrics p where p.associatedAnalysis = :analysis and p.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(ProjectMetricValues.class, query, parameter1, parameter2).getDensity();
	}

	public double getDensityAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(p.density) from project_metrics p where p.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getDensityDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(p.density) from project_metrics p where p.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getClusterMetricValueByEntry(final Analysis analysis, final Entry entry) throws DatabaseException {
		final String query = "select p from project_metrics p where p.associatedAnalysis = :analysis and p.entry = :entry";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"entry", entry};
		
		return findUnique(ProjectMetricValues.class, query, parameter1, parameter2).getClusterCoefficient();
	}
	
	public double getClusterCoefficientAverageValue(final Analysis analysis) throws DatabaseException{
		final String query = "select avg(p.clusterCoefficient) from project_metrics p where p.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}

	public double getClusterCoefficientDeviationValue(final Analysis analysis) throws DatabaseException{
		final String query = "select stddev(p.clusterCoefficient) from project_metrics p where p.associatedAnalysis = :analysis";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getDoubleValueByQuery(query, parameter1);
	}
}
