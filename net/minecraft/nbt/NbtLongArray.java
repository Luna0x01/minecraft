package net.minecraft.nbt;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ArrayUtils;

public class NbtLongArray extends AbstractNbtList<NbtLong> {
	private long[] value;

	NbtLongArray() {
	}

	public NbtLongArray(long[] ls) {
		this.value = ls;
	}

	public NbtLongArray(LongSet longSet) {
		this.value = longSet.toLongArray();
	}

	public NbtLongArray(List<Long> list) {
		this(toArray(list));
	}

	private static long[] toArray(List<Long> value) {
		long[] ls = new long[value.size()];

		for (int i = 0; i < value.size(); i++) {
			Long long_ = (Long)value.get(i);
			ls[i] = long_ == null ? 0L : long_;
		}

		return ls;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.value.length);

		for (long l : this.value) {
			output.writeLong(l);
		}
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(192L);
		int i = input.readInt();
		tracker.add((long)(64 * i));
		this.value = new long[i];

		for (int j = 0; j < i; j++) {
			this.value[j] = input.readLong();
		}
	}

	@Override
	public byte getType() {
		return 12;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder("[L;");

		for (int i = 0; i < this.value.length; i++) {
			if (i != 0) {
				stringBuilder.append(',');
			}

			stringBuilder.append(this.value[i]).append('L');
		}

		return stringBuilder.append(']').toString();
	}

	public NbtLongArray copy() {
		long[] ls = new long[this.value.length];
		System.arraycopy(this.value, 0, ls, 0, this.value.length);
		return new NbtLongArray(ls);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtLongArray && Arrays.equals(this.value, ((NbtLongArray)o).value);
	}

	public int hashCode() {
		return Arrays.hashCode(this.value);
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText("L").formatted(TYPE_FORMATTING);
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

	public long[] toArray() {
		return this.value;
	}

	@Override
	public int size() {
		return this.value.length;
	}

	public NbtLong getElement(int i) {
		return new NbtLong(this.value[i]);
	}

	@Override
	public void setElement(int index, NbtElement nbt) {
		this.value[index] = ((AbstractNbtNumber)nbt).longValue();
	}

	@Override
	public void remove(int index) {
		this.value = ArrayUtils.remove(this.value, index);
	}
}
