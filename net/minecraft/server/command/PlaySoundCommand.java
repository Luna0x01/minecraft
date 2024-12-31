package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.class_3915;
import net.minecraft.class_4062;
import net.minecraft.class_4181;
import net.minecraft.class_4287;
import net.minecraft.class_4327;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundNameS2CPacket;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PlaySoundCommand {
	private static final SimpleCommandExceptionType field_21766 = new SimpleCommandExceptionType(new TranslatableText("commands.playsound.failed"));

	public static void method_20897(CommandDispatcher<class_3915> commandDispatcher) {
		RequiredArgumentBuilder<class_3915, Identifier> requiredArgumentBuilder = CommandManager.method_17530("sound", class_4181.method_18904())
			.suggests(class_4327.field_21256);

		for (SoundCategory soundCategory : SoundCategory.values()) {
			requiredArgumentBuilder.then(method_20898(soundCategory));
		}

		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("playsound").requires(arg -> arg.method_17575(2)))
				.then(requiredArgumentBuilder)
		);
	}

	private static LiteralArgumentBuilder<class_3915> method_20898(SoundCategory soundCategory) {
		return (LiteralArgumentBuilder<class_3915>)CommandManager.method_17529(soundCategory.getName())
			.then(
				((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17904())
						.executes(
							commandContext -> method_20896(
									(class_3915)commandContext.getSource(),
									class_4062.method_17907(commandContext, "targets"),
									class_4181.method_18910(commandContext, "sound"),
									soundCategory,
									((class_3915)commandContext.getSource()).method_17467(),
									1.0F,
									1.0F,
									0.0F
								)
						))
					.then(
						((RequiredArgumentBuilder)CommandManager.method_17530("pos", class_4287.method_19562())
								.executes(
									commandContext -> method_20896(
											(class_3915)commandContext.getSource(),
											class_4062.method_17907(commandContext, "targets"),
											class_4181.method_18910(commandContext, "sound"),
											soundCategory,
											class_4287.method_19564(commandContext, "pos"),
											1.0F,
											1.0F,
											0.0F
										)
								))
							.then(
								((RequiredArgumentBuilder)CommandManager.method_17530("volume", FloatArgumentType.floatArg(0.0F))
										.executes(
											commandContext -> method_20896(
													(class_3915)commandContext.getSource(),
													class_4062.method_17907(commandContext, "targets"),
													class_4181.method_18910(commandContext, "sound"),
													soundCategory,
													class_4287.method_19564(commandContext, "pos"),
													(Float)commandContext.getArgument("volume", Float.class),
													1.0F,
													0.0F
												)
										))
									.then(
										((RequiredArgumentBuilder)CommandManager.method_17530("pitch", FloatArgumentType.floatArg(0.0F, 2.0F))
												.executes(
													commandContext -> method_20896(
															(class_3915)commandContext.getSource(),
															class_4062.method_17907(commandContext, "targets"),
															class_4181.method_18910(commandContext, "sound"),
															soundCategory,
															class_4287.method_19564(commandContext, "pos"),
															(Float)commandContext.getArgument("volume", Float.class),
															(Float)commandContext.getArgument("pitch", Float.class),
															0.0F
														)
												))
											.then(
												CommandManager.method_17530("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F))
													.executes(
														commandContext -> method_20896(
																(class_3915)commandContext.getSource(),
																class_4062.method_17907(commandContext, "targets"),
																class_4181.method_18910(commandContext, "sound"),
																soundCategory,
																class_4287.method_19564(commandContext, "pos"),
																(Float)commandContext.getArgument("volume", Float.class),
																(Float)commandContext.getArgument("pitch", Float.class),
																(Float)commandContext.getArgument("minVolume", Float.class)
															)
													)
											)
									)
							)
					)
			);
	}

	private static int method_20896(
		class_3915 arg, Collection<ServerPlayerEntity> collection, Identifier identifier, SoundCategory soundCategory, Vec3d vec3d, float f, float g, float h
	) throws CommandSyntaxException {
		double d = Math.pow(f > 1.0F ? (double)(f * 16.0F) : 16.0, 2.0);
		int i = 0;

		for (ServerPlayerEntity serverPlayerEntity : collection) {
			double e = vec3d.x - serverPlayerEntity.x;
			double j = vec3d.y - serverPlayerEntity.y;
			double k = vec3d.z - serverPlayerEntity.z;
			double l = e * e + j * j + k * k;
			Vec3d vec3d2 = vec3d;
			float m = f;
			if (l > d) {
				if (h <= 0.0F) {
					continue;
				}

				double n = (double)MathHelper.sqrt(l);
				vec3d2 = new Vec3d(serverPlayerEntity.x + e / n * 2.0, serverPlayerEntity.y + j / n * 2.0, serverPlayerEntity.z + k / n * 2.0);
				m = h;
			}

			serverPlayerEntity.networkHandler.sendPacket(new PlaySoundNameS2CPacket(identifier, soundCategory, vec3d2, m, g));
			i++;
		}

		if (i == 0) {
			throw field_21766.create();
		} else {
			if (collection.size() == 1) {
				arg.method_17459(new TranslatableText("commands.playsound.success.single", identifier, ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
			} else {
				arg.method_17459(new TranslatableText("commands.playsound.success.single", identifier, ((ServerPlayerEntity)collection.iterator().next()).getName()), true);
			}

			return i;
		}
	}
}
