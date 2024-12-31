package net.minecraft.server.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4202;
import net.minecraft.class_4252;
import net.minecraft.class_4310;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ReplaceItemCommand {
	private static final SimpleCommandExceptionType field_21771 = new SimpleCommandExceptionType(new TranslatableText("commands.replaceitem.block.failed"));
	private static final DynamicCommandExceptionType field_21772 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.replaceitem.slot.inapplicable", object)
	);
	private static final Dynamic2CommandExceptionType field_21773 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.replaceitem.entity.failed", object, object2)
	);

	public static void method_20924(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("replaceitem").requires(arg -> arg.method_17575(2)))
					.then(
						CommandManager.method_17529("block")
							.then(
								CommandManager.method_17530("pos", class_4252.method_19358())
									.then(
										CommandManager.method_17530("slot", class_4202.method_18948())
											.then(
												((RequiredArgumentBuilder)CommandManager.method_17530("item", class_4310.method_19698())
														.executes(
															commandContext -> method_20922(
																	(class_3915)commandContext.getSource(),
																	class_4252.method_19360(commandContext, "pos"),
																	class_4202.method_18950(commandContext, "slot"),
																	class_4310.method_19700(commandContext, "item").method_19702(1, false)
																)
														))
													.then(
														CommandManager.method_17530("count", IntegerArgumentType.integer(1, 64))
															.executes(
																commandContext -> method_20922(
																		(class_3915)commandContext.getSource(),
																		class_4252.method_19360(commandContext, "pos"),
																		class_4202.method_18950(commandContext, "slot"),
																		class_4310.method_19700(commandContext, "item").method_19702(IntegerArgumentType.getInteger(commandContext, "count"), true)
																	)
															)
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("entity")
						.then(
							CommandManager.method_17530("targets", class_4062.method_17899())
								.then(
									CommandManager.method_17530("slot", class_4202.method_18948())
										.then(
											((RequiredArgumentBuilder)CommandManager.method_17530("item", class_4310.method_19698())
													.executes(
														commandContext -> method_20923(
																(class_3915)commandContext.getSource(),
																class_4062.method_17901(commandContext, "targets"),
																class_4202.method_18950(commandContext, "slot"),
																class_4310.method_19700(commandContext, "item").method_19702(1, false)
															)
													))
												.then(
													CommandManager.method_17530("count", IntegerArgumentType.integer(1, 64))
														.executes(
															commandContext -> method_20923(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17901(commandContext, "targets"),
																	class_4202.method_18950(commandContext, "slot"),
																	class_4310.method_19700(commandContext, "item").method_19702(IntegerArgumentType.getInteger(commandContext, "count"), true)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_20922(class_3915 arg, BlockPos blockPos, int i, ItemStack itemStack) throws CommandSyntaxException {
		BlockEntity blockEntity = arg.method_17468().getBlockEntity(blockPos);
		if (!(blockEntity instanceof Inventory)) {
			throw field_21771.create();
		} else {
			Inventory inventory = (Inventory)blockEntity;
			if (i >= 0 && i < inventory.getInvSize()) {
				inventory.setInvStack(i, itemStack);
				arg.method_17459(
					new TranslatableText("commands.replaceitem.block.success", blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack.toHoverableText()), true
				);
				return 1;
			} else {
				throw field_21772.create(i);
			}
		}
	}

	private static int method_20923(class_3915 arg, Collection<? extends Entity> collection, int i, ItemStack itemStack) throws CommandSyntaxException {
		List<Entity> list = Lists.newArrayListWithCapacity(collection.size());

		for (Entity entity : collection) {
			if (entity instanceof ServerPlayerEntity) {
				((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
			}

			if (entity.equip(i, itemStack.copy())) {
				list.add(entity);
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).playerScreenHandler.sendContentUpdates();
				}
			}
		}

		if (list.isEmpty()) {
			throw field_21773.create(itemStack.toHoverableText(), i);
		} else {
			if (list.size() == 1) {
				arg.method_17459(
					new TranslatableText("commands.replaceitem.entity.success.single", ((Entity)list.iterator().next()).getName(), itemStack.toHoverableText()), true
				);
			} else {
				arg.method_17459(new TranslatableText("commands.replaceitem.entity.success.multiple", list.size(), itemStack.toHoverableText()), true);
			}

			return list.size();
		}
	}
}
