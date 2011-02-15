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
 *  ========================
 *  StackedAreaRenderer.java
 *  ========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.presentation.visualizations.activity;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.StackedAreaChart;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.query.NumberRangeModel;
import prefuse.data.query.ObjectRangeModel;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AxisRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import br.ufpa.linc.xflow.data.dao.AnalysisDAO;
import br.ufpa.linc.xflow.data.dao.AuthorDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.commons.util.ColorPalette;
import br.ufpa.linc.xflow.presentation.visualizations.AbstractVisualization;

public class StackedAreaRenderer {

	private Display display;
	private Table datatable;
	private VisualTable visualTable;

	private List<String> datacolumns;

	private AndPredicate filter = new AndPredicate();
	private JSearchPanel authorsSearchPanel;
	private SearchQueryBinding authorsListQueryBinding;

//	private AxisLayout xAxis;
//	private AxisLabelLayout xLabels;
//	private AxisLayout yAxis;
//	private AxisLabelLayout yLabels;
	private NumberRangeModel yAxisRangeModel;

	public StackedAreaRenderer() throws DatabaseException {
		createDatatable();
	}

	private void createDatatable() throws DatabaseException {
		createDatatableFields();
		addDatatableData();
	}

	public JPanel draw(){
		Visualization visualization = new Visualization();

		setupVisualTable(visualization);


		/*
		 * CREATE DATA RENDERER
		 */
		defineRenderer(visualization);


		/*
		 * SETUP COLORS.
		 */
		defineColors(visualization);


		/*
		 * SETUP ACTIONS.
		 */
		createActions(visualization);


		/*
		 * SETUP LAYOUTS.
		 */
		defineLayout(visualization);


		/*
		 * CREATE PREDICATES
		 */
		createPredicates(visualization);

		/*
		 * SETUP DISPLAY
		 */
		setupDisplay(visualization);

		visualization.run("draw");

		JPanel stackedAreaPanel = new JPanel(new BorderLayout());
		stackedAreaPanel.add(display);

		return stackedAreaPanel;
	}

	private void setupVisualTable(Visualization visualization) {
		this.visualTable = visualization.addTable("activity", this.datatable);
		visualTable.addColumn(VisualItem.POLYGON, float[].class);
		visualTable.addColumn(VisualItem.POLYGON + ":start", float[].class);
		visualTable.addColumn(VisualItem.POLYGON + ":end", float[].class);
	}

