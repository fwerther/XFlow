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
 *  =====================
 *  TreeMapNewLayout.java
 *  =====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.treemap;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.presentation.commons.util.ColorPalette;

public class TreeMapColorAction extends ColorAction {

	private final ColorMap cmap;
	private final ColorMap cmap2;

	public TreeMapColorAction(String group, int treeDepth) {
		super(group, VisualItem.FILLCOLOR);
		this.cmap = new ColorMap(ColorLib.getInterpolatedPalette(treeDepth+1,
				ColorLib.rgb(160,160,160),ColorLib.rgb(104,104,104)), 0, treeDepth);
		
		this.cmap2 = new ColorMap(ColorLib.getInterpolatedPalette(7,
				ColorLib.rgb(255,255,0),ColorLib.rgb(105,5,0)), 0, 7);

	}


	public int getColor(VisualItem item) {
		int result = 0;
		int depth = 0;

		if ( item instanceof NodeItem ) {
			NodeItem nitem = (NodeItem)item;

			depth = nitem.getDepth();
			result = cmap.getColor(depth);

			if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS) ) {
				for(int i=0; i < Visualizer.getDevelopersPanel().getCheckBoxList().getModel().getSize(); i++){
					if(Visualizer.getDevelopersPanel().getCheckBoxList().getSelectionModel().isSelectedIndex(i)){
						return ColorPalette.getAuthorsColorPalette()[i];
					}
				}
			}

			if ( m_vis.isInGroup(item, "search2") ) {
				return cmap2.getColor(JCustomSearchPanel.fileChangeRate.get(item.get("name")));
			}
		} 
		else {
			return cmap.getColor(0);
		}
		return result;
	}
}
