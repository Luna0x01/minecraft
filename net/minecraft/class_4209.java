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
import net.minecraft.scoreboard.Team;
import net.minecraft.text.TranslatableText;

public class class_4209 implements ArgumentType<String> {
	private static final Collection<String> field_20629 = Arrays.asList("foo", "123");
	private static final DynamicCommandExceptionType field_20630 = new DynamicCommandExceptionType(object -> new TranslatableText("team.notFound", object));

	public static class_4209 method_18997() {
		return new class_4209();
	}

	public static Team method_18999(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		String string2 = (String)commandContext.getArgument(string, String.class);
		Scoreboard scoreboard = ((class_3915)commandContext.getSource()).method_17473().method_20333();
		Team team = scoreboard.getTeam(string2);
		if (team == null) {
			throw field_20630.create(string2);
		} else {
			return team;
		}
	}

	public String parse(StringReader stringReader) throws CommandSyntaxException {
		return stringReader.readUnquotedString();
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return commandContext.getSource() instanceof class_3965
			? class_3965.method_17571(((class_3965)commandContext.getSource()).method_17577(), suggestionsBuilder)
			: Suggestions.empty();
	}

	public Collection<String> getExamples() {
		return field_20629;
	}
}
