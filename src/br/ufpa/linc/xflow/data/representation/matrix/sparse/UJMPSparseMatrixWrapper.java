package br.ufpa.linc.xflow.data.representation.matrix.sparse;

import org.ujmp.core.exceptions.MatrixException;
import org.ujmp.core.intmatrix.impl.DefaultSparseIntMatrix;

import br.ufpa.linc.xflow.data.representation.matrix.Matrix;

public class UJMPSparseMatrixWrapper extends DefaultSparseIntMatrix implements Matrix {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8822469000920140479L;

	public UJMPSparseMatrixWrapper() {
		super(0L, 0L);
	}
	
	public UJMPSparseMatrixWrapper(org.ujmp.core.Matrix m) throws MatrixException {
		super(m);
	}

	@Override
	public int getRows() {
		return (int) getRowCount();
	}

	@Override
	public int getColumns() {
		return (int) getColumnCount();
	}

	@Override
	public int getValueAt(int row, int column) {
		return getInt(row, column);
	}

	@Override
	public void putValueAt(int value, int row, int column) {
		setInt(value, row, column);
	}

	@Override
	public void incrementValueAt(int value, int row, int column) {
		int previousValue = getInt(row, column);
		setInt(previousValue+value, row, column);
	}

	@Override
	public Matrix multiply(Matrix anotherMatrix) {
		return new UJMPSparseMatrixWrapper(mtimes((org.ujmp.core.Matrix) anotherMatrix));
	}

	@Override
	public Matrix sum(Matrix anotherMatrix) {
		return new UJMPSparseMatrixWrapper(((org.ujmp.core.Matrix) anotherMatrix));
	}

	@Override
	public Matrix sumDifferentOrderMatrix(Matrix anotherMatrix) {
		try {
			return new UJMPSparseMatrixWrapper(plus((DefaultSparseIntMatrix) anotherMatrix));
		} catch (IllegalArgumentException e ){
			final int maxRows = (int) Math.max(this.getSize(0), anotherMatrix.getRows());
			final int maxColumns = (int) Math.max(this.getSize(1), anotherMatrix.getColumns());
			
			this.setSize(maxRows, maxColumns);
			anotherMatrix.incrementMatrixRowsTo(maxRows-anotherMatrix.getRows());
			anotherMatrix.incrementMatrixColumnsTo(maxColumns-anotherMatrix.getColumns());
			
			return new UJMPSparseMatrixWrapper(plus((DefaultSparseIntMatrix) anotherMatrix));
		}
	}

	@Override
	public Matrix getTransposeMatrix() {
		return (Matrix) new UJMPSparseMatrixWrapper(transpose());
	}

	@Override
	public Matrix createIdentityMatrix(int size) {
		//TODO;
		return null;
	}

	@Override
	public void applyStatisticalFilters(int support, double confidence) {
		//TODO;
	}

	@Override
	public void incrementMatrixRowsTo(int newSize) {
		this.setSize(this.getSize()[0]+newSize, this.getSize()[1]);
	}

	@Override
	public void incrementMatrixColumnsTo(int newSize) {
		this.setSize(this.getSize()[0], this.getSize()[1]+newSize);
	}

}
