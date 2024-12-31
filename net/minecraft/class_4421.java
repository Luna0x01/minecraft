package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class class_4421 {
	private static final SimpleCommandExceptionType field_21737 = new SimpleCommandExceptionType(new TranslatableText("commands.experience.set.points.invalid"));

	public static void method_20758(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralCommandNode<class_3915> literalCommandNode = commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("experience")
							.requires(arg -> arg.method_17575(2)))
						.then(
							CommandManager.method_17529("add")
								.then(
									CommandManager.method_17530("targets", class_4062.method_17904())
										.then(
											((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("amount", IntegerArgumentType.integer())
														.executes(
															commandContext -> method_20756(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17907(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "amount"),
																	class_4421.class_4422.POINTS
																)
														))
													.then(
														CommandManager.method_17529("points")
															.executes(
																commandContext -> method_20756(
																		(class_3915)commandContext.getSource(),
																		class_4062.method_17907(commandContext, "targets"),
																		IntegerArgumentType.getInteger(commandContext, "amount"),
																		class_4421.class_4422.POINTS
																	)
															)
													))
												.then(
													CommandManager.method_17529("levels")
														.executes(
															commandContext -> method_20756(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17907(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "amount"),
																	class_4421.class_4422.LEVELS
																)
														)
												)
										)
								)
						))
					.then(
						CommandManager.method_17529("set")
							.then(
								CommandManager.method_17530("targets", class_4062.method_17904())
									.then(
										((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("amount", IntegerArgumentType.integer(0))
													.executes(
														commandContext -> method_20761(
																(class_3915)commandContext.getSource(),
																class_4062.method_17907(commandContext, "targets"),
																IntegerArgumentType.getInteger(commandContext, "amount"),
																class_4421.class_4422.POINTS
															)
													))
												.then(
													CommandManager.method_17529("points")
														.executes(
															commandContext -> method_20761(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17907(commandContext, "targets"),
																	IntegerArgumentType.getInteger(commandContext, "amount"),
																	class_4421.class_4422.POINTS
																)
														)
												))
											.then(
												CommandManager.method_17529("levels")
													.executes(
														commandContext -> method_20761(
																(class_3915)commandContext.getSource(),
																class_4062.method_17907(commandContext, "targets"),
																IntegerArgumentType.getInteger(commandContext, "amount"),
																class_4421.class_4422.LEVELS
															)
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("query")
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17902())
									.then(
										CommandManager.method_17529("points")
											.executes(
												commandContext -> method_20757(
														(class_3915)commandContext.getSource(), class_4062.method_17906(commandContext, "targets"), class_4421.class_4422.POINTS
													)
											)
									))
								.then(
									CommandManager.method_17529("levels")
										.executes(
											commandContext -> method_20757(
													(class_3915)commandContext.getSource(), class_4062.method_17906(commandContext, "targets"), class_4421.class_4422.LEVELS
												)
										)
								)
						)
				)
		);
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("xp").requires(arg -> arg.method_17575(2))).redirect(literalCommandNode)
		);
	}

	private static int method_20757(class_3915 arg, ServerPlayerEntity serverPlayerEntity, class_4421.class_4422 arg2) {
		int i = arg2.field_21743.applyAsInt(serverPlayerEntity);
		arg.method_17459(new TranslatableText("commands.experience.query." + arg2.field_21742, serverPlayerEntity.getName(), i), false);
		return i;
	}

	private static int method_20756(class_3915 arg, Collection<? extends ServerPlayerEntity> collection, int i, class_4421.class_4422 arg2) {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			arg2.field_21740.accept(serverPlayerEntity, i);
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.experience.add." + arg2.field_21742 + ".success.single", i, ((ServerPlayerEntity)collection.iterator().next()).getName()),
				true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.experience.add." + arg2.field_21742 + ".success.multiple", i, collection.size()), true);
		}

		return collection.size();
	}

	private static int method_20761(class_3915 arg, Collection<? extends ServerPlayerEntity> collection, int i, class_4421.class_4422 arg2) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (arg2.field_21741.test(serverPlayerEntity, i)) {
				j++;
			}
		}

		if (j == 0) {
			throw field_21737.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(
					new TranslatableText("commands.experience.set." + arg2.field_21742 + ".success.single", i, ((ServerPlayerEntity)collection.iterator().next()).getName()),
					true
				);
			} else {
				arg.method_17459(new TranslatableText("commands.experience.set." + arg2.field_21742 + ".success.multiple", i, collection.size()), true);
			}

			return collection.size();
		}
	}

	static enum class_4422 {
		POINTS("points", PlayerEntity::method_15934, (serverPlayerEntity, integer) -> {
			if (integer >= serverPlayerEntity.getNextLevelExperience()) {
				return false;
			} else {
				serverPlayerEntity.method_21272(integer);
				return true;
			}
		}, serverPlayerEntity -> MathHelper.floor(serverPlayerEntity.experienceProgress * (float)serverPlayerEntity.getNextLevelExperience())),
		LEVELS("levels", ServerPlayerEntity::incrementXp, (serverPlayerEntity, integer) -> {
			serverPlayerEntity.method_21283(integer);
			return true;
		}, serverPlayerEntity -> serverPlayerEntity.experienceLevel);

		public final BiConsumer<ServerPlayerEntity, Integer> field_21740;
		public final BiPredicate<ServerPlayerEntity, Integer> field_21741;
		public final String field_21742;
		private final ToIntFunction<ServerPlayerEntity> field_21743;

		private class_4422(
			String string2,
			BiConsumer<ServerPlayerEntity, Integer> biConsumer,
			BiPredicate<ServerPlayerEntity, Integer> biPredicate,
			ToIntFunction<ServerPlayerEntity> toIntFunction
		) {
			this.field_21740 = biConsumer;
			this.field_21742 = string2;
			this.field_21741 = biPredicate;
			this.field_21743 = toIntFunction;
		}
	}
}
