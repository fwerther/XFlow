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
 *  FileMetricModel.java
 *  ====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.metrics.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufpa.linc.xflow.data.dao.metrics.FileMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.jung.JUNGVertex;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.MetricModel;

public abstract class FileMetricModel implements MetricModel {

	protected static Map<Long, JUNGVertex> verticesCache;
	
	abstract public void evaluate(JUNGGraph dependencyGraph, long fileID, FileMetricValues table) throws DatabaseException;
	
	@Override
	public final ArrayList<FileMetricValues> getAllMetricsTables(Metrics metrics) throws DatabaseException {
		return new FileMetricsDAO().getFileMetricValues(metrics);
	}
	
	public final List<FileMetricValues> getMetricTable(Metrics metrics, Entry entry) throws DatabaseException {
		return new FileMetricsDAO().findFileMetricValuesByRevision(metrics, entry);
	}
	
	public static void initiateCache() {
		verticesCache = new HashMap<Long, JUNGVertex>();
	}
	
	public static void clearVerticesCache(){
		verticesCache = null;
	}

}
