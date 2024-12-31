package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class class_4419 {
	private static final Dynamic2CommandExceptionType field_21733 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.execute.blocks.toobig", object, object2)
	);
	private static final SimpleCommandExceptionType field_21734 = new SimpleCommandExceptionType(new TranslatableText("commands.execute.conditional.fail"));
	private static final DynamicCommandExceptionType field_21735 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.execute.conditional.fail_count", object)
	);
	private static final BinaryOperator<ResultConsumer<class_3915>> field_21736 = (resultConsumer, resultConsumer2) -> (commandContext, bl, i) -> {
			resultConsumer.onCommandComplete(commandContext, bl, i);
			resultConsumer2.onCommandComplete(commandContext, bl, i);
		};

	public static void method_20689(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralCommandNode<class_3915> literalCommandNode = commandDispatcher.register(
			(LiteralArgumentBuilder)CommandManager.method_17529("execute").requires(arg -> arg.method_17575(2))
		);
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529(
																	"execute"
																)
																.requires(arg -> arg.method_17575(2)))
															.then(CommandManager.method_17529("run").redirect(commandDispatcher.getRoot())))
														.then(method_20700(literalCommandNode, CommandManager.method_17529("if"), true)))
													.then(method_20700(literalCommandNode, CommandManager.method_17529("unless"), false)))
												.then(
													CommandManager.method_17529("as")
														.then(CommandManager.method_17530("targets", class_4062.method_17899()).fork(literalCommandNode, commandContext -> {
															List<class_3915> list = Lists.newArrayList();

															for (Entity entity : class_4062.method_17903(commandContext, "targets")) {
																list.add(((class_3915)commandContext.getSource()).method_17450(entity));
															}

															return list;
														}))
												))
											.then(
												CommandManager.method_17529("at")
													.then(
														CommandManager.method_17530("targets", class_4062.method_17899())
															.fork(
																literalCommandNode,
																commandContext -> {
																	List<class_3915> list = Lists.newArrayList();

																	for (Entity entity : class_4062.method_17903(commandContext, "targets")) {
																		list.add(
																			((class_3915)commandContext.getSource())
																				.method_17460((ServerWorld)entity.world)
																				.method_17454(entity.method_10787())
																				.method_17453(entity.getRotationClient())
																		);
																	}

																	return list;
																}
															)
													)
											))
										.then(
											((LiteralArgumentBuilder)CommandManager.method_17529("store").then(method_20701(literalCommandNode, CommandManager.method_17529("result"), true)))
												.then(method_20701(literalCommandNode, CommandManager.method_17529("success"), false))
										))
									.then(
										((LiteralArgumentBuilder)CommandManager.method_17529("positioned")
												.then(
													CommandManager.method_17530("pos", class_4287.method_19562())
														.redirect(
															literalCommandNode, commandContext -> ((class_3915)commandContext.getSource()).method_17454(class_4287.method_19564(commandContext, "pos"))
														)
												))
											.then(
												CommandManager.method_17529("as")
													.then(CommandManager.method_17530("targets", class_4062.method_17899()).fork(literalCommandNode, commandContext -> {
														List<class_3915> list = Lists.newArrayList();

														for (Entity entity : class_4062.method_17903(commandContext, "targets")) {
															list.add(((class_3915)commandContext.getSource()).method_17454(entity.method_10787()));
														}

														return list;
													}))
											)
									))
								.then(
									((LiteralArgumentBuilder)CommandManager.method_17529("rotated")
											.then(
												CommandManager.method_17530("rot", class_4271.method_19435())
													.redirect(
														literalCommandNode,
														commandContext -> ((class_3915)commandContext.getSource())
																.method_17453(class_4271.method_19437(commandContext, "rot").method_19413((class_3915)commandContext.getSource()))
													)
											))
										.then(
											CommandManager.method_17529("as")
												.then(CommandManager.method_17530("targets", class_4062.method_17899()).fork(literalCommandNode, commandContext -> {
													List<class_3915> list = Lists.newArrayList();

													for (Entity entity : class_4062.method_17903(commandContext, "targets")) {
														list.add(((class_3915)commandContext.getSource()).method_17453(entity.getRotationClient()));
													}

													return list;
												}))
										)
								))
							.then(
								((LiteralArgumentBuilder)CommandManager.method_17529("facing")
										.then(
											CommandManager.method_17529("entity")
												.then(
													CommandManager.method_17530("targets", class_4062.method_17899())
														.then(CommandManager.method_17530("anchor", class_4048.method_17865()).fork(literalCommandNode, commandContext -> {
															List<class_3915> list = Lists.newArrayList();
															class_4048.class_4049 lv = class_4048.method_17867(commandContext, "anchor");

															for (Entity entity : class_4062.method_17903(commandContext, "targets")) {
																list.add(((class_3915)commandContext.getSource()).method_17451(entity, lv));
															}

															return list;
														}))
												)
										))
									.then(
										CommandManager.method_17530("pos", class_4287.method_19562())
											.redirect(
												literalCommandNode, commandContext -> ((class_3915)commandContext.getSource()).method_17463(class_4287.method_19564(commandContext, "pos"))
											)
									)
							))
						.then(
							CommandManager.method_17529("align")
								.then(
									CommandManager.method_17530("axes", class_4275.method_19445())
										.redirect(
											literalCommandNode,
											commandContext -> ((class_3915)commandContext.getSource())
													.method_17454(((class_3915)commandContext.getSource()).method_17467().method_18012(class_4275.method_19447(commandContext, "axes")))
										)
								)
						))
					.then(
						CommandManager.method_17529("anchored")
							.then(
								CommandManager.method_17530("anchor", class_4048.method_17865())
									.redirect(
										literalCommandNode, commandContext -> ((class_3915)commandContext.getSource()).method_17452(class_4048.method_17867(commandContext, "anchor"))
									)
							)
					))
				.then(
					CommandManager.method_17529("in")
						.then(
							CommandManager.method_17530("dimension", class_4030.method_17821())
								.redirect(
									literalCommandNode,
									commandContext -> ((class_3915)commandContext.getSource())
											.method_17460(((class_3915)commandContext.getSource()).method_17473().method_20312(class_4030.method_17824(commandContext, "dimension")))
								)
						)
				)
		);
	}

	private static ArgumentBuilder<class_3915, ?> method_20701(
		LiteralCommandNode<class_3915> literalCommandNode, LiteralArgumentBuilder<class_3915> literalArgumentBuilder, boolean bl
	) {
		literalArgumentBuilder.then(
			CommandManager.method_17529("score")
				.then(
					CommandManager.method_17530("targets", class_4186.method_18927())
						.suggests(class_4186.field_20535)
						.then(
							CommandManager.method_17530("objective", class_4151.method_18520())
								.redirect(
									literalCommandNode,
									commandContext -> method_20686(
											(class_3915)commandContext.getSource(), class_4186.method_18930(commandContext, "targets"), class_4151.method_18522(commandContext, "objective"), bl
										)
								)
						)
				)
		);
		literalArgumentBuilder.then(
			CommandManager.method_17529("bossbar")
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("id", class_4181.method_18904())
							.suggests(class_4408.field_21693)
							.then(
								CommandManager.method_17529("value")
									.redirect(
										literalCommandNode, commandContext -> method_20687((class_3915)commandContext.getSource(), class_4408.method_20538(commandContext), true, bl)
									)
							))
						.then(
							CommandManager.method_17529("max")
								.redirect(
									literalCommandNode, commandContext -> method_20687((class_3915)commandContext.getSource(), class_4408.method_20538(commandContext), false, bl)
								)
						)
				)
		);

		for (class_4437.class_4438 lv : class_4437.field_21827) {
			lv.method_21229(
				literalArgumentBuilder,
				argumentBuilder -> argumentBuilder.then(
						((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
													"path", class_4124.method_18432()
												)
												.then(
													CommandManager.method_17529("int")
														.then(
															CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
																.redirect(
																	literalCommandNode,
																	commandContext -> method_20688(
																			(class_3915)commandContext.getSource(),
																			lv.method_21230(commandContext),
																			class_4124.method_18435(commandContext, "path"),
																			i -> new NbtInt((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
																			bl
																		)
																)
														)
												))
											.then(
												CommandManager.method_17529("float")
													.then(
														CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
															.redirect(
																literalCommandNode,
																commandContext -> method_20688(
																		(class_3915)commandContext.getSource(),
																		lv.method_21230(commandContext),
																		class_4124.method_18435(commandContext, "path"),
																		i -> new NbtFloat((float)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
																		bl
																	)
															)
													)
											))
										.then(
											CommandManager.method_17529("short")
												.then(
													CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
														.redirect(
															literalCommandNode,
															commandContext -> method_20688(
																	(class_3915)commandContext.getSource(),
																	lv.method_21230(commandContext),
																	class_4124.method_18435(commandContext, "path"),
																	i -> new NbtShort((short)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
																	bl
																)
														)
												)
										))
									.then(
										CommandManager.method_17529("long")
											.then(
												CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
													.redirect(
														literalCommandNode,
														commandContext -> method_20688(
																(class_3915)commandContext.getSource(),
																lv.method_21230(commandContext),
																class_4124.method_18435(commandContext, "path"),
																i -> new NbtLong((long)((double)i * DoubleArgumentType.getDouble(commandContext, "scale"))),
																bl
															)
													)
											)
									))
								.then(
									CommandManager.method_17529("double")
										.then(
											CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
												.redirect(
													literalCommandNode,
													commandContext -> method_20688(
															(class_3915)commandContext.getSource(),
															lv.method_21230(commandContext),
															class_4124.method_18435(commandContext, "path"),
															i -> new NbtDouble((double)i * DoubleArgumentType.getDouble(commandContext, "scale")),
															bl
														)
												)
										)
								))
							.then(
								CommandManager.method_17529("byte")
									.then(
										CommandManager.method_17530("scale", DoubleArgumentType.doubleArg())
											.redirect(
												literalCommandNode,
												commandContext -> method_20688(
														(class_3915)commandContext.getSource(),
														lv.method_21230(commandContext),
														class_4124.method_18435(commandContext, "path"),
														i -> new NbtByte((byte)((int)((double)i * DoubleArgumentType.getDouble(commandContext, "scale")))),
														bl
													)
											)
									)
							)
					)
			);
		}

		return literalArgumentBuilder;
	}

	private static class_3915 method_20686(class_3915 arg, Collection<String> collection, ScoreboardObjective scoreboardObjective, boolean bl) {
		Scoreboard scoreboard = arg.method_17473().method_20333();
		return arg.method_17456((commandContext, bl2, i) -> {
			for (String string : collection) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
				int j = bl ? i : (bl2 ? 1 : 0);
				scoreboardPlayerScore.setScore(j);
			}
		}, field_21736);
	}

	private static class_3915 method_20687(class_3915 arg, class_4402 arg2, boolean bl, boolean bl2) {
		return arg.method_17456((commandContext, bl3, i) -> {
			int j = bl2 ? i : (bl3 ? 1 : 0);
			if (bl) {
				arg2.method_20465(j);
			} else {
				arg2.method_20470(j);
			}
		}, field_21736);
	}

	private static class_3915 method_20688(class_3915 arg, class_4436 arg2, class_4124.class_4127 arg3, IntFunction<NbtElement> intFunction, boolean bl) {
		return arg.method_17456((commandContext, bl2, i) -> {
			try {
				NbtCompound nbtCompound = arg2.method_21207();
				int j = bl ? i : (bl2 ? 1 : 0);
				arg3.method_18443(nbtCompound, (NbtElement)intFunction.apply(j));
				arg2.method_21209(nbtCompound);
			} catch (CommandSyntaxException var9) {
			}
		}, field_21736);
	}

	private static ArgumentBuilder<class_3915, ?> method_20700(
		CommandNode<class_3915> commandNode, LiteralArgumentBuilder<class_3915> literalArgumentBuilder, boolean bl
	) {
		return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literalArgumentBuilder.then(
						CommandManager.method_17529("block")
							.then(
								CommandManager.method_17530("pos", class_4252.method_19358())
									.then(
										method_20698(
											commandNode,
											CommandManager.method_17530("block", class_4220.method_19107()),
											bl,
											commandContext -> class_4220.method_19109(commandContext, "block")
													.test(new CachedBlockPosition(((class_3915)commandContext.getSource()).method_17468(), class_4252.method_19360(commandContext, "pos"), true))
										)
									)
							)
					))
					.then(
						CommandManager.method_17529("score")
							.then(
								CommandManager.method_17530("target", class_4186.method_18919())
									.suggests(class_4186.field_20535)
									.then(
										((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530(
																	"targetObjective", class_4151.method_18520()
																)
																.then(
																	CommandManager.method_17529("=")
																		.then(
																			CommandManager.method_17530("source", class_4186.method_18919())
																				.suggests(class_4186.field_20535)
																				.then(
																					method_20698(
																						commandNode,
																						CommandManager.method_17530("sourceObjective", class_4151.method_18520()),
																						bl,
																						commandContext -> method_20695(commandContext, Integer::equals)
																					)
																				)
																		)
																))
															.then(
																CommandManager.method_17529("<")
																	.then(
																		CommandManager.method_17530("source", class_4186.method_18919())
																			.suggests(class_4186.field_20535)
																			.then(
																				method_20698(
																					commandNode,
																					CommandManager.method_17530("sourceObjective", class_4151.method_18520()),
																					bl,
																					commandContext -> method_20695(commandContext, (integer, integer2) -> integer < integer2)
																				)
																			)
																	)
															))
														.then(
															CommandManager.method_17529("<=")
																.then(
																	CommandManager.method_17530("source", class_4186.method_18919())
																		.suggests(class_4186.field_20535)
																		.then(
																			method_20698(
																				commandNode,
																				CommandManager.method_17530("sourceObjective", class_4151.method_18520()),
																				bl,
																				commandContext -> method_20695(commandContext, (integer, integer2) -> integer <= integer2)
																			)
																		)
																)
														))
													.then(
														CommandManager.method_17529(">")
															.then(
																CommandManager.method_17530("source", class_4186.method_18919())
																	.suggests(class_4186.field_20535)
																	.then(
																		method_20698(
																			commandNode,
																			CommandManager.method_17530("sourceObjective", class_4151.method_18520()),
																			bl,
																			commandContext -> method_20695(commandContext, (integer, integer2) -> integer > integer2)
																		)
																	)
															)
													))
												.then(
													CommandManager.method_17529(">=")
														.then(
															CommandManager.method_17530("source", class_4186.method_18919())
																.suggests(class_4186.field_20535)
																.then(
																	method_20698(
																		commandNode,
																		CommandManager.method_17530("sourceObjective", class_4151.method_18520()),
																		bl,
																		commandContext -> method_20695(commandContext, (integer, integer2) -> integer >= integer2)
																	)
																)
														)
												))
											.then(
												CommandManager.method_17529("matches")
													.then(
														method_20698(
															commandNode,
															CommandManager.method_17530("range", class_4173.method_18871()),
															bl,
															commandContext -> method_20694(commandContext, class_4173.class_4176.method_18875(commandContext, "range"))
														)
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("blocks")
						.then(
							CommandManager.method_17530("start", class_4252.method_19358())
								.then(
									CommandManager.method_17530("end", class_4252.method_19358())
										.then(
											((RequiredArgumentBuilder)CommandManager.method_17530("destination", class_4252.method_19358())
													.then(method_20699(commandNode, CommandManager.method_17529("all"), bl, false)))
												.then(method_20699(commandNode, CommandManager.method_17529("masked"), bl, true))
										)
								)
						)
				))
			.then(
				CommandManager.method_17529("entity")
					.then(
						((RequiredArgumentBuilder)CommandManager.method_17530("entities", class_4062.method_17899())
								.fork(commandNode, commandContext -> method_20697(commandContext, bl, !class_4062.method_17903(commandContext, "entities").isEmpty())))
							.executes(bl ? commandContext -> {
								int i = class_4062.method_17903(commandContext, "entities").size();
								if (i > 0) {
									((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.execute.conditional.pass_count", i), false);
									return i;
								} else {
									throw field_21734.create();
								}
							} : commandContext -> {
								int i = class_4062.method_17903(commandContext, "entities").size();
								if (i == 0) {
									((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.execute.conditional.pass"), false);
									return 1;
								} else {
									throw field_21735.create(i);
								}
							})
					)
			);
	}

	private static boolean method_20695(CommandContext<class_3915> commandContext, BiPredicate<Integer, Integer> biPredicate) throws CommandSyntaxException {
		String string = class_4186.method_18923(commandContext, "target");
		ScoreboardObjective scoreboardObjective = class_4151.method_18522(commandContext, "targetObjective");
		String string2 = class_4186.method_18923(commandContext, "source");
		ScoreboardObjective scoreboardObjective2 = class_4151.method_18522(commandContext, "sourceObjective");
		Scoreboard scoreboard = ((class_3915)commandContext.getSource()).method_17473().method_20333();
		if (scoreboard.playerHasObjective(string, scoreboardObjective) && scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
			return biPredicate.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
		} else {
			return false;
		}
	}

	private static boolean method_20694(CommandContext<class_3915> commandContext, class_3638.class_3642 arg) throws CommandSyntaxException {
		String string = class_4186.method_18923(commandContext, "target");
		ScoreboardObjective scoreboardObjective = class_4151.method_18522(commandContext, "targetObjective");
		Scoreboard scoreboard = ((class_3915)commandContext.getSource()).method_17473().method_20333();
		return !scoreboard.playerHasObjective(string, scoreboardObjective)
			? false
			: arg.method_16531(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
	}

	private static Collection<class_3915> method_20697(CommandContext<class_3915> commandContext, boolean bl, boolean bl2) {
		return (Collection<class_3915>)(bl2 == bl ? Collections.singleton(commandContext.getSource()) : Collections.emptyList());
	}

	private static ArgumentBuilder<class_3915, ?> method_20698(
		CommandNode<class_3915> commandNode, ArgumentBuilder<class_3915, ?> argumentBuilder, boolean bl, class_4419.class_4420 arg
	) {
		return argumentBuilder.fork(commandNode, commandContext -> method_20697(commandContext, bl, arg.test(commandContext))).executes(commandContext -> {
			if (bl == arg.test(commandContext)) {
				((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw field_21734.create();
			}
		});
	}

	private static ArgumentBuilder<class_3915, ?> method_20699(
		CommandNode<class_3915> commandNode, ArgumentBuilder<class_3915, ?> argumentBuilder, boolean bl, boolean bl2
	) {
		return argumentBuilder.fork(commandNode, commandContext -> method_20697(commandContext, bl, method_20724(commandContext, bl2).isPresent()))
			.executes(bl ? commandContext -> method_20696(commandContext, bl2) : commandContext -> method_20717(commandContext, bl2));
	}

	private static int method_20696(CommandContext<class_3915> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = method_20724(commandContext, bl);
		if (optionalInt.isPresent()) {
			((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
			return optionalInt.getAsInt();
		} else {
			throw field_21734.create();
		}
	}

	private static int method_20717(CommandContext<class_3915> commandContext, boolean bl) throws CommandSyntaxException {
		OptionalInt optionalInt = method_20724(commandContext, bl);
		if (!optionalInt.isPresent()) {
			((class_3915)commandContext.getSource()).method_17459(new TranslatableText("commands.execute.conditional.pass"), false);
			return 1;
		} else {
			throw field_21735.create(optionalInt.getAsInt());
		}
	}

	private static OptionalInt method_20724(CommandContext<class_3915> commandContext, boolean bl) throws CommandSyntaxException {
		return method_20709(
			((class_3915)commandContext.getSource()).method_17468(),
			class_4252.method_19360(commandContext, "start"),
			class_4252.method_19360(commandContext, "end"),
			class_4252.method_19360(commandContext, "destination"),
			bl
		);
	}

	private static OptionalInt method_20709(ServerWorld serverWorld, BlockPos blockPos, BlockPos blockPos2, BlockPos blockPos3, boolean bl) throws CommandSyntaxException {
		BlockBox blockBox = new BlockBox(blockPos, blockPos2);
		BlockBox blockBox2 = new BlockBox(blockPos3, blockPos3.add(blockBox.getDimensions()));
		BlockPos blockPos4 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		if (i > 32768) {
			throw field_21733.create(32768, i);
		} else {
			int j = 0;

			for (int k = blockBox.minZ; k <= blockBox.maxZ; k++) {
				for (int l = blockBox.minY; l <= blockBox.maxY; l++) {
					for (int m = blockBox.minX; m <= blockBox.maxX; m++) {
						BlockPos blockPos5 = new BlockPos(m, l, k);
						BlockPos blockPos6 = blockPos5.add(blockPos4);
						BlockState blockState = serverWorld.getBlockState(blockPos5);
						if (!bl || blockState.getBlock() != Blocks.AIR) {
							if (blockState != serverWorld.getBlockState(blockPos6)) {
								return OptionalInt.empty();
							}

							BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos5);
							BlockEntity blockEntity2 = serverWorld.getBlockEntity(blockPos6);
							if (blockEntity != null) {
								if (blockEntity2 == null) {
									return OptionalInt.empty();
								}

								NbtCompound nbtCompound = blockEntity.toNbt(new NbtCompound());
								nbtCompound.remove("x");
								nbtCompound.remove("y");
								nbtCompound.remove("z");
								NbtCompound nbtCompound2 = blockEntity2.toNbt(new NbtCompound());
								nbtCompound2.remove("x");
								nbtCompound2.remove("y");
								nbtCompound2.remove("z");
								if (!nbtCompound.equals(nbtCompound2)) {
									return OptionalInt.empty();
								}
							}

							j++;
						}
					}
				}
			}

			return OptionalInt.of(j);
		}
	}

	@FunctionalInterface
	interface class_4420 {
		boolean test(CommandContext<class_3915> commandContext) throws CommandSyntaxException;
	}
}
