package br.ufpa.linc.xflow.core.processors.callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufpa.linc.xflow.core.processors.DependenciesIdentifier;
import br.ufpa.linc.xflow.data.dao.cm.AuthorDAO;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.core.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencySetDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.database.DatabaseManager;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.TaskAssignment;
import br.ufpa.linc.xflow.data.entities.TaskDependency;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.Filter;

public class CallGraphCollector implements DependenciesIdentifier {

	private CallGraphAnalysis analysis;	
	private Filter filter;
	private int latestFileStampAssigned;
	private int latestAuthorStampAssigned;
	
	private Map<String, AuthorDependencyObject> authorsStampsMap;
	
	@Override
	public final void dataCollect(List<Long> revisions, Analysis analysis, Filter filter) throws DatabaseException {
		this.analysis = (CallGraphAnalysis) analysis;
		this.filter = filter;
		initiateCache();
		
		EntryDAO entryDAO = new EntryDAO();
		DependencyDAO dependencyDAO = new DependencyDAO();
		
		System.out.println("** Starting Structural analysis **");
		
		for (Long revision : revisions) {
		
			final Entry entry = entryDAO.findEntryFromRevision(analysis.getProject(), revision);
//			final Entry entry = entryDAO.findById(Entry.class, revision);

			System.out.print("- Processing entry: "+entry.getRevision()+" ("+revision+")\n");
			System.out.print("* Collecting file dependencies...");
			
			final List<ObjFile> studiedFiles;
			if(this.analysis.isWholeSystemSnapshot()){
				if(this.analysis.isTemporalConsistencyForced()){
					studiedFiles = new ObjFileDAO().getAllAddedFilesUntilEntry(entry.getProject(), entry);
				} else {
					studiedFiles = new ObjFileDAO().getAllAddedFilesUntilRevision(entry.getProject(), entry.getRevision());
				}
			} else {
				studiedFiles = entry.getEntryFiles();
			}
			
			final Set<DependencySet<FileDependencyObject, FileDependencyObject>> structuralDependencies; 
			if(this.analysis.isWholeSystemSnapshot()){
				structuralDependencies = gatherStructuralDependencies(studiedFiles, entry, true);
			} else {
				structuralDependencies = gatherStructuralDependencies(studiedFiles, entry);
			}
			
			System.out.println("* Collecting author dependencies...");
			
			final Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> taskAssignmentDependencies;
			if(this.analysis.isWholeSystemSnapshot()){
				taskAssignmentDependencies = gatherWholeTaskAssignmentDependencies(entry);
			} else {
				taskAssignmentDependencies = gatherTaskAssignmentDependencies(entry.getAuthor(), structuralDependencies);
			}
					
			if(structuralDependencies.size() > 0) { 
				final TaskDependency taskDependency = new TaskDependency(true);
				taskDependency.setAssociatedAnalysis(analysis);
				taskDependency.setAssociatedEntry(entry);
				
				final TaskAssignment taskAssignment = new TaskAssignment();
				taskAssignment.setAssociatedAnalysis(analysis);
				taskAssignment.setAssociatedEntry(entry);
	
				//Sets on both sides (bi-directional association)
				taskDependency.setDependencies(structuralDependencies);
				taskAssignment.setDependencies(taskAssignmentDependencies);
								
				dependencyDAO.insert(taskDependency);
				dependencyDAO.insert(taskAssignment);
				System.out.print(" done!\n");
			} else {
				System.out.println("\nSkipped. No dependency collected on specified entry.");
			}				
			
			//FIXME:
			//As we don't have an application layer yet, it is necessary 
			//to frequently clear the persistence context to avoid memory issues
			DatabaseManager.getDatabaseSession().clear();
		}
		
	}

