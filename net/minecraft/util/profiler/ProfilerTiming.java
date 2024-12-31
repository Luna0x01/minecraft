package net.minecraft.util.profiler;

public final class ProfilerTiming implements Comparable<ProfilerTiming> {
	public final double parentSectionUsagePercentage;
	public final double totalUsagePercentage;
	public final long visitCount;
	public final String name;

	public ProfilerTiming(String string, double d, double e, long l) {
		this.name = string;
		this.parentSectionUsagePercentage = d;
		this.totalUsagePercentage = e;
		this.visitCount = l;
	}

	public int compareTo(ProfilerTiming profilerTiming) {
		if (profilerTiming.parentSectionUsagePercentage < this.parentSectionUsagePercentage) {
			return -1;
		} else {
			return profilerTiming.parentSectionUsagePercentage > this.parentSectionUsagePercentage ? 1 : profilerTiming.name.compareTo(this.name);
		}
	}

	public int getColor() {
		return (this.name.hashCode() & 11184810) + 4473924;
	}
}
