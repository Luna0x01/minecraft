package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.ColorArgumentType;
import net.minecraft.command.arguments.ScoreHolderArgumentType;
import net.minecraft.command.arguments.TeamArgumentType;
import net.minecraft.command.arguments.TextArgumentType;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class TeamCommand {
	private static final SimpleCommandExceptionType ADD_DUPLICATE_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.team.add.duplicate"));
	private static final DynamicCommandExceptionType ADD_LONGNAME_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.team.add.longName", object)
	);
	private static final SimpleCommandExceptionType EMPTY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.empty.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_NAME_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.name.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_COLOR_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.color.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.friendlyfire.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.friendlyfire.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.seeFriendlyInvisibles.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.seeFriendlyInvisibles.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.nametagVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.deathMessageVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.collisionRule.unchanged")
	);

	public static void register(CommandDispatcher<ServerCommandSource> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(
												"team"
											)
											.requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)))
										.then(
											((LiteralArgumentBuilder)CommandManager.literal("list")
													.executes(commandContext -> executeListTeams((ServerCommandSource)commandContext.getSource())))
												.then(
													CommandManager.argument("team", TeamArgumentType.team())
														.executes(commandContext -> executeListMembers((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team")))
												)
										))
									.then(
										CommandManager.literal("add")
											.then(
												((RequiredArgumentBuilder)CommandManager.argument("team", StringArgumentType.word())
														.executes(commandContext -> executeAdd((ServerCommandSource)commandContext.getSource(), StringArgumentType.getString(commandContext, "team"))))
													.then(
														CommandManager.argument("displayName", TextArgumentType.text())
															.executes(
																commandContext -> executeAdd(
																		(ServerCommandSource)commandContext.getSource(),
																		StringArgumentType.getString(commandContext, "team"),
																		TextArgumentType.getTextArgument(commandContext, "displayName")
																	)
															)
													)
											)
									))
								.then(
									CommandManager.literal("remove")
										.then(
											CommandManager.argument("team", TeamArgumentType.team())
												.executes(commandContext -> executeRemove((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team")))
										)
								))
							.then(
								CommandManager.literal("empty")
									.then(
										CommandManager.argument("team", TeamArgumentType.team())
											.executes(commandContext -> executeEmpty((ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team")))
									)
							))
						.then(
							CommandManager.literal("join")
								.then(
									((RequiredArgumentBuilder)CommandManager.argument("team", TeamArgumentType.team())
											.executes(
												commandContext -> executeJoin(
														(ServerCommandSource)commandContext.getSource(),
														TeamArgumentType.getTeam(commandContext, "team"),
														Collections.singleton(((ServerCommandSource)commandContext.getSource()).getEntityOrThrow().getEntityName())
													)
											))
										.then(
											CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders())
												.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
												.executes(
													commandContext -> executeJoin(
															(ServerCommandSource)commandContext.getSource(),
															TeamArgumentType.getTeam(commandContext, "team"),
															ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "members")
														)
												)
										)
								)
						))
					.then(
						CommandManager.literal("leave")
							.then(
								CommandManager.argument("members", ScoreHolderArgumentType.scoreHolders())
									.suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER)
									.executes(
										commandContext -> executeLeave(
												(ServerCommandSource)commandContext.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(commandContext, "members")
											)
									)
							)
					))
				.then(
					CommandManager.literal("modify")
						.then(
							((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument(
																	"team", TeamArgumentType.team()
																)
																.then(
																	CommandManager.literal("displayName")
																		.then(
																			CommandManager.argument("displayName", TextArgumentType.text())
																				.executes(
																					commandContext -> executeModifyDisplayName(
																							(ServerCommandSource)commandContext.getSource(),
																							TeamArgumentType.getTeam(commandContext, "team"),
																							TextArgumentType.getTextArgument(commandContext, "displayName")
																						)
																				)
																		)
																))
															.then(
																CommandManager.literal("color")
																	.then(
																		CommandManager.argument("value", ColorArgumentType.color())
																			.executes(
																				commandContext -> executeModifyColor(
																						(ServerCommandSource)commandContext.getSource(),
																						TeamArgumentType.getTeam(commandContext, "team"),
																						ColorArgumentType.getColor(commandContext, "value")
																					)
																			)
																	)
															))
														.then(
															CommandManager.literal("friendlyFire")
																.then(
																	CommandManager.argument("allowed", BoolArgumentType.bool())
																		.executes(
																			commandContext -> executeModifyFriendlyFire(
																					(ServerCommandSource)commandContext.getSource(),
																					TeamArgumentType.getTeam(commandContext, "team"),
																					BoolArgumentType.getBool(commandContext, "allowed")
																				)
																		)
																)
														))
													.then(
														CommandManager.literal("seeFriendlyInvisibles")
															.then(
																CommandManager.argument("allowed", BoolArgumentType.bool())
																	.executes(
																		commandContext -> executeModifySeeFriendlyInvisibles(
																				(ServerCommandSource)commandContext.getSource(),
																				TeamArgumentType.getTeam(commandContext, "team"),
																				BoolArgumentType.getBool(commandContext, "allowed")
																			)
																	)
															)
													))
												.then(
													((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("nametagVisibility")
																	.then(
																		CommandManager.literal("never")
																			.executes(
																				commandContext -> executeModifyNametagVisibility(
																						(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1443
																					)
																			)
																	))
																.then(
																	CommandManager.literal("hideForOtherTeams")
																		.executes(
																			commandContext -> executeModifyNametagVisibility(
																					(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1444
																				)
																		)
																))
															.then(
																CommandManager.literal("hideForOwnTeam")
																	.executes(
																		commandContext -> executeModifyNametagVisibility(
																				(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1446
																			)
																	)
															))
														.then(
															CommandManager.literal("always")
																.executes(
																	commandContext -> executeModifyNametagVisibility(
																			(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1442
																		)
																)
														)
												))
											.then(
												((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("deathMessageVisibility")
																.then(
																	CommandManager.literal("never")
																		.executes(
																			commandContext -> executeModifyDeathMessageVisibility(
																					(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1443
																				)
																		)
																))
															.then(
																CommandManager.literal("hideForOtherTeams")
																	.executes(
																		commandContext -> executeModifyDeathMessageVisibility(
																				(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1444
																			)
																	)
															))
														.then(
															CommandManager.literal("hideForOwnTeam")
																.executes(
																	commandContext -> executeModifyDeathMessageVisibility(
																			(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1446
																		)
																)
														))
													.then(
														CommandManager.literal("always")
															.executes(
																commandContext -> executeModifyDeathMessageVisibility(
																		(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.VisibilityRule.field_1442
																	)
															)
													)
											))
										.then(
											((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("collisionRule")
															.then(
																CommandManager.literal("never")
																	.executes(
																		commandContext -> executeModifyCollisionRule(
																				(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1435
																			)
																	)
															))
														.then(
															CommandManager.literal("pushOwnTeam")
																.executes(
																	commandContext -> executeModifyCollisionRule(
																			(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1440
																		)
																)
														))
													.then(
														CommandManager.literal("pushOtherTeams")
															.executes(
																commandContext -> executeModifyCollisionRule(
																		(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1434
																	)
															)
													))
												.then(
													CommandManager.literal("always")
														.executes(
															commandContext -> executeModifyCollisionRule(
																	(ServerCommandSource)commandContext.getSource(), TeamArgumentType.getTeam(commandContext, "team"), AbstractTeam.CollisionRule.field_1437
																)
														)
												)
										))
									.then(
										CommandManager.literal("prefix")
											.then(
												CommandManager.argument("prefix", TextArgumentType.text())
													.executes(
														commandContext -> executeModifyPrefix(
																(ServerCommandSource)commandContext.getSource(),
																TeamArgumentType.getTeam(commandContext, "team"),
																TextArgumentType.getTextArgument(commandContext, "prefix")
															)
													)
											)
									))
								.then(
									CommandManager.literal("suffix")
										.then(
											CommandManager.argument("suffix", TextArgumentType.text())
												.executes(
													commandContext -> executeModifySuffix(
															(ServerCommandSource)commandContext.getSource(),
															TeamArgumentType.getTeam(commandContext, "team"),
															TextArgumentType.getTextArgument(commandContext, "suffix")
														)
												)
										)
								)
						)
				)
		);
	}

	private static int executeLeave(ServerCommandSource serverCommandSource, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (String string : collection) {
			scoreboard.clearPlayerTeam(string);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.leave.success.single", collection.iterator().next()), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.leave.success.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int executeJoin(ServerCommandSource serverCommandSource, Team team, Collection<String> collection) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();

		for (String string : collection) {
			scoreboard.addPlayerToTeam(string, team);
		}

		if (collection.size() == 1) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.join.success.single", collection.iterator().next(), team.getFormattedName()), true);
		} else {
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.join.success.multiple", collection.size(), team.getFormattedName()), true);
		}

		return collection.size();
	}

	private static int executeModifyNametagVisibility(ServerCommandSource serverCommandSource, Team team, AbstractTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (team.getNameTagVisibilityRule() == visibilityRule) {
			throw OPTION_NAMETAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			team.setNameTagVisibilityRule(visibilityRule);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.team.option.nametagVisibility.success", team.getFormattedName(), visibilityRule.getTranslationKey()), true
			);
			return 0;
		}
	}

	private static int executeModifyDeathMessageVisibility(ServerCommandSource serverCommandSource, Team team, AbstractTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (team.getDeathMessageVisibilityRule() == visibilityRule) {
			throw OPTION_DEATHMESSAGEVISIBILITY_UNCHANGED_EXCEPTION.create();
		} else {
			team.setDeathMessageVisibilityRule(visibilityRule);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.team.option.deathMessageVisibility.success", team.getFormattedName(), visibilityRule.getTranslationKey()), true
			);
			return 0;
		}
	}

	private static int executeModifyCollisionRule(ServerCommandSource serverCommandSource, Team team, AbstractTeam.CollisionRule collisionRule) throws CommandSyntaxException {
		if (team.getCollisionRule() == collisionRule) {
			throw OPTION_COLLISIONRULE_UNCHANGED_EXCEPTION.create();
		} else {
			team.setCollisionRule(collisionRule);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.team.option.collisionRule.success", team.getFormattedName(), collisionRule.getTranslationKey()), true
			);
			return 0;
		}
	}

	private static int executeModifySeeFriendlyInvisibles(ServerCommandSource serverCommandSource, Team team, boolean bl) throws CommandSyntaxException {
		if (team.shouldShowFriendlyInvisibles() == bl) {
			if (bl) {
				throw OPTION_SEEFRIENDLYINVISIBLES_ALREADYENABLED_EXCEPTION.create();
			} else {
				throw SEEFRIENDLYINVISIBLES_ALREADYDSISABLED_EXCEPTION.create();
			}
		} else {
			team.setShowFriendlyInvisibles(bl);
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), team.getFormattedName()), true
			);
			return 0;
		}
	}

	private static int executeModifyFriendlyFire(ServerCommandSource serverCommandSource, Team team, boolean bl) throws CommandSyntaxException {
		if (team.isFriendlyFireAllowed() == bl) {
			if (bl) {
				throw OPTION_FRIENDLYFIRE_ALREADYENABLED_EXCEPTION.create();
			} else {
				throw OPTION_FRIENDLYFIRE_ALREADYDISABLED_EXCEPTION.create();
			}
		} else {
			team.setFriendlyFireAllowed(bl);
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), team.getFormattedName()), true);
			return 0;
		}
	}

	private static int executeModifyDisplayName(ServerCommandSource serverCommandSource, Team team, Text text) throws CommandSyntaxException {
		if (team.getDisplayName().equals(text)) {
			throw OPTION_NAME_UNCHANGED_EXCEPTION.create();
		} else {
			team.setDisplayName(text);
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.option.name.success", team.getFormattedName()), true);
			return 0;
		}
	}

	private static int executeModifyColor(ServerCommandSource serverCommandSource, Team team, Formatting formatting) throws CommandSyntaxException {
		if (team.getColor() == formatting) {
			throw OPTION_COLOR_UNCHANGED_EXCEPTION.create();
		} else {
			team.setColor(formatting);
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.option.color.success", team.getFormattedName(), formatting.getName()), true);
			return 0;
		}
	}

	private static int executeEmpty(ServerCommandSource serverCommandSource, Team team) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		Collection<String> collection = Lists.newArrayList(team.getPlayerList());
		if (collection.isEmpty()) {
			throw EMPTY_UNCHANGED_EXCEPTION.create();
		} else {
			for (String string : collection) {
				scoreboard.removePlayerFromTeam(string, team);
			}

			serverCommandSource.sendFeedback(new TranslatableText("commands.team.empty.success", collection.size(), team.getFormattedName()), true);
			return collection.size();
		}
	}

	private static int executeRemove(ServerCommandSource serverCommandSource, Team team) {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		scoreboard.removeTeam(team);
		serverCommandSource.sendFeedback(new TranslatableText("commands.team.remove.success", team.getFormattedName()), true);
		return scoreboard.getTeams().size();
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, String string) throws CommandSyntaxException {
		return executeAdd(serverCommandSource, string, new LiteralText(string));
	}

	private static int executeAdd(ServerCommandSource serverCommandSource, String string, Text text) throws CommandSyntaxException {
		Scoreboard scoreboard = serverCommandSource.getMinecraftServer().getScoreboard();
		if (scoreboard.getTeam(string) != null) {
			throw ADD_DUPLICATE_EXCEPTION.create();
		} else if (string.length() > 16) {
			throw ADD_LONGNAME_EXCEPTION.create(16);
		} else {
			Team team = scoreboard.addTeam(string);
			team.setDisplayName(text);
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.add.success", team.getFormattedName()), true);
			return scoreboard.getTeams().size();
		}
	}

	private static int executeListMembers(ServerCommandSource serverCommandSource, Team team) {
		Collection<String> collection = team.getPlayerList();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.list.members.empty", team.getFormattedName()), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.team.list.members.success", team.getFormattedName(), collection.size(), Texts.joinOrdered(collection)), false
			);
		}

		return collection.size();
	}

	private static int executeListTeams(ServerCommandSource serverCommandSource) {
		Collection<Team> collection = serverCommandSource.getMinecraftServer().getScoreboard().getTeams();
		if (collection.isEmpty()) {
			serverCommandSource.sendFeedback(new TranslatableText("commands.team.list.teams.empty"), false);
		} else {
			serverCommandSource.sendFeedback(
				new TranslatableText("commands.team.list.teams.success", collection.size(), Texts.join(collection, Team::getFormattedName)), false
			);
		}

		return collection.size();
	}

	private static int executeModifyPrefix(ServerCommandSource serverCommandSource, Team team, Text text) {
		team.setPrefix(text);
		serverCommandSource.sendFeedback(new TranslatableText("commands.team.option.prefix.success", text), false);
		return 1;
	}

	private static int executeModifySuffix(ServerCommandSource serverCommandSource, Team team, Text text) {
		team.setSuffix(text);
		serverCommandSource.sendFeedback(new TranslatableText("commands.team.option.suffix.success", text), false);
		return 1;
	}
}
