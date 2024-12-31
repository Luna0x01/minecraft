package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_3915;
import net.minecraft.class_3965;
import net.minecraft.class_4009;
import net.minecraft.class_4151;
import net.minecraft.class_4159;
import net.minecraft.class_4164;
import net.minecraft.class_4186;
import net.minecraft.class_4196;
import net.minecraft.scoreboard.GenericScoreboardCriteria;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;

public class ScoreboardCommand {
	private static final SimpleCommandExceptionType field_21777 = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.objectives.add.duplicate")
	);
	private static final SimpleCommandExceptionType field_21778 = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.objectives.display.alreadyEmpty")
	);
	private static final SimpleCommandExceptionType field_21779 = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.objectives.display.alreadySet")
	);
	private static final SimpleCommandExceptionType field_21780 = new SimpleCommandExceptionType(new TranslatableText("commands.scoreboard.players.enable.failed"));
	private static final SimpleCommandExceptionType field_21781 = new SimpleCommandExceptionType(
		new TranslatableText("commands.scoreboard.players.enable.invalid")
	);
	private static final Dynamic2CommandExceptionType field_21782 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.scoreboard.players.get.null", object, object2)
	);

	public static void method_20962(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("scoreboard").requires(arg -> arg.method_17575(2)))
					.then(
						((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("objectives")
											.then(CommandManager.method_17529("list").executes(commandContext -> method_20966((class_3915)commandContext.getSource()))))
										.then(
											CommandManager.method_17529("add")
												.then(
													CommandManager.method_17530("objective", StringArgumentType.word())
														.then(
															((RequiredArgumentBuilder)CommandManager.method_17530("criteria", class_4159.method_18598())
																	.executes(
																		commandContext -> method_20955(
																				(class_3915)commandContext.getSource(),
																				StringArgumentType.getString(commandContext, "objective"),
																				class_4159.method_18600(commandContext, "criteria"),
																				new LiteralText(StringArgumentType.getString(commandContext, "objective"))
																			)
																	))
																.then(
																	CommandManager.method_17530("displayName", class_4009.method_17711())
																		.executes(
																			commandContext -> method_20955(
																					(class_3915)commandContext.getSource(),
																					StringArgumentType.getString(commandContext, "objective"),
																					class_4159.method_18600(commandContext, "criteria"),
																					class_4009.method_17713(commandContext, "displayName")
																				)
																		)
																)
														)
												)
										))
									.then(
										CommandManager.method_17529("modify")
											.then(
												((RequiredArgumentBuilder)CommandManager.method_17530("objective", class_4151.method_18520())
														.then(
															CommandManager.method_17529("displayname")
																.then(
																	CommandManager.method_17530("displayName", class_4009.method_17711())
																		.executes(
																			commandContext -> method_20952(
																					(class_3915)commandContext.getSource(),
																					class_4151.method_18522(commandContext, "objective"),
																					class_4009.method_17713(commandContext, "displayName")
																				)
																		)
																)
														))
													.then(method_20946())
											)
									))
								.then(
									CommandManager.method_17529("remove")
										.then(
											CommandManager.method_17530("objective", class_4151.method_18520())
												.executes(commandContext -> method_20950((class_3915)commandContext.getSource(), class_4151.method_18522(commandContext, "objective")))
										)
								))
							.then(
								CommandManager.method_17529("setdisplay")
									.then(
										((RequiredArgumentBuilder)CommandManager.method_17530("slot", class_4196.method_18938())
												.executes(commandContext -> method_20948((class_3915)commandContext.getSource(), class_4196.method_18940(commandContext, "slot"))))
											.then(
												CommandManager.method_17530("objective", class_4151.method_18520())
													.executes(
														commandContext -> method_20949(
																(class_3915)commandContext.getSource(), class_4196.method_18940(commandContext, "slot"), class_4151.method_18522(commandContext, "objective")
															)
													)
											)
									)
							)
					))
				.then(
					((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
														"players"
													)
													.then(
														((LiteralArgumentBuilder)CommandManager.method_17529("list").executes(commandContext -> method_20947((class_3915)commandContext.getSource())))
															.then(
																CommandManager.method_17530("target", class_4186.method_18919())
																	.suggests(class_4186.field_20535)
																	.executes(commandContext -> method_20953((class_3915)commandContext.getSource(), class_4186.method_18923(commandContext, "target")))
															)
													))
												.then(
													CommandManager.method_17529("set")
														.then(
															CommandManager.method_17530("targets", class_4186.method_18927())
																.suggests(class_4186.field_20535)
																.then(
																	CommandManager.method_17530("objective", class_4151.method_18520())
																		.then(
																			CommandManager.method_17530("score", IntegerArgumentType.integer())
																				.executes(
																					commandContext -> method_20958(
																							(class_3915)commandContext.getSource(),
																							class_4186.method_18930(commandContext, "targets"),
																							class_4151.method_18524(commandContext, "objective"),
																							IntegerArgumentType.getInteger(commandContext, "score")
																						)
																				)
																		)
																)
														)
												))
											.then(
												CommandManager.method_17529("get")
													.then(
														CommandManager.method_17530("target", class_4186.method_18919())
															.suggests(class_4186.field_20535)
															.then(
																CommandManager.method_17530("objective", class_4151.method_18520())
																	.executes(
																		commandContext -> method_20954(
																				(class_3915)commandContext.getSource(),
																				class_4186.method_18923(commandContext, "target"),
																				class_4151.method_18522(commandContext, "objective")
																			)
																	)
															)
													)
											))
										.then(
											CommandManager.method_17529("add")
												.then(
													CommandManager.method_17530("targets", class_4186.method_18927())
														.suggests(class_4186.field_20535)
														.then(
															CommandManager.method_17530("objective", class_4151.method_18520())
																.then(
																	CommandManager.method_17530("score", IntegerArgumentType.integer(0))
																		.executes(
																			commandContext -> method_20968(
																					(class_3915)commandContext.getSource(),
																					class_4186.method_18930(commandContext, "targets"),
																					class_4151.method_18524(commandContext, "objective"),
																					IntegerArgumentType.getInteger(commandContext, "score")
																				)
																		)
																)
														)
												)
										))
									.then(
										CommandManager.method_17529("remove")
											.then(
												CommandManager.method_17530("targets", class_4186.method_18927())
													.suggests(class_4186.field_20535)
													.then(
														CommandManager.method_17530("objective", class_4151.method_18520())
															.then(
																CommandManager.method_17530("score", IntegerArgumentType.integer(0))
																	.executes(
																		commandContext -> method_20971(
																				(class_3915)commandContext.getSource(),
																				class_4186.method_18930(commandContext, "targets"),
																				class_4151.method_18524(commandContext, "objective"),
																				IntegerArgumentType.getInteger(commandContext, "score")
																			)
																	)
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("reset")
										.then(
											((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4186.method_18927())
													.suggests(class_4186.field_20535)
													.executes(commandContext -> method_20956((class_3915)commandContext.getSource(), class_4186.method_18930(commandContext, "targets"))))
												.then(
													CommandManager.method_17530("objective", class_4151.method_18520())
														.executes(
															commandContext -> method_20967(
																	(class_3915)commandContext.getSource(),
																	class_4186.method_18930(commandContext, "targets"),
																	class_4151.method_18522(commandContext, "objective")
																)
														)
												)
										)
								))
							.then(
								CommandManager.method_17529("enable")
									.then(
										CommandManager.method_17530("targets", class_4186.method_18927())
											.suggests(class_4186.field_20535)
											.then(
												CommandManager.method_17530("objective", class_4151.method_18520())
													.suggests(
														(commandContext, suggestionsBuilder) -> method_20960(
																(class_3915)commandContext.getSource(), class_4186.method_18930(commandContext, "targets"), suggestionsBuilder
															)
													)
													.executes(
														commandContext -> method_20957(
																(class_3915)commandContext.getSource(),
																class_4186.method_18930(commandContext, "targets"),
																class_4151.method_18522(commandContext, "objective")
															)
													)
											)
									)
							))
						.then(
							CommandManager.method_17529("operation")
								.then(
									CommandManager.method_17530("targets", class_4186.method_18927())
										.suggests(class_4186.field_20535)
										.then(
											CommandManager.method_17530("targetObjective", class_4151.method_18520())
												.then(
													CommandManager.method_17530("operation", class_4164.method_18683())
														.then(
															CommandManager.method_17530("source", class_4186.method_18927())
																.suggests(class_4186.field_20535)
																.then(
																	CommandManager.method_17530("sourceObjective", class_4151.method_18520())
																		.executes(
																			commandContext -> method_20959(
																					(class_3915)commandContext.getSource(),
																					class_4186.method_18930(commandContext, "targets"),
																					class_4151.method_18524(commandContext, "targetObjective"),
																					class_4164.method_18687(commandContext, "operation"),
																					class_4186.method_18930(commandContext, "source"),
																					class_4151.method_18522(commandContext, "sourceObjective")
																				)
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static LiteralArgumentBuilder<class_3915> method_20946() {
		LiteralArgumentBuilder<class_3915> literalArgumentBuilder = CommandManager.method_17529("rendertype");

		for (GenericScoreboardCriteria.class_4104 lv : GenericScoreboardCriteria.class_4104.values()) {
			literalArgumentBuilder.then(
				CommandManager.method_17529(lv.method_18132())
					.executes(commandContext -> method_20951((class_3915)commandContext.getSource(), class_4151.method_18522(commandContext, "objective"), lv))
			);
		}

		return literalArgumentBuilder;
	}

	private static CompletableFuture<Suggestions> method_20960(class_3915 arg, Collection<String> collection, SuggestionsBuilder suggestionsBuilder) {
		List<String> list = Lists.newArrayList();
		Scoreboard scoreboard = arg.method_17473().method_20333();

		for (ScoreboardObjective scoreboardObjective : scoreboard.getObjectives()) {
			if (scoreboardObjective.method_4848() == GenericScoreboardCriteria.TRIGGER) {
				boolean bl = false;

				for (String string : collection) {
					if (!scoreboard.playerHasObjective(string, scoreboardObjective) || scoreboard.getPlayerScore(string, scoreboardObjective).isLocked()) {
						bl = true;
						break;
					}
				}

				if (bl) {
					list.add(scoreboardObjective.getName());
				}
			}
		}

		return class_3965.method_17571(list, suggestionsBuilder);
	}

	private static int method_20954(class_3915 arg, String string, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		if (!scoreboard.playerHasObjective(string, scoreboardObjective)) {
			throw field_21782.create(scoreboardObjective.getName(), string);
		} else {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			arg.method_17459(
				new TranslatableText("commands.scoreboard.players.get.success", string, scoreboardPlayerScore.getScore(), scoreboardObjective.method_18090()), false
			);
			return scoreboardPlayerScore.getScore();
		}
	}

	private static int method_20959(
		class_3915 arg,
		Collection<String> collection,
		ScoreboardObjective scoreboardObjective,
		class_4164.class_4165 arg2,
		Collection<String> collection2,
		ScoreboardObjective scoreboardObjective2
	) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		int i = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);

			for (String string2 : collection2) {
				ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
				arg2.apply(scoreboardPlayerScore, scoreboardPlayerScore2);
			}

			i += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.scoreboard.players.operation.success.single", scoreboardObjective.method_18090(), collection.iterator().next(), i), true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.operation.success.multiple", scoreboardObjective.method_18090(), collection.size()), true);
		}

		return i;
	}

	private static int method_20957(class_3915 arg, Collection<String> collection, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		if (scoreboardObjective.method_4848() != GenericScoreboardCriteria.TRIGGER) {
			throw field_21781.create();
		} else {
			Scoreboard scoreboard = arg.method_17473().method_20333();
			int i = 0;

			for (String string : collection) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				if (scoreboardPlayerScore.isLocked()) {
					scoreboardPlayerScore.setLocked(false);
					i++;
				}
			}

			if (i == 0) {
				throw field_21780.create();
			} else {
				if (collection.size() == 1) {
					arg.method_17459(
						new TranslatableText("commands.scoreboard.players.enable.success.single", scoreboardObjective.method_18090(), collection.iterator().next()), true
					);
				} else {
					arg.method_17459(new TranslatableText("commands.scoreboard.players.enable.success.multiple", scoreboardObjective.method_18090(), collection.size()), true);
				}

				return i;
			}
		}
	}

	private static int method_20956(class_3915 arg, Collection<String> collection) {
		Scoreboard scoreboard = arg.method_17473().method_20333();

		for (String string : collection) {
			scoreboard.resetPlayerScore(string, null);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.reset.all.single", collection.iterator().next()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.reset.all.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_20967(class_3915 arg, Collection<String> collection, ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = arg.method_17473().method_20333();

		for (String string : collection) {
			scoreboard.resetPlayerScore(string, scoreboardObjective);
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.scoreboard.players.reset.specific.single", scoreboardObjective.method_18090(), collection.iterator().next()), true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.reset.specific.multiple", scoreboardObjective.method_18090(), collection.size()), true);
		}

		return collection.size();
	}

	private static int method_20958(class_3915 arg, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = arg.method_17473().method_20333();

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(i);
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.scoreboard.players.set.success.single", scoreboardObjective.method_18090(), collection.iterator().next(), i), true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.set.success.multiple", scoreboardObjective.method_18090(), collection.size(), i), true);
		}

		return i * collection.size();
	}

	private static int method_20968(class_3915 arg, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		int j = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() + i);
			j += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.scoreboard.players.add.success.single", i, scoreboardObjective.method_18090(), collection.iterator().next(), j), true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.add.success.multiple", i, scoreboardObjective.method_18090(), collection.size()), true);
		}

		return j;
	}

	private static int method_20971(class_3915 arg, Collection<String> collection, ScoreboardObjective scoreboardObjective, int i) {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		int j = 0;

		for (String string : collection) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			scoreboardPlayerScore.setScore(scoreboardPlayerScore.getScore() - i);
			j += scoreboardPlayerScore.getScore();
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.scoreboard.players.remove.success.single", i, scoreboardObjective.method_18090(), collection.iterator().next(), j), true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.remove.success.multiple", i, scoreboardObjective.method_18090(), collection.size()), true);
		}

		return j;
	}

	private static int method_20947(class_3915 arg) {
		Collection<String> collection = arg.method_17473().method_20333().getKnownPlayers();
		if (collection.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.list.empty"), false);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.list.success", collection.size(), ChatSerializer.method_20191(collection)), false);
		}

		return collection.size();
	}

	private static int method_20953(class_3915 arg, String string) {
		Map<ScoreboardObjective, ScoreboardPlayerScore> map = arg.method_17473().method_20333().getPlayerObjectives(string);
		if (map.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.list.entity.empty", string), false);
		} else {
			arg.method_17459(new TranslatableText("commands.scoreboard.players.list.entity.success", string, map.size()), false);

			for (Entry<ScoreboardObjective, ScoreboardPlayerScore> entry : map.entrySet()) {
				arg.method_17459(
					new TranslatableText(
						"commands.scoreboard.players.list.entity.entry",
						((ScoreboardObjective)entry.getKey()).method_18090(),
						((ScoreboardPlayerScore)entry.getValue()).getScore()
					),
					false
				);
			}
		}

		return map.size();
	}

	private static int method_20948(class_3915 arg, int i) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		if (scoreboard.getObjectiveForSlot(i) == null) {
			throw field_21778.create();
		} else {
			scoreboard.setObjectiveSlot(i, null);
			arg.method_17459(new TranslatableText("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[i]), true);
			return 0;
		}
	}

	private static int method_20949(class_3915 arg, int i, ScoreboardObjective scoreboardObjective) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		if (scoreboard.getObjectiveForSlot(i) == scoreboardObjective) {
			throw field_21779.create();
		} else {
			scoreboard.setObjectiveSlot(i, scoreboardObjective);
			arg.method_17459(
				new TranslatableText("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[i], scoreboardObjective.method_4849()), true
			);
			return 0;
		}
	}

	private static int method_20952(class_3915 arg, ScoreboardObjective scoreboardObjective, Text text) {
		if (!scoreboardObjective.method_4849().equals(text)) {
			scoreboardObjective.method_18088(text);
			arg.method_17459(
				new TranslatableText("commands.scoreboard.objectives.modify.displayname", scoreboardObjective.getName(), scoreboardObjective.method_18090()), true
			);
		}

		return 0;
	}

	private static int method_20951(class_3915 arg, ScoreboardObjective scoreboardObjective, GenericScoreboardCriteria.class_4104 arg2) {
		if (scoreboardObjective.method_9351() != arg2) {
			scoreboardObjective.method_9350(arg2);
			arg.method_17459(new TranslatableText("commands.scoreboard.objectives.modify.rendertype", scoreboardObjective.method_18090()), true);
		}

		return 0;
	}

	private static int method_20950(class_3915 arg, ScoreboardObjective scoreboardObjective) {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		scoreboard.removeObjective(scoreboardObjective);
		arg.method_17459(new TranslatableText("commands.scoreboard.objectives.remove.success", scoreboardObjective.method_18090()), true);
		return scoreboard.getObjectives().size();
	}

	private static int method_20955(class_3915 arg, String string, GenericScoreboardCriteria genericScoreboardCriteria, Text text) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		if (scoreboard.getNullableObjective(string) != null) {
			throw field_21777.create();
		} else if (string.length() > 16) {
			throw class_4151.field_20192.create(16);
		} else {
			scoreboard.method_18113(string, genericScoreboardCriteria, text, genericScoreboardCriteria.method_18131());
			ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(string);
			arg.method_17459(new TranslatableText("commands.scoreboard.objectives.add.success", scoreboardObjective.method_18090()), true);
			return scoreboard.getObjectives().size();
		}
	}

	private static int method_20966(class_3915 arg) {
		Collection<ScoreboardObjective> collection = arg.method_17473().method_20333().getObjectives();
		if (collection.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.scoreboard.objectives.list.empty"), false);
		} else {
			arg.method_17459(
				new TranslatableText(
					"commands.scoreboard.objectives.list.success", collection.size(), ChatSerializer.method_20193(collection, ScoreboardObjective::method_18090)
				),
				false
			);
		}

		return collection.size();
	}
}
