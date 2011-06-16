package br.ufpa.linc.xflow.data.representation.matrix;

import br.ufpa.linc.xflow.data.representation.matrix.sparse.UJMPSparseMatrixWrapper;

public abstract class MatrixFactory {

	public static Matrix createMatrix(int matrixType){
		switch (matrixType) {
		case 0:
			
		default:
			return new br.ufpa.linc.xflow.data.representation.matrix.sparse.XFlowSparseMatrixImpl();
		}
	}
	
	public static Matrix createMatrix(){
		return new UJMPSparseMatrixWrapper();
	}
	
}
