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
 *  =======================
 *  ProjectMetricModel.java
 *  =======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.project;

import java.util.ArrayList;

import br.ufpa.linc.xflow.data.dao.ProjectMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;

public abstract class ProjectMetricModel implements MetricModel {

	public abstract void evaluate(JUNGGraph dependencyGraph, ProjectMetricValues table);
	
	@Override
	public ProjectMetricValues getMetricTable(Analysis analysis, Entry entry) throws DatabaseException {
		return new ProjectMetricsDAO().findProjectMetricValuesByEntry(analysis, entry);
	}
	
	@Override
	public final ArrayList<ProjectMetricValues> getAllMetricsTables(Analysis analysis) throws DatabaseException {
		return new ProjectMetricsDAO().getProjectMetricValues(analysis);
	}
}
