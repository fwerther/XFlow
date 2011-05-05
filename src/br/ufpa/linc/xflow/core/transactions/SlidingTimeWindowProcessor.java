package br.ufpa.linc.xflow.core.transactions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import br.ufpa.linc.xflow.data.dao.cm.AuthorDAO;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.database.DatabaseManager;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class SlidingTimeWindowProcessor {

	private static Map<String, ObjFile> addedFilesMap;
	
	public static void process(Project project, int seconds) throws DatabaseException{
		addedFilesMap = new HashMap<String, ObjFile>();
		EntryDAO entryDAO = new EntryDAO();
		AuthorDAO authorDAO = new AuthorDAO();
				
		List<Author> authorList = authorDAO.getProjectAuthors(project.getId());		
		for(Author author : authorList){
			System.out.println("author "+author.getName());
			
			List<Entry> entryList = entryDAO.getNonBlankEntriesByAuthorSortedByDate(project, author);
			
			if (!entryList.isEmpty()){
				
				List<ObjFile> windowFiles = new ArrayList<ObjFile>();
				
				//Inserts a dummy entry at the end of the list
				Entry dummyEntry = new Entry();
				entryList.add(dummyEntry);
				
				ListIterator<Entry> entryIterator = entryList.listIterator();
				Entry previousEntry = entryIterator.next();
				
				System.out.println("processing entry "+ previousEntry.getId());
				
				while (entryIterator.hasNext()){
				
					windowFiles.addAll(previousEntry.getEntryFiles());
					Entry entry = entryIterator.next();
					System.out.println("comparing with "+entry.getId());
					
					if(entriesOnSameWindow(entry, previousEntry, seconds)){
						System.out.println("entries on same window");						
					}					
					//New transaction
					else{
						System.out.println("entries not on same window.");
						Entry window = createWindow(windowFiles);
						saveWindow(window);
						
						//Starting a new window
						windowFiles.clear();						
					}
					
					previousEntry = entry;
					System.out.println("processing entry "+previousEntry.getId());
				}
			}
		}	
	}

	private static boolean entriesOnSameWindow(Entry entry, Entry previousEntry, 
			int windowSize) {
		
		//Same comments
		boolean sameComments = 
			previousEntry.getComment().equals(entry.getComment()); 

		//Inside time window
		Date limit = DateUtils.addSeconds(previousEntry.getDate(), windowSize);
		boolean insideTimeWindow = 
			entry.getDate() != null && !entry.getDate().after(limit);
			
		//Entries on same window
		boolean entriesOnSameWindow = sameComments && insideTimeWindow;
		return entriesOnSameWindow;
	}

	private static void saveWindow(Entry window){
		EntryDAO entryDAO = new EntryDAO();
		try{
		
			//Detaching all objFiles (to force insertion)
			for(ObjFile file : window.getEntryFiles()){
				DatabaseManager.getDatabaseSession().detach(file);					
				file.setId(0);
			}
			
			//Insert entry and associated files
			entryDAO.insert(window);
		}catch(Exception e){
			e.printStackTrace();
		}		
	}

	private static Entry createWindow(List<ObjFile> windowFiles) throws DatabaseException {
		
		//Sliding time window
		int indexLastFile = windowFiles.size() - 1;
		Entry lastEntry = windowFiles.get(indexLastFile).getEntry();
		Entry window = new Entry();
		window.setAuthor(lastEntry.getAuthor());
		window.setComment(lastEntry.getComment());
		window.setDate(lastEntry.getDate());
		window.setProject(lastEntry.getProject());
		window.setRevision(lastEntry.getRevision());
		
		//The list of consolidated files
		List<ObjFile> windowConsolidatedFiles = 
			new ArrayList<ObjFile>();
		
		//Sort by filepath
		Collections.sort(windowFiles, new FileComparator());
		
		//Inserts a dummy file at the end of the list
		ObjFile dummyFile = new ObjFile();
		windowFiles.add(dummyFile);
		
		ListIterator<ObjFile> fileIterator = windowFiles.listIterator();
		ObjFile previousFile = fileIterator.next();
		ObjFile lastVersion = null;
		
		while(fileIterator.hasNext()){
			ObjFile file = fileIterator.next();
			
			//If same paths, then file is a new "version" of previousFile
			if(previousFile.getPath().equals(file.getPath())){
				lastVersion = file;
			}
			else{
				//At least two "versions" of the same file
				if(lastVersion != null){
					ObjFile consolidatedFile = 
						handleFiles(previousFile,lastVersion);
					
					windowConsolidatedFiles.add(consolidatedFile);
					consolidatedFile.setEntry(window);
					
					lastVersion = null;
				}
				//Just another file
				else{
					windowConsolidatedFiles.add(previousFile);
					previousFile.setEntry(window);
				}
				
				previousFile = file;
			}
		}
		
		window.setEntryFiles(windowConsolidatedFiles);
		return window;
	}
	
	private static ObjFile handleFiles(ObjFile firstFile, ObjFile lastFile) throws DatabaseException {

		/**
		The following rules apply to create a consolidated Entry:
		[i]   (D, D) = D
		[ii]  (D, A) = A
		[iii] (D, M) = M 
		[iv]  (A, A) = A 
		[v]   (A, M) = A
		[vi]  (A, D) = D 
		[vii] (M, M) = M
		[viii](M, A) = M
		[ix]  (M, D) = D 
		*/
		
		//[i],[ii],[iii]
		if (firstFile.getOperationType() == 'D'){
			if(lastFile.getOperationType() == 'D'){
				//Updating the deletedOn property to refer to the last entry
				ObjFile referredAddedFile = addedFilesMap.get(lastFile.getPath());
				referredAddedFile.setDeletedOn(lastFile.getEntry());
				new ObjFileDAO().update(referredAddedFile);
			}
			//TODO: Check loc stuff
			return lastFile;
		}
		
		//[iv],[v],[vi]
		if (firstFile.getOperationType() == 'A'){
			//We treat the file as if it had never existed
			if(firstFile.getOperationType() == 'D'){
				return null;
			} else {
				//In case of (A,M), we treat the file as if it was added
				lastFile.setOperationType('A');
				
				//FIXME: Shouldn't it be lastFile.getPath()?
				addedFilesMap.put(lastFile.getPath(), lastFile);
				
				//TODO: Check loc stuff
				return lastFile;
			}
		}
		
		//[vii],[viii],[ix]
		if (firstFile.getOperationType() == 'M'){
			if(lastFile.getOperationType() == 'D'){
				//Updating the deletedOn property to refer to the last entry
				ObjFile referredAddedFile = addedFilesMap.get(lastFile.getPath());
				referredAddedFile.setDeletedOn(lastFile.getEntry());
				new ObjFileDAO().update(referredAddedFile);
				
			} else {
				//In case of (M,A), we treat the file as if it was only modified
				lastFile.setOperationType('M');
			}
			//TODO: Check loc stuff
			return lastFile;
		}
		
		//Odd situation not foreseen
		return null;
	}
}

class FileComparator implements Comparator<ObjFile>{
	@Override
	public int compare(ObjFile file, ObjFile otherFile) {
		return file.getPath().compareTo(otherFile.getPath());
	}
}