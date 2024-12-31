package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4151;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.GenericScoreboardCriteria;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.TranslatableText;

public class TriggerCommand {
	private static final SimpleCommandExceptionType field_21809 = new SimpleCommandExceptionType(new TranslatableText("commands.trigger.failed.unprimed"));
	private static final SimpleCommandExceptionType field_21810 = new SimpleCommandExceptionType(new TranslatableText("commands.trigger.failed.invalid"));

	public static void method_21151(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)CommandManager.method_17529("trigger")
				.then(
					((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("objective", class_4151.method_18520())
								.suggests((commandContext, suggestionsBuilder) -> method_21150((class_3915)commandContext.getSource(), suggestionsBuilder))
								.executes(
									commandContext -> method_21148(
											(class_3915)commandContext.getSource(),
											method_21154(((class_3915)commandContext.getSource()).method_17471(), class_4151.method_18522(commandContext, "objective"))
										)
								))
							.then(
								CommandManager.method_17529("add")
									.then(
										CommandManager.method_17530("value", IntegerArgumentType.integer())
											.executes(
												commandContext -> method_21149(
														(class_3915)commandContext.getSource(),
														method_21154(((class_3915)commandContext.getSource()).method_17471(), class_4151.method_18522(commandContext, "objective")),
														IntegerArgumentType.getInteger(commandContext, "value")
													)
											)
									)
							))
						.then(
							CommandManager.method_17529("set")
								.then(
									CommandManager.method_17530("value", IntegerArgumentType.integer())
										.executes(
											commandContext -> method_21155(
													(class_3915)commandContext.getSource(),
													method_21154(((class_3915)commandContext.getSource()).method_17471(), class_4151.method_18522(commandContext, "objective")),
													IntegerArgumentType.getInteger(commandContext, "value")
												)
										)
								)
						)
				)
		);
	}

	public static CompletableFuture<Suggestions> method_21150(class_3915 arg, SuggestionsBuilder suggestionsBuilder) {
		Entity entity = arg.method_17469();
		List<String> list = Lists.newArrayList();
		if (entity != null) {
			Scoreboard scoreboard = arg.method_17473().method_20333();
			String string = entity.method_15586();

			for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
				if (scoreboardObjective.method_4848() == GenericScoreboardCriteria.TRIGGER && scoreboard.playerHasObjective(string, scoreboardObjective)) {
					ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
					if (!scoreboardPlayerScore.isLocked()) {
						list.add(scoreboardObjective.getName());
					}
				}
			}
		}

		return class_3965.method_17571(list, suggestionsBuilder);
	}

	private static int method_21149(class_3915 arg, ScoreboardPlayerScore scoreboardPlayerScore, int i) {
		scoreboardPlayerScore.incrementScore(i);
		arg.method_17459(new TranslatableText("commands.trigger.add.success", scoreboardPlayerScore.getObjective().method_18090(), i), true);
		return scoreboardPlayerScore.getScore();
	}

	private static int method_21155(class_3915 arg, ScoreboardPlayerScore scoreboardPlayerScore, int i) {
		scoreboardPlayerScore.setScore(i);
		arg.method_17459(new TranslatableText("commands.trigger.set.success", scoreboardPlayerScore.getObjective().method_18090(), i), true);
		return i;
	}

	private static int method_21148(class_3915 arg, ScoreboardPlayerScore scoreboardPlayerScore) {
		scoreboardPlayerScore.incrementScore(1);
		arg.method_17459(new TranslatableText("commands.trigger.simple.success", scoreboardPlayerScore.getObjective().method_18090()), true);
		return scoreboardPlayerScore.getScore();
	}

	private static ScoreboardPlayerScore method_21154(ServerPlayerEntity serverPlayerEntity, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		if (scoreboardObjective.method_4848() != GenericScoreboardCriteria.TRIGGER) {
			throw field_21810.create();
		} else {
			Scoreboard scoreboard = serverPlayerEntity.getScoreboard();
			String string = serverPlayerEntity.method_15586();
			if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
				throw field_21809.create();
			} else {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				if (scoreboardPlayerScore.isLocked()) {
					throw field_21809.create();
				} else {
					scoreboardPlayerScore.setLocked(true);
					return scoreboardPlayerScore;
				}
			}
		}
	}
}
