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
 *  ==================
 *  SliderControl.java
 *  ==================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.commons;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import br.ufpa.linc.xflow.data.dao.EntryDAO;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.Visualizer;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class SliderControl extends JSlider implements ChangeListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4229643512099615047L;

	public SliderControl(long lowestValue, long highestValue) {
		this.addChangeListener(this);
		this.addMouseListener(this);
		this.setMaximum((int) highestValue);
		this.setMinimum((int) lowestValue+1);
		this.setValue((int) highestValue);
	}

	public void stateChanged(ChangeEvent sliderChange) {

		SliderControl source = (SliderControl) sliderChange.getSource();

		if (source.getValueIsAdjusting()) {
			if (Visualizer.getScatterPlotView() != null){
				Visualizer.getScatterPlotView().getScatterPlotRenderer().getDisplay().setHighQuality(false);
			}
			if (Visualizer.getTreeMapView() != null){
				Visualizer.getTreeMapView().getTreeMapRenderer().getDisplay().setHighQuality(false);
				Visualizer.getTreeMapView().getTreeMapNewLayout().getDisplay().setHighQuality(false);
			}
			if (Visualizer.getGraphView() != null){
				Visualizer.getGraphView().getGraphRenderer().getDisplay().setHighQuality(false);
			}
			if (Visualizer.getLineView() != null){
				Visualizer.getLineView().getZoomer().getZoomSlider().setMaximum(this.getValue());
				Visualizer.getLineView().getLineChartRenderer().getChart().getXYPlot().getDomainAxis().setRange(this.getMinimum(), Visualizer.getLineView().getZoomer().getZoomSlider().getHighValue());
			}
		}
	}

	public Component getSliderPanel() {
		return this;	
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		try {
			if (Visualizer.getScatterPlotView() != null){
				Visualizer.getScatterPlotView().getScatterPlotRenderer().getxAxisQueryBinding().getNumberModel().setValueRange(this.getMinimum() , getValue(), this.getMinimum(), getValue());
				Visualizer.getScatterPlotView().getScatterPlotRenderer().getDisplay().repaint();
				Visualizer.getScatterPlotView().getScatterPlotRenderer().getDisplay().setHighQuality(true);
				Visualizer.getAnalysisInfoBar().getLastRevisionLabel().setText(Visualizer.getAnalysisInfoBar().LAST_REVISION_LABEL_TEXT + " ("+this.getValue()+")");
			}
			if (Visualizer.getTreeMapView() != null){
				Visualizer.getTreeMapView().getTreeMapRenderer().updateTree(getValue());
				Visualizer.getTreeMapView().getTreeMapNewLayout().updateTree(getValue());
				Visualizer.getTreeMapView().getTreeMapNewLayout().getDisplay().setHighQuality(true);
				Visualizer.getTreeMapView().getTreeMapRenderer().getDisplay().setHighQuality(true);
			}
			if (Visualizer.getGraphView() != null){
				Visualizer.getGraphView().getGraphRenderer().updateGraph(getValue());
				Visualizer.getGraphView().getGraphRenderer().getDisplay().setHighQuality(true);
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		Date selectedRevisionDate;
		
		try {
			if(AbstractVisualization.getCurrentAnalysis().isTemporalConsistencyForced()){
				selectedRevisionDate = new EntryDAO().findEntryFromSequence(AbstractVisualization.getCurrentAnalysis().getProject(), getValue()).getDate();
			} else {
				selectedRevisionDate = new EntryDAO().findEntryFromRevision(AbstractVisualization.getCurrentAnalysis().getProject(), getValue()).getDate();
			}
		} catch (DatabaseException e) {
			selectedRevisionDate = null;
			e.printStackTrace();
		}

		Visualizer.getAnalysisInfoBar().getLastRevisionDateLabel().setText(Visualizer.getAnalysisInfoBar().LAST_DATE_LABEL_TEXT + " (" + selectedRevisionDate + ")");
	}

}
