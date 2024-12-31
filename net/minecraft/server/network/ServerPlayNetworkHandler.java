package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.class_2971;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.SteerBoatC2SPacket;
import net.minecraft.network.packet.c2s.play.SwingHandC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.level.LevelInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener, Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection connection;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int lastTickMovePacketsCount;
	private int field_8934;
	private long lastKeepAliveTime;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private final IntObjectStorage<Short> transactions = new IntObjectStorage<>();
	private double field_13883;
	private double field_13884;
	private double field_13885;
	private double field_13886;
	private double field_13887;
	private double field_13888;
	private Entity field_13889;
	private double field_13890;
	private double field_13891;
	private double field_13892;
	private double field_13893;
	private double field_13894;
	private double field_13895;
	private Vec3d field_13896;
	private int field_13897;
	private int field_11776;
	private boolean field_13878;
	private int field_8932;
	private boolean field_13879;
	private int field_13880;
	private int field_13881;
	private int field_13882;

	public ServerPlayNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.connection = clientConnection;
		clientConnection.setPacketListener(this);
		this.player = serverPlayerEntity;
		serverPlayerEntity.networkHandler = this;
	}

	@Override
	public void tick() {
		this.method_12823();
		this.player.tickPlayer();
		this.player.updatePositionAndAngles(this.field_13883, this.field_13884, this.field_13885, this.player.yaw, this.player.pitch);
		this.lastTickMovePacketsCount++;
		this.field_13882 = this.field_13881;
		if (this.field_13878) {
			if (++this.field_8932 > 80) {
				LOGGER.warn(this.player.getTranslationKey() + " was kicked for floating too long!");
				this.disconnect("Flying is not enabled on this server");
				return;
			}
		} else {
			this.field_13878 = false;
			this.field_8932 = 0;
		}

		this.field_13889 = this.player.getRootVehicle();
		if (this.field_13889 != this.player && this.field_13889.getPrimaryPassenger() == this.player) {
			this.field_13890 = this.field_13889.x;
			this.field_13891 = this.field_13889.y;
			this.field_13892 = this.field_13889.z;
			this.field_13893 = this.field_13889.x;
			this.field_13894 = this.field_13889.y;
			this.field_13895 = this.field_13889.z;
			if (this.field_13879 && this.player.getRootVehicle().getPrimaryPassenger() == this.player) {
				if (++this.field_13880 > 80) {
					LOGGER.warn(this.player.getTranslationKey() + " was kicked for floating a vehicle too long!");
					this.disconnect("Flying is not enabled on this server");
					return;
				}
			} else {
				this.field_13879 = false;
				this.field_13880 = 0;
			}
		} else {
			this.field_13889 = null;
			this.field_13879 = false;
			this.field_13880 = 0;
		}

		this.server.profiler.push("keepAlive");
		if ((long)this.lastTickMovePacketsCount - this.keepAliveId > 40L) {
			this.keepAliveId = (long)this.lastTickMovePacketsCount;
			this.lastKeepAliveTime = this.method_8169();
			this.field_8934 = (int)this.lastKeepAliveTime;
			this.sendPacket(new KeepAliveS2CPacket(this.field_8934));
		}

		this.server.profiler.pop();
		if (this.messageCooldown > 0) {
			this.messageCooldown--;
		}

		if (this.creativeItemDropThreshold > 0) {
			this.creativeItemDropThreshold--;
		}

		if (this.player.getLastActionTime() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& MinecraftServer.getTimeMillis() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.disconnect("You have been idle for too long!");
		}
	}

	private void method_12823() {
		this.field_13883 = this.player.x;
		this.field_13884 = this.player.y;
		this.field_13885 = this.player.z;
		this.field_13886 = this.player.x;
		this.field_13887 = this.player.y;
		this.field_13888 = this.player.z;
	}

	public ClientConnection getConnection() {
		return this.connection;
	}

	public void disconnect(String reason) {
		final LiteralText literalText = new LiteralText(reason);
		this.connection.send(new DisconnectS2CPacket(literalText), new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> future) throws Exception {
				ServerPlayNetworkHandler.this.connection.disconnect(literalText);
			}
		});
		this.connection.disableAutoRead();
		Futures.getUnchecked(this.server.submit(new Runnable() {
			public void run() {
				ServerPlayNetworkHandler.this.connection.handleDisconnection();
			}
		}));
	}

	@Override
	public void onPlayerInput(PlayerInputC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.method_6066(packet.getSideways(), packet.getForward(), packet.isJumping(), packet.isSneaking());
	}

	private static boolean validatePlayerMove(PlayerMoveC2SPacket playerMoveC2SPacket) {
		return Doubles.isFinite(playerMoveC2SPacket.method_12687(0.0))
				&& Doubles.isFinite(playerMoveC2SPacket.method_12689(0.0))
				&& Doubles.isFinite(playerMoveC2SPacket.method_12691(0.0))
				&& Floats.isFinite(playerMoveC2SPacket.method_12690(0.0F))
				&& Floats.isFinite(playerMoveC2SPacket.method_12688(0.0F))
			? false
			: !(Math.abs(playerMoveC2SPacket.method_12687(0.0)) > 3.0E7) && !(Math.abs(playerMoveC2SPacket.method_12687(0.0)) > 3.0E7);
	}

	private static boolean method_12822(VehicleMoveC2SPacket packet) {
		return !Doubles.isFinite(packet.getX())
			|| !Doubles.isFinite(packet.getY())
			|| !Doubles.isFinite(packet.getZ())
			|| !Floats.isFinite(packet.getPitch())
			|| !Floats.isFinite(packet.getYaw());
	}

	@Override
	public void onVehicleMove(VehicleMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (method_12822(packet)) {
			this.disconnect("Invalid move vehicle packet received");
		} else {
			Entity entity = this.player.getRootVehicle();
			if (entity != this.player && entity.getPrimaryPassenger() == this.player && entity == this.field_13889) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.x;
				double e = entity.y;
				double f = entity.z;
				double g = packet.getX();
				double h = packet.getY();
				double i = packet.getZ();
				float j = packet.getYaw();
				float k = packet.getPitch();
				double l = g - this.field_13890;
				double m = h - this.field_13891;
				double n = i - this.field_13892;
				double o = entity.velocityX * entity.velocityX + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ;
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(entity.getTranslationKey()))) {
					LOGGER.warn(entity.getTranslationKey() + " (vehicle of " + this.player.getTranslationKey() + ") moved too quickly! " + l + "," + m + "," + n);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.doesBoxCollide(entity, entity.getBoundingBox().contract(0.0625)).isEmpty();
				l = g - this.field_13893;
				m = h - this.field_13894 - 1.0E-6;
				n = i - this.field_13895;
				entity.move(l, m, n);
				l = g - entity.x;
				m = h - entity.y;
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.z;
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn(entity.getTranslationKey() + " moved wrongly!");
				}

				entity.updatePositionAndAngles(g, h, i, j, k);
				boolean bl3 = serverWorld.doesBoxCollide(entity, entity.getBoundingBox().contract(0.0625)).isEmpty();
				if (bl && (bl2 || !bl3)) {
					entity.updatePositionAndAngles(d, e, f, j, k);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				this.server.getPlayerManager().method_2003(this.player);
				this.player.method_3209(this.player.x - d, this.player.y - e, this.player.z - f);
				this.field_13879 = m >= -0.03125
					&& !this.server.isFlightEnabled()
					&& !serverWorld.isBoxNotEmpty(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0));
				this.field_13893 = entity.x;
				this.field_13894 = entity.y;
				this.field_13895 = entity.z;
			}
		}
	}

	@Override
	public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getTeleportId() == this.field_13897) {
			this.player.updatePositionAndAngles(this.field_13896.x, this.field_13896.y, this.field_13896.z, this.player.yaw, this.player.pitch);
			if (this.player.method_12784()) {
				this.field_13886 = this.field_13896.x;
				this.field_13887 = this.field_13896.y;
				this.field_13888 = this.field_13896.z;
				this.player.method_12785();
			}

			this.field_13896 = null;
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (validatePlayerMove(packet)) {
			this.disconnect("Invalid move player packet received");
		} else {
			ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
			if (!this.player.killedEnderdragon) {
				if (this.lastTickMovePacketsCount == 0) {
					this.method_12823();
				}

				if (this.field_13896 != null) {
					if (this.lastTickMovePacketsCount - this.field_11776 > 20) {
						this.field_11776 = this.lastTickMovePacketsCount;
						this.requestTeleport(this.field_13896.x, this.field_13896.y, this.field_13896.z, this.player.yaw, this.player.pitch);
					}
				} else {
					this.field_11776 = this.lastTickMovePacketsCount;
					if (this.player.hasMount()) {
						this.player
							.updatePositionAndAngles(this.player.x, this.player.y, this.player.z, packet.method_12688(this.player.yaw), packet.method_12690(this.player.pitch));
						this.server.getPlayerManager().method_2003(this.player);
					} else {
						double d = this.player.x;
						double e = this.player.y;
						double f = this.player.z;
						double g = this.player.y;
						double h = packet.method_12687(this.player.x);
						double i = packet.method_12689(this.player.y);
						double j = packet.method_12691(this.player.z);
						float k = packet.method_12688(this.player.yaw);
						float l = packet.method_12690(this.player.pitch);
						double m = h - this.field_13883;
						double n = i - this.field_13884;
						double o = j - this.field_13885;
						double p = this.player.velocityX * this.player.velocityX + this.player.velocityY * this.player.velocityY + this.player.velocityZ * this.player.velocityZ;
						double q = m * m + n * n + o * o;
						this.field_13881++;
						int r = this.field_13881 - this.field_13882;
						if (r > 5) {
							LOGGER.debug(this.player.getTranslationKey() + " is sending move packets too frequently (" + r + " packets since last tick)");
							r = 1;
						}

						if (!this.player.method_12784() && (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.method_13055())
							)
						 {
							float s = this.player.method_13055() ? 300.0F : 100.0F;
							if (q - p > (double)(s * (float)r) && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(this.player.getTranslationKey()))) {
								LOGGER.warn(this.player.getTranslationKey() + " moved too quickly! " + m + "," + n + "," + o);
								this.requestTeleport(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
								return;
							}
						}

						boolean bl = serverWorld.doesBoxCollide(this.player, this.player.getBoundingBox().contract(0.0625)).isEmpty();
						m = h - this.field_13886;
						n = i - this.field_13887;
						o = j - this.field_13888;
						if (this.player.onGround && !packet.isOnGround() && n > 0.0) {
							this.player.jump();
						}

						this.player.move(m, n, o);
						this.player.onGround = packet.isOnGround();
						m = h - this.player.x;
						n = i - this.player.y;
						if (n > -0.5 || n < 0.5) {
							n = 0.0;
						}

						o = j - this.player.z;
						q = m * m + n * n + o * o;
						boolean bl2 = false;
						if (!this.player.method_12784()
							&& q > 0.0625
							&& !this.player.isSleeping()
							&& !this.player.interactionManager.isCreative()
							&& this.player.interactionManager.getGameMode() != LevelInfo.GameMode.SPECTATOR) {
							bl2 = true;
							LOGGER.warn(this.player.getTranslationKey() + " moved wrongly!");
						}

						this.player.updatePositionAndAngles(h, i, j, k, l);
						this.player.method_3209(this.player.x - d, this.player.y - e, this.player.z - f);
						if (!this.player.noClip && !this.player.isSleeping()) {
							boolean bl3 = serverWorld.doesBoxCollide(this.player, this.player.getBoundingBox().contract(0.0625)).isEmpty();
							if (bl && (bl2 || !bl3)) {
								this.requestTeleport(d, e, f, k, l);
								return;
							}
						}

						this.field_13878 = n >= -0.03125;
						this.field_13878 = this.field_13878 & (!this.server.isFlightEnabled() && !this.player.abilities.allowFlying);
						this.field_13878 = this.field_13878
							& (
								!this.player.hasStatusEffect(StatusEffects.LEVITATION)
									&& !this.player.method_13055()
									&& !serverWorld.isBoxNotEmpty(this.player.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0))
							);
						this.player.onGround = packet.isOnGround();
						this.server.getPlayerManager().method_2003(this.player);
						this.player.handleFall(this.player.y - g, packet.isOnGround());
						this.field_13886 = this.player.x;
						this.field_13887 = this.player.y;
						this.field_13888 = this.player.z;
					}
				}
			}
		}
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
		this.teleportRequest(x, y, z, yaw, pitch, Collections.emptySet());
	}

	public void teleportRequest(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> set) {
		double d = set.contains(PlayerPositionLookS2CPacket.Flag.X) ? this.player.x : 0.0;
		double e = set.contains(PlayerPositionLookS2CPacket.Flag.Y) ? this.player.y : 0.0;
		double f = set.contains(PlayerPositionLookS2CPacket.Flag.Z) ? this.player.z : 0.0;
		this.field_13896 = new Vec3d(x + d, y + e, z + f);
		float g = yaw;
		float h = pitch;
		if (set.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
			g = yaw + this.player.yaw;
		}

		if (set.contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
			h = pitch + this.player.pitch;
		}

		if (++this.field_13897 == Integer.MAX_VALUE) {
			this.field_13897 = 0;
		}

		this.field_11776 = this.lastTickMovePacketsCount;
		this.player.updatePositionAndAngles(this.field_13896.x, this.field_13896.y, this.field_13896.z, g, h);
		this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x, y, z, yaw, pitch, set, this.field_13897));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = packet.getPos();
		this.player.updateLastActionTime();
		switch (packet.getAction()) {
			case SWAP_HELD_ITEMS:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
					this.player.equipStack(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
					this.player.equipStack(Hand.MAIN_HAND, itemStack);
				}

				return;
			case DROP_ITEM:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(false);
				}

				return;
			case DROP_ALL_ITEMS:
				if (!this.player.isSpectator()) {
					this.player.dropSelectedItem(true);
				}

				return;
			case RELEASE_USE_ITEM:
				this.player.method_13067();
				ItemStack itemStack2 = this.player.getMainHandStack();
				if (itemStack2 != null && itemStack2.count == 0) {
					this.player.equipStack(Hand.MAIN_HAND, null);
				}

				return;
			case START_DESTROY_BLOCK:
			case ABORT_DESTROY_BLOCK:
			case STOP_DESTROY_BLOCK:
				double d = this.player.x - ((double)blockPos.getX() + 0.5);
				double e = this.player.y - ((double)blockPos.getY() + 0.5) + 1.5;
				double f = this.player.z - ((double)blockPos.getZ() + 0.5);
				double g = d * d + e * e + f * f;
				if (g > 36.0) {
					return;
				} else if (blockPos.getY() >= this.server.getWorldHeight()) {
					return;
				} else {
					if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK) {
						if (!this.server.isSpawnProtected(serverWorld, blockPos, this.player) && serverWorld.getWorldBorder().contains(blockPos)) {
							this.player.interactionManager.processBlockBreakingAction(blockPos, packet.getDirection());
						} else {
							this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
						}
					} else {
						if (packet.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK) {
							this.player.interactionManager.method_10764(blockPos);
						} else if (packet.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK) {
							this.player.interactionManager.method_10769();
						}

						if (serverWorld.getBlockState(blockPos).getMaterial() != Material.AIR) {
							this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
						}
					}

					return;
				}
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = packet.method_12709();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockPos blockPos = packet.getPos();
		Direction direction = packet.method_12708();
		this.player.updateLastActionTime();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.field_13896 == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& !this.server.isSpawnProtected(serverWorld, blockPos, this.player)
				&& serverWorld.getWorldBorder().contains(blockPos)) {
				this.player
					.interactionManager
					.method_12792(this.player, serverWorld, itemStack, hand, blockPos, direction, packet.getDistanceX(), packet.getDistanceY(), packet.getDistanceZ());
			}
		} else {
			TranslatableText translatableText = new TranslatableText("build.tooHigh", this.server.getWorldHeight());
			translatableText.getStyle().setFormatting(Formatting.RED);
			this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(translatableText));
		}

		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
		itemStack = this.player.getStackInHand(hand);
		if (itemStack != null && itemStack.count == 0) {
			this.player.equipStack(hand, null);
			itemStack = null;
		}
	}

	@Override
	public void onSwingHand(SwingHandC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (itemStack != null) {
			this.player.interactionManager.method_12791(this.player, serverWorld, itemStack, hand);
			itemStack = this.player.getStackInHand(hand);
			if (itemStack != null && itemStack.count == 0) {
				this.player.equipStack(hand, null);
				itemStack = null;
			}
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			Entity entity = null;

			for (ServerWorld serverWorld : this.server.worlds) {
				if (serverWorld != null) {
					entity = packet.getTarget(serverWorld);
					if (entity != null) {
						break;
					}
				}
			}

			if (entity != null) {
				this.player.method_10763(this.player);
				this.player.stopRiding();
				if (entity.world != this.player.world) {
					ServerWorld serverWorld2 = this.player.getServerWorld();
					ServerWorld serverWorld3 = (ServerWorld)entity.world;
					this.player.dimension = entity.dimension;
					this.sendPacket(
						new PlayerRespawnS2CPacket(
							this.player.dimension,
							serverWorld2.getGlobalDifficulty(),
							serverWorld2.getLevelProperties().getGeneratorType(),
							this.player.interactionManager.getGameMode()
						)
					);
					this.server.getPlayerManager().method_12831(this.player);
					serverWorld2.method_3700(this.player);
					this.player.removed = false;
					this.player.refreshPositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
					if (this.player.isAlive()) {
						serverWorld2.checkChunk(this.player, false);
						serverWorld3.spawnEntity(this.player);
						serverWorld3.checkChunk(this.player, false);
					}

					this.player.setWorld(serverWorld3);
					this.server.getPlayerManager().method_1986(this.player, serverWorld2);
					this.player.refreshPositionAfterTeleport(entity.x, entity.y, entity.z);
					this.player.interactionManager.setWorld(serverWorld3);
					this.server.getPlayerManager().sendWorldInfo(this.player, serverWorld3);
					this.server.getPlayerManager().method_2009(this.player);
				} else {
					this.player.refreshPositionAfterTeleport(entity.x, entity.y, entity.z);
				}
			}
		}
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
	}

	@Override
	public void onSteerBoat(SteerBoatC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		Entity entity = this.player.getVehicle();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).setPaddleMoving(packet.isRightPaddleTurning(), packet.isLeftPaddleTurning());
		}
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info(this.player.getTranslationKey() + " lost connection: " + reason);
		this.server.forcePlayerSampleUpdate();
		TranslatableText translatableText = new TranslatableText("multiplayer.player.left", this.player.getName());
		translatableText.getStyle().setFormatting(Formatting.YELLOW);
		this.server.getPlayerManager().sendToAll(translatableText);
		this.player.method_2160();
		this.server.getPlayerManager().method_12830(this.player);
		if (this.server.isSinglePlayer() && this.player.getTranslationKey().equals(this.server.getUserName())) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stopRunning();
		}
	}

	public void sendPacket(Packet<?> packet) {
		if (packet instanceof ChatMessageS2CPacket) {
			ChatMessageS2CPacket chatMessageS2CPacket = (ChatMessageS2CPacket)packet;
			PlayerEntity.ChatVisibilityType chatVisibilityType = this.player.method_8137();
			if (chatVisibilityType == PlayerEntity.ChatVisibilityType.HIDDEN) {
				return;
			}

			if (chatVisibilityType == PlayerEntity.ChatVisibilityType.SYSTEM && !chatMessageS2CPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.connection.send(packet);
		} catch (Throwable var5) {
			CrashReport crashReport = CrashReport.create(var5, "Sending packet");
			CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
			crashReportSection.add("Packet class", new CrashCallable<String>() {
				public String call() throws Exception {
					return packet.getClass().getCanonicalName();
				}
			});
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getSelectedSlot() >= 0 && packet.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			this.player.inventory.selectedSlot = packet.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn(this.player.getTranslationKey() + " tried to set an invalid carried item");
		}
	}

	@Override
	public void onChatMessage(ChatMessageC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.method_8137() == PlayerEntity.ChatVisibilityType.HIDDEN) {
			TranslatableText translatableText = new TranslatableText("chat.cannotSend");
			translatableText.getStyle().setFormatting(Formatting.RED);
			this.sendPacket(new ChatMessageS2CPacket(translatableText));
		} else {
			this.player.updateLastActionTime();
			String string = packet.getChatMessage();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.disconnect("Illegal characters in chat");
					return;
				}
			}

			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				Text text = new TranslatableText("chat.type.text", this.player.getName(), string);
				this.server.getPlayerManager().broadcastChatMessage(text, false);
			}

			this.messageCooldown += 20;
			if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
				this.disconnect("disconnect.spam");
			}
		}
	}

	private void executeCommand(String string) {
		this.server.getCommandManager().execute(this.player, string);
	}

	@Override
	public void onHandSwing(HandSwingC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(packet.method_12707());
	}

	@Override
	public void onClientCommand(ClientCommandC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (packet.getMode()) {
			case START_SNEAKING:
				this.player.setSneaking(true);
				break;
			case STOP_SNEAKING:
				this.player.setSneaking(false);
				break;
			case START_SPRINTING:
				this.player.setSprinting(true);
				break;
			case STOP_SPRINTING:
				this.player.setSprinting(false);
				break;
			case STOP_SLEEPING:
				this.player.awaken(false, true, true);
				this.field_13896 = new Vec3d(this.player.x, this.player.y, this.player.z);
				break;
			case START_RIDING_JUMP:
				if (this.player.getVehicle() instanceof class_2971) {
					class_2971 lv = (class_2971)this.player.getVehicle();
					int i = packet.getMountJumpHeight();
					if (lv.method_13089() && i > 0) {
						lv.method_13090(i);
					}
				}
				break;
			case STOP_RIDING_JUMP:
				if (this.player.getVehicle() instanceof class_2971) {
					class_2971 lv2 = (class_2971)this.player.getVehicle();
					lv2.method_13091();
				}
				break;
			case OPEN_INVENTORY:
				if (this.player.getVehicle() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getVehicle()).openInventory(this.player);
				}
				break;
			case START_FALL_FLYING:
				if (!this.player.onGround && this.player.velocityY < 0.0 && !this.player.method_13055() && !this.player.isTouchingWater()) {
					ItemStack itemStack = this.player.getStack(EquipmentSlot.CHEST);
					if (itemStack != null && itemStack.getItem() == Items.ELYTRA && ElytraItem.method_11370(itemStack)) {
						this.player.method_12786();
					}
				} else {
					this.player.method_12787();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		Entity entity = packet.getEntity(serverWorld);
		this.player.updateLastActionTime();
		if (entity != null) {
			boolean bl = this.player.canSee(entity);
			double d = 36.0;
			if (!bl) {
				d = 9.0;
			}

			if (this.player.squaredDistanceTo(entity) < d) {
				if (packet.getType() == PlayerInteractEntityC2SPacket.Type.INTERACT) {
					Hand hand = packet.method_12686();
					ItemStack itemStack = this.player.getStackInHand(hand);
					this.player.method_13263(entity, itemStack, hand);
				} else if (packet.getType() == PlayerInteractEntityC2SPacket.Type.INTERACT_AT) {
					Hand hand2 = packet.method_12686();
					ItemStack itemStack2 = this.player.getStackInHand(hand2);
					entity.method_12976(this.player, packet.getHitPosition(), itemStack2, hand2);
				} else if (packet.getType() == PlayerInteractEntityC2SPacket.Type.ATTACK) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == this.player) {
						this.disconnect("Attempting to attack an invalid entity");
						this.server.warn("Player " + this.player.getTranslationKey() + " tried to attack an invalid entity");
						return;
					}

					this.player.attack(entity);
				}
			}
		}
	}

	@Override
	public void onClientStatus(ClientStatusC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ClientStatusC2SPacket.Mode mode = packet.getMode();
		switch (mode) {
			case PERFORM_RESPAWN:
				if (this.player.killedEnderdragon) {
					this.player.killedEnderdragon = false;
					this.player = this.server.getPlayerManager().respawnPlayer(this.player, 0, true);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().respawnPlayer(this.player, 0, false);
					if (this.server.isHardcore()) {
						this.player.setGameMode(LevelInfo.GameMode.SPECTATOR);
						this.player.getServerWorld().getGameRules().setGameRule("spectatorsGenerateChunks", "false");
					}
				}
				break;
			case REQUEST_STATS:
				this.player.getStatHandler().method_8273(this.player);
				break;
			case OPEN_INVENTORY_ACHIEVEMENT:
				this.player.incrementStat(AchievementsAndCriterions.TAKING_INVENTORY);
		}
	}

	@Override
	public void onGuiClose(GuiCloseC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.closeOpenedScreenHandler();
	}

	@Override
	public void onClickWindow(ClickWindowC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.openScreenHandler.syncId == packet.getSyncId() && this.player.openScreenHandler.isNotRestricted(this.player)) {
			if (this.player.isSpectator()) {
				List<ItemStack> list = Lists.newArrayList();

				for (int i = 0; i < this.player.openScreenHandler.slots.size(); i++) {
					list.add(((Slot)this.player.openScreenHandler.slots.get(i)).getStack());
				}

				this.player.updateScreenHandler(this.player.openScreenHandler, list);
			} else {
				ItemStack itemStack = this.player.openScreenHandler.method_3252(packet.getSlot(), packet.getButton(), packet.method_7977(), this.player);
				if (ItemStack.equalsAll(packet.getSelectedStack(), itemStack)) {
					this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getTransactionId(), true));
					this.player.skipPacketSlotUpdates = true;
					this.player.openScreenHandler.sendContentUpdates();
					this.player.method_2158();
					this.player.skipPacketSlotUpdates = false;
				} else {
					this.transactions.set(this.player.openScreenHandler.syncId, packet.getTransactionId());
					this.player.networkHandler.sendPacket(new ConfirmGuiActionS2CPacket(packet.getSyncId(), packet.getTransactionId(), false));
					this.player.openScreenHandler.setPlayerRestriction(this.player, false);
					List<ItemStack> list2 = Lists.newArrayList();

					for (int j = 0; j < this.player.openScreenHandler.slots.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.openScreenHandler.slots.get(j)).getStack();
						ItemStack itemStack3 = itemStack2 != null && itemStack2.count > 0 ? itemStack2 : null;
						list2.add(itemStack3);
					}

					this.player.updateScreenHandler(this.player.openScreenHandler, list2);
				}
			}
		}
	}

	@Override
	public void onButtonClick(ButtonClickC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.openScreenHandler.syncId == packet.getSyncId() && this.player.openScreenHandler.isNotRestricted(this.player) && !this.player.isSpectator()) {
			this.player.openScreenHandler.onButtonClick(this.player, packet.getButtonId());
			this.player.openScreenHandler.sendContentUpdates();
		}
	}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = packet.getSlot() < 0;
			ItemStack itemStack = packet.getItemStack();
			if (itemStack != null && itemStack.hasNbt() && itemStack.getNbt().contains("BlockEntityTag", 10)) {
				NbtCompound nbtCompound = itemStack.getNbt().getCompound("BlockEntityTag");
				if (nbtCompound.contains("x") && nbtCompound.contains("y") && nbtCompound.contains("z")) {
					BlockPos blockPos = new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z"));
					BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
					if (blockEntity != null) {
						NbtCompound nbtCompound2 = blockEntity.toNbt(new NbtCompound());
						nbtCompound2.remove("x");
						nbtCompound2.remove("y");
						nbtCompound2.remove("z");
						itemStack.putSubNbt("BlockEntityTag", nbtCompound2);
					}
				}
			}

			boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
			boolean bl3 = itemStack == null || itemStack.getItem() != null;
			boolean bl4 = itemStack == null || itemStack.getData() >= 0 && itemStack.count <= 64 && itemStack.count > 0;
			if (bl2 && bl3 && bl4) {
				if (itemStack == null) {
					this.player.playerScreenHandler.setStackInSlot(packet.getSlot(), null);
				} else {
					this.player.playerScreenHandler.setStackInSlot(packet.getSlot(), itemStack);
				}

				this.player.playerScreenHandler.setPlayerRestriction(this.player, true);
			} else if (bl && bl3 && bl4 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				ItemEntity itemEntity = this.player.dropItem(itemStack, true);
				if (itemEntity != null) {
					itemEntity.method_3058();
				}
			}
		}
	}

	@Override
	public void onConfirmTransaction(ConfirmGuiActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		Short short_ = this.transactions.get(this.player.openScreenHandler.syncId);
		if (short_ != null
			&& packet.getSyncId() == short_
			&& this.player.openScreenHandler.syncId == packet.getWindowId()
			&& !this.player.openScreenHandler.isNotRestricted(this.player)
			&& !this.player.isSpectator()) {
			this.player.openScreenHandler.setPlayerRestriction(this.player, true);
		}
	}

	@Override
	public void onSignUpdate(UpdateSignC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = packet.getSignPos();
		if (serverWorld.blockExists(blockPos)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.getTranslationKey() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = packet.method_10729();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.text[i] = new LiteralText(Formatting.strip(strings[i]));
			}

			signBlockEntity.markDirty();
			serverWorld.method_11481(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveC2SPacket packet) {
		if (packet.getTime() == this.field_8934) {
			int i = (int)(this.method_8169() - this.lastKeepAliveTime);
			this.player.ping = (this.player.ping * 3 + i) / 4;
		}
	}

	private long method_8169() {
		return System.nanoTime() / 1000000L;
	}

	@Override
	public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.abilities.flying = packet.isFlying() && this.player.abilities.allowFlying;
	}

	@Override
	public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		List<String> list = Lists.newArrayList();

		for (String string : this.server.method_12835(this.player, packet.getPartialCommand(), packet.getLookingAt(), packet.method_12684())) {
			list.add(string);
		}

		this.player.networkHandler.sendPacket(new CommandSuggestionsS2CPacket((String[])list.toArray(new String[list.size()])));
	}

	@Override
	public void onClientSettings(ClientSettingsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.method_12789(packet);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		String string = packet.getChannel();
		if ("MC|BEdit".equals(string)) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getPayload()));

			try {
				ItemStack itemStack = packetByteBuf.readItemStack();
				if (itemStack == null) {
					return;
				}

				if (!WritableBookItem.isValid(itemStack.getNbt())) {
					throw new IOException("Invalid book tag!");
				}

				ItemStack itemStack2 = this.player.getMainHandStack();
				if (itemStack2 != null) {
					if (itemStack.getItem() == Items.WRITABLE_BOOK && itemStack.getItem() == itemStack2.getItem()) {
						itemStack2.putSubNbt("pages", itemStack.getNbt().getList("pages", 8));
					}

					return;
				}
			} catch (Exception var112) {
				LOGGER.error("Couldn't handle book info", var112);
				return;
			} finally {
				packetByteBuf.release();
			}

			return;
		} else if ("MC|BSign".equals(string)) {
			PacketByteBuf packetByteBuf2 = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getPayload()));

			try {
				ItemStack itemStack3 = packetByteBuf2.readItemStack();
				if (itemStack3 == null) {
					return;
				}

				if (!WrittenBookItem.isValid(itemStack3.getNbt())) {
					throw new IOException("Invalid book tag!");
				}

				ItemStack itemStack4 = this.player.getMainHandStack();
				if (itemStack4 != null) {
					if (itemStack3.getItem() == Items.WRITABLE_BOOK && itemStack4.getItem() == Items.WRITABLE_BOOK) {
						itemStack4.putSubNbt("author", new NbtString(this.player.getTranslationKey()));
						itemStack4.putSubNbt("title", new NbtString(itemStack3.getNbt().getString("title")));
						NbtList nbtList = itemStack3.getNbt().getList("pages", 8);

						for (int i = 0; i < nbtList.size(); i++) {
							String string2 = nbtList.getString(i);
							Text text = new LiteralText(string2);
							string2 = Text.Serializer.serialize(text);
							nbtList.set(i, new NbtString(string2));
						}

						itemStack4.putSubNbt("pages", nbtList);
						itemStack4.setItem(Items.WRITTEN_BOOK);
					}

					return;
				}
			} catch (Exception var114) {
				LOGGER.error("Couldn't sign book", var114);
				return;
			} finally {
				packetByteBuf2.release();
			}

			return;
		} else if ("MC|TrSel".equals(string)) {
			try {
				int j = packet.getPayload().readInt();
				ScreenHandler screenHandler = this.player.openScreenHandler;
				if (screenHandler instanceof VillagerScreenHandler) {
					((VillagerScreenHandler)screenHandler).setRecipeIndex(j);
				}
			} catch (Exception var111) {
				LOGGER.error("Couldn't select trade", var111);
			}
		} else if ("MC|AdvCmd".equals(string)) {
			if (!this.server.areCommandBlocksEnabled()) {
				this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
				return;
			}

			if (!this.player.canUseCommand(2, "") || !this.player.abilities.creativeMode) {
				this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
				return;
			}

			PacketByteBuf packetByteBuf3 = packet.getPayload();

			try {
				int k = packetByteBuf3.readByte();
				CommandBlockExecutor commandBlockExecutor = null;
				if (k == 0) {
					BlockEntity blockEntity = this.player.world.getBlockEntity(new BlockPos(packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt()));
					if (blockEntity instanceof CommandBlockBlockEntity) {
						commandBlockExecutor = ((CommandBlockBlockEntity)blockEntity).getCommandExecutor();
					}
				} else if (k == 1) {
					Entity entity = this.player.world.getEntityById(packetByteBuf3.readInt());
					if (entity instanceof CommandBlockMinecartEntity) {
						commandBlockExecutor = ((CommandBlockMinecartEntity)entity).getCommandExecutor();
					}
				}

				String string3 = packetByteBuf3.readString(packetByteBuf3.readableBytes());
				boolean bl = packetByteBuf3.readBoolean();
				if (commandBlockExecutor != null) {
					commandBlockExecutor.setCommand(string3);
					commandBlockExecutor.setTrackOutput(bl);
					if (!bl) {
						commandBlockExecutor.setLastOutput(null);
					}

					commandBlockExecutor.markDirty();
					this.player.sendMessage(new TranslatableText("advMode.setCommand.success", string3));
				}
			} catch (Exception var109) {
				LOGGER.error("Couldn't set command block", var109);
			} finally {
				packetByteBuf3.release();
			}
		} else if ("MC|AutoCmd".equals(string)) {
			if (!this.server.areCommandBlocksEnabled()) {
				this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
				return;
			}

			if (!this.player.canUseCommand(2, "") || !this.player.abilities.creativeMode) {
				this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
				return;
			}

			PacketByteBuf packetByteBuf4 = packet.getPayload();

			try {
				CommandBlockExecutor commandBlockExecutor2 = null;
				CommandBlockBlockEntity commandBlockBlockEntity = null;
				BlockPos blockPos = new BlockPos(packetByteBuf4.readInt(), packetByteBuf4.readInt(), packetByteBuf4.readInt());
				BlockEntity blockEntity2 = this.player.world.getBlockEntity(blockPos);
				if (blockEntity2 instanceof CommandBlockBlockEntity) {
					commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity2;
					commandBlockExecutor2 = commandBlockBlockEntity.getCommandExecutor();
				}

				String string4 = packetByteBuf4.readString(packetByteBuf4.readableBytes());
				boolean bl2 = packetByteBuf4.readBoolean();
				CommandBlockBlockEntity.class_2736 lv = CommandBlockBlockEntity.class_2736.valueOf(packetByteBuf4.readString(16));
				boolean bl3 = packetByteBuf4.readBoolean();
				boolean bl4 = packetByteBuf4.readBoolean();
				if (commandBlockExecutor2 != null) {
					Direction direction = this.player.world.getBlockState(blockPos).get(CommandBlock.FACING);
					switch (lv) {
						case SEQUENCE: {
							BlockState blockState = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
							this.player.world.setBlockState(blockPos, blockState.with(CommandBlock.FACING, direction).with(CommandBlock.field_12637, bl3), 2);
							break;
						}
						case AUTO:
							BlockState var155 = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
							this.player.world.setBlockState(blockPos, var155.with(CommandBlock.FACING, direction).with(CommandBlock.field_12637, bl3), 2);
							break;
						case REDSTONE: {
							BlockState blockState = Blocks.COMMAND_BLOCK.getDefaultState();
							this.player.world.setBlockState(blockPos, blockState.with(CommandBlock.FACING, direction).with(CommandBlock.field_12637, bl3), 2);
						}
					}

					blockEntity2.cancelRemoval();
					this.player.world.setBlockEntity(blockPos, blockEntity2);
					commandBlockExecutor2.setCommand(string4);
					commandBlockExecutor2.setTrackOutput(bl2);
					if (!bl2) {
						commandBlockExecutor2.setLastOutput(null);
					}

					commandBlockBlockEntity.method_11650(bl4);
					commandBlockExecutor2.markDirty();
					if (!ChatUtil.isEmpty(string4)) {
						this.player.sendMessage(new TranslatableText("advMode.setCommand.success", string4));
					}
				}
			} catch (Exception var107) {
				LOGGER.error("Couldn't set command block", var107);
			} finally {
				packetByteBuf4.release();
			}
		} else if ("MC|Beacon".equals(string)) {
			if (this.player.openScreenHandler instanceof BeaconScreenHandler) {
				try {
					PacketByteBuf packetByteBuf5 = packet.getPayload();
					int l = packetByteBuf5.readInt();
					int m = packetByteBuf5.readInt();
					BeaconScreenHandler beaconScreenHandler = (BeaconScreenHandler)this.player.openScreenHandler;
					Slot slot = beaconScreenHandler.getSlot(0);
					if (slot.hasStack()) {
						slot.takeStack(1);
						Inventory inventory = beaconScreenHandler.getPaymentInventory();
						inventory.setProperty(1, l);
						inventory.setProperty(2, m);
						inventory.markDirty();
					}
				} catch (Exception var106) {
					LOGGER.error("Couldn't set beacon", var106);
				}
			}
		} else if ("MC|ItemName".equals(string)) {
			if (this.player.openScreenHandler instanceof AnvilScreenHandler) {
				AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler)this.player.openScreenHandler;
				if (packet.getPayload() != null && packet.getPayload().readableBytes() >= 1) {
					String string5 = SharedConstants.stripInvalidChars(packet.getPayload().readString(32767));
					if (string5.length() <= 30) {
						anvilScreenHandler.rename(string5);
					}
				} else {
					anvilScreenHandler.rename("");
				}
			}
		} else if ("MC|Struct".equals(string)) {
			PacketByteBuf packetByteBuf6 = packet.getPayload();

			try {
				if (this.player.canUseCommand(4, "") && this.player.abilities.creativeMode) {
					BlockPos blockPos2 = new BlockPos(packetByteBuf6.readInt(), packetByteBuf6.readInt(), packetByteBuf6.readInt());
					BlockState blockState2 = this.player.world.getBlockState(blockPos2);
					BlockEntity blockEntity3 = this.player.world.getBlockEntity(blockPos2);
					if (blockEntity3 instanceof StructureBlockEntity) {
						StructureBlockEntity structureBlockEntity = (StructureBlockEntity)blockEntity3;
						int n = packetByteBuf6.readByte();
						String string6 = packetByteBuf6.readString(32);
						structureBlockEntity.method_11669(StructureBlockEntity.class_2739.valueOf(string6));
						structureBlockEntity.method_11673(packetByteBuf6.readString(64));
						structureBlockEntity.method_11677(new BlockPos(packetByteBuf6.readInt(), packetByteBuf6.readInt(), packetByteBuf6.readInt()));
						structureBlockEntity.method_11679(new BlockPos(packetByteBuf6.readInt(), packetByteBuf6.readInt(), packetByteBuf6.readInt()));
						String string7 = packetByteBuf6.readString(32);
						structureBlockEntity.method_11667(BlockMirror.valueOf(string7));
						String string8 = packetByteBuf6.readString(32);
						structureBlockEntity.method_11668(BlockRotation.valueOf(string8));
						structureBlockEntity.method_11678(packetByteBuf6.readString(128));
						structureBlockEntity.method_11675(packetByteBuf6.readBoolean());
						if (n == 2) {
							if (structureBlockEntity.method_11681()) {
								this.player.addMessage(new LiteralText("Structure saved"));
							} else {
								this.player.addMessage(new LiteralText("Structure NOT saved"));
							}
						} else if (n == 3) {
							if (structureBlockEntity.method_11682()) {
								this.player.addMessage(new LiteralText("Structure loaded"));
							} else {
								this.player.addMessage(new LiteralText("Structure prepared"));
							}
						} else if (n == 4 && structureBlockEntity.method_11680()) {
							this.player.addMessage(new LiteralText("Size detected"));
						}

						structureBlockEntity.markDirty();
						this.player.world.method_11481(blockPos2, blockState2, blockState2, 3);
					}
				}
			} catch (Exception var104) {
				LOGGER.error("Couldn't set structure block", var104);
			} finally {
				packetByteBuf6.release();
			}
		} else if ("MC|PickItem".equals(string)) {
			PacketByteBuf packetByteBuf7 = packet.getPayload();

			try {
				int o = packetByteBuf7.readVarInt();
				this.player.inventory.method_13256(o);
				this.player
					.networkHandler
					.sendPacket(
						new ScreenHandlerSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot))
					);
				this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, o, this.player.inventory.getInvStack(o)));
				this.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
			} catch (Exception var102) {
				LOGGER.error("Couldn't pick item", var102);
			} finally {
				packetByteBuf7.release();
			}
		}
	}
}
