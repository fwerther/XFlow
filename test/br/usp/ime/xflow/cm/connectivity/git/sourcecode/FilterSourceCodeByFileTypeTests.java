package br.usp.ime.xflow.cm.connectivity.git.sourcecode;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.usp.ime.xflow.cm.connectivity.git.GitRepository;

public class FilterSourceCodeByFileTypeTests {

	private GitRepository git;

	@Before
	public void setUp() {
		git = mock(GitRepository.class);
	}
	
	@Test
	public void shouldGetSourceCodeIfFilenameMatchesThePattern() {
		FilterSourceCodeByFileType filter = new FilterSourceCodeByFileType(git, new String[] {".*?\\.java"});
		when(git.sourceOf("123abc", "SomeClass.java")).thenReturn("public class SomeClass { ... }");
		
		String sourceCode = filter.get("123abc", "SomeClass.java");
		assertEquals("public class SomeClass { ... }", sourceCode);
	}
	
	@Test
	public void shouldReturnEmptyIfFilenameDoesNotMatchThePattern() {
		FilterSourceCodeByFileType filter = new FilterSourceCodeByFileType(git, new String[] {".*?\\.java"});
		
		String sourceCode = filter.get("123abc", "file.css");
		
		assertEquals("", sourceCode);
		verify(git, times(0)).sourceOf("123abc", "file.css");
	}
}
