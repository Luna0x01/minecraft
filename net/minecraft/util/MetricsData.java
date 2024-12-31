package net.minecraft.util;

public class MetricsData {
	private final long[] samples = new long[240];
	private int startIndex;
	private int sampleCount;
	private int writeIndex;

	public void pushSample(long time) {
		this.samples[this.writeIndex] = time;
		this.writeIndex++;
		if (this.writeIndex == 240) {
			this.writeIndex = 0;
		}

		if (this.sampleCount < 240) {
			this.startIndex = 0;
			this.sampleCount++;
		} else {
			this.startIndex = this.wrapIndex(this.writeIndex + 1);
		}
	}

	public int getFps(long l, int i) {
		double d = (double)l / 1.6666666E7;
		return (int)(d * (double)i);
	}

	public int getStartIndex() {
		return this.startIndex;
	}

	public int getCurrentIndex() {
		return this.writeIndex;
	}

	public int wrapIndex(int index) {
		return index % 240;
	}

	public long[] getSamples() {
		return this.samples;
	}
}
