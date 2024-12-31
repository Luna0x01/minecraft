package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.PacketByteBuf;

public class class_4186 implements ArgumentType<class_4186.class_4187> {
	public static final SuggestionProvider<class_3915> field_20535 = (commandContext, suggestionsBuilder) -> {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_4318 lv = new class_4318(stringReader);

		try {
			lv.method_19818();
		} catch (CommandSyntaxException var5) {
		}

		return lv.method_19761(
			suggestionsBuilder, suggestionsBuilderx -> class_3965.method_17571(((class_3915)commandContext.getSource()).method_17576(), suggestionsBuilderx)
		);
	};
	private static final Collection<String> field_20536 = Arrays.asList("Player", "0123", "*", "@e");
	private static final SimpleCommandExceptionType field_20537 = new SimpleCommandExceptionType(new TranslatableText("argument.scoreHolder.empty"));
	private final boolean field_20538;

	public class_4186(boolean bl) {
		this.field_20538 = bl;
	}

	public static String method_18923(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return (String)method_18929(commandContext, string).iterator().next();
	}

	public static Collection<String> method_18929(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return method_18924(commandContext, string, Collections::emptyList);
	}

	public static Collection<String> method_18930(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return method_18924(commandContext, string, ((class_3915)commandContext.getSource()).method_17473().method_20333()::getKnownPlayers);
	}

	public static Collection<String> method_18924(CommandContext<class_3915> commandContext, String string, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
		Collection<String> collection = ((class_4186.class_4187)commandContext.getArgument(string, class_4186.class_4187.class))
			.getNames((class_3915)commandContext.getSource(), supplier);
		if (collection.isEmpty()) {
			throw class_4062.field_19707.create();
		} else {
			return collection;
		}
	}

	public static class_4186 method_18919() {
		return new class_4186(false);
	}

	public static class_4186 method_18927() {
		return new class_4186(true);
	}

	public class_4186.class_4187 parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '@') {
			class_4318 lv = new class_4318(stringReader);
			class_4317 lv2 = lv.method_19818();
			if (!this.field_20538 && lv2.method_19726() > 1) {
				throw class_4062.field_19704.create();
			} else {
				return new class_4186.class_4188(lv2);
			}
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			String string = stringReader.getString().substring(i, stringReader.getCursor());
			if (string.equals("*")) {
				return (arg, supplier) -> {
					Collection<String> collectionx = (Collection<String>)supplier.get();
					if (collectionx.isEmpty()) {
						throw field_20537.create();
					} else {
						return collectionx;
					}
				};
			} else {
				Collection<String> collection = Collections.singleton(string);
				return (arg, supplier) -> collection;
			}
		}
	}

	public Collection<String> getExamples() {
		return field_20536;
	}

	@FunctionalInterface
	public interface class_4187 {
		Collection<String> getNames(class_3915 arg, Supplier<Collection<String>> supplier) throws CommandSyntaxException;
	}

	public static class class_4188 implements class_4186.class_4187 {
		private final class_4317 field_20539;

		public class_4188(class_4317 arg) {
			this.field_20539 = arg;
		}

		@Override
		public Collection<String> getNames(class_3915 arg, Supplier<Collection<String>> supplier) throws CommandSyntaxException {
			List<? extends Entity> list = this.field_20539.method_19735(arg);
			if (list.isEmpty()) {
				throw class_4062.field_19707.create();
			} else {
				List<String> list2 = Lists.newArrayList();

				for (Entity entity : list) {
					list2.add(entity.method_15586());
				}

				return list2;
			}
		}
	}

	public static class class_4189 implements class_4322<class_4186> {
		public void method_19890(class_4186 arg, PacketByteBuf packetByteBuf) {
			byte b = 0;
			if (arg.field_20538) {
				b = (byte)(b | 1);
			}

			packetByteBuf.writeByte(b);
		}

		public class_4186 method_19891(PacketByteBuf packetByteBuf) {
			byte b = packetByteBuf.readByte();
			boolean bl = (b & 1) != 0;
			return new class_4186(bl);
		}

		public void method_19889(class_4186 arg, JsonObject jsonObject) {
			jsonObject.addProperty("amount", arg.field_20538 ? "multiple" : "single");
		}
	}
}
