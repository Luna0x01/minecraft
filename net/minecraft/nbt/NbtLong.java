package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtLong extends NbtElement.AbstractNbtNumber {
	private long value;

	NbtLong() {
	}

	public NbtLong(long l) {
		this.value = l;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(128L);
		this.value = input.readLong();
	}

	@Override
	public byte getType() {
		return 4;
	}

	@Override
	public String toString() {
		return "" + this.value + "L";
	}

	@Override
	public NbtElement copy() {
		return new NbtLong(this.value);
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			NbtLong nbtLong = (NbtLong)object;
			return this.value == nbtLong.value;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ (int)(this.value ^ this.value >>> 32);
	}

	@Override
	public long longValue() {
		return this.value;
	}

	@Override
	public int intValue() {
		return (int)(this.value & -1L);
	}

	@Override
	public short shortValue() {
		return (short)((int)(this.value & 65535L));
	}

	@Override
	public byte byteValue() {
		return (byte)((int)(this.value & 255L));
	}

	@Override
	public double doubleValue() {
		return (double)this.value;
	}

	@Override
	public float floatValue() {
		return (float)this.value;
	}
}
