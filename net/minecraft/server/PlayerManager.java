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
import net.minecraft.world.PlayerDataHandler;
import net.minecraft.world.World;
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
		this.bannedProfiles.setEnabled(false);
		this.bannedIps.setEnabled(false);
		this.maxPlayers = 8;
	}

	public void method_12827(ClientConnection clientConnection, ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		UserCache userCache = this.server.getUserCache();
		GameProfile gameProfile2 = userCache.getByUuid(gameProfile.getId());
		String string = gameProfile2 == null ? gameProfile.getName() : gameProfile2.getName();
		userCache.add(gameProfile);
		NbtCompound nbtCompound = this.loadPlayerData(serverPlayerEntity);
		serverPlayerEntity.setWorld(this.server.getWorld(serverPlayerEntity.dimension));
		serverPlayerEntity.interactionManager.setWorld((ServerWorld)serverPlayerEntity.world);
		String string2 = "local";
		if (clientConnection.getAddress() != null) {
			string2 = clientConnection.getAddress().toString();
		}

		LOGGER.info(
			"{}[{}] logged in with entity id {} at ({}, {}, {})",
			serverPlayerEntity.getTranslationKey(),
			string2,
			serverPlayerEntity.getEntityId(),
			serverPlayerEntity.x,
			serverPlayerEntity.y,
			serverPlayerEntity.z
		);
		ServerWorld serverWorld = this.server.getWorld(serverPlayerEntity.dimension);
		LevelProperties levelProperties = serverWorld.getLevelProperties();
		this.setGameMode(serverPlayerEntity, null, serverWorld);
		ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, clientConnection, serverPlayerEntity);
		serverPlayNetworkHandler.sendPacket(
			new GameJoinS2CPacket(
				serverPlayerEntity.getEntityId(),
				serverPlayerEntity.interactionManager.getGameMode(),
				levelProperties.isHardcore(),
				serverWorld.dimension.getDimensionType().getId(),
				serverWorld.getGlobalDifficulty(),
				this.getMaxPlayerCount(),
				levelProperties.getGeneratorType(),
				serverWorld.getGameRules().getBoolean("reducedDebugInfo")
			)
		);
		serverPlayNetworkHandler.sendPacket(
			new CustomPayloadS2CPacket("MC|Brand", new PacketByteBuf(Unpooled.buffer()).writeString(this.getServer().getServerModName()))
		);
		serverPlayNetworkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
		serverPlayNetworkHandler.sendPacket(new PlayerAbilitiesS2CPacket(serverPlayerEntity.abilities));
		serverPlayNetworkHandler.sendPacket(new HeldItemChangeS2CPacket(serverPlayerEntity.inventory.selectedSlot));
		this.method_12831(serverPlayerEntity);
		serverPlayerEntity.getStatHandler().updateStatSet();
		serverPlayerEntity.method_14965().method_14997(serverPlayerEntity);
		this.sendScoreboard((ServerScoreboard)serverWorld.getScoreboard(), serverPlayerEntity);
		this.server.forcePlayerSampleUpdate();
		TranslatableText translatableText;
		if (serverPlayerEntity.getTranslationKey().equalsIgnoreCase(string)) {
			translatableText = new TranslatableText("multiplayer.player.joined", serverPlayerEntity.getName());
		} else {
			translatableText = new TranslatableText("multiplayer.player.joined.renamed", serverPlayerEntity.getName(), string);
		}

		translatableText.getStyle().setFormatting(Formatting.YELLOW);
		this.sendToAll(translatableText);
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

	public void setMainWorld(ServerWorld[] world) {
		this.saveHandler = world[0].getSaveHandler().getInstance();
		world[0].getWorldBorder().addListener(new WorldBorderListener() {
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
		serverWorld.getChunkProvider().getOrGenerateChunks((int)player.x >> 4, (int)player.z >> 4);
		if (world != null) {
			AchievementsAndCriterions.field_16349.method_15071(player, world.dimension.getDimensionType(), serverWorld.dimension.getDimensionType());
			if (world.dimension.getDimensionType() == DimensionType.NETHER
				&& player.world.dimension.getDimensionType() == DimensionType.OVERWORLD
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
		NbtCompound nbtCompound = this.server.worlds[0].getLevelProperties().getNbt();
		NbtCompound nbtCompound2;
		if (player.getTranslationKey().equals(this.server.getUserName()) && nbtCompound != null) {
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
		ServerWorld serverWorld = this.server.getWorld(player.dimension);

		for (int i = 0; i < this.players.size(); i++) {
			player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, (ServerPlayerEntity)this.players.get(i)));
		}

		serverWorld.spawnEntity(player);
		this.method_1986(player, null);
	}

	public void method_2003(ServerPlayerEntity player) {
		player.getServerWorld().getPlayerWorldManager().method_2116(player);
	}

	public void method_12830(ServerPlayerEntity serverPlayerEntity) {
		ServerWorld serverWorld = serverPlayerEntity.getServerWorld();
		serverPlayerEntity.incrementStat(Stats.GAMES_LEFT);
		this.savePlayerData(serverPlayerEntity);
		if (serverPlayerEntity.hasMount()) {
			Entity entity = serverPlayerEntity.getRootVehicle();
			if (entity.getPassengersDeep(ServerPlayerEntity.class).size() == 1) {
				LOGGER.debug("Removing player mount");
				serverPlayerEntity.stopRiding();
				serverWorld.method_3700(entity);

				for (Entity entity2 : entity.getPassengersDeep()) {
					serverWorld.method_3700(entity2);
				}

				serverWorld.getChunk(serverPlayerEntity.chunkX, serverPlayerEntity.chunkZ).setModified();
			}
		}

		serverWorld.removeEntity(serverPlayerEntity);
		serverWorld.getPlayerWorldManager().method_2115(serverPlayerEntity);
		serverPlayerEntity.getAdvancementFile().method_14917();
		this.players.remove(serverPlayerEntity);
		UUID uUID = serverPlayerEntity.getUuid();
		ServerPlayerEntity serverPlayerEntity2 = (ServerPlayerEntity)this.playerMap.get(uUID);
		if (serverPlayerEntity2 == serverPlayerEntity) {
			this.playerMap.remove(uUID);
			this.advancementTrackers.remove(uUID);
			this.field_16412.remove(uUID);
		}

		this.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.REMOVE_PLAYER, serverPlayerEntity));
	}

	public String checkCanJoin(SocketAddress address, GameProfile profile) {
		if (this.bannedProfiles.contains(profile)) {
			BannedPlayerEntry bannedPlayerEntry = this.bannedProfiles.get(profile);
			String string = "You are banned from this server!\nReason: " + bannedPlayerEntry.getReason();
			if (bannedPlayerEntry.getExpiryDate() != null) {
				string = string + "\nYour ban will be removed on " + DATE_FORMATTER.format(bannedPlayerEntry.getExpiryDate());
			}

			return string;
		} else if (!this.isWhitelisted(profile)) {
			return "You are not white-listed on this server!";
		} else if (this.bannedIps.isBanned(address)) {
			BannedIpEntry bannedIpEntry = this.bannedIps.get(address);
			String string2 = "Your IP address is banned from this server!\nReason: " + bannedIpEntry.getReason();
			if (bannedIpEntry.getExpiryDate() != null) {
				string2 = string2 + "\nYour ban will be removed on " + DATE_FORMATTER.format(bannedIpEntry.getExpiryDate());
			}

			return string2;
		} else {
			return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(profile) ? "The server is full!" : null;
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
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(0));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(0));
		}

		return new ServerPlayerEntity(this.server, this.server.getWorld(0), profile, serverPlayerInteractionManager);
	}

	public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, int dimension, boolean alive) {
		player.getServerWorld().getEntityTracker().method_2096(player);
		player.getServerWorld().getEntityTracker().method_2101(player);
		player.getServerWorld().getPlayerWorldManager().method_2115(player);
		this.players.remove(player);
		this.server.getWorld(player.dimension).method_3700(player);
		BlockPos blockPos = player.getSpawnPosition();
		boolean bl = player.isSpawnForced();
		player.dimension = dimension;
		ServerPlayerInteractionManager serverPlayerInteractionManager;
		if (this.server.isDemo()) {
			serverPlayerInteractionManager = new DemoServerPlayerInteractionManager(this.server.getWorld(player.dimension));
		} else {
			serverPlayerInteractionManager = new ServerPlayerInteractionManager(this.server.getWorld(player.dimension));
		}

		ServerPlayerEntity serverPlayerEntity = new ServerPlayerEntity(
			this.server, this.server.getWorld(player.dimension), player.getGameProfile(), serverPlayerInteractionManager
		);
		serverPlayerEntity.networkHandler = player.networkHandler;
		serverPlayerEntity.method_14968(player, alive);
		serverPlayerEntity.setEntityId(player.getEntityId());
		serverPlayerEntity.method_10965(player);
		serverPlayerEntity.method_13264(player.getDurability());

		for (String string : player.getScoreboardTags()) {
			serverPlayerEntity.addScoreboardTag(string);
		}

		ServerWorld serverWorld = this.server.getWorld(player.dimension);
		this.setGameMode(serverPlayerEntity, player, serverWorld);
		if (blockPos != null) {
			BlockPos blockPos2 = PlayerEntity.findRespawnPosition(this.server.getWorld(player.dimension), blockPos, bl);
			if (blockPos2 != null) {
				serverPlayerEntity.refreshPositionAndAngles(
					(double)((float)blockPos2.getX() + 0.5F), (double)((float)blockPos2.getY() + 0.1F), (double)((float)blockPos2.getZ() + 0.5F), 0.0F, 0.0F
				);
				serverPlayerEntity.setPlayerSpawn(blockPos, bl);
			} else {
				serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(0, 0.0F));
			}
		}

		serverWorld.getChunkProvider().getOrGenerateChunks((int)serverPlayerEntity.x >> 4, (int)serverPlayerEntity.z >> 4);

		while (!serverWorld.doesBoxCollide(serverPlayerEntity, serverPlayerEntity.getBoundingBox()).isEmpty() && serverPlayerEntity.y < 256.0) {
			serverPlayerEntity.updatePosition(serverPlayerEntity.x, serverPlayerEntity.y + 1.0, serverPlayerEntity.z);
		}

		serverPlayerEntity.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					serverPlayerEntity.dimension,
					serverPlayerEntity.world.getGlobalDifficulty(),
					serverPlayerEntity.world.getLevelProperties().getGeneratorType(),
					serverPlayerEntity.interactionManager.getGameMode()
				)
			);
		BlockPos blockPos3 = serverWorld.getSpawnPos();
		serverPlayerEntity.networkHandler
			.requestTeleport(serverPlayerEntity.x, serverPlayerEntity.y, serverPlayerEntity.z, serverPlayerEntity.yaw, serverPlayerEntity.pitch);
		serverPlayerEntity.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(blockPos3));
		serverPlayerEntity.networkHandler
			.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayerEntity.experienceProgress, serverPlayerEntity.totalExperience, serverPlayerEntity.experienceLevel));
		this.sendWorldInfo(serverPlayerEntity, serverWorld);
		this.method_12831(serverPlayerEntity);
		serverWorld.getPlayerWorldManager().method_2109(serverPlayerEntity);
		serverWorld.spawnEntity(serverPlayerEntity);
		this.players.add(serverPlayerEntity);
		this.playerMap.put(serverPlayerEntity.getUuid(), serverPlayerEntity);
		serverPlayerEntity.listenToScreenHandler();
		serverPlayerEntity.setHealth(serverPlayerEntity.getHealth());
		return serverPlayerEntity;
	}

	public void method_12831(ServerPlayerEntity serverPlayerEntity) {
		GameProfile gameProfile = serverPlayerEntity.getGameProfile();
		int i = this.isOperator(gameProfile) ? this.ops.method_12832(gameProfile) : 0;
		i = this.server.isSinglePlayer() && this.server.worlds[0].getLevelProperties().areCheatsEnabled() ? 4 : i;
		i = this.cheatsAllowed ? 4 : i;
		this.method_12829(serverPlayerEntity, i);
	}

	public void teleportToDimension(ServerPlayerEntity player, int dimension) {
		int i = player.dimension;
		ServerWorld serverWorld = this.server.getWorld(player.dimension);
		player.dimension = dimension;
		ServerWorld serverWorld2 = this.server.getWorld(player.dimension);
		player.networkHandler
			.sendPacket(
				new PlayerRespawnS2CPacket(
					player.dimension, player.world.getGlobalDifficulty(), player.world.getLevelProperties().getGeneratorType(), player.interactionManager.getGameMode()
				)
			);
		this.method_12831(player);
		serverWorld.method_3700(player);
		player.removed = false;
		this.method_4399(player, i, serverWorld, serverWorld2);
		this.method_1986(player, serverWorld);
		player.networkHandler.requestTeleport(player.x, player.y, player.z, player.yaw, player.pitch);
		player.interactionManager.setWorld(serverWorld2);
		player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
		this.sendWorldInfo(player, serverWorld2);
		this.method_2009(player);

		for (StatusEffectInstance statusEffectInstance : player.getStatusEffectInstances()) {
			player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getEntityId(), statusEffectInstance));
		}
	}

	public void method_4399(Entity entity, int fromDimension, ServerWorld fromWorld, ServerWorld toWorld) {
		double d = entity.x;
		double e = entity.z;
		double f = 8.0;
		float g = entity.yaw;
		fromWorld.profiler.push("moving");
		if (entity.dimension == -1) {
			d = MathHelper.clamp(d / 8.0, toWorld.getWorldBorder().getBoundWest() + 16.0, toWorld.getWorldBorder().getBoundEast() - 16.0);
			e = MathHelper.clamp(e / 8.0, toWorld.getWorldBorder().getBoundNorth() + 16.0, toWorld.getWorldBorder().getBoundSouth() - 16.0);
			entity.refreshPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
			if (entity.isAlive()) {
				fromWorld.checkChunk(entity, false);
			}
		} else if (entity.dimension == 0) {
			d = MathHelper.clamp(d * 8.0, toWorld.getWorldBorder().getBoundWest() + 16.0, toWorld.getWorldBorder().getBoundEast() - 16.0);
			e = MathHelper.clamp(e * 8.0, toWorld.getWorldBorder().getBoundNorth() + 16.0, toWorld.getWorldBorder().getBoundSouth() - 16.0);
			entity.refreshPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
			if (entity.isAlive()) {
				fromWorld.checkChunk(entity, false);
			}
		} else {
			BlockPos blockPos;
			if (fromDimension == 1) {
				blockPos = toWorld.getSpawnPos();
			} else {
				blockPos = toWorld.getForcedSpawnPoint();
			}

			d = (double)blockPos.getX();
			entity.y = (double)blockPos.getY();
			e = (double)blockPos.getZ();
			entity.refreshPositionAndAngles(d, entity.y, e, 90.0F, 0.0F);
			if (entity.isAlive()) {
				fromWorld.checkChunk(entity, false);
			}
		}

		fromWorld.profiler.pop();
		if (fromDimension != 1) {
			fromWorld.profiler.push("placing");
			d = (double)MathHelper.clamp((int)d, -29999872, 29999872);
			e = (double)MathHelper.clamp((int)e, -29999872, 29999872);
			if (entity.isAlive()) {
				entity.refreshPositionAndAngles(d, entity.y, e, entity.yaw, entity.pitch);
				toWorld.getPortalTeleporter().method_8583(entity, g);
				toWorld.spawnEntity(entity);
				toWorld.checkChunk(entity, false);
			}

			fromWorld.profiler.pop();
		}

		entity.setWorld(toWorld);
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

	public void sendToDimension(Packet<?> packet, int dimension) {
		for (int i = 0; i < this.players.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(i);
			if (serverPlayerEntity.dimension == dimension) {
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
					serverPlayerEntity.sendMessage(text);
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
					serverPlayerEntity.sendMessage(text);
				}
			}
		}
	}

	public String method_8226(boolean bl) {
		String string = "";
		List<ServerPlayerEntity> list = Lists.newArrayList(this.players);

		for (int i = 0; i < list.size(); i++) {
			if (i > 0) {
				string = string + ", ";
			}

			string = string + ((ServerPlayerEntity)list.get(i)).getTranslationKey();
			if (bl) {
				string = string + " (" + ((ServerPlayerEntity)list.get(i)).getEntityName() + ")";
			}
		}

		return string;
	}

	public String[] getPlayerNames() {
		String[] strings = new String[this.players.size()];

		for (int i = 0; i < this.players.size(); i++) {
			strings[i] = ((ServerPlayerEntity)this.players.get(i)).getTranslationKey();
		}

		return strings;
	}

	public GameProfile[] getProfiles() {
		GameProfile[] gameProfiles = new GameProfile[this.players.size()];

		for (int i = 0; i < this.players.size(); i++) {
			gameProfiles[i] = ((ServerPlayerEntity)this.players.get(i)).getGameProfile();
		}

		return gameProfiles;
	}

	public BannedPlayerList getUserBanList() {
		return this.bannedProfiles;
	}

	public BannedIpList getIpBanList() {
		return this.bannedIps;
	}

	public void op(GameProfile profile) {
		int i = this.server.getOpPermissionLevel();
		this.ops.add(new OperatorEntry(profile, this.server.getOpPermissionLevel(), this.ops.isOp(profile)));
		this.method_12829(this.getPlayer(profile.getId()), i);
	}

	public void deop(GameProfile profile) {
		this.ops.remove(profile);
		this.method_12829(this.getPlayer(profile.getId()), 0);
	}

	private void method_12829(ServerPlayerEntity serverPlayerEntity, int i) {
		if (serverPlayerEntity != null && serverPlayerEntity.networkHandler != null) {
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
	}

	public boolean isWhitelisted(GameProfile profile) {
		return !this.whitelistEnabled || this.ops.contains(profile) || this.whitelist.contains(profile);
	}

	public boolean isOperator(GameProfile profile) {
		return this.ops.contains(profile)
			|| this.server.isSinglePlayer()
				&& this.server.worlds[0].getLevelProperties().areCheatsEnabled()
				&& this.server.getUserName().equalsIgnoreCase(profile.getName())
			|| this.cheatsAllowed;
	}

	@Nullable
	public ServerPlayerEntity getPlayer(String username) {
		for (ServerPlayerEntity serverPlayerEntity : this.players) {
			if (serverPlayerEntity.getTranslationKey().equalsIgnoreCase(username)) {
				return serverPlayerEntity;
			}
		}

		return null;
	}

	public void method_12828(@Nullable PlayerEntity playerEntity, double d, double e, double f, double g, int i, Packet<?> packet) {
		for (int j = 0; j < this.players.size(); j++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.players.get(j);
			if (serverPlayerEntity != playerEntity && serverPlayerEntity.dimension == i) {
				double h = d - serverPlayerEntity.x;
				double k = e - serverPlayerEntity.y;
				double l = f - serverPlayerEntity.z;
				if (h * h + k * k + l * l < g * g) {
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

	public void whitelist(GameProfile profile) {
		this.whitelist.add(new WhitelistEntry(profile));
	}

	public void unWhitelist(GameProfile profile) {
		this.whitelist.remove(profile);
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
		WorldBorder worldBorder = this.server.worlds[0].getWorldBorder();
		player.networkHandler.sendPacket(new WorldBorderS2CPacket(worldBorder, WorldBorderS2CPacket.Type.INITIALIZE));
		player.networkHandler
			.sendPacket(new WorldTimeUpdateS2CPacket(world.getLastUpdateTime(), world.getTimeOfDay(), world.getGameRules().getBoolean("doDaylightCycle")));
		BlockPos blockPos = world.getSpawnPos();
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
		return this.server.worlds[0].getSaveHandler().getInstance().getSavedPlayerIds();
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

	private void setGameMode(ServerPlayerEntity player, ServerPlayerEntity oldPlayer, World world) {
		if (oldPlayer != null) {
			player.interactionManager.setGameMode(oldPlayer.interactionManager.getGameMode());
		} else if (this.field_2719 != null) {
			player.interactionManager.setGameMode(this.field_2719);
		}

		player.interactionManager.method_2174(world.getLevelProperties().getGamemode());
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
		this.server.sendMessage(text);
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
			File file = new File(this.server.getWorld(0).getSaveHandler().getWorldFolder(), "stats");
			File file2 = new File(file, uUID + ".json");
			if (!file2.exists()) {
				File file3 = new File(file, player.getTranslationKey() + ".json");
				if (file3.exists() && file3.isFile()) {
					file3.renameTo(file2);
				}
			}

			serverStatHandler = new ServerStatHandler(this.server, file2);
			serverStatHandler.method_8270();
			this.advancementTrackers.put(uUID, serverStatHandler);
		}

		return serverStatHandler;
	}

	public AdvancementFile method_14979(ServerPlayerEntity serverPlayerEntity) {
		UUID uUID = serverPlayerEntity.getUuid();
		AdvancementFile advancementFile = (AdvancementFile)this.field_16412.get(uUID);
		if (advancementFile == null) {
			File file = new File(this.server.getWorld(0).getSaveHandler().getWorldFolder(), "advancements");
			File file2 = new File(file, uUID + ".json");
			advancementFile = new AdvancementFile(this.server, file2, serverPlayerEntity);
			this.field_16412.put(uUID, advancementFile);
		}

		advancementFile.setPlayer(serverPlayerEntity);
		return advancementFile;
	}

	public void setViewDistance(int viewDistance) {
		this.viewDistance = viewDistance;
		if (this.server.worlds != null) {
			for (ServerWorld serverWorld : this.server.worlds) {
				if (serverWorld != null) {
					serverWorld.getPlayerWorldManager().applyViewDistance(viewDistance);
					serverWorld.getEntityTracker().method_12765(viewDistance);
				}
			}
		}
	}

	public List<ServerPlayerEntity> getPlayers() {
		return this.players;
	}

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
	}
}
