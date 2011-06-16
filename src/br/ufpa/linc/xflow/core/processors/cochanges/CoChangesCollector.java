package br.ufpa.linc.xflow.core.processors.cochanges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufpa.linc.xflow.core.processors.DependenciesIdentifier;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.core.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.database.DatabaseManager;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.CoordinationRequirements;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.TaskAssignment;
import br.ufpa.linc.xflow.data.entities.TaskDependency;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.data.representation.matrix.MatrixFactory;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.Filter;

public final class CoChangesCollector implements DependenciesIdentifier {

	private CoChangesAnalysis analysis;
	
	private Map<String, DependencyObject> dependencyObjectsCache;
	
	private Filter filter;
	
	// MATRICES FOR COORDINATION REQUIREMENTS CALCS
	private Matrix taskAssignmentMatrix;
	private Matrix taskDependencyMatrix;
	
	private int latestFileStampAssigned;
	private int latestAuthorStampAssigned;
	
	@Override
	public final void dataCollect(List<Long> revisions, Analysis analysis, Filter filter) throws DatabaseException {
		this.analysis = (CoChangesAnalysis) analysis;
		this.filter = filter;
		initiateCache();
		
		EntryDAO entryDAO = new EntryDAO();
		DependencyDAO dependencyDAO = new DependencyDAO();
		
		System.out.println("** Starting CoChanges analysis **");
	
		for (Long revision : revisions) {
		
			final Entry entry = entryDAO.findEntryFromRevision(analysis.getProject(), revision);			
			System.out.print("- Processing entry: "+entry.getRevision()+" ("+revision+")\n");
			System.out.print("* Collecting file dependencies...");
					
			final Set<DependencySet<FileDependencyObject, FileDependencyObject>> fileDependencies = gatherTaskDependency(entry.getEntryFiles());
			if(fileDependencies.size() > 0) { 
				final TaskDependency taskDependency = new TaskDependency();
				taskDependency.setAssociatedAnalysis(analysis);
				taskDependency.setAssociatedEntry(entry);
	
				//Sets on both sides (bi-directional association)
				taskDependency.setDependencies(fileDependencies);
						
				dependencyDAO.insert(taskDependency);						
				System.out.print(" done!\n");
					
				
				System.out.print("* Collecting tasks dependencies...");
				final TaskAssignment taskAssignment = new TaskAssignment();
				taskAssignment.setAssociatedAnalysis(this.analysis);
				taskAssignment.setAssociatedEntry(entry);
				taskAssignment.setDirectedDependency(true);
				final Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> taskDependencies = gatherTasksAssignment(entry.getEntryFiles(), entry.getAuthor(), fileDependencies);
				for (DependencySet<AuthorDependencyObject, FileDependencyObject> dependencySet : taskDependencies) {
					dependencySet.setAssociatedDependency(taskAssignment);
				}
				taskAssignment.setDependencies(taskDependencies);
				dependencyDAO.insert(taskAssignment);
				System.out.print(" done!\n");
				if(this.analysis.isCoordinationRequirementPersisted()){
					System.out.print("* Calculating coordination requirements...");
					final CoordinationRequirements coordinationRequirement = new CoordinationRequirements();
					coordinationRequirement.setAssociatedAnalysis(this.analysis);
					coordinationRequirement.setAssociatedEntry(entry);
					coordinationRequirement.setDirectedDependency(false);
					final Set<DependencySet<AuthorDependencyObject, AuthorDependencyObject>> coordinationDependencies = gatherCoordinationRequirements(taskDependency, taskAssignment);
					for (DependencySet<AuthorDependencyObject, AuthorDependencyObject> dependencySet : coordinationDependencies) {
						dependencySet.setAssociatedDependency(coordinationRequirement);
					}
					coordinationRequirement.setDependencies(coordinationDependencies);
					dependencyDAO.insert(coordinationRequirement);
					System.out.print(" done!\n");
				}
				
			} else {
				System.out.println("\nSkipped. No dependency collected on specified entry.");
			}
			//FIXME:
			//As we don't have an application layer yet, it is necessary 
			//to frequently clear the persistence context to avoid memory issues
			DatabaseManager.getDatabaseSession().clear();
		}
	}