	private void defineRenderer(Visualization visualization) {
		RendererFactory renderFactory = new RendererFactory(){ 
			Renderer polyR = new PolygonRenderer(Constants.POLY_TYPE_LINE);

			Renderer yAxisRenderer = new AxisRenderer(Constants.RIGHT, Constants.TOP);
			Renderer xAxisRenderer = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);

			public Renderer getRenderer(VisualItem item) { 
				return item.isInGroup("yAxis") ? yAxisRenderer : 
					item.isInGroup("xAxis") ? xAxisRenderer : polyR; 
			} 
		}; 
		visualization.setRendererFactory(renderFactory);
	}

	private void defineColors(Visualization visualization) {
		//		ColorAction sStroke = new ColorAction("activity", VisualItem.STROKECOLOR);
		//		sStroke.setDefaultColor(ColorLib.red(100));
		//		sStroke.add("_hover", ColorLib.red(50));
		//		ColorAction sFill = new ColorAction("activity", VisualItem.FILLCOLOR);
		//		sFill.setDefaultColor(ColorLib.red(100));
		//		sFill.add("_hover", ColorLib.red(50));


		DataColorAction strokeColor = new DataColorAction("activity", "AuthorID",
				Constants.ORDINAL, VisualItem.STROKECOLOR, new int[]{255,255,255});

		ColorAction fillColor = new DataColorAction("activity", "AuthorID",
				Constants.ORDINAL, VisualItem.FILLCOLOR, ColorPalette.getAuthorsColorPalette());


		ActionList colors = new ActionList();
		colors.add(strokeColor);
		colors.add(fillColor);
		visualization.putAction("color", colors);
	}

	private void defineLayout(Visualization visualization) {
		ActionList layout = new ActionList();
		String[] fields = datacolumns.toArray(new String[]{});

		ObjectRangeModel orm = new ObjectRangeModel(fields); 
		AxisLabelLayout xlabels = new AxisLabelLayout("xAxis",Constants.X_AXIS,orm);

		//		yAxis = new AxisLayout("commits", "Density", Constants.Y_AXIS, VisiblePredicate.TRUE);
		//		yLabels = new AxisLabelLayout("yAxis", Constants.Y_AXIS, yAxisRangeModel);
		//		AxisLabelLayout ylabels = new AxisLabelLayout("ylab",Constants.Y_AXIS,orm);
		layout.add(xlabels);
		//		layout.add(yLabels);
		layout.add(new StackedAreaChart("activity", VisualItem.POLYGON, fields));
		layout.add(new RepaintAction());
		visualization.putAction("layout", layout);
		visualization.runAfter("draw", "layout");

	}

	private void createActions(Visualization visualization) {
		ActionList draw = new ActionList();
		draw.add(visualization.getAction("color"));
		visualization.putAction("draw", draw);

		ActionList update = new ActionList();
		update.add(new VisibilityFilter("activity", filter));
		update.add(new RepaintAction());
		visualization.putAction("update", update);
	}

	private void createPredicates(final Visualization visualization) {

		UpdateListener lstnr = new UpdateListener() {
			public void update(Object src) {
				visualization.run("update");
			}
		};

		authorsListQueryBinding = new SearchQueryBinding(visualTable, "Author");
		authorsSearchPanel = authorsListQueryBinding.createSearchPanel();
		authorsSearchPanel.setVisible(false);

		//		filter.add(xAxisQueryBinding.getPredicate());
		//		filter.add(yAxisQueryBinding.getPredicate());
		filter.add(authorsListQueryBinding.getPredicate());
		filter.addExpressionListener(lstnr);

	}

	private void setupDisplay(final Visualization visualization) {
		display = new Display(visualization);
		display.setHighQuality(true);


		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				display.setHighQuality(false);
				visualization.run("layout");
				display.setHighQuality(true);
			}
		});
	}

	private void createDatatableFields() throws DatabaseException {
		datatable = new Table();
		datacolumns = new ArrayList<String>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(AbstractVisualization.getCurrentAnalysis().getFirstEntry().getDate());
		int firstMonth = calendar.get(Calendar.MONTH);
		final int firstYear = calendar.get(Calendar.YEAR);

		calendar = Calendar.getInstance();
		calendar.setTime(AbstractVisualization.getCurrentAnalysis().getLastEntry().getDate());
		final int lastMonth = calendar.get(Calendar.MONTH);
		final int lastYear = calendar.get(Calendar.YEAR);

		final int totalYears = lastYear - firstYear + 1;
		if(totalYears > 0){
			for (int i = 0; i < totalYears; i++) {
				for (int j = firstMonth; j <= 11; j++) {
					int year = firstYear+i;
					datacolumns.add("1/"+j+"/"+year);
					datatable.addColumn("1/"+j+"/"+year, long.class);
					if((j == lastMonth)&&(year == lastYear)){
						break;
					}
				}
				firstMonth = 0;
			}
		}
		datatable.addColumn("Author", String.class);
		datatable.addColumn("AuthorID", long.class);
	}

	private void addDatatableData() throws DatabaseException {
		Analysis currentAnalysis = new AnalysisDAO().findById(Analysis.class, 1L);
		List<Author> authorsList = new AuthorDAO().getProjectAuthors(currentAnalysis.getProject().getId());
		yAxisRangeModel = new NumberRangeModel(0, 0, 0, 0);
		for (Author author : authorsList) {
			datatable.addRow();
			for (int i = 0; i < datacolumns.size(); i++) {
				Calendar calendar = Calendar.getInstance();
				Date columnDate = null;
				try {
					DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
					columnDate = df.parse(datacolumns.get(i));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				calendar.setTime(columnDate);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				long authorRevisionsOnMonth = new AuthorDAO().countAuthorsRevisionsOnMonth(author, year, month);
				datatable.set(datatable.getRowCount()-1, datacolumns.get(i), authorRevisionsOnMonth);

				if(authorRevisionsOnMonth > Integer.parseInt(yAxisRangeModel.getHighValue().toString())){
					yAxisRangeModel.setHighValue(authorRevisionsOnMonth);
					yAxisRangeModel.setMaxValue(authorRevisionsOnMonth+5);
				}
			}
			datatable.set(datatable.getRowCount()-1, "Author", author.getName());
			datatable.set(datatable.getRowCount()-1, "AuthorID", author.getId());
		}
	}

	public JSearchPanel getAuthorsSearchPanel() {
		return authorsSearchPanel;
	}

	public Display getDisplay() {
		return display;
	}

}
