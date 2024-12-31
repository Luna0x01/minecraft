package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4310;
import net.minecraft.class_4311;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.Sounds;
import net.minecraft.text.TranslatableText;

public class GiveCommand {
	public static void method_20826(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("give").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("targets", class_4062.method_17904())
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("item", class_4310.method_19698())
									.executes(
										commandContext -> method_20825(
												(class_3915)commandContext.getSource(), class_4310.method_19700(commandContext, "item"), class_4062.method_17907(commandContext, "targets"), 1
											)
									))
								.then(
									CommandManager.method_17530("count", IntegerArgumentType.integer(1))
										.executes(
											commandContext -> method_20825(
													(class_3915)commandContext.getSource(),
													class_4310.method_19700(commandContext, "item"),
													class_4062.method_17907(commandContext, "targets"),
													IntegerArgumentType.getInteger(commandContext, "count")
												)
										)
								)
						)
				)
		);
	}

	private static int method_20825(class_3915 arg, class_4311 arg2, Collection<ServerPlayerEntity> collection, int i) throws CommandSyntaxException {
		for (ServerPlayerEntity serverPlayerEntity : collection) {
			int j = i;

			while (j > 0) {
				int k = Math.min(arg2.method_19701().getMaxCount(), j);
				j -= k;
				ItemStack itemStack = arg2.method_19702(k, false);
				boolean bl = serverPlayerEntity.inventory.insertStack(itemStack);
				if (bl && itemStack.isEmpty()) {
					itemStack.setCount(1);
					ItemEntity itemEntity2 = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity2 != null) {
						itemEntity2.setDespawnImmediately();
					}

					serverPlayerEntity.world
						.playSound(
							null,
							serverPlayerEntity.x,
							serverPlayerEntity.y,
							serverPlayerEntity.z,
							Sounds.ENTITY_ITEM_PICKUP,
							SoundCategory.PLAYERS,
							0.2F,
							((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					serverPlayerEntity.playerScreenHandler.sendContentUpdates();
				} else {
					ItemEntity itemEntity = serverPlayerEntity.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.method_15847(serverPlayerEntity.getUuid());
					}
				}
			}
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText(
					"commands.give.success.single", i, arg2.method_19702(i, false).toHoverableText(), ((ServerPlayerEntity)collection.iterator().next()).getName()
				),
				true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.give.success.single", i, arg2.method_19702(i, false).toHoverableText(), collection.size()), true);
		}

		return collection.size();
	}
}
