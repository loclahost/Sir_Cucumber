package git;

public class ImpactTag implements Comparable<ImpactTag> {

	private int occurence = 0;
	private String tag;

	public ImpactTag(String tag) {
		this.tag = tag;
		incrementOccurence();
	}

	public void incrementOccurence() {
		occurence++;
	}

	@Override
	public int compareTo(ImpactTag o) {
		return occurence - o.occurence;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ImpactTag)) {
			return false;
		}

		ImpactTag other = (ImpactTag) o;

		if (tag == null) {
			if (other.getTag() == null) {
				return true;
			}
			return false;
		}

		return tag.equals(((ImpactTag) o).getTag());
	}

	public String getTag() {
		return tag;
	}

	public int getOccurrence() {
		return occurence;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
