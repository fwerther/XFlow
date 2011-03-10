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
 *  ===================
 *  SVNDiffHandler.java
 *  ===================
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.cm.transformations.loc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufpa.linc.xflow.data.entities.ObjFile;

public final class SVNDiffHandler extends DiffHandler {

	private final String SVNLineAddedSymbol = "+";
	private final String SVNLineRemovedSymbol = "-";

	/*
	 * This method is quite tricky since SVNKIT handles diff as using unix DIFF command.
	 * 
	 * '+' Symbols represents added lines;
	 * 
	 * '-' Symbols followed by '+' symbols represents modified lines. If there are
	 * different amounts of each symbol, it means there are the lowest found one 
	 * modified lines and the difference of them on added/removed lines.
	 * (e.g.):  3 '-' followed by 3 '+' in a row means there are 3 changed lines.
	 * 			3 '-' followed by 1 '+' in a row means there are 3 changed lines and 1 added line;
	 *          4 '-' followed by 3 '+' in a row means there are 3 changed lines and 1 removed line;
	 * 
	 * Isolated '-' Symbols represents removed lines.
	 * 
	 */

	@Override
	public final void gatherFileChanges(ObjFile file) {
		String[] totalCodeLines = gatherLoCChanges(file.getSourceCode(), file.getDiffCode());
		
		//Convert String array to String
		StringBuilder builder = new StringBuilder();
		for (String codeLine : totalCodeLines){
			builder.append(codeLine);
		}
		String wholeCode = builder.toString();		
		totalCodeLines = null;		
		
		wholeCode = LOCProcessor.getLocCounter().removeInvalidLines(wholeCode);
		String[] validCodeLines = wholeCode.split("\n");
		wholeCode = null;

		int flag = 1;
		final int lineAdditionFlag = 1;

		int removedLineSymbolFound = 0;
		int addedLineSymbolFound = 0;

		for (int i = 0; i < validCodeLines.length; i++) {

			if(flag == lineAdditionFlag){

				/*
				 * Verify ADDED LINES and checks if they are NOT BLANK.
				 */
				if(validCodeLines[i].trim().startsWith(SVNLineAddedSymbol)){
					if(validCodeLines[i].trim().length() > 1){
						this.addedLines++;
					}
				}

				/*
				 * Verify REMOVED LINES. They're potentially MODIFIED LINES.
				 * Also, flag is set to non-addition value.
				 */
				else if(validCodeLines[i].trim().startsWith(SVNLineRemovedSymbol)){
					flag = 0;
					if(validCodeLines[i].trim().length() > 1){
						removedLineSymbolFound++;
					}
				}
			}

			else {

				/*
				 * Verify REMOVED LINES. Non-blank REMOVED LINES are accumulated,
				 * as they're potentially MODIFIED LINES.
				 */
				if(validCodeLines[i].trim().startsWith(SVNLineRemovedSymbol)){
					if(validCodeLines[i].trim().length() > 1){
						removedLineSymbolFound++;
					}
				}

				/*
				 * Verify MODIFIED LINES. All non-blank ADDED LINES immediately
				 * after REMOVED LINES are MODIFIED LINES instead.
				 */
				else if(validCodeLines[i].trim().startsWith(SVNLineAddedSymbol)){
					if(validCodeLines[i].trim().length() > 1){
						addedLineSymbolFound++;
					}
				}

				/*
				 * No line symbols found. REMOVED LINES and MODIFIED LINES
				 * calculations take place.
				 * 
				 * Flag is reset to line addition value.
				 */
				else{
					flag = lineAdditionFlag;

					modifiedLines += Math.min(addedLineSymbolFound, removedLineSymbolFound);
					if(addedLineSymbolFound > removedLineSymbolFound){
						addedLines += addedLineSymbolFound - removedLineSymbolFound;
					}
					else if(addedLineSymbolFound < removedLineSymbolFound){
						deletedLines += removedLineSymbolFound - addedLineSymbolFound;
					}
					removedLineSymbolFound = 0;
					addedLineSymbolFound = 0;
				}
			}
		}

		addedLines = (addedLineSymbolFound == 0 ? addedLines : addedLines + addedLineSymbolFound);
		deletedLines = (removedLineSymbolFound == 0 ? deletedLines : deletedLines + removedLineSymbolFound);
	}

