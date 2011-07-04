package br.usp.ime.xflow.cm.connectivity.git.artifact;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import br.usp.ime.xflow.cm.connectivity.git.ArtifactStatus;


public class GitCommit {

	private final String author;
	private final String email;
	private final String message;
	private final List<GitArtifact> artifacts;
	private final String hash;
	private final Calendar date;

	public GitCommit(String hash, Calendar date, String author, String email, String message) {
		this.hash = hash;
		this.date = date;
		this.author = author;
		this.email = email;
		this.message = message;
		this.artifacts = new ArrayList<GitArtifact>();
	}

	public void addArtifact(String name, String diff, ArtifactStatus status,
			String source) {
		artifacts.add(new GitArtifact(name, diff, source, status));
	}

	public String getAuthor() {
		return author;
	}

	public String getEmail() {
		return email;
	}

	public String getMessage() {
		return message;
	}

	public List<GitArtifact> getArtifacts() {
		return Collections.unmodifiableList(artifacts);
	}

	public String getHash() {
		return hash;
	}

	public Calendar getDate() {
		return date;
	}
	


}
