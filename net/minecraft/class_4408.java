package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatSerializer;
import net.minecraft.util.Identifier;

public class class_4408 {
	private static final DynamicCommandExceptionType field_21694 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.bossbar.create.failed", object)
	);
	private static final DynamicCommandExceptionType field_21695 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.bossbar.unknown", object)
	);
	private static final SimpleCommandExceptionType field_21696 = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.players.unchanged"));
	private static final SimpleCommandExceptionType field_21697 = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.name.unchanged"));
	private static final SimpleCommandExceptionType field_21698 = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.color.unchanged"));
	private static final SimpleCommandExceptionType field_21699 = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.style.unchanged"));
	private static final SimpleCommandExceptionType field_21700 = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.value.unchanged"));
	private static final SimpleCommandExceptionType field_21701 = new SimpleCommandExceptionType(new TranslatableText("commands.bossbar.set.max.unchanged"));
	private static final SimpleCommandExceptionType field_21702 = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.visibility.unchanged.hidden")
	);
	private static final SimpleCommandExceptionType field_21703 = new SimpleCommandExceptionType(
		new TranslatableText("commands.bossbar.set.visibility.unchanged.visible")
	);
	public static final SuggestionProvider<class_3915> field_21693 = (commandContext, suggestionsBuilder) -> class_3965.method_17559(
			((class_3915)commandContext.getSource()).method_17473().method_20336().method_20477(), suggestionsBuilder
		);

	public static void method_20537(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
										"bossbar"
									)
									.requires(arg -> arg.method_17575(2)))
								.then(
									CommandManager.method_17529("add")
										.then(
											CommandManager.method_17530("id", class_4181.method_18904())
												.then(
													CommandManager.method_17530("name", class_4009.method_17711())
														.executes(
															commandContext -> method_20529(
																	(class_3915)commandContext.getSource(), class_4181.method_18910(commandContext, "id"), class_4009.method_17713(commandContext, "name")
																)
														)
												)
										)
								))
							.then(
								CommandManager.method_17529("remove")
									.then(
										CommandManager.method_17530("id", class_4181.method_18904())
											.suggests(field_21693)
											.executes(commandContext -> method_20550((class_3915)commandContext.getSource(), method_20538(commandContext)))
									)
							))
						.then(CommandManager.method_17529("list").executes(commandContext -> method_20528((class_3915)commandContext.getSource()))))
					.then(
						CommandManager.method_17529("set")
							.then(
								((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
																"id", class_4181.method_18904()
															)
															.suggests(field_21693)
															.then(
																CommandManager.method_17529("name")
																	.then(
																		CommandManager.method_17530("name", class_4009.method_17711())
																			.executes(
																				commandContext -> method_20534(
																						(class_3915)commandContext.getSource(), method_20538(commandContext), class_4009.method_17713(commandContext, "name")
																					)
																			)
																	)
															))
														.then(
															((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
																							"color"
																						)
																						.then(
																							CommandManager.method_17529("pink")
																								.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.PINK))
																						))
																					.then(
																						CommandManager.method_17529("blue")
																							.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.BLUE))
																					))
																				.then(
																					CommandManager.method_17529("red")
																						.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.RED))
																				))
																			.then(
																				CommandManager.method_17529("green")
																					.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.GREEN))
																			))
																		.then(
																			CommandManager.method_17529("yellow")
																				.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.YELLOW))
																		))
																	.then(
																		CommandManager.method_17529("purple")
																			.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.PURPLE))
																	))
																.then(
																	CommandManager.method_17529("white")
																		.executes(commandContext -> method_20532((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Color.WHITE))
																)
														))
													.then(
														((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("style")
																			.then(
																				CommandManager.method_17529("progress")
																					.executes(
																						commandContext -> method_20533((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Division.PROGRESS)
																					)
																			))
																		.then(
																			CommandManager.method_17529("notched_6")
																				.executes(
																					commandContext -> method_20533((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Division.NOTCHED_6)
																				)
																		))
																	.then(
																		CommandManager.method_17529("notched_10")
																			.executes(
																				commandContext -> method_20533((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Division.NOTCHED_10)
																			)
																	))
																.then(
																	CommandManager.method_17529("notched_12")
																		.executes(
																			commandContext -> method_20533((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Division.NOTCHED_12)
																		)
																))
															.then(
																CommandManager.method_17529("notched_20")
																	.executes(commandContext -> method_20533((class_3915)commandContext.getSource(), method_20538(commandContext), class_2957.Division.NOTCHED_20))
															)
													))
												.then(
													CommandManager.method_17529("value")
														.then(
															CommandManager.method_17530("value", IntegerArgumentType.integer(0))
																.executes(
																	commandContext -> method_20531(
																			(class_3915)commandContext.getSource(), method_20538(commandContext), IntegerArgumentType.getInteger(commandContext, "value")
																		)
																)
														)
												))
											.then(
												CommandManager.method_17529("max")
													.then(
														CommandManager.method_17530("max", IntegerArgumentType.integer(1))
															.executes(
																commandContext -> method_20543(
																		(class_3915)commandContext.getSource(), method_20538(commandContext), IntegerArgumentType.getInteger(commandContext, "max")
																	)
															)
													)
											))
										.then(
											CommandManager.method_17529("visible")
												.then(
													CommandManager.method_17530("visible", BoolArgumentType.bool())
														.executes(
															commandContext -> method_20536(
																	(class_3915)commandContext.getSource(), method_20538(commandContext), BoolArgumentType.getBool(commandContext, "visible")
																)
														)
												)
										))
									.then(
										((LiteralArgumentBuilder)CommandManager.method_17529("players")
												.executes(commandContext -> method_20535((class_3915)commandContext.getSource(), method_20538(commandContext), Collections.emptyList())))
											.then(
												CommandManager.method_17530("targets", class_4062.method_17904())
													.executes(
														commandContext -> method_20535(
																(class_3915)commandContext.getSource(), method_20538(commandContext), class_4062.method_17905(commandContext, "targets")
															)
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("get")
						.then(
							((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("id", class_4181.method_18904())
											.suggests(field_21693)
											.then(
												CommandManager.method_17529("value").executes(commandContext -> method_20530((class_3915)commandContext.getSource(), method_20538(commandContext)))
											))
										.then(
											CommandManager.method_17529("max").executes(commandContext -> method_20542((class_3915)commandContext.getSource(), method_20538(commandContext)))
										))
									.then(
										CommandManager.method_17529("visible").executes(commandContext -> method_20546((class_3915)commandContext.getSource(), method_20538(commandContext)))
									))
								.then(
									CommandManager.method_17529("players").executes(commandContext -> method_20548((class_3915)commandContext.getSource(), method_20538(commandContext)))
								)
						)
				)
		);
	}

	private static int method_20530(class_3915 arg, class_4402 arg2) {
		arg.method_17459(new TranslatableText("commands.bossbar.get.value", arg2.method_20475(), arg2.method_20471()), true);
		return arg2.method_20471();
	}

	private static int method_20542(class_3915 arg, class_4402 arg2) {
		arg.method_17459(new TranslatableText("commands.bossbar.get.max", arg2.method_20475(), arg2.method_20473()), true);
		return arg2.method_20473();
	}

	private static int method_20546(class_3915 arg, class_4402 arg2) {
		if (arg2.method_21247()) {
			arg.method_17459(new TranslatableText("commands.bossbar.get.visible.visible", arg2.method_20475()), true);
			return 1;
		} else {
			arg.method_17459(new TranslatableText("commands.bossbar.get.visible.hidden", arg2.method_20475()), true);
			return 0;
		}
	}

	private static int method_20548(class_3915 arg, class_4402 arg2) {
		if (arg2.method_12770().isEmpty()) {
			arg.method_17459(new TranslatableText("commands.bossbar.get.players.none", arg2.method_20475()), true);
		} else {
			arg.method_17459(
				new TranslatableText(
					"commands.bossbar.get.players.some",
					arg2.method_20475(),
					arg2.method_12770().size(),
					ChatSerializer.method_20193(arg2.method_12770(), PlayerEntity::getName)
				),
				true
			);
		}

		return arg2.method_12770().size();
	}

	private static int method_20536(class_3915 arg, class_4402 arg2, boolean bl) throws CommandSyntaxException {
		if (arg2.method_21247() == bl) {
			if (bl) {
				throw field_21703.create();
			} else {
				throw field_21702.create();
			}
		} else {
			arg2.method_12771(bl);
			if (bl) {
				arg.method_17459(new TranslatableText("commands.bossbar.set.visible.success.visible", arg2.method_20475()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.bossbar.set.visible.success.hidden", arg2.method_20475()), true);
			}

			return 0;
		}
	}

	private static int method_20531(class_3915 arg, class_4402 arg2, int i) throws CommandSyntaxException {
		if (arg2.method_20471() == i) {
			throw field_21700.create();
		} else {
			arg2.method_20465(i);
			arg.method_17459(new TranslatableText("commands.bossbar.set.value.success", arg2.method_20475(), i), true);
			return i;
		}
	}

	private static int method_20543(class_3915 arg, class_4402 arg2, int i) throws CommandSyntaxException {
		if (arg2.method_20473() == i) {
			throw field_21701.create();
		} else {
			arg2.method_20470(i);
			arg.method_17459(new TranslatableText("commands.bossbar.set.max.success", arg2.method_20475(), i), true);
			return i;
		}
	}

	private static int method_20532(class_3915 arg, class_4402 arg2, class_2957.Color color) throws CommandSyntaxException {
		if (arg2.getColor().equals(color)) {
			throw field_21698.create();
		} else {
			arg2.setColor(color);
			arg.method_17459(new TranslatableText("commands.bossbar.set.color.success", arg2.method_20475()), true);
			return 0;
		}
	}

	private static int method_20533(class_3915 arg, class_4402 arg2, class_2957.Division division) throws CommandSyntaxException {
		if (arg2.getDivision().equals(division)) {
			throw field_21699.create();
		} else {
			arg2.setDivision(division);
			arg.method_17459(new TranslatableText("commands.bossbar.set.style.success", arg2.method_20475()), true);
			return 0;
		}
	}

	private static int method_20534(class_3915 arg, class_4402 arg2, Text text) throws CommandSyntaxException {
		Text text2 = ChatSerializer.method_20185(arg, text, null);
		if (arg2.getTitle().equals(text2)) {
			throw field_21697.create();
		} else {
			arg2.setTitle(text2);
			arg.method_17459(new TranslatableText("commands.bossbar.set.name.success", arg2.method_20475()), true);
			return 0;
		}
	}

	private static int method_20535(class_3915 arg, class_4402 arg2, Collection<ServerPlayerEntity> collection) throws CommandSyntaxException {
		boolean bl = arg2.method_20468(collection);
		if (!bl) {
			throw field_21696.create();
		} else {
			if (arg2.method_12770().isEmpty()) {
				arg.method_17459(new TranslatableText("commands.bossbar.set.players.success.none", arg2.method_20475()), true);
			} else {
				arg.method_17459(
					new TranslatableText(
						"commands.bossbar.set.players.success.some", arg2.method_20475(), collection.size(), ChatSerializer.method_20193(collection, PlayerEntity::getName)
					),
					true
				);
			}

			return arg2.method_12770().size();
		}
	}

	private static int method_20528(class_3915 arg) {
		Collection<class_4402> collection = arg.method_17473().method_20336().method_20483();
		if (collection.isEmpty()) {
			arg.method_17459(new TranslatableText("commands.bossbar.list.bars.none"), false);
		} else {
			arg.method_17459(
				new TranslatableText("commands.bossbar.list.bars.some", collection.size(), ChatSerializer.method_20193(collection, class_4402::method_20475)), false
			);
		}

		return collection.size();
	}

	private static int method_20529(class_3915 arg, Identifier identifier, Text text) throws CommandSyntaxException {
		class_4403 lv = arg.method_17473().method_20336();
		if (lv.method_20479(identifier) != null) {
			throw field_21694.create(identifier.toString());
		} else {
			class_4402 lv2 = lv.method_20480(identifier, ChatSerializer.method_20185(arg, text, null));
			arg.method_17459(new TranslatableText("commands.bossbar.create.success", lv2.method_20475()), true);
			return lv.method_20483().size();
		}
	}

	private static int method_20550(class_3915 arg, class_4402 arg2) {
		class_4403 lv = arg.method_17473().method_20336();
		arg2.method_21246();
		lv.method_20481(arg2);
		arg.method_17459(new TranslatableText("commands.bossbar.remove.success", arg2.method_20475()), true);
		return lv.method_20483().size();
	}

	public static class_4402 method_20538(CommandContext<class_3915> commandContext) throws CommandSyntaxException {
		Identifier identifier = class_4181.method_18910(commandContext, "id");
		class_4402 lv = ((class_3915)commandContext.getSource()).method_17473().method_20336().method_20479(identifier);
		if (lv == null) {
			throw field_21695.create(identifier.toString());
		} else {
			return lv;
		}
	}
}
