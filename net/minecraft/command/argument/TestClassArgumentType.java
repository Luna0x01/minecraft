package net.minecraft.command.argument;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.test.TestFunctions;
import net.minecraft.text.LiteralText;

public class TestClassArgumentType implements ArgumentType<String> {
	private static final Collection<String> EXAMPLES = Arrays.asList("techtests", "mobtests");

	public String parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (TestFunctions.testClassExists(string)) {
			return string;
		} else {
			Message message = new LiteralText("No such test class: " + string);
			throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
		}
	}

	public static TestClassArgumentType testClass() {
		return new TestClassArgumentType();
	}

	public static String getTestClass(CommandContext<ServerCommandSource> context, String name) {
		return (String)context.getArgument(name, String.class);
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestMatching(TestFunctions.getTestClasses().stream(), suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
