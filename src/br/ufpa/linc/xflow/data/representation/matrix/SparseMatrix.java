package br.ufpa.linc.xflow.data.representation.matrix;

import java.util.HashMap;

public class SparseMatrix {

	private long id;
	
	private int rows;
	
	private int columns;
	
	private HashMap<String,Integer> map = new HashMap<String,Integer>();
		
	public SparseMatrix(){
		this.rows = 0;
		this.columns = 0;		
	}
		
	public SparseMatrix(int rows, int columns){
		this.rows = rows;
		this.columns = columns;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRows(){
		return rows;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public int getColumns(){
		return columns;
	}
	
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	public HashMap<String, Integer> getMap() {
		return map;
	}

	public void setMap(HashMap<String, Integer> map) {
		this.map = map;
	}
	
	public Integer get(int row, int column){
		if (row >= rows || column >= columns){
			throw new IllegalArgumentException("Invalid index");
		}
		String key = getKey(row, column);
		Integer value = map.get(key);
		if (value == null) value = 0;
		return value;
	}
	
	public void put(int row, int column, int value){
		if (row >= rows || column >= columns){
			throw new IllegalArgumentException("Invalid index");
		}
		String key = getKey(row, column);
		map.put(key, value);
	}

	public void increment(int row, int column, int increment){
		if (row >= rows || column >= columns){
			throw new IllegalArgumentException("Invalid index");
		}
		Integer value = get(row, column);
		value += increment;
		put(row, column, value);
	}
		
	
	public void increaseDimensions(int rowsIncreased, int columnsIncreased){
		rows += rowsIncreased;
		columns += columnsIncreased;
	}

	public SparseMatrix sum(SparseMatrix m2){
		SparseMatrix m1 = this;
		
		if (m1.isAdditionCompatible(m2)){
			throw new IllegalArgumentException(
					"Matrices are not addition compatible"); 
		}
		
		SparseMatrix resultMatrix = new SparseMatrix(rows,columns);
		for (int i = 0; i < m1.rows; i++) {
			for (int j = 0; j < m1.columns; j++) {
				resultMatrix.increment(i,j,m1.get(i,j));
				resultMatrix.increment(i,j,m2.get(i,j));
			}
		}
		
		return resultMatrix;
	}
	
	public SparseMatrix multiply(SparseMatrix m2){
		SparseMatrix m1 = this;
		
		if (!m1.isMultiplicationCompatible(m2)){
			throw new IllegalArgumentException(
					"Matrices are not multiplication compatible"); 
		}
		
		SparseMatrix resultMatrix = new SparseMatrix(m1.getRows(), m2.getColumns());
		for (int i = 0; i < m1.rows; i++) {
			for (int j = 0; j < m2.columns; j++) {
				for (int k = 0; k < m1.columns; k++) {
					resultMatrix.increment(i, j, m1.get(i,k) * m2.get(k,j));
					if(resultMatrix.get(i, j) < 0){
						resultMatrix.put(i,j,Integer.MAX_VALUE);
					}
				}
			}
		}
		
		return resultMatrix;
	}
	
	public SparseMatrix sumDifferentOrderMatrix(final SparseMatrix m2) {
		
		SparseMatrix m1 = this;
		
		if(m2 == null){
			return m1;
		}
		
		//Increase rows if necessary
		if(m2.getRows() < m1.getRows()){
			int rowsToBeIncreased = m1.getRows() - m2.getRows();
			m2.increaseDimensions(rowsToBeIncreased,0);
		}
		else if (m2.getRows() > m1.getRows()){
			int rowsToBeIncreased = m2.getRows() - m1.getRows();
			m1.increaseDimensions(rowsToBeIncreased, 0);
		}

		//Increase columns if necessary
		
		if(m2.getColumns() < m1.getColumns()){
			int colsToBeIncreased = m1.getColumns() - m2.getColumns();
			m2.increaseDimensions(0,colsToBeIncreased);
		}
		else if (m2.getColumns() > m1.getColumns()){
			int colsToBeIncreased = m2.getColumns() - m1.getColumns();
			m1.increaseDimensions(0, colsToBeIncreased);
		}
		
		final SparseMatrix result = new SparseMatrix(m1.getRows(),m1.getColumns());
		for (int i = 0; i < m1.getRows(); i++) {
			for (int j = 0; j < m1.getColumns(); j++) {
				int value = m1.get(i, j) + m2.get(i, j);
				result.put(i, j, value); 
			}
		}
		return result;
	}
	
	public SparseMatrix getTransposedMatrix(){
		SparseMatrix m = this;
		SparseMatrix resultMatrix = new SparseMatrix(columns, rows);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int value = m.get(i,j);
				resultMatrix.put(j, i, value);
			}
		}
		return resultMatrix;
	}
	
	public static SparseMatrix createIdentityMatrix(int size){
		SparseMatrix identityMatrix = new SparseMatrix(size,size);
		for (int i = 0; i < size; i++) {
			identityMatrix.put(i, i, 1);
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
				sb.append(get(i, j) + "\t");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	//Deep copy
	public SparseMatrix copy(){
		SparseMatrix copy = new SparseMatrix(this.rows,this.columns);
		copy.map.putAll(this.map);
		return copy;
	}
	
	public boolean isAdditionCompatible(SparseMatrix m2){
		SparseMatrix m1 = this;
		boolean compatible = false;
		
		if (m1.getRows() == m2.getRows() && 
			m1.getColumns() == m2.getColumns()){			
			
			compatible = true;		
		}
		return compatible;		
	}

	public boolean isMultiplicationCompatible(SparseMatrix m2){
		SparseMatrix m1 = this;
		boolean compatible = false;
		
		if (m1.getColumns() == m2.getRows()){
			compatible = true;
		}
		return compatible;
	}
	
	public boolean hasRow(int row){
		return row < this.rows ? true : false;
	}
	
	public boolean hasColumn(int column){
		return column < this.columns ? true : false;
	}
}