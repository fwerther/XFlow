package br.usp.ime.xflow.cm.connectivity.git;

public enum ArtifactStatus {

	NEW("new file mode"), 
	DELETED("deleted file mode"), 
	DEFAULT("nothing");
	
	private final String pattern;

	ArtifactStatus(String pattern) {
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}
	
}
