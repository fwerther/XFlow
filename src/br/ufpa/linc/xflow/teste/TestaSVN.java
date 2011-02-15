package br.ufpa.linc.xflow.teste;

import org.tmatesoft.svn.core.SVNException;

import br.ufpa.linc.xflow.data.dao.ProjectDAO;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.exception.cm.svn.SVNProtocolNotSupportedException;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public class TestaSVN {

	public static void main(String[] args) {
		try {
			System.out.println(br.ufpa.linc.xflow.cm.DataExtractor.checkForSVNDatesInconsistency(new ProjectDAO().findById(Project.class, 1L)));
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SVNProtocolNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
