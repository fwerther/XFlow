package br.ufpa.linc.xflow.core.processors.callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufpa.linc.xflow.core.processors.DependenciesIdentifier;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.database.DatabaseManager;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.TaskDependency;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.util.Filter;

public class CallGraphCollector implements DependenciesIdentifier {

	private CallGraphAnalysis analysis;	
	private Filter filter;
	private int latestFileStampAssigned;
	
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

			System.out.print("- Processing entry: "+entry.getRevision()+" ("+revision+")\n");
			System.out.print("* Collecting file dependencies...");
					
			final Set<DependencySet<FileDependencyObject, FileDependencyObject>> structuralDependencies = 
				gatherStructuralDependencies(entry.getEntryFiles());
					
			if(structuralDependencies.size() > 0) { 
				final TaskDependency taskDependency = new TaskDependency(true);
				taskDependency.setAssociatedAnalysis(analysis);
				taskDependency.setAssociatedEntry(entry);
	
				//Sets on both sides (bi-directional association)
				taskDependency.setDependencies(structuralDependencies);
								
				dependencyDAO.insert(taskDependency);						
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

	private void initiateCache() throws DatabaseException {
		latestFileStampAssigned = new FileDependencyObjectDAO().checkHighestStamp(analysis);
	}

	private Set<DependencySet<FileDependencyObject, FileDependencyObject>> gatherStructuralDependencies(List<ObjFile> changedFiles) throws DatabaseException {
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
