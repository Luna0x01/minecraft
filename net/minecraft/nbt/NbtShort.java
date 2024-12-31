package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NbtShort extends AbstractNbtNumber {
	private short value;

	public NbtShort() {
	}

	public NbtShort(short s) {
		this.value = s;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeShort(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
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

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtShort && this.value == ((NbtShort)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("s").formatted(TYPE_FORMATTING);
		return new LiteralText(String.valueOf(this.value)).append(text).formatted(VALUE_FORMATTING);
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

	@Override
	public Number numberValue() {
		return this.value;
	}
}
