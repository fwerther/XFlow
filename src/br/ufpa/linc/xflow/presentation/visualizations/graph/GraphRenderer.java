package br.ufpa.linc.xflow.presentation.visualizations.graph;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.assignment.ShapeAction;
import prefuse.action.layout.CircleLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import br.ufpa.linc.xflow.data.dao.cm.EntryDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.entities.AuthorDependencyObject;
import br.ufpa.linc.xflow.data.entities.CoordinationRequirements;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.entities.Metrics;
import br.ufpa.linc.xflow.data.representation.Converter;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.data.representation.prefuse.PrefuseGraph;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;
import br.ufpa.linc.xflow.presentation.visualizations.VisualizationRenderer;

@SuppressWarnings("unchecked")
public class GraphRenderer implements VisualizationRenderer<GraphVisualization> {

	private Metrics metricsSession;
	
	private Display display;
	private PrefuseGraph graph;
	
	private int representedDependency = Dependency.COORD_REQUIREMENTS;
	private long currentRevision;
	
	@Override
	public void composeVisualization(JComponent visualizationComponent) throws DatabaseException {
		this.metricsSession = (Metrics) visualizationComponent.getClientProperty("Metrics Session");
		constructGraph();
		visualizationComponent.add(this.draw(), BorderLayout.CENTER);
	}
	
	private void constructGraph() throws DatabaseException {
		if(metricsSession.getAssociatedAnalysis().isCoordinationRequirementPersisted()){
			this.graph = Converter.convertJungToPrefuseGraph(metricsSession.getAssociatedAnalysis().processEntryDependencyGraph(metricsSession.getAssociatedAnalysis().getLastEntry(), Dependency.COORD_REQUIREMENTS));
		} else {
			final Dependency<AuthorDependencyObject, AuthorDependencyObject> dependencyDTO = new CoordinationRequirements();
			dependencyDTO.setAssociatedAnalysis(metricsSession.getAssociatedAnalysis());
			dependencyDTO.setAssociatedEntry(metricsSession.getAssociatedAnalysis().getLastEntry());
			final Matrix taskAssignmentMatrix = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(metricsSession.getAssociatedAnalysis().getLastEntry(), Dependency.TASK_ASSIGNMENT);
			System.out.println(taskAssignmentMatrix.getRows()+", "+taskAssignmentMatrix.getColumns());
			final Matrix taskDependencyMatrix = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(metricsSession.getAssociatedAnalysis().getLastEntry(), Dependency.TASK_DEPENDENCY);
			System.out.println(taskDependencyMatrix.getRows()+", "+taskDependencyMatrix.getColumns());
			final Matrix matrix = taskAssignmentMatrix.multiply(taskDependencyMatrix).multiply(taskAssignmentMatrix.getTransposeMatrix());
			JUNGGraph graph = new JUNGGraph();
			graph = JUNGGraph.convertMatrixToJUNGGraph(matrix, dependencyDTO);
			this.graph = Converter.convertJungToPrefuseGraph(graph);
		}
		this.currentRevision = metricsSession.getAssociatedAnalysis().getLastEntry().getRevision();
	}

	public static void main(String[] args) throws DatabaseException {
		new GraphRenderer();
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public Display getDisplay() {
		return display;
	} 

	public JPanel draw() {

		Visualization visualization = new Visualization();
//		final VisualGraph visualGraph = visualization.addGraph("graph", graph.getPrefuseGraph());
		visualization.addGraph("graph", graph.getPrefuseGraph());
		visualization.setValue("graph.edges", null, VisualItem.INTERACTIVE, Boolean.FALSE);



		/*
		 * SETUP RENDERERS.
		 * 
		 * Controls how nodes and edges will be presented
		 * (e.g. directed or undirected edges, label field and size).
		 */
		defineRenderers(visualization);

		/*
		 * SETUP COLORS.
		 * 
		 * Definitions for nodes and edges colors
		 * (e.g. node and node text color, edge color).
		 */
		defineColors(visualization);

		/*
		 * SETUP ACTIONS.
		 * 
		 * Definitions for visualization basic effects.
		 */
		createActions(visualization);

		/*
		 * SETUP LAYOUTS.
		 * 
		 */
		defineLayout(visualization);

		/*
		 * GROUPS DEFINITION
		 */
//		defineGroups(visualization, visualGraph);

		/*
		 * SETUP DISPLAY
		 */
		setupDisplay(visualization);

		visualization.run("draw");

		JPanel graphPanel = new JPanel(new BorderLayout());
		graphPanel.add(display, BorderLayout.CENTER);
		return graphPanel;
	}

	private void setupDisplay(final Visualization visualization) {
		display = new Display(visualization);
		display.addControlListener(new DragControl());
//		display.addControlListener(new PanControl());
		display.addControlListener(new ZoomControl());
		display.addControlListener(new FocusControl());
		display.addControlListener(new ZoomToFitControl());
		display.addControlListener(new NeighborHighlightControl());


		display.pan(display.getSize().getWidth(), display.getSize().getHeight());
		
		display.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				display.setHighQuality(false);
				display.getVisualization().run("draw");
				display.getVisualization().run("layout");
				display.setHighQuality(true);
			}
		});
	}

