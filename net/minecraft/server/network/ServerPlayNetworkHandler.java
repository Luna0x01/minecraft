package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_2971;
import net.minecraft.class_3915;
import net.minecraft.class_4381;
import net.minecraft.class_4384;
import net.minecraft.class_4385;
import net.minecraft.class_4386;
import net.minecraft.class_4387;
import net.minecraft.class_4388;
import net.minecraft.class_4389;
import net.minecraft.class_4390;
import net.minecraft.class_4391;
import net.minecraft.class_4392;
import net.minecraft.class_4393;
import net.minecraft.class_4398;
import net.minecraft.class_4399;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.advancement.SimpleAdvancement;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.StructureBlockEntity;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRecipeRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftingBlockData;
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
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.VillagerScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatMessageType;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Tickable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayNetworkHandler implements ServerPlayPacketListener, Tickable {
	private static final Logger LOGGER = LogManager.getLogger();
	public final ClientConnection connection;
	private final MinecraftServer server;
	public ServerPlayerEntity player;
	private int lastTickMovePacketsCount;
	private long lastKeepAliveTime;
	private boolean field_16410;
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
				LOGGER.warn("{} was kicked for floating too long!", this.player.method_15540().getString());
				this.method_14977(new TranslatableText("multiplayer.disconnect.flying"));
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
					LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.method_15540().getString());
					this.method_14977(new TranslatableText("multiplayer.disconnect.flying"));
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
		long l = Util.method_20227();
		if (l - this.lastKeepAliveTime >= 15000L) {
			if (this.field_16410) {
				this.method_14977(new TranslatableText("disconnect.timeout"));
			} else {
				this.field_16410 = true;
				this.lastKeepAliveTime = l;
				this.keepAliveId = l;
				this.sendPacket(new KeepAliveS2CPacket(this.keepAliveId));
			}
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
			&& Util.method_20227() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
			this.method_14977(new TranslatableText("multiplayer.disconnect.idling"));
		}
	}

	public void method_12823() {
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

	public void method_14977(Text text) {
		this.connection.method_20160(new DisconnectS2CPacket(text), future -> this.connection.disconnect(text));
		this.connection.disableAutoRead();
		Futures.getUnchecked(this.server.submit(this.connection::handleDisconnection));
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
			? Math.abs(playerMoveC2SPacket.method_12687(0.0)) > 3.0E7
				|| Math.abs(playerMoveC2SPacket.method_12689(0.0)) > 3.0E7
				|| Math.abs(playerMoveC2SPacket.method_12691(0.0)) > 3.0E7
			: true;
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
			this.method_14977(new TranslatableText("multiplayer.disconnect.invalid_vehicle_movement"));
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
				if (p - o > 100.0 && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(entity.method_15540().getString()))) {
					LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.method_15540().getString(), this.player.method_15540().getString(), l, m, n);
					this.connection.send(new VehicleMoveS2CPacket(entity));
					return;
				}

				boolean bl = serverWorld.method_16387(entity, entity.getBoundingBox().contract(0.0625));
				l = g - this.field_13893;
				m = h - this.field_13894 - 1.0E-6;
				n = i - this.field_13895;
				entity.move(MovementType.PLAYER, l, m, n);
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
					LOGGER.warn("{} moved wrongly!", entity.method_15540().getString());
				}

				entity.updatePositionAndAngles(g, h, i, j, k);
				boolean bl3 = serverWorld.method_16387(entity, entity.getBoundingBox().contract(0.0625));
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
			this.field_13886 = this.field_13896.x;
			this.field_13887 = this.field_13896.y;
			this.field_13888 = this.field_13896.z;
			if (this.player.method_12784()) {
				this.player.method_12785();
			}

			this.field_13896 = null;
		}
	}

	@Override
	public void onCraftingBlockData(CraftingBlockData packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getType() == CraftingBlockData.Type.SHOWN) {
			RecipeType recipeType = this.server.method_20331().method_16207(packet.method_14867());
			if (recipeType != null) {
				this.player.method_14965().method_21409(recipeType);
			}
		} else if (packet.getType() == CraftingBlockData.Type.SETTINGS) {
			this.player.method_14965().method_21397(packet.isBookOpen());
			this.player.method_14965().method_21401(packet.isFilterActive());
			this.player.method_14965().method_21405(packet.method_20300());
			this.player.method_14965().method_21408(packet.method_20301());
		}
	}

	@Override
	public void onAdvancementTab(AdvancementTabC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (packet.getAction() == AdvancementTabC2SPacket.Action.OPENED_TAB) {
			Identifier identifier = packet.getIdentifier();
			SimpleAdvancement simpleAdvancement = this.server.method_14910().method_14938(identifier);
			if (simpleAdvancement != null) {
				this.player.getAdvancementFile().method_14918(simpleAdvancement);
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

		ParseResults<class_3915> parseResults = this.server.method_2971().method_17518().parse(stringReader, this.player.method_15582());
		this.server
			.method_2971()
			.method_17518()
			.getCompletionSuggestions(parseResults)
			.thenAccept(suggestions -> this.connection.send(new CommandSuggestionsS2CPacket(packet.method_20289(), suggestions)));
	}

	@Override
	public void method_20283(class_4391 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.method_5505(new TranslatableText("advMode.notEnabled"));
		} else if (!this.player.method_15936()) {
			this.player.method_5505(new TranslatableText("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = null;
			CommandBlockBlockEntity commandBlockBlockEntity = null;
			BlockPos blockPos = arg.method_20358();
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof CommandBlockBlockEntity) {
				commandBlockBlockEntity = (CommandBlockBlockEntity)blockEntity;
				commandBlockExecutor = commandBlockBlockEntity.getCommandExecutor();
			}

			String string = arg.method_20359();
			boolean bl = arg.method_20360();
			if (commandBlockExecutor != null) {
				Direction direction = this.player.world.getBlockState(blockPos).getProperty(CommandBlock.FACING);
				switch (arg.method_20363()) {
					case SEQUENCE:
						BlockState blockState = Blocks.CHAIN_COMMAND_BLOCK.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos, blockState.withProperty(CommandBlock.FACING, direction).withProperty(CommandBlock.CONDITIONAL, Boolean.valueOf(arg.method_20361())), 2
							);
						break;
					case AUTO:
						BlockState blockState2 = Blocks.REPEATING_COMMAND_BLOCK.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos, blockState2.withProperty(CommandBlock.FACING, direction).withProperty(CommandBlock.CONDITIONAL, Boolean.valueOf(arg.method_20361())), 2
							);
						break;
					case REDSTONE:
					default:
						BlockState blockState3 = Blocks.COMMAND_BLOCK.getDefaultState();
						this.player
							.world
							.setBlockState(
								blockPos, blockState3.withProperty(CommandBlock.FACING, direction).withProperty(CommandBlock.CONDITIONAL, Boolean.valueOf(arg.method_20361())), 2
							);
				}

				blockEntity.cancelRemoval();
				this.player.world.setBlockEntity(blockPos, blockEntity);
				commandBlockExecutor.setCommand(string);
				commandBlockExecutor.setTrackOutput(bl);
				if (!bl) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockBlockEntity.method_11650(arg.method_20362());
				commandBlockExecutor.markDirty();
				if (!ChatUtil.isEmpty(string)) {
					this.player.method_5505(new TranslatableText("advMode.setCommand.success", string));
				}
			}
		}
	}

	@Override
	public void method_20284(class_4392 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (!this.server.areCommandBlocksEnabled()) {
			this.player.method_5505(new TranslatableText("advMode.notEnabled"));
		} else if (!this.player.method_15936()) {
			this.player.method_5505(new TranslatableText("advMode.notAllowed"));
		} else {
			CommandBlockExecutor commandBlockExecutor = arg.method_20364(this.player.world);
			if (commandBlockExecutor != null) {
				commandBlockExecutor.setCommand(arg.method_20366());
				commandBlockExecutor.setTrackOutput(arg.method_20367());
				if (!arg.method_20367()) {
					commandBlockExecutor.setLastOutput(null);
				}

				commandBlockExecutor.markDirty();
				this.player.method_5505(new TranslatableText("advMode.setCommand.success", arg.method_20366()));
			}
		}
	}

	@Override
	public void method_20279(class_4387 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		this.player.inventory.method_13256(arg.method_20298());
		this.player
			.networkHandler
			.sendPacket(
				new ScreenHandlerSlotUpdateS2CPacket(-2, this.player.inventory.selectedSlot, this.player.inventory.getInvStack(this.player.inventory.selectedSlot))
			);
		this.player.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, arg.method_20298(), this.player.inventory.getInvStack(arg.method_20298())));
		this.player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(this.player.inventory.selectedSlot));
	}

	@Override
	public void method_20280(class_4388 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.openScreenHandler instanceof AnvilScreenHandler) {
			AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler)this.player.openScreenHandler;
			String string = SharedConstants.stripInvalidChars(arg.method_20303());
			if (string.length() <= 35) {
				anvilScreenHandler.rename(string);
			}
		}
	}

	@Override
	public void method_20282(class_4390 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.openScreenHandler instanceof BeaconScreenHandler) {
			BeaconScreenHandler beaconScreenHandler = (BeaconScreenHandler)this.player.openScreenHandler;
			Slot slot = beaconScreenHandler.getSlot(0);
			if (slot.hasStack()) {
				slot.takeStack(1);
				Inventory inventory = beaconScreenHandler.getPaymentInventory();
				inventory.setProperty(1, arg.method_20355());
				inventory.setProperty(2, arg.method_20356());
				inventory.markDirty();
			}
		}
	}

	@Override
	public void method_20285(class_4393 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.method_15936()) {
			BlockPos blockPos = arg.method_20369();
			BlockState blockState = this.player.world.getBlockState(blockPos);
			BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
			if (blockEntity instanceof StructureBlockEntity) {
				StructureBlockEntity structureBlockEntity = (StructureBlockEntity)blockEntity;
				structureBlockEntity.method_11669(arg.method_20371());
				structureBlockEntity.method_11673(arg.method_20372());
				structureBlockEntity.method_11677(arg.method_20373());
				structureBlockEntity.method_11679(arg.method_20374());
				structureBlockEntity.method_11667(arg.method_20375());
				structureBlockEntity.method_11668(arg.method_20376());
				structureBlockEntity.method_11678(arg.method_20377());
				structureBlockEntity.method_11675(arg.method_20378());
				structureBlockEntity.method_13348(arg.method_20379());
				structureBlockEntity.method_13349(arg.method_20380());
				structureBlockEntity.method_13338(arg.method_20381());
				structureBlockEntity.method_13339(arg.method_20382());
				if (structureBlockEntity.method_16845()) {
					String string = structureBlockEntity.method_13345();
					if (arg.method_20370() == StructureBlockEntity.class_3745.SAVE_AREA) {
						if (structureBlockEntity.method_11681()) {
							this.player.sendMessage(new TranslatableText("structure_block.save_success", string), false);
						} else {
							this.player.sendMessage(new TranslatableText("structure_block.save_failure", string), false);
						}
					} else if (arg.method_20370() == StructureBlockEntity.class_3745.LOAD_AREA) {
						if (!structureBlockEntity.method_13333()) {
							this.player.sendMessage(new TranslatableText("structure_block.load_not_found", string), false);
						} else if (structureBlockEntity.method_11682()) {
							this.player.sendMessage(new TranslatableText("structure_block.load_success", string), false);
						} else {
							this.player.sendMessage(new TranslatableText("structure_block.load_prepare", string), false);
						}
					} else if (arg.method_20370() == StructureBlockEntity.class_3745.SCAN_AREA) {
						if (structureBlockEntity.method_11680()) {
							this.player.sendMessage(new TranslatableText("structure_block.size_success", string), false);
						} else {
							this.player.sendMessage(new TranslatableText("structure_block.size_failure"), false);
						}
					}
				} else {
					this.player.sendMessage(new TranslatableText("structure_block.invalid_structure_name", arg.method_20372()), false);
				}

				structureBlockEntity.markDirty();
				this.player.world.method_11481(blockPos, blockState, blockState, 3);
			}
		}
	}

	@Override
	public void method_20281(class_4389 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		int i = arg.method_20353();
		ScreenHandler screenHandler = this.player.openScreenHandler;
		if (screenHandler instanceof VillagerScreenHandler) {
			((VillagerScreenHandler)screenHandler).setRecipeIndex(i);
		}
	}

	@Override
	public void method_20277(class_4385 arg) {
		ItemStack itemStack = arg.method_20291();
		if (!itemStack.isEmpty()) {
			if (WritableBookItem.isValid(itemStack.getNbt())) {
				ItemStack itemStack2 = this.player.getStackInHand(arg.method_20293());
				if (!itemStack2.isEmpty()) {
					if (itemStack.getItem() == Items.WRITABLE_BOOK && itemStack2.getItem() == Items.WRITABLE_BOOK) {
						if (arg.method_20292()) {
							ItemStack itemStack3 = new ItemStack(Items.WRITTEN_BOOK);
							itemStack3.addNbt("author", new NbtString(this.player.method_15540().getString()));
							itemStack3.addNbt("title", new NbtString(itemStack.getNbt().getString("title")));
							NbtList nbtList = itemStack.getNbt().getList("pages", 8);

							for (int i = 0; i < nbtList.size(); i++) {
								String string = nbtList.getString(i);
								Text text = new LiteralText(string);
								string = Text.Serializer.serialize(text);
								nbtList.set(i, (NbtElement)(new NbtString(string)));
							}

							itemStack3.addNbt("pages", nbtList);
							EquipmentSlot equipmentSlot = arg.method_20293() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
							this.player.equipStack(equipmentSlot, itemStack3);
						} else {
							itemStack2.addNbt("pages", itemStack.getNbt().getList("pages", 8));
						}
					}
				}
			}
		}
	}

	@Override
	public void method_20278(class_4386 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.method_15592(2)) {
			Entity entity = this.player.getServerWorld().getEntityById(arg.method_20296());
			if (entity != null) {
				NbtCompound nbtCompound = entity.toNbt(new NbtCompound());
				this.player.networkHandler.sendPacket(new class_4381(arg.method_20295(), nbtCompound));
			}
		}
	}

	@Override
	public void method_20276(class_4384 arg) {
		NetworkThreadUtils.forceMainThread(arg, this, this.player.getServerWorld());
		if (this.player.method_15592(2)) {
			BlockEntity blockEntity = this.player.getServerWorld().getBlockEntity(arg.method_20288());
			NbtCompound nbtCompound = blockEntity != null ? blockEntity.toNbt(new NbtCompound()) : null;
			this.player.networkHandler.sendPacket(new class_4381(arg.method_20287(), nbtCompound));
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (validatePlayerMove(packet)) {
			this.method_14977(new TranslatableText("multiplayer.disconnect.invalid_player_movement"));
		} else {
			ServerWorld serverWorld = this.server.method_20312(this.player.field_16696);
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
						if (this.player.isSleeping()) {
							if (q > 1.0) {
								this.requestTeleport(this.player.x, this.player.y, this.player.z, packet.method_12688(this.player.yaw), packet.method_12690(this.player.pitch));
							}
						} else {
							this.field_13881++;
							int r = this.field_13881 - this.field_13882;
							if (r > 5) {
								LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.method_15540().getString(), r);
								r = 1;
							}

							if (!this.player.method_12784()
								&& (!this.player.getServerWorld().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.method_13055())) {
								float s = this.player.method_13055() ? 300.0F : 100.0F;
								if (q - p > (double)(s * (float)r) && (!this.server.isSinglePlayer() || !this.server.getUserName().equals(this.player.getGameProfile().getName()))) {
									LOGGER.warn("{} moved too quickly! {},{},{}", this.player.method_15540().getString(), m, n, o);
									this.requestTeleport(this.player.x, this.player.y, this.player.z, this.player.yaw, this.player.pitch);
									return;
								}
							}

							boolean bl = serverWorld.method_16387(this.player, this.player.getBoundingBox().contract(0.0625));
							m = h - this.field_13886;
							n = i - this.field_13887;
							o = j - this.field_13888;
							if (this.player.onGround && !packet.isOnGround() && n > 0.0) {
								this.player.jump();
							}

							this.player.move(MovementType.PLAYER, m, n, o);
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
								&& this.player.interactionManager.getGameMode() != GameMode.SPECTATOR) {
								bl2 = true;
								LOGGER.warn("{} moved wrongly!", this.player.method_15540().getString());
							}

							this.player.updatePositionAndAngles(h, i, j, k, l);
							this.player.method_3209(this.player.x - d, this.player.y - e, this.player.z - f);
							if (!this.player.noClip && !this.player.isSleeping()) {
								boolean bl3 = serverWorld.method_16387(this.player, this.player.getBoundingBox().contract(0.0625));
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
	}

	public void requestTeleport(double x, double y, double z, float yaw, float pitch) {
		this.teleportRequest(x, y, z, yaw, pitch, Collections.emptySet());
	}

	public void teleportRequest(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> set) {
		double d = set.contains(PlayerPositionLookS2CPacket.Flag.X) ? this.player.x : 0.0;
		double e = set.contains(PlayerPositionLookS2CPacket.Flag.Y) ? this.player.y : 0.0;
		double f = set.contains(PlayerPositionLookS2CPacket.Flag.Z) ? this.player.z : 0.0;
		float g = set.contains(PlayerPositionLookS2CPacket.Flag.Y_ROT) ? this.player.yaw : 0.0F;
		float h = set.contains(PlayerPositionLookS2CPacket.Flag.X_ROT) ? this.player.pitch : 0.0F;
		this.field_13896 = new Vec3d(x, y, z);
		if (++this.field_13897 == Integer.MAX_VALUE) {
			this.field_13897 = 0;
		}

		this.field_11776 = this.lastTickMovePacketsCount;
		this.player.updatePositionAndAngles(x, y, z, yaw, pitch);
		this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(x - d, y - e, z - f, yaw - g, pitch - h, set, this.field_13897));
	}

	@Override
	public void onPlayerAction(PlayerActionC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.method_20312(this.player.field_16696);
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
						if (!this.server.isSpawnProtected(serverWorld, blockPos, this.player) && serverWorld.method_8524().contains(blockPos)) {
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

						if (!serverWorld.getBlockState(blockPos).isAir()) {
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
		ServerWorld serverWorld = this.server.method_20312(this.player.field_16696);
		Hand hand = packet.method_12709();
		ItemStack itemStack = this.player.getStackInHand(hand);
		BlockPos blockPos = packet.getPos();
		Direction direction = packet.method_12708();
		this.player.updateLastActionTime();
		if (blockPos.getY() < this.server.getWorldHeight() - 1 || direction != Direction.UP && blockPos.getY() < this.server.getWorldHeight()) {
			if (this.field_13896 == null
				&& this.player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) < 64.0
				&& !this.server.isSpawnProtected(serverWorld, blockPos, this.player)
				&& serverWorld.method_8524().contains(blockPos)) {
				this.player
					.interactionManager
					.method_12792(this.player, serverWorld, itemStack, hand, blockPos, direction, packet.getDistanceX(), packet.getDistanceY(), packet.getDistanceZ());
			}
		} else {
			Text text = new TranslatableText("build.tooHigh", this.server.getWorldHeight()).formatted(Formatting.RED);
			this.player.networkHandler.sendPacket(new ChatMessageS2CPacket(text, ChatMessageType.GAME_INFO));
		}

		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos));
		this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(serverWorld, blockPos.offset(direction)));
	}

	@Override
	public void onSwingHand(SwingHandC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		ServerWorld serverWorld = this.server.method_20312(this.player.field_16696);
		Hand hand = packet.getHand();
		ItemStack itemStack = this.player.getStackInHand(hand);
		this.player.updateLastActionTime();
		if (!itemStack.isEmpty()) {
			this.player.interactionManager.method_12791(this.player, serverWorld, itemStack, hand);
		}
	}

	@Override
	public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.isSpectator()) {
			Entity entity = null;

			for (ServerWorld serverWorld : this.server.method_20351()) {
				entity = packet.getTarget(serverWorld);
				if (entity != null) {
					break;
				}
			}

			if (entity != null) {
				this.player.method_21282((ServerWorld)entity.world, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
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
		LOGGER.info("{} lost connection: {}", this.player.method_15540().getString(), reason.getString());
		this.server.forcePlayerSampleUpdate();
		this.server.getPlayerManager().sendToAll(new TranslatableText("multiplayer.player.left", this.player.getName()).formatted(Formatting.YELLOW));
		this.player.method_2160();
		this.server.getPlayerManager().method_12830(this.player);
		if (this.server.isSinglePlayer() && this.player.method_15540().getString().equals(this.server.getUserName())) {
			LOGGER.info("Stopping singleplayer server as player logged out");
			this.server.stopRunning();
		}
	}

	public void sendPacket(Packet<?> packet) {
		this.method_21319(packet, null);
	}

	public void method_21319(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> genericFutureListener) {
		if (packet instanceof ChatMessageS2CPacket) {
			ChatMessageS2CPacket chatMessageS2CPacket = (ChatMessageS2CPacket)packet;
			PlayerEntity.ChatVisibilityType chatVisibilityType = this.player.method_8137();
			if (chatVisibilityType == PlayerEntity.ChatVisibilityType.HIDDEN && chatMessageS2CPacket.getMessageType() != ChatMessageType.GAME_INFO) {
				return;
			}

			if (chatVisibilityType == PlayerEntity.ChatVisibilityType.SYSTEM && !chatMessageS2CPacket.isNonChat()) {
				return;
			}
		}

		try {
			this.connection.method_20160(packet, genericFutureListener);
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
			this.player.inventory.selectedSlot = packet.getSelectedSlot();
			this.player.updateLastActionTime();
		} else {
			LOGGER.warn("{} tried to set an invalid carried item", this.player.method_15540().getString());
		}
	}

	@Override
	public void onChatMessage(ChatMessageC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		if (this.player.method_8137() == PlayerEntity.ChatVisibilityType.HIDDEN) {
			this.sendPacket(new ChatMessageS2CPacket(new TranslatableText("chat.cannotSend").formatted(Formatting.RED)));
		} else {
			this.player.updateLastActionTime();
			String string = packet.getChatMessage();
			string = StringUtils.normalizeSpace(string);

			for (int i = 0; i < string.length(); i++) {
				if (!SharedConstants.isValidChar(string.charAt(i))) {
					this.method_14977(new TranslatableText("multiplayer.disconnect.illegal_characters"));
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
				this.method_14977(new TranslatableText("disconnect.spam"));
			}
		}
	}

	private void executeCommand(String string) {
		this.server.method_2971().method_17519(this.player.method_15582(), string);
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
				if (this.player.isSleeping()) {
					this.player.awaken(false, true, true);
					this.field_13896 = new Vec3d(this.player.x, this.player.y, this.player.z);
				}
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
				if (this.player.getVehicle() instanceof AbstractHorseEntity) {
					((AbstractHorseEntity)this.player.getVehicle()).method_14000(this.player);
				}
				break;
			case START_FALL_FLYING:
				if (!this.player.onGround && this.player.velocityY < 0.0 && !this.player.method_13055() && !this.player.isTouchingWater()) {
					ItemStack itemStack = this.player.getStack(EquipmentSlot.CHEST);
					if (itemStack.getItem() == Items.ELYTRA && ElytraItem.method_11370(itemStack)) {
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
		ServerWorld serverWorld = this.server.method_20312(this.player.field_16696);
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
					this.player.method_13616(entity, hand);
				} else if (packet.getType() == PlayerInteractEntityC2SPacket.Type.INTERACT_AT) {
					Hand hand2 = packet.method_12686();
					entity.interactAt(this.player, packet.getHitPosition(), hand2);
				} else if (packet.getType() == PlayerInteractEntityC2SPacket.Type.ATTACK) {
					if (entity instanceof ItemEntity || entity instanceof ExperienceOrbEntity || entity instanceof AbstractArrowEntity || entity == this.player) {
						this.method_14977(new TranslatableText("multiplayer.disconnect.invalid_entity_attacked"));
						this.server.warn("Player " + this.player.method_15540().getString() + " tried to attack an invalid entity");
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
					this.player = this.server.getPlayerManager().method_1985(this.player, DimensionType.OVERWORLD, true);
					AchievementsAndCriterions.field_16349.method_15071(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
				} else {
					if (this.player.getHealth() > 0.0F) {
						return;
					}

					this.player = this.server.getPlayerManager().method_1985(this.player, DimensionType.OVERWORLD, false);
					if (this.server.isHardcore()) {
						this.player.method_3170(GameMode.SPECTATOR);
						this.player.getServerWorld().getGameRules().method_16297("spectatorsGenerateChunks", "false", this.server);
					}
				}
				break;
			case REQUEST_STATS:
				this.player.getStatHandler().method_8273(this.player);
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
				DefaultedList<ItemStack> defaultedList = DefaultedList.of();

				for (int i = 0; i < this.player.openScreenHandler.slots.size(); i++) {
					defaultedList.add(((Slot)this.player.openScreenHandler.slots.get(i)).getStack());
				}

				this.player.method_13643(this.player.openScreenHandler, defaultedList);
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
					DefaultedList<ItemStack> defaultedList2 = DefaultedList.of();

					for (int j = 0; j < this.player.openScreenHandler.slots.size(); j++) {
						ItemStack itemStack2 = ((Slot)this.player.openScreenHandler.slots.get(j)).getStack();
						defaultedList2.add(itemStack2.isEmpty() ? ItemStack.EMPTY : itemStack2);
					}

					this.player.method_13643(this.player.openScreenHandler, defaultedList2);
				}
			}
		}
	}

	@Override
	public void onCraftRecipeRequest(CraftRecipeRequestC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.updateLastActionTime();
		if (!this.player.isSpectator() && this.player.openScreenHandler.syncId == packet.getSyncId() && this.player.openScreenHandler.isNotRestricted(this.player)) {
			RecipeType recipeType = this.server.method_20331().method_16207(packet.method_14863());
			if (this.player.openScreenHandler instanceof FurnaceScreenHandler) {
				new class_4399().method_20435(this.player, recipeType, packet.shouldMakeAll());
			} else {
				new class_4398().method_20435(this.player, recipeType, packet.shouldMakeAll());
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
			NbtCompound nbtCompound = itemStack.getNbtCompound("BlockEntityTag");
			if (!itemStack.isEmpty() && nbtCompound != null && nbtCompound.contains("x") && nbtCompound.contains("y") && nbtCompound.contains("z")) {
				BlockPos blockPos = new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z"));
				BlockEntity blockEntity = this.player.world.getBlockEntity(blockPos);
				if (blockEntity != null) {
					NbtCompound nbtCompound2 = blockEntity.toNbt(new NbtCompound());
					nbtCompound2.remove("x");
					nbtCompound2.remove("y");
					nbtCompound2.remove("z");
					itemStack.addNbt("BlockEntityTag", nbtCompound2);
				}
			}

			boolean bl2 = packet.getSlot() >= 1 && packet.getSlot() <= 45;
			boolean bl3 = itemStack.isEmpty() || itemStack.getDamage() >= 0 && itemStack.getCount() <= 64 && !itemStack.isEmpty();
			if (bl2 && bl3) {
				if (itemStack.isEmpty()) {
					this.player.playerScreenHandler.setStackInSlot(packet.getSlot(), ItemStack.EMPTY);
				} else {
					this.player.playerScreenHandler.setStackInSlot(packet.getSlot(), itemStack);
				}

				this.player.playerScreenHandler.setPlayerRestriction(this.player, true);
			} else if (bl && bl3 && this.creativeItemDropThreshold < 200) {
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
		ServerWorld serverWorld = this.server.method_20312(this.player.field_16696);
		BlockPos blockPos = packet.getSignPos();
		if (serverWorld.method_16359(blockPos)) {
			BlockState blockState = serverWorld.getBlockState(blockPos);
			BlockEntity blockEntity = serverWorld.getBlockEntity(blockPos);
			if (!(blockEntity instanceof SignBlockEntity)) {
				return;
			}

			SignBlockEntity signBlockEntity = (SignBlockEntity)blockEntity;
			if (!signBlockEntity.isEditable() || signBlockEntity.getEditor() != this.player) {
				this.server.warn("Player " + this.player.method_15540().getString() + " just tried to change non-editable sign");
				return;
			}

			String[] strings = packet.method_10729();

			for (int i = 0; i < strings.length; i++) {
				signBlockEntity.method_16837(i, new LiteralText(Formatting.strip(strings[i])));
			}

			signBlockEntity.markDirty();
			serverWorld.method_11481(blockPos, blockState, blockState, 3);
		}
	}

	@Override
	public void onKeepAlive(KeepAliveC2SPacket packet) {
		if (this.field_16410 && packet.method_7988() == this.keepAliveId) {
			int i = (int)(Util.method_20227() - this.lastKeepAliveTime);
			this.player.ping = (this.player.ping * 3 + i) / 4;
			this.field_16410 = false;
		} else if (!this.player.method_15540().getString().equals(this.server.getUserName())) {
			this.method_14977(new TranslatableText("disconnect.timeout"));
		}
	}

	@Override
	public void onPlayerAbilities(UpdatePlayerAbilitiesC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.abilities.flying = packet.isFlying() && this.player.abilities.allowFlying;
	}

	@Override
	public void onClientSettings(ClientSettingsC2SPacket packet) {
		NetworkThreadUtils.forceMainThread(packet, this, this.player.getServerWorld());
		this.player.method_12789(packet);
	}

	@Override
	public void onCustomPayload(CustomPayloadC2SPacket packet) {
	}
}
