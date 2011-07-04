package br.usp.ime.xflow.cm.connectivity.git.sourcecode;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NoSourceCodeTests {

	@Test
	public void shouldAlwaysReturnEmpty() {
		NoSourceCode retriever = new NoSourceCode();
		String sourceCode = retriever.get("any-hash", "any-file");
		
		assertEquals("", sourceCode);
	}
}
