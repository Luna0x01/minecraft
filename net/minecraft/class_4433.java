package net.minecraft;

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
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Formatting;

public class class_4433 {
	private static final SimpleCommandExceptionType field_21794 = new SimpleCommandExceptionType(new TranslatableText("commands.team.add.duplicate"));
	private static final DynamicCommandExceptionType field_21795 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.team.add.longName", object)
	);
	private static final SimpleCommandExceptionType field_21796 = new SimpleCommandExceptionType(new TranslatableText("commands.team.empty.unchanged"));
	private static final SimpleCommandExceptionType field_21797 = new SimpleCommandExceptionType(new TranslatableText("commands.team.option.name.unchanged"));
	private static final SimpleCommandExceptionType field_21798 = new SimpleCommandExceptionType(new TranslatableText("commands.team.option.color.unchanged"));
	private static final SimpleCommandExceptionType field_21799 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.friendlyfire.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType field_21800 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.friendlyfire.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType field_21801 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.seeFriendlyInvisibles.alreadyEnabled")
	);
	private static final SimpleCommandExceptionType field_21802 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.seeFriendlyInvisibles.alreadyDisabled")
	);
	private static final SimpleCommandExceptionType field_21803 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.nametagVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType field_21804 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.deathMessageVisibility.unchanged")
	);
	private static final SimpleCommandExceptionType field_21805 = new SimpleCommandExceptionType(
		new TranslatableText("commands.team.option.collisionRule.unchanged")
	);

	public static void method_21068(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
												"team"
											)
											.requires(arg -> arg.method_17575(2)))
										.then(
											((LiteralArgumentBuilder)CommandManager.method_17529("list").executes(commandContext -> method_21057((class_3915)commandContext.getSource())))
												.then(
													CommandManager.method_17530("team", class_4209.method_18997())
														.executes(commandContext -> method_21077((class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team")))
												)
										))
									.then(
										CommandManager.method_17529("add")
											.then(
												((RequiredArgumentBuilder)CommandManager.method_17530("team", StringArgumentType.word())
														.executes(commandContext -> method_21065((class_3915)commandContext.getSource(), StringArgumentType.getString(commandContext, "team"))))
													.then(
														CommandManager.method_17530("displayName", class_4009.method_17711())
															.executes(
																commandContext -> method_21066(
																		(class_3915)commandContext.getSource(),
																		StringArgumentType.getString(commandContext, "team"),
																		class_4009.method_17713(commandContext, "displayName")
																	)
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("remove")
										.then(
											CommandManager.method_17530("team", class_4209.method_18997())
												.executes(commandContext -> method_21072((class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team")))
										)
								))
							.then(
								CommandManager.method_17529("empty")
									.then(
										CommandManager.method_17530("team", class_4209.method_18997())
											.executes(commandContext -> method_21058((class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team")))
									)
							))
						.then(
							CommandManager.method_17529("join")
								.then(
									((RequiredArgumentBuilder)CommandManager.method_17530("team", class_4209.method_18997())
											.executes(
												commandContext -> method_21063(
														(class_3915)commandContext.getSource(),
														class_4209.method_18999(commandContext, "team"),
														Collections.singleton(((class_3915)commandContext.getSource()).method_17470().method_15586())
													)
											))
										.then(
											CommandManager.method_17530("members", class_4186.method_18927())
												.suggests(class_4186.field_20535)
												.executes(
													commandContext -> method_21063(
															(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), class_4186.method_18930(commandContext, "members")
														)
												)
										)
								)
						))
					.then(
						CommandManager.method_17529("leave")
							.then(
								CommandManager.method_17530("members", class_4186.method_18927())
									.suggests(class_4186.field_20535)
									.executes(commandContext -> method_21067((class_3915)commandContext.getSource(), class_4186.method_18930(commandContext, "members")))
							)
					))
				.then(
					CommandManager.method_17529("modify")
						.then(
							((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
																	"team", class_4209.method_18997()
																)
																.then(
																	CommandManager.method_17529("displayName")
																		.then(
																			CommandManager.method_17530("displayName", class_4009.method_17711())
																				.executes(
																					commandContext -> method_21062(
																							(class_3915)commandContext.getSource(),
																							class_4209.method_18999(commandContext, "team"),
																							class_4009.method_17713(commandContext, "displayName")
																						)
																				)
																		)
																))
															.then(
																CommandManager.method_17529("color")
																	.then(
																		CommandManager.method_17530("value", class_3991.method_17647())
																			.executes(
																				commandContext -> method_21059(
																						(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), class_3991.method_17649(commandContext, "value")
																					)
																			)
																	)
															))
														.then(
															CommandManager.method_17529("friendlyFire")
																.then(
																	CommandManager.method_17530("allowed", BoolArgumentType.bool())
																		.executes(
																			commandContext -> method_21075(
																					(class_3915)commandContext.getSource(),
																					class_4209.method_18999(commandContext, "team"),
																					BoolArgumentType.getBool(commandContext, "allowed")
																				)
																		)
																)
														))
													.then(
														CommandManager.method_17529("seeFriendlyInvisibles")
															.then(
																CommandManager.method_17530("allowed", BoolArgumentType.bool())
																	.executes(
																		commandContext -> method_21064(
																				(class_3915)commandContext.getSource(),
																				class_4209.method_18999(commandContext, "team"),
																				BoolArgumentType.getBool(commandContext, "allowed")
																			)
																	)
															)
													))
												.then(
													((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("nametagVisibility")
																	.then(
																		CommandManager.method_17529("never")
																			.executes(
																				commandContext -> method_21061(
																						(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.NEVER
																					)
																			)
																	))
																.then(
																	CommandManager.method_17529("hideForOtherTeams")
																		.executes(
																			commandContext -> method_21061(
																					(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS
																				)
																		)
																))
															.then(
																CommandManager.method_17529("hideForOwnTeam")
																	.executes(
																		commandContext -> method_21061(
																				(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM
																			)
																	)
															))
														.then(
															CommandManager.method_17529("always")
																.executes(
																	commandContext -> method_21061(
																			(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS
																		)
																)
														)
												))
											.then(
												((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("deathMessageVisibility")
																.then(
																	CommandManager.method_17529("never")
																		.executes(
																			commandContext -> method_21073(
																					(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.NEVER
																				)
																		)
																))
															.then(
																CommandManager.method_17529("hideForOtherTeams")
																	.executes(
																		commandContext -> method_21073(
																				(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS
																			)
																	)
															))
														.then(
															CommandManager.method_17529("hideForOwnTeam")
																.executes(
																	commandContext -> method_21073(
																			(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM
																		)
																)
														))
													.then(
														CommandManager.method_17529("always")
															.executes(
																commandContext -> method_21073(
																		(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.VisibilityRule.ALWAYS
																	)
															)
													)
											))
										.then(
											((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("collisionRule")
															.then(
																CommandManager.method_17529("never")
																	.executes(
																		commandContext -> method_21060(
																				(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.CollisionRule.NEVER
																			)
																	)
															))
														.then(
															CommandManager.method_17529("pushOwnTeam")
																.executes(
																	commandContext -> method_21060(
																			(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.CollisionRule.PUSH_OWN_TEAM
																		)
																)
														))
													.then(
														CommandManager.method_17529("pushOtherTeams")
															.executes(
																commandContext -> method_21060(
																		(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS
																	)
															)
													))
												.then(
													CommandManager.method_17529("always")
														.executes(
															commandContext -> method_21060(
																	(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), AbstractTeam.CollisionRule.ALWAYS
																)
														)
												)
										))
									.then(
										CommandManager.method_17529("prefix")
											.then(
												CommandManager.method_17530("prefix", class_4009.method_17711())
													.executes(
														commandContext -> method_21074(
																(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), class_4009.method_17713(commandContext, "prefix")
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("suffix")
										.then(
											CommandManager.method_17530("suffix", class_4009.method_17711())
												.executes(
													commandContext -> method_21078(
															(class_3915)commandContext.getSource(), class_4209.method_18999(commandContext, "team"), class_4009.method_17713(commandContext, "suffix")
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_21067(class_3915 arg, Collection<String> collection) {
		Scoreboard scoreboard = arg.method_17473().method_20333();

		for (String string : collection) {
			scoreboard.clearPlayerTeam(string);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.team.leave.success.single", collection.iterator().next()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.team.leave.success.multiple", collection.size()), true);
		}

		return collection.size();
	}

	private static int method_21063(class_3915 arg, Team team, Collection<String> collection) {
		Scoreboard scoreboard = arg.method_17473().method_20333();

		for (String string : collection) {
			scoreboard.method_6614(string, team);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.team.join.success.single", collection.iterator().next(), team.method_18103()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.team.join.success.multiple", collection.size(), team.method_18103()), true);
		}

		return collection.size();
	}

	private static int method_21061(class_3915 arg, Team team, AbstractTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (team.getNameTagVisibilityRule() == visibilityRule) {
			throw field_21803.create();
		} else {
			team.method_12128(visibilityRule);
			arg.method_17459(new TranslatableText("commands.team.option.nametagVisibility.success", team.method_18103(), visibilityRule.method_18127()), true);
			return 0;
		}
	}

	private static int method_21073(class_3915 arg, Team team, AbstractTeam.VisibilityRule visibilityRule) throws CommandSyntaxException {
		if (team.getDeathMessageVisibilityRule() == visibilityRule) {
			throw field_21804.create();
		} else {
			team.setDeathMessageVisibilityRule(visibilityRule);
			arg.method_17459(new TranslatableText("commands.team.option.deathMessageVisibility.success", team.method_18103(), visibilityRule.method_18127()), true);
			return 0;
		}
	}

	private static int method_21060(class_3915 arg, Team team, AbstractTeam.CollisionRule collisionRule) throws CommandSyntaxException {
		if (team.method_12129() == collisionRule) {
			throw field_21805.create();
		} else {
			team.method_9353(collisionRule);
			arg.method_17459(new TranslatableText("commands.team.option.collisionRule.success", team.method_18103(), collisionRule.method_18124()), true);
			return 0;
		}
	}

	private static int method_21064(class_3915 arg, Team team, boolean bl) throws CommandSyntaxException {
		if (team.shouldShowFriendlyInvisibles() == bl) {
			if (bl) {
				throw field_21801.create();
			} else {
				throw field_21802.create();
			}
		} else {
			team.setShowFriendlyInvisibles(bl);
			arg.method_17459(new TranslatableText("commands.team.option.seeFriendlyInvisibles." + (bl ? "enabled" : "disabled"), team.method_18103()), true);
			return 0;
		}
	}

	private static int method_21075(class_3915 arg, Team team, boolean bl) throws CommandSyntaxException {
		if (team.isFriendlyFireAllowed() == bl) {
			if (bl) {
				throw field_21799.create();
			} else {
				throw field_21800.create();
			}
		} else {
			team.setFriendlyFireAllowed(bl);
			arg.method_17459(new TranslatableText("commands.team.option.friendlyfire." + (bl ? "enabled" : "disabled"), team.method_18103()), true);
			return 0;
		}
	}

	private static int method_21062(class_3915 arg, Team team, Text text) throws CommandSyntaxException {
		if (team.method_18101().equals(text)) {
			throw field_21797.create();
		} else {
			team.method_18098(text);
			arg.method_17459(new TranslatableText("commands.team.option.name.success", team.method_18103()), true);
			return 0;
		}
	}

	private static int method_21059(class_3915 arg, Team team, Formatting formatting) throws CommandSyntaxException {
		if (team.method_12130() == formatting) {
			throw field_21798.create();
		} else {
			team.setFormatting(formatting);
			arg.method_17459(new TranslatableText("commands.team.option.color.success", team.method_18103(), formatting.getName()), true);
			return 0;
		}
	}

	private static int method_21058(class_3915 arg, Team team) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		Collection<String> collection = Lists.newArrayList(team.getPlayerList());
		if (collection.isEmpty()) {
			throw field_21796.create();
		} else {
			for (String string : collection) {
				scoreboard.removePlayerFromTeam(string, team);
			}

			arg.method_17459(new TranslatableText("commands.team.empty.success", collection.size(), team.method_18103()), true);
			return collection.size();
		}
	}

	private static int method_21072(class_3915 arg, Team team) {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		scoreboard.removeTeam(team);
		arg.method_17459(new TranslatableText("commands.team.remove.success", team.method_18103()), true);
		return scoreboard.getTeams().size();
	}

	private static int method_21065(class_3915 arg, String string) throws CommandSyntaxException {
		return method_21066(arg, string, new LiteralText(string));
	}

	private static int method_21066(class_3915 arg, String string, Text text) throws CommandSyntaxException {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		if (scoreboard.getTeam(string) != null) {
			throw field_21794.create();
		} else if (string.length() > 16) {
			throw field_21795.create(16);
		} else {
			Team team = scoreboard.addTeam(string);
			team.method_18098(text);
			arg.method_17459(new TranslatableText("commands.team.add.success", team.method_18103()), true);
			return scoreboard.getTeams().size();
		}
	}

	private static int method_21077(class_3915 arg, Team team) {
		Collection<String> collection = team.getPlayerList();
		if (collection.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.team.list.members.empty", team.method_18103()), false);
		} else {
			arg.method_17459(
				new TranslatableText("commands.team.list.members.success", team.method_18103(), collection.size(), ChatSerializer.method_20191(collection)), false
			);
		}

		return collection.size();
	}

	private static int method_21057(class_3915 arg) {
		Collection<Team> collection = arg.method_17473().method_20333().getTeams();
		if (collection.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.team.list.teams.empty"), false);
		} else {
			arg.method_17459(
				new TranslatableText("commands.team.list.teams.success", collection.size(), ChatSerializer.method_20193(collection, Team::method_18103)), false
			);
		}

		return collection.size();
	}

	private static int method_21074(class_3915 arg, Team team, Text text) {
		team.method_18100(text);
		arg.method_17459(new TranslatableText("commands.team.option.prefix.success", text), false);
		return 1;
	}

	private static int method_21078(class_3915 arg, Team team, Text text) {
		team.method_18102(text);
		arg.method_17459(new TranslatableText("commands.team.option.suffix.success", text), false);
		return 1;
	}
}
