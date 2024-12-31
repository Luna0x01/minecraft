package net.minecraft.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.class_3415;
import net.minecraft.class_3833;

public class SharedConstants {
	public static final Level RESOURCE_LEAK_DETECTOR_DISABLED = Level.DISABLED;
	public static boolean isDevelopment;
	public static final char[] INVALID_LEVEL_NAME_CHARS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '"', ':'};

	public static boolean isValidChar(char chr) {
		return chr != 167 && chr >= ' ' && chr != 127;
	}

	public static String stripInvalidChars(String s) {
		StringBuilder stringBuilder = new StringBuilder();

		for (char c : s.toCharArray()) {
			if (isValidChar(c)) {
				stringBuilder.append(c);
			}
		}

		return stringBuilder.toString();
	}

	static {
		ResourceLeakDetector.setLevel(RESOURCE_LEAK_DETECTOR_DISABLED);
		CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
		CommandSyntaxException.BUILT_IN_EXCEPTIONS = new class_3833();
		NamespacedStringType.ENSURE_NAMESPACE = class_3415::method_15286;
	}
}
