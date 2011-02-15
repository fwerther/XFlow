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
 *  LineChartZoomer.java
 *  ====================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.line.controls;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import prefuse.util.ui.JRangeSlider;
import br.ufpa.linc.xflow.presentation.Visualizer;

public class LineChartZoomer implements LineViewController, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2790925597150859161L;
	
	private final JRangeSlider zoomSlider;
	
	public LineChartZoomer(int minimum, int maximum) {
		zoomSlider = new JRangeSlider(minimum, maximum, minimum, maximum, JRangeSlider.HORIZONTAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
		zoomSlider.setThumbColor(null);
		setupZoomSliderMouseEvent();
	}
	
	@Override
	public javax.swing.JComponent getControlComponent() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(zoomSlider);
		panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5,0,0,0));
		return panel;
	}

	private void setupZoomSliderMouseEvent() {
		zoomSlider.addMouseMotionListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot().getDomainAxis().setRange(zoomSlider.getLowValue(), zoomSlider.getHighValue());
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	public JRangeSlider getZoomSlider() {
		return zoomSlider;
	}
	
}
