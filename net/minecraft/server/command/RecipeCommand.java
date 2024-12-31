package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4181;
import net.minecraft.class_4327;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.TranslatableText;

public class RecipeCommand {
	private static final SimpleCommandExceptionType field_21769 = new SimpleCommandExceptionType(new TranslatableText("commands.recipe.give.failed"));
	private static final SimpleCommandExceptionType field_21770 = new SimpleCommandExceptionType(new TranslatableText("commands.recipe.take.failed"));

	public static void method_20912(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("recipe").requires(arg -> arg.method_17575(2)))
					.then(
						CommandManager.method_17529("give")
							.then(
								((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17904())
										.then(
											CommandManager.method_17530("recipe", class_4181.method_18904())
												.suggests(class_4327.field_21255)
												.executes(
													commandContext -> method_20911(
															(class_3915)commandContext.getSource(),
															class_4062.method_17907(commandContext, "targets"),
															Collections.singleton(class_4181.method_18908(commandContext, "recipe"))
														)
												)
										))
									.then(
										CommandManager.method_17529("*")
											.executes(
												commandContext -> method_20911(
														(class_3915)commandContext.getSource(),
														class_4062.method_17907(commandContext, "targets"),
														((class_3915)commandContext.getSource()).method_17473().method_20331().method_16208()
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("take")
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17904())
									.then(
										CommandManager.method_17530("recipe", class_4181.method_18904())
											.suggests(class_4327.field_21255)
											.executes(
												commandContext -> method_20914(
														(class_3915)commandContext.getSource(),
														class_4062.method_17907(commandContext, "targets"),
														Collections.singleton(class_4181.method_18908(commandContext, "recipe"))
													)
											)
									))
								.then(
									CommandManager.method_17529("*")
										.executes(
											commandContext -> method_20914(
													(class_3915)commandContext.getSource(),
													class_4062.method_17907(commandContext, "targets"),
													((class_3915)commandContext.getSource()).method_17473().method_20331().method_16208()
												)
										)
								)
						)
				)
		);
	}

	private static int method_20911(class_3915 arg, Collection<ServerPlayerEntity> collection, Collection<RecipeType> collection2) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += serverPlayerEntity.method_15927(collection2);
		}

		if (i == 0) {
			throw field_21769.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(
					new TranslatableText("commands.recipe.give.success.single", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getName()), true
				);
			} else {
				arg.method_17459(new TranslatableText("commands.recipe.give.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}

	private static int method_20914(class_3915 arg, Collection<ServerPlayerEntity> collection, Collection<RecipeType> collection2) throws CommandSyntaxException {
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			i += serverPlayerEntity.method_15931(collection2);
		}

		if (i == 0) {
			throw field_21770.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(
					new TranslatableText("commands.recipe.take.success.single", collection2.size(), ((ServerPlayerEntity)collection.iterator().next()).getName()), true
				);
			} else {
				arg.method_17459(new TranslatableText("commands.recipe.take.success.multiple", collection2.size(), collection.size()), true);
			}

			return i;
		}
	}
}
