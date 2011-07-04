package br.usp.ime.xflow.cm.connectivity.git;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.NotSupportedException;

import br.ufpa.linc.xflow.cm.connectivity.Access;
import br.ufpa.linc.xflow.cm.info.Artifact;
import br.ufpa.linc.xflow.cm.info.Commit;
import br.ufpa.linc.xflow.exception.cm.CMException;
import br.usp.ime.xflow.cm.connectivity.git.artifact.GitArtifact;
import br.usp.ime.xflow.cm.connectivity.git.artifact.GitCommit;
import br.usp.ime.xflow.cm.connectivity.git.impl.commandline.CommandLineGitRepository;
import br.usp.ime.xflow.cm.connectivity.git.impl.commandline.SimpleCommandExecutor;
import br.usp.ime.xflow.cm.connectivity.git.parser.DefaultGitDiffParser;
import br.usp.ime.xflow.cm.connectivity.git.runner.GitLogParserRunner;
import br.usp.ime.xflow.cm.connectivity.git.sourcecode.FilterSourceCodeByFileType;
import br.usp.ime.xflow.cm.connectivity.git.sourcecode.NoSourceCode;

public class GitAccess extends Access {

	private final String gitPath;
	private final String repositoryPath;

	public GitAccess(String gitPath, String repositoryPath) {
		this.gitPath = gitPath;
		this.repositoryPath = repositoryPath;
	}
	
	@Override
	public ArrayList<Commit> collectData(long startRevision, long endRevision,
			boolean downloadCode) throws CMException {
		throw new RuntimeException(new NotSupportedException("Git does not use numbers as revision IDs"));
	}

	@Override
	public ArrayList<Commit> collectData(Date startDate, Date endDate,
			boolean downloadCode) throws CMException {
		System.out.println("Starting git mechanism...");
		CommandLineGitRepository git = new CommandLineGitRepository(repositoryPath, new SimpleCommandExecutor(gitPath));

		GitLogParserRunner runner = new GitLogParserRunner(git,new DefaultGitDiffParser(), (downloadCode? new FilterSourceCodeByFileType(git, new String[]{getFilter().getExtension()})  : new NoSourceCode()));

		List<GitCommit> commits = runner.parse(toCalendar(startDate), toCalendar(endDate));
		System.out.println(commits.size() + "commits gathered");
		return convert(commits);

	}

	private ArrayList<Commit> convert(List<GitCommit> commits) {
		ArrayList<Commit> finalList = new ArrayList<Commit>();
		
		for (GitCommit commit : commits) {
			Commit convertedCommit = new Commit();
			System.out.println(commit.getHash());
			// FIXME: deve setar o hash do commit ==> convertedCommit.setRevisionNbr(commit.getHash());
			convertedCommit.setAuthorName(commit.getAuthor());
			convertedCommit.setDate(commit.getDate().getTime());
			convertedCommit.setLogMessage(commit.getMessage());
			convertedCommit.setArtifacts(convertArtifacts(commit.getArtifacts()));
			
			finalList.add(convertedCommit);
		}
		
		return finalList;
	}


	private List<Artifact> convertArtifacts(List<GitArtifact> artifacts) {
		List<Artifact> finalList = new ArrayList<Artifact>();
		
		for(GitArtifact artifact : artifacts) {
			Artifact convertedArtifact = new Artifact();
			
			if(artifact.getStatus() == ArtifactStatus.DELETED) {
				convertedArtifact.setSourceCode(null);
				convertedArtifact.setChangeType('D');
			}
			else if(artifact.getStatus() == ArtifactStatus.NEW) {
				convertedArtifact.setChangeType('A');
				convertedArtifact.setSourceCode(artifact.getSourceCode());
			}
			else {
				convertedArtifact.setChangeType('M');
				convertedArtifact.setSourceCode(artifact.getDiff());
			}
			
			convertedArtifact.setArtifactKind("FILE");
			convertedArtifact.setTargetPath("/" + artifact.getName());
			
			finalList.add(convertedArtifact);
		}
		
		return finalList;
	}

	private Calendar toCalendar(Date date) {
		Calendar converted = Calendar.getInstance();
		converted.setTime(date);
		return converted;
	}

}
