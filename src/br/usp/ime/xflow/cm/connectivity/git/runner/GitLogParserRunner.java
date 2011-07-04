package br.usp.ime.xflow.cm.connectivity.git.runner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.usp.ime.xflow.cm.connectivity.git.GitDiff;
import br.usp.ime.xflow.cm.connectivity.git.GitLogResult;
import br.usp.ime.xflow.cm.connectivity.git.GitRepository;
import br.usp.ime.xflow.cm.connectivity.git.artifact.GitCommit;
import br.usp.ime.xflow.cm.connectivity.git.parser.GitDiffParser;
import br.usp.ime.xflow.cm.connectivity.git.sourcecode.SourceCodeRetriever;


public class GitLogParserRunner {

	private final GitRepository git;
	private final GitDiffParser diffParser;
	private final SourceCodeRetriever codeRetriever;

	public GitLogParserRunner(GitRepository git, GitDiffParser diffParser, SourceCodeRetriever codeRetriever) {
		this.git = git;
		this.diffParser = diffParser;
		this.codeRetriever = codeRetriever;
	}
	
	public List<GitCommit> parse(Calendar start, Calendar end) {
		List<GitCommit> commits = new ArrayList<GitCommit>();
		
		for(String hash : git.allCommits(start, end)) {
			GitLogResult details = git.detail(hash);
			GitCommit commit = new GitCommit(hash, convert(details.getDate()), details.getAuthor(), details.getEmail(), details.getMessage());
			
			for(GitDiff diff : diffParser.parse(details.getDiffs())) {
				commit.addArtifact(diff.getName(), diff.getContent(), diff.getStatus(), codeRetriever.get(details.getHash(), diff.getName()));
			}
			
			commits.add(commit);
		}

		return commits;
	}

	private Calendar convert(String date) {
		try {
			Date parsedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(date);
			Calendar convertedDate = Calendar.getInstance();
			convertedDate.setTime(parsedDate);
		return convertedDate;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
