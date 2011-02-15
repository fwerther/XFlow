package br.ufpa.linc.xflow.core.processors.cochanges;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufpa.linc.xflow.core.processors.DependenciesIdentifier;
import br.ufpa.linc.xflow.data.dao.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.TaskDependency;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.Filter;

public final class CoChangesCollector implements DependenciesIdentifier {

	private CoChangesAnalysis analysis;
	
	private Map<String, Integer> stampsCache;
	
	// MATRICES FOR COORDINATION REQUIREMENTS CALCS
	private Matrix taskAssignmentMatrix;
	private Matrix taskDependencyMatrix;
	
	private int latestFileStampAssigned;
	private int latestAuthorStampAssigned;
	
	@Override
	public final void dataCollect(List<Entry> entries, Analysis analysis, Filter filter) throws DatabaseException {
		this.analysis = (CoChangesAnalysis) analysis;
		initiateCache();
		if(this.analysis.isCoordinationRequirementPersisted()){
			initiateCoChangesMatrix(entries.get(0));
		}
		
		System.out.println("** Starting CoChanges analysis **");
		final int maxFiles = this.analysis.getMaxFilesPerRevision();

		for (int i = 0; i < entries.size(); i++) {
			
			final DependencyDAO dependencyDAO = new DependencyDAO();
			final Entry entry = entries.get(i);
			
			System.out.print("- Processing entry: "+entry.getRevision()+"\n");

			if(entry.getEntryFiles().size() > 0 ){
				if((entry.getEntryFiles().size() <= maxFiles) || (maxFiles == 0)){
					System.out.print("* Collecting file dependencies...");
					final Set<DependencyObject> fileDependencies = gatherTaskDependency(entry.getEntryFiles());
					final TaskDependency taskDependency = new TaskDependency();
					taskDependency.setAssociatedAnalysis(analysis);
					taskDependency.setAssociatedEntry(entry);
					taskDependency.setDependencies(fileDependencies);
					dependencyDAO.insert(taskDependency);
					System.out.print(" done!\n");
					
					System.out.print("* Collecting tasks dependencies...");
					final Dependency taskAssignment = new Dependency();
					taskAssignment.setAssociatedAnalysis(this.analysis);
					taskAssignment.setAssociatedEntry(entry);
					taskAssignment.setDirectedDependency(true);
					taskAssignment.setType(Dependency.AUTHOR_FILE_DEPENDENCY);
					final Set<DependencyObject> taskDependencies = gatherTasksAssignment(entry.getEntryFiles(), entry.getAuthor(), fileDependencies);
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
						final Dependency coordinationRequirement = new Dependency();
						coordinationRequirement.setAssociatedAnalysis(this.analysis);
						coordinationRequirement.setAssociatedEntry(entry);
						coordinationRequirement.setDirectedDependency(false);
						coordinationRequirement.setType(Dependency.AUTHOR_AUTHOR_DEPENDENCY);
						final Set<DependencyObject> coordinationDependencies = gatherCoordinationRequirements(taskDependency, taskAssignment);
						coordinationRequirement.setDependencies(coordinationDependencies);
						dependencyDAO.insert(coordinationRequirement);
						System.out.print(" done!\n");
					}
				}
				else{
					System.out.println("* Skipped. Number of files higher than specified parameter.");
				}
			}
			else{
				System.out.println("* Skipped. No Co-Change info on revision.");
			}

//			this.analysis.calculateCoordinationRequirements(taskAssignment, taskDependency);
		}
		
	}

	private void initiateCache() throws DatabaseException {
		stampsCache = new HashMap<String, Integer>();
		
		latestFileStampAssigned = new FileDependencyObjectDAO().checkHighestStamp(analysis);
		latestAuthorStampAssigned = new AuthorDependencyObjectDAO().checkHighestStamp(analysis);
	}

	private void initiateCoChangesMatrix(Entry entry) throws DatabaseException {
		Dependency dependency = new DependencyDAO().findDependencyByEntry(analysis.getId(), entry.getId(), Dependency.AUTHOR_FILE_DEPENDENCY);
		taskAssignmentMatrix = analysis.processHistoricalEntryDependencyMatrix(entry, dependency);
		dependency = new DependencyDAO().findDependencyByEntry(analysis.getId(), entry.getId(), Dependency.FILE_FILE_DEPENDENCY);
		taskDependencyMatrix = analysis.processHistoricalEntryDependencyMatrix(entry, dependency);
	}

	private Set<DependencyObject> gatherTaskDependency(List<ObjFile> changedFiles) throws DatabaseException {
		
		final FileDependencyObjectDAO dependencyObjDAO = new FileDependencyObjectDAO();
		Set<DependencyObject> dependencyObjectSet = new HashSet<DependencyObject>();

		//Builds the set of dependency objects
		for (ObjFile changedFile : changedFiles) {
			FileDependencyObject dependencyObject = new FileDependencyObject();
			dependencyObject.setAnalysis(analysis);
			dependencyObject.setFile(changedFile);
			dependencyObject.setFilePath(changedFile.getPath());
			dependencyObject.setDependenceDegree(1);

			//Sets the file stamp
			if(changedFile.getOperationType() == 'A'){
				latestFileStampAssigned++;
				dependencyObject.setAssignedStamp(latestFileStampAssigned);
				stampsCache.put(changedFile.getPath(), latestFileStampAssigned);
			}
			else if(stampsCache.containsKey(changedFile.getPath())){
				dependencyObject.setAssignedStamp(stampsCache.get(changedFile.getPath()));
			} else {
				latestFileStampAssigned++;
				ObjFile addedFileReference = new ObjFileDAO().findAddedFileByPathUntilEntry(analysis.getProject(), changedFile.getEntry(), changedFile.getPath());
				if(addedFileReference == null){
					dependencyObject = null;
				} else {
					dependencyObject.setFile(addedFileReference);
					dependencyObject.setAssignedStamp(latestFileStampAssigned);
					stampsCache.put(changedFile.getPath(), latestFileStampAssigned);
				}
			}
			if(dependencyObject != null){
				dependencyObjectSet.add(dependencyObject);
			}
		}

		//Set dependents for each dependency object
		for (DependencyObject dependencyObject : dependencyObjectSet){
			Set<DependencyObject> dependentObjects = new HashSet<DependencyObject>(dependencyObjectSet);
			dependencyObject.setDependentObjects(dependentObjects);
			dependentObjects.remove(dependencyObject);
		}

		//Inserts the dependency object (cascades the insertion to all dependents)
		if(dependencyObjectSet.size() > 0){
			dependencyObjDAO.insert((FileDependencyObject)dependencyObjectSet.iterator().next());
		}
		return dependencyObjectSet;
	}
	
	private Set<DependencyObject> gatherTasksAssignment(List<ObjFile> changedFiles, Author author, Set<DependencyObject> fileDependencies) throws DatabaseException {
		final AuthorDependencyObjectDAO authorDependencyDAO = new AuthorDependencyObjectDAO();
		final Set<DependencyObject> taskDependencySet = new HashSet<DependencyObject>();
		
		final AuthorDependencyObject dependedAuthor = new AuthorDependencyObject();
		dependedAuthor.setAnalysis(analysis);
		dependedAuthor.setAuthor(author);
		if(stampsCache.containsKey(author.getName())){
			dependedAuthor.setAssignedStamp(stampsCache.get(author.getName()));
		} else {
			latestAuthorStampAssigned++;
			dependedAuthor.setAssignedStamp(latestAuthorStampAssigned);
			stampsCache.put(author.getName(), latestAuthorStampAssigned);
		}
			
		//Builds the set of dependency objects
		final Set<DependencyObject> dependentFiles = new HashSet<DependencyObject>();
		for (DependencyObject dependencyObject : fileDependencies) {
			dependentFiles.add(dependencyObject);
		}
		
		dependedAuthor.setDependentObjects(dependentFiles);
		authorDependencyDAO.insert(dependedAuthor);
		taskDependencySet.add(dependedAuthor);
	    return taskDependencySet;
	}
	
	
	private Set<DependencyObject> gatherCoordinationRequirements(final Dependency taskDependency, final Dependency taskAssignment) throws DatabaseException {

		final Set<DependencyObject> coordinationDependencies = new HashSet<DependencyObject>();
		final AuthorDependencyObjectDAO authorDependencyDAO = new AuthorDependencyObjectDAO();
		
		Matrix entryTaskDependencyMatrix = analysis.processDependencyMatrix(taskDependency);
		Matrix entryTaskAssignmentMatrix = analysis.processDependencyMatrix(taskAssignment);

		taskDependencyMatrix = entryTaskDependencyMatrix.sumDifferentOrderMatrix(taskDependencyMatrix);
		taskAssignmentMatrix = entryTaskAssignmentMatrix.sumDifferentOrderMatrix(taskAssignmentMatrix);
		
		entryTaskAssignmentMatrix = null;
		entryTaskDependencyMatrix = null;
		
		Matrix coordReq = taskAssignmentMatrix.multiply(taskDependencyMatrix).multiply(taskAssignmentMatrix.getTransposeMatrix());
		for (int i = 0; i < coordReq.getRows(); i++) {
			

			final AuthorDependencyObject dependedAuthor = authorDependencyDAO.findDependencyObjectByStamp(analysis, i);
			final Set<DependencyObject> authorDependencies = new HashSet<DependencyObject>();
			
			for (int j = 0; j < coordReq.getColumns(); j++) {
				final AuthorDependencyObject dependentAuthor = authorDependencyDAO.findDependencyObjectByStamp(analysis, j);
				
				dependentAuthor.setDependenceDegree(coordReq.get(j, i));
				authorDependencyDAO.insert(dependentAuthor);
				authorDependencies.add(dependentAuthor);
			}
			
			dependedAuthor.setDependentObjects(authorDependencies);
			authorDependencyDAO.insert(dependedAuthor);
			coordinationDependencies.add(dependedAuthor);
		}
		
		return coordinationDependencies;
	}
}
