package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NbtIntArray extends NbtElement {
	private int[] value;

	NbtIntArray() {
	}

	public NbtIntArray(int[] is) {
		this.value = is;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);

		for (int k : this.value) {
			output.writeInt(k);
		}
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(192L);
		int i = input.readInt();
		tracker.add((long)(32 * i));
		this.value = new int[i];

		for (int j = 0; j < i; j++) {
			this.value[j] = input.readInt();
		}
	}

	@Override
	public byte getType() {
		return 11;
	}

	@Override
	public String toString() {
		String string = "[";

		for (int k : this.value) {
			string = string + k + ",";
		}

		return string + "]";
	}

	public NbtIntArray copy() {
		int[] is = new int[this.value.length];
		System.arraycopy(this.value, 0, is, 0, this.value.length);
		return new NbtIntArray(is);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) ? Arrays.equals(this.value, ((NbtIntArray)object).value) : false;
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.value);
	}

	public int[] getIntArray() {
		return this.value;
	}
}
