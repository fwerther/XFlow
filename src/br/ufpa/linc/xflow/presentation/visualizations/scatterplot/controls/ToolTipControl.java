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
 *  ===================
 *  ToolTipControl.java
 *  ===================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls;

import java.awt.event.MouseEvent;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

public class ToolTipControl extends ControlAdapter {

	private final String tooltipFields[] = new String[]{
		"Density", "Revision"	
	};

	public ToolTipControl(){

	}

	public void itemEntered(VisualItem item, MouseEvent e){
		Display display = (Display)e.getSource();
		if(tooltipFields.length == 1){
			display.setToolTipText(item.getString(tooltipFields[0]));
		} 
		else {
			
			StringBuilder builder = new StringBuilder();
			
			builder.delete(0, builder.length());
			for(int i = 0; i < tooltipFields.length; i++)
				if(item.canGetString(tooltipFields[i])){
					if(builder.length() > 0){
						builder.append("; ");
					}
					builder.append(item.getString(tooltipFields[i]));
				}
			display.setToolTipText(builder.toString());
		}
	}

	public void itemExited(VisualItem item, MouseEvent e){
		Display d = (Display)e.getSource();
		d.setToolTipText(null);
	}
	
}
