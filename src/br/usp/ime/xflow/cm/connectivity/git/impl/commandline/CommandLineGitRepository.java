package br.usp.ime.xflow.cm.connectivity.git.impl.commandline;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


import br.usp.ime.xflow.cm.connectivity.git.GitLogResult;
import br.usp.ime.xflow.cm.connectivity.git.GitRepository;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CommandLineGitRepository implements GitRepository {

	private final CommandExecutor cmd;
	private final String basePath;

	public CommandLineGitRepository(String basePath, CommandExecutor cmd) {
		this.basePath = basePath;
		this.cmd = cmd;
	}

	public String sourceOf(String hash, String fileName) {
		return cmd.execute("git show " + hash + ":" + fileName, basePath);
	}

	public List<String> allCommits() {
		String response = cmd.execute("git log --pretty=format:%H", basePath);
		return Arrays.asList(response.replace("\r", "").split("\n"));
	}

	public GitLogResult detail(String hash) {
		String response = cmd.execute("git show " + hash + " --pretty=format:<GitLogResult><hash>%H</hash><author><![CDATA[%an]]></author><email>%ae</email><date>%ai</date><message><![CDATA[%s]]></message></GitLogResult>", basePath);
		XStream xs = new XStream(new DomDriver());
		xs.alias("GitLogResult", GitLogResult.class);

		GitLogResult r = (GitLogResult) xs.fromXML(response.substring(0, response.indexOf("</GitLogResult>") + 15));
		r.setDiffs(response.substring(response.indexOf("</GitLogResult>") + 15));
		
		return r;
	}

	public List<String> allCommits(Calendar start, Calendar end) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String before = sdf.format(end.getTime());
		String after = sdf.format(start.getTime());
		
		String response = cmd.execute("git log --pretty=format:%H --before=" + before + " --after=" + after, basePath);
		return Arrays.asList(response.replace("\r", "").split("\n"));
	}
	
}
