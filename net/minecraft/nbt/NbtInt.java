package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NbtInt extends AbstractNbtNumber {
	private int value;

	NbtInt() {
	}

	public NbtInt(int i) {
		this.value = i;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(96L);
		this.value = input.readInt();
	}

	@Override
	public byte getType() {
		return 3;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	public NbtInt copy() {
		return new NbtInt(this.value);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtInt && this.value == ((NbtInt)o).value;
	}

	public int hashCode() {
		return this.value;
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		return new LiteralText(String.valueOf(this.value)).formatted(VALUE_FORMATTING);
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

	@Override
	public Number numberValue() {
		return this.value;
	}
}
