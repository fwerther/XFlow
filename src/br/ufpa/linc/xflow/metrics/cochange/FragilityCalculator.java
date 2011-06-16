package br.ufpa.linc.xflow.metrics.cochange;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class FragilityCalculator {

	public void calculate(Project project, int numFiles) throws DatabaseException{
		
		EntryDAO entryDAO = new EntryDAO();
		ObjFileDAO objFileDAO = new ObjFileDAO();
		
		List<Double> listAvgDistance = new ArrayList<Double>();
		
		//Get entries with a limited number of files
		List<Entry> entries = entryDAO.getEntriesLimitedByNumFiles(project,numFiles);
		
		for (Entry entry : entries){
			List<String> filePaths = objFileDAO.getFilePathsFromEntry(entry);
			//System.out.println(filePaths);
			double avgDistance = getAvgDistance(filePaths);
			System.out.println(entry.getId() + " " + avgDistance);
			listAvgDistance.add(avgDistance);
		}
		
		//System.out.println(listAvgDistance.size());
		//System.out.println(listAvgDistance);
	}
	
	public double getAvgDistance(List<String> filePaths){ 
		double totalDistance = 0;
		double avgDistance = 0; 
		int pairsOfFiles = 0;
		int numFiles = filePaths.size();		
		for (int i = 0; i < numFiles; i++){
			for (int j = i + 1; j < numFiles; j++){
				String leafNode1 = filePaths.get(i);
				String leafNode2 = filePaths.get(j);
				String lca = getLCA(leafNode1, leafNode2);
				totalDistance += calcDistance(leafNode1, leafNode2, lca);
				pairsOfFiles++;
			}
		}
		if (pairsOfFiles > 0){
			avgDistance = totalDistance/pairsOfFiles;
		}
		//System.out.println(avgDistance);
		return avgDistance;
	}

	private String getLCA(String leafNode1, String leafNode2) {
		int i = 0;
		while(i < leafNode1.length() && i < leafNode2.length() && 
				leafNode1.charAt(i) == leafNode2.charAt(i)){
			i++;
		}
		//Grab the Lowest Common Ancestor
		String lca = leafNode1.substring(0, i-1);
		return lca;
	}

	private double calcDistance(String leafNode1, String leafNode2, String lca){
		//System.out.println(leafNode1);
		//System.out.println(leafNode2);
		
		int d1 = 
			StringUtils.countMatches(StringUtils.remove(leafNode1, lca),"/");
		int d2 = 
			StringUtils.countMatches(StringUtils.remove(leafNode2, lca),"/");
		//System.out.println(d1 + d2);
		return d1 + d2;
	}
		
	
}
