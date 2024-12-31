package net.minecraft.nbt;

public class NbtException extends Exception {
	public NbtException(String string, String string2, int i) {
		super(string + " at: " + getContext(string2, i));
	}

	private static String getContext(String input, int cursor) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = Math.min(input.length(), cursor);
		if (i > 35) {
			stringBuilder.append("...");
		}

		stringBuilder.append(input.substring(Math.max(0, i - 35), i));
		stringBuilder.append("<--[HERE]");
		return stringBuilder.toString();
	}
}
