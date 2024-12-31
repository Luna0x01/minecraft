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
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.TranslatableText;

public class class_4151 implements ArgumentType<String> {
	private static final Collection<String> field_20193 = Arrays.asList("foo", "*", "012");
	private static final DynamicCommandExceptionType field_20194 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.objective.notFound", object)
	);
	private static final DynamicCommandExceptionType field_20195 = new DynamicCommandExceptionType(
		object -> new TranslatableText("arguments.objective.readonly", object)
	);
	public static final DynamicCommandExceptionType field_20192 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.scoreboard.objectives.add.longName", object)
	);

	public static class_4151 method_18520() {
		return new class_4151();
	}

	public static ScoreboardObjective method_18522(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		String string2 = (String)commandContext.getArgument(string, String.class);
		Scoreboard scoreboard = ((class_3915)commandContext.getSource()).method_17473().method_20333();
		ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string2);
		if (scoreboardObjective == null) {
			throw field_20194.create(string2);
		} else {
			return scoreboardObjective;
		}
	}

	public static ScoreboardObjective method_18524(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		ScoreboardObjective scoreboardObjective = method_18522(commandContext, string);
		if (scoreboardObjective.method_4848().method_4919()) {
			throw field_20195.create(scoreboardObjective.getName());
		} else {
			return scoreboardObjective;
		}
	}

	public String parse(StringReader stringReader) throws CommandSyntaxException {
		String string = stringReader.readUnquotedString();
		if (string.length() > 16) {
			throw field_20192.create(16);
		} else {
			return string;
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		if (commandContext.getSource() instanceof class_3915) {
			return class_3965.method_17571(((class_3915)commandContext.getSource()).method_17473().method_20333().method_18118(), suggestionsBuilder);
		} else if (commandContext.getSource() instanceof class_3965) {
			class_3965 lv = (class_3965)commandContext.getSource();
			return lv.method_17555(commandContext, suggestionsBuilder);
		} else {
			return Suggestions.empty();
		}
	}

	public Collection<String> getExamples() {
		return field_20193;
	}
}
