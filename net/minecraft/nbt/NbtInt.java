package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtInt extends AbstractNbtNumber {
	private int value;

	NbtInt() {
	}

	public NbtInt(int i) {
		this.value = i;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeInt(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(96L);
		this.value = input.readInt();
	}

	@Override
	public byte getType() {
		return 3;
	}

	@Override
	public String toString() {
		return "" + this.value;
	}

	public NbtInt copy() {
		return new NbtInt(this.value);
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			NbtInt nbtInt = (NbtInt)object;
			return this.value == nbtInt.value;
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
		return (short)(this.value & 65535);
	}

	@Override
	public byte byteValue() {
		return (byte)(this.value & 0xFF);
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
