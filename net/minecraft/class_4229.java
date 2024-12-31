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

public class class_4229 implements ArgumentType<class_4213> {
	private static final Collection<String> field_20779 = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

	public static class_4229 method_19207() {
		return new class_4229();
	}

	public class_4213 parse(StringReader stringReader) throws CommandSyntaxException {
		class_4238 lv = new class_4238(stringReader, false).method_19300(true);
		return new class_4213(lv.method_19301(), lv.method_19288().keySet(), lv.method_19304());
	}

	public static class_4213 method_19209(CommandContext<class_3915> commandContext, String string) {
		return (class_4213)commandContext.getArgument(string, class_4213.class);
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
		stringReader.setCursor(suggestionsBuilder.getStart());
		class_4238 lv = new class_4238(stringReader, false);

		try {
			lv.method_19300(true);
		} catch (CommandSyntaxException var6) {
		}

		return lv.method_19292(suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_20779;
	}
}
