package net.minecraft.command.argument;

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
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ColorArgumentType implements ArgumentType<Formatting> {
	private static final Collection<String> EXAMPLES = Arrays.asList("red", "green");
	public static final DynamicCommandExceptionType INVALID_COLOR_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.color.invalid", object)
	);

	private ColorArgumentType() {
	}

	public static ColorArgumentType color() {
		return new ColorArgumentType();
	}

	public static Formatting getColor(CommandContext<ServerCommandSource> context, String name) {
		return (Formatting)context.getArgument(name, Formatting.class);
	}

	public Formatting parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		Formatting formatting = Formatting.byName(string);
		if (formatting != null && !formatting.isModifier()) {
			return formatting;
		} else {
			throw INVALID_COLOR_EXCEPTION.create(string);
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestMatching(Formatting.getNames(true, false), builder);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
