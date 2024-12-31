package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class NbtString extends NbtElement {
	private String value;

	public NbtString() {
		this("");
	}

	public NbtString(String string) {
		Objects.requireNonNull(string, "Null string not allowed");
		this.value = string;
	}

	@Override
	void write(DataOutput output) throws IOException {
		output.writeUTF(this.value);
	}

	@Override
	void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
		tracker.add(288L);
		this.value = input.readUTF();
		tracker.add((long)(16 * this.value.length()));
	}

	@Override
	public byte getType() {
		return 8;
	}

	@Override
	public String toString() {
		return quote(this.value);
	}

	public NbtString copy() {
		return new NbtString(this.value);
	}

	@Override
	public boolean isEmpty() {
		return this.value.isEmpty();
	}

	@Override
	public boolean equals(Object object) {
		if (!super.equals(object)) {
			return false;
		} else {
			NbtString nbtString = (NbtString)object;
			return this.value == null && nbtString.value == null || Objects.equals(this.value, nbtString.value);
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode() ^ this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}

	public static String quote(String string) {
		StringBuilder stringBuilder = new StringBuilder("\"");

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\\' || c == '"') {
				stringBuilder.append('\\');
			}

			stringBuilder.append(c);
		}

		return stringBuilder.append('"').toString();
	}
}
