//package br.ufpa.linc.xflow.data.representation.matrix;
//
//import java.util.ArrayList;
//
//import br.ufpa.linc.xflow.data.entities.ObjFile;
//import br.ufpa.linc.xflow.data.representation.matrix.sparse.SparseMatrix;
//
//public class XFlowSparseMatrixImpl {
//
//	protected SparseMatrix sparseMatrix;
//
//	public XFlowSparseMatrixImpl(){
//		sparseMatrix = new SparseMatrix();
//	}
//	
//	public XFlowSparseMatrixImpl(final int size){
//		sparseMatrix = new SparseMatrix(size,size);
//	}
//
//	public XFlowSparseMatrixImpl(final int rowSize, final int columnSize) {
//		sparseMatrix = new SparseMatrix(rowSize,columnSize);
//	}
//	
//	public XFlowSparseMatrixImpl(SparseMatrix sparseMatrix){
//		this.sparseMatrix = sparseMatrix;
//	}
//
//	public SparseMatrix getSparseMatrix() {
//		return sparseMatrix;
//	}
//	
//	public void setSparseMatrix(SparseMatrix sparseMatrix){
//		this.sparseMatrix = sparseMatrix;
//	}
//
//	/*
//	 * MATRIX OPERATION AND GENERAL HANDLING METHODS.
//	 */
//
//	public int getRows(){
//		return this.sparseMatrix.getRows();
//	}
//	
//	public int getColumns(){
//		return this.sparseMatrix.getColumns();
//	}
//	
//	public int get(final int row, final int column){
//		return sparseMatrix.getValueAt(row, column);
//	}
//
//	public void put(final int value, final int row, final int column){
//		sparseMatrix.putValueAt(row, column, value);
//	}
//
//	public void increment(final int value, final int row, final int column) {
//		sparseMatrix.incrementValueAt(row, column, value);
//	}
//
//	public void incrementValues(final ArrayList<ObjFile> files) {
//		for (int i = 0; i < files.size()-1; i++) {
//			for (int j = 0; j < files.size()-1; j++) {
//				this.sparseMatrix.incrementValueAt(i, j, 1);
//			}
//		}
//	}
//
//	public XFlowSparseMatrixImpl multiply(XFlowSparseMatrixImpl anotherMatrix) {
//		SparseMatrix m1 = this.sparseMatrix;
//		SparseMatrix m2 = anotherMatrix.getSparseMatrix();
//		
//		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(m1.multiply(m2));
//		return resultMatrix;
//	}
//	
//	public XFlowSparseMatrixImpl sum(XFlowSparseMatrixImpl anotherMatrix) {
//		SparseMatrix m1 = sparseMatrix;
//		SparseMatrix m2 = anotherMatrix.getSparseMatrix();
//		
//		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(m1.sum(m2));
//		return resultMatrix;
//	}
//	
//	public XFlowSparseMatrixImpl sumDifferentOrderMatrix(XFlowSparseMatrixImpl anotherMatrix) {
//		SparseMatrix m1 = sparseMatrix;
//		SparseMatrix m2 = anotherMatrix.getSparseMatrix();
//		
//		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(m1.sumDifferentOrderMatrix(m2));
//		return resultMatrix;
//	}
//		
//	public XFlowSparseMatrixImpl getTransposeMatrix(){
//		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(sparseMatrix.getTransposedMatrix());
//		return resultMatrix;
//	}
//
//	public XFlowSparseMatrixImpl createIdentityMatrix(final int size){
//		XFlowSparseMatrixImpl identity = new XFlowSparseMatrixImpl(new SparseMatrix().createIdentityMatrix(size));
//		return identity;
//	}
//	
//	public void applyStatisticalFilters(int support, double confidence){
//		
//		for (int i = 0; i < sparseMatrix.getRows(); i++) {
//			for (int j = 0; j < sparseMatrix.getColumns(); j++) {
//				if(confidence == 0){
//					support = support > 0 ? support : 1;
//					if(sparseMatrix.getValueAt(i,j) >= support){
//						sparseMatrix.putValueAt(i, j, 1);
//					}
//					else{
//						sparseMatrix.putValueAt(i, j, 0);
//					}
//				}
//				else {
//					double timesChanged = sparseMatrix.getValueAt(i, i);
//					if(support == 0 && timesChanged != 0){
//						if ((sparseMatrix.getValueAt(i,j) / timesChanged) >= confidence){
//							sparseMatrix.putValueAt(i, j, 1);
//						}
//					}
//					else if (timesChanged != 0){
//						if (((sparseMatrix.getValueAt(i,j) / timesChanged) >= confidence) && (timesChanged >= support))
//							sparseMatrix.putValueAt(i, j, 1);
//					}
//				}
//			}
//		}
//	}
//	
//	/*
//	 * AUGMENT MATRIX SIZE METHODS
//	 */
//	
//	public final XFlowSparseMatrixImpl incrementMatrixRowsTo(int newSize){
//		int rowsToBeIncreased = newSize - this.getSparseMatrix().getRows();
//		this.sparseMatrix.increaseDimensions(rowsToBeIncreased, 0);
//		return this;
//	}
//	
//	public final XFlowSparseMatrixImpl incrementMatrixColumnsTo(int newSize){
//		int colsToBeIncreased = newSize - this.getSparseMatrix().getColumns();
//		this.sparseMatrix.increaseDimensions(0, colsToBeIncreased);
//		return this;
//	}
//	
//	public String toString(){
//		return this.sparseMatrix.toString();
//	}
//}
