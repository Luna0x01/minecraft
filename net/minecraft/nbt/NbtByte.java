package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NbtByte extends AbstractNbtNumber {
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
		return this.value + "b";
	}

	public NbtByte copy() {
		return new NbtByte(this.value);
	}

	@Override
	public boolean equals(Object object) {
		return super.equals(object) && this.value == ((NbtByte)object).value;
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
