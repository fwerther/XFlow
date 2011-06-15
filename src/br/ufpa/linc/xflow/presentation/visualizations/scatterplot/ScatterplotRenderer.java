package br.ufpa.linc.xflow.presentation.visualizations.scatterplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
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
import prefuse.action.layout.AxisLayout;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.AxisRenderer;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;
import br.ufpa.linc.xflow.data.dao.cm.AuthorDAO;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.cm.ObjFileDAO;
import br.ufpa.linc.xflow.data.dao.metrics.EntryMetricsDAO;
import br.ufpa.linc.xflow.data.dao.metrics.FileMetricsDAO;
import br.ufpa.linc.xflow.data.dao.metrics.ProjectMetricsDAO;
import br.ufpa.linc.xflow.data.entities.Author;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.data.entities.ObjFile;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricModel;
import br.ufpa.linc.xflow.metrics.entry.EntryMetricValues;
import br.ufpa.linc.xflow.metrics.file.FileMetricModel;
import br.ufpa.linc.xflow.metrics.file.FileMetricValues;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricModel;
import br.ufpa.linc.xflow.metrics.project.ProjectMetricValues;
import br.ufpa.linc.xflow.presentation.commons.util.ColorPalette;
import br.ufpa.linc.xflow.presentation.view.ProjectViewer;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;
import br.ufpa.linc.xflow.presentation.visualizations.scatterplot.controls.TooltipControl;

public class ScatterplotRenderer implements VisualizationRenderer<ScatterplotVisualization> {

	private Metrics metricsSession;
	
	private Display display;
	private Table dataTable;
	private VisualTable visualTable;

	private Rectangle2D dataContainer = new Rectangle2D.Double();
    private Rectangle2D xAxisLabelsContainer = new Rectangle2D.Double();
    private Rectangle2D yAxisLabelsContainer = new Rectangle2D.Double();
    
    private AndPredicate filter = new AndPredicate();
    private JSearchPanel authorsSearchPanel;
    private SearchQueryBinding authorsListQueryBinding;
    
    private RangeQueryBinding xAxisQueryBinding;
    private AxisLayout xAxis;
    private AxisLabelLayout xLabels;
    private RangeQueryBinding yAxisQueryBinding;
    private AxisLayout yAxis;
    private AxisLabelLayout yLabels;
    private JRangeSlider verticalSlider;
    private JRangeSlider horizontalSlider;
    
    //FIXME: REMOVER ESSAS VARIAVEIS. ISSO É GAMBIARRA!
    private int firstRevision;
    private int lastRevision;
    
	@Override
	public void composeVisualization(JComponent visualizationComponent) throws DatabaseException {
		this.metricsSession = (Metrics) visualizationComponent.getClientProperty("Metrics Session");
		
		Date date = new Date();
		date.setYear(100);
		date.setMonth(6);
		date.setDate(8);
		
		Date date2 = new Date();
		date2.setYear(100);
		date2.setMonth(10);
		date2.setDate(4);
		createDataTable(date, date2);
//		createDataTable();
		visualizationComponent.add(this.draw(), BorderLayout.CENTER);
	}
	
	public JPanel draw() {
		Visualization visualization = new Visualization();
		visualTable = visualization.addTable("commits", dataTable);

		/*
		 * CREATE DATA RENDERER
		 */
		createRenderer(visualization);

		/*
		 * CREATE AXES
		 */
		createAxes(visualization);

		/*
		 * CREATE ACTIONS
		 */
		createActions(visualization);
		
		/*
		 * CREATE PREDICATES
		 */
		createPredicates(visualization);
		
		/*
		 * DISPLAY SETUP
		 */
		setupDisplay(visualization);
		
		/*
		 * CREATE CONTROLS
		 */
		createToolTip(visualization);
		
		
        visualization.run("draw");
        visualization.repaint();
        
        JPanel scatterplotPanel = new JPanel(new BorderLayout());
        scatterplotPanel.add(display, BorderLayout.CENTER);
        scatterplotPanel.add(createHorizontalSlider(), BorderLayout.SOUTH);
        scatterplotPanel.add(createVerticalSlider(), BorderLayout.EAST);
		return scatterplotPanel;
	}


