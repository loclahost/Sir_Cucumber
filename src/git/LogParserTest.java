package git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class LogParserTest {

	private static Map<String, String> commits = new HashMap<>();

	@BeforeClass
	public static void initializeCommits() {
		StringBuilder commit = new StringBuilder();

		commit.append("commit c422f74e82a4a9992aa6d7b52956a71e99f383da\n");
		commit.append("Author: Person Commiter <commiter@developer.orc>\n");
		commit.append("Date:   Wed May 30 15:47:04 2012 +0200\n");
		commit.append("\n");
		commit.append("    Exclude stuff in related things\n");
		commit.append("    \n");
		commit.append("    Task:  12365\n");
		commit.append("    Description: Connected things were displayed\n");
		commit.append("                 in subsequent searches. Exclude these to avoid\n");
		commit.append("                 confusion.\n");
		commit.append("    \n");
		commit.append("                 Fixed a missing translation in the connected\n");
		commit.append("                 editor.\n");
		commit.append("    \n");
		commit.append("                 Removed an unused method.\n");
		commit.append("    \n");
		commit.append("    Test:        Stepped the patch execution and verified the result.\n");
		commit.append("    \n");
		commit.append("    Impact:      mail, checkout, admin editor\n");
		commit.append("    \n");
		commit.append("    Change-Id: Ibc8d50cadbfa57b2c3490dae45ade4361b70a38f\n");
		commit.append("    \n");

		commits.put("c422f74e82a4a9992aa6d7b52956a71e99f383da", commit.toString());

		commit.setLength(0);

		commit.append("commit 57c9ca260b481555249b7932cede2b3f90ac01a0\n");
		commit.append("Author: Fredrik Mårtensson <fredrik.martensson@developer.orc>\n");
		commit.append("Date:   Fri Aug 3 15:18:08 2012 +0200\n");
		commit.append("\n");
		commit.append("    Added optional automatic mailing on import error\n");
		commit.append("    \n");
		commit.append("    Task:        67892\n");
		commit.append("    Description: Added the option of enabling automatic mail\n");
		commit.append("                 sending when we fail.\n");
		commit.append("    \n");
		commit.append("    Test:        Tested importing an non correct import and\n");
		commit.append("                 verified that the mail was created correctly,\n");
		commit.append("    \n");
		commit.append("    Test:        Tested editing and saving the new settings and\n");
		commit.append("                 verified that the values was saved correctly.\n");
		commit.append("    \n");
		commit.append("    Impact:      administration, checkout, error handling\n");
		commit.append("    \n");
		commit.append("    Change-Id: Ib0dfc89c9332f27467a465664208d77be25018e2\n");
		commits.put("57c9ca260b481555249b7932cede2b3f90ac01a0", commit.toString());

	}

	@Test
	public void testGetTasks() {

		List<String> expected = new ArrayList<>();

		expected.add("c422f74e82a4a9992aa6d7b52956a71e99f383da");
		expected.add("b97bdc91a9c9bfe9456dc60dd79fa40706d0edb0");
		expected.add("0700fa3cc1e572a7aeede12631a83163a26e7f5f");
		expected.add("57c9ca260b481555249b7932cede2b3f90ac01a0");
		expected.add("f8d0dd4f74776bb79cc9d18797d385545b0333bc");
		expected.add("0413e27287460e0f27f7040510e278d65f85e5be");
		expected.add("7cdf1b7cb0de5aea3d8c64509c371b8e49d9dc94");
		expected.add("f330a86b97aa0452d46c179828c01aec29955c75");
		expected.add("af1d53cf9cb693e93a11089142be6523178c6dd1");
		expected.add("7492911cad4597dff2a849d680db8b7d1e37d7b9");
		expected.add("6c012927d7f2f02a9968e2d3783c5acdf085e8f4");
		expected.add("49b5fa22c24997361d835e1e2b3277f3b3d80295");
		expected.add("baafbffa0bc93f5b0f85ca3a20dcf3ca0cb480ae");
		expected.add("5edf7d1fe5997fcea17e7a10ba8b82077cb76df4");
		expected.add("bdb3ed3f1a4bc729c3b0a4104b1da1b706eebf7f");
		expected.add("d37921718182dc8d3d078ebbfc18e288114a5ff6");

		StringBuilder commitData = new StringBuilder();
		for (String id : expected) {
			commitData.append("+ ");
			commitData.append(id);
		}

		LogParser parser = new LogParserMock(commitData.toString());

		List<String> actual = parser.getTasks();

		for (int i = 0; i < expected.size(); i++) {
			assertEquals(expected.get(i), actual.get(i));
		}

	}

	@Test
	public void testGetLogFor() {

		for (String commitId : commits.keySet()) {
			String commitMsg = commits.get(commitId);

			LogParser parser = new LogParserMock(commitMsg);
			String msg = parser.getLogFor(commitId);
			assertEquals(commitMsg, msg);
		}

	}

	@Test
	public void testGetImpactTagsList() {
		List<ImpactTag> expected = new ArrayList<>();

		ImpactTag occursTwice = new ImpactTag("checkout");

		expected.add(occursTwice);
		expected.add(new ImpactTag("mail"));
		expected.add(new ImpactTag("admin editor"));
		expected.add(new ImpactTag("administration"));
		expected.add(new ImpactTag("error handling"));

		List<String> commitMsgs = new ArrayList<>(commits.values());
		LogParser parser = new LogParserMock(null);
		List<ImpactTag> actual = parser.getImpactTags(commitMsgs);

		for (ImpactTag tag : actual) {
			System.out.println(tag.getTag());
		}

		assertTrue(actual.get(0).equals(occursTwice));

		for (ImpactTag expectedTag : expected) {
			assertTrue(actual.contains(expectedTag));
		}
	}

	@Test
	public void testGetImpactTagsSingeCommit() {
		List<ImpactTag> expected = new ArrayList<>();
		expected.add(new ImpactTag("mail"));
		expected.add(new ImpactTag("checkout"));
		expected.add(new ImpactTag("admin editor"));

		expected.add(new ImpactTag("administration"));
		expected.add(new ImpactTag("error handling"));

		List<ImpactTag> actual = new ArrayList<>();
		for (String commitId : commits.keySet()) {
			String commitMsg = commits.get(commitId);

			LogParser parser = new LogParserMock(commitMsg);
			actual.addAll(parser.getImpactTags(commitMsg));
		}

		for (ImpactTag expectedTag : expected) {
			assertTrue(actual.contains(expectedTag));
		}

	}

	private class LogParserMock extends LogParser {

		private String cmdReturnData;

		public LogParserMock(String cmdReturnData) {
			this.cmdReturnData = cmdReturnData;
		}

		@Override
		protected String executeCommand(String cmd) {
			return cmdReturnData;
		}
	}

}
