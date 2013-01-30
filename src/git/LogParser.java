package git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

	private String gitDir = "";
	private String baseBranch;

	protected LogParser() {

	}

	public LogParser(String base) {
		this.baseBranch = base;
	}

	public LogParser(String base, String gitDir) {
		this.baseBranch = base;
		this.gitDir = gitDir;
	}

	public List<ImpactTag> getImpactTags() {

		List<String> commitIds = getTasks();

		List<String> commitMsgs = new ArrayList<>();
		for (String currentId : commitIds) {
			commitMsgs.add(getLogFor(currentId));
		}

		return getImpactTags(commitMsgs);

	}

	public List<String> getTasks() {

		List<String> commitIds = new ArrayList<>();

		String output = executeGitCommand("cherry " + baseBranch);

		for (String line : output.split("\\+ ")) {
			if (!line.isEmpty()) {
				commitIds.add(line);
			}
		}

		return commitIds;

	}

	public String getLogFor(String commitId) {
		return executeGitCommand("log -n1 " + commitId);
	}

	private String executeGitCommand(String cmd) {

		StringBuilder gitCmd = new StringBuilder();
		gitCmd.append("/usr/local/git/bin//git ");

		if (!gitDir.isEmpty()) {
			gitCmd.append("--git-dir=");
			gitCmd.append(gitDir);
			gitCmd.append("/.git");
			gitCmd.append(" ");
			gitCmd.append("--work-tree=");
			gitCmd.append(gitDir);
			gitCmd.append(" ");
		}
		gitCmd.append(cmd);

		return executeCommand(gitCmd.toString());
	}

	public List<ImpactTag> getImpactTags(List<String> commitMsgs) {

		List<ImpactTag> returnTags = new ArrayList<>();

		for (String msg : commitMsgs) {
			List<ImpactTag> commitTags = getImpactTags(msg);

			for (ImpactTag tag : commitTags) {
				if (returnTags.contains(tag)) {
					ImpactTag currentTag = returnTags.get(returnTags.indexOf(tag));
					currentTag.incrementOccurence();
				} else {
					returnTags.add(tag);
				}
			}

		}
		Collections.sort(returnTags);
		Collections.reverse(returnTags);
		return returnTags;
	}

	public List<ImpactTag> getImpactTags(String commitMsg) {
		List<ImpactTag> tags = new ArrayList<>();

		Pattern p = Pattern.compile("Impact:\\s+.+");
		Matcher m = p.matcher(commitMsg);
		if (m.find()) {
			String row = m.group();

			StringTokenizer st = new StringTokenizer(row.replace("Impact:", ""), ",");
			while (st.hasMoreTokens()) {
				String tag = st.nextToken().trim();
				if (!tags.contains(tag)) {
					ImpactTag impactTag = new ImpactTag(tag);
					tags.add(impactTag);
				}
			}
		}

		return tags;
	}

	protected String executeCommand(String cmd) {

		StringBuilder cmdOutput = new StringBuilder();

		Process p;
		try {
			String[] commandArgs = cmd.split(" ");
			p = Runtime.getRuntime().exec(commandArgs);

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = stdError.readLine()) != null) {
				cmdOutput.append(s);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return cmdOutput.toString();

	}

}
