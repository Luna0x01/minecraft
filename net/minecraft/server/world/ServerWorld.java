package net.minecraft.server.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_2772;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.HorseType;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.PortalTeleporter;
import net.minecraft.entity.Tradable;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.class_2787;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockActionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerWorldManager;
import net.minecraft.structure.class_2763;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.chunk.ServerChunkProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerWorld extends World implements ThreadExecutor {
	private static final Logger LOGGER = LogManager.getLogger();
	private final MinecraftServer server;
	private final EntityTracker entityTracker;
	private final PlayerWorldManager playerWorldManager;
	private final Set<ScheduledTick> field_2811 = Sets.newHashSet();
	private final TreeSet<ScheduledTick> scheduledTicks = new TreeSet();
	private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
	public boolean savingDisabled;
	private boolean ready;
	private int idleTimeout;
	private final PortalTeleporter portalTeleporter;
	private final MobSpawnerHelper field_6728 = new MobSpawnerHelper();
	protected final ZombieSiegeManager field_11761 = new ZombieSiegeManager(this);
	private final ServerWorld.BlockActionList[] field_2815 = new ServerWorld.BlockActionList[]{
		new ServerWorld.BlockActionList(), new ServerWorld.BlockActionList()
	};
	private int field_2816;
	private final List<ScheduledTick> field_6729 = Lists.newArrayList();

	public ServerWorld(MinecraftServer minecraftServer, SaveHandler saveHandler, LevelProperties levelProperties, int i, Profiler profiler) {
		super(saveHandler, levelProperties, DimensionType.fromId(i).create(), profiler, false);
		this.server = minecraftServer;
		this.entityTracker = new EntityTracker(this);
		this.playerWorldManager = new PlayerWorldManager(this);
		this.dimension.copyFromWorld(this);
		this.chunkProvider = this.getChunkCache();
		this.portalTeleporter = new PortalTeleporter(this);
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
		this.getWorldBorder().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
	}

	@Override
	public World getWorld() {
		this.persistentStateManager = new PersistentStateManager(this.saveHandler);
		String string = VillageState.getId(this.dimension);
		VillageState villageState = (VillageState)this.persistentStateManager.getOrCreate(VillageState.class, string);
		if (villageState == null) {
			this.villageState = new VillageState(this);
			this.persistentStateManager.replace(string, this.villageState);
		} else {
			this.villageState = villageState;
			this.villageState.setWorld(this);
		}

		this.scoreboard = new ServerScoreboard(this.server);
		ScoreboardState scoreboardState = (ScoreboardState)this.persistentStateManager.getOrCreate(ScoreboardState.class, "scoreboard");
		if (scoreboardState == null) {
			scoreboardState = new ScoreboardState();
			this.persistentStateManager.replace("scoreboard", scoreboardState);
		}

		scoreboardState.setScoreboard(this.scoreboard);
		((ServerScoreboard)this.scoreboard).method_12759(new class_2772(scoreboardState));
		this.field_12435 = new class_2787(new File(new File(this.saveHandler.getWorldFolder(), "data"), "loot_tables"));
		this.getWorldBorder().setCenter(this.levelProperties.getBorderCenterX(), this.levelProperties.getBorderCenterZ());
		this.getWorldBorder().setDamagePerBlock(this.levelProperties.getBorderDamagePerBlock());
		this.getWorldBorder().setSafeZone(this.levelProperties.getSafeZone());
		this.getWorldBorder().setWarningBlocks(this.levelProperties.getBorderWarningBlocks());
		this.getWorldBorder().setWarningTime(this.levelProperties.getBorderWarningTime());
		if (this.levelProperties.getBorderSizeLerpTime() > 0L) {
			this.getWorldBorder()
				.interpolateSize(this.levelProperties.getBorderSize(), this.levelProperties.getBorderSizeLerpTarget(), this.levelProperties.getBorderSizeLerpTime());
		} else {
			this.getWorldBorder().setSize(this.levelProperties.getBorderSize());
		}

		return this;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getLevelProperties().isHardcore() && this.getGlobalDifficulty() != Difficulty.HARD) {
			this.getLevelProperties().setDifficulty(Difficulty.HARD);
		}

		this.dimension.method_9175().method_11539();
		if (this.isReady()) {
			if (this.getGameRules().getBoolean("doDaylightCycle")) {
				long l = this.levelProperties.getTimeOfDay() + 24000L;
				this.levelProperties.setDayTime(l - l % 24000L);
			}

			this.awakenPlayers();
		}

		this.profiler.push("mobSpawner");
		if (this.getGameRules().getBoolean("doMobSpawning") && this.levelProperties.getGeneratorType() != LevelGeneratorType.DEBUG) {
			this.field_6728.tickSpawners(this, this.spawnAnimals, this.spawnMonsters, this.levelProperties.getTime() % 400L == 0L);
		}

		this.profiler.swap("chunkSource");
		this.chunkProvider.tickChunks();
		int i = this.method_3597(1.0F);
		if (i != this.getAmbientDarkness()) {
			this.setAmbientDarkness(i);
		}

		this.levelProperties.setTime(this.levelProperties.getTime() + 1L);
		if (this.getGameRules().getBoolean("doDaylightCycle")) {
			this.levelProperties.setDayTime(this.levelProperties.getTimeOfDay() + 1L);
		}

		this.profiler.swap("tickPending");
		this.method_3644(false);
		this.profiler.swap("tickBlocks");
		this.tickBlocks();
		this.profiler.swap("chunkMap");
		this.playerWorldManager.method_2111();
		this.profiler.swap("village");
		this.villageState.method_2839();
		this.field_11761.method_2835();
		this.profiler.swap("portalForcer");
		this.portalTeleporter.method_4698(this.getLastUpdateTime());
		this.profiler.pop();
		this.method_2131();
	}

	@Nullable
	public Biome.SpawnEntry method_10754(EntityCategory entityCategory, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = this.getChunkProvider().method_12775(entityCategory, blockPos);
		return list != null && !list.isEmpty() ? Weighting.getRandom(this.random, list) : null;
	}

	public boolean method_10753(EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = this.getChunkProvider().method_12775(entityCategory, blockPos);
		return list != null && !list.isEmpty() ? list.contains(spawnEntry) : false;
	}

	@Override
	public void updateSleepingStatus() {
		this.ready = false;
		if (!this.playerEntities.isEmpty()) {
			int i = 0;
			int j = 0;

			for (PlayerEntity playerEntity : this.playerEntities) {
				if (playerEntity.isSpectator()) {
					i++;
				} else if (playerEntity.isSleeping()) {
					j++;
				}
			}

			this.ready = j > 0 && j >= this.playerEntities.size() - i;
		}
	}

	protected void awakenPlayers() {
		this.ready = false;

		for (PlayerEntity playerEntity : this.playerEntities) {
			if (playerEntity.isSleeping()) {
				playerEntity.awaken(false, false, true);
			}
		}

		this.resetWeather();
	}

	private void resetWeather() {
		this.levelProperties.setRainTime(0);
		this.levelProperties.setRaining(false);
		this.levelProperties.setThunderTime(0);
		this.levelProperties.setThundering(false);
	}

	public boolean isReady() {
		if (this.ready && !this.isClient) {
			for (PlayerEntity playerEntity : this.playerEntities) {
				if (!playerEntity.isSpectator() && !playerEntity.isSleepingLongEnough()) {
					return false;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setDefaultSpawnClient() {
		if (this.levelProperties.getSpawnY() <= 0) {
			this.levelProperties.setSpawnY(this.getSeaLevel() + 1);
		}

		int i = this.levelProperties.getSpawnX();
		int j = this.levelProperties.getSpawnZ();
		int k = 0;

		while (this.method_8540(new BlockPos(i, 0, j)).getMaterial() == Material.AIR) {
			i += this.random.nextInt(8) - this.random.nextInt(8);
			j += this.random.nextInt(8) - this.random.nextInt(8);
			if (++k == 10000) {
				break;
			}
		}

		this.levelProperties.setSpawnX(i);
		this.levelProperties.setSpawnZ(j);
	}

	@Override
	protected boolean isChunkLoaded(int chunkX, int chunkZ, boolean canBeEmpty) {
		return this.getChunkProvider().method_3864(chunkX, chunkZ);
	}

	protected void method_12780() {
		this.profiler.push("playerCheckLight");
		if (!this.playerEntities.isEmpty()) {
			int i = this.random.nextInt(this.playerEntities.size());
			PlayerEntity playerEntity = (PlayerEntity)this.playerEntities.get(i);
			int j = MathHelper.floor(playerEntity.x) + this.random.nextInt(11) - 5;
			int k = MathHelper.floor(playerEntity.y) + this.random.nextInt(11) - 5;
			int l = MathHelper.floor(playerEntity.z) + this.random.nextInt(11) - 5;
			this.method_8568(new BlockPos(j, k, l));
		}

		this.profiler.pop();
	}

	@Override
	protected void tickBlocks() {
		this.method_12780();
		if (this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			Iterator<Chunk> iterator = this.playerWorldManager.method_12810();

			while (iterator.hasNext()) {
				((Chunk)iterator.next()).populateBlockEntities(false);
			}
		} else {
			int i = this.getGameRules().getInt("randomTickSpeed");
			boolean bl = this.isRaining();
			boolean bl2 = this.isThundering();
			this.profiler.push("pollingChunks");

			for (Iterator<Chunk> iterator2 = this.playerWorldManager.method_12810(); iterator2.hasNext(); this.profiler.pop()) {
				this.profiler.push("getChunk");
				Chunk chunk = (Chunk)iterator2.next();
				int j = chunk.chunkX * 16;
				int k = chunk.chunkZ * 16;
				this.profiler.swap("checkNextLight");
				chunk.method_3923();
				this.profiler.swap("tickChunk");
				chunk.populateBlockEntities(false);
				this.profiler.swap("thunder");
				if (bl && bl2 && this.random.nextInt(100000) == 0) {
					this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
					int l = this.lcgBlockSeed >> 2;
					BlockPos blockPos = this.method_10749(new BlockPos(j + (l & 15), 0, k + (l >> 8 & 15)));
					if (this.hasRain(blockPos)) {
						LocalDifficulty localDifficulty = this.getLocalDifficulty(blockPos);
						if (this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.05) {
							HorseBaseEntity horseBaseEntity = new HorseBaseEntity(this);
							horseBaseEntity.method_13126(HorseType.SKELETON);
							horseBaseEntity.method_13133(true);
							horseBaseEntity.setAge(0);
							horseBaseEntity.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
							this.spawnEntity(horseBaseEntity);
							this.addEntity(new LightningBoltEntity(this, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), true));
						} else {
							this.addEntity(new LightningBoltEntity(this, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), false));
						}
					}
				}

				this.profiler.swap("iceandsnow");
				if (this.random.nextInt(16) == 0) {
					this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
					int m = this.lcgBlockSeed >> 2;
					BlockPos blockPos2 = this.method_8562(new BlockPos(j + (m & 15), 0, k + (m >> 8 & 15)));
					BlockPos blockPos3 = blockPos2.down();
					if (this.canWaterNotFreezeAt(blockPos3)) {
						this.setBlockState(blockPos3, Blocks.ICE.getDefaultState());
					}

					if (bl && this.method_8552(blockPos2, true)) {
						this.setBlockState(blockPos2, Blocks.SNOW_LAYER.getDefaultState());
					}

					if (bl && this.getBiome(blockPos3).method_3830()) {
						this.getBlockState(blockPos3).getBlock().onRainTick(this, blockPos3);
					}
				}

				this.profiler.swap("tickBlocks");
				if (i > 0) {
					for (ChunkSection chunkSection : chunk.getBlockStorage()) {
						if (chunkSection != Chunk.EMPTY && chunkSection.hasTickableBlocks()) {
							for (int p = 0; p < i; p++) {
								this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
								int q = this.lcgBlockSeed >> 2;
								int r = q & 15;
								int s = q >> 8 & 15;
								int t = q >> 16 & 15;
								BlockState blockState = chunkSection.getBlockState(r, t, s);
								Block block = blockState.getBlock();
								this.profiler.push("randomTick");
								if (block.ticksRandomly()) {
									block.onRandomTick(this, new BlockPos(r + j, t + chunkSection.getYOffset(), s + k), blockState, this.random);
								}

								this.profiler.pop();
							}
						}
					}
				}
			}

			this.profiler.pop();
		}
	}

	protected BlockPos method_10749(BlockPos blockPos) {
		BlockPos blockPos2 = this.method_8562(blockPos);
		Box box = new Box(blockPos2, new BlockPos(blockPos2.getX(), this.getMaxBuildHeight(), blockPos2.getZ())).expand(3.0);
		List<LivingEntity> list = this.getEntitiesInBox(LivingEntity.class, box, new Predicate<LivingEntity>() {
			public boolean apply(@Nullable LivingEntity livingEntity) {
				return livingEntity != null && livingEntity.isAlive() && ServerWorld.this.hasDirectSunlight(livingEntity.getBlockPos());
			}
		});
		if (!list.isEmpty()) {
			return ((LivingEntity)list.get(this.random.nextInt(list.size()))).getBlockPos();
		} else {
			if (blockPos2.getY() == -1) {
				blockPos2 = blockPos2.up(2);
			}

			return blockPos2;
		}
	}

	@Override
	public boolean hasScheduledTick(BlockPos pos, Block block) {
		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		return this.field_6729.contains(scheduledTick);
	}

	@Override
	public boolean method_11489(BlockPos pos, Block block) {
		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		return this.field_2811.contains(scheduledTick);
	}

	@Override
	public void createAndScheduleBlockTick(BlockPos pos, Block block, int tickRate) {
		this.createAndScheduleBlockTick(pos, block, tickRate, 0);
	}

	@Override
	public void createAndScheduleBlockTick(BlockPos pos, Block block, int tickRate, int priority) {
		if (pos instanceof BlockPos.Mutable || pos instanceof BlockPos.Pooled) {
			pos = new BlockPos(pos);
			LogManager.getLogger().warn("Tried to assign a mutable BlockPos to tick data...", new Error(pos.getClass().toString()));
		}

		Material material = block.getDefaultState().getMaterial();
		if (this.immediateUpdates && material != Material.AIR) {
			if (block.doImmediateUpdates()) {
				if (this.isRegionLoaded(pos.add(-8, -8, -8), pos.add(8, 8, 8))) {
					BlockState blockState = this.getBlockState(pos);
					if (blockState.getMaterial() != Material.AIR && blockState.getBlock() == block) {
						blockState.getBlock().onScheduledTick(this, pos, blockState, this.random);
					}
				}

				return;
			}

			tickRate = 1;
		}

		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		if (this.blockExists(pos)) {
			if (material != Material.AIR) {
				scheduledTick.setTime((long)tickRate + this.levelProperties.getTime());
				scheduledTick.setPriority(priority);
			}

			if (!this.field_2811.contains(scheduledTick)) {
				this.field_2811.add(scheduledTick);
				this.scheduledTicks.add(scheduledTick);
			}
		}
	}

	@Override
	public void scheduleTick(BlockPos pos, Block block, int tickRate, int priority) {
		if (pos instanceof BlockPos.Mutable || pos instanceof BlockPos.Pooled) {
			pos = new BlockPos(pos);
			LogManager.getLogger().warn("Tried to assign a mutable BlockPos to tick data...", new Error(pos.getClass().toString()));
		}

		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		scheduledTick.setPriority(priority);
		Material material = block.getDefaultState().getMaterial();
		if (material != Material.AIR) {
			scheduledTick.setTime((long)tickRate + this.levelProperties.getTime());
		}

		if (!this.field_2811.contains(scheduledTick)) {
			this.field_2811.add(scheduledTick);
			this.scheduledTicks.add(scheduledTick);
		}
	}

	@Override
	public void tickEntities() {
		if (this.playerEntities.isEmpty()) {
			if (this.idleTimeout++ >= 300) {
				return;
			}
		} else {
			this.resetIdleTimeout();
		}

		this.dimension.method_11791();
		super.tickEntities();
	}

	@Override
	protected void method_11491() {
		super.method_11491();
		this.profiler.swap("players");

		for (int i = 0; i < this.playerEntities.size(); i++) {
			Entity entity = (Entity)this.playerEntities.get(i);
			Entity entity2 = entity.getVehicle();
			if (entity2 != null) {
				if (!entity2.removed && entity2.hasPassenger(entity)) {
					continue;
				}

				entity.stopRiding();
			}

			this.profiler.push("tick");
			if (!entity.removed) {
				try {
					this.checkChunk(entity);
				} catch (Throwable var7) {
					CrashReport crashReport = CrashReport.create(var7, "Ticking player");
					CrashReportSection crashReportSection = crashReport.addElement("Player being ticked");
					entity.populateCrashReport(crashReportSection);
					throw new CrashException(crashReport);
				}
			}

			this.profiler.pop();
			this.profiler.push("remove");
			if (entity.removed) {
				int j = entity.chunkX;
				int k = entity.chunkZ;
				if (entity.updateNeeded && this.isChunkLoaded(j, k, true)) {
					this.getChunk(j, k).removeEntity(entity);
				}

				this.loadedEntities.remove(entity);
				this.onEntityRemoved(entity);
			}

			this.profiler.pop();
		}
	}

	public void resetIdleTimeout() {
		this.idleTimeout = 0;
	}

	@Override
	public boolean method_3644(boolean bl) {
		if (this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			return false;
		} else {
			int i = this.scheduledTicks.size();
			if (i != this.field_2811.size()) {
				throw new IllegalStateException("TickNextTick list out of synch");
			} else {
				if (i > 65536) {
					i = 65536;
				}

				this.profiler.push("cleaning");

				for (int j = 0; j < i; j++) {
					ScheduledTick scheduledTick = (ScheduledTick)this.scheduledTicks.first();
					if (!bl && scheduledTick.time > this.levelProperties.getTime()) {
						break;
					}

					this.scheduledTicks.remove(scheduledTick);
					this.field_2811.remove(scheduledTick);
					this.field_6729.add(scheduledTick);
				}

				this.profiler.pop();
				this.profiler.push("ticking");
				Iterator<ScheduledTick> iterator = this.field_6729.iterator();

				while (iterator.hasNext()) {
					ScheduledTick scheduledTick2 = (ScheduledTick)iterator.next();
					iterator.remove();
					int k = 0;
					if (this.isRegionLoaded(scheduledTick2.pos.add(0, 0, 0), scheduledTick2.pos.add(0, 0, 0))) {
						BlockState blockState = this.getBlockState(scheduledTick2.pos);
						if (blockState.getMaterial() != Material.AIR && Block.areBlocksEqual(blockState.getBlock(), scheduledTick2.getBlock())) {
							try {
								blockState.getBlock().onScheduledTick(this, scheduledTick2.pos, blockState, this.random);
							} catch (Throwable var10) {
								CrashReport crashReport = CrashReport.create(var10, "Exception while ticking a block");
								CrashReportSection crashReportSection = crashReport.addElement("Block being ticked");
								CrashReportSection.addBlockInfo(crashReportSection, scheduledTick2.pos, blockState);
								throw new CrashException(crashReport);
							}
						}
					} else {
						this.createAndScheduleBlockTick(scheduledTick2.pos, scheduledTick2.getBlock(), 0);
					}
				}

				this.profiler.pop();
				this.field_6729.clear();
				return !this.scheduledTicks.isEmpty();
			}
		}
	}

	@Nullable
	@Override
	public List<ScheduledTick> getScheduledTicks(Chunk chunk, boolean bl) {
		ChunkPos chunkPos = chunk.getChunkPos();
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.getScheduledTicks(new BlockBox(i, 0, k, j, 256, l), bl);
	}

	@Nullable
	@Override
	public List<ScheduledTick> getScheduledTicks(BlockBox box, boolean bl) {
		List<ScheduledTick> list = null;

		for (int i = 0; i < 2; i++) {
			Iterator<ScheduledTick> iterator;
			if (i == 0) {
				iterator = this.scheduledTicks.iterator();
			} else {
				iterator = this.field_6729.iterator();
			}

			while (iterator.hasNext()) {
				ScheduledTick scheduledTick = (ScheduledTick)iterator.next();
				BlockPos blockPos = scheduledTick.pos;
				if (blockPos.getX() >= box.minX && blockPos.getX() < box.maxX && blockPos.getZ() >= box.minZ && blockPos.getZ() < box.maxZ) {
					if (bl) {
						if (i == 0) {
							this.field_2811.remove(scheduledTick);
						}

						iterator.remove();
					}

					if (list == null) {
						list = Lists.newArrayList();
					}

					list.add(scheduledTick);
				}
			}
		}

		return list;
	}

	@Override
	public void checkChunk(Entity entity, boolean bl) {
		if (!this.method_10756() && (entity instanceof AnimalEntity || entity instanceof WaterCreatureEntity)) {
			entity.remove();
		}

		if (!this.method_10755() && entity instanceof Tradable) {
			entity.remove();
		}

		super.checkChunk(entity, bl);
	}

	private boolean method_10755() {
		return this.server.shouldSpawnNpcs();
	}

	private boolean method_10756() {
		return this.server.shouldSpawnAnimals();
	}

	@Override
	protected ChunkProvider getChunkCache() {
		ChunkStorage chunkStorage = this.saveHandler.getChunkWriter(this.dimension);
		return new ServerChunkProvider(this, chunkStorage, this.dimension.getChunkGenerator());
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return !this.server.isSpawnProtected(this, pos, player) && this.getWorldBorder().contains(pos);
	}

	@Override
	public void setPropertiesInitialized(LevelInfo info) {
		if (!this.levelProperties.isInitialized()) {
			try {
				this.init(info);
				if (this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
					this.setDebugLevelProperties();
				}

				super.setPropertiesInitialized(info);
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Exception initializing level");

				try {
					this.addToCrashReport(crashReport);
				} catch (Throwable var5) {
				}

				throw new CrashException(crashReport);
			}

			this.levelProperties.setInitialized(true);
		}
	}

	private void setDebugLevelProperties() {
		this.levelProperties.setStructures(false);
		this.levelProperties.setCheats(true);
		this.levelProperties.setRaining(false);
		this.levelProperties.setThundering(false);
		this.levelProperties.setClearWeatherTime(1000000000);
		this.levelProperties.setDayTime(6000L);
		this.levelProperties.getGameMode(GameMode.SPECTATOR);
		this.levelProperties.setHardcore(false);
		this.levelProperties.setDifficulty(Difficulty.PEACEFUL);
		this.levelProperties.setDifficultyLocked(true);
		this.getGameRules().setGameRule("doDaylightCycle", "false");
	}

	private void init(LevelInfo levelInfo) {
		if (!this.dimension.containsWorldSpawn()) {
			this.levelProperties.setSpawnPos(BlockPos.ORIGIN.up(this.dimension.getAverageYLevel()));
		} else if (this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			this.levelProperties.setSpawnPos(BlockPos.ORIGIN.up());
		} else {
			this.field_4523 = true;
			SingletonBiomeSource singletonBiomeSource = this.dimension.method_9175();
			List<Biome> list = singletonBiomeSource.method_11532();
			Random random = new Random(this.getSeed());
			BlockPos blockPos = singletonBiomeSource.method_11534(0, 0, 256, list, random);
			int i = 8;
			int j = this.dimension.getAverageYLevel();
			int k = 8;
			if (blockPos != null) {
				i = blockPos.getX();
				k = blockPos.getZ();
			} else {
				LOGGER.warn("Unable to find spawn biome");
			}

			int l = 0;

			while (!this.dimension.isSpawnableBlock(i, k)) {
				i += random.nextInt(64) - random.nextInt(64);
				k += random.nextInt(64) - random.nextInt(64);
				if (++l == 1000) {
					break;
				}
			}

			this.levelProperties.setSpawnPos(new BlockPos(i, j, k));
			this.field_4523 = false;
			if (levelInfo.hasBonusChest()) {
				this.placeBonusChest();
			}
		}
	}

	protected void placeBonusChest() {
		BonusChestFeature bonusChestFeature = new BonusChestFeature();

		for (int i = 0; i < 10; i++) {
			int j = this.levelProperties.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
			int k = this.levelProperties.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
			BlockPos blockPos = this.getTopPosition(new BlockPos(j, 0, k)).up();
			if (bonusChestFeature.generate(this, this.random, blockPos)) {
				break;
			}
		}
	}

	public BlockPos getForcedSpawnPoint() {
		return this.dimension.getForcedSpawnPoint();
	}

	public void save(boolean bl, @Nullable ProgressListener listener) throws WorldSaveException {
		ServerChunkProvider serverChunkProvider = this.getChunkProvider();
		if (serverChunkProvider.canSaveChunks()) {
			if (listener != null) {
				listener.setTitle("Saving level");
			}

			this.method_2132();
			if (listener != null) {
				listener.setTask("Saving chunks");
			}

			serverChunkProvider.saveAllChunks(bl);

			for (Chunk chunk : Lists.newArrayList(serverChunkProvider.method_12772())) {
				if (chunk != null && !this.playerWorldManager.method_12808(chunk.chunkX, chunk.chunkZ)) {
					serverChunkProvider.unload(chunk);
				}
			}
		}
	}

	public void method_5323() {
		ServerChunkProvider serverChunkProvider = this.getChunkProvider();
		if (serverChunkProvider.canSaveChunks()) {
			serverChunkProvider.flushChunks();
		}
	}

	protected void method_2132() throws WorldSaveException {
		this.readSaveLock();

		for (ServerWorld serverWorld : this.server.worlds) {
			if (serverWorld instanceof MultiServerWorld) {
				((MultiServerWorld)serverWorld).method_12763();
			}
		}

		this.levelProperties.setBorderSize(this.getWorldBorder().getOldSize());
		this.levelProperties.setBorderCenterX(this.getWorldBorder().getCenterX());
		this.levelProperties.setBorderCenterZ(this.getWorldBorder().getCenterZ());
		this.levelProperties.setSafeZone(this.getWorldBorder().getSafeZone());
		this.levelProperties.setBorderDamagePerBlock(this.getWorldBorder().getBorderDamagePerBlock());
		this.levelProperties.setBorderWarningBlocks(this.getWorldBorder().getWarningBlocks());
		this.levelProperties.setBorderWarningTime(this.getWorldBorder().getWarningTime());
		this.levelProperties.setBorderSizeLerpTarget(this.getWorldBorder().getTargetSize());
		this.levelProperties.setBorderSizeLerpTime(this.getWorldBorder().getInterpolationDuration());
		this.saveHandler.saveWorld(this.levelProperties, this.server.getPlayerManager().getUserData());
		this.persistentStateManager.save();
	}

	@Override
	public boolean spawnEntity(Entity entity) {
		return this.method_12781(entity) ? super.spawnEntity(entity) : false;
	}

	@Override
	public void method_8537(Collection<Entity> collection) {
		for (Entity entity : Lists.newArrayList(collection)) {
			if (this.method_12781(entity)) {
				this.loadedEntities.add(entity);
				this.onEntitySpawned(entity);
			}
		}
	}

	private boolean method_12781(Entity entity) {
		if (entity.removed) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", new Object[]{EntityType.getEntityName(entity)});
			return false;
		} else {
			UUID uUID = entity.getUuid();
			if (this.entitiesByUuid.containsKey(uUID)) {
				Entity entity2 = (Entity)this.entitiesByUuid.get(uUID);
				if (this.unloadedEntities.contains(entity2)) {
					this.unloadedEntities.remove(entity2);
				} else {
					if (!(entity instanceof PlayerEntity)) {
						LOGGER.warn("Keeping entity {} that already exists with UUID {}", new Object[]{EntityType.getEntityName(entity2), uUID.toString()});
						return false;
					}

					LOGGER.warn("Force-added player with duplicate UUID {}", new Object[]{uUID.toString()});
				}

				this.method_3700(entity2);
			}

			return true;
		}
	}

	@Override
	protected void onEntitySpawned(Entity entity) {
		super.onEntitySpawned(entity);
		this.idToEntity.set(entity.getEntityId(), entity);
		this.entitiesByUuid.put(entity.getUuid(), entity);
		Entity[] entitys = entity.getParts();
		if (entitys != null) {
			for (Entity entity2 : entitys) {
				this.idToEntity.set(entity2.getEntityId(), entity2);
			}
		}
	}

	@Override
	protected void onEntityRemoved(Entity entity) {
		super.onEntityRemoved(entity);
		this.idToEntity.remove(entity.getEntityId());
		this.entitiesByUuid.remove(entity.getUuid());
		Entity[] entitys = entity.getParts();
		if (entitys != null) {
			for (Entity entity2 : entitys) {
				this.idToEntity.remove(entity2.getEntityId());
			}
		}
	}

	@Override
	public boolean addEntity(Entity entity) {
		if (super.addEntity(entity)) {
			this.server
				.getPlayerManager()
				.method_12828(null, entity.x, entity.y, entity.z, 512.0, this.dimension.getDimensionType().getId(), new EntitySpawnGlobalS2CPacket(entity));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void sendEntityStatus(Entity entity, byte status) {
		this.getEntityTracker().sendToAllTrackingEntities(entity, new EntityStatusS2CPacket(entity, status));
	}

	public ServerChunkProvider getChunkProvider() {
		return (ServerChunkProvider)super.getChunkProvider();
	}

	@Override
	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, boolean destructive) {
		Explosion explosion = new Explosion(this, entity, x, y, z, power, createFire, destructive);
		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (!destructive) {
			explosion.clearAffectedBlocks();
		}

		for (PlayerEntity playerEntity : this.playerEntities) {
			if (playerEntity.squaredDistanceTo(x, y, z) < 4096.0) {
				((ServerPlayerEntity)playerEntity)
					.networkHandler
					.sendPacket(new ExplosionS2CPacket(x, y, z, power, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(playerEntity)));
			}
		}

		return explosion;
	}

	@Override
	public void addBlockAction(BlockPos pos, Block block, int type, int data) {
		BlockAction blockAction = new BlockAction(pos, block, type, data);

		for (BlockAction blockAction2 : this.field_2815[this.field_2816]) {
			if (blockAction2.equals(blockAction)) {
				return;
			}
		}

		this.field_2815[this.field_2816].add(blockAction);
	}

	private void method_2131() {
		while (!this.field_2815[this.field_2816].isEmpty()) {
			int i = this.field_2816;
			this.field_2816 ^= 1;

			for (BlockAction blockAction : this.field_2815[i]) {
				if (this.method_2137(blockAction)) {
					this.server
						.getPlayerManager()
						.method_12828(
							null,
							(double)blockAction.getPos().getX(),
							(double)blockAction.getPos().getY(),
							(double)blockAction.getPos().getZ(),
							64.0,
							this.dimension.getDimensionType().getId(),
							new BlockActionS2CPacket(blockAction.getPos(), blockAction.getBlock(), blockAction.getType(), blockAction.getData())
						);
				}
			}

			this.field_2815[i].clear();
		}
	}

	private boolean method_2137(BlockAction blockAction) {
		BlockState blockState = this.getBlockState(blockAction.getPos());
		return blockState.getBlock() == blockAction.getBlock()
			? blockState.onSyncedBlockEvent(this, blockAction.getPos(), blockAction.getType(), blockAction.getData())
			: false;
	}

	public void close() {
		this.saveHandler.clear();
	}

	@Override
	protected void tickWeather() {
		boolean bl = this.isRaining();
		super.tickWeather();
		if (this.rainGradientPrev != this.rainGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.dimension.getDimensionType().getId());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.dimension.getDimensionType().getId());
		}

		if (bl != this.isRaining()) {
			if (bl) {
				this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(2, 0.0F));
			} else {
				this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(1, 0.0F));
			}

			this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(7, this.rainGradient));
			this.server.getPlayerManager().sendToAll(new GameStateChangeS2CPacket(8, this.thunderGradient));
		}
	}

	@Nullable
	@Override
	public MinecraftServer getServer() {
		return this.server;
	}

	public EntityTracker getEntityTracker() {
		return this.entityTracker;
	}

	public PlayerWorldManager getPlayerWorldManager() {
		return this.playerWorldManager;
	}

	public PortalTeleporter getPortalTeleporter() {
		return this.portalTeleporter;
	}

	public class_2763 method_12783() {
		return this.saveHandler.method_11956();
	}

	public void addParticle(ParticleType type, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed, int... args) {
		this.addParticle(type, false, x, y, z, count, offsetX, offsetY, offsetZ, speed, args);
	}

	public void addParticle(
		ParticleType type, boolean longDistance, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed, int... args
	) {
		ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(
			type, longDistance, (float)x, (float)y, (float)z, (float)offsetX, (float)offsetY, (float)offsetZ, (float)speed, count, args
		);

		for (int i = 0; i < this.playerEntities.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.playerEntities.get(i);
			this.method_12779(serverPlayerEntity, longDistance, x, y, z, particleS2CPacket);
		}
	}

	public void method_12778(
		ServerPlayerEntity serverPlayerEntity,
		ParticleType particleType,
		boolean bl,
		double d,
		double e,
		double f,
		int i,
		double g,
		double h,
		double j,
		double k,
		int... is
	) {
		Packet<?> packet = new ParticleS2CPacket(particleType, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i, is);
		this.method_12779(serverPlayerEntity, bl, d, e, f, packet);
	}

	private void method_12779(ServerPlayerEntity serverPlayerEntity, boolean bl, double d, double e, double f, Packet<?> packet) {
		BlockPos blockPos = serverPlayerEntity.getBlockPos();
		double g = blockPos.squaredDistanceTo(d, e, f);
		if (g <= 1024.0 || bl && g <= 262144.0) {
			serverPlayerEntity.networkHandler.sendPacket(packet);
		}
	}

	@Nullable
	public Entity getEntity(UUID uuid) {
		return (Entity)this.entitiesByUuid.get(uuid);
	}

	@Override
	public ListenableFuture<Object> submit(Runnable task) {
		return this.server.submit(task);
	}

	@Override
	public boolean isOnThread() {
		return this.server.isOnThread();
	}

	static class BlockActionList extends ArrayList<BlockAction> {
		private BlockActionList() {
		}
	}
}
