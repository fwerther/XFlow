package br.ufpa.linc.xflow.data.dao;

import java.util.Collection;
import java.util.List;

import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class AuthorDependencyObjectDAO extends DependencyObjectDAO<AuthorDependencyObject> {

	@Override
	public boolean insert(final AuthorDependencyObject entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final AuthorDependencyObject entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final AuthorDependencyObject entity) throws DatabaseException {
		return super.update(entity);
	}

	@Override
	public AuthorDependencyObject findById(final Class<AuthorDependencyObject> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	protected AuthorDependencyObject findUnique(final Class<AuthorDependencyObject> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<AuthorDependencyObject> findByQuery(final Class<AuthorDependencyObject> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	protected Collection<AuthorDependencyObject> findAll(final Class<? extends AuthorDependencyObject> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	@Override
	protected int getIntegerValueByQuery(final String query, final Object[]... parameters) throws DatabaseException {
		return super.getIntegerValueByQuery(query, parameters);
	}

	@Override
	public int checkDependencyStamp(final AuthorDependencyObject dependedObj) throws DatabaseException {
		final String query = "SELECT authordep from author_dependency authordep where authordep.author = :author and authordep.analysis = :analysis";
		final Object[] parameter1 = new Object[]{"author", dependedObj.getAuthor()};
		final Object[] parameter2 = new Object[]{"analysis", dependedObj.getAnalysis()};

		final List<AuthorDependencyObject> authordep = (List<AuthorDependencyObject>) findByQuery(AuthorDependencyObject.class, query, parameter1, parameter2);
		if(authordep.isEmpty()){
			final String subquery = "SELECT MAX(authordep.assignedStamp) from dependency_object authordep where authordep.analysis = :analysis and authordep.objectType = "+DependencyObject.AUTHOR_DEPENDENCY;
			return getIntegerValueByQuery(subquery, parameter2) + 1;
		}
		else{
			return authordep.get(0).getAssignedStamp();			
		}
	}
	
	@Override
	public int checkHighestStamp(Analysis analysis) throws DatabaseException {
		final String query = "SELECT MAX(authordep.assignedStamp) from dependency_object authordep where authordep.analysis = :analysis and authordep.objectType = "+DependencyObject.AUTHOR_DEPENDENCY;
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getIntegerValueByQuery(query, parameter1);
	}

	@Override
	public List<AuthorDependencyObject> findAllDependencyObjsUntilDependency(final Dependency dependency) throws DatabaseException {
		if(dependency.isDirectedDependency()){
			String query = "SELECT dep from author_dependency dep, dependency d where dep.analysis = :analysis and d.id <= :dependencyID and d in elements(dep.dependencies)";
			Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			
			return (List<AuthorDependencyObject>) findByQuery(AuthorDependencyObject.class, query, parameter1, parameter2);			
		}
		else{
			final String query = "SELECT dep from author_dependency dep, dependency d where dep.analysis = :analysis and d.id <= :dependencyID and d in elements(dep.dependencies) and dep.dependentObjects.size > 0";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			
			return (List<AuthorDependencyObject>) findByQuery(AuthorDependencyObject.class, query, parameter1, parameter2);	
		}
	}

	@Override
	public List<AuthorDependencyObject> findDependencyObjsByDependency(final Dependency dependency) throws DatabaseException {
		if(dependency.isDirectedDependency()){
			final String query = "SELECT dep from author_dependency dep, dependency d where dep.analysis = :analysis and d.id = :dependencyID and d in elements(dep.dependencies)";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			
			return (List<AuthorDependencyObject>) findByQuery(AuthorDependencyObject.class, query, parameter1, parameter2);			
		}
		else{
			final String query = "SELECT dep from author_dependency dep, dependency d where dep.analysis = :analysis and d.id = :dependencyID and d in elements(dep.dependencies) and dep.dependentObjects.size > 0";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			
			return (List<AuthorDependencyObject>) findByQuery(AuthorDependencyObject.class, query, parameter1, parameter2);	
		}
	}

	@Override
	public AuthorDependencyObject findDependencyObjectByStamp(final Analysis analysis, final int stamp) throws DatabaseException {
		final String query = "SELECT dep from author_dependency dep where dep.analysis = :analysis and dep.assignedStamp = :stamp";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"stamp", stamp};
		
		return findUnique(AuthorDependencyObject.class, query, parameter1, parameter2);
	}

}
