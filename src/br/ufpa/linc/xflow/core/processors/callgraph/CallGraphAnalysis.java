package br.ufpa.linc.xflow.core.processors.callgraph;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import br.ufpa.linc.xflow.core.AnalysisFactory;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencySetDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.Converter;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.data.representation.matrix.MatrixFactory;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

@Entity(name = "callgraph_analysis")
@DiscriminatorValue(""+AnalysisFactory.CALLGRAPH_ANALYSIS)
public class CallGraphAnalysis extends Analysis{

	@Transient
	private Matrix matrixCache = null;
	
	@Column(name = "is_whole_system_snapshot")
	private boolean wholeSystemSnapshot;
	
	@Transient
	private Dependency dependencyCache = null;
	
	@Transient
	private JUNGGraph graphCache = null;
	
	public CallGraphAnalysis(){
		this.setType(AnalysisFactory.CALLGRAPH_ANALYSIS);
		this.setWholeSystemSnapshot(false);
	}
	
	@Override
	public JUNGGraph processEntryDependencyGraph(Entry entry, int dependencyType)
			throws DatabaseException {
		final Matrix matrix;
		Dependency dependency = new DependencyDAO().findDependencyByEntry(this.getId(), entry.getId(), dependencyType);
		
		if(dependency == null){
			if(dependencyCache != null){
				return graphCache;
			}
			else{
				return null;
			}
		}
		
		if(matrixCache == null){
			matrix = processHistoricalDependencyMatrix(dependency);
			matrixCache = matrix;
			dependencyCache = dependency;
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(matrix, dependency);
		}
		else{
			Matrix processedMatrix = processDependencyMatrix(dependency);
//			matrix = processedMatrix.sumDifferentOrderMatrix(matrixCache);
//			matrixCache = matrix;
//			dependencyCache = dependency;
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(processedMatrix, dependency);
//			graphCache = JUNGGraph.convertMatrixToJUNGGraph(processedMatrix, dependency, graphCache);
		}
		
		return graphCache;
	}

	private Matrix processDependencyMatrix(Dependency dependency) {
		if(!dependency.getDependencies().isEmpty()){
			return Converter.convertDependenciesToMatrix(new ArrayList<DependencySet>(dependency.getDependencies()), dependency.isDirectedDependency());
		}
		else {
			return MatrixFactory.createMatrix();
		}
	}

	@Override
	public JUNGGraph processDependencyGraph(Dependency entryDependency)
			throws DatabaseException {
		final Matrix matrix;
		Dependency dependency = new DependencyDAO().findDependencyByEntry(this.getId(), entryDependency.getAssociatedEntry().getId(), entryDependency.getType());
		
		if(dependency == null){
			if(dependencyCache != null){
				return graphCache;
			}
			else{
				return null;
			}
		}
		
			if(matrixCache == null){
			matrix = processHistoricalDependencyMatrix(dependency);
			matrixCache = matrix;
			dependencyCache = dependency;
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(matrix, dependency);
		}
		else{
			Matrix processedMatrix = processDependencyMatrix(dependency);
//			matrix = processedMatrix.sumDifferentOrderMatrix(matrixCache);
//			matrixCache = matrix;
//			dependencyCache = dependency;
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(processedMatrix, dependency);
//			graphCache = JUNGGraph.convertMatrixToJUNGGraph(processedMatrix, dependency, graphCache);
		}
		
		return graphCache;
	}

	@Override
	public Matrix processEntryDependencyMatrix(Entry entry, int dependencyType)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkCutoffValues(Entry entry) {
		if(this.getMaxFilesPerRevision() <= 0) return true;
		if(entry.getEntryFiles().size() > this.getMaxFilesPerRevision()){
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public final Matrix processHistoricalDependencyMatrix(final Dependency dependency) throws DatabaseException {
		
		if(!dependency.getDependencies().isEmpty()){
			final List<DependencySet> dependencySets = new DependencySetDAO().getAllDependenciesSetUntilDependency(dependency);
			final Matrix matrix = Converter.convertDependenciesToMatrix(dependencySets, true);
			return matrix;
		}
		else{
			return matrixCache;
		}
	}

	public void setWholeSystemSnapshot(boolean wholeSystemSnapshot) {
		this.wholeSystemSnapshot = wholeSystemSnapshot;
	}

	public boolean isWholeSystemSnapshot() {
		return wholeSystemSnapshot;
	}

//	public void setWholeSystemSnapshot(boolean wholeSystemSnapshot) {
//		this.wholeSystemSnapshot = wholeSystemSnapshot;
//	}
//
//	public boolean isWholeSystemSnapshot() {
//		return wholeSystemSnapshot;
//	}


}
