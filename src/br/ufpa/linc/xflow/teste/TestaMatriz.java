package br.ufpa.linc.xflow.teste;

import br.ufpa.linc.xflow.data.representation.matrix.Matrix;

public class TestaMatriz {

	public static void main(String[] args) throws br.ufpa.linc.xflow.exception.persistence.DatabaseException {
//		br.ufpa.linc.xflow.data.representation.matrix.Matrix taskDependencyMatrix = new MatrixDAO().findById(Matrix.class, 18530L);
//		System.out.println(taskDependencyMatrix.getMatrix().length);
//		System.out.println(taskDependencyMatrix.getMatrix()[0].length);
//		System.out.println("------------- MATRIZ ------------");
		
//		br.ufpa.linc.xflow.data.entities.Matrix taskDependencyMatrix2 = new MatrixDAO().findById(Matrix.class, 10759L);
//		System.out.println(taskDependencyMatrix2.getMatrix().length);
//		taskDependencyMatrix.applyStatisticalFilters(0, 0);
		
//		for (int i = 0; i < taskDependencyMatrix.getMatrix().length; i++) {
//			for (int j = 0; j < taskDependencyMatrix.getMatrix()[0].length; j++) {
//				System.out.print(taskDependencyMatrix.get(i, j)+" ");
//			}
//			System.out.println();
//		}
		
//		Matrix matrix = new br.ufpa.linc.xflow.core.processors.cochanges.CoChangesCollector().calculateMacCormacksDerivation(taskDependencyMatrix);
//		
//		int higherFIV = Integer.MIN_VALUE;
//		int lowerFIV = Integer.MAX_VALUE;
//		int higherFOV = Integer.MIN_VALUE;
//		int lowerFOV = Integer.MAX_VALUE;
//		int averageFIV = 0;
//		int averageFOV = 0;
//		
//		for (int i = 0; i < matrix.getMatrix().length; i++) {
//			int componentFIV = 0;
//			int componentFOV = 0;
//			for (int j = 0; j < matrix.getMatrix().length; j++) {
//				if(j == i){
//					// Do nothing.
//				}
//				else{
//					componentFIV += matrix.getMatrix()[i][j];
//					componentFOV += matrix.getMatrix()[j][i];
//				}
//			}
//			
//			if(componentFIV > higherFIV) higherFIV = componentFIV;
//			if(componentFOV > higherFOV) higherFOV = componentFOV;
//			if(componentFIV < lowerFIV) lowerFIV = componentFIV;
//			if(componentFOV < lowerFOV) lowerFOV = componentFOV;
//			
//			System.out.println("FILE "+i+": -FIV: "+componentFIV+" +FOV: "+componentFOV);
//			
//			averageFIV += componentFIV;
//			averageFOV += componentFOV;
//		}
//		
//		averageFIV = averageFIV / matrix.getMatrix().length;
//		averageFOV = averageFOV / matrix.getMatrix().length;
//		
//		System.out.println();
//		
//		System.out.println("--- AVERAGE FIV: "+averageFIV);
//		System.out.println("+++ AVERAGE FOV: "+averageFOV);
//		System.out.println("--- HIGHER FIV: "+higherFIV);
//		System.out.println("--- LOWER FIV: "+lowerFIV);
//		System.out.println("+++ HIGHER FOV: "+higherFOV);
//		System.out.println("+++ LOWER FOV: "+lowerFOV);
//		
//		for (int i = 0; i < taskDependencyMatrix.getMatrix().length; i++) {
//			for (int j = 0; j < taskDependencyMatrix.getMatrix()[0].length; j++) {
//				System.out.print(taskDependencyMatrix.getMatrix()[i][j]+", ");
//			}
//			System.out.println();
//		}
	}
	
}
