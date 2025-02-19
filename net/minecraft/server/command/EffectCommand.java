package net.minecraft.server.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.StatusEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.TranslatableText;

public class EffectCommand {
	private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.effect.give.failed"));
	private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.effect.clear.everything.failed")
	);
	private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(
		new TranslatableText("commands.effect.clear.specific.failed")
	);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("effect").requires(source -> source.hasPermissionLevel(2)))
					.then(
						((LiteralArgumentBuilder)CommandManager.literal("clear")
								.executes(
									context -> executeClear((ServerCommandSource)context.getSource(), ImmutableList.of(((ServerCommandSource)context.getSource()).getEntityOrThrow()))
								))
							.then(
								((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.entities())
										.executes(context -> executeClear((ServerCommandSource)context.getSource(), EntityArgumentType.getEntities(context, "targets"))))
									.then(
										CommandManager.argument("effect", StatusEffectArgumentType.statusEffect())
											.executes(
												context -> executeClear(
														(ServerCommandSource)context.getSource(),
														EntityArgumentType.getEntities(context, "targets"),
														StatusEffectArgumentType.getStatusEffect(context, "effect")
													)
											)
									)
							)
					))
				.then(
					CommandManager.literal("give")
						.then(
							CommandManager.argument("targets", EntityArgumentType.entities())
								.then(
									((RequiredArgumentBuilder)CommandManager.argument("effect", StatusEffectArgumentType.statusEffect())
											.executes(
												context -> executeGive(
														(ServerCommandSource)context.getSource(),
														EntityArgumentType.getEntities(context, "targets"),
														StatusEffectArgumentType.getStatusEffect(context, "effect"),
														null,
														0,
														true
													)
											))
										.then(
											((RequiredArgumentBuilder)CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000))
													.executes(
														context -> executeGive(
																(ServerCommandSource)context.getSource(),
																EntityArgumentType.getEntities(context, "targets"),
																StatusEffectArgumentType.getStatusEffect(context, "effect"),
																IntegerArgumentType.getInteger(context, "seconds"),
																0,
																true
															)
													))
												.then(
													((RequiredArgumentBuilder)CommandManager.argument("amplifier", IntegerArgumentType.integer(0, 255))
															.executes(
																context -> executeGive(
																		(ServerCommandSource)context.getSource(),
																		EntityArgumentType.getEntities(context, "targets"),
																		StatusEffectArgumentType.getStatusEffect(context, "effect"),
																		IntegerArgumentType.getInteger(context, "seconds"),
																		IntegerArgumentType.getInteger(context, "amplifier"),
																		true
																	)
															))
														.then(
															CommandManager.argument("hideParticles", BoolArgumentType.bool())
																.executes(
																	context -> executeGive(
																			(ServerCommandSource)context.getSource(),
																			EntityArgumentType.getEntities(context, "targets"),
																			StatusEffectArgumentType.getStatusEffect(context, "effect"),
																			IntegerArgumentType.getInteger(context, "seconds"),
																			IntegerArgumentType.getInteger(context, "amplifier"),
																			!BoolArgumentType.getBool(context, "hideParticles")
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

	private static int executeGive(
		ServerCommandSource source, Collection<? extends Entity> targets, StatusEffect effect, @Nullable Integer seconds, int amplifier, boolean showParticles
	) throws CommandSyntaxException {
		int i = 0;
		int j;
		if (seconds != null) {
			if (effect.isInstant()) {
				j = seconds;
			} else {
				j = seconds * 20;
			}
		} else if (effect.isInstant()) {
			j = 1;
		} else {
			j = 600;
		}

		for (Entity entity : targets) {
			if (entity instanceof LivingEntity) {
				StatusEffectInstance statusEffectInstance = new StatusEffectInstance(effect, j, amplifier, false, showParticles);
				if (((LivingEntity)entity).addStatusEffect(statusEffectInstance, source.getEntity())) {
					i++;
				}
			}
		}

		if (i == 0) {
			throw GIVE_FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(
					new TranslatableText("commands.effect.give.success.single", effect.getName(), ((Entity)targets.iterator().next()).getDisplayName(), j / 20), true
				);
			} else {
				source.sendFeedback(new TranslatableText("commands.effect.give.success.multiple", effect.getName(), targets.size(), j / 20), true);
			}

			return i;
		}
	}

	private static int executeClear(ServerCommandSource source, Collection<? extends Entity> targets) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : targets) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).clearStatusEffects()) {
				i++;
			}
		}

		if (i == 0) {
			throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.single", ((Entity)targets.iterator().next()).getDisplayName()), true);
			} else {
				source.sendFeedback(new TranslatableText("commands.effect.clear.everything.success.multiple", targets.size()), true);
			}

			return i;
		}
	}

	private static int executeClear(ServerCommandSource source, Collection<? extends Entity> targets, StatusEffect effect) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : targets) {
			if (entity instanceof LivingEntity && ((LivingEntity)entity).removeStatusEffect(effect)) {
				i++;
			}
		}

		if (i == 0) {
			throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(
					new TranslatableText("commands.effect.clear.specific.success.single", effect.getName(), ((Entity)targets.iterator().next()).getDisplayName()), true
				);
			} else {
				source.sendFeedback(new TranslatableText("commands.effect.clear.specific.success.multiple", effect.getName(), targets.size()), true);
			}

			return i;
		}
	}
}
