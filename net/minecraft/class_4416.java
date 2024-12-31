package net.minecraft;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.TranslatableText;

public class class_4416 {
	private static final SimpleCommandExceptionType field_21725 = new SimpleCommandExceptionType(new TranslatableText("commands.effect.give.failed"));
	private static final SimpleCommandExceptionType field_21726 = new SimpleCommandExceptionType(new TranslatableText("commands.effect.clear.everything.failed"));
	private static final SimpleCommandExceptionType field_21727 = new SimpleCommandExceptionType(new TranslatableText("commands.effect.clear.specific.failed"));

	public static void method_20667(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("effect").requires(arg -> arg.method_17575(2)))
					.then(
						CommandManager.method_17529("clear")
							.then(
								((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17899())
										.executes(commandContext -> method_20664((class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets"))))
									.then(
										CommandManager.method_17530("effect", class_4114.method_18275())
											.executes(
												commandContext -> method_20665(
														(class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets"), class_4114.method_18277(commandContext, "effect")
													)
											)
									)
							)
					))
				.then(
					CommandManager.method_17529("give")
						.then(
							CommandManager.method_17530("targets", class_4062.method_17899())
								.then(
									((RequiredArgumentBuilder)CommandManager.method_17530("effect", class_4114.method_18275())
											.executes(
												commandContext -> method_20666(
														(class_3915)commandContext.getSource(),
														class_4062.method_17901(commandContext, "targets"),
														class_4114.method_18277(commandContext, "effect"),
														null,
														0,
														true
													)
											))
										.then(
											((RequiredArgumentBuilder)CommandManager.method_17530("seconds", IntegerArgumentType.integer(1, 1000000))
													.executes(
														commandContext -> method_20666(
																(class_3915)commandContext.getSource(),
																class_4062.method_17901(commandContext, "targets"),
																class_4114.method_18277(commandContext, "effect"),
																IntegerArgumentType.getInteger(commandContext, "seconds"),
																0,
																true
															)
													))
												.then(
													((RequiredArgumentBuilder)CommandManager.method_17530("amplifier", IntegerArgumentType.integer(0, 255))
															.executes(
																commandContext -> method_20666(
																		(class_3915)commandContext.getSource(),
																		class_4062.method_17901(commandContext, "targets"),
																		class_4114.method_18277(commandContext, "effect"),
																		IntegerArgumentType.getInteger(commandContext, "seconds"),
																		IntegerArgumentType.getInteger(commandContext, "amplifier"),
																		true
																	)
															))
														.then(
															CommandManager.method_17530("hideParticles", BoolArgumentType.bool())
																.executes(
																	commandContext -> method_20666(
																			(class_3915)commandContext.getSource(),
																			class_4062.method_17901(commandContext, "targets"),
																			class_4114.method_18277(commandContext, "effect"),
																			IntegerArgumentType.getInteger(commandContext, "seconds"),
																			IntegerArgumentType.getInteger(commandContext, "amplifier"),
																			!BoolArgumentType.getBool(commandContext, "hideParticles")
																		)
																)
														)
												)
										)
								)
						)
				)
		);
	}

	private static int method_20666(
		class_3915 arg, Collection<? extends Entity> collection, StatusEffect statusEffect, @Nullable Integer integer, int i, boolean bl
	) throws CommandSyntaxException {
		int j = 0;
		int k;
		if (integer != null) {
			if (statusEffect.isInstant()) {
				k = integer;
			} else {
				k = integer * 20;
			}
		} else if (statusEffect.isInstant()) {
			k = 1;
		} else {
			k = 600;
		}

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(statusEffect, k, i, false, bl);
				if (((LivingEntity)entity).method_2654(statusEffectInstance)) {
					j++;
				}
			}
		}

		if (j == 0) {
			throw field_21725.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(
					new TranslatableText("commands.effect.give.success.single", statusEffect.method_15550(), ((Entity)collection.iterator().next()).getName(), k / 20), true
				);
			} else {
				arg.method_17459(new TranslatableText("commands.effect.give.success.multiple", statusEffect.method_15550(), collection.size(), k / 20), true);
			}

			return j;
		}
	}

	private static int method_20664(class_3915 arg, Collection<? extends Entity> collection) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).method_6119()) {
				i++;
			}
		}

		if (i == 0) {
			throw field_21726.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(new TranslatableText("commands.effect.clear.everything.success.single", ((Entity)collection.iterator().next()).getName()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.effect.clear.everything.success.multiple", collection.size()), true);
			}

			return i;
		}
	}

	private static int method_20665(class_3915 arg, Collection<? extends Entity> collection, StatusEffect statusEffect) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : collection) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).method_13069(statusEffect)) {
				i++;
			}
		}

		if (i == 0) {
			throw field_21727.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(
					new TranslatableText("commands.effect.clear.specific.success.single", statusEffect.method_15550(), ((Entity)collection.iterator().next()).getName()), true
				);
			} else {
				arg.method_17459(new TranslatableText("commands.effect.clear.specific.success.multiple", statusEffect.method_15550(), collection.size()), true);
			}

			return i;
		}
	}
}
