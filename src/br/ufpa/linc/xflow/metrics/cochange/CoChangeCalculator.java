package br.ufpa.linc.xflow.metrics.cochange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesAnalysis;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class CoChangeCalculator {

	public void calculate(CoChangesAnalysis analysis) throws DatabaseException{
		FileDependencyObjectDAO fileDependencyDAO = 
			new FileDependencyObjectDAO();

		//Obtains the last dependency from the analysis
		Dependency<FileDependencyObject,FileDependencyObject> lastDependency = 
			getLastDependency(analysis);
		
		//Builds the Co-Change Matrix
		Matrix matrix = analysis.processHistoricalDependencyMatrix(lastDependency);
		
		System.out.println(matrix);
		
		List<CoChange> coChangeList = getCoChangeList(analysis,
				fileDependencyDAO, matrix);
		
		//Prints Co-Changes
		for(CoChange coChange : coChangeList){
			System.out.println(coChange);
		}
	}

	private Dependency<FileDependencyObject,FileDependencyObject> getLastDependency(Analysis analysis) throws DatabaseException{
		DependencyDAO dependencyDAO = new DependencyDAO();
		
		Dependency<FileDependencyObject, FileDependencyObject> lastDependency = 
			dependencyDAO.findHighestDependencyByEntry(
				analysis.getId(), 
				analysis.getLastEntry().getId(), 
				Dependency.TASK_DEPENDENCY);
		
		return lastDependency; 
	}

	private List<CoChange> getCoChangeList(CoChangesAnalysis analysis,
			FileDependencyObjectDAO fileDependencyDAO, Matrix matrix)
			throws DatabaseException {
		
		//Retrieves all file paths
		List<String> filePathList = 
			fileDependencyDAO.getFilePathsOrderedByStamp(analysis);
		
		ArrayList<String> backup = new ArrayList<String>();
		
		//Builds the list of Co-Changes
		List<CoChange> coChangeList = new ArrayList<CoChange>();
		for (int i = 0; i < matrix.getRows(); i++) {
			String a = filePathList.get(i);
			int aChanges = matrix.get(i,i);
			
			for (int j = i+1; j < matrix.getColumns(); j++) {
				int support = matrix.get(i,j);
				
				if(support > 0){
					String b = filePathList.get(j);
					int bChanges = matrix.get(j,j);
					
					coChangeList.add(new CoChange(a, b, support, aChanges));
					coChangeList.add(new CoChange(b, a, support, bChanges));
				}
			}
		}
		return coChangeList;
	}
}