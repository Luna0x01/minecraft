package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class NbtIntArray extends AbstractNbtList<NbtInt> {
	private int[] value;

	NbtIntArray() {
	}

	public NbtIntArray(int[] is) {
		this.value = is;
	}

	public NbtIntArray(List<Integer> list) {
		this(toArray(list));
	}

	private static int[] toArray(List<Integer> value) {
		int[] is = new int[value.size()];

		for (int i = 0; i < value.size(); i++) {
			Integer integer = (Integer)value.get(i);
			is[i] = integer == null ? 0 : integer;
		}

		return is;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);

		for (int i : this.value) {
			output.writeInt(i);
		}
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(192L);
		int i = input.readInt();
		tracker.add((long)(32 * i));
		this.value = new int[i];

		for (int j = 0; j < i; j++) {
			this.value[j] = input.readInt();
		}
	}

	@Override
	public byte getType() {
		return 11;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[I;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]);
		}

		return stringBuilder.append(']').toString();
	}

	public NbtIntArray copy() {
		int[] is = new int[this.value.length];
		System.arraycopy(this.value, 0, is, 0, this.value.length);
		return new NbtIntArray(is);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtIntArray && Arrays.equals(this.value, ((NbtIntArray)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	public int[] getIntArray() {
		return this.value;
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("I").formatted(TYPE_FORMATTING);
		Text text2 = new LiteralText("[").append(text).append(";");

		for (int i = 0; i < this.value.length; i++) {
			text2.append(" ").append(new LiteralText(String.valueOf(this.value[i])).formatted(VALUE_FORMATTING));
			if (i != this.value.length - 1) {
				text2.append(",");
			}
		}

		text2.append("]");
		return text2;
	}

	@Override
	public int size() {
		return this.value.length;
	}

	public NbtInt getElement(int i) {
		return new NbtInt(this.value[i]);
	}

	@Override
	public void setElement(int index, NbtElement nbt) {
		this.value[index] = ((AbstractNbtNumber)nbt).intValue();
	}

	@Override
	public void remove(int index) {
		this.value = ArrayUtils.remove(this.value, index);
	}
}
