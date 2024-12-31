package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_4382;
import net.minecraft.class_4383;
import net.minecraft.advancement.AchievementsAndCriterions;
import net.minecraft.advancement.AdvancementFile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.DemoServerPlayerInteractionManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ChatMessageType;
import net.minecraft.util.Formatting;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.IWorld;
import net.minecraft.world.PlayerDataHandler;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.border.WorldBorderListener;
import net.minecraft.world.chunk.ThreadedAnvilChunkStorage;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerManager {
	public static final File BANNED_PLAYERS_FILE = new File("banned-players.json");
	public static final File BANNED_IPS_FILE = new File("banned-ips.json");
	public static final File OPERATORS_FILE = new File("ops.json");
	public static final File WHITELIST_FILE = new File("whitelist.json");
	private static final Logger LOGGER = LogManager.getLogger();
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
	private final MinecraftServer server;
	private final List<ServerPlayerEntity> players = Lists.newArrayList();
	private final Map<UUID, ServerPlayerEntity> playerMap = Maps.newHashMap();
	private final BannedPlayerList bannedProfiles = new BannedPlayerList(BANNED_PLAYERS_FILE);
	private final BannedIpList bannedIps = new BannedIpList(BANNED_IPS_FILE);
	private final OperatorList ops = new OperatorList(OPERATORS_FILE);
	private final Whitelist whitelist = new Whitelist(WHITELIST_FILE);
	private final Map<UUID, ServerStatHandler> advancementTrackers = Maps.newHashMap();
	private final Map<UUID, AdvancementFile> field_16412 = Maps.newHashMap();
	private PlayerDataHandler saveHandler;
	private boolean whitelistEnabled;
	protected int maxPlayers;
	private int viewDistance;
	private GameMode field_2719;
	private boolean cheatsAllowed;
	private int latencyUpdateTimer;

	public PlayerManager(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
		this.getUserBanList().setEnabled(true);
		this.getIpBanList().setEnabled(true);
		this.maxPlayers = 8;
	}

	public void method_12827(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		UserCache userCache = this.server.getUserCache();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		NbtCompound nbtCompound = this.loadPlayerData(serverPlayerEntity);
		serverPlayerEntity.setWorld(this.server.method_20312(serverPlayerEntity.field_16696));
		serverPlayerEntity.interactionManager.setWorld((ServerWorld)serverPlayerEntity.world);
		String string2 = "local";
		if (clientConnection.getAddress() != null) {
			string2 = clientConnection.getAddress().toString();
		}

		LOGGER.info(
			"{}[{}] logged in with entity id {} at ({}, {}, {})",
			serverPlayerEntity.method_15540().getString(),
			string2,
			serverPlayerEntity.getEntityId(),
			serverPlayerEntity.x,
			serverPlayerEntity.y,
			serverPlayerEntity.z
		);
		ServerWorld serverWorld = this.server.method_20312(serverPlayerEntity.field_16696);
		LevelProperties levelProperties = serverWorld.method_3588();
		this.method_1987(serverPlayerEntity, null, serverWorld);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, clientConnection, serverPlayerEntity);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinS2CPacket(
				serverPlayerEntity.getEntityId(),
				serverPlayerEntity.interactionManager.getGameMode(),
				levelProperties.isHardcore(),
				serverWorld.dimension.method_11789(),
				serverWorld.method_16346(),
				this.getMaxPlayerCount(),
				levelProperties.getGeneratorType(),
				serverWorld.getGameRules().getBoolean("reducedDebugInfo")
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadS2CPacket(CustomPayloadS2CPacket.field_21532, new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(serverPlayerEntity.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
		serverPlayNetworkHandler.sendPacket(new class_4382(this.server.method_20331().method_16208()));
		serverPlayNetworkHandler.sendPacket(new class_4383(this.server.method_20332()));
		this.method_12831(serverPlayerEntity);
		serverPlayerEntity.getStatHandler().updateStatSet();
		serverPlayerEntity.method_14965().method_14997(serverPlayerEntity);
		this.sendScoreboard(serverWorld.getScoreboard(), serverPlayerEntity);
		this.server.forcePlayerSampleUpdate();
		Text text;
		if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(string)) {
			text = new TranslatableText("multiplayer.player.joined", serverPlayerEntity.getName());
		} else {
			text = new TranslatableText("multiplayer.player.joined.renamed", serverPlayerEntity.getName(), string);
		}

		this.sendToAll(text.formatted(Formatting.YELLOW));
		this.sendPlayerList(serverPlayerEntity);
		serverPlayNetworkHandler.requestTeleport(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		this.sendWorldInfo(serverPlayerEntity, serverWorld);
		if (!this.server.getResourcePackUrl().isEmpty()) {
			serverPlayerEntity.sendResourcePackUrl(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
		}

		for (StatusEffectInstance statusEffectInstance : serverPlayerEntity.getStatusEffectInstances()) {
			serverPlayNetworkHandler.sendPacket(new EntityStatusEffectS2CPacket(serverPlayerEntity.getEntityId(), statusEffectInstance));
		}

		if (nbtCompound != null && nbtCompound.contains("RootVehicle", 10)) {
			NbtCompound nbtCompound2 = nbtCompound.getCompound("RootVehicle");
			Entity entity = ThreadedAnvilChunkStorage.method_11784(nbtCompound2.getCompound("Entity"), serverWorld, true);
			if (entity != null) {
				UUID uUID = nbtCompound2.getUuid("Attach");
				if (entity.getUuid().equals(uUID)) {
					serverPlayerEntity.startRiding(entity, true);
				} else {
					for (Entity entity2 : entity.getPassengersDeep()) {
						if (entity2.getUuid().equals(uUID)) {
							serverPlayerEntity.startRiding(entity2, true);
							break;
						}
					}
				}

				if (!serverPlayerEntity.hasMount()) {
					LOGGER.warn("Couldn't reattach entity to player");
					serverWorld.method_3700(entity);

					for (Entity entity3 : entity.getPassengersDeep()) {
						serverWorld.method_3700(entity3);
					}
				}
			}
		}

		serverPlayerEntity.listenToScreenHandler();
	}

	protected void sendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player) {
		Set<ScoreboardObjective> set = Sets.newHashSet();

		for (Team team : scoreboard.getTeams()) {
			player.networkHandler.sendPacket(new TeamS2CPacket(team, 0));
		}

		for (int i = 0; i < 19; i++) {
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(i);
			if (scoreboardObjective != null && !set.contains(scoreboardObjective)) {
				for (Packet<?> packet : scoreboard.createChangePackets(scoreboardObjective)) {
					player.networkHandler.sendPacket(packet);
				}

				set.add(scoreboardObjective);
			}
		}
	}

	public void method_21387(ServerWorld serverWorld) {
		this.saveHandler = serverWorld.method_3587().getInstance();
		serverWorld.method_8524().addListener(new WorldBorderListener() {
			@Override
			public void onSizeChange(WorldBorder border, double newSize) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_SIZE));
			}

			@Override
			public void onInterpolateSize(WorldBorder border, double oldSize, double targetSize, long time) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.LERP_SIZE));
			}

			@Override
			public void onCenterChanged(WorldBorder border, double centerX, double centerZ) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_CENTER));
			}

			@Override
			public void onWarningTimeChanged(WorldBorder border, int newTime) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_TIME));
			}

			@Override
			public void onWarningBlocksChanged(WorldBorder border, int warningBlocks) {
				PlayerManager.this.sendToAll(new WorldBorderS2CPacket(border, WorldBorderS2CPacket.Type.SET_WARNING_BLOCKS));
			}

			@Override
			public void onDamagePerBlockChanged(WorldBorder border, double damagePerBlock) {
			}

			@Override
			public void onSafeZoneChanged(WorldBorder border, double safeZone) {
			}
		});
	}

	public void method_1986(ServerPlayerEntity player, @Nullable ServerWorld world) {
		ServerWorld serverWorld = player.getServerWorld();
		if (world != null) {
			world.getPlayerWorldManager().method_2115(player);
		}

		serverWorld.getPlayerWorldManager().method_2109(player);
		serverWorld.method_3586().method_17044((int)player.x >> 4, (int)player.z >> 4, true, true);
		if (world != null) {
			AchievementsAndCriterions.field_16349.method_15071(player, world.dimension.method_11789(), serverWorld.dimension.method_11789());
			if (world.dimension.method_11789() == DimensionType.THE_NETHER
				&& player.world.dimension.method_11789() == DimensionType.OVERWORLD
				&& player.method_14967() != null) {
				AchievementsAndCriterions.field_16327.method_14354(player, player.method_14967());
			}
		}
	}

	public int method_1978() {
		return PlayerWorldManager.method_2104(this.getViewDistance());
	}

	@Nullable
	public NbtCompound loadPlayerData(ServerPlayerEntity player) {
		NbtCompound nbtCompound = this.server.method_20312(DimensionType.OVERWORLD).method_3588().getNbt();
		NbtCompound nbtCompound2;
		if (player.method_15540().getString().equals(this.server.getUserName()) && nbtCompound != null) {
			nbtCompound2 = nbtCompound;
			player.fromNbt(nbtCompound);
			LOGGER.debug("loading single player");
		} else {
			nbtCompound2 = this.saveHandler.getPlayerData(player);
		}

		return nbtCompound2;
	}

	protected void savePlayerData(ServerPlayerEntity player) {
		this.saveHandler.savePlayerData(player);
		ServerStatHandler serverStatHandler = (ServerStatHandler)this.advancementTrackers.get(player.getUuid());
		if (serverStatHandler != null) {
			serverStatHandler.save();
		}

		AdvancementFile advancementFile = (AdvancementFile)this.field_16412.get(player.getUuid());
		if (advancementFile != null) {
			advancementFile.method_14926();
		}
	}

	public void sendPlayerList(ServerPlayerEntity player) {
		this.players.add(player);
		this.playerMap.put(player.getUuid(), player);
		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, player));
		ServerWorld serverWorld = this.server.method_20312(player.field_16696);

		for (int i = 0; i < this.players.size(); i++) {
			player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, (ServerPlayerEntity)this.players.get(i)));
		}

		serverWorld.method_3686(player);
		this.method_1986(player, null);
		this.server.method_20336().method_20482(player);
	}

	public void method_2003(ServerPlayerEntity player) {
		player.getServerWorld().getPlayerWorldManager().method_2116(player);
	}

	public void method_12830(ServerPlayerEntity serverPlayerEntity) {
		ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
		serverPlayerEntity.method_15928(Stats.LEAVE_GAME);
		this.savePlayerData(serverPlayerEntity);
		if (serverPlayerEntity.hasMount()) {
			Entity entity = serverPlayerEntity.getRootVehicle();
			if (entity.method_15581()) {
				LOGGER.debug("Removing player mount");
				serverPlayerEntity.stopRiding();
				serverWorld.method_3700(entity);

				for (Entity entity2 : entity.getPassengersDeep()) {
					serverWorld.method_3700(entity2);
				}

				serverWorld.method_16347(serverPlayerEntity.chunkX, serverPlayerEntity.chunkZ).setModified();
			}
		}

		serverWorld.removeEntity(serverPlayerEntity);
		serverWorld.getPlayerWorldManager().method_2115(serverPlayerEntity);
		serverPlayerEntity.getAdvancementFile().method_14917();
		this.players.remove(serverPlayerEntity);
		this.server.method_20336().method_20484(serverPlayerEntity);
		UUID uUID = serverPlayerEntity.getUuid();
		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(uUID);
		if (serverPlayerEntity2 == serverPlayerEntity) {
			this.playerMap.remove(uUID);
			this.advancementTrackers.remove(uUID);
			this.field_16412.remove(uUID);
		}

		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, serverPlayerEntity));
	}

	@Nullable
	public Text method_21386(SocketAddress socketAddress, GameProfile gameProfile) {
		if (this.bannedProfiles.contains(gameProfile)) {
			BannedPlayerEntry bannedPlayerEntry = this.bannedProfiles.get(gameProfile);
			Text text = new TranslatableText("multiplayer.disconnect.banned.reason", bannedPlayerEntry.getReason());
			if (bannedPlayerEntry.getExpiryDate() != null) {
				text.append(new TranslatableText("multiplayer.disconnect.banned.expiration", DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate())));
			}

			return text;
		} else if (!this.isWhitelisted(gameProfile)) {
			return new TranslatableText("multiplayer.disconnect.not_whitelisted");
		} else if (this.bannedIps.isBanned(socketAddress)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.get(socketAddress);
			Text text2 = new TranslatableText("multiplayer.disconnect.banned_ip.reason", bannedIpEntry.getReason());
			if (bannedIpEntry.getExpiryDate() != null) {
				text2.append(new TranslatableText("multiplayer.disconnect.banned_ip.expiration", DATE_FORMATTER.format(bannedIpEntry.getExpiryDate())));
			}

			return text2;
		} else {
			return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile) ? new TranslatableText("multiplayer.disconnect.server_full") : null;
		}
	}

	public ServerPlayerEntity createPlayer(GameProfile profile) {
		UUID uUID = PlayerEntity.getUuidFromProfile(profile);
		List<ServerPlayerEntity> list = Lists.newArrayList();

		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.getUuid().equals(uUID)) {
				list.add(serverPlayerEntity);
			}
		}

		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(profile.getId());
		if (serverPlayerEntity2 != null && !list.contains(serverPlayerEntity2)) {
			list.add(serverPlayerEntity2);
		}

		for (ServerPlayerEntity serverPlayerEntity3 : list) {
			serverPlayerEntity3.networkHandler.method_14977(new TranslatableText("multiplayer.disconnect.duplicate_login"));
		}

		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.method_20312(DimensionType.OVERWORLD));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.method_20312(DimensionType.OVERWORLD));
		}

		return new ServerPlayerEntity(this.server, this.server.method_20312(DimensionType.OVERWORLD), profile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity method_1985(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType, boolean bl) {
		serverPlayerEntity.getServerWorld().getEntityTracker().method_2096(serverPlayerEntity);
		serverPlayerEntity.getServerWorld().getEntityTracker().method_2101(serverPlayerEntity);
		serverPlayerEntity.getServerWorld().getPlayerWorldManager().method_2115(serverPlayerEntity);
		this.players.remove(serverPlayerEntity);
		this.server.method_20312(serverPlayerEntity.field_16696).method_3700(serverPlayerEntity);
		BlockPos blockPos = serverPlayerEntity.getSpawnPosition();
		boolean bl2 = serverPlayerEntity.isSpawnForced();
		serverPlayerEntity.field_16696 = dimensionType;
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.method_20312(serverPlayerEntity.field_16696));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.method_20312(serverPlayerEntity.field_16696));
		}

		ServerPlayerEntity serverPlayerEntity2 = new ServerPlayerEntity(
			this.server, this.server.method_20312(serverPlayerEntity.field_16696), serverPlayerEntity.getGameProfile(), serverPlayerInteractionManager
		);
		serverPlayerEntity2.networkHandler = serverPlayerEntity.networkHandler;
		serverPlayerEntity2.method_14968(serverPlayerEntity, bl);
		serverPlayerEntity2.setEntityId(serverPlayerEntity.getEntityId());
		serverPlayerEntity2.method_13264(serverPlayerEntity.getDurability());

		for (String string : serverPlayerEntity.getScoreboardTags()) {
			serverPlayerEntity2.addScoreboardTag(string);
		}

		ServerWorld serverWorld = this.server.method_20312(serverPlayerEntity.field_16696);
		this.method_1987(serverPlayerEntity2, serverPlayerEntity, serverWorld);
		if (blockPos != null) {
			BlockPos blockPos2 = PlayerEntity.method_15925(this.server.method_20312(serverPlayerEntity.field_16696), blockPos, bl2);
			if (blockPos2 != null) {
				serverPlayerEntity2.refreshPositionAndAngles(
					(double)((float)blockPos2.getX() + 0.5F), (double)((float)blockPos2.getY() + 0.1F), (double)((float)blockPos2.getZ() + 0.5F), 0.0F, 0.0F
				);
				serverPlayerEntity2.setPlayerSpawn(blockPos, bl2);
			} else {
				serverPlayerEntity2.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0F));
			}
		}

		serverWorld.method_3586().method_17044((int)serverPlayerEntity2.x >> 4, (int)serverPlayerEntity2.z >> 4, true, true);

		while (!serverWorld.method_16387(serverPlayerEntity2, serverPlayerEntity2.getBoundingBox()) && serverPlayerEntity2.y < 256.0) {
			serverPlayerEntity2.updatePosition(serverPlayerEntity2.x, serverPlayerEntity2.y + 1.0, serverPlayerEntity2.z);
		}

		serverPlayerEntity2.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					serverPlayerEntity2.field_16696,
					serverPlayerEntity2.world.method_16346(),
					serverPlayerEntity2.world.method_3588().getGeneratorType(),
					serverPlayerEntity2.interactionManager.getGameMode()
				)
			);
		BlockPos blockPos3 = serverWorld.method_3585();
		serverPlayerEntity2.networkHandler
			.requestTeleport(serverPlayerEntity2.x, serverPlayerEntity2.y, serverPlayerEntity2.z, serverPlayerEntity2.yaw, serverPlayerEntity2.pitch);
		serverPlayerEntity2.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos3));
		serverPlayerEntity2.networkHandler
			.sendPacket(
				new ExperienceBarUpdateS2CPacket(serverPlayerEntity2.experienceProgress, serverPlayerEntity2.totalExperience, serverPlayerEntity2.experienceLevel)
			);
		this.sendWorldInfo(serverPlayerEntity2, serverWorld);
		this.method_12831(serverPlayerEntity2);
		serverWorld.getPlayerWorldManager().method_2109(serverPlayerEntity2);
		serverWorld.method_3686(serverPlayerEntity2);
		this.players.add(serverPlayerEntity2);
		this.playerMap.put(serverPlayerEntity2.getUuid(), serverPlayerEntity2);
		serverPlayerEntity2.listenToScreenHandler();
		serverPlayerEntity2.setHealth(serverPlayerEntity2.getHealth());
		return serverPlayerEntity2;
	}

	public void method_12831(ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		int i = this.server.method_20318(gameProfile);
		this.method_12829(serverPlayerEntity, i);
	}

	public void method_1984(ServerPlayerEntity serverPlayerEntity, DimensionType dimensionType) {
		DimensionType dimensionType2 = serverPlayerEntity.field_16696;
		ServerWorld serverWorld = this.server.method_20312(serverPlayerEntity.field_16696);
		serverPlayerEntity.field_16696 = dimensionType;
		ServerWorld serverWorld2 = this.server.method_20312(serverPlayerEntity.field_16696);
		serverPlayerEntity.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					serverPlayerEntity.field_16696,
					serverPlayerEntity.world.method_16346(),
					serverPlayerEntity.world.method_3588().getGeneratorType(),
					serverPlayerEntity.interactionManager.getGameMode()
				)
			);
		this.method_12831(serverPlayerEntity);
		serverWorld.method_3700(serverPlayerEntity);
		serverPlayerEntity.removed = false;
		this.method_4399(serverPlayerEntity, dimensionType2, serverWorld, serverWorld2);
		this.method_1986(serverPlayerEntity, serverWorld);
		serverPlayerEntity.networkHandler
			.requestTeleport(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		serverPlayerEntity.interactionManager.setWorld(serverWorld2);
		serverPlayerEntity.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(serverPlayerEntity.abilities));
		this.sendWorldInfo(serverPlayerEntity, serverWorld2);
		this.method_2009(serverPlayerEntity);

		for (StatusEffectInstance statusEffectInstance : serverPlayerEntity.getStatusEffectInstances()) {
			serverPlayerEntity.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(serverPlayerEntity.getEntityId(), statusEffectInstance));
		}
	}

	public void method_4399(Entity entity, DimensionType dimensionType, ServerWorld serverWorld, ServerWorld serverWorld2) {
		double d = entity.x;
		double e = entity.z;
		double f = 8.0;
		float g = entity.yaw;
		serverWorld.profiler.push("moving");
		if (entity.field_16696 == DimensionType.THE_NETHER) {
			d = MathHelper.clamp(d / 8.0, serverWorld2.method_8524().getBoundWest() + 16.0, serverWorld2.method_8524().getBoundEast() - 16.0);
			e = MathHelper.clamp(e / 8.0, serverWorld2.method_8524().getBoundNorth() + 16.0, serverWorld2.method_8524().getBoundSouth() - 16.0);
			entity.refreshPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
			if (entity.isAlive()) {
				serverWorld.checkChunk(entity, false);
			}
		} else if (entity.field_16696 == DimensionType.OVERWORLD) {
			d = MathHelper.clamp(d * 8.0, serverWorld2.method_8524().getBoundWest() + 16.0, serverWorld2.method_8524().getBoundEast() - 16.0);
			e = MathHelper.clamp(e * 8.0, serverWorld2.method_8524().getBoundNorth() + 16.0, serverWorld2.method_8524().getBoundSouth() - 16.0);
			entity.refreshPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
			if (entity.isAlive()) {
				serverWorld.checkChunk(entity, false);
			}
		} else {
			BlockPos blockPos;
			if (dimensionType == DimensionType.THE_END) {
				blockPos = serverWorld2.method_3585();
			} else {
				blockPos = serverWorld2.getForcedSpawnPoint();
			}

			d = (double)blockPos.getX();
			entity.y = (double)blockPos.getY();
			e = (double)blockPos.getZ();
			entity.refreshPositionAndAngles(d, entity.y, e, 90.0F, 0.0F);
			if (entity.isAlive()) {
				serverWorld.checkChunk(entity, false);
			}
		}

		serverWorld.profiler.pop();
		if (dimensionType != DimensionType.THE_END) {
			serverWorld.profiler.push("placing");
			d = (double)MathHelper.clamp((int)d, -29999872, 29999872);
			e = (double)MathHelper.clamp((int)e, -29999872, 29999872);
			if (entity.isAlive()) {
				entity.refreshPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
				serverWorld2.getPortalTeleporter().method_8583(entity, g);
				serverWorld2.method_3686(entity);
				serverWorld2.checkChunk(entity, false);
			}

			serverWorld.profiler.pop();
		}

		entity.setWorld(serverWorld2);
	}

	public void updatePlayerLatency() {
		if (++this.latencyUpdateTimer > 600) {
			this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LATENCY, this.players));
			this.latencyUpdateTimer = 0;
		}
	}

	public void sendToAll(Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).networkHandler.sendPacket(packet);
		}
	}

	public void method_21385(Packet<?> packet, DimensionType dimensionType) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.field_16696 == dimensionType) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}
	}

	public void sendMessageToTeam(PlayerEntity source, Text text) {
		AbstractTeam abstractTeam = source.getScoreboardTeam();
		if (abstractTeam != null) {
			for (String string : abstractTeam.getPlayerList()) {
				ServerPlayerEntity serverPlayerEntity = this.getPlayer(string);
				if (serverPlayerEntity != null && serverPlayerEntity != source) {
					serverPlayerEntity.method_5505(text);
				}
			}
		}
	}

	public void sendMessageToOtherTeams(PlayerEntity source, Text text) {
		AbstractTeam abstractTeam = source.getScoreboardTeam();
		if (abstractTeam == null) {
			this.sendToAll(text);
		} else {
			for (int i = 0; i < this.players.size(); i++) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
				if (serverPlayerEntity.getScoreboardTeam() != abstractTeam) {
					serverPlayerEntity.method_5505(text);
				}
			}
		}
	}

	public String[] getPlayerNames() {
		String[] strings = new String[this.players.size()];

		for (int i = 0; i < this.players.size(); i++) {
			strings[i] = ((ServerPlayerEntity)this.players.get(i)).getGameProfile().getName();
		}

		return strings;
	}

	public BannedPlayerList getUserBanList() {
		return this.bannedProfiles;
	}

	public BannedIpList getIpBanList() {
		return this.bannedIps;
	}

	public void op(GameProfile profile) {
		this.ops.add(new OperatorEntry(profile, this.server.getOpPermissionLevel(), this.ops.isOp(profile)));
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
		if (serverPlayerEntity != null) {
			this.method_12831(serverPlayerEntity);
		}
	}

	public void deop(GameProfile profile) {
		this.ops.remove(profile);
		ServerPlayerEntity serverPlayerEntity = this.getPlayer(profile.getId());
		if (serverPlayerEntity != null) {
			this.method_12831(serverPlayerEntity);
		}
	}

	private void method_12829(ServerPlayerEntity serverPlayerEntity, int i) {
		if (serverPlayerEntity.networkHandler != null) {
			byte b;
			if (i <= 0) {
				b = 24;
			} else if (i >= 4) {
				b = 28;
			} else {
				b = (byte)(24 + i);
			}

			serverPlayerEntity.networkHandler.sendPacket(new EntityStatusS2CPacket(serverPlayerEntity, b));
		}

		this.server.method_2971().method_17532(serverPlayerEntity);
	}

	public boolean isWhitelisted(GameProfile profile) {
		return !this.whitelistEnabled || this.ops.contains(profile) || this.whitelist.contains(profile);
	}

	public boolean isOperator(GameProfile profile) {
		return this.ops.contains(profile)
			|| this.server.isSinglePlayer()
				&& this.server.method_20312(DimensionType.OVERWORLD).method_3588().areCheatsEnabled()
				&& this.server.getUserName().equalsIgnoreCase(profile.getName())
			|| this.cheatsAllowed;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(String username) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getGameProfile().getName().equalsIgnoreCase(username)) {
				return serverPlayerEntity;
			}
		}

		return null;
	}

	public void method_12828(@Nullable PlayerEntity playerEntity, double d, double e, double f, double g, DimensionType dimensionType, Packet<?> packet) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity != playerEntity && serverPlayerEntity.field_16696 == dimensionType) {
				double h = d - serverPlayerEntity.x;
				double j = e - serverPlayerEntity.y;
				double k = f - serverPlayerEntity.z;
				if (h * h + j * j + k * k < g * g) {
					serverPlayerEntity.networkHandler.sendPacket(packet);
				}
			}
		}
	}

	public void saveAllPlayerData() {
		for (int i = 0; i < this.players.size(); i++) {
			this.savePlayerData((ServerPlayerEntity)this.players.get(i));
		}
	}

	public Whitelist getWhitelist() {
		return this.whitelist;
	}

	public String[] getWhitelistedNames() {
		return this.whitelist.getNames();
	}

	public OperatorList getOpList() {
		return this.ops;
	}

	public String[] getOpNames() {
		return this.ops.getNames();
	}

	public void reloadWhitelist() {
	}

	public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {
		WorldBorder worldBorder = this.server.method_20312(DimensionType.OVERWORLD).method_8524();
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
		player.networkHandler
			.sendPacket(new WorldTimeUpdateS2CPacket(world.getLastUpdateTime(), world.getTimeOfDay(), world.getGameRules().getBoolean("doDaylightCycle")));
		BlockPos blockPos = world.method_3585();
		player.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos));
		if (world.isRaining()) {
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(1, 0.0F));
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(7, world.getRainGradient(1.0F)));
			player.networkHandler.sendPacket(new GameStateChangeS2CPacket(8, world.getThunderGradient(1.0F)));
		}
	}

	public void method_2009(ServerPlayerEntity player) {
		player.refreshScreenHandler(player.playerScreenHandler);
		player.markHealthDirty();
		player.networkHandler.sendPacket(new HeldItemChangeS2CPacket(player.inventory.selectedSlot));
	}

	public int getCurrentPlayerCount() {
		return this.players.size();
	}

	public int getMaxPlayerCount() {
		return this.maxPlayers;
	}

	public String[] getSavedPlayerIds() {
		return this.server.method_20312(DimensionType.OVERWORLD).method_3587().getInstance().getSavedPlayerIds();
	}

	public boolean isWhitelistEnabled() {
		return this.whitelistEnabled;
	}

	public void setWhitelistEnabled(boolean whitelistEnabled) {
		this.whitelistEnabled = whitelistEnabled;
	}

	public List<ServerPlayerEntity> getPlayersByIp(String ip) {
		List<ServerPlayerEntity> list = Lists.newArrayList();

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getIp().equals(ip)) {
				list.add(serverPlayerEntity);
			}
		}

		return list;
	}

	public int getViewDistance() {
		return this.viewDistance;
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public NbtCompound getUserData() {
		return null;
	}

	public void setGameMode(GameMode gamemode) {
		this.field_2719 = gamemode;
	}

	private void method_1987(ServerPlayerEntity serverPlayerEntity, ServerPlayerEntity serverPlayerEntity2, IWorld iWorld) {
		if (serverPlayerEntity2 != null) {
			serverPlayerEntity.interactionManager.setGameMode(serverPlayerEntity2.interactionManager.getGameMode());
		} else if (this.field_2719 != null) {
			serverPlayerEntity.interactionManager.setGameMode(this.field_2719);
		}

		serverPlayerEntity.interactionManager.method_2174(iWorld.method_3588().getGamemode());
	}

	public void setCheatsAllowed(boolean cheatsAllowed) {
		this.cheatsAllowed = cheatsAllowed;
	}

	public void disconnectAllPlayers() {
		for (int i = 0; i < this.players.size(); i++) {
			((ServerPlayerEntity)this.players.get(i)).networkHandler.method_14977(new TranslatableText("multiplayer.disconnect.server_shutdown"));
		}
	}

	public void broadcastChatMessage(Text text, boolean system) {
		this.server.method_5505(text);
		ChatMessageType chatMessageType = system ? ChatMessageType.SYSTEM : ChatMessageType.CHAT;
		this.sendToAll(new ChatMessageS2CPacket(text, chatMessageType));
	}

	public void sendToAll(Text text) {
		this.broadcastChatMessage(text, true);
	}

	public ServerStatHandler createStatHandler(PlayerEntity player) {
		UUID uUID = player.getUuid();
		ServerStatHandler serverStatHandler = uUID == null ? null : (ServerStatHandler)this.advancementTrackers.get(uUID);
		if (serverStatHandler == null) {
			File file = new File(this.server.method_20312(DimensionType.OVERWORLD).method_3587().getWorldFolder(), "stats");
			File file2 = new File(file, uUID + ".json");
			if (!file2.exists()) {
				File file3 = new File(file, player.method_15540().getString() + ".json");
				if (file3.exists() && file3.isFile()) {
					file3.renameTo(file2);
				}
			}

			serverStatHandler = new ServerStatHandler(this.server, file2);
			this.advancementTrackers.put(uUID, serverStatHandler);
		}

		return serverStatHandler;
	}

	public AdvancementFile method_14979(ServerPlayerEntity serverPlayerEntity) {
		UUID uUID = serverPlayerEntity.getUuid();
		AdvancementFile advancementFile = (AdvancementFile)this.field_16412.get(uUID);
		if (advancementFile == null) {
			File file = new File(this.server.method_20312(DimensionType.OVERWORLD).method_3587().getWorldFolder(), "advancements");
			File file2 = new File(file, uUID + ".json");
			advancementFile = new AdvancementFile(this.server, file2, serverPlayerEntity);
			this.field_16412.put(uUID, advancementFile);
		}

		advancementFile.setPlayer(serverPlayerEntity);
		return advancementFile;
	}

	public void setViewDistance(int viewDistance) {
		this.viewDistance = viewDistance;

		for (ServerWorld serverWorld : this.server.method_20351()) {
			if (serverWorld != null) {
				serverWorld.getPlayerWorldManager().applyViewDistance(viewDistance);
				serverWorld.getEntityTracker().method_12765(viewDistance);
			}
		}
	}

	public List<ServerPlayerEntity> getPlayers() {
		return this.players;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(UUID UUID) {
		return (ServerPlayerEntity)this.playerMap.get(UUID);
	}

	public boolean canBypassPlayerLimit(GameProfile profile) {
		return false;
	}

	public void method_14980() {
		for (AdvancementFile advancementFile : this.field_16412.values()) {
			advancementFile.method_14922();
		}

		this.sendToAll(new class_4383(this.server.method_20332()));
		class_4382 lv = new class_4382(this.server.method_20331().method_16208());

		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			serverPlayerEntity.networkHandler.sendPacket(lv);
			serverPlayerEntity.method_14965().method_14997(serverPlayerEntity);
		}
	}

	public boolean method_21388() {
		return this.cheatsAllowed;
	}
}
