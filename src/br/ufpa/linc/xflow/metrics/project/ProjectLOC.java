package br.ufpa.linc.xflow.metrics.project;

import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class ProjectLOC extends ProjectMetricModel {

	@Override
	public double getAverageValue(Metrics metrics) throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getStdDevValue(Metrics metrics) throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getMetricName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getMetricValue(Metrics metrics, Entry entry)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void evaluate(JUNGGraph dependencyGraph, ProjectMetricValues table) {
		// TODO Auto-generated method stub
		
	}

}
