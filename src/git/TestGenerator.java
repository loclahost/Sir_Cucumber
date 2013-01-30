package git;

import java.util.List;

public class TestGenerator {

	public static void main(String[] args) {

		String gitDir = "";
		if (args.length == 0) {
			System.out.println("Must specify baseline branch");
			System.exit(1);
		}

		String baseline = args[0];

		if (args.length > 1) {
			gitDir = args[1];
		}

		LogParser parser = new LogParser(baseline, gitDir);

		List<ImpactTag> logTags = parser.getImpactTags();

		for (ImpactTag tag : logTags) {
			System.out.println(tag.getOccurrence() + " times: " + tag.getTag());
		}
	}
}
