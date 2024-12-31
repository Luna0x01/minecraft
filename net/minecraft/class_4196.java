package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.TranslatableText;

public class class_4196 implements ArgumentType<Integer> {
	private static final Collection<String> field_20591 = Arrays.asList("sidebar", "foo.bar");
	public static final DynamicCommandExceptionType field_20590 = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.scoreboardDisplaySlot.invalid", object)
	);

	private class_4196() {
	}

	public static class_4196 method_18938() {
		return new class_4196();
	}

	public static int method_18940(CommandContext<class_3915> commandContext, String string) {
		return (Integer)commandContext.getArgument(string, Integer.class);
	}

	public Integer parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		int i = Scoreboard.getDisplaySlotId(string);
		if (i == -1) {
			throw field_20590.create(string);
		} else {
			return i;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17570(Scoreboard.getDisplaySlotNames(), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_20591;
	}
}
