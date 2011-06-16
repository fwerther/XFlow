package br.ufpa.linc.xflow.data.representation.matrix;


public interface Matrix {

	/*
	 * MATRIX OPERATION AND GENERAL HANDLING METHODS.
	 */
	public int getRows();
	public int getColumns();
	public int getValueAt(final int row, final int column);
	public void putValueAt(final int value, final int row, final int column);
	public void incrementValueAt(final int value, final int row, final int column);
	public Matrix multiply(Matrix anotherMatrix);
	public Matrix sum(Matrix anotherMatrix);
	public Matrix sumDifferentOrderMatrix(Matrix anotherMatrix);
	public Matrix getTransposeMatrix();
	public Matrix createIdentityMatrix(final int size);
	public void applyStatisticalFilters(int support, double confidence);
	
	/*
	 * AUGMENT MATRIX SIZE METHODS
	 */
	public void incrementMatrixRowsTo(int newSize);
	public void incrementMatrixColumnsTo(int newSize);
	
}
