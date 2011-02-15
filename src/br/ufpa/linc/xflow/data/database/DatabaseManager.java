/* 
 * 
 * XFlow
 * _______
 * 
 *  
 *  (C) Copyright 2010, by Universidade Federal do Pará (UFPA), Francisco Santana, Jean Costa, Pedro Treccani and Cleidson de Souza.
 * 
 *  This file is part of XFlow.
 *
 *  XFlow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XFlow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XFlow.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  ====================
 *  DatabaseManager.java
 *  ====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  Pedro Treccani, David Bentolila;
 *  
 */

package br.ufpa.linc.xflow.data.database;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import br.ufpa.linc.xflow.core.processors.cochanges.CoChangesAnalysis;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.entities.Folder;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.data.entities.Project;
import br.ufpa.linc.xflow.data.entities.Resource;
import br.ufpa.linc.xflow.data.entities.TaskDependency;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricValues;
import br.ufpa.linc.xflow.metrics.file.FileMetricValues;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricValues;


public class DatabaseManager {

	private static EntityManagerFactory emf = null;
	private static EntityManager em = null;
	
	private static Properties databaseProperties;
	
	private final Class<?>[] CLAZZS = new Class<?>[]{Project.class, Dependency.class,
			Author.class, Entry.class, Folder.class, ObjFile.class, Resource.class, 
			Analysis.class, CoChangesAnalysis.class, EntryMetricValues.class, 
			ProjectMetricValues.class, FileMetricValues.class, DependencyObject.class, 
			FileDependencyObject.class, AuthorDependencyObject.class, TaskDependency.class
	};
//	
//	private void loadProperties() throws DatabaseException{
//		databaseProperties = new Properties();
//		try {
//			FileInputStream fis = new FileInputStream("databaseProperties");
//			databaseProperties.load(fis);
//			fis.close();
//		} catch (FileNotFoundException e) {
//			createDefaultProperties();
//		} catch (IOException e) {
//			throw new DatabaseException("There was an error trying to access database settings.");
//		}
//	}
//	
//	public static void updateProperties(Properties newProperties) {
//		try {
//			FileOutputStream out;
//			out = new FileOutputStream("databaseProperties");
//			newProperties.store(out, "Database settings.");
//			out.close();
//		} catch (FileNotFoundException e) {
//			
//		} catch (IOException e) {
//			
//		}
//	}
//
//	private void createDefaultProperties() {
//		File propertiesFile = new File("databaseProperties");	
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(propertiesFile);
//			databaseProperties.store(fos, "Database settings.");  
//			fos.close();
//		}
//		catch (IOException exc) {
//			exc.printStackTrace();
//		}
//	}
//
	private DatabaseManager() throws DatabaseException {
//		loadProperties();
//		gotta remind to put your database password
		String host = "localhost";
		String port = "3306";
		String DBname = "xflow";
		String DBUser = "root";
		String DBpassword = "";
		String dialect = "br.ufpa.linc.xflow.data.database.XFlowMySqlDialect";
		
//		String dialect = databaseProperties.getProperty("dialect");
//		String host = databaseProperties.getProperty("host");
//		String port = databaseProperties.getProperty("port");
//		String DBname = databaseProperties.getProperty("schema");
//		String DBUser = databaseProperties.getProperty("username");
//		String DBpassword = databaseProperties.getProperty("password");

		emf = DynamicPersistenceUnits.createEMF(CLAZZS, dialect, host, port, DBname, DBUser, DBpassword);
		em = emf.createEntityManager();
	}
	
	private synchronized static EntityManagerFactory getManagerFactory() throws DatabaseException {
		if (emf == null) {
			new DatabaseManager();
		}
		return emf;
	}

	public static EntityManager getDatabaseSession() throws DatabaseException {
		if (em == null)
			em = getManagerFactory().createEntityManager();
		return em;
	}
	
}
