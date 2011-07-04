package br.usp.ime.xflow.cm.connectivity.git.artifact;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import br.usp.ime.xflow.cm.connectivity.git.ArtifactStatus;

public class GitCommitTests {

	@Test
	public void shouldAddAnArtifact() {
		GitCommit commit = new GitCommit("hash", Calendar.getInstance(), "John Doe", "some@email.com", "commit message");
		assertEquals(0, commit.getArtifacts().size());
		
		commit.addArtifact("file 1", "diff", ArtifactStatus.NEW, "source code");
		assertEquals(1, commit.getArtifacts().size());
		assertEquals("file 1", commit.getArtifacts().get(0).getName());
		
	}
}
