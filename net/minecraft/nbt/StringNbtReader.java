package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;

public class StringNbtReader {
	private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
	private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
	private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
	private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
	private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
	private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
	private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
	private final String input;
	private int cursor;

	public static NbtCompound parse(String string) throws NbtException {
		return new StringNbtReader(string).parse();
	}

	@VisibleForTesting
	NbtCompound parse() throws NbtException {
		NbtCompound nbtCompound = this.parseCompound();
		this.skipWhitespace();
		if (this.canRead()) {
			this.cursor++;
			throw this.createException("Trailing data found");
		} else {
			return nbtCompound;
		}
	}

	@VisibleForTesting
	StringNbtReader(String string) {
		this.input = string;
	}

	protected String parseString() throws NbtException {
		this.skipWhitespace();
		if (!this.canRead()) {
			throw this.createException("Expected key");
		} else {
			return this.peek() == '"' ? this.readQuotedString() : this.readUnquotedString();
		}
	}

	private NbtException createException(String message) {
		return new NbtException(message, this.input, this.cursor);
	}

	protected NbtElement parsePrimitive() throws NbtException {
		this.skipWhitespace();
		if (this.peek() == '"') {
			return new NbtString(this.readQuotedString());
		} else {
			String string = this.readUnquotedString();
			if (string.isEmpty()) {
				throw this.createException("Expected value");
			} else {
				return this.parsePrimitive(string);
			}
		}
	}

	private NbtElement parsePrimitive(String input) {
		try {
			if (FLOAT_PATTERN.matcher(input).matches()) {
				return new NbtFloat(Float.parseFloat(input.substring(0, input.length() - 1)));
			}

			if (BYTE_PATTERN.matcher(input).matches()) {
				return new NbtByte(Byte.parseByte(input.substring(0, input.length() - 1)));
			}

			if (LONG_PATTERN.matcher(input).matches()) {
				return new NbtLong(Long.parseLong(input.substring(0, input.length() - 1)));
			}

			if (SHORT_PATTERN.matcher(input).matches()) {
				return new NbtShort(Short.parseShort(input.substring(0, input.length() - 1)));
			}

			if (INT_PATTERN.matcher(input).matches()) {
				return new NbtInt(Integer.parseInt(input));
			}

			if (DOUBLE_PATTERN.matcher(input).matches()) {
				return new NbtDouble(Double.parseDouble(input.substring(0, input.length() - 1)));
			}

			if (DOUBLE_PATTERN_IMPLICIT.matcher(input).matches()) {
				return new NbtDouble(Double.parseDouble(input));
			}

			if ("true".equalsIgnoreCase(input)) {
				return new NbtByte((byte)1);
			}

			if ("false".equalsIgnoreCase(input)) {
				return new NbtByte((byte)0);
			}
		} catch (NumberFormatException var3) {
		}

		return new NbtString(input);
	}

	private String readQuotedString() throws NbtException {
		int i = ++this.cursor;
		StringBuilder stringBuilder = null;
		boolean bl = false;

		while (this.canRead()) {
			char c = this.read();
			if (bl) {
				if (c != '\\' && c != '"') {
					throw this.createException("Invalid escape of '" + c + "'");
				}

				bl = false;
			} else {
				if (c == '\\') {
					bl = true;
					if (stringBuilder == null) {
						stringBuilder = new StringBuilder(this.input.substring(i, this.cursor - 1));
					}
					continue;
				}

				if (c == '"') {
					return stringBuilder == null ? this.input.substring(i, this.cursor - 1) : stringBuilder.toString();
				}
			}

			if (stringBuilder != null) {
				stringBuilder.append(c);
			}
		}

		throw this.createException("Missing termination quote");
	}

	private String readUnquotedString() {
		int i = this.cursor;

		while (this.canRead() && this.isAllowedInUnquotedString(this.peek())) {
			this.cursor++;
		}

		return this.input.substring(i, this.cursor);
	}

	protected NbtElement parseElement() throws NbtException {
		this.skipWhitespace();
		if (!this.canRead()) {
			throw this.createException("Expected value");
		} else {
			char c = this.peek();
			if (c == '{') {
				return this.parseCompound();
			} else {
				return c == '[' ? this.parseArray() : this.parsePrimitive();
			}
		}
	}

