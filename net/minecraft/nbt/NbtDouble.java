package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.util.math.MathHelper;

public class NbtDouble extends NbtElement.AbstractNbtNumber {
	private double value;

	NbtDouble() {
	}

	public NbtDouble(double d) {
		this.value = d;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeDouble(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(128L);
		this.value = input.readDouble();
	}

	@Override
	public byte getType() {
		return 6;
	}

	@Override
	public String toString() {
		return "" + this.value + "d";
	}

	@Override
	public NbtElement copy() {
		return new NbtDouble(this.value);
	}

	@Override
	public boolean equals(Object object) {
		if (super.equals(object)) {
			NbtDouble nbtDouble = (NbtDouble)object;
			return this.value == nbtDouble.value;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		long l = Double.doubleToLongBits(this.value);
		return super.hashCode() ^ (int)(l ^ l >>> 32);
	}

	@Override
	public long longValue() {
		return (long)Math.floor(this.value);
	}

	@Override
	public int intValue() {
		return MathHelper.floor(this.value);
	}

	@Override
	public short shortValue() {
		return (short)(MathHelper.floor(this.value) & 65535);
	}

	@Override
	public byte byteValue() {
		return (byte)(MathHelper.floor(this.value) & 0xFF);
	}

	@Override
	public double doubleValue() {
		return this.value;
	}

	@Override
	public float floatValue() {
		return (float)this.value;
	}
}
