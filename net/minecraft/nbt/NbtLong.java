package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NbtLong extends AbstractNbtNumber {
	private long value;

	NbtLong() {
	}

	public NbtLong(long l) {
		this.value = l;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(128L);
		this.value = input.readLong();
	}

	@Override
	public byte getType() {
		return 4;
	}

	@Override
	public String toString() {
		return this.value + "L";
	}

	public NbtLong copy() {
		return new NbtLong(this.value);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtLong && this.value == ((NbtLong)o).value;
	}

	public int hashCode() {
		return (int)(this.value ^ this.value >>> 32);
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("L").formatted(TYPE_FORMATTING);
		return new LiteralText(String.valueOf(this.value)).append(text).formatted(VALUE_FORMATTING);
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

	@Override
	public Number numberValue() {
		return this.value;
	}
}