//	private void defineGroups(final Visualization visualization, VisualGraph visualGraph) {
//        TupleSet focusGroup = visualization.getGroup(Visualization.FOCUS_ITEMS); 
//        focusGroup.addTupleSetListener(new TupleSetListener() {
//            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
//            {
//                for ( int i=0; i<rem.length; ++i )
//                    ((VisualItem)rem[i]).setFixed(false);
//                for ( int i=0; i<add.length; ++i ) {
//                    ((VisualItem)add[i]).setFixed(false);
//                    ((VisualItem)add[i]).setFixed(true);
//                }
//                visualization.run("draw");
//            }
//        });
//
//
//		//	        NodeItem focus = (NodeItem)visualGraph.getNode(0);
//		//	        PrefuseLib.setX(focus, null, 400);
//		//	        PrefuseLib.setY(focus, null, 250);
//		//	        focusGroup.setTuple(focus);
//
//	}

	private void defineRenderers(Visualization visualization) {

		// Label Renderer
		LabelRenderer labelRenderer = new LabelRenderer("name");
		labelRenderer.setRoundedCorner(8, 8);
		labelRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
		labelRenderer.setHorizontalAlignment(Constants.CENTER);

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setBaseSize(16);
		shapeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);
		
		// Edge Renderer
		EdgeRenderer edgeRenderer = new EdgeRenderer();
		edgeRenderer.setArrowType(Constants.EDGE_ARROW_NONE);
		edgeRenderer.setEdgeType(Constants.EDGE_TYPE_CURVE);

		// Renderer Factory
		DefaultRendererFactory rendererFactory = new DefaultRendererFactory();
		rendererFactory.setDefaultRenderer(labelRenderer);
		rendererFactory.setDefaultEdgeRenderer(edgeRenderer);

		visualization.setRendererFactory(rendererFactory);
	}

	private void defineColors(Visualization visualization) {

		// Nodes Colors
		int[] palette = new int[] { ColorLib.rgb(200,200,255),
				ColorLib.rgb(255,50,50), ColorLib.rgb(255,180,180)};
		ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, palette[0]);
