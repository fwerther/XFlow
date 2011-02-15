package br.ufpa.linc.xflow.presentation.commons.util.prefuse;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;

public class LabelLayout extends Layout {
	
	double spacing;
	
	public LabelLayout(final String group, final double spacing) {
		super(group);
		this.spacing=spacing;
	}
	
	public void run(double frac) {
		final Iterator<?> iter = m_vis.items(m_group);
		while ( iter.hasNext() ) {

			DecoratorItem item = (DecoratorItem) iter.next();		
			Rectangle2D boundsLabel = item.getBounds();
			VisualItem node = item.getDecoratedItem();
			Rectangle2D bounds = node.getBounds();		
			setX(item, null, bounds.getX()+boundsLabel.getWidth()/2.+spacing);
			setY(item, null, bounds.getY()+boundsLabel.getHeight()/2.+spacing);
		}
	}
}