package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.option.ChatVisibility;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayPongC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.world.EntityTrackingListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements EntityTrackingListener, ServerPlayPacketListener {
	static final Logger LOGGER = LogManager.getLogger();
	private static final int KEEP_ALIVE_INTERVAL = 15000;
	public final ClientConnection connection;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int ticks;
	private long lastKeepAliveTime;
	private boolean waitingForKeepAlive;
	private long keepAliveId;
	private int messageCooldown;
	private int creativeItemDropThreshold;
	private double lastTickX;
	private double lastTickY;
	private double lastTickZ;
	private double updatedX;
	private double updatedY;
	private double updatedZ;
	@Nullable
	private Entity topmostRiddenEntity;
	private double lastTickRiddenX;
	private double lastTickRiddenY;
	private double lastTickRiddenZ;
	private double updatedRiddenX;
	private double updatedRiddenY;
	private double updatedRiddenZ;
	@Nullable
	private Vec3d requestedTeleportPos;
	private int requestedTeleportId;
	private int teleportRequestTick;
	private boolean floating;
	private int floatingTicks;
	private boolean vehicleFloating;
	private int vehicleFloatingTicks;
	private int movePacketsCount;
	private int lastTickMovePacketsCount;

	public ServerPlayNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
		this.server = server;
		this.connection = connection;
		connection.setPacketListener(this);
		this.player = player;
		player.networkHandler = this;
		player.getTextStream().onConnect();
	}

	public void tick() {
		this.syncWithPlayerPosition();
		this.player.prevX = this.player.getX();
		this.player.prevY = this.player.getY();
		this.player.prevZ = this.player.getZ();
		this.player.playerTick();
		this.player.updatePositionAndAngles(this.lastTickX, this.lastTickY, this.lastTickZ, this.player.getYaw(), this.player.getPitch());
		this.ticks++;
		this.lastTickMovePacketsCount = this.movePacketsCount;
		if (this.floating && !this.player.isSleeping()) {
			if (++this.floatingTicks > 80) {
				LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
				this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
				return;
			}
		} else {
			this.floating = false;
			this.floatingTicks = 0;
		}

		this.topmostRiddenEntity = this.player.getRootVehicle();
		if (this.topmostRiddenEntity != this.player && this.topmostRiddenEntity.getPrimaryPassenger() == this.player) {
			this.lastTickRiddenX = this.topmostRiddenEntity.getX();
			this.lastTickRiddenY = this.topmostRiddenEntity.getY();
			this.lastTickRiddenZ = this.topmostRiddenEntity.getZ();
			this.updatedRiddenX = this.topmostRiddenEntity.getX();
			this.updatedRiddenY = this.topmostRiddenEntity.getY();
			this.updatedRiddenZ = this.topmostRiddenEntity.getZ();
			if (this.vehicleFloating && this.player.getRootVehicle().getPrimaryPassenger() == this.player) {
				if (++this.vehicleFloatingTicks > 80) {
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
					this.disconnect(new TranslatableText("multiplayer.disconnect.flying"));
					return;
				}
			} else {
				this.vehicleFloating = false;
				this.vehicleFloatingTicks = 0;
			}
		} else {
			this.topmostRiddenEntity = null;
			this.vehicleFloating = false;
			this.vehicleFloatingTicks = 0;
		}

		this.server.getProfiler().push("keepAlive");
		long l = Util.getMeasuringTimeMs();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.waitingForKeepAlive) {
				this.disconnect(new TranslatableText("disconnect.timeout"));
			} else {
				this.waitingForKeepAlive = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
			}
		}

		this.server.getProfiler().pop();
		if (this.messageCooldown > 0) {
			this.messageCooldown--;
		}

		if (this.creativeItemDropThreshold > 0) {
			this.creativeItemDropThreshold--;
		}

		if (this.player.getLastActionTime() > 0L
			&& this.server.getPlayerIdleTimeout() > 0
			&& Util.getMeasuringTimeMs() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.idling"));
		}
	}

	public void syncWithPlayerPosition() {
		this.lastTickX = this.player.getX();
		this.lastTickY = this.player.getY();
		this.lastTickZ = this.player.getZ();
		this.updatedX = this.player.getX();
		this.updatedY = this.player.getY();
		this.updatedZ = this.player.getZ();
	}

	@Override
	public ClientConnection getConnection() {
		return this.connection;
	}

	private boolean isHost() {
		return this.server.isHost(this.player.getGameProfile());
	}

	public void disconnect(Text reason) {
		this.connection.send(new DisconnectS2CPacket(reason), future -> this.connection.disconnect(reason));
		this.connection.disableAutoRead();
		this.server.submitAndJoin(this.connection::handleDisconnection);
	}

	private <T, R> void filterText(T text, Consumer<R> consumer, BiFunction<TextStream, T, CompletableFuture<R>> backingFilterer) {
		ThreadExecutor<?> threadExecutor = this.player.getServerWorld().getServer();
		Consumer<R> consumer2 = object -> {
			if (this.getConnection().isOpen()) {
				consumer.accept(object);
			} else {
				LOGGER.debug("Ignoring packet due to disconnection");
			}
		};
		((CompletableFuture)backingFilterer.apply(this.player.getTextStream(), text)).thenAcceptAsync(consumer2, threadExecutor);
	}

	private void filterText(String text, Consumer<TextStream.Message> consumer) {
		this.filterText(text, consumer, TextStream::filterText);
	}

	private void filterTexts(List<String> texts, Consumer<List<TextStream.Message>> consumer) {
		this.filterText(texts, consumer, TextStream::filterTexts);
	}

	@Override
	public void onPlayerInput(PlayerInputC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateInput(packet.getSideways(), packet.getForward(), packet.isJumping(), packet.isSneaking());
	}

	private static boolean validateVehicleMove(double x, double y, double z, float yaw, float pitch) {
		return Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z) || !Floats.isFinite(pitch) || !Floats.isFinite(yaw);
	}

	private static double clampHorizontal(double d) {
		return MathHelper.clamp(d, -3.0E7, 3.0E7);
	}

	private static double clampVertical(double d) {
		return MathHelper.clamp(d, -2.0E7, 2.0E7);
	}

	@Override
	public void onVehicleMove(VehicleMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (validateVehicleMove(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch())) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_vehicle_movement"));
		} else {
			Entity entity = this.player.getRootVehicle();
			if (entity != this.player && entity.getPrimaryPassenger() == this.player && entity == this.topmostRiddenEntity) {
				ServerWorld serverWorld = this.player.getServerWorld();
				double d = entity.getX();
				double e = entity.getY();
				double f = entity.getZ();
				double g = clampHorizontal(packet.getX());
				double h = clampVertical(packet.getY());
				double i = clampHorizontal(packet.getZ());
				float j = MathHelper.wrapDegrees(packet.getYaw());
				float k = MathHelper.wrapDegrees(packet.getPitch());
				double l = g - this.lastTickRiddenX;
				double m = h - this.lastTickRiddenY;
				double n = i - this.lastTickRiddenZ;
				double o = entity.getVelocity().lengthSquared();
				double p = l * l + m * m + n * n;
				if (p - o > 100.0 && !this.isHost()) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName().getString(), this.player.getName().getString(), l, m, n);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.isSpaceEmpty(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.updatedRiddenX;
				m = h - this.updatedRiddenY - 1.0E-6;
				n = i - this.updatedRiddenZ;
				entity.move(MovementType.PLAYER, new Vec3d(l, m, n));
				l = g - entity.getX();
				m = h - entity.getY();
				if (m > -0.5 || m < 0.5) {
					m = 0.0;
				}

				n = i - entity.getZ();
				p = l * l + m * m + n * n;
				boolean bl2 = false;
				if (p > 0.0625) {
					bl2 = true;
					LOGGER.warn("{} (vehicle of {}) moved wrongly! {}", entity.getName().getString(), this.player.getName().getString(), Math.sqrt(p));
				}

				entity.updatePositionAndAngles(g, h, i, j, k);
				boolean bl3 = serverWorld.isSpaceEmpty(entity, entity.getBoundingBox().contract(0.0625));
				if (bl && (bl2 || !bl3)) {
					entity.updatePositionAndAngles(d, e, f, j, k);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				this.player.getServerWorld().getChunkManager().updatePosition(this.player);
				this.player.increaseTravelMotionStats(this.player.getX() - d, this.player.getY() - e, this.player.getZ() - f);
				this.vehicleFloating = m >= -0.03125 && !this.server.isFlightEnabled() && this.isEntityOnAir(entity);
				this.updatedRiddenX = entity.getX();
				this.updatedRiddenY = entity.getY();
				this.updatedRiddenZ = entity.getZ();
			}
		}
	}

	private boolean isEntityOnAir(Entity entity) {
		return entity.world.getStatesInBox(entity.getBoundingBox().expand(0.0625).stretch(0.0, -0.55, 0.0)).allMatch(AbstractBlock.AbstractBlockState::isAir);
	}

	@Override
	public void onTeleportConfirm(TeleportConfirmC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getTeleportId() == this.requestedTeleportId) {
			this.player
				.updatePositionAndAngles(
					this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch()
				);
			this.updatedX = this.requestedTeleportPos.x;
			this.updatedY = this.requestedTeleportPos.y;
			this.updatedZ = this.requestedTeleportPos.z;
			if (this.player.isInTeleportationState()) {
				this.player.onTeleportationDone();
			}

			this.requestedTeleportPos = null;
		}
	}

	@Override
	public void onRecipeBookData(RecipeBookDataC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.server.getRecipeManager().get(packet.getRecipeId()).ifPresent(this.player.getRecipeBook()::onRecipeDisplayed);
	}

	@Override
	public void onRecipeCategoryOptions(RecipeCategoryOptionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.getRecipeBook().setCategoryOptions(packet.getCategory(), packet.isGuiOpen(), packet.isFilteringCraftable());
	}

	@Override
	public void onAdvancementTab(AdvancementTabC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			Identifier identifier = packet.getTabToOpen();
			Advancement advancement = this.server.getAdvancementLoader().get(identifier);
			if (advancement != null) {
				this.player.getAdvancementTracker().setDisplayTab(advancement);
			}
		}
	}

	@Override
	public void onRequestCommandCompletions(RequestCommandCompletionsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		StringReader stringReader = new StringReader(packet.getPartialCommand());
		if (stringReader.canRead() && stringReader.peek() == '/') {
			stringReader.skip();
		}

		ParseResults<ServerCommandSource> parseResults = this.server.getCommandManager().getDispatcher().parse(stringReader, this.player.getCommandSource());
		this.server
			.getCommandManager()
			.getDispatcher()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.connection.send(new CommandSuggestionsS2CPacket(packet.getCompletionId(), suggestions)));
	}

	@Override
	public void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendSystemMessage(new TranslatableText("advMode.notEnabled"), Util.NIL_UUID);
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendSystemMessage(new TranslatableText("advMode.notAllowed"), Util.NIL_UUID);
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = packet.getBlockPos();
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = packet.getCommand();
			boolean bl = packet.shouldTrackOutput();
			if (commandBlockExecutor != null) {
				CommandBlockBlockEntity.Type type = commandBlockBlockEntity.getCommandBlockType();
				BlockState blockState = this.player.world.getBlockState(blockPos);
				Direction direction = blockState.get(CommandBlock.FACING);

				BlockState blockState5 = (switch (packet.getType()) {
					case SEQUENCE -> Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
					case AUTO -> Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
					default -> Blocks.COMMAND_BLOCK.getDefaultState();
				}).with(CommandBlock.FACING, direction).with(CommandBlock.CONDITIONAL, Boolean.valueOf(packet.isConditional()));
				if (blockState5 != blockState) {
					this.player.world.setBlockState(blockPos, blockState5, 2);
					blockEntity.setCachedState(blockState5);
					this.player.world.getWorldChunk(blockPos).setBlockEntity(blockEntity);
				}

				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.setTrackingOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.setAuto(packet.isAlwaysActive());
				if (type != packet.getType()) {
					commandBlockBlockEntity.updateCommandBlock();
				}

				commandBlockExecutor.markDirty();
				if (!ChatUtil.isEmpty(string)) {
					this.player.sendSystemMessage(new TranslatableText("advMode.setCommand.success", string), Util.NIL_UUID);
				}
			}
		}
	}

	@Override
	public void onUpdateCommandBlockMinecart(UpdateCommandBlockMinecartC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.sendSystemMessage(new TranslatableText("advMode.notEnabled"), Util.NIL_UUID);
		} else if (!this.player.isCreativeLevelTwoOp()) {
			this.player.sendSystemMessage(new TranslatableText("advMode.notAllowed"), Util.NIL_UUID);
		} else {
			CommandBlockExecutor commandBlockExecutor = packet.getMinecartCommandExecutor(this.player.world);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(packet.getCommand());
				commandBlockExecutor.setTrackingOutput(packet.shouldTrackOutput());
				if (!packet.shouldTrackOutput()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.markDirty();
				this.player.sendSystemMessage(new TranslatableText("advMode.setCommand.success", packet.getCommand()), Util.NIL_UUID);
			}
		}
	}

	@Override
	public void onPickFromInventory(PickFromInventoryC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.getInventory().swapSlotWithHotbar(packet.getSlot());
		this.player
			.networkHandler
			.sendPacket(
				new ScreenHandlerSlotUpdateS2CPacket(
					-2, 0, this.player.getInventory().selectedSlot, this.player.getInventory().getStack(this.player.getInventory().selectedSlot)
				)
			);
		this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, packet.getSlot(), this.player.getInventory().getStack(packet.getSlot())));
		this.player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(this.player.getInventory().selectedSlot));
	}

	@Override
	public void onRenameItem(RenameItemC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.currentScreenHandler instanceof AnvilScreenHandler anvilScreenHandler) {
			String string = SharedConstants.stripInvalidChars(packet.getName());
			if (string.length() <= 50) {
				anvilScreenHandler.setNewItemName(string);
			}
		}
	}

	@Override
	public void onUpdateBeacon(UpdateBeaconC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.currentScreenHandler instanceof BeaconScreenHandler) {
			((BeaconScreenHandler)this.player.currentScreenHandler).setEffects(packet.getPrimaryEffectId(), packet.getSecondaryEffectId());
		}
	}

	@Override
	public void onStructureBlockUpdate(UpdateStructureBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			if (this.player.world.getBlockEntity(blockPos) instanceof StructureBlockBlockEntity structureBlockBlockEntity) {
				structureBlockBlockEntity.setMode(packet.getMode());
				structureBlockBlockEntity.setStructureName(packet.getStructureName());
				structureBlockBlockEntity.setOffset(packet.getOffset());
				structureBlockBlockEntity.setSize(packet.getSize());
				structureBlockBlockEntity.setMirror(packet.getMirror());
				structureBlockBlockEntity.setRotation(packet.getRotation());
				structureBlockBlockEntity.setMetadata(packet.getMetadata());
				structureBlockBlockEntity.setIgnoreEntities(packet.shouldIgnoreEntities());
				structureBlockBlockEntity.setShowAir(packet.shouldShowAir());
				structureBlockBlockEntity.setShowBoundingBox(packet.shouldShowBoundingBox());
				structureBlockBlockEntity.setIntegrity(packet.getIntegrity());
				structureBlockBlockEntity.setSeed(packet.getSeed());
				if (structureBlockBlockEntity.hasStructureName()) {
					String string = structureBlockBlockEntity.getStructureName();
					if (packet.getAction() == StructureBlockBlockEntity.Action.SAVE_AREA) {
						if (structureBlockBlockEntity.saveStructure()) {
							this.player.sendMessage(new TranslatableText("structure_block.save_success", string), false);
						} else {
							this.player.sendMessage(new TranslatableText("structure_block.save_failure", string), false);
						}
					} else if (packet.getAction() == StructureBlockBlockEntity.Action.LOAD_AREA) {
						if (!structureBlockBlockEntity.isStructureAvailable()) {
							this.player.sendMessage(new TranslatableText("structure_block.load_not_found", string), false);
						} else if (structureBlockBlockEntity.loadStructure(this.player.getServerWorld())) {
							this.player.sendMessage(new TranslatableText("structure_block.load_success", string), false);
						} else {
							this.player.sendMessage(new TranslatableText("structure_block.load_prepare", string), false);
						}
					} else if (packet.getAction() == StructureBlockBlockEntity.Action.SCAN_AREA) {
						if (structureBlockBlockEntity.detectStructureSize()) {
							this.player.sendMessage(new TranslatableText("structure_block.size_success", string), false);
						} else {
							this.player.sendMessage(new TranslatableText("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.sendMessage(new TranslatableText("structure_block.invalid_structure_name", packet.getStructureName()), false);
				}

				structureBlockBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onJigsawUpdate(UpdateJigsawC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			if (this.player.world.getBlockEntity(blockPos) instanceof JigsawBlockEntity jigsawBlockEntity) {
				jigsawBlockEntity.setAttachmentType(packet.getAttachmentType());
				jigsawBlockEntity.setTargetPool(packet.getTargetPool());
				jigsawBlockEntity.setPool(packet.getPool());
				jigsawBlockEntity.setFinalState(packet.getFinalState());
				jigsawBlockEntity.setJoint(packet.getJointType());
				jigsawBlockEntity.markDirty();
				this.player.world.updateListeners(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void onJigsawGenerating(JigsawGeneratingC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isCreativeLevelTwoOp()) {
			BlockPos blockPos = packet.getPos();
			if (this.player.world.getBlockEntity(blockPos) instanceof JigsawBlockEntity jigsawBlockEntity) {
				jigsawBlockEntity.generate(this.player.getServerWorld(), packet.getMaxDepth(), packet.shouldKeepJigsaws());
			}
		}
	}

	@Override
	public void onMerchantTradeSelect(SelectMerchantTradeC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		int i = packet.getTradeId();
		if (this.player.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
			merchantScreenHandler.setRecipeIndex(i);
			merchantScreenHandler.switchTo(i);
		}
	}

	@Override
	public void onBookUpdate(BookUpdateC2SPacket packet) {
		int i = packet.getSlot();
		if (PlayerInventory.isValidHotbarIndex(i) || i == 40) {
			List<String> list = Lists.newArrayList();
			Optional<String> optional = packet.getTitle();
			optional.ifPresent(list::add);
			packet.getPages().stream().limit(100L).forEach(list::add);
			this.filterTexts(
				list,
				optional.isPresent()
					? listx -> this.addBook((TextStream.Message)listx.get(0), listx.subList(1, listx.size()), i)
					: listx -> this.updateBookContent(listx, i)
			);
		}
	}

	private void updateBookContent(List<TextStream.Message> pages, int slotId) {
		ItemStack itemStack = this.player.getInventory().getStack(slotId);
		if (itemStack.isOf(Items.WRITABLE_BOOK)) {
			this.setTextToBook(pages, UnaryOperator.identity(), itemStack);
		}
	}

	private void addBook(TextStream.Message title, List<TextStream.Message> pages, int slotId) {
		ItemStack itemStack = this.player.getInventory().getStack(slotId);
		if (itemStack.isOf(Items.WRITABLE_BOOK)) {
			ItemStack itemStack2 = new ItemStack(Items.WRITTEN_BOOK);
			NbtCompound nbtCompound = itemStack.getTag();
			if (nbtCompound != null) {
				itemStack2.setTag(nbtCompound.copy());
			}

			itemStack2.putSubTag("author", NbtString.of(this.player.getName().getString()));
			if (this.player.shouldFilterText()) {
				itemStack2.putSubTag("title", NbtString.of(title.getFiltered()));
			} else {
				itemStack2.putSubTag("filtered_title", NbtString.of(title.getFiltered()));
				itemStack2.putSubTag("title", NbtString.of(title.getRaw()));
			}

			this.setTextToBook(pages, string -> Text.Serializer.toJson(new LiteralText(string)), itemStack2);
			this.player.getInventory().setStack(slotId, itemStack2);
		}
	}

	private void setTextToBook(List<TextStream.Message> messages, UnaryOperator<String> postProcessor, ItemStack book) {
		NbtList nbtList = new NbtList();
		if (this.player.shouldFilterText()) {
			messages.stream().map(messagex -> NbtString.of((String)postProcessor.apply(messagex.getFiltered()))).forEach(nbtList::add);
		} else {
			NbtCompound nbtCompound = new NbtCompound();
			int i = 0;

			for (int j = messages.size(); i < j; i++) {
				TextStream.Message message = (TextStream.Message)messages.get(i);
				String string = message.getRaw();
				nbtList.add(NbtString.of((String)postProcessor.apply(string)));
				String string2 = message.getFiltered();
				if (!string.equals(string2)) {
					nbtCompound.putString(String.valueOf(i), (String)postProcessor.apply(string2));
				}
			}

			if (!nbtCompound.isEmpty()) {
				book.putSubTag("filtered_pages", nbtCompound);
			}
		}

		book.putSubTag("pages", nbtList);
	}

	@Override
	public void onQueryEntityNbt(QueryEntityNbtC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(packet.getEntityId());
			if (entity != null) {
				NbtCompound nbtCompound = entity.writeNbt(new NbtCompound());
				this.player.networkHandler.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
			}
		}
	}

	@Override
	public void onQueryBlockNbt(QueryBlockNbtC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().getBlockEntity(packet.getPos());
			NbtCompound nbtCompound = blockEntity != null ? blockEntity.writeNbt(new NbtCompound()) : null;
			this.player.networkHandler.sendPacket(new NbtQueryResponseS2CPacket(packet.getTransactionId(), nbtCompound));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (validateVehicleMove(packet.getX(0.0), packet.getY(0.0), packet.getZ(0.0), packet.getYaw(0.0F), packet.getPitch(0.0F))) {
			this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.player.getServerWorld();
			if (!this.player.notInAnyWorld) {
				if (this.ticks == 0) {
					this.syncWithPlayerPosition();
				}

				if (this.requestedTeleportPos != null) {
					if (this.ticks - this.teleportRequestTick > 20) {
						this.teleportRequestTick = this.ticks;
						this.requestTeleport(this.requestedTeleportPos.x, this.requestedTeleportPos.y, this.requestedTeleportPos.z, this.player.getYaw(), this.player.getPitch());
					}
				} else {
					this.teleportRequestTick = this.ticks;
					double d = clampHorizontal(packet.getX(this.player.getX()));
					double e = clampVertical(packet.getY(this.player.getY()));
					double f = clampHorizontal(packet.getZ(this.player.getZ()));
					float g = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
					float h = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
					if (this.player.hasVehicle()) {
						this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
						this.player.getServerWorld().getChunkManager().updatePosition(this.player);
					} else {
						double i = this.player.getX();
						double j = this.player.getY();
						double k = this.player.getZ();
						double l = this.player.getY();
						double m = d - this.lastTickX;
						double n = e - this.lastTickY;
						double o = f - this.lastTickZ;
						double p = this.player.getVelocity().lengthSquared();
						double q = m * m + n * n + o * o;
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), g, h);
							}
						} else {
							this.movePacketsCount++;
							int r = this.movePacketsCount - this.lastTickMovePacketsCount;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), r);
								r = 1;
							}

							if (!this.player.isInTeleportationState()
								&& (!this.player.getServerWorld().getGameRules().getBoolean(GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
								float s = this.player.isFallFlying() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && !this.isHost()) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), m, n, o);
									this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
									return;
								}
							}

							Box box = this.player.getBoundingBox();
							m = d - this.updatedX;
							n = e - this.updatedY;
							o = f - this.updatedZ;
							boolean bl = n > 0.0;
							if (this.player.isOnGround() && !packet.isOnGround() && bl) {
								this.player.jump();
							}

							this.player.move(MovementType.PLAYER, new Vec3d(m, n, o));
							m = d - this.player.getX();
							n = e - this.player.getY();
							if (n > -0.5 || n < 0.5) {
								n = 0.0;
							}

							o = f - this.player.getZ();
							q = m * m + n * n + o * o;
							boolean bl2 = false;
							if (!this.player.isInTeleportationState()
								&& q > 0.0625
								&& !this.player.isSleeping()
								&& !this.player.interactionManager.isCreative()
								&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
							}

							this.player.updatePositionAndAngles(d, e, f, g, h);
							if (this.player.noClip
								|| this.player.isSleeping()
								|| (!bl2 || !serverWorld.isSpaceEmpty(this.player, box)) && !this.isPlayerNotCollidingWithBlocks(serverWorld, box)) {
								this.floating = n >= -0.03125
									&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR
									&& !this.server.isFlightEnabled()
									&& !this.player.getAbilities().allowFlying
									&& !this.player.hasStatusEffect(StatusEffects.LEVITATION)
									&& !this.player.isFallFlying()
									&& this.isEntityOnAir(this.player);
								this.player.getServerWorld().getChunkManager().updatePosition(this.player);
								this.player.handleFall(this.player.getY() - l, packet.isOnGround());
								this.player.setOnGround(packet.isOnGround());
								if (bl) {
									this.player.fallDistance = 0.0F;
								}

								this.player.increaseTravelMotionStats(this.player.getX() - i, this.player.getY() - j, this.player.getZ() - k);
								this.updatedX = this.player.getX();
								this.updatedY = this.player.getY();
								this.updatedZ = this.player.getZ();
							} else {
								this.requestTeleport(i, j, k, g, h);
							}
						}
					}
				}
			}
		}
	}

	private boolean isPlayerNotCollidingWithBlocks(WorldView world, Box box) {
		Stream<VoxelShape> stream = world.getCollisions(this.player, this.player.getBoundingBox().contract(1.0E-5F), entity -> true);
		VoxelShape voxelShape = VoxelShapes.cuboid(box.contract(1.0E-5F));
		return stream.anyMatch(voxelShape2 -> !VoxelShapes.matchesAnywhere(voxelShape2, voxelShape, BooleanBiFunction.AND));
	}

	public void requestTeleportAndDismount(double x, double y, double z, float yaw, float pitch) {
		this.requestTeleport(x, y, z, yaw, pitch, Collections.emptySet(), true);
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
		this.requestTeleport(x, y, z, yaw, pitch, Collections.emptySet(), false);
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags) {
		this.requestTeleport(x, y, z, yaw, pitch, flags, false);
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount) {
		double d = flags.contains(PlayerPositionLookS2CPacket.Flag.X) ? this.player.getX() : 0.0;
		double e = flags.contains(PlayerPositionLookS2CPacket.Flag.Y) ? this.player.getY() : 0.0;
		double f = flags.contains(PlayerPositionLookS2CPacket.Flag.Z) ? this.player.getZ() : 0.0;
		float g = flags.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT) ? this.player.getYaw() : 0.0F;
		float h = flags.contains(PlayerPositionLookS2CPacket.Flag.X_ROT) ? this.player.getPitch() : 0.0F;
		this.requestedTeleportPos = new Vec3d(x, y, z);
		if (++this.requestedTeleportId == Integer.MAX_VALUE) {
			this.requestedTeleportId = 0;
		}

		this.teleportRequestTick = this.ticks;
		this.player.updatePositionAndAngles(x, y, z, yaw, pitch);
		this.player
			.networkHandler
			.sendPacket(new PlayerPositionLookS2CPacket(x - d, y - e, z - f, yaw - g, pitch - h, flags, this.requestedTeleportId, shouldDismount));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		BlockPos blockPos = packet.getPos();
		this.player.updateLastActionTime();
		PlayerActionC2SPacket.Action action = packet.getAction();
		switch (action) {
			case SWAP_ITEM_WITH_OFFHAND:
				if (!this.player.isSpectator()) {
					ItemStack itemStack = this.player.getStackInHand(Hand.OFF_HAND);
					this.player.setStackInHand(Hand.OFF_HAND, this.player.getStackInHand(Hand.MAIN_HAND));
					this.player.setStackInHand(Hand.MAIN_HAND, itemStack);
					this.player.clearActiveItem();
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
				this.player.stopUsingItem();
				return;
			case START_DESTROY_BLOCK:
			case ABORT_DESTROY_BLOCK:
			case STOP_DESTROY_BLOCK:
				this.player.interactionManager.processBlockBreakingAction(blockPos, action, packet.getDirection(), this.player.world.getTopY());
				return;
			default:
				throw new IllegalArgumentException("Invalid player action");
		}
	}

	private static boolean canPlace(ServerPlayerEntity player, ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		} else {
			Item item = stack.getItem();
			return (item instanceof BlockItem || item instanceof BucketItem) && !player.getItemCooldownManager().isCoolingDown(item);
		}
	}

	@Override
	public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.player.getServerWorld();
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockHitResult blockHitResult = packet.getBlockHitResult();
		BlockPos blockPos = blockHitResult.getBlockPos();
		Direction direction = blockHitResult.getSide();
		this.player.updateLastActionTime();
		int i = this.player.world.getTopY();
		if (blockPos.getY() < i) {
			if (this.requestedTeleportPos == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& serverWorld.canPlayerModifyAt(this.player, blockPos)) {
				ActionResult actionResult = this.player.interactionManager.interactBlock(this.player, serverWorld, itemStack, hand, blockHitResult);
				if (direction == Direction.UP && !actionResult.isAccepted() && blockPos.getY() >= i - 1 && canPlace(this.player, itemStack)) {
					Text text = new TranslatableText("build.tooHigh", i - 1).formatted(Formatting.RED);
					this.player.sendMessage(text, MessageType.GAME_INFO, Util.NIL_UUID);
				} else if (actionResult.shouldSwingHand()) {
					this.player.swingHand(hand, true);
				}
			}
		} else {
			Text text2 = new TranslatableText("build.tooHigh", i - 1).formatted(Formatting.RED);
			this.player.sendMessage(text2, MessageType.GAME_INFO, Util.NIL_UUID);
		}

		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
	}

	@Override
	public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.player.getServerWorld();
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty()) {
			ActionResult actionResult = this.player.interactionManager.interactItem(this.player, serverWorld, itemStack, hand);
			if (actionResult.shouldSwingHand()) {
				this.player.swingHand(hand, true);
			}
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			for (ServerWorld serverWorld : this.server.getWorlds()) {
				Entity entity = packet.getTarget(serverWorld);
				if (entity != null) {
					this.player.teleport(serverWorld, entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
					return;
				}
			}
		}
	}

	@Override
	public void onResourcePackStatus(ResourcePackStatusC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getStatus() == ResourcePackStatusC2SPacket.Status.DECLINED && this.server.requireResourcePack()) {
			LOGGER.info("Disconnecting {} due to resource pack rejection", this.player.getName());
			this.disconnect(new TranslatableText("multiplayer.requiredTexturePrompt.disconnect"));
		}
	}

	@Override
	public void onBoatPaddleState(BoatPaddleStateC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		Entity entity = this.player.getVehicle();
		if (entity instanceof BoatEntity) {
			((BoatEntity)entity).setPaddleMovings(packet.isLeftPaddling(), packet.isRightPaddling());
		}
	}

	@Override
	public void onPong(PlayPongC2SPacket packet) {
	}

	@Override
	public void onDisconnected(Text reason) {
		LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
		this.server.forcePlayerSampleUpdate();
		this.server
			.getPlayerManager()
			.broadcastChatMessage(
				new TranslatableText("multiplayer.player.left", this.player.getDisplayName()).formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID
			);
		this.player.onDisconnect();
		this.server.getPlayerManager().remove(this.player);
		this.player.getTextStream().onDisconnect();
		if (this.isHost()) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stop(false);
		}
	}

	@Override
	public void sendPacket(Packet<?> packet) {
		this.sendPacket(packet, null);
	}

	public void sendPacket(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> listener) {
		try {
			this.connection.send(packet, listener);
		} catch (Throwable var6) {
			CrashReport crashReport = CrashReport.create(var6, "Sending packet");
			CrashReportSection crashReportSection = crashReport.addElement("Packet being sent");
			crashReportSection.add("Packet class", (CrashCallable<String>)(() -> packet.getClass().getCanonicalName()));
			throw new CrashException(crashReport);
		}
	}

	@Override
	public void onUpdateSelectedSlot(UpdateSelectedSlotC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getSelectedSlot() >= 0 && packet.getSelectedSlot() < PlayerInventory.getHotbarSize()) {
			if (this.player.getInventory().selectedSlot != packet.getSelectedSlot() && this.player.getActiveHand() == Hand.MAIN_HAND) {
				this.player.clearActiveItem();
			}

			this.player.getInventory().selectedSlot = packet.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
		}
	}

	@Override
	public void onGameMessage(ChatMessageC2SPacket packet) {
		String string = StringUtils.normalizeSpace(packet.getChatMessage());

		for (int i = 0; i < string.length(); i++) {
			if (!SharedConstants.isValidChar(string.charAt(i))) {
				this.disconnect(new TranslatableText("multiplayer.disconnect.illegal_characters"));
				return;
			}
		}

		if (string.startsWith("/")) {
			NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
			this.handleMessage(TextStream.Message.permitted(string));
		} else {
			this.filterText(string, this::handleMessage);
		}
	}

	private void handleMessage(TextStream.Message message) {
		if (this.player.getClientChatVisibility() == ChatVisibility.HIDDEN) {
			this.sendPacket(new GameMessageS2CPacket(new TranslatableText("chat.disabled.options").formatted(Formatting.RED), MessageType.SYSTEM, Util.NIL_UUID));
		} else {
			this.player.updateLastActionTime();
			String string = message.getRaw();
			if (string.startsWith("/")) {
				this.executeCommand(string);
			} else {
				String string2 = message.getFiltered();
				Text text = string2.isEmpty() ? null : new TranslatableText("chat.type.text", this.player.getDisplayName(), string2);
				Text text2 = new TranslatableText("chat.type.text", this.player.getDisplayName(), string);
				this.server
					.getPlayerManager()
					.broadcast(text2, player -> this.player.shouldFilterMessagesSentTo(player) ? text : text2, MessageType.CHAT, this.player.getUuid());
			}

			this.messageCooldown += 20;
			if (this.messageCooldown > 200 && !this.server.getPlayerManager().isOperator(this.player.getGameProfile())) {
				this.disconnect(new TranslatableText("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String input) {
		this.server.getCommandManager().execute(this.player.getCommandSource(), input);
	}

	@Override
	public void onHandSwing(HandSwingC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		this.player.swingHand(packet.getHand());
	}

	@Override
	public void onClientCommand(ClientCommandC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		switch (packet.getMode()) {
			case PRESS_SHIFT_KEY:
				this.player.setSneaking(true);
				break;
			case RELEASE_SHIFT_KEY:
				this.player.setSneaking(false);
				break;
			case START_SPRINTING:
				this.player.setSprinting(true);
				break;
			case STOP_SPRINTING:
				this.player.setSprinting(false);
				break;
			case STOP_SLEEPING:
				if (this.player.isSleeping()) {
					this.player.wakeUp(false, true);
					this.requestedTeleportPos = this.player.getPos();
				}
				break;
			case START_RIDING_JUMP:
				if (this.player.getVehicle() instanceof JumpingMount) {
					JumpingMount jumpingMount = (JumpingMount)this.player.getVehicle();
					int i = packet.getMountJumpHeight();
					if (jumpingMount.canJump() && i > 0) {
						jumpingMount.startJumping(i);
					}
				}
				break;
			case STOP_RIDING_JUMP:
				if (this.player.getVehicle() instanceof JumpingMount) {
					JumpingMount jumpingMount2 = (JumpingMount)this.player.getVehicle();
					jumpingMount2.stopJumping();
				}
				break;
			case OPEN_INVENTORY:
				if (this.player.getVehicle() instanceof HorseBaseEntity) {
					((HorseBaseEntity)this.player.getVehicle()).openInventory(this.player);
				}
				break;
			case START_FALL_FLYING:
				if (!this.player.checkFallFlying()) {
					this.player.stopFallFlying();
				}
				break;
			default:
				throw new IllegalArgumentException("Invalid client command!");
		}
	}

	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.player.getServerWorld();
		final Entity entity = packet.getEntity(serverWorld);
		this.player.updateLastActionTime();
		this.player.setSneaking(packet.isPlayerSneaking());
		if (entity != null) {
			double d = 36.0;
			if (this.player.squaredDistanceTo(entity) < 36.0) {
				packet.handle(
					new PlayerInteractEntityC2SPacket.Handler() {
						private void processInteract(Hand hand, ServerPlayNetworkHandler.Interaction action) {
							ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(hand).copy();
							ActionResult actionResult = action.run(ServerPlayNetworkHandler.this.player, entity, hand);
							if (actionResult.isAccepted()) {
								Criteria.PLAYER_INTERACTED_WITH_ENTITY.test(ServerPlayNetworkHandler.this.player, itemStack, entity);
								if (actionResult.shouldSwingHand()) {
									ServerPlayNetworkHandler.this.player.swingHand(hand, true);
								}
							}
						}

						@Override
						public void interact(Hand hand) {
							this.processInteract(hand, PlayerEntity::interact);
						}

						@Override
						public void interactAt(Hand hand, Vec3d pos) {
							this.processInteract(hand, (player, entityxx, handx) -> entityxx.interactAt(player, pos, handx));
						}

						@Override
						public void attack() {
							if (!(entity instanceof ItemEntity)
								&& !(entity instanceof ExperienceOrbEntity)
								&& !(entity instanceof PersistentProjectileEntity)
								&& entity != ServerPlayNetworkHandler.this.player) {
								ServerPlayNetworkHandler.this.player.attack(entity);
							} else {
								ServerPlayNetworkHandler.this.disconnect(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
								ServerPlayNetworkHandler.LOGGER.warn("Player {} tried to attack an invalid entity", ServerPlayNetworkHandler.this.player.getName().getString());
							}
						}
					}
				);
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
				if (this.player.notInAnyWorld) {
					this.player.notInAnyWorld = false;
					this.player = this.server.getPlayerManager().respawnPlayer(this.player, true);
					Criteria.CHANGED_DIMENSION.trigger(this.player, World.END, World.OVERWORLD);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().respawnPlayer(this.player, false);
					if (this.server.isHardcore()) {
						this.player.changeGameMode(GameMode.SPECTATOR);
						this.player.getServerWorld().getGameRules().get(GameRules.SPECTATORS_GENERATE_CHUNKS).set(false, this.server);
					}
				}
				break;
			case REQUEST_STATS:
				this.player.getStatHandler().sendStats(this.player);
		}
	}

	@Override
	public void onCloseHandledScreen(CloseHandledScreenC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.closeScreenHandler();
	}

	@Override
	public void onClickSlot(ClickSlotC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.currentScreenHandler.syncId == packet.getSyncId()) {
			if (this.player.isSpectator()) {
				this.player.currentScreenHandler.syncState();
			} else {
				boolean bl = packet.getRevision() != this.player.currentScreenHandler.getRevision();
				this.player.currentScreenHandler.disableSyncing();
				this.player.currentScreenHandler.onSlotClick(packet.getSlot(), packet.getButton(), packet.getActionType(), this.player);
				ObjectIterator var3 = Int2ObjectMaps.fastIterable(packet.getModifiedStacks()).iterator();

				while (var3.hasNext()) {
					Entry<ItemStack> entry = (Entry<ItemStack>)var3.next();
					this.player.currentScreenHandler.setPreviousTrackedSlotMutable(entry.getIntKey(), (ItemStack)entry.getValue());
				}

				this.player.currentScreenHandler.setPreviousCursorStack(packet.getStack());
				this.player.currentScreenHandler.enableSyncing();
				if (bl) {
					this.player.currentScreenHandler.updateToClient();
				} else {
					this.player.currentScreenHandler.sendContentUpdates();
				}
			}
		}
	}

	@Override
	public void onCraftRequest(CraftRequestC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator()
			&& this.player.currentScreenHandler.syncId == packet.getSyncId()
			&& this.player.currentScreenHandler instanceof AbstractRecipeScreenHandler) {
			this.server
				.getRecipeManager()
				.get(packet.getRecipe())
				.ifPresent(recipe -> ((AbstractRecipeScreenHandler)this.player.currentScreenHandler).fillInputSlots(packet.shouldCraftAll(), recipe, this.player));
		}
	}

	@Override
	public void onButtonClick(ButtonClickC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (this.player.currentScreenHandler.syncId == packet.getSyncId() && !this.player.isSpectator()) {
			this.player.currentScreenHandler.onButtonClick(this.player, packet.getButtonId());
			this.player.currentScreenHandler.sendContentUpdates();
		}
	}

	@Override
	public void onCreativeInventoryAction(CreativeInventoryActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.interactionManager.isCreative()) {
			boolean bl = packet.getSlot() < 0;
			ItemStack itemStack = packet.getItemStack();
			NbtCompound nbtCompound = itemStack.getSubTag("BlockEntityTag");
			if (!itemStack.isEmpty() && nbtCompound != null && nbtCompound.contains("x") && nbtCompound.contains("y") && nbtCompound.contains("z")) {
				BlockPos blockPos = new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z"));
				BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					NbtCompound nbtCompound2 = blockEntity.writeNbt(new NbtCompound());
					nbtCompound2.remove("x");
					nbtCompound2.remove("y");
					nbtCompound2.remove("z");
					itemStack.putSubTag("BlockEntityTag", nbtCompound2);
				}
			}

			boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				this.player.playerScreenHandler.getSlot(packet.getSlot()).setStack(itemStack);
				this.player.playerScreenHandler.sendContentUpdates();
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
				this.creativeItemDropThreshold += 20;
				this.player.dropItem(itemStack, true);
			}
		}
	}

	@Override
	public void onSignUpdate(UpdateSignC2SPacket packet) {
		List<String> list = (List<String>)Stream.of(packet.getText()).map(Formatting::strip).collect(Collectors.toList());
		this.filterTexts(list, listx -> this.onSignUpdate(packet, listx));
	}

	private void onSignUpdate(UpdateSignC2SPacket packet, List<TextStream.Message> signText) {
		this.player.updateLastActionTime();
		ServerWorld serverWorld = this.player.getServerWorld();
		BlockPos blockPos = packet.getPos();
		if (serverWorld.isChunkLoaded(blockPos)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			if (!(serverWorld.getBlockEntity(blockPos) instanceof SignBlockEntity signBlockEntity)) {
				return;
			}

			if (!signBlockEntity.isEditable() || !this.player.getUuid().equals(signBlockEntity.getEditor())) {
				LOGGER.warn("Player {} just tried to change non-editable sign", this.player.getName().getString());
				return;
			}

			for (int i = 0; i < signText.size(); i++) {
				TextStream.Message message = (TextStream.Message)signText.get(i);
				if (this.player.shouldFilterText()) {
					signBlockEntity.setTextOnRow(i, new LiteralText(message.getFiltered()));
				} else {
					signBlockEntity.setTextOnRow(i, new LiteralText(message.getRaw()), new LiteralText(message.getFiltered()));
				}
			}

			signBlockEntity.markDirty();
			serverWorld.updateListeners(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveC2SPacket packet) {
		if (this.waitingForKeepAlive && packet.getId() == this.keepAliveId) {
			int i = (int)(Util.getMeasuringTimeMs() - this.lastKeepAliveTime);
			this.player.pingMilliseconds = (this.player.pingMilliseconds * 3 + i) / 4;
			this.waitingForKeepAlive = false;
		} else if (!this.isHost()) {
			this.disconnect(new TranslatableText("disconnect.timeout"));
		}
	}

	@Override
	public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.getAbilities().flying = packet.isFlying() && this.player.getAbilities().allowFlying;
	}

	@Override
	public void onClientSettings(ClientSettingsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.setClientSettings(packet);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
	}

	@Override
	public void onUpdateDifficulty(UpdateDifficultyC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2) || this.isHost()) {
			this.server.setDifficulty(packet.getDifficulty(), false);
		}
	}

	@Override
	public void onUpdateDifficultyLock(UpdateDifficultyLockC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.hasPermissionLevel(2) || this.isHost()) {
			this.server.setDifficultyLocked(packet.isDifficultyLocked());
		}
	}

	@Override
	public ServerPlayerEntity getPlayer() {
		return this.player;
	}

	@FunctionalInterface
	interface Interaction {
		ActionResult run(ServerPlayerEntity player, Entity entity, Hand hand);
	}
}
