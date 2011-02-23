package br.ufpa.linc.xflow.data.dao.core;

import java.util.List;

import br.ufpa.linc.xflow.data.dao.BaseDAO;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class DependencySetDAO extends BaseDAO<DependencySet> {

	@Override
	public boolean insert(DependencySet entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(DependencySet entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(DependencySet entity) throws DatabaseException {
		return super.update(entity);
	}

	@Override
	public DependencySet findById(Class<DependencySet> clazz, long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	public List<DependencySet> getAllDependenciesSetUntilDependency(Dependency dependency) throws DatabaseException {
		final String query = "SELECT dependencySet FROM dependency_set dependencySet WHERE dependencySet.associatedDependency.id <= :associatedDependencyID AND dependencySet.associatedDependency.associatedAnalysis.id = :associatedAnalysisID";
		final Object[] parameter1 = new Object[]{"associatedDependencyID", dependency.getId()};
		final Object[] parameter2 = new Object[]{"associatedAnalysisID", dependency.getAssociatedAnalysis().getId()};
		
		return (List<DependencySet>) findByQuery(DependencySet.class, query, parameter1, parameter2);
	}
	
	public List<DependencySet> getAllDependenciesSetByDependency(Dependency dependency) throws DatabaseException {
		final String query = "SELECT dependencySet FROM dependency_set dependencySet WHERE dependencySet.associatedDependency.id = :associatedDependencyID AND dependencySet.associatedDependency.associatedAnalysis.id = :associatedAnalysisID";
		final Object[] parameter1 = new Object[]{"associatedDependencyID", dependency.getId()};
		final Object[] parameter2 = new Object[]{"associatedAnalysisID", dependency.getAssociatedAnalysis().getId()};
		
		return (List<DependencySet>) findByQuery(DependencySet.class, query, parameter1, parameter2);
	}
	
}
