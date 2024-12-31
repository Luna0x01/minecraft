package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtByte extends NbtElement.AbstractNbtNumber {
	private byte value;

	NbtByte() {
	}

	public NbtByte(byte b) {
		this.value = b;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeByte(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(72L);
		this.value = input.readByte();
	}

	@Override
	public byte getType() {
		return 1;
	}

	@Override
	public String toString() {
		return "" + this.value + "b";
	}

	@Override
	public NbtElement copy() {
		return new NbtByte(this.value);
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			NbtByte nbtByte = (NbtByte)object;
			return this.value == nbtByte.value;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.value;
	}

	@Override
	public long longValue() {
		return (long)this.value;
	}

	@Override
	public int intValue() {
		return this.value;
	}

	@Override
	public short shortValue() {
		return (short)this.value;
	}

	@Override
	public byte byteValue() {
		return this.value;
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