	private Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> gatherWholeTaskAssignmentDependencies(Entry entry) throws DatabaseException {
		final AuthorDependencyObjectDAO authorDependencyDAO = new AuthorDependencyObjectDAO();
		final Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> dependenciesSet = new HashSet<DependencySet<AuthorDependencyObject, FileDependencyObject>>();
		final List<Author> developers = new AuthorDAO().getProjectAuthorsUntilEntry(this.analysis.getProject().getId(), entry.getId(), this.analysis.isTemporalConsistencyForced());
		
		for (Author author : developers) {
			
			final AuthorDependencyObject dependedAuthor;
			if(authorsStampsMap.containsKey(author.getName())){
				dependedAuthor = (AuthorDependencyObject) authorsStampsMap.get(author.getName());
			} else {
				latestAuthorStampAssigned++;
				
				dependedAuthor = new AuthorDependencyObject();
				dependedAuthor.setAnalysis(analysis);
				dependedAuthor.setAssignedStamp(latestAuthorStampAssigned);
				dependedAuthor.setAuthor(author);
				
				authorDependencyDAO.insert(dependedAuthor);
				authorsStampsMap.put(author.getName(), dependedAuthor);
			}
			
			
			final List<ObjFile> filesChanged;
			if(this.analysis.isTemporalConsistencyForced()){
				filesChanged = new ObjFileDAO().getAllAddedFilesUntilEntryByAuthor(author.getProject(), entry, author);
			} else {
				filesChanged = new ObjFileDAO().getAllAddedFilesUntilRevisionByAuthor(author.getProject(), entry.getRevision(), author);	
			}
			
			for (ObjFile objFile : filesChanged) {
				FileDependencyObject dependencyObject =  new FileDependencyObjectDAO().findDependencyObjectByFilePath(analysis, objFile.getPath());
				
					
				//Builds the set of dependency objects
				final Map<FileDependencyObject, Integer> dependenciesMap = new HashMap<FileDependencyObject, Integer>();
				dependenciesMap.put(dependencyObject, 1);

				DependencySet<AuthorDependencyObject, FileDependencyObject> dependencySet = new DependencySet<AuthorDependencyObject, FileDependencyObject>();
				dependencySet.setDependedObject(dependedAuthor);
				dependencySet.setDependenciesMap(dependenciesMap);
				dependenciesSet.add(dependencySet);
			}
		}
		
		return dependenciesSet;	
	}

	private void initiateCache() throws DatabaseException {
		latestFileStampAssigned = new FileDependencyObjectDAO().checkHighestStamp(analysis);
		latestAuthorStampAssigned = new AuthorDependencyObjectDAO().checkHighestStamp(analysis);
		
		authorsStampsMap = new HashMap<String, AuthorDependencyObject>();
	}
	
	private Set<DependencySet<FileDependencyObject, FileDependencyObject>> gatherStructuralDependencies(List<ObjFile> changedFiles, Entry entry) throws DatabaseException {
	
		DependencySetDAO dependencySetDAO = new DependencySetDAO();
		final Set<DependencySet<FileDependencyObject, FileDependencyObject>> setOfDependencySets = new HashSet<DependencySet<FileDependencyObject, FileDependencyObject>>();

		final List<ObjFile> allProjectFiles;
		if(this.analysis.isTemporalConsistencyForced()){
			allProjectFiles = new ObjFileDAO().getAllAddedFilesUntilEntry(entry.getProject(), entry);
		} else {
			allProjectFiles = new ObjFileDAO().getAllAddedFilesUntilRevision(entry.getProject(), entry.getRevision());
		}

		for (int i = 0; i < changedFiles.size(); i++) {
			if(filter.match(changedFiles.get(i).getPath())){
				final FileDependencyObject changedFileDO = findFileDependencyObjectInstance(changedFiles.get(i), entry);
				final ObjFile changedFile;
				
				if(this.analysis.isTemporalConsistencyForced()){
					changedFile = new ObjFileDAO().findFileByPathUntilEntry(entry.getProject(), entry, changedFileDO.getFilePath());
				} else {
					changedFile = new ObjFileDAO().findFileByPathUntilRevision(entry.getProject(), entry.getRevision(), changedFileDO.getFilePath());
				}
				
				for (ObjFile objFile : allProjectFiles) {
					if(filter.match(objFile.getPath())){
						final FileDependencyObject otherFileDO = findFileDependencyObjectInstance(objFile, entry);
						final ObjFile otherFile;
							
						if(this.analysis.isTemporalConsistencyForced()){
							otherFile = new ObjFileDAO().findFileByPathUntilEntry(entry.getProject(), entry, otherFileDO.getFilePath());
						} else {
							otherFile = new ObjFileDAO().findFileByPathUntilRevision(entry.getProject(), entry.getRevision(), otherFileDO.getFilePath());
						}
						
						boolean hasDep = StructuralCouplingIdentifier.checkStructuralCoupling(changedFile, otherFile);
						
						//If otherFile is a supplier of changedFile 
						if(dependencySetDAO.isSupplier(this.analysis, changedFileDO, otherFileDO)){
							//If the dependency is gone
							if(!hasDep){
								handleDependency(setOfDependencySets, changedFileDO, otherFileDO, 0);								
							}
						}
						//If otherFile is not a supplier of changedFile
						else{
							//If a dependency came up
							if(hasDep){
								handleDependency(setOfDependencySets, changedFileDO, otherFileDO, 1);	
							}
						}
					}
				}
			}
		}
		
		return setOfDependencySets;
	}

