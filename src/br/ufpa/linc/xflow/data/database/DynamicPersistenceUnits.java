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
 *  ===========================
 *  DynamicPersistenceUnit.java
 *  ===========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  Pedro Treccani, David Bentolila;
 *  
 */

package br.ufpa.linc.xflow.data.database;


import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;

public class DynamicPersistenceUnits {

    public static EntityManagerFactory createEMF(final Class<?>[] entityClasses, final String dialect, final String host, final String port, final String dataBaseName, final String dataBaseUser, final String password) {
    	final Ejb3Configuration ejb3conf = new Ejb3Configuration();

            ejb3conf.setProperty("hibernate.hbm2ddl.auto", "update");
            ejb3conf.setProperty("hibernate.format_sql", "true");
            //ejb3conf.setProperty("hibernate.show_sql", "true");
            ejb3conf.setProperty("hibernate.dialect", dialect);
            ejb3conf.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
            ejb3conf.setProperty("hibernate.connection.url", "jdbc:mysql://" + host + ":" + port + "/" + dataBaseName);
            ejb3conf.setProperty("hibernate.connection.username", dataBaseUser);
            ejb3conf.setProperty("hibernate.connection.password", password);

        for (int i = 0; i < entityClasses.length; i++) {
            assert entityClasses[i] != null;
            ejb3conf.addAnnotatedClass(entityClasses[i]);
        }

        return ejb3conf.buildEntityManagerFactory();
    }
}