//		fill.add("_fixed", palette[1]);
//		fill.add("_highlight", palette[2]);
//		fill.add("_neighbours", palette[2]);

		// Text and edges Colors
		ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
		ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(100));

		ActionList colorActions = new ActionList();
		colorActions.add(fill);
		colorActions.add(text);
		colorActions.add(edges);
		visualization.putAction("color", colorActions);
	}

	private void defineLayout(Visualization visualization) {

		// Tree Layout
		RadialTreeLayout treeLayout = new RadialTreeLayout("graph");
		
		// Circle Layout
		CircleLayout circleLayout = new CircleLayout("graph", 200);
		
		// Force Layout
//		ForceDirectedLayout forceLayout = new ForceDirectedLayout("graph");
		FruchtermanReingoldLayout frLayout = new FruchtermanReingoldLayout("graph", 10);
//		forceSimulator = forceLayout.getForceSimulator();
//		ForceSimulator forceSimulator = forceLayout.getForceSimulator();
//		forceSimulator.getForces()[0].setParameter(0, -1.2f);

		ActionList layoutAction = new ActionList(100);
//		layoutAction.add(treeLayout);
		layoutAction.add(frLayout);
//		layoutAction.add(circleLayout);
//		layoutAction.add(f);
		layoutAction.add(new RepaintAction());
		visualization.putAction("layout", layoutAction);
		visualization.runAfter("draw", "layout");
	}

	private void createActions(Visualization visualization) {
		ActionList draw = new ActionList();
		DataSizeAction a = new DataSizeAction("graph.edges", "weight");
		a.setMaximumSize(150);
		ShapeAction graphShape = new ShapeAction("graph.nodes", Constants.SHAPE_ELLIPSE);
//		a.setIs2DArea(true);
//		a.setBinCount(8);
//		a.setScale(Constants.QUANTILE_SCALE);
//		draw.add(a);
		draw.add(visualization.getAction("color"));
		draw.add(a);
//		draw.add(graphShape);
		visualization.putAction("draw", draw);
	}


	public void changeGraphType(int newType) throws DatabaseException {
		representedDependency = newType;
		updateGraph(this.currentRevision);
	}
	
	public void updateGraph(long newSequence) throws DatabaseException {
		this.getDisplay().getVisualization().reset();
		this.graph = new PrefuseGraph();
		
		final Entry entry;
		if(this.metricsSession.getAssociatedAnalysis().isTemporalConsistencyForced()){
			entry = new EntryDAO().findEntryFromSequence(this.metricsSession.getAssociatedAnalysis().getProject(), newSequence);
		} else {
			entry = new EntryDAO().findEntryFromRevision(this.metricsSession.getAssociatedAnalysis().getProject(), newSequence);
		}

		if(representedDependency == Dependency.COORD_REQUIREMENTS){
			if(this.metricsSession.getAssociatedAnalysis().isCoordinationRequirementPersisted()){
				this.graph = Converter.convertJungToPrefuseGraph(metricsSession.getAssociatedAnalysis().processEntryDependencyGraph(entry, Dependency.COORD_REQUIREMENTS));
			} else {
				final Dependency<AuthorDependencyObject, AuthorDependencyObject> dependencyDTO = new DependencyDAO().findDependencyByEntry(metricsSession.getAssociatedAnalysis().getId(), entry.getId(), Dependency.COORD_REQUIREMENTS);
				final Matrix taskAssignmentMatrix = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(entry, Dependency.TASK_ASSIGNMENT);
				final Matrix taskDependencyMatrix = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(entry, Dependency.TASK_DEPENDENCY);
				final Matrix matrix = taskAssignmentMatrix.multiply(taskDependencyMatrix).multiply(taskAssignmentMatrix.getTransposeMatrix());

				this.graph = Converter.convertJungToPrefuseGraph(JUNGGraph.convertMatrixToJUNGGraph(matrix, dependencyDTO));
			}
		} else {
//			final Dependency<AuthorDependencyObject, AuthorDependencyObject> dependencyDTO = new CoordinationRequirements();
//			dependencyDTO.setAssociatedAnalysis(metricsSession.getAssociatedAnalysis());
//			dependencyDTO.setAssociatedEntry(metricsSession.getAssociatedAnalysis().getLastEntry());
//			final Matrix taskAssignmentMatrix = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(metricsSession.getAssociatedAnalysis().getLastEntry(), Dependency.TASK_ASSIGNMENT);
//			System.out.println(taskAssignmentMatrix.getRows()+", "+taskAssignmentMatrix.getColumns());
//			final Matrix taskDependencyMatrix = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(metricsSession.getAssociatedAnalysis().getLastEntry(), Dependency.TASK_DEPENDENCY);
//			System.out.println(taskDependencyMatrix.getRows()+", "+taskDependencyMatrix.getColumns());
//			final Matrix matrix = taskAssignmentMatrix.multiply(taskDependencyMatrix).multiply(taskAssignmentMatrix.getTransposeMatrix());
//			JUNGGraph graph = new JUNGGraph();
//			graph = JUNGGraph.convertMatrixToJUNGGraph(matrix, dependencyDTO);
//			this.graph = Converter.convertJungToPrefuseGrapha(graph);
			
			final Dependency dependency = new DependencyDAO().findHighestDependencyByEntry(this.metricsSession.getAssociatedAnalysis().getId(), entry.getId(), representedDependency);
			JUNGGraph graph = new JUNGGraph();
			final Matrix m = this.metricsSession.getAssociatedAnalysis().processEntryDependencyMatrix(entry, dependency.getType());
			System.out.println(m.getColumns());
			graph = JUNGGraph.convertMatrixToJUNGGraph(m, dependency);
			System.out.println(graph.getGraph());
			this.graph = Converter.convertJungToPrefuseGraph(graph);
		}
		this.getDisplay().getVisualization().addGraph("graph", this.graph.getPrefuseGraph());
		this.getDisplay().getVisualization().run("draw");
		this.getDisplay().getVisualization().run("layout");
		this.currentRevision = newSequence;
	}

	@Override
	public void setLowerQuality() {
		this.display.setHighQuality(false);
	}

	@Override
	public void setHighQuality() {
		this.display.setHighQuality(true);
	}

	@Override
	public void updateVisualizationLimits(int inferiorLimit, int superiorLimit) throws DatabaseException {
		this.updateGraph(superiorLimit);
	}
}
