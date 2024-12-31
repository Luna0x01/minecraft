package net.minecraft.nbt;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StringNbtReader {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern VALID_LIST_PATTERN = Pattern.compile("\\[[-+\\d|,\\s]+\\]");

	public static NbtCompound parse(String string) throws NbtException {
		string = string.trim();
		if (!string.startsWith("{")) {
			throw new NbtException("Invalid tag encountered, expected '{' as first char.");
		} else if (getTopElementCount(string) != 1) {
			throw new NbtException("Encountered multiple top tags, only one expected");
		} else {
			return (NbtCompound)createParser("tag", string).parse();
		}
	}

	static int getTopElementCount(String string) throws NbtException {
		int i = 0;
		boolean bl = false;
		Stack<Character> stack = new Stack();

		for (int j = 0; j < string.length(); j++) {
			char c = string.charAt(j);
			if (c == '"') {
				if (isEscaped(string, j)) {
					if (!bl) {
						throw new NbtException("Illegal use of \\\": " + string);
					}
				} else {
					bl = !bl;
				}
			} else if (!bl) {
				if (c != '{' && c != '[') {
					if (c == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
						throw new NbtException("Unbalanced curly brackets {}: " + string);
					}

					if (c == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
						throw new NbtException("Unbalanced square brackets []: " + string);
					}
				} else {
					if (stack.isEmpty()) {
						i++;
					}

					stack.push(c);
				}
			}
		}

		if (bl) {
			throw new NbtException("Unbalanced quotation: " + string);
		} else if (!stack.isEmpty()) {
			throw new NbtException("Unbalanced brackets: " + string);
		} else {
			if (i == 0 && !string.isEmpty()) {
				i = 1;
			}

			return i;
		}
	}

	static StringNbtReader.Parser createParserFromArray(String... keyNbtArray) throws NbtException {
		return createParser(keyNbtArray[0], keyNbtArray[1]);
	}

	static StringNbtReader.Parser createParser(String key, String stringNbt) throws NbtException {
		stringNbt = stringNbt.trim();
		if (stringNbt.startsWith("{")) {
			stringNbt = stringNbt.substring(1, stringNbt.length() - 1);
			StringNbtReader.NbtCompoundParser nbtCompoundParser = new StringNbtReader.NbtCompoundParser(key);

			while (stringNbt.length() > 0) {
				String string = getFirstElement(stringNbt, true);
				if (string.length() > 0) {
					boolean bl = false;
					nbtCompoundParser.parsers.add(createParser(string, bl));
				}

				if (stringNbt.length() < string.length() + 1) {
					break;
				}

				char c = stringNbt.charAt(string.length());
				if (c != ',' && c != '{' && c != '}' && c != '[' && c != ']') {
					throw new NbtException("Unexpected token '" + c + "' at: " + stringNbt.substring(string.length()));
				}

				stringNbt = stringNbt.substring(string.length() + 1);
			}

			return nbtCompoundParser;
		} else if (stringNbt.startsWith("[") && !VALID_LIST_PATTERN.matcher(stringNbt).matches()) {
			stringNbt = stringNbt.substring(1, stringNbt.length() - 1);
			StringNbtReader.NbtListParser nbtListParser = new StringNbtReader.NbtListParser(key);

			while (stringNbt.length() > 0) {
				String string2 = getFirstElement(stringNbt, false);
				if (string2.length() > 0) {
					boolean bl2 = true;
					nbtListParser.parsers.add(createParser(string2, bl2));
				}

				if (stringNbt.length() < string2.length() + 1) {
					break;
				}

				char d = stringNbt.charAt(string2.length());
				if (d != ',' && d != '{' && d != '}' && d != '[' && d != ']') {
					throw new NbtException("Unexpected token '" + d + "' at: " + stringNbt.substring(string2.length()));
				}

				stringNbt = stringNbt.substring(string2.length() + 1);
			}

			return nbtListParser;
		} else {
			return new StringNbtReader.PrimitiveTypeParser(key, stringNbt);
		}
	}

	private static StringNbtReader.Parser createParser(String stringNbt, boolean missingKey) throws NbtException {
		String string = getKey(stringNbt, missingKey);
		String string2 = getValue(stringNbt, missingKey);
		return createParserFromArray(string, string2);
	}

	private static String getFirstElement(String stringNbt, boolean hasKey) throws NbtException {
		int i = indexOf(stringNbt, ':');
		int j = indexOf(stringNbt, ',');
		if (hasKey) {
			if (i == -1) {
				throw new NbtException("Unable to locate name/value separator for string: " + stringNbt);
			}

			if (j != -1 && j < i) {
				throw new NbtException("Name error at: " + stringNbt);
			}
		} else if (i == -1 || i > j) {
			i = -1;
		}

		return getFirstElement(stringNbt, i);
	}

	private static String getFirstElement(String string, int startIndex) throws NbtException {
		Stack<Character> stack = new Stack();
		int i = startIndex + 1;
		boolean bl = false;
		boolean bl2 = false;
		boolean bl3 = false;

		for (int j = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '"') {
				if (isEscaped(string, i)) {
					if (!bl) {
						throw new NbtException("Illegal use of \\\": " + string);
					}
				} else {
					bl = !bl;
					if (bl && !bl3) {
						bl2 = true;
					}

					if (!bl) {
						j = i;
					}
				}
			} else if (!bl) {
				if (c != '{' && c != '[') {
					if (c == '}' && (stack.isEmpty() || (Character)stack.pop() != '{')) {
						throw new NbtException("Unbalanced curly brackets {}: " + string);
					}

					if (c == ']' && (stack.isEmpty() || (Character)stack.pop() != '[')) {
						throw new NbtException("Unbalanced square brackets []: " + string);
					}

					if (c == ',' && stack.isEmpty()) {
						return string.substring(0, i);
					}
				} else {
					stack.push(c);
				}
			}

			if (!Character.isWhitespace(c)) {
				if (!bl && bl2 && j != i) {
					return string.substring(0, j + 1);
				}

				bl3 = true;
			}
		}

		return string.substring(0, i);
	}

	private static String getKey(String stringNbt, boolean missingKey) throws NbtException {
		if (missingKey) {
			stringNbt = stringNbt.trim();
			if (stringNbt.startsWith("{") || stringNbt.startsWith("[")) {
				return "";
			}
		}

		int i = indexOf(stringNbt, ':');
		if (i != -1) {
			return stringNbt.substring(0, i).trim();
		} else if (missingKey) {
			return "";
		} else {
			throw new NbtException("Unable to locate name/value separator for string: " + stringNbt);
		}
	}

	private static String getValue(String stringNbt, boolean missingKey) throws NbtException {
		if (missingKey) {
			stringNbt = stringNbt.trim();
			if (stringNbt.startsWith("{") || stringNbt.startsWith("[")) {
				return stringNbt;
			}
		}

		int i = indexOf(stringNbt, ':');
		if (i != -1) {
			return stringNbt.substring(i + 1).trim();
		} else if (missingKey) {
			return stringNbt;
		} else {
			throw new NbtException("Unable to locate name/value separator for string: " + stringNbt);
		}
	}

	private static int indexOf(String string, char c) {
		int i = 0;

		for (boolean bl = true; i < string.length(); i++) {
			char d = string.charAt(i);
			if (d == '"') {
				if (!isEscaped(string, i)) {
					bl = !bl;
				}
			} else if (bl) {
				if (d == c) {
					return i;
				}

				if (d == '{' || d == '[') {
					return -1;
				}
			}
		}

		return -1;
	}

	private static boolean isEscaped(String string, int index) {
		return index > 0 && string.charAt(index - 1) == '\\' && !isEscaped(string, index - 1);
	}

	static class NbtCompoundParser extends StringNbtReader.Parser {
		protected List<StringNbtReader.Parser> parsers = Lists.newArrayList();

		public NbtCompoundParser(String string) {
			this.key = string;
		}

		@Override
		public NbtElement parse() throws NbtException {
			NbtCompound nbtCompound = new NbtCompound();

			for (StringNbtReader.Parser parser : this.parsers) {
				nbtCompound.put(parser.key, parser.parse());
			}

			return nbtCompound;
		}
	}

	static class NbtListParser extends StringNbtReader.Parser {
		protected List<StringNbtReader.Parser> parsers = Lists.newArrayList();

		public NbtListParser(String string) {
			this.key = string;
		}

		@Override
		public NbtElement parse() throws NbtException {
			NbtList nbtList = new NbtList();

			for (StringNbtReader.Parser parser : this.parsers) {
				nbtList.add(parser.parse());
			}

			return nbtList;
		}
	}

	abstract static class Parser {
		protected String key;

		public abstract NbtElement parse() throws NbtException;
	}

	static class PrimitiveTypeParser extends StringNbtReader.Parser {
		private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
		private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
		private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?[0-9]+[b|B]");
		private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?[0-9]+[l|L]");
		private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?[0-9]+[s|S]");
		private static final Pattern INT_PATTERN = Pattern.compile("[-+]?[0-9]+");
		private static final Pattern DECIMAL_PATTERN = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
		private static final Splitter SPLITTER = Splitter.on(',').omitEmptyStrings();
		protected String value;

		public PrimitiveTypeParser(String string, String string2) {
			this.key = string;
			this.value = string2;
		}

		@Override
		public NbtElement parse() throws NbtException {
			try {
				if (DOUBLE_PATTERN.matcher(this.value).matches()) {
					return new NbtDouble(Double.parseDouble(this.value.substring(0, this.value.length() - 1)));
				}

				if (FLOAT_PATTERN.matcher(this.value).matches()) {
					return new NbtFloat(Float.parseFloat(this.value.substring(0, this.value.length() - 1)));
				}

				if (BYTE_PATTERN.matcher(this.value).matches()) {
					return new NbtByte(Byte.parseByte(this.value.substring(0, this.value.length() - 1)));
				}

				if (LONG_PATTERN.matcher(this.value).matches()) {
					return new NbtLong(Long.parseLong(this.value.substring(0, this.value.length() - 1)));
				}

				if (SHORT_PATTERN.matcher(this.value).matches()) {
					return new NbtShort(Short.parseShort(this.value.substring(0, this.value.length() - 1)));
				}

				if (INT_PATTERN.matcher(this.value).matches()) {
					return new NbtInt(Integer.parseInt(this.value));
				}

				if (DECIMAL_PATTERN.matcher(this.value).matches()) {
					return new NbtDouble(Double.parseDouble(this.value));
				}

				if (this.value.equalsIgnoreCase("true") || this.value.equalsIgnoreCase("false")) {
					return new NbtByte((byte)(Boolean.parseBoolean(this.value) ? 1 : 0));
				}
			} catch (NumberFormatException var6) {
				this.value = this.value.replaceAll("\\\\\"", "\"");
				return new NbtString(this.value);
			}

			if (this.value.startsWith("[") && this.value.endsWith("]")) {
				String string = this.value.substring(1, this.value.length() - 1);
				String[] strings = (String[])Iterables.toArray(SPLITTER.split(string), String.class);

				try {
					int[] is = new int[strings.length];

					for (int i = 0; i < strings.length; i++) {
						is[i] = Integer.parseInt(strings[i].trim());
					}

					return new NbtIntArray(is);
				} catch (NumberFormatException var5) {
					return new NbtString(this.value);
				}
			} else {
				if (this.value.startsWith("\"") && this.value.endsWith("\"")) {
					this.value = this.value.substring(1, this.value.length() - 1);
				}

				this.value = this.value.replaceAll("\\\\\"", "\"");
				StringBuilder stringBuilder = new StringBuilder();

				for (int j = 0; j < this.value.length(); j++) {
					if (j < this.value.length() - 1 && this.value.charAt(j) == '\\' && this.value.charAt(j + 1) == '\\') {
						stringBuilder.append('\\');
						j++;
					} else {
						stringBuilder.append(this.value.charAt(j));
					}
				}

				return new NbtString(stringBuilder.toString());
			}
		}
	}
}