	private void createRenderer(Visualization visualization) {
        visualization.setRendererFactory(new RendererFactory() {
            AbstractShapeRenderer shapeRenderer = new ShapeRenderer(10);
            Renderer yAxisRenderer = new AxisRenderer(Constants.RIGHT, Constants.TOP);
            Renderer xAxisRenderer = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);
            
            public Renderer getRenderer(VisualItem item) {
                return item.isInGroup("xAxis") ? xAxisRenderer :
                       item.isInGroup("yAxis") ? yAxisRenderer : shapeRenderer;
            }
        });
	}
	
	
	private void createAxes(Visualization visualization) {
	
		ActionList xAxisActions = new ActionList();
		
		xAxisQueryBinding = new RangeQueryBinding(visualTable, "Commit Sequence");
		xAxis = new AxisLayout("commits", "Commit Sequence", Constants.X_AXIS, VisiblePredicate.TRUE);
		xAxis.setLayoutBounds(dataContainer);
		xLabels = new AxisLabelLayout("xAxis", xAxis, xAxisLabelsContainer);
		xAxis.setRangeModel(xAxisQueryBinding.getModel());
		xLabels.setRangeModel(xAxisQueryBinding.getModel());
		
		xAxisActions.add(xAxis);
		xAxisActions.add(xLabels);
		
		ActionList yAxisActions = new ActionList();
		
		yAxisQueryBinding = new RangeQueryBinding(visualTable, "Higher Centrality");
		yAxis = new AxisLayout("commits", "Higher Centrality", Constants.Y_AXIS, VisiblePredicate.TRUE);
		yAxis.setLayoutBounds(dataContainer);
		yLabels = new AxisLabelLayout("yAxis", yAxis, yAxisLabelsContainer);
		yAxis.setRangeModel(yAxisQueryBinding.getModel());
		yLabels.setRangeModel(yAxisQueryBinding.getModel());
		
		yAxisActions.add(yAxis);
		yAxisActions.add(yLabels);
		
		
		visualization.putAction("xAxisActions", xAxisActions);
		visualization.putAction("yAxisActions", yAxisActions);
	}
	
	private void createActions(Visualization visualization) {
        DataColorAction color = new DataColorAction("commits", "AuthorID",
                Constants.ORDINAL, VisualItem.STROKECOLOR, new int[]{0,0,0});
		
        ColorAction fill = new DataColorAction("commits", "AuthorID",
                Constants.ORDINAL, VisualItem.FILLCOLOR, ColorPalette.getAuthorsColorPalette());
        
//        int[] shapes = new int[] { Constants.SHAPE_RECTANGLE, Constants.SHAPE_DIAMOND };
//        DataShapeAction shape = new DataShapeAction("commits", "Author", shapes);
        
        ActionList draw = new ActionList();
        draw.add(color);
        draw.add(fill);
//        draw.add(shape);
        draw.add(visualization.getAction("xAxisActions"));
        draw.add(visualization.getAction("yAxisActions"));
		visualization.putAction("draw", draw);
		
		
		ActionList update = new ActionList();
		update.add(new VisibilityFilter("commits", filter));
		update.add(visualization.getAction("xAxisActions"));
		update.add(visualization.getAction("yAxisActions"));
		update.add(new RepaintAction());
		visualization.putAction("update", update);
	}
	
	private void createToolTip(Visualization visualization) {
		
		TooltipControl toolTipController = new TooltipControl();
		display.addControlListener(toolTipController);
		
	}
	
	private void setupDisplay(final Visualization visualization) {
		
		display = new Display(visualization);
		display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		display.setSize(700,450);
        display.setHighQuality(true);
        display.setBackground(new Color(0, 0, 0));
        
		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				Insets i = display.getInsets();
				int w = display.getWidth();
				int h = display.getHeight();
				int iw = i.left+i.right;
				int ih = i.top+i.bottom;
				int aw = 85;
				int ah = 15;

				dataContainer.setRect(i.left, i.top, w-iw-aw, h-ih-ah);
				xAxisLabelsContainer.setRect(i.left, h-ah-i.bottom, w-iw-aw, ah-10);
				yAxisLabelsContainer.setRect(i.left, i.top, w-iw, h-ih-ah);
				
				visualization.run("update");
			}
		});
        
		Insets i = display.getInsets();
		int w = display.getWidth();
		int h = display.getHeight();
		int iw = i.left+i.right;
		int ih = i.top+i.bottom;
		int aw = 85;
		int ah = 15;

		
		dataContainer.setRect(i.left, i.top, w-iw-aw, h-ih-ah);
		xAxisLabelsContainer.setRect(i.left, h-ah-i.bottom, w-iw-aw, ah-10);
		yAxisLabelsContainer.setRect(i.left, i.top, w-iw, h-ih-ah);
	}
	
	private Component createHorizontalSlider() {
        int maxValue = (int) lastRevision;
        int minValue = (int) firstRevision;
		
        xAxisQueryBinding.getNumberModel().setValueRange(minValue, maxValue, minValue, maxValue);
		        
		horizontalSlider = xAxisQueryBinding.createRangeSlider(JRangeSlider.HORIZONTAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
//		horizontalSlider.setMaximum(maxCommit);
//		horizontalSlider.setMinimum(minCommit);
		
//		horizontalSlider.setRange(minCommit, maxCommit);
		horizontalSlider.setMinExtent(1);
//		horizontalSlider.getModel().setMaximum(maxCommit);
//		horizontalSlider.getModel().setMinimum(minCommit);
        
        horizontalSlider.setThumbColor(null);
        horizontalSlider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                display.setHighQuality(false);
            }
            public void mouseReleased(MouseEvent e) {
                display.setHighQuality(true);
                display.repaint();
            }
        });
		
        return horizontalSlider;
	}
	
	private Component createVerticalSlider() {
        verticalSlider = yAxisQueryBinding.createVerticalRangeSlider();
        verticalSlider.setThumbColor(null);
        verticalSlider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                display.setHighQuality(false);
            }
            public void mouseReleased(MouseEvent e) {
                display.setHighQuality(true);
                display.repaint();
            }
        });
		
        return verticalSlider;
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
		
		filter.add(xAxisQueryBinding.getPredicate());
		filter.add(yAxisQueryBinding.getPredicate());
		filter.add(authorsListQueryBinding.getPredicate());
		filter.addExpressionListener(lstnr);
	}
	
	private void createDataTable() throws DatabaseException{
		createDataTableFields();
		addDataTableData();
	}
	
	private void createDataTable(Date initialDate, Date finalDate) throws DatabaseException{
		createDataTableFields();
		addDataTableData(initialDate, finalDate);
	}

	private void createDataTableFields() {
		dataTable = new Table();
		
		dataTable.addColumn("Author", String.class);
		dataTable.addColumn("AuthorID", long.class);
		dataTable.addColumn("Commit Sequence", long.class);
		dataTable.addColumn("Revision Number", int.class);
		dataTable.addColumn("Author Sequence Number", int.class);
		dataTable.addColumn("Comment", String.class);
		dataTable.addColumn("Density", double.class);
		dataTable.addColumn("Cluster Coefficient", double.class);
		dataTable.addColumn("Files", String.class);
		dataTable.addColumn("Added Files", int.class);
		dataTable.addColumn("Modified Files", int.class);
		dataTable.addColumn("Deleted Files", int.class);
		dataTable.addColumn("Entry Lines of Code", int.class);
		dataTable.addColumn("Higher Centrality", int.class);
//		dataTable.addColumn("Average Centrality", double.class);
//		dataTable.addColumn("Max Centrality", int.class);
//		dataTable.addColumn("Higher Betweenness Centrality", int.class);
//		dataTable.addColumn("Average Betweenness Centrality", double.class);
//		dataTable.addColumn("Max Betweenness Centrality", int.class);
	}
	
