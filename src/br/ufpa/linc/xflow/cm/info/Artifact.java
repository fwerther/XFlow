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
 *  =============
 *  Artifact.java
 *  =============
 *  
 *  Original Author: Francisco Santana;
 *  Contributor(s):  -;
 *  
 */

package br.ufpa.linc.xflow.cm.info;

public final class Artifact {

	private char changeType;
	private String targetPath;
	private String artifactKind;
	private String sourceCode;
	private String diffCode;


	public Artifact(){
		// Empty constructor.
	}

	public Artifact(final char changeType, final String targetPath, final String artifactKind){
		this.changeType = changeType;
		this.targetPath = targetPath;
		this.artifactKind = artifactKind;
	}


	public final char getChangeType() {
		return changeType;
	}

	public final void setChangeType(final char changeType) {
		this.changeType = changeType;
	}

	public final String getTargetPath() {
		return targetPath;
	}

	public final void setTargetPath(final String targetPath) {
		this.targetPath = targetPath;
	}

	public final String getArtifactKind() {
		return artifactKind;
	}

	public final void setArtifactKind(final String artifactKind) {
		this.artifactKind = artifactKind;
	}

	public final String getSourceCode() {
		return sourceCode;
	}

	public final void setSourceCode(final String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public final String getDiffCode() {
		return diffCode;
	}

	public final void setDiffCode(final String diffCode) {
		this.diffCode = diffCode;
	}

}
