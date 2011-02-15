package br.ufpa.linc.xflow.teste;

import br.ufpa.linc.xflow.data.dao.AnalysisDAO;
import br.ufpa.linc.xflow.data.dao.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class TestaBanco {

	public static void main(String[] args) throws DatabaseException {

//		br.ufpa.linc.xflow.data.database.DatabaseManager.getDatabaseSession();

//		List<Dependency> dependencies = (List<Dependency>) new DependencyDAO().findAll(Dependency.class);
		
//		List<Dependency> dependencies = new DependencyDAO().findAllDependenciesUntilIt(new DependencyDAO().findById(Dependency.class, 12L));
		
//		List<AuthorDependencyObject> dependencies = new AuthorDependencyObjectDAO().findAllDependencyObjsUntilDependency(new DependencyDAO().findById(Dependency.class, 163L));
//		List<FileDependencyObject> dependencies = new br.ufpa.linc.xflow.data.dao.FileDependencyObjectDAO().findAllDependencyObjsUntilDependency(new DependencyDAO().findById(Dependency.class, 35781));
//		
////		for (AuthorDependencyObject authorDependency : dependencies) {
//		for (FileDependencyObject authorDependency : dependencies) {
//			System.out.println("Revision: "+authorDependency.getDependencies().iterator().next().getAssociatedEntry().getRevision());
////			System.out.println("Depended author: "+authorDependency.getAuthor().getName()+" ("+authorDependency.getAssignedStamp()+")");
//			System.out.println("Depended author: "+authorDependency.getFile().getName()+" ("+authorDependency.getAssignedStamp()+")");
//			for (DependencyObject dependenciesFound : authorDependency.getDependentObjects()) {
//				System.out.print("Dependent files: ");
//				System.out.print(((FileDependencyObject) dependenciesFound).getFile().getPath());
//				System.out.print(" ("+((FileDependencyObject) dependenciesFound).getAssignedStamp()+")");
//				System.out.println(" - Degree: "+dependenciesFound.getDependenceDegree());
//			}
//		}
		
		Analysis analysis = new AnalysisDAO().findById(Analysis.class, 1L);
//		Entry entry = new EntryDAO().findById(Entry.class, 19662L);
		Entry entry = new EntryDAO().findById(Entry.class, 25L);
//		System.out.println(dependency.getDependencies().size());
//		System.out.println(dependency.getDependencies().iterator().next().getDependencyObjectName());
		
		Matrix m = analysis.processEntryDependencyMatrix(entry, br.ufpa.linc.xflow.data.entities.Dependency.FILE_FILE_DEPENDENCY);
		for (int i = 0; i < m.getRows(); i++) {
			for (int j = 0; j < m.getColumns(); j++) {
				System.out.print(m.get(i, j)+" ");
			}
			System.out.println();
		}
	}

}
