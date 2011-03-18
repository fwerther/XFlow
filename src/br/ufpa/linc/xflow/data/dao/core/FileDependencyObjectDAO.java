package br.ufpa.linc.xflow.data.dao.core;

import java.util.Collection;
import java.util.List;

import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesAnalysis;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class FileDependencyObjectDAO extends DependencyObjectDAO<FileDependencyObject> {

	@Override
	public boolean insert(final FileDependencyObject entity) throws DatabaseException {
		return super.insert(entity);
	}

	@Override
	public boolean remove(final FileDependencyObject entity) throws DatabaseException {
		return super.remove(entity);
	}

	@Override
	public boolean update(final FileDependencyObject entity) throws DatabaseException {
		return super.update(entity);
	}

	@Override
	public FileDependencyObject findById(final Class<FileDependencyObject> clazz, final long id) throws DatabaseException {
		return super.findById(clazz, id);
	}

	@Override
	protected FileDependencyObject findUnique(final Class<FileDependencyObject> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findUnique(clazz, query, parameters);
	}

	@Override
	protected Collection<FileDependencyObject> findByQuery(final Class<FileDependencyObject> clazz, final String query, final Object[]... parameters) throws DatabaseException {
		return super.findByQuery(clazz, query, parameters);
	}

	@Override
	public Collection<FileDependencyObject> findAll(final Class<? extends FileDependencyObject> myClass) throws DatabaseException {
		return super.findAll(myClass);
	}

	@Override
	protected int getIntegerValueByQuery(final String query, final Object[]... parameters) throws DatabaseException {
		return super.getIntegerValueByQuery(query, parameters);
	}

	@Override
	public int checkDependencyStamp(final FileDependencyObject dependedObj) throws DatabaseException {
		if(dependedObj.getFile().getOperationType() == 'A'){
			final String query = "SELECT MAX(objdep.assignedStamp) from dependency_object objdep where objdep.analysis = :analysis and objdep.objectType = "+DependencyObject.FILE_DEPENDENCY;
			final Object[] parameter1 = new Object[]{"analysis", dependedObj.getAnalysis()};

			return getIntegerValueByQuery(query, parameter1) + 1;
		}
		else{
			final String query = "SELECT filedep from file_dependency filedep where filedep.id = "+
			"(select max(filedep.id) from file_dependency filedep where filedep.analysis = :analysis and filedep.filePath = :path)";
			final Object[] parameter1 = new Object[]{"analysis", dependedObj.getAnalysis()};
			final Object[] parameter2 = new Object[]{"path", dependedObj.getFile().getPath()};
			
			final FileDependencyObject filedep = findUnique(FileDependencyObject.class, query, parameter1, parameter2);
			if(filedep != null){
				return filedep.getAssignedStamp();
			}
			else{
				final String subquery = "SELECT MAX(objdep.assignedStamp) from dependency_object objdep where objdep.analysis = :analysis and objdep.objectType = "+DependencyObject.FILE_DEPENDENCY;
				return getIntegerValueByQuery(subquery, parameter1) + 1;
			}
		}
	}
	
	@Override
	public int checkHighestStamp(Analysis analysis) throws DatabaseException {
		final String query = "SELECT MAX(objdep.assignedStamp) from dependency_object objdep where objdep.analysis = :analysis and objdep.objectType = "+DependencyObject.FILE_DEPENDENCY;
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		return getIntegerValueByQuery(query, parameter1);
	}

	@Override
	public List<FileDependencyObject> findAllDependencyObjsUntilDependency(final Dependency dependency) throws DatabaseException {
		if(dependency.isDirectedDependency()){
			final String query = "SELECT dep from file_dependency dep, dependency d where dep.analysis = :analysis and d.id <= :dependencyID and d.type = :type and d in elements(dep.dependencies)";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			final Object[] parameter3 = new Object[]{"type", dependency.getType()};
			
			return (List<FileDependencyObject>) findByQuery(FileDependencyObject.class, query, parameter1, parameter2, parameter3);			
		}
		else{
			final String query = "SELECT dep from file_dependency dep, dependency d where dep.analysis = :analysis and d.id <= :dependencyID and d.type = :type and d in elements(dep.dependencies)";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			final Object[] parameter3 = new Object[]{"type", dependency.getType()};
			
			return (List<FileDependencyObject>) findByQuery(FileDependencyObject.class, query, parameter1, parameter2, parameter3);	
		}
	}

	@Override
	public List<FileDependencyObject> findDependencyObjsByDependency(final Dependency dependency) throws DatabaseException {
		if(dependency.isDirectedDependency()){
			final String query = "SELECT dep from file_dependency dep, dependency d where dep.analysis = :analysis and d.id = :dependencyID and d in elements(dep.dependencies)";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			
			return (List<FileDependencyObject>) findByQuery(FileDependencyObject.class, query, parameter1, parameter2);			
		}
		else{
			final String query = "SELECT dep from file_dependency dep, dependency d where dep.analysis = :analysis and d.id = :dependencyID and d in elements(dep.dependencies)";
			final Object[] parameter1 = new Object[]{"analysis", dependency.getAssociatedAnalysis()};
			final Object[] parameter2 = new Object[]{"dependencyID", dependency.getId()};
			
			return (List<FileDependencyObject>) findByQuery(FileDependencyObject.class, query, parameter1, parameter2);	
		}
	}

	//FIXME: ajeitar akee!
	@Override
	public FileDependencyObject findDependencyObjectByStamp(final Analysis analysis, final int stamp) throws DatabaseException {
		final String query = "SELECT dep from file_dependency dep where dep.analysis = :analysis and dep.assignedStamp = :stamp order by dep.id";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"stamp", stamp};
		
		final List<FileDependencyObject> dependencyObjects = (List<FileDependencyObject>) findByQuery(FileDependencyObject.class, query, parameter1, parameter2);
		
		if(dependencyObjects.size() == 0){
			return null;
		}
		return dependencyObjects.get(0);
	}
	

	public List<String> getFilePathsOrderedByStamp(final Analysis analysis) throws DatabaseException {
		final String query = "SELECT dep.filePath from file_dependency dep " +
				"where dep.analysis = :analysis order by dep.assignedStamp";

		final Object[] parameter1 = new Object[]{"analysis", analysis};
		
		final List<String> dependencyObjects = 
			(List<String>) findObjectsByQuery(query, parameter1);
		
		return dependencyObjects;
	}

	public FileDependencyObject findDependencyObjectByFilePath (
			CoChangesAnalysis analysis, String path) throws DatabaseException {
		final String query = "SELECT dep from file_dependency dep where " +
				"dep.analysis = :analysis and dep.filePath = :path";
		final Object[] parameter1 = new Object[]{"analysis", analysis};
		final Object[] parameter2 = new Object[]{"path", path};
		
		Collection<FileDependencyObject> listFileDependencyObject = 
			findByQuery(FileDependencyObject.class, query, parameter1, 
					parameter2);
		
		return listFileDependencyObject.isEmpty() ? 
				null : listFileDependencyObject.iterator().next();
	}

}
