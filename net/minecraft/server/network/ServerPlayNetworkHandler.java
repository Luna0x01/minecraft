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
import java.util.concurrent.Callable;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.NbtCompound;
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
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.CommandBlockExecutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener, Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection connection;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int lastTickMovePacketsCount;
	private int field_11776;
	private int field_8932;
	private boolean field_8933;
	private int field_8934;
	private long lastKeepAliveTime;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private IntObjectStorage<Short> transactions = new IntObjectStorage<>();
	private double requestedTeleportPosX;
	private double requestedTeleportPosY;
	private double requestedTeleportPosZ;
	private boolean field_8944 = true;

	public ServerPlayNetworkHandler(MinecraftServer minecraftServer, ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		this.server = minecraftServer;
		this.connection = clientConnection;
		clientConnection.setPacketListener(this);
		this.player = serverPlayerEntity;
		serverPlayerEntity.networkHandler = this;
	}

	@Override
	public void tick() {
		this.field_8933 = false;
		this.lastTickMovePacketsCount++;
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

	private boolean validatePlayerMove(PlayerMoveC2SPacket playerMoveC2SPacket) {
		return !Doubles.isFinite(playerMoveC2SPacket.getX())
			|| !Doubles.isFinite(playerMoveC2SPacket.getY())
			|| !Doubles.isFinite(playerMoveC2SPacket.getZ())
			|| !Floats.isFinite(playerMoveC2SPacket.getPitch())
			|| !Floats.isFinite(playerMoveC2SPacket.getYaw());
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.validatePlayerMove(packet)) {
			this.disconnect("Invalid move packet received");
		} else {
			ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
			this.field_8933 = true;
			if (!this.player.killedEnderdragon) {
				double d = this.player.x;
				double e = this.player.y;
				double f = this.player.z;
				double g = 0.0;
				double h = packet.getX() - this.requestedTeleportPosX;
				double i = packet.getY() - this.requestedTeleportPosY;
				double j = packet.getZ() - this.requestedTeleportPosZ;
				if (packet.isPositionChanged()) {
					g = h * h + i * i + j * j;
					if (!this.field_8944 && g < 0.25) {
						this.field_8944 = true;
					}
				}

				if (this.field_8944) {
					this.field_11776 = this.lastTickMovePacketsCount;
					if (this.player.vehicle != null) {
						float k = this.player.yaw;
						float l = this.player.pitch;
						this.player.vehicle.updatePassengerPosition();
						double m = this.player.x;
						double n = this.player.y;
						double o = this.player.z;
						if (packet.isLookChanged()) {
							k = packet.getYaw();
							l = packet.getPitch();
						}

						this.player.onGround = packet.isOnGround();
						this.player.tickPlayer();
						this.player.updatePositionAndAngles(m, n, o, k, l);
						if (this.player.vehicle != null) {
							this.player.vehicle.updatePassengerPosition();
						}

						this.server.getPlayerManager().method_2003(this.player);
						if (this.player.vehicle != null) {
							if (g > 4.0) {
								Entity entity = this.player.vehicle;
								this.player.networkHandler.sendPacket(new EntityPositionS2CPacket(entity));
								this.requestTeleport(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
							}

							this.player.vehicle.velocityDirty = true;
						}

						if (this.field_8944) {
							this.requestedTeleportPosX = this.player.x;
							this.requestedTeleportPosY = this.player.y;
							this.requestedTeleportPosZ = this.player.z;
						}

						serverWorld.checkChunk(this.player);
						return;
					}

					if (this.player.isSleeping()) {
						this.player.tickPlayer();
						this.player
							.updatePositionAndAngles(this.requestedTeleportPosX, this.requestedTeleportPosY, this.requestedTeleportPosZ, this.player.yaw, this.player.pitch);
						serverWorld.checkChunk(this.player);
						return;
					}

					double p = this.player.y;
					this.requestedTeleportPosX = this.player.x;
					this.requestedTeleportPosY = this.player.y;
					this.requestedTeleportPosZ = this.player.z;
					double q = this.player.x;
					double r = this.player.y;
					double s = this.player.z;
					float t = this.player.yaw;
					float u = this.player.pitch;
					if (packet.isPositionChanged() && packet.getY() == -999.0) {
						packet.setPositionChanged(false);
					}

					if (packet.isPositionChanged()) {
						q = packet.getX();
						r = packet.getY();
						s = packet.getZ();
						if (Math.abs(packet.getX()) > 3.0E7 || Math.abs(packet.getZ()) > 3.0E7) {
							this.disconnect("Illegal position");
							return;
						}
					}

					if (packet.isLookChanged()) {
						t = packet.getYaw();
						u = packet.getPitch();
					}

					this.player.tickPlayer();
					this.player.updatePositionAndAngles(this.requestedTeleportPosX, this.requestedTeleportPosY, this.requestedTeleportPosZ, t, u);
					if (!this.field_8944) {
						return;
					}

					double v = q - this.player.x;
					double w = r - this.player.y;
					double x = s - this.player.z;
					double y = this.player.velocityX * this.player.velocityX + this.player.velocityY * this.player.velocityY + this.player.velocityZ * this.player.velocityZ;
					double z = v * v + w * w + x * x;
					if (z - y > 100.0 && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(this.player.getTranslationKey()))) {
						LOGGER.warn(this.player.getTranslationKey() + " moved too quickly! " + v + "," + w + "," + x + " (" + v + ", " + w + ", " + x + ")");
						this.requestTeleport(this.requestedTeleportPosX, this.requestedTeleportPosY, this.requestedTeleportPosZ, this.player.yaw, this.player.pitch);
						return;
					}

					float aa = 0.0625F;
					boolean bl = serverWorld.doesBoxCollide(this.player, this.player.getBoundingBox().increment((double)aa, (double)aa, (double)aa)).isEmpty();
					if (this.player.onGround && !packet.isOnGround() && w > 0.0) {
						this.player.jump();
					}

					this.player.move(v, w, x);
					this.player.onGround = packet.isOnGround();
					v = q - this.player.x;
					w = r - this.player.y;
					if (w > -0.5 || w < 0.5) {
						w = 0.0;
					}

					x = s - this.player.z;
					z = v * v + w * w + x * x;
					boolean bl2 = false;
					if (z > 0.0625 && !this.player.isSleeping() && !this.player.interactionManager.isCreative()) {
						bl2 = true;
						LOGGER.warn(this.player.getTranslationKey() + " moved wrongly!");
					}

					this.player.updatePositionAndAngles(q, r, s, t, u);
					this.player.method_3209(this.player.x - d, this.player.y - e, this.player.z - f);
					if (!this.player.noClip) {
						boolean bl3 = serverWorld.doesBoxCollide(this.player, this.player.getBoundingBox().increment((double)aa, (double)aa, (double)aa)).isEmpty();
						if (bl && (bl2 || !bl3) && !this.player.isSleeping()) {
							this.requestTeleport(this.requestedTeleportPosX, this.requestedTeleportPosY, this.requestedTeleportPosZ, t, u);
							return;
						}
					}

					Box box = this.player.getBoundingBox().expand((double)aa, (double)aa, (double)aa).stretch(0.0, -0.55, 0.0);
					if (this.server.isFlightEnabled() || this.player.abilities.allowFlying || serverWorld.isBoxNotEmpty(box)) {
						this.field_8932 = 0;
					} else if (w >= -0.03125) {
						this.field_8932++;
						if (this.field_8932 > 80) {
							LOGGER.warn(this.player.getTranslationKey() + " was kicked for floating too long!");
							this.disconnect("Flying is not enabled on this server");
							return;
						}
					}

					this.player.onGround = packet.isOnGround();
					this.server.getPlayerManager().method_2003(this.player);
					this.player.handleFall(this.player.y - p, packet.isOnGround());
				} else if (this.lastTickMovePacketsCount - this.field_11776 > 20) {
					this.requestTeleport(this.requestedTeleportPosX, this.requestedTeleportPosY, this.requestedTeleportPosZ, this.player.yaw, this.player.pitch);
				}
			}
		}
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
		this.teleportRequest(x, y, z, yaw, pitch, Collections.emptySet());
	}

	public void teleportRequest(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> set) {
		this.field_8944 = false;
		this.requestedTeleportPosX = x;
		this.requestedTeleportPosY = y;
		this.requestedTeleportPosZ = z;
		if (set.contains(PlayerPositionLookS2CPacket.Flag.X)) {
			this.requestedTeleportPosX = this.requestedTeleportPosX + this.player.x;
		}

		if (set.contains(PlayerPositionLookS2CPacket.Flag.Y)) {
			this.requestedTeleportPosY = this.requestedTeleportPosY + this.player.y;
		}

		if (set.contains(PlayerPositionLookS2CPacket.Flag.Z)) {
			this.requestedTeleportPosZ = this.requestedTeleportPosZ + this.player.z;
		}

		float f = yaw;
		float g = pitch;
		if (set.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT)) {
			f = yaw + this.player.yaw;
		}

		if (set.contains(PlayerPositionLookS2CPacket.Flag.X_ROT)) {
			g = pitch + this.player.pitch;
		}

		this.player.updatePositionAndAngles(this.requestedTeleportPosX, this.requestedTeleportPosY, this.requestedTeleportPosZ, f, g);
		this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x, y, z, yaw, pitch, set));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.getWorld(this.player.dimension);
		BlockPos blockPos = packet.getPos();
		this.player.updateLastActionTime();
		switch (packet.getAction()) {
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
				this.player.stopUsingItem();
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

						if (serverWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.AIR) {
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
		ItemStack itemStack = this.player.inventory.getMainHandStack();
		boolean bl = false;
		BlockPos blockPos = packet.getPos();
		Direction direction = Direction.getById(packet.getDirectionId());
		this.player.updateLastActionTime();
		if (packet.getDirectionId() == 255) {
			if (itemStack == null) {
				return;
			}

			this.player.interactionManager.interactItem(this.player, serverWorld, itemStack);
		} else if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.field_8944
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& !this.server.isSpawnProtected(serverWorld, blockPos, this.player)
				&& serverWorld.getWorldBorder().contains(blockPos)) {
				this.player
					.interactionManager
					.interactBlock(this.player, serverWorld, itemStack, blockPos, direction, packet.getDistanceX(), packet.getDistanceY(), packet.getDistanceZ());
			}

			bl = true;
		} else {
			TranslatableText translatableText = new TranslatableText("build.tooHigh", this.server.getWorldHeight());
			translatableText.getStyle().setFormatting(Formatting.RED);
			this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(translatableText));
			bl = true;
		}

		if (bl) {
			this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
			this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
		}

		itemStack = this.player.inventory.getMainHandStack();
		if (itemStack != null && itemStack.count == 0) {
			this.player.inventory.main[this.player.inventory.selectedSlot] = null;
			itemStack = null;
		}

		if (itemStack == null || itemStack.getMaxUseTime() == 0) {
			this.player.skipPacketSlotUpdates = true;
			this.player.inventory.main[this.player.inventory.selectedSlot] = ItemStack.copyOf(this.player.inventory.main[this.player.inventory.selectedSlot]);
			Slot slot = this.player.openScreenHandler.getSlot(this.player.inventory, this.player.inventory.selectedSlot);
			this.player.openScreenHandler.sendContentUpdates();
			this.player.skipPacketSlotUpdates = false;
			if (!ItemStack.equalsAll(this.player.inventory.getMainHandStack(), packet.getStack())) {
				this.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(this.player.openScreenHandler.syncId, slot.id, this.player.inventory.getMainHandStack()));
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
				this.player.startRiding(null);
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
	public void onDisconnected(Text reason) {
		LOGGER.info(this.player.getTranslationKey() + " lost connection: " + reason);
		this.server.forcePlayerSampleUpdate();
		TranslatableText translatableText = new TranslatableText("multiplayer.player.left", this.player.getName());
		translatableText.getStyle().setFormatting(Formatting.YELLOW);
		this.server.getPlayerManager().sendToAll(translatableText);
		this.player.method_2160();
		this.server.getPlayerManager().remove(this.player);
		if (this.server.isSinglePlayer() && this.player.getTranslationKey().equals(this.server.getUserName())) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stopRunning();
		}
	}

	public void sendPacket(Packet packet) {
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
			crashReportSection.add("Packet class", new Callable<String>() {
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
		this.player.swingHand();
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
				this.field_8944 = false;
				break;
			case RIDING_JUMP:
				if (this.player.vehicle instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.vehicle).method_6299(packet.getMountJumpHeight());
				}
				break;
			case OPEN_INVENTORY:
				if (this.player.vehicle instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.vehicle).openInventory(this.player);
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
					this.player.method_3215(entity);
				} else if (packet.getType() == PlayerInteractEntityC2SPacket.Type.INTERACT_AT) {
					entity.interactAt(this.player, packet.getHitPosition());
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
					this.player = this.server.getPlayerManager().respawnPlayer(this.player, 0, true);
				} else if (this.player.getServerWorld().getLevelProperties().isHardcore()) {
					if (this.server.isSinglePlayer() && this.player.getTranslationKey().equals(this.server.getUserName())) {
						this.player.networkHandler.disconnect("You have died. Game over, man, it's game over!");
						this.server.method_2980();
					} else {
						BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(this.player.getGameProfile(), null, "(You just lost the game)", null, "Death in Hardcore");
						this.server.getPlayerManager().getUserBanList().add(bannedPlayerEntry);
						this.player.networkHandler.disconnect("You have died. Game over, man, it's game over!");
					}
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().respawnPlayer(this.player, 0, false);
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
				ItemStack itemStack = this.player.openScreenHandler.onSlotClick(packet.getSlot(), packet.getButton(), packet.getActionType(), this.player);
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
						list2.add(((Slot)this.player.openScreenHandler.slots.get(j)).getStack());
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
						NbtCompound nbtCompound2 = new NbtCompound();
						blockEntity.toNbt(nbtCompound2);
						nbtCompound2.remove("x");
						nbtCompound2.remove("y");
						nbtCompound2.remove("z");
						itemStack.putSubNbt("BlockEntityTag", nbtCompound2);
					}
				}
			}

			boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() < 36 + PlayerInventory.getHotbarSize();
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
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.getTranslationKey() + " just tried to change non-editable sign");
				return;
			}

			Text[] texts = packet.getText();

			for (int i = 0; i < texts.length; i++) {
				signBlockEntity.text[i] = new LiteralText(Formatting.strip(texts[i].asUnformattedString()));
			}

			signBlockEntity.markDirty();
			serverWorld.onBlockUpdate(blockPos);
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

		for (String string : this.server.getCompletions(this.player, packet.getPartialCommand(), packet.getLookingAt())) {
			list.add(string);
		}

		this.player.networkHandler.sendPacket(new CommandSuggestionsS2CPacket((String[])list.toArray(new String[list.size()])));
	}

	@Override
	public void onClientSettings(ClientSettingsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.method_2150(packet);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if ("MC|BEdit".equals(packet.getChannel())) {
			PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getPayload()));

			try {
				ItemStack itemStack = packetByteBuf.readItemStack();
				if (itemStack == null) {
					return;
				}

				if (!WritableBookItem.isValid(itemStack.getNbt())) {
					throw new IOException("Invalid book tag!");
				}

				ItemStack itemStack2 = this.player.inventory.getMainHandStack();
				if (itemStack2 != null) {
					if (itemStack.getItem() == Items.WRITABLE_BOOK && itemStack.getItem() == itemStack2.getItem()) {
						itemStack2.putSubNbt("pages", itemStack.getNbt().getList("pages", 8));
					}

					return;
				}
			} catch (Exception var36) {
				LOGGER.error("Couldn't handle book info", var36);
				return;
			} finally {
				packetByteBuf.release();
			}

			return;
		} else if ("MC|BSign".equals(packet.getChannel())) {
			PacketByteBuf packetByteBuf2 = new PacketByteBuf(Unpooled.wrappedBuffer(packet.getPayload()));

			try {
				ItemStack itemStack3 = packetByteBuf2.readItemStack();
				if (itemStack3 == null) {
					return;
				}

				if (!WrittenBookItem.isValid(itemStack3.getNbt())) {
					throw new IOException("Invalid book tag!");
				}

				ItemStack itemStack4 = this.player.inventory.getMainHandStack();
				if (itemStack4 != null) {
					if (itemStack3.getItem() == Items.WRITTEN_BOOK && itemStack4.getItem() == Items.WRITABLE_BOOK) {
						itemStack4.putSubNbt("author", new NbtString(this.player.getTranslationKey()));
						itemStack4.putSubNbt("title", new NbtString(itemStack3.getNbt().getString("title")));
						itemStack4.putSubNbt("pages", itemStack3.getNbt().getList("pages", 8));
						itemStack4.setItem(Items.WRITTEN_BOOK);
					}

					return;
				}
			} catch (Exception var38) {
				LOGGER.error("Couldn't sign book", var38);
				return;
			} finally {
				packetByteBuf2.release();
			}

			return;
		} else if ("MC|TrSel".equals(packet.getChannel())) {
			try {
				int i = packet.getPayload().readInt();
				ScreenHandler screenHandler = this.player.openScreenHandler;
				if (screenHandler instanceof VillagerScreenHandler) {
					((VillagerScreenHandler)screenHandler).setRecipeIndex(i);
				}
			} catch (Exception var35) {
				LOGGER.error("Couldn't select trade", var35);
			}
		} else if ("MC|AdvCdm".equals(packet.getChannel())) {
			if (!this.server.areCommandBlocksEnabled()) {
				this.player.sendMessage(new TranslatableText("advMode.notEnabled"));
			} else if (this.player.canUseCommand(2, "") && this.player.abilities.creativeMode) {
				PacketByteBuf packetByteBuf3 = packet.getPayload();

				try {
					int j = packetByteBuf3.readByte();
					CommandBlockExecutor commandBlockExecutor = null;
					if (j == 0) {
						BlockEntity blockEntity = this.player.world.getBlockEntity(new BlockPos(packetByteBuf3.readInt(), packetByteBuf3.readInt(), packetByteBuf3.readInt()));
						if (blockEntity instanceof CommandBlockBlockEntity) {
							commandBlockExecutor = ((CommandBlockBlockEntity)blockEntity).getCommandExecutor();
						}
					} else if (j == 1) {
						Entity entity = this.player.world.getEntityById(packetByteBuf3.readInt());
						if (entity instanceof CommandBlockMinecartEntity) {
							commandBlockExecutor = ((CommandBlockMinecartEntity)entity).getCommandExecutor();
						}
					}

					String string = packetByteBuf3.readString(packetByteBuf3.readableBytes());
					boolean bl = packetByteBuf3.readBoolean();
					if (commandBlockExecutor != null) {
						commandBlockExecutor.setCommand(string);
						commandBlockExecutor.setTrackOutput(bl);
						if (!bl) {
							commandBlockExecutor.setLastOutput(null);
						}

						commandBlockExecutor.markDirty();
						this.player.sendMessage(new TranslatableText("advMode.setCommand.success", string));
					}
				} catch (Exception var33) {
					LOGGER.error("Couldn't set command block", var33);
				} finally {
					packetByteBuf3.release();
				}
			} else {
				this.player.sendMessage(new TranslatableText("advMode.notAllowed"));
			}
		} else if ("MC|Beacon".equals(packet.getChannel())) {
			if (this.player.openScreenHandler instanceof BeaconScreenHandler) {
				try {
					PacketByteBuf packetByteBuf4 = packet.getPayload();
					int k = packetByteBuf4.readInt();
					int l = packetByteBuf4.readInt();
					BeaconScreenHandler beaconScreenHandler = (BeaconScreenHandler)this.player.openScreenHandler;
					Slot slot = beaconScreenHandler.getSlot(0);
					if (slot.hasStack()) {
						slot.takeStack(1);
						Inventory inventory = beaconScreenHandler.getPaymentInventory();
						inventory.setProperty(1, k);
						inventory.setProperty(2, l);
						inventory.markDirty();
					}
				} catch (Exception var32) {
					LOGGER.error("Couldn't set beacon", var32);
				}
			}
		} else if ("MC|ItemName".equals(packet.getChannel()) && this.player.openScreenHandler instanceof AnvilScreenHandler) {
			AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler)this.player.openScreenHandler;
			if (packet.getPayload() != null && packet.getPayload().readableBytes() >= 1) {
				String string2 = SharedConstants.stripInvalidChars(packet.getPayload().readString(32767));
				if (string2.length() <= 30) {
					anvilScreenHandler.rename(string2);
				}
			} else {
				anvilScreenHandler.rename("");
			}
		}
	}
}
