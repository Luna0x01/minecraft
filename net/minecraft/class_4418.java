package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4418 {
	private static final DynamicCommandExceptionType field_21728 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.enchant.failed.entity", object)
	);
	private static final DynamicCommandExceptionType field_21729 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.enchant.failed.itemless", object)
	);
	private static final DynamicCommandExceptionType field_21730 = new DynamicCommandExceptionType(
		object -> new TranslatableText("commands.enchant.failed.incompatible", object)
	);
	private static final Dynamic2CommandExceptionType field_21731 = new Dynamic2CommandExceptionType(
		(object, object2) -> new TranslatableText("commands.enchant.failed.level", object, object2)
	);
	private static final SimpleCommandExceptionType field_21732 = new SimpleCommandExceptionType(new TranslatableText("commands.enchant.failed"));

	public static void method_20678(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("enchant").requires(arg -> arg.method_17575(2)))
				.then(
					CommandManager.method_17530("targets", class_4062.method_17899())
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("enchantment", class_4078.method_17997())
									.executes(
										commandContext -> method_20677(
												(class_3915)commandContext.getSource(),
												class_4062.method_17901(commandContext, "targets"),
												class_4078.method_17999(commandContext, "enchantment"),
												1
											)
									))
								.then(
									CommandManager.method_17530("level", IntegerArgumentType.integer(0))
										.executes(
											commandContext -> method_20677(
													(class_3915)commandContext.getSource(),
													class_4062.method_17901(commandContext, "targets"),
													class_4078.method_17999(commandContext, "enchantment"),
													IntegerArgumentType.getInteger(commandContext, "level")
												)
										)
								)
						)
				)
		);
	}

	private static int method_20677(class_3915 arg, Collection<? extends Entity> collection, Enchantment enchantment, int i) throws CommandSyntaxException {
		if (i > enchantment.getMaximumLevel()) {
			throw field_21731.create(i, enchantment.getMaximumLevel());
		} else {
			int j = 0;

			for (Entity entity : collection) {
				if (entity instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity)entity;
					ItemStack itemStack = livingEntity.getMainHandStack();
					if (!itemStack.isEmpty()) {
						if (enchantment.isAcceptableItem(itemStack) && EnchantmentHelper.method_16262(EnchantmentHelper.get(itemStack).keySet(), enchantment)) {
							itemStack.addEnchantment(enchantment, i);
							j++;
						} else if (collection.size() == 1) {
							throw field_21730.create(itemStack.getItem().getDisplayName(itemStack).getString());
						}
					} else if (collection.size() == 1) {
						throw field_21729.create(livingEntity.method_15540().getString());
					}
				} else if (collection.size() == 1) {
					throw field_21728.create(entity.method_15540().getString());
				}
			}

			if (j == 0) {
				throw field_21732.create();
			} else {
				if (collection.size() == 1) {
					arg.method_17459(
						new TranslatableText("commands.enchant.success.single", enchantment.method_16257(i), ((Entity)collection.iterator().next()).getName()), true
					);
				} else {
					arg.method_17459(new TranslatableText("commands.enchant.success.multiple", enchantment.method_16257(i), collection.size()), true);
				}

				return j;
			}
		}
	}
}
