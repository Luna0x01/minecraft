package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Pattern;
import net.minecraft.text.TranslatableText;

public class StringNbtReader {
	public static final SimpleCommandExceptionType TRAILING_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.nbt.trailing"));
	public static final SimpleCommandExceptionType EXPECTED_KEY_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.nbt.expected.key"));
	public static final SimpleCommandExceptionType EXPECTED_VALUE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("argument.nbt.expected.value"));
	public static final Dynamic2CommandExceptionType MIXED_LIST_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.nbt.list.mixed", object, object2)
	);
	public static final Dynamic2CommandExceptionType MIXED_ARRAY_EXCEPTION = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("argument.nbt.array.mixed", object, object2)
	);
	public static final DynamicCommandExceptionType INVALID_ARRAY_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.nbt.array.invalid", object)
	);
	private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
	private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", 2);
	private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", 2);
	private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", 2);
	private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", 2);
	private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", 2);
	private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
	private final StringReader reader;

	public static NbtCompound parse(String string) throws CommandSyntaxException {
		return new StringNbtReader(new StringReader(string)).parse();
	}

	@VisibleForTesting
	NbtCompound parse() throws CommandSyntaxException {
		NbtCompound nbtCompound = this.parseCompound();
		this.reader.skipWhitespace();
		if (this.reader.canRead()) {
			throw TRAILING_EXCEPTION.createWithContext(this.reader);
		} else {
			return nbtCompound;
		}
	}

	public StringNbtReader(StringReader stringReader) {
		this.reader = stringReader;
	}

	protected String parseString() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_KEY_EXCEPTION.createWithContext(this.reader);
		} else {
			return this.reader.readString();
		}
	}

	protected NbtElement parsePrimitive() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		int i = this.reader.getCursor();
		if (this.reader.peek() == '"') {
			return new NbtString(this.reader.readQuotedString());
		} else {
			String string = this.reader.readUnquotedString();
			if (string.isEmpty()) {
				this.reader.setCursor(i);
				throw EXPECTED_VALUE_EXCEPTION.createWithContext(this.reader);
			} else {
				return this.parseNumber(string);
			}
		}
	}

	private NbtElement parseNumber(String string) {
		try {
			if (FLOAT_PATTERN.matcher(string).matches()) {
				return new NbtFloat(Float.parseFloat(string.substring(0, string.length() - 1)));
			}

			if (BYTE_PATTERN.matcher(string).matches()) {
				return new NbtByte(Byte.parseByte(string.substring(0, string.length() - 1)));
			}

			if (LONG_PATTERN.matcher(string).matches()) {
				return new NbtLong(Long.parseLong(string.substring(0, string.length() - 1)));
			}

			if (SHORT_PATTERN.matcher(string).matches()) {
				return new NbtShort(Short.parseShort(string.substring(0, string.length() - 1)));
			}

			if (INT_PATTERN.matcher(string).matches()) {
				return new NbtInt(Integer.parseInt(string));
			}

			if (DOUBLE_PATTERN.matcher(string).matches()) {
				return new NbtDouble(Double.parseDouble(string.substring(0, string.length() - 1)));
			}

			if (DOUBLE_PATTERN_IMPLICIT.matcher(string).matches()) {
				return new NbtDouble(Double.parseDouble(string));
			}

			if ("true".equalsIgnoreCase(string)) {
				return new NbtByte((byte)1);
			}

			if ("false".equalsIgnoreCase(string)) {
				return new NbtByte((byte)0);
			}
		} catch (NumberFormatException var3) {
		}

		return new NbtString(string);
	}

	protected NbtElement parseElement() throws CommandSyntaxException {
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE_EXCEPTION.createWithContext(this.reader);
		} else {
			char c = this.reader.peek();
			if (c == '{') {
				return this.parseCompound();
			} else {
				return c == '[' ? this.parseAbstractList() : this.parsePrimitive();
			}
		}
	}

	protected NbtElement parseAbstractList() throws CommandSyntaxException {
		return this.reader.canRead(3) && this.reader.peek(1) != '"' && this.reader.peek(2) == ';' ? this.parseArray() : this.parseList();
	}

	public NbtCompound parseCompound() throws CommandSyntaxException {
		this.expect('{');
		NbtCompound nbtCompound = new NbtCompound();
		this.reader.skipWhitespace();

		while (this.reader.canRead() && this.reader.peek() != '}') {
			int i = this.reader.getCursor();
			String string = this.parseString();
			if (string.isEmpty()) {
				this.reader.setCursor(i);
				throw EXPECTED_KEY_EXCEPTION.createWithContext(this.reader);
			}

			this.expect(':');
			nbtCompound.put(string, this.parseElement());
			if (!this.readComma()) {
				break;
			}

			if (!this.reader.canRead()) {
				throw EXPECTED_KEY_EXCEPTION.createWithContext(this.reader);
			}
		}

		this.expect('}');
		return nbtCompound;
	}

	private NbtElement parseList() throws CommandSyntaxException {
		this.expect('[');
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE_EXCEPTION.createWithContext(this.reader);
		} else {
			NbtList nbtList = new NbtList();
			int i = -1;

			while (this.reader.peek() != ']') {
				int j = this.reader.getCursor();
				NbtElement nbtElement = this.parseElement();
				int k = nbtElement.getType();
				if (i < 0) {
					i = k;
				} else if (k != i) {
					this.reader.setCursor(j);
					throw MIXED_LIST_EXCEPTION.createWithContext(this.reader, NbtElement.getTypeName(k), NbtElement.getTypeName(i));
				}

				nbtList.add(nbtElement);
				if (!this.readComma()) {
					break;
				}

				if (!this.reader.canRead()) {
					throw EXPECTED_VALUE_EXCEPTION.createWithContext(this.reader);
				}
			}

			this.expect(']');
			return nbtList;
		}
	}

	private NbtElement parseArray() throws CommandSyntaxException {
		this.expect('[');
		int i = this.reader.getCursor();
		char c = this.reader.read();
		this.reader.read();
		this.reader.skipWhitespace();
		if (!this.reader.canRead()) {
			throw EXPECTED_VALUE_EXCEPTION.createWithContext(this.reader);
		} else if (c == 'B') {
			return new NbtByteArray(this.parseArray((byte)7, (byte)1));
		} else if (c == 'L') {
			return new NbtLongArray(this.parseArray((byte)12, (byte)4));
		} else if (c == 'I') {
			return new NbtIntArray(this.parseArray((byte)11, (byte)3));
		} else {
			this.reader.setCursor(i);
			throw INVALID_ARRAY_EXCEPTION.createWithContext(this.reader, String.valueOf(c));
		}
	}

	private <T extends Number> List<T> parseArray(byte nbtType, byte numberType) throws CommandSyntaxException {
		List<T> list = Lists.newArrayList();

		while (this.reader.peek() != ']') {
			int i = this.reader.getCursor();
			NbtElement nbtElement = this.parseElement();
			int j = nbtElement.getType();
			if (j != numberType) {
				this.reader.setCursor(i);
				throw MIXED_ARRAY_EXCEPTION.createWithContext(this.reader, NbtElement.getTypeName(j), NbtElement.getTypeName(nbtType));
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

			if (!this.reader.canRead()) {
				throw EXPECTED_VALUE_EXCEPTION.createWithContext(this.reader);
			}
		}

		this.expect(']');
		return list;
	}

	private boolean readComma() {
		this.reader.skipWhitespace();
		if (this.reader.canRead() && this.reader.peek() == ',') {
			this.reader.skip();
			this.reader.skipWhitespace();
			return true;
		} else {
			return false;
		}
	}

	private void expect(char c) throws CommandSyntaxException {
		this.reader.skipWhitespace();
		this.reader.expect(c);
	}
}
