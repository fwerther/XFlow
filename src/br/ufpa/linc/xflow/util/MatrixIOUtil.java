/* 
 * 
 * XFlow
 * _______
 * 
 *  
 *  (C) Copyright 2010, by Universidade Federal do Pará (UFPA), Francisco Santana, Jean Costa, Pedro Treccani and Cleidson de Souza.
 * 
 *  This file is part of XFlow.
 *
 *  XFlow is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  XFlow is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XFlow.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  ==========================
 *  MatrixIOUtil.java
 *  ==========================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.util;

import java.io.FileWriter;
import java.io.IOException;

import br.ufpa.linc.xflow.data.dao.core.AuthorDependencyObjectDAO;
import br.ufpa.linc.xflow.data.dao.core.FileDependencyObjectDAO;
import br.ufpa.linc.xflow.data.entities.Dependency;
import br.ufpa.linc.xflow.data.entities.FileDependencyObject;
import br.ufpa.linc.xflow.data.representation.matrix.Matrix;
import br.ufpa.linc.xflow.exception.persistence.DatabaseException;

public abstract class MatrixIOUtil {

	public static final int UCINET_COMPATIBLE_FILE = 1;
	public static final int CSV_FILE = 2;
	
//	public static void exportMatrix(Matrix toBeExportedMatrix, int documentType, String destinyFolder) throws DatabaseException{
//		try {
//			switch (documentType) {
//			case UCINET_COMPATIBLE_FILE:
//				exportMatrixToUCINetCompatibleFile(toBeExportedMatrix, destinyFolder);
//				break;
//
//			case CSV_FILE:
//				exportMatrixToCSVFile(toBeExportedMatrix, destinyFolder);
//				break;		
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static void exportMatrixToUCINetCompatibleFile(Dependency dependency, Matrix matrix, String destinyFolder) throws IOException, DatabaseException {
//		FileWriter writer = new FileWriter(destinyFolder);
//
//		writer.append("DL N = "+matrix.getRows()+"\n");
//		writer.append("FORMAT = fullmatrix\n");
//		writer.append("LABELS EMBEDDED\n");
//		writer.append("DATA:\n");
//		
//		if(dependency.getType() == Dependency.AUTHOR_AUTHOR_DEPENDENCY){
//			for (int i = 0; i < matrix.getRows(); i++) {
//				String authorName = new AuthorDependencyObjectDAO().findDependencyObjectByStamp(dependency.getAssociatedAnalysis(), i).getAuthor().getName();
//				writer.append(authorName+" ");
//			}
//			writer.append('\n');
//
//			for (int i = 0; i < matrix.getRows(); i++) {
//				String authorName = new AuthorDependencyObjectDAO().findDependencyObjectByStamp(dependency.getAssociatedAnalysis(), i).getAuthor().getName();
//				writer.append(authorName+" ");
//				for (int j = 0; j < matrix.getColumns(); j++) {
//					writer.append(matrix.get(i,j)+" ");
//				}
//				writer.append('\n');
//			}
//		}
//		else if(dependency.getType() == Dependency.FILE_FILE_DEPENDENCY){
//			for (int i = 0; i < matrix.getRows(); i++) {
//				FileDependencyObject fileDTO = new FileDependencyObjectDAO().findDependencyObjectByStamp(dependency.getAssociatedAnalysis(), i);
//				if(fileDTO == null){
//					
//				} else {
//					long fileID = fileDTO.getFile().getId();
//					writer.append(fileID+" ");					
//				}
//			}
//			writer.append('\n');
//
//			System.out.println("label section finished");
//			
//			for (int i = 0; i < matrix.getRows(); i++) {
//				FileDependencyObject fileDTO = new FileDependencyObjectDAO().findDependencyObjectByStamp(dependency.getAssociatedAnalysis(), i);
//				if(fileDTO == null){
//					
//				} else {
//					long fileID = fileDTO.getFile().getId();
//					writer.append(fileID+" ");
//					for (int j = 0; j < matrix.getColumns(); j++) {
//						writer.append(matrix.get(i,j)+" ");
//					}
//					writer.append('\n');
//				}
//
//			}
//		}
//		
//		writer.flush();
//		writer.close();
//
//	}
//
//	private static void exportMatrixToCSVFile(Matrix matrix, String destinyFolder) throws IOException, DatabaseException {
//		FileWriter writer = new FileWriter(destinyFolder);
//
//		for (int i = 0; i < matrix.getMatrix().length; i++) {
//			String authorName = new AuthorDAO().findAuthorFromMatrixPosition(matrix.getAssociatedAnalysis().getProject(), i).getName();
//			writer.append(authorName+" ");
//		}
//		writer.append('\n');
//
//		for (int i = 0; i < matrix.getMatrix().length; i++) {
//			String authorName = new AuthorDAO().findAuthorFromMatrixPosition(matrix.getAssociatedAnalysis().getProject(), i).getName();
//			writer.append(authorName+" ");
//			for (int j = 0; j < matrix.getMatrix()[0].length; j++) {
//				writer.append(matrix.get(i,j)+" ");
//			}
//			writer.append('\n');
//		}
//		
//		writer.flush();
//		writer.close();
//	}
	
	public static Matrix importMatrix(int documentType, String fileLocation){
		Matrix importedMatrix = null;
		String fileContents = null;
		
		switch (documentType) {
		case UCINET_COMPATIBLE_FILE:
			importedMatrix = importFromUCINetCompatibleFile(fileContents);
			break;

		case CSV_FILE:
			importedMatrix = importMatrixFromCSVFile(fileContents);
			break;		
		}
		
		return importedMatrix;
	}

	private static Matrix importFromUCINetCompatibleFile(String fileContents) {
		return null;
	}
	
	private static Matrix importMatrixFromCSVFile(String fileContents) {
		return null;
	}
	
}
