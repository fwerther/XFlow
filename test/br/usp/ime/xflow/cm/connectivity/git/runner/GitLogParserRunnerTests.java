package br.usp.ime.xflow.cm.connectivity.git.runner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.usp.ime.xflow.cm.connectivity.git.ArtifactStatus;
import br.usp.ime.xflow.cm.connectivity.git.GitDiff;
import br.usp.ime.xflow.cm.connectivity.git.GitLogResult;
import br.usp.ime.xflow.cm.connectivity.git.GitRepository;
import br.usp.ime.xflow.cm.connectivity.git.artifact.GitCommit;
import br.usp.ime.xflow.cm.connectivity.git.parser.GitDiffParser;
import br.usp.ime.xflow.cm.connectivity.git.sourcecode.SourceCodeRetriever;

public class GitLogParserRunnerTests {

	private GitRepository git;
	private GitDiffParser diffParser;
	private SourceCodeRetriever codeRetriever;

	@Before
	public void setUp() {
		git = mock(GitRepository.class);
		diffParser = mock(GitDiffParser.class);
		codeRetriever = mock(SourceCodeRetriever.class);
	}
	
	@Test
	public void shouldGetCommitInfo() throws ParseException {
		GitLogParserRunner runner = new GitLogParserRunner(git, diffParser, codeRetriever);
		
		Calendar start = Calendar.getInstance(); 
		Calendar end = Calendar.getInstance(); 

		when(git.allCommits(start, end)).thenReturn(hashes("hash1", "hash2"));
		when(git.detail("hash1")).thenReturn(new GitLogResult("hash 1", "2010-01-01 10:00:00 -0300", "author 1", "email 1", "message 1", "diff 1;diff 2"));
		when(git.detail("hash2")).thenReturn(new GitLogResult("hash 2", "2010-01-02 10:00:00 -0300", "author 2", "email 2", "message 2", "diff 3;diff 4"));
		
		List<GitCommit> commits = runner.parse(start, end);
		
		GitCommit firstCommit = commits.get(0);
		assertEquals("author 1", firstCommit.getAuthor());
		assertEquals("email 1", firstCommit.getEmail());
		assertEquals("message 1", firstCommit.getMessage());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse("2010-01-01 10:00:00 -0300"),  firstCommit.getDate().getTime());
		
		GitCommit secondCommit = commits.get(1);
		assertEquals("author 2", secondCommit.getAuthor());
		assertEquals("email 2", secondCommit.getEmail());
		assertEquals("message 2", secondCommit.getMessage());
		assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse("2010-01-02 10:00:00 -0300"),  secondCommit.getDate().getTime());
	}

	@Test
	public void shouldGetArtifacts() {
		GitLogParserRunner runner = new GitLogParserRunner(git, diffParser, codeRetriever);
		
		Calendar start = Calendar.getInstance(); 
		Calendar end = Calendar.getInstance(); 

		when(git.allCommits(start, end)).thenReturn(hashes("hash1", "hash2"));
		when(git.detail("hash1")).thenReturn(new GitLogResult("hash 1", "2010-01-01 10:00:00 -0300", "author 1", "email 1", "message 1", "diff 1;diff 2"));
		when(git.detail("hash2")).thenReturn(new GitLogResult("hash 2", "2010-01-02 10:00:00 -0300", "author 2", "email 2", "message 2", "diff 3;diff 4"));
		
		when(diffParser.parse("diff 1;diff 2")).thenReturn(diffs("diff 1", "diff 2"));
		when(diffParser.parse("diff 3;diff 4")).thenReturn(diffs("diff 3", "diff 4"));
		
		when(codeRetriever.get("hash 1", "diff 1 name")).thenReturn("sc 1");
		when(codeRetriever.get("hash 1", "diff 2 name")).thenReturn("sc 2");
		when(codeRetriever.get("hash 2", "diff 3 name")).thenReturn("sc 3");
		when(codeRetriever.get("hash 2", "diff 4 name")).thenReturn("sc 4");
		
		List<GitCommit> commits = runner.parse(start, end);
		
		GitCommit firstCommit = commits.get(0);
		assertEquals("diff 1 name", firstCommit.getArtifacts().get(0).getName());
		assertEquals("diff 1", firstCommit.getArtifacts().get(0).getDiff());
		assertEquals("sc 1", firstCommit.getArtifacts().get(0).getSourceCode());
		assertEquals("diff 2 name", firstCommit.getArtifacts().get(1).getName());
		assertEquals("diff 2", firstCommit.getArtifacts().get(1).getDiff());
		assertEquals("sc 2", firstCommit.getArtifacts().get(1).getSourceCode());	
		
		GitCommit secondCommit = commits.get(1);
		assertEquals("diff 3 name", secondCommit.getArtifacts().get(0).getName());
		assertEquals("diff 3", secondCommit.getArtifacts().get(0).getDiff());
		assertEquals("sc 3", secondCommit.getArtifacts().get(0).getSourceCode());
		assertEquals("diff 4 name", secondCommit.getArtifacts().get(1).getName());
		assertEquals("diff 4", secondCommit.getArtifacts().get(1).getDiff());
		assertEquals("sc 4", secondCommit.getArtifacts().get(1).getSourceCode());
	}
	
	private List<GitDiff> diffs(String... diffs) {
		List<GitDiff> list = new ArrayList<GitDiff>();
		
		for(String diff : diffs) {
			list.add(new GitDiff(diff + " name", diff, ArtifactStatus.NEW));
		}
		return list;
	}

	private List<String> hashes(String... hashes) {
		return Arrays.asList(hashes);
	}
}
