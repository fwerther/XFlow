package br.usp.ime.xflow.cm.connectivity.git;

import java.util.Calendar;
import java.util.List;

public interface GitRepository {
	String sourceOf(String hash, String fileName);
	List<String> allCommits();
	List<String> allCommits(Calendar start, Calendar end);
	GitLogResult detail(String hash);
}
