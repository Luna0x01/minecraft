package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_3915;
import net.minecraft.class_4048;
import net.minecraft.class_4062;
import net.minecraft.class_4261;
import net.minecraft.class_4271;
import net.minecraft.class_4287;
import net.minecraft.class_4304;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class TeleportCommand {
	public static void method_21107(CommandDispatcher<class_3915> commandDispatcher) {
		LiteralCommandNode<class_3915> literalCommandNode = commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("teleport")
							.requires(arg -> arg.method_17575(2)))
						.then(
							((RequiredArgumentBuilder)CommandManager.method_17530("targets", class_4062.method_17899())
									.then(
										((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.method_17530("location", class_4287.method_19562())
													.executes(
														commandContext -> method_21106(
																(class_3915)commandContext.getSource(),
																class_4062.method_17901(commandContext, "targets"),
																((class_3915)commandContext.getSource()).method_17468(),
																class_4287.method_19566(commandContext, "location"),
																null,
																null
															)
													))
												.then(
													CommandManager.method_17530("rotation", class_4271.method_19435())
														.executes(
															commandContext -> method_21106(
																	(class_3915)commandContext.getSource(),
																	class_4062.method_17901(commandContext, "targets"),
																	((class_3915)commandContext.getSource()).method_17468(),
																	class_4287.method_19566(commandContext, "location"),
																	class_4271.method_19437(commandContext, "rotation"),
																	null
																)
														)
												))
											.then(
												((LiteralArgumentBuilder)CommandManager.method_17529("facing")
														.then(
															CommandManager.method_17529("entity")
																.then(
																	((RequiredArgumentBuilder)CommandManager.method_17530("facingEntity", class_4062.method_17894())
																			.executes(
																				commandContext -> method_21106(
																						(class_3915)commandContext.getSource(),
																						class_4062.method_17901(commandContext, "targets"),
																						((class_3915)commandContext.getSource()).method_17468(),
																						class_4287.method_19566(commandContext, "location"),
																						null,
																						new TeleportCommand.class_4434(class_4062.method_17898(commandContext, "facingEntity"), class_4048.class_4049.FEET)
																					)
																			))
																		.then(
																			CommandManager.method_17530("facingAnchor", class_4048.method_17865())
																				.executes(
																					commandContext -> method_21106(
																							(class_3915)commandContext.getSource(),
																							class_4062.method_17901(commandContext, "targets"),
																							((class_3915)commandContext.getSource()).method_17468(),
																							class_4287.method_19566(commandContext, "location"),
																							null,
																							new TeleportCommand.class_4434(
																								class_4062.method_17898(commandContext, "facingEntity"), class_4048.method_17867(commandContext, "facingAnchor")
																							)
																						)
																				)
																		)
																)
														))
													.then(
														CommandManager.method_17530("facingLocation", class_4287.method_19562())
															.executes(
																commandContext -> method_21106(
																		(class_3915)commandContext.getSource(),
																		class_4062.method_17901(commandContext, "targets"),
																		((class_3915)commandContext.getSource()).method_17468(),
																		class_4287.method_19566(commandContext, "location"),
																		null,
																		new TeleportCommand.class_4434(class_4287.method_19564(commandContext, "facingLocation"))
																	)
															)
													)
											)
									))
								.then(
									CommandManager.method_17530("destination", class_4062.method_17894())
										.executes(
											commandContext -> method_21105(
													(class_3915)commandContext.getSource(), class_4062.method_17901(commandContext, "targets"), class_4062.method_17898(commandContext, "destination")
												)
										)
								)
						))
					.then(
						CommandManager.method_17530("location", class_4287.method_19562())
							.executes(
								commandContext -> method_21106(
										(class_3915)commandContext.getSource(),
										Collections.singleton(((class_3915)commandContext.getSource()).method_17470()),
										((class_3915)commandContext.getSource()).method_17468(),
										class_4287.method_19566(commandContext, "location"),
										class_4304.method_19638(),
										null
									)
							)
					))
				.then(
					CommandManager.method_17530("destination", class_4062.method_17894())
						.executes(
							commandContext -> method_21105(
									(class_3915)commandContext.getSource(),
									Collections.singleton(((class_3915)commandContext.getSource()).method_17470()),
									class_4062.method_17898(commandContext, "destination")
								)
						)
				)
		);
		commandDispatcher.register(
			(LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.method_17529("tp").requires(arg -> arg.method_17575(2))).redirect(literalCommandNode)
		);
	}

	private static int method_21105(class_3915 arg, Collection<? extends Entity> collection, Entity entity) {
		for (Entity entity2 : collection) {
			method_21104(
				arg, entity2, arg.method_17468(), entity.x, entity.y, entity.z, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class), entity.yaw, entity.pitch, null
			);
		}

		if (collection.size() == 1) {
			arg.method_17459(new TranslatableText("commands.teleport.success.entity.single", ((Entity)collection.iterator().next()).getName(), entity.getName()), true);
		} else {
			arg.method_17459(new TranslatableText("commands.teleport.success.entity.multiple", collection.size(), entity.getName()), true);
		}

		return collection.size();
	}

	private static int method_21106(
		class_3915 arg,
		Collection<? extends Entity> collection,
		ServerWorld serverWorld,
		class_4261 arg2,
		@Nullable class_4261 arg3,
		@Nullable TeleportCommand.class_4434 arg4
	) throws CommandSyntaxException {
		Vec3d vec3d = arg2.method_19411(arg);
		Vec2f vec2f = arg3 == null ? null : arg3.method_19413(arg);
		Set<PlayerPositionLookS2CPacket.Flag> set = EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class);
		if (arg2.method_19410()) {
			set.add(PlayerPositionLookS2CPacket.Flag.X);
		}

		if (arg2.method_19412()) {
			set.add(PlayerPositionLookS2CPacket.Flag.Y);
		}

		if (arg2.method_19414()) {
			set.add(PlayerPositionLookS2CPacket.Flag.Z);
		}

		if (arg3 == null) {
			set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
			set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
		} else {
			if (arg3.method_19410()) {
				set.add(PlayerPositionLookS2CPacket.Flag.X_ROT);
			}

			if (arg3.method_19412()) {
				set.add(PlayerPositionLookS2CPacket.Flag.Y_ROT);
			}
		}

		for (Entity entity : collection) {
			if (arg3 == null) {
				method_21104(arg, entity, serverWorld, vec3d.x, vec3d.y, vec3d.z, set, entity.yaw, entity.pitch, arg4);
			} else {
				method_21104(arg, entity, serverWorld, vec3d.x, vec3d.y, vec3d.z, set, vec2f.y, vec2f.x, arg4);
			}
		}

		if (collection.size() == 1) {
			arg.method_17459(
				new TranslatableText("commands.teleport.success.location.single", ((Entity)collection.iterator().next()).getName(), vec3d.x, vec3d.y, vec3d.z), true
			);
		} else {
			arg.method_17459(new TranslatableText("commands.teleport.success.location.multiple", collection.size(), vec3d.x, vec3d.y, vec3d.z), true);
		}

		return collection.size();
	}

	private static void method_21104(
		class_3915 arg,
		Entity entity,
		ServerWorld serverWorld,
		double d,
		double e,
		double f,
		Set<PlayerPositionLookS2CPacket.Flag> set,
		float g,
		float h,
		@Nullable TeleportCommand.class_4434 arg2
	) {
		if (entity instanceof ServerPlayerEntity) {
			entity.stopRiding();
			if (((ServerPlayerEntity)entity).isSleeping()) {
				((ServerPlayerEntity)entity).awaken(true, true, false);
			}

			if (serverWorld == entity.world) {
				((ServerPlayerEntity)entity).networkHandler.teleportRequest(d, e, f, g, h, set);
			} else {
				((ServerPlayerEntity)entity).method_21282(serverWorld, d, e, f, g, h);
			}

			entity.setHeadYaw(g);
		} else {
			float i = MathHelper.wrapDegrees(g);
			float j = MathHelper.wrapDegrees(h);
			j = MathHelper.clamp(j, -90.0F, 90.0F);
			if (serverWorld == entity.world) {
				entity.refreshPositionAndAngles(d, e, f, i, j);
				entity.setHeadYaw(i);
			} else {
				ServerWorld serverWorld2 = (ServerWorld)entity.world;
				serverWorld2.removeEntity(entity);
				entity.field_16696 = serverWorld.dimension.method_11789();
				entity.removed = false;
				Entity entity2 = entity;
				entity = entity.method_15557().spawn(serverWorld);
				if (entity == null) {
					return;
				}

				entity.copyPortalInfo(entity2);
				entity.refreshPositionAndAngles(d, e, f, i, j);
				entity.setHeadYaw(i);
				boolean bl = entity.teleporting;
				entity.teleporting = true;
				serverWorld.method_3686(entity);
				entity.teleporting = bl;
				serverWorld.checkChunk(entity, false);
				entity2.removed = true;
			}
		}

		if (arg2 != null) {
			arg2.method_21117(arg, entity);
		}

		if (!(entity instanceof LivingEntity) || !((LivingEntity)entity).method_13055()) {
			entity.velocityY = 0.0;
			entity.onGround = true;
		}
	}

	static class class_4434 {
		private final Vec3d field_21806;
		private final Entity field_21807;
		private final class_4048.class_4049 field_21808;

		public class_4434(Entity entity, class_4048.class_4049 arg) {
			this.field_21807 = entity;
			this.field_21808 = arg;
			this.field_21806 = arg.method_17870(entity);
		}

		public class_4434(Vec3d vec3d) {
			this.field_21807 = null;
			this.field_21806 = vec3d;
			this.field_21808 = null;
		}

		public void method_21117(class_3915 arg, Entity entity) {
			if (this.field_21807 != null) {
				if (entity instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity).method_21274(arg.method_17474(), this.field_21807, this.field_21808);
				} else {
					entity.method_15563(arg.method_17474(), this.field_21806);
				}
			} else {
				entity.method_15563(arg.method_17474(), this.field_21806);
			}
		}
	}
}
