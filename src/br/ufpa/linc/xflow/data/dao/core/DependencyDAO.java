package br.ufpa.linc.xflow.data.dao.core;

import java.util.Collection;
import java.util.List;

import br.ufpa.linc.xflow.data.dao.BaseDAO;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class DependencyDAO extends BaseDAO<Dependency> {

	@Override
	public boolean insert(final Dependency entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final Dependency entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final Dependency entity) throws DatabaseException {
		return super.update(entity);
	}

	@Override
	public Dependency findById(final Class<Dependency> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}
	
	@Override
	public Collection<Dependency> findAll(final Class<? extends Dependency> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	@Override
	protected Dependency findUnique(final Class<Dependency> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<Dependency> findByQuery(final Class<Dependency> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}
	
	public Dependency findDependencyByEntry(final long analysisID, final long entryID, final int dependencyType) throws DatabaseException {
		final String query = "SELECT dep from dependency dep where dep.associatedAnalysis.id = :analysisID and dep.associatedEntry.id = :entryID and dep.type = :dependencyType";
		final Object[] parameter1 = new Object[]{"analysisID", analysisID};
		final Object[] parameter2 = new Object[]{"entryID", entryID};
		final Object[] parameter3 = new Object[]{"dependencyType", dependencyType};
		
		return findUnique(Dependency.class, query, parameter1, parameter2, parameter3);
	}
	
	public Dependency findHighestDependencyByEntry(final long analysisID, final long entryID, final int dependencyType) throws DatabaseException {
		final String entryQuery = "select MAX(d.associatedEntry.id) from dependency d where d.associatedAnalysis.id = :analysisID and d.type = :dependencyType and d.associatedEntry.id <= :entryID";
		
		final Object[] parameter1 = new Object[]{"analysisID", analysisID};
		final Object[] parameter2 = new Object[]{"entryID", entryID};
		final Object[] parameter3 = new Object[]{"dependencyType", dependencyType};
		
		Long foundEntry = getLongValueByQuery(entryQuery, parameter1, parameter2, parameter3);
	
		final String subquery = "SELECT dep from dependency dep where dep.associatedAnalysis.id = :analysisID and dep.type = :dependencyType and dep.associatedEntry.id = "+foundEntry;
		return findUnique(Dependency.class, subquery, parameter1, parameter3);
	}
	
	public List<Dependency> findAllDependenciesUntilIt(final Dependency dependency) throws DatabaseException {
		final String query = "SELECT dep from dependency dep where dep.associatedAnalysis = :analysis and dep.id <= :dependencyID and dep.type = :dependencyType";
		final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
		final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
		final Object[] parameter3 = new Object[]{"dependencyType", dependency.getType()};
		
		return (List<Dependency>) findByQuery(Dependency.class, query, parameter1, parameter2, parameter3);
	}

	public List<Dependency> findDependenciesBetweenDependencies(Dependency initialEntryDependency, Dependency finalEntryDependency) throws DatabaseException {
		final String query = "SELECT dep from dependency dep where dep.associatedAnalysis = :analysis and dep.type = :dependencyType and dep.associatedEntry.id between :initialEntry and :finalEntry";
		final Object[] parameter1 = new Object[]{"analysis", initialEntryDependency.getAssociatedAnalysis()};
		final Object[] parameter2 = new Object[]{"dependencyType", initialEntryDependency.getType()};
		final Object[] parameter3 = new Object[]{"initialEntry", initialEntryDependency.getAssociatedEntry().getId()};
		final Object[] parameter4 = new Object[]{"finalEntry", finalEntryDependency.getAssociatedEntry().getId()};
		
		return (List<Dependency>) findByQuery(Dependency.class, query, parameter1, parameter2, parameter3, parameter4);
	}


}
