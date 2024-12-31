package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NbtByteArray extends NbtElement {
	private byte[] value;

	NbtByteArray() {
	}

	public NbtByteArray(byte[] bs) {
		this.value = bs;
	}

	public NbtByteArray(List<Byte> list) {
		this(toArray(list));
	}

	private static byte[] toArray(List<Byte> bytes) {
		byte[] bs = new byte[bytes.size()];

		for (int i = 0; i < bytes.size(); i++) {
			Byte byte_ = (Byte)bytes.get(i);
			bs[i] = byte_ == null ? 0 : byte_;
		}

		return bs;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);
		output.write(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(192L);
		int i = input.readInt();
		tracker.add((long)(8 * i));
		this.value = new byte[i];
		input.readFully(this.value);
	}

	@Override
	public byte getType() {
		return 7;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[B;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]).append('B');
		}

		return stringBuilder.append(']').toString();
	}

	@Override
	public NbtElement copy() {
		byte[] bs = new byte[this.value.length];
		System.arraycopy(this.value, 0, bs, 0, this.value.length);
		return new NbtByteArray(bs);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) && Arrays.equals(this.value, ((NbtByteArray)object).value);
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.value);
	}

	public byte[] getArray() {
		return this.value;
	}
}
