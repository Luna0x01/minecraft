package net.minecraft.server.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.class_2772;
import net.minecraft.class_3603;
import net.minecraft.class_3798;
import net.minecraft.class_3804;
import net.minecraft.class_3815;
import net.minecraft.class_3845;
import net.minecraft.class_3998;
import net.minecraft.class_4070;
import net.minecraft.class_4488;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningBoltEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobSpawnerHelper;
import net.minecraft.entity.PortalTeleporter;
import net.minecraft.entity.SkeletonHorseEntity;
import net.minecraft.entity.Tradable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockActionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerWorldManager;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.ThreadExecutor;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillageState;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.MultiServerWorld;
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
	private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
	public boolean savingDisabled;
	private boolean ready;
	private int idleTimeout;
	private final PortalTeleporter portalTeleporter;
	private final MobSpawnerHelper field_6728 = new MobSpawnerHelper();
	private final class_3603<Block> field_21842 = new class_3603<>(
		this, block -> block == null || block.getDefaultState().isAir(), Registry.BLOCK::getId, Registry.BLOCK::get, this::method_21264
	);
	private final class_3603<Fluid> field_21843 = new class_3603<>(
		this, fluid -> fluid == null || fluid == Fluids.EMPTY, Registry.FLUID::getId, Registry.FLUID::get, this::method_21258
	);
	protected final ZombieSiegeManager field_11761 = new ZombieSiegeManager(this);
	ObjectLinkedOpenHashSet<BlockAction> field_21845 = new ObjectLinkedOpenHashSet();
	private boolean field_21844;

	public ServerWorld(
		MinecraftServer minecraftServer, SaveHandler saveHandler, class_4070 arg, LevelProperties levelProperties, DimensionType dimensionType, Profiler profiler
	) {
		super(saveHandler, arg, levelProperties, dimensionType.method_17203(), profiler, false);
		this.server = minecraftServer;
		this.entityTracker = new EntityTracker(this);
		this.playerWorldManager = new PlayerWorldManager(this);
		this.dimension.copyFromWorld(this);
		this.chunkProvider = this.getChunkCache();
		this.portalTeleporter = new PortalTeleporter(this);
		this.calculateAmbientDarkness();
		this.initWeatherGradients();
		this.method_8524().setMaxWorldBorderRadius(minecraftServer.getMaxWorldBorderRadius());
	}

	public ServerWorld method_21265() {
		String string = VillageState.getId(this.dimension);
		VillageState villageState = this.method_16398(DimensionType.OVERWORLD, VillageState::new, string);
		if (villageState == null) {
			this.villageState = new VillageState(this);
			this.method_16397(DimensionType.OVERWORLD, string, this.villageState);
		} else {
			this.villageState = villageState;
			this.villageState.setWorld(this);
		}

		ScoreboardState scoreboardState = this.method_16398(DimensionType.OVERWORLD, ScoreboardState::new, "scoreboard");
		if (scoreboardState == null) {
			scoreboardState = new ScoreboardState();
			this.method_16397(DimensionType.OVERWORLD, "scoreboard", scoreboardState);
		}

		scoreboardState.setScoreboard(this.server.method_20333());
		this.server.method_20333().method_12759(new class_2772(scoreboardState));
		this.method_8524().setCenter(this.levelProperties.getBorderCenterX(), this.levelProperties.getBorderCenterZ());
		this.method_8524().setDamagePerBlock(this.levelProperties.getBorderDamagePerBlock());
		this.method_8524().setSafeZone(this.levelProperties.getSafeZone());
		this.method_8524().setWarningBlocks(this.levelProperties.getBorderWarningBlocks());
		this.method_8524().setWarningTime(this.levelProperties.getBorderWarningTime());
		if (this.levelProperties.getBorderSizeLerpTime() > 0L) {
			this.method_8524()
				.interpolateSize(this.levelProperties.getBorderSize(), this.levelProperties.getBorderSizeLerpTarget(), this.levelProperties.getBorderSizeLerpTime());
		} else {
			this.method_8524().setSize(this.levelProperties.getBorderSize());
		}

		return this;
	}

	@Override
	public void method_16327(BooleanSupplier booleanSupplier) {
		this.field_21844 = true;
		super.method_16327(booleanSupplier);
		if (this.method_3588().isHardcore() && this.method_16346() != Difficulty.HARD) {
			this.method_3588().setDifficulty(Difficulty.HARD);
		}

		this.chunkProvider.method_17046().method_17020().tick();
		if (this.isReady()) {
			if (this.getGameRules().getBoolean("doDaylightCycle")) {
				long l = this.levelProperties.getTimeOfDay() + 24000L;
				this.levelProperties.setDayTime(l - l % 24000L);
			}

			this.awakenPlayers();
		}

		this.profiler.push("spawner");
		if (this.getGameRules().getBoolean("doMobSpawning") && this.levelProperties.getGeneratorType() != LevelGeneratorType.DEBUG) {
			this.field_6728.tickSpawners(this, this.spawnAnimals, this.spawnMonsters, this.levelProperties.getTime() % 400L == 0L);
			this.method_3586().method_21251(this, this.spawnAnimals, this.spawnMonsters);
		}

		this.profiler.swap("chunkSource");
		this.chunkProvider.method_17045(booleanSupplier);
		int i = this.method_3597(1.0F);
		if (i != this.method_8520()) {
			this.setAmbientDarkness(i);
		}

		this.levelProperties.setTime(this.levelProperties.getTime() + 1L);
		if (this.getGameRules().getBoolean("doDaylightCycle")) {
			this.levelProperties.setDayTime(this.levelProperties.getTimeOfDay() + 1L);
		}

		this.profiler.swap("tickPending");
		this.method_21269();
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
		this.field_21844 = false;
	}

	public boolean method_21266() {
		return this.field_21844;
	}

	@Nullable
	public Biome.SpawnEntry method_10754(EntityCategory entityCategory, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = this.method_3586().method_12775(entityCategory, blockPos);
		return list.isEmpty() ? null : Weighting.getRandom(this.random, list);
	}

	public boolean method_10753(EntityCategory entityCategory, Biome.SpawnEntry spawnEntry, BlockPos blockPos) {
		List<Biome.SpawnEntry> list = this.method_3586().method_12775(entityCategory, blockPos);
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

	public ServerScoreboard getScoreboard() {
		return this.server.method_20333();
	}

	protected void awakenPlayers() {
		this.ready = false;

		for (PlayerEntity playerEntity : (List)this.playerEntities.stream().filter(PlayerEntity::isSleeping).collect(Collectors.toList())) {
			playerEntity.awaken(false, false, true);
		}

		if (this.getGameRules().getBoolean("doWeatherCycle")) {
			this.resetWeather();
		}
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
			this.levelProperties.setSpawnY(this.method_8483() + 1);
		}

		int i = this.levelProperties.getSpawnX();
		int j = this.levelProperties.getSpawnZ();
		int k = 0;

		while (this.method_8540(new BlockPos(i, 0, j)).isAir()) {
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
	public boolean method_8487(int i, int j, boolean bl) {
		return this.method_21256(i, j);
	}

	public boolean method_21256(int i, int j) {
		return this.method_3586().method_3864(i, j);
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
						LocalDifficulty localDifficulty = this.method_8482(blockPos);
						boolean bl3 = this.getGameRules().getBoolean("doMobSpawning") && this.random.nextDouble() < (double)localDifficulty.getLocalDifficulty() * 0.01;
						if (bl3) {
							SkeletonHorseEntity skeletonHorseEntity = new SkeletonHorseEntity(this);
							skeletonHorseEntity.method_14041(true);
							skeletonHorseEntity.setAge(0);
							skeletonHorseEntity.updatePosition((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
							this.method_3686(skeletonHorseEntity);
						}

						this.addEntity(new LightningBoltEntity(this, (double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, bl3));
					}
				}

				this.profiler.swap("iceandsnow");
				if (this.random.nextInt(16) == 0) {
					this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
					int m = this.lcgBlockSeed >> 2;
					BlockPos blockPos2 = this.method_16373(class_3804.class_3805.MOTION_BLOCKING, new BlockPos(j + (m & 15), 0, k + (m >> 8 & 15)));
					BlockPos blockPos3 = blockPos2.down();
					Biome biome = this.method_8577(blockPos2);
					if (biome.method_16426(this, blockPos3)) {
						this.setBlockState(blockPos3, Blocks.ICE.getDefaultState());
					}

					if (bl && biome.method_16439(this, blockPos2)) {
						this.setBlockState(blockPos2, Blocks.SNOW.getDefaultState());
					}

					if (bl && this.method_8577(blockPos3).getPrecipitation() == Biome.Precipitation.RAIN) {
						this.getBlockState(blockPos3).getBlock().onRainTick(this, blockPos3);
					}
				}

				this.profiler.swap("tickBlocks");
				if (i > 0) {
					for (ChunkSection chunkSection : chunk.method_17003()) {
						if (chunkSection != Chunk.EMPTY && chunkSection.method_17092()) {
							for (int n = 0; n < i; n++) {
								this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
								int o = this.lcgBlockSeed >> 2;
								int p = o & 15;
								int q = o >> 8 & 15;
								int r = o >> 16 & 15;
								BlockState blockState = chunkSection.getBlockState(p, r, q);
								FluidState fluidState = chunkSection.method_17093(p, r, q);
								this.profiler.push("randomTick");
								if (blockState.hasRandomTicks()) {
									blockState.method_16887(this, new BlockPos(p + j, r + chunkSection.getYOffset(), q + k), this.random);
								}

								if (fluidState.method_17812()) {
									fluidState.method_17806(this, new BlockPos(p + j, r + chunkSection.getYOffset(), q + k), this.random);
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
		BlockPos blockPos2 = this.method_16373(class_3804.class_3805.MOTION_BLOCKING, blockPos);
		Box box = new Box(blockPos2, new BlockPos(blockPos2.getX(), this.getMaxBuildHeight(), blockPos2.getZ())).expand(3.0);
		List<LivingEntity> list = this.method_16325(
			LivingEntity.class, box, livingEntity -> livingEntity != null && livingEntity.isAlive() && this.method_8555(livingEntity.method_4086())
		);
		if (!list.isEmpty()) {
			return ((LivingEntity)list.get(this.random.nextInt(list.size()))).method_4086();
		} else {
			if (blockPos2.getY() == -1) {
				blockPos2 = blockPos2.up(2);
			}

			return blockPos2;
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
				if (entity.updateNeeded && this.method_8487(j, k, true)) {
					this.method_16347(j, k).removeEntity(entity);
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

	public void method_21269() {
		if (this.levelProperties.getGeneratorType() != LevelGeneratorType.DEBUG) {
			this.field_21842.method_16409();
			this.field_21843.method_16409();
		}
	}

	private void method_21258(ScheduledTick<Fluid> scheduledTick) {
		FluidState fluidState = this.getFluidState(scheduledTick.pos);
		if (fluidState.getFluid() == scheduledTick.method_16421()) {
			fluidState.method_17801(this, scheduledTick.pos);
		}
	}

	private void method_21264(ScheduledTick<Block> scheduledTick) {
		BlockState blockState = this.getBlockState(scheduledTick.pos);
		if (blockState.getBlock() == scheduledTick.method_16421()) {
			blockState.scheduledTick(this, scheduledTick.pos, this.random);
		}
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
		return new ServerChunkProvider(this, chunkStorage, this.dimension.method_17193(), this.server);
	}

	@Override
	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return !this.server.isSpawnProtected(this, pos, player) && this.method_8524().contains(pos);
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
		this.getGameRules().method_16297("doDaylightCycle", "false", this.server);
	}

	private void init(LevelInfo levelInfo) {
		if (!this.dimension.containsWorldSpawn()) {
			this.levelProperties.setSpawnPos(BlockPos.ORIGIN.up(this.chunkProvider.method_17046().method_17025()));
		} else if (this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			this.levelProperties.setSpawnPos(BlockPos.ORIGIN.up());
		} else {
			SingletonBiomeSource singletonBiomeSource = this.chunkProvider.method_17046().method_17020();
			List<Biome> list = singletonBiomeSource.method_11532();
			Random random = new Random(this.method_3581());
			BlockPos blockPos = singletonBiomeSource.method_16478(0, 0, 256, list, random);
			ChunkPos chunkPos = blockPos == null ? new ChunkPos(0, 0) : new ChunkPos(blockPos);
			if (blockPos == null) {
				LOGGER.warn("Unable to find spawn biome");
			}

			boolean bl = false;

			for (Block block : BlockTags.VALID_SPAWN.values()) {
				if (singletonBiomeSource.method_16481().contains(block.getDefaultState())) {
					bl = true;
					break;
				}
			}

			this.levelProperties.setSpawnPos(chunkPos.method_16284().add(8, this.chunkProvider.method_17046().method_17025(), 8));
			int i = 0;
			int j = 0;
			int k = 0;
			int l = -1;
			int m = 32;

			for (int n = 0; n < 1024; n++) {
				if (i > -16 && i <= 16 && j > -16 && j <= 16) {
					BlockPos blockPos2 = this.dimension.method_17191(new ChunkPos(chunkPos.x + i, chunkPos.z + j), bl);
					if (blockPos2 != null) {
						this.levelProperties.setSpawnPos(blockPos2);
						break;
					}
				}

				if (i == j || i < 0 && i == -j || i > 0 && i == 1 - j) {
					int o = k;
					k = -l;
					l = o;
				}

				i += k;
				j += l;
			}

			if (levelInfo.hasBonusChest()) {
				this.placeBonusChest();
			}
		}
	}

	protected void placeBonusChest() {
		class_3815 lv = new class_3815();

		for (int i = 0; i < 10; i++) {
			int j = this.levelProperties.getSpawnX() + this.random.nextInt(6) - this.random.nextInt(6);
			int k = this.levelProperties.getSpawnZ() + this.random.nextInt(6) - this.random.nextInt(6);
			BlockPos blockPos = this.method_16373(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES, new BlockPos(j, 0, k)).up();
			if (lv.method_17343(this, (ChunkGenerator<? extends class_3798>)this.chunkProvider.method_17046(), this.random, blockPos, class_3845.field_19203)) {
				break;
			}
		}
	}

	@Nullable
	public BlockPos getForcedSpawnPoint() {
		return this.dimension.getForcedSpawnPoint();
	}

	public void save(boolean bl, @Nullable ProgressListener listener) throws WorldSaveException {
		ServerChunkProvider serverChunkProvider = this.method_3586();
		if (serverChunkProvider.canSaveChunks()) {
			if (listener != null) {
				listener.method_21524(new TranslatableText("menu.savingLevel"));
			}

			this.method_2132();
			if (listener != null) {
				listener.method_21526(new TranslatableText("menu.savingChunks"));
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
		ServerChunkProvider serverChunkProvider = this.method_3586();
		if (serverChunkProvider.canSaveChunks()) {
			serverChunkProvider.flushChunks();
		}
	}

	protected void method_2132() throws WorldSaveException {
		this.readSaveLock();

		for (ServerWorld serverWorld : this.server.method_20351()) {
			if (serverWorld instanceof MultiServerWorld) {
				((MultiServerWorld)serverWorld).method_12763();
			}
		}

		this.levelProperties.setBorderSize(this.method_8524().getOldSize());
		this.levelProperties.setBorderCenterX(this.method_8524().getCenterX());
		this.levelProperties.setBorderCenterZ(this.method_8524().getCenterZ());
		this.levelProperties.setSafeZone(this.method_8524().getSafeZone());
		this.levelProperties.setBorderDamagePerBlock(this.method_8524().getBorderDamagePerBlock());
		this.levelProperties.setBorderWarningBlocks(this.method_8524().getWarningBlocks());
		this.levelProperties.setBorderWarningTime(this.method_8524().getWarningTime());
		this.levelProperties.setBorderSizeLerpTarget(this.method_8524().getTargetSize());
		this.levelProperties.setBorderSizeLerpTime(this.method_8524().getInterpolationDuration());
		this.levelProperties.method_17965(this.server.method_20336().method_20485());
		this.saveHandler.saveWorld(this.levelProperties, this.server.getPlayerManager().getUserData());
		this.method_16399().method_17975();
	}

	@Override
	public boolean method_3686(Entity entity) {
		return this.method_12781(entity) ? super.method_3686(entity) : false;
	}

	@Override
	public void method_16328(Stream<Entity> stream) {
		stream.forEach(entity -> {
			if (this.method_12781(entity)) {
				this.loadedEntities.add(entity);
				this.onEntitySpawned(entity);
			}
		});
	}

	private boolean method_12781(Entity entity) {
		if (entity.removed) {
			LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getId(entity.method_15557()));
			return false;
		} else {
			UUID uUID = entity.getUuid();
			if (this.entitiesByUuid.containsKey(uUID)) {
				Entity entity2 = (Entity)this.entitiesByUuid.get(uUID);
				if (this.unloadedEntities.contains(entity2)) {
					this.unloadedEntities.remove(entity2);
				} else {
					if (!(entity instanceof PlayerEntity)) {
						LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getId(entity2.method_15557()), uUID.toString());
						return false;
					}

					LOGGER.warn("Force-added player with duplicate UUID {}", uUID.toString());
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
				.method_12828(null, entity.x, entity.y, entity.z, 512.0, this.dimension.method_11789(), new EntitySpawnGlobalS2CPacket(entity));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void sendEntityStatus(Entity entity, byte status) {
		this.getEntityTracker().sendToAllTrackingEntities(entity, new EntityStatusS2CPacket(entity, status));
	}

	public ServerChunkProvider method_3586() {
		return (ServerChunkProvider)super.method_3586();
	}

	@Override
	public Explosion method_16320(@Nullable Entity entity, DamageSource damageSource, double d, double e, double f, float g, boolean bl, boolean bl2) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, bl2);
		if (damageSource != null) {
			explosion.method_16294(damageSource);
		}

		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(false);
		if (!bl2) {
			explosion.clearAffectedBlocks();
		}

		for (PlayerEntity playerEntity : this.playerEntities) {
			if (playerEntity.squaredDistanceTo(d, e, f) < 4096.0) {
				((ServerPlayerEntity)playerEntity)
					.networkHandler
					.sendPacket(new ExplosionS2CPacket(d, e, f, g, explosion.getAffectedBlocks(), (Vec3d)explosion.getAffectedPlayers().get(playerEntity)));
			}
		}

		return explosion;
	}

	@Override
	public void addBlockAction(BlockPos pos, Block block, int type, int data) {
		this.field_21845.add(new BlockAction(pos, block, type, data));
	}

	private void method_2131() {
		while (!this.field_21845.isEmpty()) {
			BlockAction blockAction = (BlockAction)this.field_21845.removeFirst();
			if (this.method_2137(blockAction)) {
				this.server
					.getPlayerManager()
					.method_12828(
						null,
						(double)blockAction.getPos().getX(),
						(double)blockAction.getPos().getY(),
						(double)blockAction.getPos().getZ(),
						64.0,
						this.dimension.method_11789(),
						new BlockActionS2CPacket(blockAction.getPos(), blockAction.getBlock(), blockAction.getType(), blockAction.getData())
					);
			}
		}
	}

	private boolean method_2137(BlockAction blockAction) {
		BlockState blockState = this.getBlockState(blockAction.getPos());
		return blockState.getBlock() == blockAction.getBlock()
			? blockState.method_16868(this, blockAction.getPos(), blockAction.getType(), blockAction.getData())
			: false;
	}

	@Override
	public void close() {
		this.saveHandler.clear();
		super.close();
	}

	@Override
	protected void tickWeather() {
		boolean bl = this.isRaining();
		super.tickWeather();
		if (this.rainGradientPrev != this.rainGradient) {
			this.server.getPlayerManager().method_21385(new GameStateChangeS2CPacket(7, this.rainGradient), this.dimension.method_11789());
		}

		if (this.thunderGradientPrev != this.thunderGradient) {
			this.server.getPlayerManager().method_21385(new GameStateChangeS2CPacket(8, this.thunderGradient), this.dimension.method_11789());
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

	public class_3603<Block> getBlockTickScheduler() {
		return this.field_21842;
	}

	public class_3603<Fluid> method_16340() {
		return this.field_21843;
	}

	@Nonnull
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

	public class_3998 method_12783() {
		return this.saveHandler.method_11956();
	}

	public <T extends ParticleEffect> int method_21261(T particleEffect, double d, double e, double f, int i, double g, double h, double j, double k) {
		ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particleEffect, false, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		int l = 0;

		for (int m = 0; m < this.playerEntities.size(); m++) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.playerEntities.get(m);
			if (this.method_21263(serverPlayerEntity, false, d, e, f, particleS2CPacket)) {
				l++;
			}
		}

		return l;
	}

	public <T extends ParticleEffect> boolean method_21262(
		ServerPlayerEntity serverPlayerEntity, T particleEffect, boolean bl, double d, double e, double f, int i, double g, double h, double j, double k
	) {
		Packet<?> packet = new ParticleS2CPacket(particleEffect, bl, (float)d, (float)e, (float)f, (float)g, (float)h, (float)j, (float)k, i);
		return this.method_21263(serverPlayerEntity, bl, d, e, f, packet);
	}

	private boolean method_21263(ServerPlayerEntity serverPlayerEntity, boolean bl, double d, double e, double f, Packet<?> packet) {
		if (serverPlayerEntity.getServerWorld() != this) {
			return false;
		} else {
			BlockPos blockPos = serverPlayerEntity.method_4086();
			double g = blockPos.squaredDistanceTo(d, e, f);
			if (!(g <= 1024.0) && (!bl || !(g <= 262144.0))) {
				return false;
			} else {
				serverPlayerEntity.networkHandler.sendPacket(packet);
				return true;
			}
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

	@Nullable
	@Override
	public BlockPos method_13688(String string, BlockPos blockPos, int i, boolean bl) {
		return this.method_3586().method_12773(this, string, blockPos, i, bl);
	}

	@Override
	public RecipeDispatcher method_16313() {
		return this.server.method_20331();
	}

	@Override
	public class_4488 method_16314() {
		return this.server.method_20332();
	}
}
