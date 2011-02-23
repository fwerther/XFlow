package br.ufpa.linc.xflow.core.processors.cochanges;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import br.ufpa.linc.xflow.core.processors.DependenciesIdentifier;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.core.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.CoordinationRequirements;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.TaskAssignment;
import br.ufpa.linc.xflow.data.entities.TaskDependency;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
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
	public final void dataCollect(List<Entry> entries, Analysis analysis, Filter filter) throws DatabaseException {
		this.analysis = (CoChangesAnalysis) analysis;
		this.filter = filter;
		initiateCache();
		if(this.analysis.isCoordinationRequirementPersisted()){
			initiateCoChangesMatrix(entries.get(0));
		}
		
		System.out.println("** Starting CoChanges analysis **");
		final int maxFiles = this.analysis.getMaxFilesPerRevision();

		for (int i = 0; i < entries.size(); i++) {
			
			final DependencyDAO dependencyDAO = new DependencyDAO();
			final Entry entry = entries.get(i);
			
			System.out.print("- Processing entry: "+entry.getRevision()+" ("+i+")\n");

			if(entry.getEntryFiles().size() > 0 ){
				if((entry.getEntryFiles().size() <= maxFiles) || (maxFiles == 0)){
					System.out.print("* Collecting file dependencies...");
					final Set<DependencySet<FileDependencyObject, FileDependencyObject>> fileDependencies = gatherTaskDependency(entry.getEntryFiles());
					if(fileDependencies.size() > 0) { 
						final TaskDependency taskDependency = new TaskDependency();
						taskDependency.setAssociatedAnalysis(analysis);
						taskDependency.setAssociatedEntry(entry);
						for (DependencySet<FileDependencyObject, FileDependencyObject> dependencySet : fileDependencies) {
							dependencySet.setAssociatedDependency(taskDependency);
						}
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

						/**
					if (i == entries.size() -1 ){
						Matrix matrix = this.analysis.processDependencyMatrix(taskDependency);
						for (int j = 0; j < matrix.getRows(); j++) {
							for (int j2 = 0; j2 < matrix.getColumns(); j2++) {
								System.out.print(matrix.get(j, j2) + " ");							
							}
							System.out.println();
						}
					}
						 */

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
				}
				else{
					System.out.println("* Skipped. Number of files higher than specified parameter.");
				}
			}
			else{
				System.out.println("* Skipped. No Co-Change info on revision.");
			}

		}
		
	}

	private void initiateCache() throws DatabaseException {
		dependencyObjectsCache = new HashMap<String, DependencyObject>();
		
		latestFileStampAssigned = new FileDependencyObjectDAO().checkHighestStamp(analysis);
		latestAuthorStampAssigned = new AuthorDependencyObjectDAO().checkHighestStamp(analysis);
	}

	private void initiateCoChangesMatrix(Entry entry) throws DatabaseException {
		if(entry == analysis.getFirstEntry()){
			taskAssignmentMatrix = new Matrix(0);
			taskDependencyMatrix = new Matrix(0);
		} else {
			taskAssignmentMatrix = new Matrix(0);
			taskDependencyMatrix = new Matrix(0);		
		}
	}

	private Set<DependencySet<FileDependencyObject, FileDependencyObject>> gatherTaskDependency(List<ObjFile> changedFiles) throws DatabaseException {
		
		final FileDependencyObjectDAO dependencyObjDAO = new FileDependencyObjectDAO();
		Set<DependencySet<FileDependencyObject, FileDependencyObject>> dependenciesSet = new HashSet<DependencySet<FileDependencyObject, FileDependencyObject>>();
		Vector<FileDependencyObject> dependencyObjectVector = new Vector<FileDependencyObject>();

		//Builds the set of dependency objects
		for (ObjFile changedFile : changedFiles) {
			if(filter.match(changedFile.getPath())){
				final FileDependencyObject dependencyObject;
				
				if(changedFile.getOperationType() == 'A'){
					latestFileStampAssigned++;
					
					dependencyObject = new FileDependencyObject();
					dependencyObject.setAnalysis(analysis);
					dependencyObject.setFile(changedFile);
					dependencyObject.setFilePath(changedFile.getPath());
					dependencyObject.setAssignedStamp(latestFileStampAssigned);
					
					dependencyObjDAO.insert(dependencyObject);
					dependencyObjectsCache.put(changedFile.getPath(), dependencyObject);
					dependencyObjectsCache.put("\\u0F"+latestFileStampAssigned, dependencyObject);
				} else if(dependencyObjectsCache.containsKey(changedFile.getPath())){
					dependencyObject = (FileDependencyObject) dependencyObjectsCache.get(changedFile.getPath());
				} else {
					ObjFile addedFileReference = new ObjFileDAO().findAddedFileByPathUntilEntry(analysis.getProject(), changedFile.getEntry(), changedFile.getPath());
					if(addedFileReference == null){
						dependencyObject = null;
					} else {
						latestFileStampAssigned++;

						dependencyObject = new FileDependencyObject();
						dependencyObject.setAnalysis(analysis);
						dependencyObject.setFile(changedFile);
						dependencyObject.setFilePath(changedFile.getPath());
						dependencyObject.setAssignedStamp(latestFileStampAssigned);
						
						dependencyObjDAO.insert(dependencyObject);
						dependencyObjectsCache.put(changedFile.getPath(), dependencyObject);
						dependencyObjectsCache.put("\\u0F"+latestFileStampAssigned, dependencyObject);
					}

				}
				
				if(dependencyObject != null){
					dependencyObjectVector.add(dependencyObject);
				}
			}
		}

		//Set dependents for each dependency object
		for (int i = 0; i < dependencyObjectVector.size(); i++) {
			Map<FileDependencyObject, Integer> dependenciesMap = new HashMap<FileDependencyObject, Integer>();
			for (int j = i; j < dependencyObjectVector.size(); j++) {
				dependenciesMap.put(dependencyObjectVector.get(j), 1);
			}
			
			DependencySet<FileDependencyObject, FileDependencyObject> dependencySet = new DependencySet<FileDependencyObject, FileDependencyObject>();
			dependencySet.setDependedObject(dependencyObjectVector.get(i));
			dependencySet.setDependenciesMap(dependenciesMap);
			dependenciesSet.add(dependencySet);
		}
		
		return dependenciesSet;
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
					System.out.println("PROCUREI: "+j);
					for (String string : dependencyObjectsCache.keySet()) {
						System.out.println(string);
					}
					dependentAuthor = null;
				}
				
				dependenciesMap.put(dependentAuthor, coordReq.get(j, i));
			}
			authorDependencies.setDependenciesMap(dependenciesMap);
			authorDependencies.setDependedObject(dependedAuthor);
			coordinationDependencies.add(authorDependencies);
		}
		return coordinationDependencies;
	}
}
