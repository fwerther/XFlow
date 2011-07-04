package br.usp.ime.xflow.cm.connectivity.git.sourcecode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.usp.ime.xflow.cm.connectivity.git.GitRepository;

public class FilterSourceCodeByFileType implements SourceCodeRetriever {

	private final String[] extensions;
	private final GitRepository git;

	public FilterSourceCodeByFileType(GitRepository git, String[] extensions) {
		this.git = git;
		this.extensions = extensions;
	}
	
	public String get(String hash, String fileName) {
		for (String extension : extensions) {
			
			final Pattern pattern = Pattern.compile(""+extension);
			final Matcher matcher = pattern.matcher(fileName);
			
			if(matcher.matches()) {
				return git.sourceOf(hash, fileName); 
			}
		}
		return "";
	}

}
