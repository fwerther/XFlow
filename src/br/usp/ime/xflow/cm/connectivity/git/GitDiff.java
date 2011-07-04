package br.usp.ime.xflow.cm.connectivity.git;

public class GitDiff {

	private final String name;
	private final String content;
	private final ArtifactStatus status;
	public GitDiff(String name, String content, ArtifactStatus status) {
		super();
		this.name = name;
		this.content = content;
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public String getContent() {
		return content;
	}
	public ArtifactStatus getStatus() {
		return status;
	}
	
	
}