	private void initiateCache() throws DatabaseException {
		dependencyObjectsCache = new HashMap<String, DependencyObject>();
		
		latestFileStampAssigned = new FileDependencyObjectDAO().checkHighestStamp(analysis);
		latestAuthorStampAssigned = new AuthorDependencyObjectDAO().checkHighestStamp(analysis);
		taskAssignmentMatrix = MatrixFactory.createMatrix();
		taskDependencyMatrix = MatrixFactory.createMatrix();
	}

	private Set<DependencySet<FileDependencyObject, FileDependencyObject>> gatherTaskDependency(List<ObjFile> changedFiles) throws DatabaseException {
		FileDependencyObjectDAO dependencyObjDAO = new FileDependencyObjectDAO();
		Set<DependencySet<FileDependencyObject, FileDependencyObject>> setOfDependencySets = new HashSet<DependencySet<FileDependencyObject, FileDependencyObject>>();
		List<FileDependencyObject> dependencyObjectList = new ArrayList<FileDependencyObject>();

		//Builds the list of dependency objects
		for (ObjFile changedFile : changedFiles) {
			if(filter.match(changedFile.getPath())){
				FileDependencyObject dependencyObject;
				
				//Added file
				if(changedFile.getOperationType() == 'A'){
					latestFileStampAssigned++;
					
					dependencyObject = new FileDependencyObject();
					dependencyObject.setAnalysis(analysis);
					dependencyObject.setFile(changedFile);
					dependencyObject.setFilePath(changedFile.getPath());
					dependencyObject.setAssignedStamp(latestFileStampAssigned);
					
					dependencyObjDAO.insert(dependencyObject);
				
				//Modified or deleted file
				} else {
					
					//Search database for matching FileDependencyObject 
					dependencyObject = 
						dependencyObjDAO.findDependencyObjectByFilePath(
								analysis, changedFile.getPath());
					
					if (dependencyObject == null){
						ObjFile addedFileReference = 
							new ObjFileDAO().findAddedFileByPathUntilEntry(
									analysis.getProject(), 
									changedFile.getEntry(), 
									changedFile.getPath());
						
						if(addedFileReference != null){
							latestFileStampAssigned++;

							dependencyObject = new FileDependencyObject();
							dependencyObject.setAnalysis(analysis);
							dependencyObject.setFile(changedFile);
							dependencyObject.setFilePath(changedFile.getPath());
							dependencyObject.setAssignedStamp(latestFileStampAssigned);
							
							dependencyObjDAO.insert(dependencyObject);
						}
					}
				}
				
				if(dependencyObject != null){
					dependencyObjectList.add(dependencyObject);
				}
			}
		}

		//Builds the set of DependencySets
		for (int i = 0; i < dependencyObjectList.size(); i++) {
			
			Map<FileDependencyObject, Integer> dependenciesMap = 
				new HashMap<FileDependencyObject, Integer>();
			
			for (int j = i; j < dependencyObjectList.size(); j++) {
				dependenciesMap.put(dependencyObjectList.get(j), 1);
			}
			
			DependencySet<FileDependencyObject, FileDependencyObject> dependencySet = 
				new DependencySet<FileDependencyObject, FileDependencyObject>();
			dependencySet.setDependedObject(dependencyObjectList.get(i));
			dependencySet.setDependenciesMap(dependenciesMap);
			setOfDependencySets.add(dependencySet);
		}
		
		return setOfDependencySets;
	}
	