	protected NbtElement parseArray() throws NbtException {
		return this.canRead(2) && this.peek(1) != '"' && this.peek(2) == ';' ? this.parsePrimitiveArray() : this.parseList();
	}

	protected NbtCompound parseCompound() throws NbtException {
		this.expect('{');
		NbtCompound nbtCompound = new NbtCompound();
		this.skipWhitespace();

		while (this.canRead() && this.peek() != '}') {
			String string = this.parseString();
			if (string.isEmpty()) {
				throw this.createException("Expected non-empty key");
			}

			this.expect(':');
			nbtCompound.put(string, this.parseElement());
			if (!this.readComma()) {
				break;
			}

			if (!this.canRead()) {
				throw this.createException("Expected key");
			}
		}

		this.expect('}');
		return nbtCompound;
	}

	private NbtElement parseList() throws NbtException {
		this.expect('[');
		this.skipWhitespace();
		if (!this.canRead()) {
			throw this.createException("Expected value");
		} else {
			NbtList nbtList = new NbtList();
			int i = -1;

			while (this.peek() != ']') {
				NbtElement nbtElement = this.parseElement();
				int j = nbtElement.getType();
				if (i < 0) {
					i = j;
				} else if (j != i) {
					throw this.createException("Unable to insert " + NbtElement.getTypeName(j) + " into ListTag of type " + NbtElement.getTypeName(i));
				}

				nbtList.add(nbtElement);
				if (!this.readComma()) {
					break;
				}

				if (!this.canRead()) {
					throw this.createException("Expected value");
				}
			}

			this.expect(']');
			return nbtList;
		}
	}

	private NbtElement parsePrimitiveArray() throws NbtException {
		this.expect('[');
		char c = this.read();
		this.read();
		this.skipWhitespace();
		if (!this.canRead()) {
			throw this.createException("Expected value");
		} else if (c == 'B') {
			return new NbtByteArray(this.parseArray((byte)7, (byte)1));
		} else if (c == 'L') {
			return new NbtLongArray(this.parseArray((byte)12, (byte)4));
		} else if (c == 'I') {
			return new NbtIntArray(this.parseArray((byte)11, (byte)3));
		} else {
			throw this.createException("Invalid array type '" + c + "' found");
		}
	}

	private <T extends Number> List<T> parseArray(byte nbtType, byte numberType) throws NbtException {
		List<T> list = Lists.newArrayList();

		while (this.peek() != ']') {
			NbtElement nbtElement = this.parseElement();
			int i = nbtElement.getType();
			if (i != numberType) {
				throw this.createException("Unable to insert " + NbtElement.getTypeName(i) + " into " + NbtElement.getTypeName(nbtType));
			}

			if (numberType == 1) {
				list.add(((AbstractNbtNumber)nbtElement).byteValue());
			} else if (numberType == 4) {
				list.add(((AbstractNbtNumber)nbtElement).longValue());
			} else {
				list.add(((AbstractNbtNumber)nbtElement).intValue());
			}

			if (!this.readComma()) {
				break;
			}

			if (!this.canRead()) {
				throw this.createException("Expected value");
			}
		}

		this.expect(']');
		return list;
	}

	private void skipWhitespace() {
		while (this.canRead() && Character.isWhitespace(this.peek())) {
			this.cursor++;
		}
	}

	private boolean readComma() {
		this.skipWhitespace();
		if (this.canRead() && this.peek() == ',') {
			this.cursor++;
			this.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	private void expect(char c) throws NbtException {
		this.skipWhitespace();
		boolean bl = this.canRead();
		if (bl && this.peek() == c) {
			this.cursor++;
		} else {
			throw new NbtException("Expected '" + c + "' but got '" + (bl ? this.peek() : "<EOF>") + "'", this.input, this.cursor + 1);
		}
	}

	protected boolean isAllowedInUnquotedString(char c) {
		return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+';
	}

	private boolean canRead(int offset) {
		return this.cursor + offset < this.input.length();
	}

	boolean canRead() {
		return this.canRead(0);
	}

	private char peek(int offset) {
		return this.input.charAt(this.cursor + offset);
	}

	private char peek() {
		return this.peek(0);
	}

	private char read() {
		return this.input.charAt(this.cursor++);
	}
}
