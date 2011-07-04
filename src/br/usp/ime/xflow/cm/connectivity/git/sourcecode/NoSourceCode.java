package br.usp.ime.xflow.cm.connectivity.git.sourcecode;

public class NoSourceCode implements SourceCodeRetriever{

	public String get(String hash, String fileName) {
		return "";
	}

}
