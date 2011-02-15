package br.ufpa.linc.xflow.presentation.commons.util.prefuse;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorMap;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class FillColorAction extends ColorAction {
	private ColorMap cmap;
	private ColorMap smap;

	public FillColorAction(final String group, final ColorMap cmap, final ColorMap smap) {
		super(group, VisualItem.FILLCOLOR);
		this.cmap = cmap;
		this.smap = smap;
	}

	public int getColor(final VisualItem item) {
		if ( item instanceof NodeItem ) {
			final NodeItem nitem = (NodeItem)item;

			if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {    

				return smap.getColor(nitem.getDepth());
			} else {
				return cmap.getColor(nitem.getDepth());
			}
		} else {
			return cmap.getColor(0);
		}
	}

}