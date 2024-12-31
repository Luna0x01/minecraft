package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class NbtString implements NbtElement {
	private String value;

	public NbtString() {
		this("");
	}

	public NbtString(String string) {
		Objects.requireNonNull(string, "Null string not allowed");
		this.value = string;
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.value);
	}

	@Override
	public void read(DataInput input, int depth, PositionTracker tracker) throws IOException {
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
		return escapeString(this.value, true);
	}

	public NbtString copy() {
		return new NbtString(this.value);
	}

	public boolean equals(Object o) {
		return this == o ? true : o instanceof NbtString && Objects.equals(this.value, ((NbtString)o).value);
	}

	public int hashCode() {
		return this.value.hashCode();
	}

	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public Text asText(String indentChar, int indentCount) {
		Text text = new LiteralText(escapeString(this.value, false)).formatted(STRING_FORMATTING);
		return new LiteralText("\"").append(text).append("\"");
	}

	public static String escapeString(String string, boolean addQuotes) {
		StringBuilder stringBuilder = new StringBuilder();
		if (addQuotes) {
			stringBuilder.append('"');
		}

		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '\\' || c == '"') {
				stringBuilder.append('\\');
			}

			stringBuilder.append(c);
		}

		if (addQuotes) {
			stringBuilder.append('"');
		}

		return stringBuilder.toString();
	}
}
