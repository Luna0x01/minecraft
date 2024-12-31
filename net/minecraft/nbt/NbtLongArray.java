package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NbtLongArray extends NbtElement {
	private long[] value;

	NbtLongArray() {
	}

	public NbtLongArray(long[] ls) {
		this.value = ls;
	}

	public NbtLongArray(List<Long> list) {
		this(toArray(list));
	}

	private static long[] toArray(List<Long> value) {
		long[] ls = new long[value.size()];

		for (int i = 0; i < value.size(); i++) {
			Long long_ = (Long)value.get(i);
			ls[i] = long_ == null ? 0L : long_;
		}

		return ls;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);

		for (long l : this.value) {
			output.writeLong(l);
		}
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(192L);
		int i = input.readInt();
		tracker.add((long)(64 * i));
		this.value = new long[i];

		for (int j = 0; j < i; j++) {
			this.value[j] = input.readLong();
		}
	}

	@Override
	public byte getType() {
		return 12;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[L;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]).append('L');
		}

		return stringBuilder.append(']').toString();
	}

	public NbtLongArray copy() {
		long[] ls = new long[this.value.length];
		System.arraycopy(this.value, 0, ls, 0, this.value.length);
		return new NbtLongArray(ls);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) && Arrays.equals(this.value, ((NbtLongArray)object).value);
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.value);
	}
}
