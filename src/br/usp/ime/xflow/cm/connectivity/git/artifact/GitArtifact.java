package br.usp.ime.xflow.cm.connectivity.git.artifact;

import br.usp.ime.xflow.cm.connectivity.git.ArtifactStatus;

public class GitArtifact {

	private final String name;
	private final String sourceCode;
	private final String diff;
	private final ArtifactStatus status;

	public GitArtifact(String name, String diff, String sourceCode, ArtifactStatus status) {
		this.name = name;
		this.diff = diff;
		this.sourceCode = sourceCode;
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public String getDiff() {
		return diff;
	}

	public ArtifactStatus getStatus() {
		return status;
	}
	
	
}
