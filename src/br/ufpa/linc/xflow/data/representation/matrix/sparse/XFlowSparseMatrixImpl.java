package br.ufpa.linc.xflow.data.representation.matrix.sparse;

import java.util.HashMap;

import br.ufpa.linc.xflow.data.representation.matrix.Matrix;

public class XFlowSparseMatrixImpl implements Matrix {

	private int rows;
	private int columns;
	
	private HashMap<String,Integer> map = new HashMap<String,Integer>();
		
	public XFlowSparseMatrixImpl(){
		this.rows = 0;
		this.columns = 0;		
	}
		
	public XFlowSparseMatrixImpl(int rows, int columns){
		this.rows = rows;
		this.columns = columns;
	}
	
	
	/*
	 * AUGMENT MATRIX SIZE METHODS
	 */
	
	public int getRows(){
		return rows;
	}
	
	public int getColumns(){
		return columns;
	}
	
	public HashMap<String, Integer> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}
	
	public int getValueAt(int row, int column){
		if (row >= rows || column >= columns){
			throw new IllegalArgumentException("Invalid index");
		}
		String key = getKey(row, column);
		Integer value = map.get(key);
		if (value == null) value = 0;
		return value;
	}
	
	public void putValueAt(int value, int row, int column){
		if (row >= rows || column >= columns){
			throw new IllegalArgumentException("Invalid index");
		}
		String key = getKey(row, column);
		map.put(key, value);
	}

	public void incrementValueAt(int increment, int row, int column){
		if ((row >= this.rows) || (column >= this.columns)){
			throw new IllegalArgumentException("Invalid index");
		}
		Integer value = getValueAt(row, column);
		value += increment;
		putValueAt(value, row, column);
	}
		
	
	public void increaseDimensions(int rowsIncreased, int columnsIncreased){
		rows += rowsIncreased;
		columns += columnsIncreased;
	}

	public Matrix sum(Matrix m2){
		if (this.isAdditionCompatible(m2)){
			throw new IllegalArgumentException(
					"Matrices are not addition compatible"); 
		}
		
		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(rows,columns);
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.columns; j++) {
				resultMatrix.incrementValueAt(this.getValueAt(i,j), i, j);
				resultMatrix.incrementValueAt(m2.getValueAt(i,j), i, j);
			}
		}
		
		return resultMatrix;
	}
	
	public Matrix multiply(Matrix m2){
		
		if (!this.isMultiplicationCompatible(m2)){
			throw new IllegalArgumentException(
					"Matrices are not multiplication compatible"); 
		}
		
		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(this.getRows(), m2.getColumns());
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < m2.getColumns(); j++) {
				for (int k = 0; k < this.columns; k++) {
					resultMatrix.incrementValueAt(this.getValueAt(i,k) * m2.getValueAt(k,j), i, j);
					if(resultMatrix.getValueAt(i, j) < 0){
						resultMatrix.putValueAt(Integer.MAX_VALUE, i,j);
					}
				}
			}
		}
		
		return resultMatrix;
	}
	
	public Matrix sumDifferentOrderMatrix(final Matrix m2) {
		
		if(m2 == null){
			return this;
		}
		
		//Increase rows if necessary
		if(m2.getRows() < this.getRows()){
			int rowsToBeIncreased = this.getRows() - m2.getRows();
			m2.incrementMatrixRowsTo(rowsToBeIncreased);
		}
		else if (m2.getRows() > this.getRows()){
			int rowsToBeIncreased = m2.getRows() - this.getRows();
			this.increaseDimensions(rowsToBeIncreased, 0);
		}

		//Increase columns if necessary
		
		if(m2.getColumns() < this.getColumns()){
			int colsToBeIncreased = this.getColumns() - m2.getColumns();
			m2.incrementMatrixColumnsTo(colsToBeIncreased);
		}
		else if (m2.getColumns() > this.getColumns()){
			int colsToBeIncreased = m2.getColumns() - this.getColumns();
			this.increaseDimensions(0, colsToBeIncreased);
		}
		
		final XFlowSparseMatrixImpl result = new XFlowSparseMatrixImpl(this.getRows(), this.getColumns());
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getColumns(); j++) {
				int value = this.getValueAt(i, j) + m2.getValueAt(i, j);
				result.putValueAt(value, i, j); 
			}
		}
		return result;
	}
	
	public XFlowSparseMatrixImpl getTransposedMatrix(){
		XFlowSparseMatrixImpl resultMatrix = new XFlowSparseMatrixImpl(columns, rows);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int value = this.getValueAt(i,j);
				resultMatrix.putValueAt(value, j, i);
			}
		}
		return resultMatrix;
	}
	
	public XFlowSparseMatrixImpl createIdentityMatrix(int size){
		XFlowSparseMatrixImpl identityMatrix = new XFlowSparseMatrixImpl(size,size);
		for (int i = 0; i < size; i++) {
			identityMatrix.putValueAt(1, i, i);
		}
		return identityMatrix;
	}
	
	private String getKey(int row, int column){
		StringBuilder builder = new StringBuilder();
		builder.append(row).append(",").append(column);
		return builder.toString();
	}
		
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				sb.append(getValueAt(i, j) + "\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	//Deep copy
	public XFlowSparseMatrixImpl copy(){
		XFlowSparseMatrixImpl copy = new XFlowSparseMatrixImpl(this.rows,this.columns);
		copy.map.putAll(this.map);
		return copy;
	}
	
	public boolean isAdditionCompatible(Matrix m2){
		boolean compatible = false;
		
		if (this.getRows() == m2.getRows() && 
			this.getColumns() == m2.getColumns()){			
			
			compatible = true;		
		}
		return compatible;		
	}

	public boolean isMultiplicationCompatible(Matrix m2){
		boolean compatible = false;
		
		if (this.getColumns() == m2.getRows()){
			compatible = true;
		}
		return compatible;
	}

	@Override
	public Matrix getTransposeMatrix() {
		Matrix resultMatrix = new XFlowSparseMatrixImpl(columns, rows);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int value = this.getValueAt(i,j);
				resultMatrix.putValueAt(value, j, i);
			}
		}
		return resultMatrix;
	}

	@Override
	public void applyStatisticalFilters(int support, double confidence) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void incrementMatrixRowsTo(int newSize) {
		this.increaseDimensions(newSize, 0);
	}

	@Override
	public void incrementMatrixColumnsTo(int newSize) {
		this.increaseDimensions(0, newSize);
	}
}