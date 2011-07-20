package br.ufpa.linc.xflow.teste;

import java.util.List;

import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.core.AnalysisDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.Converter;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;


public class TestaMatriz {

	public static void main(String[] args) throws br.ufpa.linc.xflow.exception.persistence.DatabaseException {
		
		Analysis a = new AnalysisDAO().findById(Analysis.class, 3L);
		List<Entry> entries = new EntryDAO().getAllEntriesWithinEntries(a.getFirstEntry(), a.getLastEntry());
		for (Entry entry : entries) {
			Matrix m1 = a.processEntryDependencyMatrix(entry, Dependency.TASK_DEPENDENCY);
			if(m1 != null){
				System.out.println("REVISION "+entry.getRevision()+" -----------------");
				for (int i = 0; i < m1.getRows(); i++) {
					for (int j = 0; j < m1.getColumns(); j++) {
						System.out.print(m1.getValueAt(i, j)+"\t");
					}
					System.out.println();
				}
				System.out.println();
				System.out.println();
			}
		}
		
	}
	
}
