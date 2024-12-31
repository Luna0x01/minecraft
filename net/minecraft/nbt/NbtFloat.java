package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class NbtFloat extends AbstractNbtNumber {
	private float value;

	NbtFloat() {
	}

	public NbtFloat(float f) {
		this.value = f;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeFloat(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(96L);
		this.value = input.readFloat();
	}

	@Override
	public byte getType() {
		return 5;
	}

	@Override
	public String toString() {
		return this.value + "f";
	}

	public NbtFloat copy() {
		return new NbtFloat(this.value);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtFloat && this.value == ((NbtFloat)o).value;
	}

	public int hashCode() {
		return Float.floatToIntBits(this.value);
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("f").formatted(TYPE_FORMATTING);
		return new LiteralText(String.valueOf(this.value)).append(text).formatted(VALUE_FORMATTING);
	}

	@Override
	public long longValue() {
		return (long)this.value;
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
		return (double)this.value;
	}

	@Override
	public float floatValue() {
		return this.value;
	}

	@Override
	public Number numberValue() {
		return this.value;
	}
}
