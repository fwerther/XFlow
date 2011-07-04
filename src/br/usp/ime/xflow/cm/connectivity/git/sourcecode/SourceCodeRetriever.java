package br.usp.ime.xflow.cm.connectivity.git.sourcecode;

public interface SourceCodeRetriever {

	String get(String hash, String fileName);
}