	private final String[] gatherLoCChanges(String sourceCode, String diffCode) {
		final String headerInfoExtractor = "((^@@)(\\s-)((\\d+),(\\d+))(\\s\\+)((\\d+),(\\d+))\\s(@@$)(.*[\r\n][^@])+(.*[\r\n]))";
		List<String> wholeCode =  (List<String>) Arrays.asList(sourceCode.split("\n"));
		for (int i = 0; i < wholeCode.size(); i++) {
			while(wholeCode.get(i).trim().startsWith("-")){
				wholeCode.set(i, wholeCode.get(i).replaceFirst("-", ""+(char)0));
			}
			while(wholeCode.get(i).trim().startsWith("+")){
				wholeCode.set(i, wholeCode.get(i).replaceFirst("\\+", ""+(char)0));
			}
			wholeCode.set(i, wholeCode.get(i).concat("\n"));
		}
		LinkedList<String> newCode = new LinkedList<String>(wholeCode);
		wholeCode = null;

		Pattern p = Pattern.compile("("+headerInfoExtractor+")", java.util.regex.Pattern.MULTILINE);
		Matcher m = p.matcher(diffCode);

		int linesAddedIndex = 0;

		while (m.find()) {

			int removedLinesCounter = 0;

			/* 
			 * Changes occurs from "indexOfChangedLines"
			 * to "indexOfChangedLines + numOfChangedLines".  
			 */
			final int indexOfChangedLines = Integer.parseInt(m.group(10));
			final int numOfChangedLines = Integer.parseInt(m.group(11));

			/*
			 * "changedBlock" represents the whole set of changed lines,
			 * whether they've been added, removed or modified.
			 */
			String changedBlock = m.group(2);

			/*
			 * "toReplaceLines" has the lines that will act as 'replacers'
			 * to current code lines, adding changed lines info on them.
			 */
			String[] toReplaceLines = changedBlock.split("\n");
			for (int i = 0; i < toReplaceLines.length; i++) {
				if(toReplaceLines[i].startsWith("-")){
					removedLinesCounter++;
				}
				else if((toReplaceLines[i].contains("-")) || (toReplaceLines[i].contains("+"))){
					toReplaceLines[i] = toReplaceLines[i].replaceAll("(^[ \\t]+(\\+|-))", ""+(char)0);
				}
			}

			if(indexOfChangedLines+linesAddedIndex == 0){
				for (int j = 0; j < numOfChangedLines+removedLinesCounter; j++) {
					newCode.add(indexOfChangedLines+linesAddedIndex+j, toReplaceLines[j+1].concat("\n"));
				}
			}
			else{
				int numOfMissingLines = numOfChangedLines+removedLinesCounter;
				if(numOfMissingLines >= toReplaceLines.length-1){
					numOfMissingLines = toReplaceLines.length-1;
				}

				for (int j = 0; j < numOfChangedLines+removedLinesCounter; j++) {
					try {
						newCode.add(indexOfChangedLines+linesAddedIndex+j-1, toReplaceLines[j+1].concat("\n"));
					}
					catch(IndexOutOfBoundsException e){
						break;
					}
				}
			}

			/*
			 * Removed lines adds to current code lines, since they don't
			 * exist on new file. That so, removed lines must be counted since
			 * they represent added lines to current code.
			 */
			linesAddedIndex += removedLinesCounter;

			if(sourceCode.length() > 0){
				for (int i = 0; i < numOfChangedLines; i++) {
					try {
						newCode.remove(indexOfChangedLines+linesAddedIndex+numOfChangedLines-1);
					}
					catch(IndexOutOfBoundsException e){

					}
				}
			}
		}
		String[] finalCode = new String[0];
		finalCode = newCode.toArray(finalCode);
		return finalCode;
	}

}
