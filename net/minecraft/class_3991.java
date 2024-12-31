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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class class_3991 implements ArgumentType<Formatting> {
	private static final Collection<String> field_19404 = Arrays.asList("red", "green");
	public static final DynamicCommandExceptionType field_19403 = new DynamicCommandExceptionType(object -> new TranslatableText("argument.color.invalid", object));

	private class_3991() {
	}

	public static class_3991 method_17647() {
		return new class_3991();
	}

	public static Formatting method_17649(CommandContext<class_3915> commandContext, String string) {
		return (Formatting)commandContext.getArgument(string, Formatting.class);
	}

	public Formatting parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		Formatting formatting = Formatting.byName(string);
		if (formatting != null && !formatting.isModifier()) {
			return formatting;
		} else {
			throw field_19403.create(string);
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17571(Formatting.getNames(true, false), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_19404;
	}
}
