package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4168;
import net.minecraft.class_4287;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

public class ParticleCommand {
	private static final SimpleCommandExceptionType field_21765 = new SimpleCommandExceptionType(new TranslatableText("commands.particle.failed"));

	public static void method_20887(CommandDispatcher<class_3915> commandDispatcher) {
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("particle").requires(arg -> arg.method_17575(2)))
				.then(
					((RequiredArgumentBuilder)CommandManager.method_17530("name", class_4168.method_18780())
							.executes(
								commandContext -> method_20886(
										(class_3915)commandContext.getSource(),
										class_4168.method_18783(commandContext, "name"),
										((class_3915)commandContext.getSource()).method_17467(),
										Vec3d.ZERO,
										0.0F,
										0,
										false,
										((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getPlayers()
									)
							))
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("pos", class_4287.method_19562())
									.executes(
										commandContext -> method_20886(
												(class_3915)commandContext.getSource(),
												class_4168.method_18783(commandContext, "name"),
												class_4287.method_19564(commandContext, "pos"),
												Vec3d.ZERO,
												0.0F,
												0,
												false,
												((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getPlayers()
											)
									))
								.then(
									CommandManager.method_17530("delta", class_4287.method_19565(false))
										.then(
											CommandManager.method_17530("speed", FloatArgumentType.floatArg(0.0F))
												.then(
													((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("count", IntegerArgumentType.integer(0))
																.executes(
																	commandContext -> method_20886(
																			(class_3915)commandContext.getSource(),
																			class_4168.method_18783(commandContext, "name"),
																			class_4287.method_19564(commandContext, "pos"),
																			class_4287.method_19564(commandContext, "delta"),
																			FloatArgumentType.getFloat(commandContext, "speed"),
																			IntegerArgumentType.getInteger(commandContext, "count"),
																			false,
																			((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getPlayers()
																		)
																))
															.then(
																((LiteralArgumentBuilder)CommandManager.method_17529("force")
																		.executes(
																			commandContext -> method_20886(
																					(class_3915)commandContext.getSource(),
																					class_4168.method_18783(commandContext, "name"),
																					class_4287.method_19564(commandContext, "pos"),
																					class_4287.method_19564(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					true,
																					((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getPlayers()
																				)
																		))
																	.then(
																		CommandManager.method_17530("viewers", class_4062.method_17904())
																			.executes(
																				commandContext -> method_20886(
																						(class_3915)commandContext.getSource(),
																						class_4168.method_18783(commandContext, "name"),
																						class_4287.method_19564(commandContext, "pos"),
																						class_4287.method_19564(commandContext, "delta"),
																						FloatArgumentType.getFloat(commandContext, "speed"),
																						IntegerArgumentType.getInteger(commandContext, "count"),
																						true,
																						class_4062.method_17907(commandContext, "viewers")
																					)
																			)
																	)
															))
														.then(
															((LiteralArgumentBuilder)CommandManager.method_17529("normal")
																	.executes(
																		commandContext -> method_20886(
																				(class_3915)commandContext.getSource(),
																				class_4168.method_18783(commandContext, "name"),
																				class_4287.method_19564(commandContext, "pos"),
																				class_4287.method_19564(commandContext, "delta"),
																				FloatArgumentType.getFloat(commandContext, "speed"),
																				IntegerArgumentType.getInteger(commandContext, "count"),
																				false,
																				((class_3915)commandContext.getSource()).method_17473().getPlayerManager().getPlayers()
																			)
																	))
																.then(
																	CommandManager.method_17530("viewers", class_4062.method_17904())
																		.executes(
																			commandContext -> method_20886(
																					(class_3915)commandContext.getSource(),
																					class_4168.method_18783(commandContext, "name"),
																					class_4287.method_19564(commandContext, "pos"),
																					class_4287.method_19564(commandContext, "delta"),
																					FloatArgumentType.getFloat(commandContext, "speed"),
																					IntegerArgumentType.getInteger(commandContext, "count"),
																					false,
																					class_4062.method_17907(commandContext, "viewers")
																				)
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

	private static int method_20886(
		class_3915 arg, ParticleEffect particleEffect, Vec3d vec3d, Vec3d vec3d2, float f, int i, boolean bl, Collection<ServerPlayerEntity> collection
	) throws CommandSyntaxException {
		int j = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			if (arg.method_17468().method_21262(serverPlayerEntity, particleEffect, bl, vec3d.x, vec3d.y, vec3d.z, i, vec3d2.x, vec3d2.y, vec3d2.z, (double)f)) {
				j++;
			}
		}

		if (j == 0) {
			throw field_21765.create();
		} else {
			arg.method_17459(
				new TranslatableText(
					"commands.particle.success", Registry.PARTICLE_TYPE.getId((ParticleType<? extends ParticleEffect>)particleEffect.particleType()).toString()
				),
				true
			);
			return j;
		}
	}
}
