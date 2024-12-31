package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtShort extends AbstractNbtNumber {
	private short value;

	public NbtShort() {
	}

	public NbtShort(short s) {
		this.value = s;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeShort(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(80L);
		this.value = input.readShort();
	}

	@Override
	public byte getType() {
		return 2;
	}

	@Override
	public String toString() {
		return this.value + "s";
	}

	public NbtShort copy() {
		return new NbtShort(this.value);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) && this.value == ((NbtShort)object).value;
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
		return this.value;
	}

	@Override
	public byte byteValue() {
		return (byte)(this.value & 255);
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
