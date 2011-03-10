package br.ufpa.linc.xflow.data.representation.matrix;

import java.util.ArrayList;

import br.ufpa.linc.xflow.data.entities.ObjFile;

public class Matrix {

	protected SparseMatrix sparseMatrix;

	public Matrix(){
		sparseMatrix = new SparseMatrix();
	}
	
	public Matrix(final int size){
		sparseMatrix = new SparseMatrix(size,size);
	}

	public Matrix(final int rowSize, final int columnSize) {
		sparseMatrix = new SparseMatrix(rowSize,columnSize);
	}
	
	public Matrix(SparseMatrix sparseMatrix){
		this.sparseMatrix = sparseMatrix;
	}

	public SparseMatrix getSparseMatrix() {
		return sparseMatrix;
	}
	
	public void setSparseMatrix(SparseMatrix sparseMatrix){
		this.sparseMatrix = sparseMatrix;
	}

	/*
	 * MATRIX OPERATION AND GENERAL HANDLING METHODS.
	 */

	public int getRows(){
		return this.sparseMatrix.getRows();
	}
	
	public int getColumns(){
		return this.sparseMatrix.getColumns();
	}
	
	public int get(final int row, final int column){
		return sparseMatrix.get(row, column);
	}

	public void put(final int value, final int row, final int column){
		sparseMatrix.put(row, column, value);
	}

	public void increment(final int value, final int row, final int column) {
		sparseMatrix.increment(row, column, value);
	}

	public void incrementValues(final ArrayList<ObjFile> files) {
		for (int i = 0; i < files.size()-1; i++) {
			for (int j = 0; j < files.size()-1; j++) {
				this.sparseMatrix.increment(i, j, 1);
			}
		}
	}

	public Matrix multiply(Matrix anotherMatrix) {
		SparseMatrix m1 = this.sparseMatrix;
		SparseMatrix m2 = anotherMatrix.getSparseMatrix();
		
		Matrix resultMatrix = new Matrix(m1.multiply(m2));
		return resultMatrix;
	}
	
	public Matrix sum(Matrix anotherMatrix) {
		SparseMatrix m1 = sparseMatrix;
		SparseMatrix m2 = anotherMatrix.getSparseMatrix();
		
		Matrix resultMatrix = new Matrix(m1.sum(m2));
		return resultMatrix;
	}
	
	public Matrix sumDifferentOrderMatrix(Matrix anotherMatrix) {
		SparseMatrix m1 = sparseMatrix;
		SparseMatrix m2 = anotherMatrix.getSparseMatrix();
		
		Matrix resultMatrix = new Matrix(m1.sumDifferentOrderMatrix(m2));
		return resultMatrix;
	}
		
	public Matrix getTransposeMatrix(){
		Matrix resultMatrix = new Matrix(sparseMatrix.getTransposedMatrix());
		return resultMatrix;
	}

	public static Matrix createIdentityMatrix(final int size){
		Matrix identity = new Matrix(SparseMatrix.createIdentityMatrix(size));
		return identity;
	}
	
	public void applyStatisticalFilters(int support, double confidence){
		
		for (int i = 0; i < sparseMatrix.getRows(); i++) {
			for (int j = 0; j < sparseMatrix.getColumns(); j++) {
				if(confidence == 0){
					support = support > 0 ? support : 1;
					if(sparseMatrix.get(i,j) >= support){
						sparseMatrix.put(i, j, 1);
					}
					else{
						sparseMatrix.put(i, j, 0);
					}
				}
				else {
					double timesChanged = sparseMatrix.get(i, i);
					if(support == 0 && timesChanged != 0){
						if ((sparseMatrix.get(i,j) / timesChanged) >= confidence){
							sparseMatrix.put(i, j, 1);
						}
					}
					else if (timesChanged != 0){
						if (((sparseMatrix.get(i,j) / timesChanged) >= confidence) && (timesChanged >= support))
							sparseMatrix.put(i, j, 1);
					}
				}
			}
		}
	}
	
	/*
	 * AUGMENT MATRIX SIZE METHODS
	 */
	
	public final Matrix incrementMatrixRowsTo(int newSize){
		int rowsToBeIncreased = newSize - this.getSparseMatrix().getRows();
		this.sparseMatrix.increaseDimensions(rowsToBeIncreased, 0);
		return this;
	}
	
	public final Matrix incrementMatrixColumnsTo(int newSize){
		int colsToBeIncreased = newSize - this.getSparseMatrix().getColumns();
		this.sparseMatrix.increaseDimensions(0, colsToBeIncreased);
		return this;
	}
	
	public String toString(){
		return this.sparseMatrix.toString();
	}
}
