package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class class_4257 implements ArgumentType<class_4261> {
	private static final Collection<String> field_20924 = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
	public static final SimpleCommandExceptionType field_20923 = new SimpleCommandExceptionType(new TranslatableText("argument.pos2d.incomplete"));

	public static class_4257 method_19369() {
		return new class_4257();
	}

	public static class_4257.class_4258 method_19371(CommandContext<class_3915> commandContext, String string) {
		BlockPos blockPos = ((class_4261)commandContext.getArgument(string, class_4261.class)).method_19415((class_3915)commandContext.getSource());
		return new class_4257.class_4258(blockPos.getX(), blockPos.getZ());
	}

	public class_4261 parse(StringReader stringReader) throws CommandSyntaxException {
		int i = stringReader.getCursor();
		if (!stringReader.canRead()) {
			throw field_20923.createWithContext(stringReader);
		} else {
			class_4298 lv = class_4298.method_19608(stringReader);
			if (stringReader.canRead() && stringReader.peek() == ' ') {
				stringReader.skip();
				class_4298 lv2 = class_4298.method_19608(stringReader);
				return new class_4304(lv, new class_4298(true, 0.0), lv2);
			} else {
				stringReader.setCursor(i);
				throw field_20923.createWithContext(stringReader);
			}
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (!(commandContext.getSource() instanceof class_3965)) {
			return Suggestions.empty();
		} else {
			String string = suggestionsBuilder.getRemaining();
			Collection<class_3965.class_3966> collection;
			if (!string.isEmpty() && string.charAt(0) == '^') {
				collection = Collections.singleton(class_3965.class_3966.field_19334);
			} else {
				collection = ((class_3965)commandContext.getSource()).method_17569(false);
			}

			return class_3965.method_17572(string, collection, suggestionsBuilder, CommandManager.method_17520(this::parse));
		}
	}

	public Collection<String> getExamples() {
		return field_20924;
	}

	public static class class_4258 {
		public final int field_20925;
		public final int field_20926;

		public class_4258(int i, int j) {
			this.field_20925 = i;
			this.field_20926 = j;
		}

		public String toString() {
			return "[" + this.field_20925 + ", " + this.field_20926 + "]";
		}
	}
}
