package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class class_4310 implements ArgumentType<class_4311> {
	private static final Collection<String> field_21154 = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

	public static class_4310 method_19698() {
		return new class_4310();
	}

	public class_4311 parse(StringReader stringReader) throws CommandSyntaxException {
		class_4312 lv = new class_4312(stringReader, false).method_19717();
		return new class_4311(lv.method_19708(), lv.method_19710());
	}

	public static <S> class_4311 method_19700(CommandContext<S> commandContext, String string) {
		return (class_4311)commandContext.getArgument(string, class_4311.class);
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_4312 lv = new class_4312(stringReader, false);

		try {
			lv.method_19717();
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_19706(suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_21154;
	}
}
