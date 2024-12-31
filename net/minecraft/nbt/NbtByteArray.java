package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class NbtByteArray extends AbstractNbtList<NbtByte> {
	private byte[] value;

	NbtByteArray() {
	}

	public NbtByteArray(byte[] bs) {
		this.value = bs;
	}

	public NbtByteArray(List<Byte> list) {
		this(toArray(list));
	}

	private static byte[] toArray(List<Byte> bytes) {
		byte[] bs = new byte[bytes.size()];

		for (int i = 0; i < bytes.size(); i++) {
			Byte byte_ = (Byte)bytes.get(i);
			bs[i] = byte_ == null ? 0 : byte_;
		}

		return bs;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);
		output.write(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(192L);
		int i = input.readInt();
		tracker.add((long)(8 * i));
		this.value = new byte[i];
		input.readFully(this.value);
	}

	@Override
	public byte getType() {
		return 7;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[B;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]).append('B');
		}

		return stringBuilder.append(']').toString();
	}

	@Override
	public NbtElement copy() {
		byte[] bs = new byte[this.value.length];
		System.arraycopy(this.value, 0, bs, 0, this.value.length);
		return new NbtByteArray(bs);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtByteArray && Arrays.equals(this.value, ((NbtByteArray)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("B").formatted(TYPE_FORMATTING);
		Text text2 = new LiteralText("[").append(text).append(";");

		for (int i = 0; i < this.value.length; i++) {
			Text text3 = new LiteralText(String.valueOf(this.value[i])).formatted(VALUE_FORMATTING);
			text2.append(" ").append(text3).append(text);
			if (i != this.value.length - 1) {
				text2.append(",");
			}
		}

		text2.append("]");
		return text2;
	}

	public byte[] getArray() {
		return this.value;
	}

	@Override
	public int size() {
		return this.value.length;
	}

	public NbtByte getElement(int i) {
		return new NbtByte(this.value[i]);
	}

	@Override
	public void setElement(int index, NbtElement nbt) {
		this.value[index] = ((AbstractNbtNumber)nbt).byteValue();
	}

	@Override
	public void remove(int index) {
		this.value = ArrayUtils.remove(this.value, index);
	}
}
