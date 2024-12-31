package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4409 {
	private static final DynamicCommandExceptionType field_21704 = new DynamicCommandExceptionType(object -> new TranslatableText("clear.failed.single", object));
	private static final DynamicCommandExceptionType field_21705 = new DynamicCommandExceptionType(object -> new TranslatableText("clear.failed.multiple", object));

	public static void method_20576(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("clear").requires(arg -> arg.method_17575(2)))
					.executes(
						commandContext -> method_20575(
								(class_3915)commandContext.getSource(), Collections.singleton(((class_3915)commandContext.getSource()).method_17471()), itemStack -> true, -1
							)
					))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17904())
							.executes(
								commandContext -> method_20575((class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), itemStack -> true, -1)
							))
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("item", class_4313.method_19718())
									.executes(
										commandContext -> method_20575(
												(class_3915)commandContext.getSource(), class_4062.method_17907(commandContext, "targets"), class_4313.method_19720(commandContext, "item"), -1
											)
									))
								.then(
									CommandManager.method_17530("maxCount", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_20575(
													(class_3915)commandContext.getSource(),
													class_4062.method_17907(commandContext, "targets"),
													class_4313.method_19720(commandContext, "item"),
													IntegerArgumentType.getInteger(commandContext, "maxCount")
												)
										)
								)
						)
				)
		);
	}

	private static int method_20575(class_3915 arg, Collection<ServerPlayerEntity> collection, Predicate<ItemStack> predicate, int i) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			j += serverPlayerEntity.inventory.method_15922(predicate, i);
		}

		if (j == 0) {
			if (collection.size() == 1) {
				throw field_21704.create(((ServerPlayerEntity)collection.iterator().next()).method_15540().asFormattedString());
			} else {
				throw field_21705.create(collection.size());
			}
		} else {
			if (i == 0) {
				if (collection.size() == 1) {
					arg.method_17459(new TranslatableText("commands.clear.test.single", j, ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
				} else {
					arg.method_17459(new TranslatableText("commands.clear.test.multiple", j, collection.size()), true);
				}
			} else if (collection.size() == 1) {
				arg.method_17459(new TranslatableText("commands.clear.success.single", j, ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.clear.success.multiple", j, collection.size()), true);
			}

			return j;
		}
	}
}
