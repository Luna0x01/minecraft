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
import java.util.concurrent.CompletableFuture;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class class_4164 implements ArgumentType<class_4164.class_4165> {
	private static final Collection<String> field_20383 = Arrays.asList("=", ">", "<");
	private static final SimpleCommandExceptionType field_20384 = new SimpleCommandExceptionType(new TranslatableText("arguments.operation.invalid"));
	private static final SimpleCommandExceptionType field_20385 = new SimpleCommandExceptionType(new TranslatableText("arguments.operation.div0"));

	public static class_4164 method_18683() {
		return new class_4164();
	}

	public static class_4164.class_4165 method_18687(CommandContext<class_3915> commandContext, String string) throws CommandSyntaxException {
		return (class_4164.class_4165)commandContext.getArgument(string, class_4164.class_4165.class);
	}

	public class_4164.class_4165 parse(StringReader stringReader) throws CommandSyntaxException {
		if (!stringReader.canRead()) {
			throw field_20384.create();
		} else {
			int i = stringReader.getCursor();

			while (stringReader.canRead() && stringReader.peek() != ' ') {
				stringReader.skip();
			}

			return method_18688(stringReader.getString().substring(i, stringReader.getCursor()));
		}
	}

	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return class_3965.method_17570(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, suggestionsBuilder);
	}

	public Collection<String> getExamples() {
		return field_20383;
	}

	private static class_4164.class_4165 method_18688(String string) throws CommandSyntaxException {
		return (class_4164.class_4165)(string.equals("><") ? (scoreboardPlayerScore, scoreboardPlayerScore2) -> {
			int i = scoreboardPlayerScore.getScore();
			scoreboardPlayerScore.setScore(scoreboardPlayerScore2.getScore());
			scoreboardPlayerScore2.setScore(i);
		} : method_18690(string));
	}

	private static class_4164.class_4166 method_18690(String string) throws CommandSyntaxException {
		switch (string) {
			case "=":
				return (i, j) -> j;
			case "+=":
				return (i, j) -> i + j;
			case "-=":
				return (i, j) -> i - j;
			case "*=":
				return (i, j) -> i * j;
			case "/=":
				return (i, j) -> {
					if (j == 0) {
						throw field_20385.create();
					} else {
						return MathHelper.floorDiv(i, j);
					}
				};
			case "%=":
				return (i, j) -> {
					if (j == 0) {
						throw field_20385.create();
					} else {
						return MathHelper.floorMod(i, j);
					}
				};
			case "<":
				return Math::min;
			case ">":
				return Math::max;
			default:
				throw field_20384.create();
		}
	}

	@FunctionalInterface
	public interface class_4165 {
		void apply(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface class_4166 extends class_4164.class_4165 {
		int apply(int i, int j) throws CommandSyntaxException;

		@Override
		default void apply(ScoreboardPlayerScore scoreboardPlayerScore, ScoreboardPlayerScore scoreboardPlayerScore2) throws CommandSyntaxException {
			scoreboardPlayerScore.setScore(this.apply(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore()));
		}
	}
}
