package net.minecraft.server.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.PortalTeleporter;
import net.minecraft.entity.Tradable;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
import net.minecraft.util.ProgressListener;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LayeredBiomeSource;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.SaveHandler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStorage;
import net.minecraft.world.chunk.ServerChunkProvider;
import net.minecraft.world.dimension.Dimension;
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
	public ServerChunkProvider chunkCache;
	public boolean savingDisabled;
	private boolean ready;
	private int idleTimeout;
	private final PortalTeleporter portalTeleporter;
	private final MobSpawnerHelper field_6728 = new MobSpawnerHelper();
	protected final ZombieSiegeManager field_11761 = new ZombieSiegeManager(this);
	private ServerWorld.BlockActionList[] field_2815 = new ServerWorld.BlockActionList[]{new ServerWorld.BlockActionList(), new ServerWorld.BlockActionList()};
	private int field_2816;
	private static final List<WeightedRandomChestContent> BONUS_CHEST_LOOT_TABLE = Lists.newArrayList(
		new WeightedRandomChestContent[]{
			new WeightedRandomChestContent(Items.STICK, 0, 1, 3, 10),
			new WeightedRandomChestContent(Item.fromBlock(Blocks.PLANKS), 0, 1, 3, 10),
			new WeightedRandomChestContent(Item.fromBlock(Blocks.LOG), 0, 1, 3, 10),
			new WeightedRandomChestContent(Items.STONE_AXE, 0, 1, 1, 3),
			new WeightedRandomChestContent(Items.WOODEN_AXE, 0, 1, 1, 5),
			new WeightedRandomChestContent(Items.STONE_PICKAXE, 0, 1, 1, 3),
			new WeightedRandomChestContent(Items.WOODEN_PICKAXE, 0, 1, 1, 5),
			new WeightedRandomChestContent(Items.APPLE, 0, 2, 3, 5),
			new WeightedRandomChestContent(Items.BREAD, 0, 2, 3, 3),
			new WeightedRandomChestContent(Item.fromBlock(Blocks.LOG2), 0, 1, 3, 10)
		}
	);
	private List<ScheduledTick> field_6729 = Lists.newArrayList();

	public ServerWorld(MinecraftServer minecraftServer, SaveHandler saveHandler, LevelProperties levelProperties, int i, Profiler profiler) {
		super(saveHandler, levelProperties, Dimension.getById(i), profiler, false);
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
		((ServerScoreboard)this.scoreboard).setScoreboardState(scoreboardState);
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

		this.dimension.getBiomeSource().method_3859();
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

	public Biome.SpawnEntry method_10754(EntityCategory entityCategory, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = this.getChunkProvider().getSpawnEntries(entityCategory, blockPos);
		return list != null && !list.isEmpty() ? Weighting.rand(this.random, list) : null;
	}

	public boolean method_10753(EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = this.getChunkProvider().getSpawnEntries(entityCategory, blockPos);
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
				if (playerEntity.isSpectator() || !playerEntity.isSleepingLongEnough()) {
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

		while (this.getBlockAt(new BlockPos(i, 0, j)).getMaterial() == Material.AIR) {
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
	protected void tickBlocks() {
		super.tickBlocks();
		if (this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			for (ChunkPos chunkPos : this.field_4530) {
				this.getChunk(chunkPos.x, chunkPos.z).populateBlockEntities(false);
			}
		} else {
			int i = 0;
			int j = 0;

			for (ChunkPos chunkPos2 : this.field_4530) {
				int k = chunkPos2.x * 16;
				int l = chunkPos2.z * 16;
				this.profiler.push("getChunk");
				Chunk chunk = this.getChunk(chunkPos2.x, chunkPos2.z);
				this.method_3605(k, l, chunk);
				this.profiler.swap("tickChunk");
				chunk.populateBlockEntities(false);
				this.profiler.swap("thunder");
				if (this.random.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
					this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
					int m = this.lcgBlockSeed >> 2;
					BlockPos blockPos = this.method_10749(new BlockPos(k + (m & 15), 0, l + (m >> 8 & 15)));
					if (this.hasRain(blockPos)) {
						this.addEntity(new LightningBoltEntity(this, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
					}
				}

				this.profiler.swap("iceandsnow");
				if (this.random.nextInt(16) == 0) {
					this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
					int n = this.lcgBlockSeed >> 2;
					BlockPos blockPos2 = this.method_8562(new BlockPos(k + (n & 15), 0, l + (n >> 8 & 15)));
					BlockPos blockPos3 = blockPos2.down();
					if (this.canWaterNotFreezeAt(blockPos3)) {
						this.setBlockState(blockPos3, Blocks.ICE.getDefaultState());
					}

					if (this.isRaining() && this.method_8552(blockPos2, true)) {
						this.setBlockState(blockPos2, Blocks.SNOW_LAYER.getDefaultState());
					}

					if (this.isRaining() && this.getBiome(blockPos3).method_3830()) {
						this.getBlockState(blockPos3).getBlock().onRainTick(this, blockPos3);
					}
				}

				this.profiler.swap("tickBlocks");
				int o = this.getGameRules().getInt("randomTickSpeed");
				if (o > 0) {
					for (ChunkSection chunkSection : chunk.getBlockStorage()) {
						if (chunkSection != null && chunkSection.hasTickableBlocks()) {
							for (int r = 0; r < o; r++) {
								this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
								int s = this.lcgBlockSeed >> 2;
								int t = s & 15;
								int u = s >> 8 & 15;
								int v = s >> 16 & 15;
								j++;
								BlockState blockState = chunkSection.getBlockState(t, v, u);
								Block block = blockState.getBlock();
								if (block.ticksRandomly()) {
									i++;
									block.onRandomTick(this, new BlockPos(t + k, v + chunkSection.getYOffset(), u + l), blockState, this.random);
								}
							}
						}
					}
				}

				this.profiler.pop();
			}
		}
	}

	protected BlockPos method_10749(BlockPos blockPos) {
		BlockPos blockPos2 = this.method_8562(blockPos);
		Box box = new Box(blockPos2, new BlockPos(blockPos2.getX(), this.getMaxBuildHeight(), blockPos2.getZ())).expand(3.0, 3.0, 3.0);
		List<LivingEntity> list = this.getEntitiesInBox(LivingEntity.class, box, new Predicate<LivingEntity>() {
			public boolean apply(LivingEntity livingEntity) {
				return livingEntity != null && livingEntity.isAlive() && ServerWorld.this.hasDirectSunlight(livingEntity.getBlockPos());
			}
		});
		return !list.isEmpty() ? ((LivingEntity)list.get(this.random.nextInt(list.size()))).getBlockPos() : blockPos2;
	}

	@Override
	public boolean hasScheduledTick(BlockPos pos, Block block) {
		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		return this.field_6729.contains(scheduledTick);
	}

	@Override
	public void createAndScheduleBlockTick(BlockPos pos, Block block, int tickRate) {
		this.createAndScheduleBlockTick(pos, block, tickRate, 0);
	}

	@Override
	public void createAndScheduleBlockTick(BlockPos pos, Block block, int tickRate, int priority) {
		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		int i = 0;
		if (this.immediateUpdates && block.getMaterial() != Material.AIR) {
			if (block.doImmediateUpdates()) {
				int var8 = 8;
				if (this.isRegionLoaded(scheduledTick.pos.add(-var8, -var8, -var8), scheduledTick.pos.add(var8, var8, var8))) {
					BlockState blockState = this.getBlockState(scheduledTick.pos);
					if (blockState.getBlock().getMaterial() != Material.AIR && blockState.getBlock() == scheduledTick.getBlock()) {
						blockState.getBlock().onScheduledTick(this, scheduledTick.pos, blockState, this.random);
					}
				}

				return;
			}

			tickRate = 1;
		}

		if (this.isRegionLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
			if (block.getMaterial() != Material.AIR) {
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
		ScheduledTick scheduledTick = new ScheduledTick(pos, block);
		scheduledTick.setPriority(priority);
		if (block.getMaterial() != Material.AIR) {
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
			if (this.idleTimeout++ >= 1200) {
				return;
			}
		} else {
			this.resetIdleTimeout();
		}

		super.tickEntities();
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
				if (i > 1000) {
					i = 1000;
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
					if (this.isRegionLoaded(scheduledTick2.pos.add(-k, -k, -k), scheduledTick2.pos.add(k, k, k))) {
						BlockState blockState = this.getBlockState(scheduledTick2.pos);
						if (blockState.getBlock().getMaterial() != Material.AIR && Block.areBlocksEqual(blockState.getBlock(), scheduledTick2.getBlock())) {
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

	@Override
	public List<ScheduledTick> getScheduledTicks(Chunk chunk, boolean bl) {
		ChunkPos chunkPos = chunk.getChunkPos();
		int i = (chunkPos.x << 4) - 2;
		int j = i + 16 + 2;
		int k = (chunkPos.z << 4) - 2;
		int l = k + 16 + 2;
		return this.getScheduledTicks(new BlockBox(i, 0, k, j, 256, l), bl);
	}

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
						this.field_2811.remove(scheduledTick);
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
		this.chunkCache = new ServerChunkProvider(this, chunkStorage, this.dimension.createChunkGenerator());
		return this.chunkCache;
	}

	public List<BlockEntity> method_2134(int i, int j, int k, int l, int m, int n) {
		List<BlockEntity> list = Lists.newArrayList();

		for (int o = 0; o < this.blockEntities.size(); o++) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(o);
			BlockPos blockPos = blockEntity.getPos();
			if (blockPos.getX() >= i && blockPos.getY() >= j && blockPos.getZ() >= k && blockPos.getX() < l && blockPos.getY() < m && blockPos.getZ() < n) {
				list.add(blockEntity);
			}
		}

		return list;
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
		this.levelProperties.setGamemode(LevelInfo.GameMode.SPECTATOR);
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
			LayeredBiomeSource layeredBiomeSource = this.dimension.getBiomeSource();
			List<Biome> list = layeredBiomeSource.getBiomes();
			Random random = new Random(this.getSeed());
			BlockPos blockPos = layeredBiomeSource.method_3855(0, 0, 256, list, random);
			int i = 0;
			int j = this.dimension.getAverageYLevel();
			int k = 0;
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
		BonusChestFeature bonusChestFeature = new BonusChestFeature(BONUS_CHEST_LOOT_TABLE, 10);

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

	public void save(boolean bl, ProgressListener listener) throws WorldSaveException {
		if (this.chunkProvider.isSavingEnabled()) {
			if (listener != null) {
				listener.setTitle("Saving level");
			}

			this.method_2132();
			if (listener != null) {
				listener.setTask("Saving chunks");
			}

			this.chunkProvider.saveChunks(bl, listener);

			for (Chunk chunk : Lists.newArrayList(this.chunkCache.getChunks())) {
				if (chunk != null && !this.playerWorldManager.method_8116(chunk.chunkX, chunk.chunkZ)) {
					this.chunkCache.scheduleUnload(chunk.chunkX, chunk.chunkZ);
				}
			}
		}
	}

	public void method_5323() {
		if (this.chunkProvider.isSavingEnabled()) {
			this.chunkProvider.flushChunks();
		}
	}

	protected void method_2132() throws WorldSaveException {
		this.readSaveLock();
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
	protected void onEntitySpawned(Entity entity) {
		super.onEntitySpawned(entity);
		this.idToEntity.set(entity.getEntityId(), entity);
		this.entitiesByUuid.put(entity.getUuid(), entity);
		Entity[] entitys = entity.getParts();
		if (entitys != null) {
			for (int i = 0; i < entitys.length; i++) {
				this.idToEntity.set(entitys[i].getEntityId(), entitys[i]);
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
			for (int i = 0; i < entitys.length; i++) {
				this.idToEntity.remove(entitys[i].getEntityId());
			}
		}
	}

	@Override
	public boolean addEntity(Entity entity) {
		if (super.addEntity(entity)) {
			this.server.getPlayerManager().sendToAround(entity.x, entity.y, entity.z, 512.0, this.dimension.getType(), new EntitySpawnGlobalS2CPacket(entity));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void sendEntityStatus(Entity entity, byte status) {
		this.getEntityTracker().sendToAllTrackingEntities(entity, new EntityStatusS2CPacket(entity, status));
	}

	@Override
	public Explosion createExplosion(Entity entity, double x, double y, double z, float power, boolean createFire, boolean destructive) {
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
						.sendToAround(
							(double)blockAction.getPos().getX(),
							(double)blockAction.getPos().getY(),
							(double)blockAction.getPos().getZ(),
							64.0,
							this.dimension.getType(),
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
			? blockState.getBlock().onEvent(this, blockAction.getPos(), blockState, blockAction.getType(), blockAction.getData())
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
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(7, this.rainGradient), this.dimension.getType());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.getPlayerManager().sendToDimension(new GameStateChangeS2CPacket(8, this.thunderGradient), this.dimension.getType());
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

	@Override
	protected int getNextMapId() {
		return this.server.getPlayerManager().getViewDistance();
	}

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

	public void addParticle(ParticleType type, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed, int... args) {
		this.addParticle(type, false, x, y, z, count, offsetX, offsetY, offsetZ, speed, args);
	}

	public void addParticle(
		ParticleType type, boolean longDistance, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double speed, int... args
	) {
		Packet packet = new ParticleS2CPacket(
			type, longDistance, (float)x, (float)y, (float)z, (float)offsetX, (float)offsetY, (float)offsetZ, (float)speed, count, args
		);

		for (int i = 0; i < this.playerEntities.size(); i++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.playerEntities.get(i);
			BlockPos blockPos = serverPlayerEntity.getBlockPos();
			double d = blockPos.squaredDistanceTo(x, y, z);
			if (d <= 256.0 || longDistance && d <= 65536.0) {
				serverPlayerEntity.networkHandler.sendPacket(packet);
			}
		}
	}

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
