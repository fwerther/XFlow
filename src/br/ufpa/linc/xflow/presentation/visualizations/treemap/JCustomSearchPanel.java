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
 *  =======================
 *  EntryPointsControl.java
 *  =======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.treemap;

import java.util.HashMap;
import java.util.List;

import prefuse.data.search.SearchTupleSet;
import prefuse.util.ui.JSearchPanel;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class JCustomSearchPanel extends JSearchPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7486289014949717454L;

	public static HashMap<String, Integer> fileChangeRate;
	
	public JCustomSearchPanel(SearchTupleSet search, String field) {
		super(search, field);
		this.setLabelText("Go to commit:");
	}

	public JCustomSearchPanel(SearchTupleSet search, String field, boolean monitorKeystrokes) {
		super(search, field, monitorKeystrokes);
	}


	@Override
	protected void searchUpdate() {
        String query = m_queryF.getText();
        if(query.trim().length() > 0){
        	final List<String> filePaths = null;
			try {
				//TODO: CORRIGIR! ATÉ QUARTA!!
				if(AbstractVisualization.getCurrentAnalysis().isTemporalConsistencyForced()){
					//filePaths = new ObjFileDAO().getFilesPathFromSequenceNumber(AbstractVisualization.getCurrentAnalysis().getProject(), Long.parseLong(query));
				} else {
					//filePaths = new ObjFileDAO().getFilesPathFromRevisionNumber(AbstractVisualization.getCurrentAnalysis().getProject(), Long.parseLong(query));
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			//} catch (DatabaseException e) {
			//	e.printStackTrace();
			}
			fileChangeRate = new HashMap<String, Integer>();
        	StringBuilder filesName = new StringBuilder();
        	for (String filePath : filePaths) {
        		if(fileChangeRate.containsKey(filePath)){
        			fileChangeRate.put(filePath, fileChangeRate.get(filePath)+1);
        		}
        		else{
        			fileChangeRate.put(filePath, 1);
            		filesName.append(filePath);
            		filesName.append(" | ");
        		}
        	}
        	query = filesName.toString();
        	synchronized ( m_lock ) {
        		m_searcher.search(query);
        		if ( m_searcher.getQuery().length() == 0 )
        			m_resultL.setText(null);
        		else {
        			int r = m_searcher.getTupleCount();
        			m_resultL.setText((new StringBuilder(String.valueOf(r))).append(" match").append(r != 1 ? "es" : "").toString());
        		}
        	}
        }
	}
	
	

}
