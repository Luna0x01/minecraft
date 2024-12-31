package net.minecraft.util;

public interface ProgressListener {
	void setTitle(String title);

	void setTitleAndTask(String title);

	void setTask(String task);

	void setProgressPercentage(int percentage);

	void setDone();
}
