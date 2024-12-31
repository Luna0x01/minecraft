package net.minecraft.util.profiler;

import java.io.File;
import java.util.List;

public interface ProfileResult {
	List<ProfilerTiming> getTimings(String string);

	boolean save(File file);

	long getStartTime();

	int getStartTick();

	long getEndTime();

	int getEndTick();

	default long getTimeSpan() {
		return this.getEndTime() - this.getStartTime();
	}

	default int getTickSpan() {
		return this.getEndTick() - this.getStartTick();
	}

	static String getHumanReadableName(String string) {
		return string.replace('\u001e', '.');
	}
}