//	private void addDataTableData() {
//		for (long i = new AnalysisDAO().findById(Analysis.class, 1L).getFirstCommit(); i < new AnalysisDAO().findById(Analysis.class, 1L).getLastCommit(); i++) {
//			ProjectMetricValues metric = new ProjectMetricsDAO().findProjectMetricValuesByRevision(new AnalysisDAO().findById(Analysis.class, 1L), i);
//			if(metric != null){
//				dataTable.addRow();
//				dataTable.set(dataTable.getRowCount()-1, "Density", metric.getDensity());
//				dataTable.set(dataTable.getRowCount()-1, "Revision", (int) i);
//				dataTable.set(dataTable.getRowCount()-1, "Author", new EntryDAO().findEntryFromRevision(new AnalysisDAO().findById(Analysis.class, 1L).getProject(), metric.getRevision()).getAuthor().getName());
//			}
//		}
//	}

	private void addDataTableData() throws DatabaseException {

		ProjectMetricModel[] availableProjectMetrics = (ProjectMetricModel[]) ProjectViewer.getProjectMetrics();
		EntryMetricModel[] availableEntryMetrics = (EntryMetricModel[]) ProjectViewer.getEntryMetrics();
		FileMetricModel[] availableFileMetrics = (FileMetricModel[]) ProjectViewer.getFileMetrics();
		final String[] metricNamesVariations = new String[]{"Higher ", "Average ", "Max "};
		double[] metricValues = new double[(availableFileMetrics.length*metricNamesVariations.length)];
		
		/* 
		 * ###################################
		 * INITIATING METRICS REFERENCE VALUES
		 * ###################################
		 */
		
		double[] higherProjectMetricValues = new double[availableProjectMetrics.length];
		double[] higherEntryMetricValues = new double[availableEntryMetrics.length];
		double[] higherFileMetricValues = new double[availableFileMetrics.length];
		
		final List<Entry> entries;
		final Map<Author, Double[]> metrics = new HashMap<Author, Double[]>();
		
		if(metricsSession.getAssociatedAnalysis().isTemporalConsistencyForced()){
			entries = new EntryDAO().getAllEntriesWithinEntries(metricsSession.getAssociatedAnalysis().getFirstEntry(), metricsSession.getAssociatedAnalysis().getLastEntry());
		} else {
			entries = new EntryDAO().getAllEntriesWithinRevisions(metricsSession.getAssociatedAnalysis().getProject(), metricsSession.getAssociatedAnalysis().getFirstEntry().getRevision(), metricsSession.getAssociatedAnalysis().getLastEntry().getRevision());
		}
		
		this.firstRevision = 1;
		
		for (int i = 0; i < entries.size(); i++) {
			this.lastRevision = i+1;
			final Entry entry = entries.get(i);
			
			if(!metrics.containsKey(entry.getAuthor())){
				Double[] values = new Double[availableFileMetrics.length];
				for (int j = 0; j < values.length; j++) {
					values[j] = 0.0;
				}
				metrics.put(entry.getAuthor(), values);
			}
			
			for (int j = 0; j < availableFileMetrics.length; j++) {
				metricValues[(j*metricNamesVariations.length)] = 0;
				metricValues[(j*metricNamesVariations.length)+1] = 0;
				metricValues[(j*metricNamesVariations.length)+2] = 0;
			}

			dataTable.addRow();
			dataTable.set(dataTable.getRowCount()-1, "Revision Number", entry.getRevision());
			dataTable.set(dataTable.getRowCount()-1, "Commit Sequence", (i+1));
			dataTable.set(dataTable.getRowCount()-1, "Author", entry.getAuthor().getName());
			dataTable.set(dataTable.getRowCount()-1, "AuthorID", entry.getAuthor().getId());
			dataTable.set(dataTable.getRowCount()-1, "Comment", entry.getComment());
			dataTable.set(dataTable.getRowCount()-1, "Author Sequence Number", new EntryDAO().getAuthorEntrySequenceNumber(entry));
			dataTable.set(dataTable.getRowCount()-1, "Files", entry.getListOfEntryFiles());
			
			if(availableProjectMetrics.length > 0){
				ProjectMetricValues projectMetricsValues = new ProjectMetricsDAO().findProjectMetricValuesByEntry(this.metricsSession, entry);
				if(projectMetricsValues != null){
					for (int j = 0; j < availableProjectMetrics.length; j++) {
						double metricValue = projectMetricsValues.getValueByName(availableProjectMetrics[j].getMetricName());
						dataTable.set(dataTable.getRowCount()-1, availableProjectMetrics[j].getMetricName(), metricValue);
						higherProjectMetricValues[j] = Math.max(higherProjectMetricValues[j], metricValue);
					}
				} else {
					for (int j = 0; j < availableProjectMetrics.length; j++) {
						dataTable.set(dataTable.getRowCount()-1, availableProjectMetrics[j].getMetricName(), 0);
					}
				}
			}

			if(availableEntryMetrics.length > 0){
				EntryMetricValues entryMetricsValues = new EntryMetricsDAO().findEntryMetricValuesByEntry(this.metricsSession, entry);
				if(entryMetricsValues != null){
					for (int j = 0; j < availableEntryMetrics.length; j++) {
						double metricValue = entryMetricsValues.getValueByName(availableEntryMetrics[j].getMetricName());
						dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), metricValue);
						higherEntryMetricValues[j] = Math.max(higherEntryMetricValues[j], metricValue);
					}
				} else {
					for (int j = 0; j < availableEntryMetrics.length; j++) {
						dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), 0);
					}
				}
			}

			if(availableFileMetrics.length > 0){
				List<FileMetricValues> fileMetricsValues = new FileMetricsDAO().findFileMetricValuesByRevision(this.metricsSession, entry);
				for (FileMetricValues fileMetricValue : fileMetricsValues) {
					for (int j = 0; j < availableFileMetrics.length; j++) {
						final double metricValue = fileMetricValue.getValueByName(availableFileMetrics[j].getMetricName());
						metricValues[(j*metricNamesVariations.length)] = Math.max(metricValues[(j*metricNamesVariations.length)], metricValue); 
						metricValues[(j*metricNamesVariations.length)+1] += metricValue;
					}
				}

				final Double[] highestMetricsValues;
				if(metrics.get(entry.getAuthor()) != null){
					highestMetricsValues = metrics.get(entry.getAuthor()); 
				} else {
					highestMetricsValues = new Double[availableFileMetrics.length];
				}
				for (int j = 0; j < availableFileMetrics.length; j++) {
					metricValues[(j*metricNamesVariations.length)+1] /= fileMetricsValues.size();

					final double highestFileMetricValue = Math.max(highestMetricsValues[j], metricValues[(j*metricNamesVariations.length)]);
					metricValues[(j*metricNamesVariations.length)+2] = highestFileMetricValue;
					higherFileMetricValues[j] = Math.max(higherFileMetricValues[j], highestFileMetricValue);

					if(highestFileMetricValue > highestMetricsValues[j]){
						highestMetricsValues[j] = highestFileMetricValue;
						metrics.put(entry.getAuthor(), highestMetricsValues);
					}

					for (int k = 0; k < metricNamesVariations.length; k++) {
						dataTable.set(dataTable.getRowCount()-1, metricNamesVariations[k]+availableFileMetrics[j].getMetricName(), metricValues[(j*metricNamesVariations.length) + k]);
					}
				}
			}

			dataTable.addRow();
			dataTable.set(dataTable.getRowCount()-1, "Author", "Reference");
			dataTable.set(dataTable.getRowCount()-1, "AuthorID", Integer.MAX_VALUE);
			dataTable.set(dataTable.getRowCount()-1, "Revision Number", entry.getRevision());
			dataTable.set(dataTable.getRowCount()-1, "Commit Sequence", (i+1));

			for (int j = 0; j < higherProjectMetricValues.length; j++) {
				dataTable.set(dataTable.getRowCount()-1, availableProjectMetrics[j].getMetricName(), higherProjectMetricValues[j]);
			}
			for (int j = 0; j < higherEntryMetricValues.length; j++) {
				dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), higherEntryMetricValues[j]);
			}
			for (int j = 0; j < higherFileMetricValues.length; j++) {
				dataTable.set(dataTable.getRowCount()-1, "Max "+availableFileMetrics[j].getMetricName(), higherFileMetricValues[j]);
			}
		} 
	}
	
	private void addDataTableData(Date initialDate, Date finalDate) throws DatabaseException {

		ProjectMetricModel[] availableProjectMetrics = (ProjectMetricModel[]) ProjectViewer.getProjectMetrics();
		EntryMetricModel[] availableEntryMetrics = (EntryMetricModel[]) ProjectViewer.getEntryMetrics();
		FileMetricModel[] availableFileMetrics = (FileMetricModel[]) ProjectViewer.getFileMetrics();
		final String[] metricNamesVariations = new String[]{"Higher ", "Average ", "Max "};
		double[] metricValues = new double[(availableFileMetrics.length*metricNamesVariations.length)];
		
		/* 
		 * ###################################
		 * INITIATING METRICS REFERENCE VALUES
		 * ###################################
		 */
		
		double[] higherProjectMetricValues = new double[availableProjectMetrics.length];
		double[] higherEntryMetricValues = new double[availableEntryMetrics.length];
		double[] higherFileMetricValues = new double[availableFileMetrics.length];
		
//		final List<Entry> entries = new EntryDAO().getAllEntriesWithinDates(this.metricsSession.getAssociatedAnalysis().getProject(), initialDate, finalDate);;
		
		final Entry initialEntry = new EntryDAO().findById(Entry.class, 89354);
		final Entry finalEntry = new EntryDAO().findById(Entry.class, 92364);
		final List<Entry> entries = new EntryDAO().getAllEntriesWithinEntries(initialEntry, finalEntry);
//		final Map<Author, Double[]> metrics = new HashMap<Author, Double[]>();
		final Map<String, FileMetricValues> metricsValues = new HashMap<String, FileMetricValues>();
		
		List<Author> authors = new AuthorDAO().getProjectAuthors(this.metricsSession.getAssociatedAnalysis().getProject().getId());
		for (Author author : authors) {
			dataTable.addRow();
			dataTable.set(dataTable.getRowCount()-1, "Author", author.getName());
			dataTable.set(dataTable.getRowCount()-1, "AuthorID", author.getId());
			dataTable.set(dataTable.getRowCount()-1, "Revision Number", -1);
		}
		
		this.firstRevision = 1;
		
		for (int i = 0; i < entries.size(); i++) {
			this.lastRevision = i+1;
			final Entry entry = entries.get(i);
			
			for (int j = 0; j < availableFileMetrics.length; j++) {
				metricValues[(j*metricNamesVariations.length)] = 0;
				metricValues[(j*metricNamesVariations.length)+1] = 0;
				metricValues[(j*metricNamesVariations.length)+2] = 0;
			}

			dataTable.addRow();
			dataTable.set(dataTable.getRowCount()-1, "Revision Number", entry.getRevision());
			dataTable.set(dataTable.getRowCount()-1, "Commit Sequence", (i+1));
			dataTable.set(dataTable.getRowCount()-1, "Author", entry.getAuthor().getName());
			dataTable.set(dataTable.getRowCount()-1, "AuthorID", entry.getAuthor().getId());
			dataTable.set(dataTable.getRowCount()-1, "Comment", entry.getComment());
			dataTable.set(dataTable.getRowCount()-1, "Author Sequence Number", new EntryDAO().getAuthorEntrySequenceNumber(entry));
			dataTable.set(dataTable.getRowCount()-1, "Files", entry.getListOfEntryFiles());
			

			final double[] maxMetricValues = new double[]{0, 0};
			for (ObjFile file : entry.getEntryFiles()) {
				final FileMetricValues fileMetricValues;
				
				if(metricsValues.containsKey(file.getPath())){
					fileMetricValues = metricsValues.get(file.getPath());
				} else {
					ObjFile addedFileInstance = new ObjFileDAO().findAddedFileByPathUntilEntry(this.metricsSession.getAssociatedAnalysis().getProject(), entry, file.getPath());
					
					if(addedFileInstance != null){
						fileMetricValues = new FileMetricsDAO().findMetricValuesByFileUntilEntry(this.metricsSession, addedFileInstance, initialEntry);
					} else {
						fileMetricValues = null;
					}
					
					metricsValues.put(file.getPath(), fileMetricValues);
				}
				
				if(fileMetricValues != null){
					for (int j = 0; j < availableFileMetrics.length; j++) {
						final double metricValue = fileMetricValues.getValueByName(availableFileMetrics[j].getMetricName());
						maxMetricValues[j] = Math.max(maxMetricValues[j], metricValue);
					}
				}
			}
			
			for (int j = 0; j < availableFileMetrics.length; j++) {
				dataTable.set(dataTable.getRowCount()-1, "Higher "+availableFileMetrics[j].getMetricName(), maxMetricValues[j]);
			}
			
			
//			if(availableFileMetrics.length > 0){
//				List<FileMetricValues> fileMetricsValues = new FileMetricsDAO().findFileMetricValuesByRevision(this.metricsSession, entry);
//				for (FileMetricValues fileMetricValue : fileMetricsValues) {
//					for (int j = 0; j < availableFileMetrics.length; j++) {
//						final double metricValue = fileMetricValue.getValueByName(availableFileMetrics[j].getMetricName());
//						metricValues[(j*metricNamesVariations.length)] = Math.max(metricValues[(j*metricNamesVariations.length)], metricValue); 
//						metricValues[(j*metricNamesVariations.length)+1] += metricValue;
//					}
//				}
//
//				final Double[] highestMetricsValues;
////				if(metrics.get(entry.getAuthor()) != null){
////					highestMetricsValues = metrics.get(entry.getAuthor()); 
////				} else {
////					highestMetricsValues = new Double[availableFileMetrics.length];
////				}
//				for (int j = 0; j < availableFileMetrics.length; j++) {
//					metricValues[(j*metricNamesVariations.length)+1] /= fileMetricsValues.size();
//
//					for (int k = 0; k < metricNamesVariations.length; k++) {
//						dataTable.set(dataTable.getRowCount()-1, metricNamesVariations[k]+availableFileMetrics[j].getMetricName(), metricValues[(j*metricNamesVariations.length) + k]);
//					}
//				}
//			}

//			dataTable.addRow();
//			dataTable.set(dataTable.getRowCount()-1, "Author", "Reference");
//			dataTable.set(dataTable.getRowCount()-1, "AuthorID", Integer.MAX_VALUE);
//			dataTable.set(dataTable.getRowCount()-1, "Revision Number", entry.getRevision());
//			dataTable.set(dataTable.getRowCount()-1, "Commit Sequence", (i+1));
//
//			for (int j = 0; j < higherProjectMetricValues.length; j++) {
//				dataTable.set(dataTable.getRowCount()-1, availableProjectMetrics[j].getMetricName(), higherProjectMetricValues[j]);
//			}
//			for (int j = 0; j < higherEntryMetricValues.length; j++) {
//				dataTable.set(dataTable.getRowCount()-1, availableEntryMetrics[j].getMetricName(), higherEntryMetricValues[j]);
//			}
//			for (int j = 0; j < higherFileMetricValues.length; j++) {
//				dataTable.set(dataTable.getRowCount()-1, "Max "+availableFileMetrics[j].getMetricName(), higherFileMetricValues[j]);
//			}
		} 
	}

	
	public void updateYAxis(String newMetric){
		this.yAxis.setDataField(newMetric);
		this.yAxisQueryBinding = new RangeQueryBinding(this.visualTable, newMetric);

		this.verticalSlider = new JRangeSlider(yAxisQueryBinding.getModel(), JRangeSlider.VERTICAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
		yAxis.setRangeModel(yAxisQueryBinding.getModel());
		yLabels.setRangeModel(yAxisQueryBinding.getModel());
		
		this.display.getVisualization().run("update");
	}
	
	
	public void updateXAxis(String newScale){
		this.xAxis.setDataField(newScale);
		this.xAxisQueryBinding = new RangeQueryBinding(this.visualTable, newScale);

		this.horizontalSlider = new JRangeSlider(xAxisQueryBinding.getModel(), JRangeSlider.HORIZONTAL, JRangeSlider.LEFTRIGHT_TOPBOTTOM);
		xAxis.setRangeModel(xAxisQueryBinding.getModel());
		xLabels.setRangeModel(xAxisQueryBinding.getModel());
		
		this.display.getVisualization().run("update");
	}
	
	public Display getDisplay() {
		return display;
	}

	public RangeQueryBinding getxAxisQueryBinding() {
		return xAxisQueryBinding;
	}


	public JRangeSlider getVerticalSlider() {
		return verticalSlider;
	}


	public JRangeSlider getHorizontalSlider() {
		return horizontalSlider;
	}

	public JSearchPanel getAuthorsSearchPanel() {
		return authorsSearchPanel;
	}

	@Override
	public void updateVisualizationLimits(int inferiorLimit, int superiorLimit) throws DatabaseException {
		this.updateVisualizationLimits(inferiorLimit, superiorLimit);
	}

	@Override
	public void setLowerQuality() {
		this.display.setHighQuality(false);
	}

	@Override
	public void setHighQuality() {
		this.display.setHighQuality(true);
	}

}