	private Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> gatherTasksAssignment(List<ObjFile> changedFiles, Author author, Set<DependencySet<FileDependencyObject, FileDependencyObject>> fileDependencies) throws DatabaseException {
		final AuthorDependencyObjectDAO authorDependencyDAO = new AuthorDependencyObjectDAO();
		final Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> dependenciesSet = new HashSet<DependencySet<AuthorDependencyObject, FileDependencyObject>>();
		
		final AuthorDependencyObject dependedAuthor;
		if(dependencyObjectsCache.containsKey(author.getName())){
			dependedAuthor = (AuthorDependencyObject) dependencyObjectsCache.get(author.getName());
		} else {
			latestAuthorStampAssigned++;
			
			dependedAuthor = new AuthorDependencyObject();
			dependedAuthor.setAnalysis(analysis);
			dependedAuthor.setAssignedStamp(latestAuthorStampAssigned);
			dependedAuthor.setAuthor(author);
			
			authorDependencyDAO.insert(dependedAuthor);
			dependencyObjectsCache.put(author.getName(), dependedAuthor);
			dependencyObjectsCache.put("\\u0A"+latestAuthorStampAssigned, dependedAuthor);
		}
			
		//Builds the set of dependency objects
		final Map<FileDependencyObject, Integer> dependenciesMap = new HashMap<FileDependencyObject, Integer>();
		for (DependencySet<FileDependencyObject, FileDependencyObject> fileDependency : fileDependencies) {
			dependenciesMap.put(fileDependency.getDependedObject(), 1);
		}

		DependencySet<AuthorDependencyObject, FileDependencyObject> dependencySet = new DependencySet<AuthorDependencyObject, FileDependencyObject>();
		dependencySet.setDependedObject(dependedAuthor);
		dependencySet.setDependenciesMap(dependenciesMap);
		
		dependenciesSet.add(dependencySet);
	    return dependenciesSet;
	}
	
	
	private Set<DependencySet<AuthorDependencyObject, AuthorDependencyObject>> gatherCoordinationRequirements(final TaskDependency taskDependency, final TaskAssignment taskAssignment) throws DatabaseException {

		final Set<DependencySet<AuthorDependencyObject, AuthorDependencyObject>> coordinationDependencies = new HashSet<DependencySet<AuthorDependencyObject, AuthorDependencyObject>>();
		
		Matrix entryTaskDependencyMatrix = analysis.processDependencyMatrix(taskDependency);
		Matrix entryTaskAssignmentMatrix = analysis.processDependencyMatrix(taskAssignment);

		taskDependencyMatrix = entryTaskDependencyMatrix.sumDifferentOrderMatrix(taskDependencyMatrix);
		taskAssignmentMatrix = entryTaskAssignmentMatrix.sumDifferentOrderMatrix(taskAssignmentMatrix);
		
		entryTaskAssignmentMatrix = null;
		entryTaskDependencyMatrix = null;
		
		Matrix coordReq = taskAssignmentMatrix.multiply(taskDependencyMatrix).multiply(taskAssignmentMatrix.getTransposeMatrix());
		for (int i = 0; i < coordReq.getRows(); i++) {
			DependencySet<AuthorDependencyObject, AuthorDependencyObject> authorDependencies = new DependencySet<AuthorDependencyObject, AuthorDependencyObject>();
			final AuthorDependencyObject dependedAuthor;
			if(dependencyObjectsCache.containsKey("\\u0A"+i)){
				dependedAuthor = (AuthorDependencyObject) dependencyObjectsCache.get("\\u0A"+i);
			} else {
				dependedAuthor = null;
			}
			
			Map<AuthorDependencyObject, Integer> dependenciesMap = new HashMap<AuthorDependencyObject, Integer>();
			for (int j = 0; j < coordReq.getColumns(); j++) {
				final AuthorDependencyObject dependentAuthor;
				
				if(dependencyObjectsCache.containsKey("\\u0A"+j)){
					dependentAuthor = (AuthorDependencyObject) dependencyObjectsCache.get("\\u0A"+j);
				} else {
					dependentAuthor = null;
				}
				
				dependenciesMap.put(dependentAuthor, coordReq.getValueAt(j, i));
			}
			authorDependencies.setDependenciesMap(dependenciesMap);
			authorDependencies.setDependedObject(dependedAuthor);
			coordinationDependencies.add(authorDependencies);
		}
		return coordinationDependencies;
	}
}
