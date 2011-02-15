package br.ufpa.linc.xflow.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.dao.ProjectDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class StatisticsIOUtil {

	public static void createFileModificationHistogram(Project project, String destinationPath) throws DatabaseException, IOException{
		HashMap<Integer, Integer> filesModificatedPerEntryMap = new HashMap<Integer, Integer>();
		List<Entry> projectEntries = new EntryDAO().getAllProjectEntries(project);
		FileWriter writer = new FileWriter(destinationPath);
		
		for (Entry entry : projectEntries) {
//			if(filesModificatedPerEntryMap.containsKey(entry.getEntryFiles().size())){
//				final int previousValue = filesModificatedPerEntryMap.get(entry.getEntryFiles().size());
//				filesModificatedPerEntryMap.put(entry.getEntryFiles().size(), previousValue+1);
//			}
//			else{
//				filesModificatedPerEntryMap.put(entry.getEntryFiles().size(), 1);
//			}
			writer.append("\""+entry.getEntryFiles().size()+"\"\r\n");
		}
		

//		writer.append("\"Number of files changed\";\"Times occurred\"\r\n");
//		for (Integer mapKey : filesModificatedPerEntryMap.keySet()) {
//			writer.append("\""+mapKey+"\";");
//			writer.append("\""+filesModificatedPerEntryMap.get(mapKey)+"\"\r\n");
//		}
		
		writer.flush();
		writer.close();
	}
	
	public static void main(String[] args) throws DatabaseException, IOException {
		Project p = new ProjectDAO().findById(Project.class, 6L);
		StatisticsIOUtil.createFileModificationHistogram(p, "D:\\xflow\\teste\\histogram\\pmd-histogram-data.csv");
	}
}
