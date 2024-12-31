package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.util.PacketByteBuf;

public interface class_4173<T extends class_3638<?>> extends ArgumentType<T> {
	static class_4173.class_4176 method_18871() {
		return new class_4173.class_4176();
	}

	public static class class_4174 implements class_4173<class_3638.class_3641> {
		private static final Collection<String> field_20491 = Arrays.asList("0..5.2", "0", "-5.4", "-100.76..", "..100");

		public class_3638.class_3641 parse(StringReader stringReader) throws CommandSyntaxException {
			return class_3638.class_3641.method_16516(stringReader);
		}

		public Collection<String> getExamples() {
			return field_20491;
		}

		public static class class_4175 extends class_4173.class_4178<class_4173.class_4174> {
			public class_4173.class_4174 method_19891(PacketByteBuf packetByteBuf) {
				return new class_4173.class_4174();
			}
		}
	}

	public static class class_4176 implements class_4173<class_3638.class_3642> {
		private static final Collection<String> field_20492 = Arrays.asList("0..5", "0", "-5", "-100..", "..100");

		public static class_3638.class_3642 method_18875(CommandContext<class_3915> commandContext, String string) {
			return (class_3638.class_3642)commandContext.getArgument(string, class_3638.class_3642.class);
		}

		public class_3638.class_3642 parse(StringReader stringReader) throws CommandSyntaxException {
			return class_3638.class_3642.method_16525(stringReader);
		}

		public Collection<String> getExamples() {
			return field_20492;
		}

		public static class class_4177 extends class_4173.class_4178<class_4173.class_4176> {
			public class_4173.class_4176 method_19891(PacketByteBuf packetByteBuf) {
				return new class_4173.class_4176();
			}
		}
	}

	public abstract static class class_4178<T extends class_4173<?>> implements class_4322<T> {
		public void method_19890(T arg, PacketByteBuf packetByteBuf) {
		}

		public void method_19889(T arg, JsonObject jsonObject) {
		}
	}
}
