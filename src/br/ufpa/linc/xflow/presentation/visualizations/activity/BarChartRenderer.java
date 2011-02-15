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
 *  ======================
 *  BarsChartRenderer.java
 *  ======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.activity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

import br.ufpa.linc.xflow.data.dao.AuthorDAO;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.util.ColorPalette;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class BarChartRenderer {

	private static final double bar_width = 0.45D;
	private String[] authorsNames;
	
	private JFreeChart chart;
	private XYIntervalSeriesCollection dataset;
	
	private HashMap<String, Integer> seriesMapper;
	
	public BarChartRenderer() throws DatabaseException {
		createDatasets();
	}

	public JPanel draw() {
		JPanel barsChartPanel = createBarsChart();
		return barsChartPanel;
	}

	private JPanel createBarsChart() {
		IntervalXYDataset plottedData = dataset;
		chart = ChartFactory.createXYBarChart("Activity Chart", "xAxis", true, "yAxis", plottedData, PlotOrientation.HORIZONTAL, false, false, false);
		XYPlot localXYPlot = (XYPlot)chart.getPlot();
		
		
		/*
		 * SETUP BACKGROUND AND GRID COLORS.
		 */
		setupGeneralChartAppearence(localXYPlot);
		
		
		/*
		 * SETUP SERIES APPEARENCE.
		 * (e.g. line shape and color)
		 */
		setupSeriesRenderer(localXYPlot);


		/*
		 * SETUP AXES. 
		 */
		setupAxes(localXYPlot);


		return new ChartPanel(chart);	
	}
	
	private void setupGeneralChartAppearence(XYPlot xyplot) {
		xyplot.setBackgroundPaint(Color.white);
		xyplot.setDomainGridlinePaint(Color.black);
		xyplot.setRangeGridlinePaint(Color.black);
		xyplot.setDomainPannable(true);
		xyplot.setRangePannable(true);
		
	    ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
	    ChartUtilities.applyCurrentTheme(chart);
	}

	private void setupSeriesRenderer(XYPlot xyplot) {
		XYBarRenderer lineAndShapeRenderer = (XYBarRenderer)xyplot.getRenderer();
		lineAndShapeRenderer.setBarPainter(new StandardXYBarPainter());
		lineAndShapeRenderer.setUseYInterval(true);
		lineAndShapeRenderer.setShadowVisible(false);
		for (int i = 0; i < xyplot.getSeriesCount(); i++) {
			lineAndShapeRenderer.setSeriesPaint(i, new Color(ColorPalette.getAuthorsColorPalette()[i]));
			lineAndShapeRenderer.setSeriesStroke(i, new BasicStroke((float)1));
			lineAndShapeRenderer.setSeriesFillPaint(i, new Color(ColorPalette.getAuthorsColorPalette()[i]));
			lineAndShapeRenderer.setSeriesOutlinePaint(i, Color.black);
		}
		 xyplot.setRenderer(lineAndShapeRenderer);
	}
	
	private void setupAxes(XYPlot xyplot) {
		xyplot.setRangeAxis(new DateAxis("Date"));
		
		xyplot.setDomainAxis(0, createDomainAxis());
		xyplot.setRangeAxis(0, createRangeAxis());

		List<Integer> localList = Arrays.asList(new Integer[] { new Integer(0), new Integer(1) });
		xyplot.mapDatasetToDomainAxes(0, localList);
		xyplot.mapDatasetToRangeAxes(0, localList);
	}

	private ValueAxis createRangeAxis() {
		DateAxis rangeAxis = new DateAxis("Date");
		rangeAxis.setDateFormatOverride(new SimpleDateFormat("dd-MMM-yyyy"));
//		rangeAxis.setLowerMargin(0.05);
//		rangeAxis.setUpperMargin(0.05);
//		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//		rangeAxis.setLabelFont(new Font("Helvetica", Font.BOLD, 15));
//		rangeAxis.setLabelInsets(new RectangleInsets(20,20,20,20));
		return rangeAxis;
	}

	private ValueAxis createDomainAxis(){
	    SymbolAxis domainAxis = new SymbolAxis("Series", this.authorsNames);
	    domainAxis.setGridBandsVisible(false);
	    domainAxis.setVisible(false);
//		domainAxis.setLabelFont(new Font("Helvetica", Font.BOLD, 15));
//		domainAxis.setLabelInsets(new RectangleInsets(20,20,20,20));
		
	    return domainAxis;
	}

	private void createDatasets() throws DatabaseException{

		List<Author> authorsList = new AuthorDAO().getProjectAuthors(AbstractVisualization.getCurrentAnalysis().getProject().getId());
		this.dataset = new XYIntervalSeriesCollection();
		
		Calendar calendar = Calendar.getInstance();
		int i = 0;
		
		seriesMapper = new HashMap<String, Integer>();
		ArrayList<String> names = new ArrayList<String>();
		
		for (Author author : authorsList) {
			
			if(!seriesMapper.containsKey(author.getName())){
				seriesMapper.put(author.getName(), seriesMapper.size());
			}
			
			Date authorStartDate = author.getStartDate();
			calendar.setTime(authorStartDate);
			final Day authorInitialDay = new Day(authorStartDate);

			Date authorLastContributionDate = author.getLastContribution();
			calendar.setTime(authorLastContributionDate);
			final Day authorLastDay = new Day(authorLastContributionDate);
			
			if(authorStartDate.compareTo(authorLastContributionDate) <= 0){
				names.add(author.getName());
				XYIntervalSeries authorSerie = new XYIntervalSeries(author.getName());
				authorSerie.add(i, i - bar_width, i + bar_width, authorInitialDay.getFirstMillisecond(), authorInitialDay.getFirstMillisecond(), authorLastDay.getLastMillisecond());
				dataset.addSeries(authorSerie);
				i++;
			}
			else{
				names.add(author.getName());
				XYIntervalSeries authorSerie = new XYIntervalSeries(author.getName());
				dataset.addSeries(authorSerie);
			}
		}
		this.authorsNames = names.toArray(new String[]{});
	}

	public JFreeChart getChart() {
		return chart;
	}
	
	public void updateSeriesVisibility(String visibleSeries){
		String[] toShowSeries = visibleSeries.split("\\|");
		
		for(int i = 0; i < this.getChart().getXYPlot().getSeriesCount(); i++){
			this.getChart().getXYPlot().getRenderer().setSeriesVisible(i, false);
		}
		
		for (int i = 0; i < toShowSeries.length-1; i++) {
			this.getChart().getXYPlot().getRenderer().setSeriesVisible(seriesMapper.get(toShowSeries[i].trim()), true);
		}
	}
}
