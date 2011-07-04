package br.usp.ime.xflow.cm.connectivity.git.parser;

import java.util.List;

import br.usp.ime.xflow.cm.connectivity.git.GitDiff;


public interface GitDiffParser {
	List<GitDiff> parse(String diff);
}