	private void handleDependency(
			final Set<DependencySet<FileDependencyObject, FileDependencyObject>> setOfDependencySets,
			final FileDependencyObject client,
			final FileDependencyObject supplier, 
			final Integer degree) {
		
		DependencySet<FileDependencyObject, FileDependencyObject> dependencySet = searchForDependencySetofASupplier(setOfDependencySets, supplier);
		if (dependencySet == null){
			dependencySet = new DependencySet<FileDependencyObject, FileDependencyObject>();
			dependencySet.setDependedObject(supplier);
			final Map<FileDependencyObject, Integer> dependenciesMap = new HashMap<FileDependencyObject, Integer>();
			dependenciesMap.put(client, degree);
			dependencySet.setDependenciesMap(dependenciesMap);
			setOfDependencySets.add(dependencySet);	
		}
		else{
			Map<FileDependencyObject, Integer> dependenciesMap = (Map<FileDependencyObject, Integer>) dependencySet.getDependenciesMap();
			dependenciesMap.put(client, degree);
		}
	}
	
	private DependencySet<FileDependencyObject, FileDependencyObject> searchForDependencySetofASupplier(Set<DependencySet<FileDependencyObject, FileDependencyObject>> setOfDependencySets, FileDependencyObject supplier){
		for (DependencySet<FileDependencyObject, FileDependencyObject> dependencySet : setOfDependencySets){
			if (dependencySet.getDependedObject().equals(supplier)){
				return dependencySet;
			}
		}
		return null;
	}
	
	private Set<DependencySet<FileDependencyObject, FileDependencyObject>> gatherStructuralDependencies(List<ObjFile> changedFiles, Entry entry, boolean wholeSystem) throws DatabaseException {

		//Builds the list of dependency objects
		final Set<DependencySet<FileDependencyObject, FileDependencyObject>> setOfDependencySets = new HashSet<DependencySet<FileDependencyObject, FileDependencyObject>>();

		final List<ObjFile> allProjectFiles;
		if(this.analysis.isTemporalConsistencyForced()){
			allProjectFiles = new ObjFileDAO().getAllAddedFilesUntilEntry(entry.getProject(), entry);
		} else {
			allProjectFiles = new ObjFileDAO().getAllAddedFilesUntilRevision(entry.getProject(), entry.getRevision());
		}

		for (int i = 0; i < changedFiles.size(); i++) {
			if(filter.match(changedFiles.get(i).getPath())){
				final FileDependencyObject changedFileDO = findFileDependencyObjectInstance(changedFiles.get(i), entry);
				final Map<FileDependencyObject, Integer> dependenciesMap = new HashMap<FileDependencyObject, Integer>();
				
				int j = 0;
				for (ObjFile objFile : allProjectFiles) {
					j++;
					System.out.println("currently processing "+j+" of "+changedFiles.size()+" ("+i+"th iteration)");
					if(filter.match(objFile.getPath())){
						final FileDependencyObject otherFileDO = findFileDependencyObjectInstance(objFile, entry);
						final ObjFile otherFile;
						
						if(this.analysis.isTemporalConsistencyForced()){
							otherFile = new ObjFileDAO().findFileByPathUntilEntry(entry.getProject(), entry, otherFileDO.getFilePath());
						} else {
							otherFile = new ObjFileDAO().findFileByPathUntilRevision(entry.getProject(), entry.getRevision(), otherFileDO.getFilePath());
						}
						
						if(StructuralCouplingIdentifier.checkStructuralCoupling(otherFile, changedFileDO.getFile())){
							final DependencySet<FileDependencyObject, FileDependencyObject> dependencySet = new DependencySet<FileDependencyObject, FileDependencyObject>();
							dependencySet.setDependedObject(changedFileDO);
							dependenciesMap.put(otherFileDO, 1);
							dependencySet.setDependenciesMap(dependenciesMap);
							setOfDependencySets.add(dependencySet);
						}
					}
				}
			}
		}
		
		return setOfDependencySets;
	}
	
