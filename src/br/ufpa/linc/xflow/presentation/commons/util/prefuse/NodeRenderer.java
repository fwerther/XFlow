package br.ufpa.linc.xflow.presentation.commons.util.prefuse;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;

public class NodeRenderer extends AbstractShapeRenderer {
	
	private Rectangle2D m_bounds = new Rectangle2D.Double();
	private double gap;
	
	public NodeRenderer(final double gap) {
		m_manageBounds = false;
		this.gap = gap;

	}

	protected Shape getRawShape(final VisualItem item) {
		final Rectangle2D itemBounds = item.getBounds();
		m_bounds.setRect(new Rectangle2D.Double(itemBounds.getX()+gap,itemBounds.getY()+gap,itemBounds.getWidth()-2*gap,itemBounds.getHeight()-2*gap));
		return m_bounds;
	}
} 