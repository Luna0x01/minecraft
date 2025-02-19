package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.nbt.visitor.NbtElementVisitor;

public class NbtString implements NbtElement {
	private static final int field_33241 = 288;
	public static final NbtType<NbtString> TYPE = new NbtType<NbtString>() {
		public NbtString read(DataInput dataInput, int i, NbtTagSizeTracker nbtTagSizeTracker) throws IOException {
			nbtTagSizeTracker.add(288L);
			String string = dataInput.readUTF();
			nbtTagSizeTracker.add((long)(16 * string.length()));
			return NbtString.of(string);
		}

		@Override
		public String getCrashReportName() {
			return "STRING";
		}

		@Override
		public String getCommandFeedbackName() {
			return "TAG_String";
		}

		@Override
		public boolean isImmutable() {
			return true;
		}
	};
	private static final NbtString EMPTY = new NbtString("");
	private static final char field_33242 = '"';
	private static final char field_33243 = '\'';
	private static final char field_33244 = '\\';
	private static final char field_33245 = '\u0000';
	private final String value;

	private NbtString(String value) {
		Objects.requireNonNull(value, "Null string not allowed");
		this.value = value;
	}

	public static NbtString of(String value) {
		return value.isEmpty() ? EMPTY : new NbtString(value);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.value);
	}

	@Override
	public byte getType() {
		return 8;
	}

	@Override
	public NbtType<NbtString> getNbtType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return NbtElement.super.asString();
	}

	public NbtString copy() {
		return this;
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
	public void accept(NbtElementVisitor visitor) {
		visitor.visitString(this);
	}

	public static String escape(String value) {
		StringBuilder stringBuilder = new StringBuilder(" ");
		char c = 0;

		for (int i = 0; i < value.length(); i++) {
			char d = value.charAt(i);
			if (d == '\\') {
				stringBuilder.append('\\');
			} else if (d == '"' || d == '\'') {
				if (c == 0) {
					c = (char)(d == '"' ? 39 : 34);
				}

				if (c == d) {
					stringBuilder.append('\\');
				}
			}

			stringBuilder.append(d);
		}

		if (c == 0) {
			c = '"';
		}

		stringBuilder.setCharAt(0, c);
		stringBuilder.append(c);
		return stringBuilder.toString();
	}
}