	private Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> gatherTaskAssignmentDependencies(Author author, Set<DependencySet<FileDependencyObject, FileDependencyObject>> fileDependencies) throws DatabaseException {
		final AuthorDependencyObjectDAO authorDependencyDAO = new AuthorDependencyObjectDAO();
		final Set<DependencySet<AuthorDependencyObject, FileDependencyObject>> dependenciesSet = new HashSet<DependencySet<AuthorDependencyObject, FileDependencyObject>>();
		
		final AuthorDependencyObject dependedAuthor;
		if(authorsStampsMap.containsKey(author.getName())){
			dependedAuthor = (AuthorDependencyObject) authorsStampsMap.get(author.getName());
		} else {
			latestAuthorStampAssigned++;
			
			dependedAuthor = new AuthorDependencyObject();
			dependedAuthor.setAnalysis(analysis);
			dependedAuthor.setAssignedStamp(latestAuthorStampAssigned);
			dependedAuthor.setAuthor(author);
			
			authorDependencyDAO.insert(dependedAuthor);
			authorsStampsMap.put(author.getName(), dependedAuthor);
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

	private FileDependencyObject findFileDependencyObjectInstance(ObjFile changedFile, Entry entry) throws DatabaseException {
		FileDependencyObject dependencyObject;
		FileDependencyObjectDAO dependencyObjDAO = new FileDependencyObjectDAO();

		dependencyObject = new FileDependencyObjectDAO().findDependencyObjectByFilePath(this.analysis, changedFile.getPath());
		if(dependencyObject == null){
			latestFileStampAssigned++;

			dependencyObject = new FileDependencyObject();
			dependencyObject.setAnalysis(analysis);
			dependencyObject.setFile(changedFile);
			dependencyObject.setFilePath(changedFile.getPath());
			dependencyObject.setAssignedStamp(latestFileStampAssigned);

			dependencyObjDAO.insert(dependencyObject);
		} else {
			if(changedFile.getOperationType() == 'A'){
				
				final ObjFile file;
				if(this.analysis.isTemporalConsistencyForced()){
					file = new ObjFileDAO().findAddedFileByPathUntilEntry(entry.getProject(), entry, changedFile.getPath());
				} else {
					file = new ObjFileDAO().findAddedFileByPathUntilRevision(entry.getProject(), entry.getRevision(), changedFile.getPath());
				}
				if(file != changedFile) {
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
		
		return dependencyObject;		
	}
	
	private Set<DependencySet<FileDependencyObject, FileDependencyObject>> gatherStructuralDependenciesOld(List<ObjFile> changedFiles) throws DatabaseException {
		FileDependencyObjectDAO dependencyObjDAO = new FileDependencyObjectDAO();
	
		//Builds the list of dependency objects
		List<FileDependencyObject> dependencyObjectList = new ArrayList<FileDependencyObject>();
		
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
						dependencyObjDAO.findDependencyObjectByFilePath(analysis, changedFile.getPath());
					
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
					else{
						dependencyObject.setFile(changedFile);
					}
				}
				
				if(dependencyObject != null){
					dependencyObjectList.add(dependencyObject);
				}
			}
		}
		
		//Builds a list of empty dependency maps
		List<Map<FileDependencyObject, Integer>> listOfDependencyMaps = 
			new ArrayList<Map<FileDependencyObject,Integer>>();
		
		for (int i = 0; i < dependencyObjectList.size(); i++) {
			Map<FileDependencyObject, Integer> dependenciesMap = 
				new HashMap<FileDependencyObject, Integer>();
			listOfDependencyMaps.add(dependenciesMap);			
		}
		
		//Fills dependency maps with coupling values from this revision
		for (int i = 0; i < dependencyObjectList.size(); i++) {
			for (int j = i + 1; j < dependencyObjectList.size(); j++) {
				//System.out.println();
				//System.out.println(dependencyObjectList.get(i).getFilePath());
				//System.out.println(dependencyObjectList.get(j).getFilePath());
				
				Iterator<Integer> couplingValues = 
					StructuralCouplingIdentifier.calcStructuralCoupling(
						dependencyObjectList.get(i), 
						dependencyObjectList.get(j)).listIterator();
				
				listOfDependencyMaps.get(j).put(dependencyObjectList.get(i), 
						couplingValues.next());
				
				listOfDependencyMaps.get(i).put(dependencyObjectList.get(j), 
						couplingValues.next());
			
				//System.out.println();
				//System.out.print("[");
				//System.out.print(listOfDependencyMaps.get(j).get(dependencyObjectList.get(i)));
				//System.out.print(",");
				//System.out.print(listOfDependencyMaps.get(i).get(dependencyObjectList.get(j)));
				//System.out.print("]");
			}
		}
		
		//Fills dependency maps (i,i) position with 1
		for (int i = 0; i < dependencyObjectList.size(); i++) {
			listOfDependencyMaps.get(i).put(dependencyObjectList.get(i),1);
		}
		
		List<FileDependencyObject> listOfOldSuppliersNotAppearingInThisRevision = 
			new ArrayList<FileDependencyObject>();
		
		//Fills new dependency maps with updated coupling values
		for (int i = 0; i < dependencyObjectList.size(); i++) {
			FileDependencyObject client = dependencyObjectList.get(i);
			//System.out.println("Client: " + client.getFilePath());
			List<FileDependencyObject> oldSuppliers = dependencyObjDAO.findSuppliers(client,dependencyObjectList);
			for (FileDependencyObject oldSupplier: oldSuppliers){
				//System.out.println("Supplier: " + oldSupplier.getFilePath());
				//System.out.println("Supplier ID: " + oldSupplier.getId());
				//System.out.println("Supplier Stamp: " + oldSupplier.getAssignedStamp());
				
				Map<FileDependencyObject, Integer> dependenciesMap = 
					new HashMap<FileDependencyObject, Integer>();
				
				List<Integer> couplingValues = 
					StructuralCouplingIdentifier.calcStructuralCoupling(
						client,oldSupplier);
				//System.out.println(couplingValues);
					
				dependenciesMap.put(client, couplingValues.iterator().next());
				
				listOfOldSuppliersNotAppearingInThisRevision.add(oldSupplier);
				listOfDependencyMaps.add(dependenciesMap);
			}
		}
		
		//Updates the list of dependency objects
		dependencyObjectList.addAll(listOfOldSuppliersNotAppearingInThisRevision);
		
		//Helping garbage collection...
		listOfOldSuppliersNotAppearingInThisRevision = null;
		
		//Builds dependency sets
		Set<DependencySet<FileDependencyObject, FileDependencyObject>> setOfDependencySets = 
			new HashSet<DependencySet<FileDependencyObject, FileDependencyObject>>();
	
		for (int i = 0; i < dependencyObjectList.size(); i++) {	
			DependencySet<FileDependencyObject, FileDependencyObject> dependencySet = 
				new DependencySet<FileDependencyObject, FileDependencyObject>();
			dependencySet.setDependedObject(dependencyObjectList.get(i));
			dependencySet.setDependenciesMap(listOfDependencyMaps.get(i));
			setOfDependencySets.add(dependencySet);
		}
		
		return setOfDependencySets;
	}
	
}