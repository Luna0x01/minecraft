package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NbtByte extends AbstractNbtNumber {
	private byte value;

	NbtByte() {
	}

	public NbtByte(byte b) {
		this.value = b;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeByte(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
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

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtByte && this.value == ((NbtByte)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("b").formatted(TYPE_FORMATTING);
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

	@Override
	public Number numberValue() {
		return this.value;
	}
}
