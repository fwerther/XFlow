package br.usp.ime.xflow.cm.connectivity.git;


public class GitLogResult {

	private final String author;
	private final String message;
	private final String email;
	private final String hash;
	private final String date;
	private String diffs;

	public GitLogResult(String hash, String date, String author, String email, String message, String diffs) {
		this.hash = hash;
		this.author = author;
		this.email = email;
		this.diffs = diffs;
		this.message = message;
		this.date = date;
	}

	public String getEmail() {
		return email;
	}

	public String getAuthor() {
		return author;
	}

	public String getMessage() {
		return message;
	}

	public String getHash() {
		return hash;
	}

	public String getDiffs() {
		return diffs;
	}

	public void setDiffs(String diffs) {
		this.diffs = diffs;
	}

	public String getDate() {
		return date;
	}

	
	
}
