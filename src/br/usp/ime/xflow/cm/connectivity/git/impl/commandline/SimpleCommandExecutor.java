package br.usp.ime.xflow.cm.connectivity.git.impl.commandline;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class SimpleCommandExecutor implements CommandExecutor {

	private final String gitPath;

	public SimpleCommandExecutor(String gitPath) {
		this.gitPath = gitPath;
	}

	public String execute(String command, String basePath) {
		StringBuffer total = new StringBuffer();
		String finalCommand = gitPath + command;
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(finalCommand, null, new File(basePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Scanner sc = new Scanner(proc.getInputStream());

		while (sc.hasNextLine()) {
			total.append(sc.nextLine() + "\r\n");
		}
		return total.toString();

	}

}
