package br.ufpa.linc.xflow.core.processors.cochanges;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import br.ufpa.linc.xflow.core.AnalysisFactory;
import br.ufpa.linc.xflow.data.dao.core.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.DependencySetDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.entities.Analysis;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.DependencyObject;
import br.ufpa.linc.xflow.data.entities.DependencySet;
import br.ufpa.linc.xflow.data.entities.Entry;
import br.ufpa.linc.xflow.data.representation.Converter;
import br.ufpa.linc.xflow.data.representation.jung.JUNGGraph;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

//TODO: AINDA TEM COISA PRA FAZER AQUI!
@Entity(name = "cochanges_analysis")
@DiscriminatorValue(""+AnalysisFactory.COCHANGES_ANALYSIS)
public final class CoChangesAnalysis extends Analysis {

	@Transient
	private Matrix matrixCache = null;
	
	@Transient
	private Dependency dependencyCache = null;
	
	@Transient
	private JUNGGraph graphCache = null;
	
	@Column(name = "ANALYSIS_FILE_LIMIT_PER_REVISION", nullable = false)
	private int maxFilesPerRevision;
	
	public CoChangesAnalysis() {
		super();
		this.setType(AnalysisFactory.COCHANGES_ANALYSIS);
	}
	
	
	public final int getMaxFilesPerRevision() {
		return maxFilesPerRevision;
	}

	public final void setMaxFilesPerRevision(final int maxFilesPerRevision) {
		this.maxFilesPerRevision = maxFilesPerRevision;
	}


//	public final Matrix calculateCoordinationRequirements(long revision) throws DatabaseException{
//		MatrixDAO matrixDAO = new MatrixDAO();
//
//		CoChangesMatrix taskAssignment = (CoChangesMatrix) matrixDAO.getTaskAssignmentByRevision(this, revision);
//		CoChangesMatrix taskDependency = (CoChangesMatrix) matrixDAO.getTaskDependencyByRevision(this, revision);
//
//		if((taskAssignment.getMatrix()[0].length > 0) && (taskDependency.getMatrix().length > 0)){
//			taskDependency.applyStatisticalFilters(this.getSupportValue(), this.getConfidenceValue());
//
//			Matrix coordReq = taskAssignment.multiply(taskDependency).multiply(taskAssignment.getTransposeMatrix());
//			coordReq.setAssociatedAnalysis(this);
//			coordReq.setEntry(taskAssignment.getEntry());
//			coordReq.setName("Coordination Requirements");
//			matrixDAO.insert(coordReq);
//			
//			return coordReq;
//		}
//		
//		return null;
//	}
//
//	public final Matrix calculateCoordinationRequirements(CoChangesMatrix taskAssignment, CoChangesMatrix taskDependency) throws DatabaseException {
//
//		Matrix coordinationRequirements = new CoChangesMatrix();
//
//		if((taskAssignment.getMatrix().length > 0) && (taskDependency.getMatrix().length > 0)){
//
//			taskDependency.applyStatisticalFilters();
//			
//			coordinationRequirements = taskAssignment.multiply(taskDependency);
//			coordinationRequirements = coordinationRequirements.multiply(coordinationRequirements.getTransposeMatrix());
//
//		}
//
//		coordinationRequirements.setName("Coordination Requirements");
//		coordinationRequirements.setAssociatedAnalysis(taskAssignment.getAssociatedAnalysis());
//		coordinationRequirements.setEntry(taskAssignment.getEntry());
//		
//		return coordinationRequirements;
//	}

	
	@Override
	public boolean checkCutoffValues(Entry entry) {
		if(this.maxFilesPerRevision <= 0) return true;
		if(entry.getEntryFiles().size() > this.maxFilesPerRevision){
			return false;
		}
		return true;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public Matrix processEntryDependencyMatrix(Entry entry, int dependencyType) throws DatabaseException {
		final Matrix matrix;
		Dependency dependency = new DependencyDAO().findDependencyByEntry(this.getId(), entry.getId(), dependencyType);
		
		if(dependency == null){
			if(dependencyCache != null){
				return matrixCache;
			}
			else{
				return null;
			}
		}
		
		if(matrixCache == null){
			matrix = processHistoricalDependencyMatrix(dependency);
			matrixCache = matrix;
			dependencyCache = dependency;
		}
		else{
			Matrix processedMatrix = processDependencyMatrix(dependency);
			matrix = processedMatrix.sumDifferentOrderMatrix(matrixCache);
			matrixCache = matrix;
			dependencyCache = dependency;
		}
		
		return matrix;
	}

	
	@Override
	@SuppressWarnings("rawtypes")
	public final JUNGGraph processEntryDependencyGraph(final Entry entry, final int dependencyType) throws DatabaseException {
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
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(processedMatrix, dependency, graphCache);
		}
		
		return graphCache;
	}
	
	@SuppressWarnings("rawtypes")
	public final JUNGGraph processDependencyGraph(final Dependency entryDependency) throws DatabaseException {
		final Matrix matrix;
		
		if(entryDependency == null){
			if(dependencyCache != null){
				return graphCache;
			}
			else{
				return null;
			}
		}
		
		if(matrixCache == null){
			matrix = processHistoricalDependencyMatrix(entryDependency);
			matrixCache = matrix;
			dependencyCache = entryDependency;
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(matrix, entryDependency);
		}
		else{
			Matrix processedMatrix = processDependencyMatrix(entryDependency);
//			matrix = processedMatrix.sumDifferentOrderMatrix(matrixCache);
//			matrixCache = matrix;
//			dependencyCache = dependency;
			graphCache = JUNGGraph.convertMatrixToJUNGGraph(processedMatrix, entryDependency, graphCache);
		}
		
		return graphCache;
	}
	
	@SuppressWarnings("rawtypes")
	public final Matrix processHistoricalDependencyMatrix(final Dependency dependency) throws DatabaseException {
		
		if(!dependency.getDependencies().isEmpty()){
			final List<DependencySet> dependencies = new DependencySetDAO().getAllDependenciesSetUntilDependency(dependency);
			final Matrix matrix = Converter.convertDependenciesToMatrixNew(dependencies, false);
			return matrix;
		}
		else{
			return matrixCache;
		}
	}

	@SuppressWarnings("rawtypes")
	public final Matrix processDependencyMatrix(final Dependency dependency) throws DatabaseException {
		
		if(!dependency.getDependencies().isEmpty()){
			final Matrix matrix = Converter.convertDependenciesToMatrixNew(new ArrayList<DependencySet>(dependency.getDependencies()), dependency.isDirectedDependency());
			return matrix;
		}
		else {
			return new Matrix(0);
		}
	}
}
