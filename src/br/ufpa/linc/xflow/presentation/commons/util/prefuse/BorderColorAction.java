package br.ufpa.linc.xflow.presentation.commons.util.prefuse;

import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class BorderColorAction extends ColorAction {


	public BorderColorAction(final String group) {
		super(group, VisualItem.STROKECOLOR);

	}

	public int getColor(final VisualItem item) {
		final NodeItem nitem = (NodeItem)item;
		if ( nitem.isHover() )
			return ColorLib.rgb(99,130,191);

		int depth = nitem.getDepth();
		if ( depth < 2 ) {
			return ColorLib.gray(100);
		} else if ( depth < 4 ) {
			return ColorLib.gray(75);
		} else {
			return ColorLib.gray(50);
		}
	}
}
