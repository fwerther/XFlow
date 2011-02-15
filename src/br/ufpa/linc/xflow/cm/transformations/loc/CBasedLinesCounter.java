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
 *  =======================
 *  CBasedLinesCounter.java
 *  =======================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.cm.transformations.loc;

import java.util.ArrayList;
import java.util.List;

public class CBasedLinesCounter implements LOCCounter {

	@Override
	public int countFileLOC(String sourceCode) {
		sourceCode = removeInvalidLines(sourceCode);
		return sourceCode.split("\n").length;
	}
	
	@Override
	public String removeInvalidLines(String sourceCode){
		sourceCode = removeCommentedLines(sourceCode);
		sourceCode = removeBlankLines(sourceCode);

		return sourceCode;
	}

	private String removeBlankLines(final String sourceCode) {
		String[] linesOfCode = sourceCode.split("\n");
		List<String> validLines = new ArrayList<String>();
		for (int i = 0; i < linesOfCode.length; i++) {
			if(linesOfCode[i].trim().length() > 0){
				validLines.add(linesOfCode[i]+"\n");
			}
		}
		StringBuffer buffer = new StringBuffer();
		for (String string : validLines) {
			buffer.append(string);
		}

		return buffer.toString();
	}

	private String removeCommentedLines(final String sourcecode) {
		String cleanCode = removeBlockComments(sourcecode);
		cleanCode = removeSingleLineComments(cleanCode);
		return cleanCode;
	}

	private String removeSingleLineComments(String sourcecode) {
		sourcecode = sourcecode.replaceAll("(//.*)", "");
		return sourcecode;
	}

	//TODO: TALVEZ TENHA ERRO AKEE!
	private String removeBlockComments(String sourcecode) {

		int currentCommentIndex = -1;
		
		/*
		 * Checks if block comment starter exists. If no "/*" symbols are found, do not waste time.
		 */
		if(sourcecode.contains("/*")){
			int stringIndexFinder = 0;
			while(sourcecode.indexOf("/*", currentCommentIndex) > -1){
				
				currentCommentIndex = sourcecode.indexOf("/*", currentCommentIndex)+1;
				int quotesFound = 0;
				
				/*
				 * Checks for "/*" starting on line comments (e.g.: //  /* not a valid block comment).
				 */
				int lastLineFeed = sourcecode.lastIndexOf("\n", currentCommentIndex-1);
				int lastLineComment = sourcecode.lastIndexOf("//", currentCommentIndex-1);
				
				if(lastLineComment != -1){
					if(lastLineComment > lastLineFeed){
						continue;
					}
				}
				
				/*
				 * Checks for quotation marks, since strings can have "/*" symbols.
				 * These symbols are not valid and thus they shouldn't be considered.
				 * 
				 * Cases: a) " as part of string (e.g.: "\"");
				 *        b) " as character      (e.g.: '\"');
				 */
				while((sourcecode.indexOf("\"", stringIndexFinder) < currentCommentIndex) && (sourcecode.indexOf("\"", stringIndexFinder) > 0)){
					int quoteFoundIndex = sourcecode.indexOf("\"", stringIndexFinder);
					stringIndexFinder = quoteFoundIndex+1;
					if(quoteFoundIndex > 0){
						String checkForCommentedLine = sourcecode.substring(sourcecode.lastIndexOf("\n", quoteFoundIndex)+2, sourcecode.indexOf("\n", quoteFoundIndex));
						if(checkForCommentedLine.trim().startsWith("//")){
							continue;
						}
						if(sourcecode.charAt(quoteFoundIndex-1) == '\\'){
							int slashesIndex = 2;
							while(sourcecode.charAt(quoteFoundIndex-slashesIndex) == '\\'){
								slashesIndex++;
							}
							if(((slashesIndex - 1) % 2) == 0){
								quotesFound++;
							}
						}
						else if(sourcecode.charAt(quoteFoundIndex+1) != '\''){
							quotesFound++;
						}
						else {

						}
					}
				}
				
				/* IF ZERO or EVEN number of quotation marks are found, THEN block comment is valid.
				 * Search for block comment ending and remove invalid lines of code.
				 * 
				 * Invalid situation: "this is not a valid comment block/*"
				 */
				if(quotesFound%2 == 0 || quotesFound == 0){
					int commentCloserIndex = sourcecode.indexOf("*/", currentCommentIndex);
					if(commentCloserIndex == -1){
						break;
					}
					else{
						StringBuffer stringBuffer = new StringBuffer(sourcecode.substring(0, currentCommentIndex-1));
						sourcecode = sourcecode.substring(commentCloserIndex+2);
						stringBuffer.append(sourcecode);
						sourcecode = new String(stringBuffer);
					}
				}
			}
		}

		return sourcecode;
	}

}
