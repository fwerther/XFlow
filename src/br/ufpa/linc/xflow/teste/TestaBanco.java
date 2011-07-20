package br.ufpa.linc.xflow.teste;

import java.io.IOException;
import java.util.List;

import br.ufpa.linc.xflow.data.dao.core.AnalysisDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class TestaBanco {

	public static void main(String[] args) throws DatabaseException, IOException {

		List<Dependency> dependencies = (List<Dependency>) new br.ufpa.linc.xflow.data.dao.core.DependencyDAO().findAllDependenciesFromAnalysis(new AnalysisDAO().findById(Analysis.class, 3L));
		for (Dependency<?, ?> dependency : dependencies) {
			System.out.println("*** REVISION "+dependency.getAssociatedEntry().getRevision()+" ***");
			for (DependencySet<?, ?> dependencySet : dependency.getDependencies()) {
				if(dependencySet.getDependedObject().getObjectType() == DependencyObject.FILE_DEPENDENCY){
					System.out.println("DEPENDED FILE: "+dependencySet.getDependedObject().getDependencyObjectName()+" ("+dependencySet.getDependedObject().getAssignedStamp()+")");
					System.out.println("DEPENDENTS: ");
					for (DependencyObject dependedFile : dependencySet.getDependenciesMap().keySet()) {
						FileDependencyObject dependedFile2 = (FileDependencyObject) dependedFile;
						System.out.print("("+dependedFile2.getAssignedStamp()+") "+dependedFile2.getDependencyObjectName());
						System.out.println(" DEGREE: "+dependencySet.getDependenciesMap().get(dependedFile));
					}
				} else {
					System.out.println("DEPENDED AUTHOR: "+dependencySet.getDependedObject().getDependencyObjectName()+" ("+dependencySet.getDependedObject().getAssignedStamp()+")");
					System.out.println("DEPENDENTS: ");
					for (DependencyObject dependedFile : dependencySet.getDependenciesMap().keySet()) {
						FileDependencyObject dependedFile2 = (FileDependencyObject) dependedFile;
						System.out.print("("+dependedFile2.getAssignedStamp()+") "+dependedFile2.getDependencyObjectName());
						System.out.println(" DEGREE: "+dependencySet.getDependenciesMap().get(dependedFile));
					}
				}
				
				System.out.println("-------------------");
			}
			System.out.println();
		}
	}

}
