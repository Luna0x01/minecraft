package net.minecraft.world;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.achievement.class_3348;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ObserverBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.loot.class_2787;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.FunctionTickable;
import net.minecraft.sound.Sound;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;

public abstract class World implements BlockView {
	private int seaLevel = 63;
	protected boolean immediateUpdates;
	public final List<Entity> loadedEntities = Lists.newArrayList();
	protected final List<Entity> unloadedEntities = Lists.newArrayList();
	public final List<BlockEntity> blockEntities = Lists.newArrayList();
	public final List<BlockEntity> tickingBlockEntities = Lists.newArrayList();
	private final List<BlockEntity> pendingBlockEntities = Lists.newArrayList();
	private final List<BlockEntity> unloadedBlockEntities = Lists.newArrayList();
	public final List<PlayerEntity> playerEntities = Lists.newArrayList();
	public final List<Entity> entities = Lists.newArrayList();
	protected final IntObjectStorage<Entity> idToEntity = new IntObjectStorage<>();
	private final long cloudColor = 16777215L;
	private int ambientDarkness;
	protected int lcgBlockSeed = new Random().nextInt();
	protected final int unusedIncrement = 1013904223;
	protected float rainGradientPrev;
	protected float rainGradient;
	protected float thunderGradientPrev;
	protected float thunderGradient;
	private int field_4553;
	public final Random random = new Random();
	public final Dimension dimension;
	protected class_2975 field_12436 = new class_2975();
	protected List<WorldEventListener> eventListeners = Lists.newArrayList(new WorldEventListener[]{this.field_12436});
	protected ChunkProvider chunkProvider;
	protected final SaveHandler saveHandler;
	protected LevelProperties levelProperties;
	protected boolean field_4523;
	protected PersistentStateManager persistentStateManager;
	protected VillageState villageState;
	protected class_2787 field_12435;
	protected class_3348 field_15708;
	protected FunctionTickable field_15709;
	public final Profiler profiler;
	private final Calendar calender = Calendar.getInstance();
	protected Scoreboard scoreboard = new Scoreboard();
	public final boolean isClient;
	protected boolean spawnAnimals = true;
	protected boolean spawnMonsters = true;
	private boolean iteratingTickingBlockEntities;
	private final WorldBorder border;
	int[] updateLightBlocks = new int[32768];

	protected World(SaveHandler saveHandler, LevelProperties levelProperties, Dimension dimension, Profiler profiler, boolean bl) {
		this.saveHandler = saveHandler;
		this.profiler = profiler;
		this.levelProperties = levelProperties;
		this.dimension = dimension;
		this.isClient = bl;
		this.border = dimension.createWorldBorder();
	}

	public World getWorld() {
		return this;
	}

	@Override
	public Biome getBiome(BlockPos pos) {
		if (this.blockExists(pos)) {
			Chunk chunk = this.getChunk(pos);

			try {
				return chunk.method_11771(pos, this.dimension.method_9175());
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Getting biome");
				CrashReportSection crashReportSection = crashReport.addElement("Coordinates of biome request");
				crashReportSection.add("Location", new CrashCallable<String>() {
					public String call() throws Exception {
						return CrashReportSection.createPositionString(pos);
					}
				});
				throw new CrashException(crashReport);
			}
		} else {
			return this.dimension.method_9175().method_11536(pos, Biomes.PLAINS);
		}
	}

	public SingletonBiomeSource method_3726() {
		return this.dimension.method_9175();
	}

	protected abstract ChunkProvider getChunkCache();

	public void setPropertiesInitialized(LevelInfo info) {
		this.levelProperties.setInitialized(true);
	}

	@Nullable
	public MinecraftServer getServer() {
		return null;
	}

	public void setDefaultSpawnClient() {
		this.setSpawnPos(new BlockPos(8, 64, 8));
	}

	public BlockState method_8540(BlockPos pos) {
		BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());

		while (!this.isAir(blockPos.up())) {
			blockPos = blockPos.up();
		}

		return this.getBlockState(blockPos);
	}

	private boolean method_11479(BlockPos blockPos) {
		return !this.method_11475(blockPos)
			&& blockPos.getX() >= -30000000
			&& blockPos.getZ() >= -30000000
			&& blockPos.getX() < 30000000
			&& blockPos.getZ() < 30000000;
	}

	private boolean method_11475(BlockPos blockPos) {
		return blockPos.getY() < 0 || blockPos.getY() >= 256;
	}

	@Override
	public boolean isAir(BlockPos pos) {
		return this.getBlockState(pos).getMaterial() == Material.AIR;
	}

	public boolean blockExists(BlockPos pos) {
		return this.isLoaded(pos, true);
	}

	public boolean isLoaded(BlockPos pos, boolean canBeEmpty) {
		return this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, canBeEmpty);
	}

	public boolean isRegionLoaded(BlockPos pos, int distance) {
		return this.isRegionLoaded(pos, distance, true);
	}

	public boolean isRegionLoaded(BlockPos pos, int distance, boolean canBeEmpty) {
		return this.isRegionLoaded(
			pos.getX() - distance, pos.getY() - distance, pos.getZ() - distance, pos.getX() + distance, pos.getY() + distance, pos.getZ() + distance, canBeEmpty
		);
	}

	public boolean isRegionLoaded(BlockPos start, BlockPos end) {
		return this.isRegionLoaded(start, end, true);
	}

	public boolean isRegionLoaded(BlockPos start, BlockPos end, boolean canBeEmpty) {
		return this.isRegionLoaded(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ(), canBeEmpty);
	}

	public boolean isRegionLoaded(BlockBox box) {
		return this.isRegionLoaded(box, true);
	}

	public boolean isRegionLoaded(BlockBox box, boolean canBeEmpty) {
		return this.isRegionLoaded(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, canBeEmpty);
	}

	private boolean isRegionLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean canBeEmpty) {
		if (maxY >= 0 && minY < 256) {
			minX >>= 4;
			minZ >>= 4;
			maxX >>= 4;
			maxZ >>= 4;

			for (int i = minX; i <= maxX; i++) {
				for (int j = minZ; j <= maxZ; j++) {
					if (!this.isChunkLoaded(i, j, canBeEmpty)) {
						return false;
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	protected abstract boolean isChunkLoaded(int chunkX, int chunkZ, boolean canBeEmpty);

	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public Chunk getChunk(int chunkX, int chunkZ) {
		return this.chunkProvider.getOrGenerateChunks(chunkX, chunkZ);
	}

	public boolean method_13690(int i, int j) {
		return this.isChunkLoaded(i, j, false) ? true : this.chunkProvider.isChunkGenerated(i, j);
	}

	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		if (this.method_11475(pos)) {
			return false;
		} else if (!this.isClient && this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			return false;
		} else {
			Chunk chunk = this.getChunk(pos);
			Block block = state.getBlock();
			BlockState blockState = chunk.getBlockState(pos, state);
			if (blockState == null) {
				return false;
			} else {
				if (state.getOpacity() != blockState.getOpacity() || state.getLuminance() != blockState.getLuminance()) {
					this.profiler.push("checkLight");
					this.method_8568(pos);
					this.profiler.pop();
				}

				if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && chunk.isPopulated()) {
					this.method_11481(pos, blockState, state, flags);
				}

				if (!this.isClient && (flags & 1) != 0) {
					this.method_8531(pos, blockState.getBlock(), true);
					if (state.method_11736()) {
						this.updateHorizontalAdjacent(pos, block);
					}
				} else if (!this.isClient && (flags & 16) == 0) {
					this.method_13693(pos, block);
				}

				return true;
			}
		}
	}

	public boolean setAir(BlockPos pos) {
		return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
	}

	public boolean removeBlock(BlockPos pos, boolean dropAsItem) {
		BlockState blockState = this.getBlockState(pos);
		Block block = blockState.getBlock();
		if (blockState.getMaterial() == Material.AIR) {
			return false;
		} else {
			this.syncGlobalEvent(2001, pos, Block.getByBlockState(blockState));
			if (dropAsItem) {
				block.dropAsItem(this, pos, blockState, 0);
			}

			return this.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean setBlockState(BlockPos pos, BlockState state) {
		return this.setBlockState(pos, state, 3);
	}

	public void method_11481(BlockPos position, BlockState blockState, BlockState blockState2, int i) {
		for (int j = 0; j < this.eventListeners.size(); j++) {
			((WorldEventListener)this.eventListeners.get(j)).method_11493(this, position, blockState, blockState2, i);
		}
	}

	public void method_8531(BlockPos pos, Block block, boolean bl) {
		if (this.levelProperties.getGeneratorType() != LevelGeneratorType.DEBUG) {
			this.method_13692(pos, block, bl);
		}
	}

	public void method_3704(int x, int z, int minY, int maxY) {
		if (minY > maxY) {
			int i = maxY;
			maxY = minY;
			minY = i;
		}

		if (this.dimension.isOverworld()) {
			for (int j = minY; j <= maxY; j++) {
				this.calculateLightAtPos(LightType.SKY, new BlockPos(x, j, z));
			}
		}

		this.onRenderRegionUpdate(x, minY, z, x, maxY, z);
	}

	public void onRenderRegionUpdate(BlockPos pos1, BlockPos pos2) {
		this.onRenderRegionUpdate(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
	}

	public void onRenderRegionUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).onRenderRegionUpdate(x1, y1, z1, x2, y2, z2);
		}
	}

	public void method_13693(BlockPos pos, Block block) {
		this.method_13691(pos.west(), block, pos);
		this.method_13691(pos.east(), block, pos);
		this.method_13691(pos.down(), block, pos);
		this.method_13691(pos.up(), block, pos);
		this.method_13691(pos.north(), block, pos);
		this.method_13691(pos.south(), block, pos);
	}

	public void method_13692(BlockPos pos, Block block, boolean bl) {
		this.updateNeighbor(pos.west(), block, pos);
		this.updateNeighbor(pos.east(), block, pos);
		this.updateNeighbor(pos.down(), block, pos);
		this.updateNeighbor(pos.up(), block, pos);
		this.updateNeighbor(pos.north(), block, pos);
		this.updateNeighbor(pos.south(), block, pos);
		if (bl) {
			this.method_13693(pos, block);
		}
	}

	public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction dir) {
		if (dir != Direction.WEST) {
			this.updateNeighbor(pos.west(), sourceBlock, pos);
		}

		if (dir != Direction.EAST) {
			this.updateNeighbor(pos.east(), sourceBlock, pos);
		}

		if (dir != Direction.DOWN) {
			this.updateNeighbor(pos.down(), sourceBlock, pos);
		}

		if (dir != Direction.UP) {
			this.updateNeighbor(pos.up(), sourceBlock, pos);
		}

		if (dir != Direction.NORTH) {
			this.updateNeighbor(pos.north(), sourceBlock, pos);
		}

		if (dir != Direction.SOUTH) {
			this.updateNeighbor(pos.south(), sourceBlock, pos);
		}
	}

	public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
		if (!this.isClient) {
			BlockState blockState = this.getBlockState(pos);

			try {
				blockState.neighbourUpdate(this, pos, sourceBlock, sourcePos);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", new CrashCallable<String>() {
					public String call() throws Exception {
						try {
							return String.format("ID #%d (%s // %s)", Block.getIdByBlock(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
						} catch (Throwable var2) {
							return "ID #" + Block.getIdByBlock(sourceBlock);
						}
					}
				});
				CrashReportSection.addBlockInfo(crashReportSection, pos, blockState);
				throw new CrashException(crashReport);
			}
		}
	}

	public void method_13691(BlockPos pos, Block block, BlockPos sourcePos) {
		if (!this.isClient) {
			BlockState blockState = this.getBlockState(pos);
			if (blockState.getBlock() == Blocks.OBSERVER) {
				try {
					((ObserverBlock)blockState.getBlock()).method_13711(blockState, this, pos, block, sourcePos);
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
					CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
					crashReportSection.add("Source block type", new CrashCallable<String>() {
						public String call() throws Exception {
							try {
								return String.format("ID #%d (%s // %s)", Block.getIdByBlock(block), block.getTranslationKey(), block.getClass().getCanonicalName());
							} catch (Throwable var2) {
								return "ID #" + Block.getIdByBlock(block);
							}
						}
					});
					CrashReportSection.addBlockInfo(crashReportSection, pos, blockState);
					throw new CrashException(crashReport);
				}
			}
		}
	}

	public boolean hasScheduledTick(BlockPos pos, Block block) {
		return false;
	}

	public boolean hasDirectSunlight(BlockPos pos) {
		return this.getChunk(pos).hasDirectSunlight(pos);
	}

	public boolean receivesSunlight(BlockPos pos) {
		if (pos.getY() >= this.getSeaLevel()) {
			return this.hasDirectSunlight(pos);
		} else {
			BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
			if (!this.hasDirectSunlight(blockPos)) {
				return false;
			} else {
				for (BlockPos var4 = blockPos.down(); var4.getY() > pos.getY(); var4 = var4.down()) {
					BlockState blockState = this.getBlockState(var4);
					if (blockState.getOpacity() > 0 && !blockState.getMaterial().isFluid()) {
						return false;
					}
				}

				return true;
			}
		}
	}

	public int getLightLevel(BlockPos pos) {
		if (pos.getY() < 0) {
			return 0;
		} else {
			if (pos.getY() >= 256) {
				pos = new BlockPos(pos.getX(), 255, pos.getZ());
			}

			return this.getChunk(pos).getLightLevel(pos, 0);
		}
	}

	public int getLightLevelWithNeighbours(BlockPos pos) {
		return this.getLightLevel(pos, true);
	}

	public int getLightLevel(BlockPos pos, boolean checkNeighbours) {
		if (pos.getX() < -30000000 || pos.getZ() < -30000000 || pos.getX() >= 30000000 || pos.getZ() >= 30000000) {
			return 15;
		} else if (checkNeighbours && this.getBlockState(pos).useNeighbourLight()) {
			int i = this.getLightLevel(pos.up(), false);
			int j = this.getLightLevel(pos.east(), false);
			int k = this.getLightLevel(pos.west(), false);
			int l = this.getLightLevel(pos.south(), false);
			int m = this.getLightLevel(pos.north(), false);
			if (j > i) {
				i = j;
			}

			if (k > i) {
				i = k;
			}

			if (l > i) {
				i = l;
			}

			if (m > i) {
				i = m;
			}

			return i;
		} else if (pos.getY() < 0) {
			return 0;
		} else {
			if (pos.getY() >= 256) {
				pos = new BlockPos(pos.getX(), 255, pos.getZ());
			}

			Chunk chunk = this.getChunk(pos);
			return chunk.getLightLevel(pos, this.ambientDarkness);
		}
	}

	public BlockPos getHighestBlock(BlockPos pos) {
		return new BlockPos(pos.getX(), this.getHighestBlockY(pos.getX(), pos.getZ()), pos.getZ());
	}

	public int getHighestBlockY(int x, int z) {
		int j;
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
			if (this.isChunkLoaded(x >> 4, z >> 4, true)) {
				j = this.getChunk(x >> 4, z >> 4).getHighestBlockY(x & 15, z & 15);
			} else {
				j = 0;
			}
		} else {
			j = this.getSeaLevel() + 1;
		}

		return j;
	}

	@Deprecated
	public int getMinimumChunkHeightmap(int x, int z) {
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
			if (!this.isChunkLoaded(x >> 4, z >> 4, true)) {
				return 0;
			} else {
				Chunk chunk = this.getChunk(x >> 4, z >> 4);
				return chunk.getMinimumHeightMap();
			}
		} else {
			return this.getSeaLevel() + 1;
		}
	}

	public int getLuminance(LightType lightType, BlockPos pos) {
		if (!this.dimension.isOverworld() && lightType == LightType.SKY) {
			return 0;
		} else {
			if (pos.getY() < 0) {
				pos = new BlockPos(pos.getX(), 0, pos.getZ());
			}

			if (!this.method_11479(pos)) {
				return lightType.defaultValue;
			} else if (!this.blockExists(pos)) {
				return lightType.defaultValue;
			} else if (this.getBlockState(pos).useNeighbourLight()) {
				int i = this.getLightAtPos(lightType, pos.up());
				int j = this.getLightAtPos(lightType, pos.east());
				int k = this.getLightAtPos(lightType, pos.west());
				int l = this.getLightAtPos(lightType, pos.south());
				int m = this.getLightAtPos(lightType, pos.north());
				if (j > i) {
					i = j;
				}

				if (k > i) {
					i = k;
				}

				if (l > i) {
					i = l;
				}

				if (m > i) {
					i = m;
				}

				return i;
			} else {
				Chunk chunk = this.getChunk(pos);
				return chunk.getLightAtPos(lightType, pos);
			}
		}
	}

	public int getLightAtPos(LightType lightType, BlockPos pos) {
		if (pos.getY() < 0) {
			pos = new BlockPos(pos.getX(), 0, pos.getZ());
		}

		if (!this.method_11479(pos)) {
			return lightType.defaultValue;
		} else if (!this.blockExists(pos)) {
			return lightType.defaultValue;
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getLightAtPos(lightType, pos);
		}
	}

	public void method_8491(LightType lightType, BlockPos pos, int lightLevel) {
		if (this.method_11479(pos)) {
			if (this.blockExists(pos)) {
				Chunk chunk = this.getChunk(pos);
				chunk.setLightAtPos(lightType, pos, lightLevel);
				this.onLightUpdate(pos);
			}
		}
	}

	public void onLightUpdate(BlockPos pos) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).onLightUpdate(pos);
		}
	}

	@Override
	public int getLight(BlockPos pos, int minBlockLight) {
		int i = this.getLuminance(LightType.SKY, pos);
		int j = this.getLuminance(LightType.BLOCK, pos);
		if (j < minBlockLight) {
			j = minBlockLight;
		}

		return i << 20 | j << 4;
	}

	public float getBrightness(BlockPos pos) {
		return this.dimension.getLightLevelToBrightness()[this.getLightLevelWithNeighbours(pos)];
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (this.method_11475(pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getBlockState(pos);
		}
	}

	public boolean isDay() {
		return this.ambientDarkness < 4;
	}

	@Nullable
	public BlockHitResult rayTrace(Vec3d start, Vec3d end) {
		return this.rayTrace(start, end, false, false, false);
	}

	@Nullable
	public BlockHitResult rayTrace(Vec3d start, Vec3d end, boolean bl) {
		return this.rayTrace(start, end, bl, false, false);
	}

	@Nullable
	public BlockHitResult rayTrace(Vec3d start, Vec3d end, boolean bl, boolean bl2, boolean bl3) {
		if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
			return null;
		} else if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
			int i = MathHelper.floor(end.x);
			int j = MathHelper.floor(end.y);
			int k = MathHelper.floor(end.z);
			int l = MathHelper.floor(start.x);
			int m = MathHelper.floor(start.y);
			int n = MathHelper.floor(start.z);
			BlockPos blockPos = new BlockPos(l, m, n);
			BlockState blockState = this.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if ((!bl2 || blockState.method_11726(this, blockPos) != Block.EMPTY_BOX) && block.canCollide(blockState, bl)) {
				BlockHitResult blockHitResult = blockState.method_11711(this, blockPos, start, end);
				if (blockHitResult != null) {
					return blockHitResult;
				}
			}

			BlockHitResult blockHitResult2 = null;
			int o = 200;

			while (o-- >= 0) {
				if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
					return null;
				}

				if (l == i && m == j && n == k) {
					return bl3 ? blockHitResult2 : null;
				}

				boolean bl4 = true;
				boolean bl5 = true;
				boolean bl6 = true;
				double d = 999.0;
				double e = 999.0;
				double f = 999.0;
				if (i > l) {
					d = (double)l + 1.0;
				} else if (i < l) {
					d = (double)l + 0.0;
				} else {
					bl4 = false;
				}

				if (j > m) {
					e = (double)m + 1.0;
				} else if (j < m) {
					e = (double)m + 0.0;
				} else {
					bl5 = false;
				}

				if (k > n) {
					f = (double)n + 1.0;
				} else if (k < n) {
					f = (double)n + 0.0;
				} else {
					bl6 = false;
				}

				double g = 999.0;
				double h = 999.0;
				double p = 999.0;
				double q = end.x - start.x;
				double r = end.y - start.y;
				double s = end.z - start.z;
				if (bl4) {
					g = (d - start.x) / q;
				}

				if (bl5) {
					h = (e - start.y) / r;
				}

				if (bl6) {
					p = (f - start.z) / s;
				}

				if (g == -0.0) {
					g = -1.0E-4;
				}

				if (h == -0.0) {
					h = -1.0E-4;
				}

				if (p == -0.0) {
					p = -1.0E-4;
				}

				Direction direction;
				if (g < h && g < p) {
					direction = i > l ? Direction.WEST : Direction.EAST;
					start = new Vec3d(d, start.y + r * g, start.z + s * g);
				} else if (h < p) {
					direction = j > m ? Direction.DOWN : Direction.UP;
					start = new Vec3d(start.x + q * h, e, start.z + s * h);
				} else {
					direction = k > n ? Direction.NORTH : Direction.SOUTH;
					start = new Vec3d(start.x + q * p, start.y + r * p, f);
				}

				l = MathHelper.floor(start.x) - (direction == Direction.EAST ? 1 : 0);
				m = MathHelper.floor(start.y) - (direction == Direction.UP ? 1 : 0);
				n = MathHelper.floor(start.z) - (direction == Direction.SOUTH ? 1 : 0);
				blockPos = new BlockPos(l, m, n);
				BlockState blockState2 = this.getBlockState(blockPos);
				Block block2 = blockState2.getBlock();
				if (!bl2 || blockState2.getMaterial() == Material.PORTAL || blockState2.method_11726(this, blockPos) != Block.EMPTY_BOX) {
					if (block2.canCollide(blockState2, bl)) {
						BlockHitResult blockHitResult3 = blockState2.method_11711(this, blockPos, start, end);
						if (blockHitResult3 != null) {
							return blockHitResult3;
						}
					} else {
						blockHitResult2 = new BlockHitResult(BlockHitResult.Type.MISS, start, direction, blockPos);
					}
				}
			}

			return bl3 ? blockHitResult2 : null;
		} else {
			return null;
		}
	}

	public void method_11486(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Sound sound, SoundCategory soundCategory, float f, float g) {
		this.playSound(playerEntity, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, sound, soundCategory, f, g);
	}

	public void playSound(@Nullable PlayerEntity playerEntity, double d, double e, double f, Sound sound, SoundCategory soundCategory, float g, float h) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).method_3747(playerEntity, sound, soundCategory, d, e, f, g, h);
		}
	}

	public void playSound(double d, double e, double f, Sound sound, SoundCategory soundCategory, float g, float h, boolean bl) {
	}

	public void method_8509(BlockPos blockPos, @Nullable Sound sound) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).method_8572(sound, blockPos);
		}
	}

	public void addParticle(ParticleType type, double d, double e, double f, double g, double h, double i, int... is) {
		this.addParticle(type.getId(), type.getAlwaysShow(), d, e, f, g, h, i, is);
	}

	public void method_13687(int i, double d, double e, double f, double g, double h, double j, int... is) {
		for (int k = 0; k < this.eventListeners.size(); k++) {
			((WorldEventListener)this.eventListeners.get(k)).method_13696(i, false, true, d, e, f, g, h, j, is);
		}
	}

	public void addParticle(ParticleType type, boolean bl, double d, double e, double f, double g, double h, double i, int... is) {
		this.addParticle(type.getId(), type.getAlwaysShow() || bl, d, e, f, g, h, i, is);
	}

	private void addParticle(int id, boolean bl, double d, double e, double f, double g, double h, double i, int... is) {
		for (int j = 0; j < this.eventListeners.size(); j++) {
			((WorldEventListener)this.eventListeners.get(j)).addParticle(id, bl, d, e, f, g, h, i, is);
		}
	}

	public boolean addEntity(Entity entity) {
		this.entities.add(entity);
		return true;
	}

	public boolean spawnEntity(Entity entity) {
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		boolean bl = entity.teleporting;
		if (entity instanceof PlayerEntity) {
			bl = true;
		}

		if (!bl && !this.isChunkLoaded(i, j, false)) {
			return false;
		} else {
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				this.playerEntities.add(playerEntity);
				this.updateSleepingStatus();
			}

			this.getChunk(i, j).addEntity(entity);
			this.loadedEntities.add(entity);
			this.onEntitySpawned(entity);
			return true;
		}
	}

	protected void onEntitySpawned(Entity entity) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).onEntitySpawned(entity);
		}
	}

	protected void onEntityRemoved(Entity entity) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).onEntityRemoved(entity);
		}
	}

	public void removeEntity(Entity entity) {
		if (entity.hasPassengers()) {
			entity.removeAllPassengers();
		}

		if (entity.hasMount()) {
			entity.stopRiding();
		}

		entity.remove();
		if (entity instanceof PlayerEntity) {
			this.playerEntities.remove(entity);
			this.updateSleepingStatus();
			this.onEntityRemoved(entity);
		}
	}

	public void method_3700(Entity entity) {
		entity.method_12991(false);
		entity.remove();
		if (entity instanceof PlayerEntity) {
			this.playerEntities.remove(entity);
			this.updateSleepingStatus();
		}

		int i = entity.chunkX;
		int j = entity.chunkZ;
		if (entity.updateNeeded && this.isChunkLoaded(i, j, true)) {
			this.getChunk(i, j).removeEntity(entity);
		}

		this.loadedEntities.remove(entity);
		this.onEntityRemoved(entity);
	}

	public void addListener(WorldEventListener listener) {
		this.eventListeners.add(listener);
	}

	public void removeListener(WorldEventListener listener) {
		this.eventListeners.remove(listener);
	}

	private boolean method_13689(@Nullable Entity entity, Box box, boolean bl, @Nullable List<Box> list) {
		int i = MathHelper.floor(box.minX) - 1;
		int j = MathHelper.ceil(box.maxX) + 1;
		int k = MathHelper.floor(box.minY) - 1;
		int l = MathHelper.ceil(box.maxY) + 1;
		int m = MathHelper.floor(box.minZ) - 1;
		int n = MathHelper.ceil(box.maxZ) + 1;
		WorldBorder worldBorder = this.getWorldBorder();
		boolean bl2 = entity != null && entity.isOutsideWorldBorder();
		boolean bl3 = entity != null && this.method_13694(entity);
		BlockState blockState = Blocks.STONE.getDefaultState();
		BlockPos.Pooled pooled = BlockPos.Pooled.get();

		try {
			for (int o = i; o < j; o++) {
				for (int p = m; p < n; p++) {
					boolean bl4 = o == i || o == j - 1;
					boolean bl5 = p == m || p == n - 1;
					if ((!bl4 || !bl5) && this.blockExists(pooled.setPosition(o, 64, p))) {
						for (int q = k; q < l; q++) {
							if (!bl4 && !bl5 || q != l - 1) {
								if (bl) {
									if (o < -30000000 || o >= 30000000 || p < -30000000 || p >= 30000000) {
										return true;
									}
								} else if (entity != null && bl2 == bl3) {
									entity.setOutsideWorldBorder(!bl3);
								}

								pooled.setPosition(o, q, p);
								BlockState blockState3;
								if (!bl && !worldBorder.contains(pooled) && bl3) {
									blockState3 = blockState;
								} else {
									blockState3 = this.getBlockState(pooled);
								}

								blockState3.appendCollisionBoxes(this, pooled, box, list, entity, false);
								if (bl && !list.isEmpty()) {
									return true;
								}
							}
						}
					}
				}
			}
		} finally {
			pooled.method_12576();
		}

		return !list.isEmpty();
	}

	public List<Box> doesBoxCollide(@Nullable Entity entity, Box box) {
		List<Box> list = Lists.newArrayList();
		this.method_13689(entity, box, false, list);
		if (entity != null) {
			List<Entity> list2 = this.getEntitiesIn(entity, box.expand(0.25));

			for (int i = 0; i < list2.size(); i++) {
				Entity entity2 = (Entity)list2.get(i);
				if (!entity.isConnectedThroughVehicle(entity2)) {
					Box box2 = entity2.getBox();
					if (box2 != null && box2.intersects(box)) {
						list.add(box2);
					}

					box2 = entity.getHardCollisionBox(entity2);
					if (box2 != null && box2.intersects(box)) {
						list.add(box2);
					}
				}
			}
		}

		return list;
	}

	public boolean method_13694(Entity entity) {
		double d = this.border.getBoundWest();
		double e = this.border.getBoundNorth();
		double f = this.border.getBoundEast();
		double g = this.border.getBoundSouth();
		if (entity.isOutsideWorldBorder()) {
			d++;
			e++;
			f--;
			g--;
		} else {
			d--;
			e--;
			f++;
			g++;
		}

		return entity.x > d && entity.x < f && entity.z > e && entity.z < g;
	}

	public boolean method_11488(Box box) {
		return this.method_13689(null, box, true, Lists.newArrayList());
	}

	public int method_3597(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		h = 1.0F - h;
		return (int)(h * 11.0F);
	}

	public float method_3649(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	public Vec3d method_3631(Entity entity, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		int i = MathHelper.floor(entity.x);
		int j = MathHelper.floor(entity.y);
		int k = MathHelper.floor(entity.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Biome biome = this.getBiome(blockPos);
		float l = biome.getTemperature(blockPos);
		int m = biome.getSkyColor(l);
		float n = (float)(m >> 16 & 0xFF) / 255.0F;
		float o = (float)(m >> 8 & 0xFF) / 255.0F;
		float p = (float)(m & 0xFF) / 255.0F;
		n *= h;
		o *= h;
		p *= h;
		float q = this.getRainGradient(f);
		if (q > 0.0F) {
			float r = (n * 0.3F + o * 0.59F + p * 0.11F) * 0.6F;
			float s = 1.0F - q * 0.75F;
			n = n * s + r * (1.0F - s);
			o = o * s + r * (1.0F - s);
			p = p * s + r * (1.0F - s);
		}

		float t = this.getThunderGradient(f);
		if (t > 0.0F) {
			float u = (n * 0.3F + o * 0.59F + p * 0.11F) * 0.2F;
			float v = 1.0F - t * 0.75F;
			n = n * v + u * (1.0F - v);
			o = o * v + u * (1.0F - v);
			p = p * v + u * (1.0F - v);
		}

		if (this.field_4553 > 0) {
			float w = (float)this.field_4553 - f;
			if (w > 1.0F) {
				w = 1.0F;
			}

			w *= 0.45F;
			n = n * (1.0F - w) + 0.8F * w;
			o = o * (1.0F - w) + 0.8F * w;
			p = p * (1.0F - w) + 1.0F * w;
		}

		return new Vec3d((double)n, (double)o, (double)p);
	}

	public float getSkyAngle(float tickDelta) {
		return this.dimension.getSkyAngle(this.levelProperties.getTimeOfDay(), tickDelta);
	}

	public int getMoonPhase() {
		return this.dimension.getMoonPhase(this.levelProperties.getTimeOfDay());
	}

	public float getMoonSize() {
		return Dimension.MOON_PHASE_TO_SIZE[this.dimension.getMoonPhase(this.levelProperties.getTimeOfDay())];
	}

	public float getSkyAngleRadians(float tickDelta) {
		float f = this.getSkyAngle(tickDelta);
		return f * (float) (Math.PI * 2);
	}

	public Vec3d getCloudColor(float tickDelta) {
		float f = this.getSkyAngle(tickDelta);
		float g = MathHelper.cos(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		float h = 1.0F;
		float i = 1.0F;
		float j = 1.0F;
		float k = this.getRainGradient(tickDelta);
		if (k > 0.0F) {
			float l = (h * 0.3F + i * 0.59F + j * 0.11F) * 0.6F;
			float m = 1.0F - k * 0.95F;
			h = h * m + l * (1.0F - m);
			i = i * m + l * (1.0F - m);
			j = j * m + l * (1.0F - m);
		}

		h *= g * 0.9F + 0.1F;
		i *= g * 0.9F + 0.1F;
		j *= g * 0.85F + 0.15F;
		float n = this.getThunderGradient(tickDelta);
		if (n > 0.0F) {
			float o = (h * 0.3F + i * 0.59F + j * 0.11F) * 0.2F;
			float p = 1.0F - n * 0.95F;
			h = h * p + o * (1.0F - p);
			i = i * p + o * (1.0F - p);
			j = j * p + o * (1.0F - p);
		}

		return new Vec3d((double)h, (double)i, (double)j);
	}

	public Vec3d getFogColor(float tickDelta) {
		float f = this.getSkyAngle(tickDelta);
		return this.dimension.getFogColor(f, tickDelta);
	}

	public BlockPos method_8562(BlockPos pos) {
		return this.getChunk(pos).method_9156(pos);
	}

	public BlockPos getTopPosition(BlockPos pos) {
		Chunk chunk = this.getChunk(pos);
		BlockPos blockPos = new BlockPos(pos.getX(), chunk.getHighestNonEmptySectionYOffset() + 16, pos.getZ());

		while (blockPos.getY() >= 0) {
			BlockPos blockPos2 = blockPos.down();
			Material material = chunk.getBlockState(blockPos2).getMaterial();
			if (material.blocksMovement() && material != Material.FOLIAGE) {
				break;
			}

			blockPos = blockPos2;
		}

		return blockPos;
	}

	public float method_3707(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
	}

	public boolean method_11489(BlockPos pos, Block block) {
		return true;
	}

	public void createAndScheduleBlockTick(BlockPos pos, Block block, int tickRate) {
	}

	public void createAndScheduleBlockTick(BlockPos pos, Block block, int tickRate, int priority) {
	}

	public void scheduleTick(BlockPos pos, Block block, int tickRate, int priority) {
	}

	public void tickEntities() {
		this.profiler.push("entities");
		this.profiler.push("global");

		for (int i = 0; i < this.entities.size(); i++) {
			Entity entity = (Entity)this.entities.get(i);

			try {
				entity.ticksAlive++;
				entity.tick();
			} catch (Throwable var9) {
				CrashReport crashReport = CrashReport.create(var9, "Ticking entity");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being ticked");
				if (entity == null) {
					crashReportSection.add("Entity", "~~NULL~~");
				} else {
					entity.populateCrashReport(crashReportSection);
				}

				throw new CrashException(crashReport);
			}

			if (entity.removed) {
				this.entities.remove(i--);
			}
		}

		this.profiler.swap("remove");
		this.loadedEntities.removeAll(this.unloadedEntities);

		for (int j = 0; j < this.unloadedEntities.size(); j++) {
			Entity entity2 = (Entity)this.unloadedEntities.get(j);
			int k = entity2.chunkX;
			int l = entity2.chunkZ;
			if (entity2.updateNeeded && this.isChunkLoaded(k, l, true)) {
				this.getChunk(k, l).removeEntity(entity2);
			}
		}

		for (int m = 0; m < this.unloadedEntities.size(); m++) {
			this.onEntityRemoved((Entity)this.unloadedEntities.get(m));
		}

		this.unloadedEntities.clear();
		this.method_11491();
		this.profiler.swap("regular");

		for (int n = 0; n < this.loadedEntities.size(); n++) {
			Entity entity3 = (Entity)this.loadedEntities.get(n);
			Entity entity4 = entity3.getVehicle();
			if (entity4 != null) {
				if (!entity4.removed && entity4.hasPassenger(entity3)) {
					continue;
				}

				entity3.stopRiding();
			}

			this.profiler.push("tick");
			if (!entity3.removed && !(entity3 instanceof ServerPlayerEntity)) {
				try {
					this.checkChunk(entity3);
				} catch (Throwable var8) {
					CrashReport crashReport2 = CrashReport.create(var8, "Ticking entity");
					CrashReportSection crashReportSection2 = crashReport2.addElement("Entity being ticked");
					entity3.populateCrashReport(crashReportSection2);
					throw new CrashException(crashReport2);
				}
			}

			this.profiler.pop();
			this.profiler.push("remove");
			if (entity3.removed) {
				int o = entity3.chunkX;
				int p = entity3.chunkZ;
				if (entity3.updateNeeded && this.isChunkLoaded(o, p, true)) {
					this.getChunk(o, p).removeEntity(entity3);
				}

				this.loadedEntities.remove(n--);
				this.onEntityRemoved(entity3);
			}

			this.profiler.pop();
		}

		this.profiler.swap("blockEntities");
		if (!this.unloadedBlockEntities.isEmpty()) {
			this.tickingBlockEntities.removeAll(this.unloadedBlockEntities);
			this.blockEntities.removeAll(this.unloadedBlockEntities);
			this.unloadedBlockEntities.clear();
		}

		this.iteratingTickingBlockEntities = true;
		Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();

		while (iterator.hasNext()) {
			BlockEntity blockEntity = (BlockEntity)iterator.next();
			if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
				BlockPos blockPos = blockEntity.getPos();
				if (this.blockExists(blockPos) && this.border.contains(blockPos)) {
					try {
						this.profiler.push((Supplier<String>)(() -> String.valueOf(BlockEntity.getIdentifier(blockEntity.getClass()))));
						((Tickable)blockEntity).tick();
						this.profiler.pop();
					} catch (Throwable var7) {
						CrashReport crashReport3 = CrashReport.create(var7, "Ticking block entity");
						CrashReportSection crashReportSection3 = crashReport3.addElement("Block entity being ticked");
						blockEntity.populateCrashReport(crashReportSection3);
						throw new CrashException(crashReport3);
					}
				}
			}

			if (blockEntity.isRemoved()) {
				iterator.remove();
				this.blockEntities.remove(blockEntity);
				if (this.blockExists(blockEntity.getPos())) {
					this.getChunk(blockEntity.getPos()).method_9150(blockEntity.getPos());
				}
			}
		}

		this.iteratingTickingBlockEntities = false;
		this.profiler.swap("pendingBlockEntities");
		if (!this.pendingBlockEntities.isEmpty()) {
			for (int q = 0; q < this.pendingBlockEntities.size(); q++) {
				BlockEntity blockEntity2 = (BlockEntity)this.pendingBlockEntities.get(q);
				if (!blockEntity2.isRemoved()) {
					if (!this.blockEntities.contains(blockEntity2)) {
						this.addBlockEntity(blockEntity2);
					}

					if (this.blockExists(blockEntity2.getPos())) {
						Chunk chunk = this.getChunk(blockEntity2.getPos());
						BlockState blockState = chunk.getBlockState(blockEntity2.getPos());
						chunk.method_9136(blockEntity2.getPos(), blockEntity2);
						this.method_11481(blockEntity2.getPos(), blockState, blockState, 3);
					}
				}
			}

			this.pendingBlockEntities.clear();
		}

		this.profiler.pop();
		this.profiler.pop();
	}

	protected void method_11491() {
	}

	public boolean addBlockEntity(BlockEntity blockEntity) {
		boolean bl = this.blockEntities.add(blockEntity);
		if (bl && blockEntity instanceof Tickable) {
			this.tickingBlockEntities.add(blockEntity);
		}

		if (this.isClient) {
			BlockPos blockPos = blockEntity.getPos();
			BlockState blockState = this.getBlockState(blockPos);
			this.method_11481(blockPos, blockState, blockState, 2);
		}

		return bl;
	}

	public void addBlockEntities(Collection<BlockEntity> collection) {
		if (this.iteratingTickingBlockEntities) {
			this.pendingBlockEntities.addAll(collection);
		} else {
			for (BlockEntity blockEntity : collection) {
				this.addBlockEntity(blockEntity);
			}
		}
	}

	public void checkChunk(Entity entity) {
		this.checkChunk(entity, true);
	}

	public void checkChunk(Entity entity, boolean bl) {
		if (!(entity instanceof PlayerEntity)) {
			int i = MathHelper.floor(entity.x);
			int j = MathHelper.floor(entity.z);
			int k = 32;
			if (bl && !this.isRegionLoaded(i - 32, 0, j - 32, i + 32, 0, j + 32, true)) {
				return;
			}
		}

		entity.prevTickX = entity.x;
		entity.prevTickY = entity.y;
		entity.prevTickZ = entity.z;
		entity.prevYaw = entity.yaw;
		entity.prevPitch = entity.pitch;
		if (bl && entity.updateNeeded) {
			entity.ticksAlive++;
			if (entity.hasMount()) {
				entity.tickRiding();
			} else {
				entity.tick();
			}
		}

		this.profiler.push("chunkCheck");
		if (Double.isNaN(entity.x) || Double.isInfinite(entity.x)) {
			entity.x = entity.prevTickX;
		}

		if (Double.isNaN(entity.y) || Double.isInfinite(entity.y)) {
			entity.y = entity.prevTickY;
		}

		if (Double.isNaN(entity.z) || Double.isInfinite(entity.z)) {
			entity.z = entity.prevTickZ;
		}

		if (Double.isNaN((double)entity.pitch) || Double.isInfinite((double)entity.pitch)) {
			entity.pitch = entity.prevPitch;
		}

		if (Double.isNaN((double)entity.yaw) || Double.isInfinite((double)entity.yaw)) {
			entity.yaw = entity.prevYaw;
		}

		int l = MathHelper.floor(entity.x / 16.0);
		int m = MathHelper.floor(entity.y / 16.0);
		int n = MathHelper.floor(entity.z / 16.0);
		if (!entity.updateNeeded || entity.chunkX != l || entity.chunkY != m || entity.chunkZ != n) {
			if (entity.updateNeeded && this.isChunkLoaded(entity.chunkX, entity.chunkZ, true)) {
				this.getChunk(entity.chunkX, entity.chunkZ).removeEntity(entity, entity.chunkY);
			}

			if (!entity.teleportRequested() && !this.isChunkLoaded(l, n, true)) {
				entity.updateNeeded = false;
			} else {
				this.getChunk(l, n).addEntity(entity);
			}
		}

		this.profiler.pop();
		if (bl && entity.updateNeeded) {
			for (Entity entity2 : entity.getPassengerList()) {
				if (!entity2.removed && entity2.getVehicle() == entity) {
					this.checkChunk(entity2);
				} else {
					entity2.stopRiding();
				}
			}
		}
	}

	public boolean hasEntityIn(Box box) {
		return this.hasEntityIn(box, null);
	}

	public boolean hasEntityIn(Box box, @Nullable Entity except) {
		List<Entity> list = this.getEntitiesIn(null, box);

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (!entity.removed && entity.inanimate && entity != except && (except == null || entity.isConnectedThroughVehicle(except))) {
				return false;
			}
		}

		return true;
	}

	public boolean isBoxNotEmpty(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockPos.Pooled pooled = BlockPos.Pooled.get();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					BlockState blockState = this.getBlockState(pooled.setPosition(o, p, q));
					if (blockState.getMaterial() != Material.AIR) {
						pooled.method_12576();
						return true;
					}
				}
			}
		}

		pooled.method_12576();
		return false;
	}

	public boolean containsFluid(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockPos.Pooled pooled = BlockPos.Pooled.get();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					BlockState blockState = this.getBlockState(pooled.setPosition(o, p, q));
					if (blockState.getMaterial().isFluid()) {
						pooled.method_12576();
						return true;
					}
				}
			}
		}

		pooled.method_12576();
		return false;
	}

	public boolean containsFireSource(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (this.isRegionLoaded(i, k, m, j, l, n, true)) {
			BlockPos.Pooled pooled = BlockPos.Pooled.get();

			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						Block block = this.getBlockState(pooled.setPosition(o, p, q)).getBlock();
						if (block == Blocks.FIRE || block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
							pooled.method_12576();
							return true;
						}
					}
				}
			}

			pooled.method_12576();
		}

		return false;
	}

	public boolean method_3610(Box box, Material material, Entity entity) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (!this.isRegionLoaded(i, k, m, j, l, n, true)) {
			return false;
		} else {
			boolean bl = false;
			Vec3d vec3d = Vec3d.ZERO;
			BlockPos.Pooled pooled = BlockPos.Pooled.get();

			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						pooled.setPosition(o, p, q);
						BlockState blockState = this.getBlockState(pooled);
						Block block = blockState.getBlock();
						if (blockState.getMaterial() == material) {
							double d = (double)((float)(p + 1) - AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL)));
							if ((double)l >= d) {
								bl = true;
								vec3d = block.onEntityCollision(this, pooled, entity, vec3d);
							}
						}
					}
				}
			}

			pooled.method_12576();
			if (vec3d.length() > 0.0 && entity.canFly()) {
				vec3d = vec3d.normalize();
				double e = 0.014;
				entity.velocityX = entity.velocityX + vec3d.x * 0.014;
				entity.velocityY = entity.velocityY + vec3d.y * 0.014;
				entity.velocityZ = entity.velocityZ + vec3d.z * 0.014;
			}

			return bl;
		}
	}

	public boolean containsMaterial(Box box, Material material) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockPos.Pooled pooled = BlockPos.Pooled.get();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					if (this.getBlockState(pooled.setPosition(o, p, q)).getMaterial() == material) {
						pooled.method_12576();
						return true;
					}
				}
			}
		}

		pooled.method_12576();
		return false;
	}

	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean destructive) {
		return this.createExplosion(entity, x, y, z, power, false, destructive);
	}

	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, boolean destructive) {
		Explosion explosion = new Explosion(this, entity, x, y, z, power, createFire, destructive);
		explosion.collectBlocksAndDamageEntities();
		explosion.affectWorld(true);
		return explosion;
	}

	public float method_3612(Vec3d pos, Box box) {
		double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
		double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
		double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
		double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
		double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
		if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
			int i = 0;
			int j = 0;

			for (float k = 0.0F; k <= 1.0F; k = (float)((double)k + d)) {
				for (float l = 0.0F; l <= 1.0F; l = (float)((double)l + e)) {
					for (float m = 0.0F; m <= 1.0F; m = (float)((double)m + f)) {
						double n = box.minX + (box.maxX - box.minX) * (double)k;
						double o = box.minY + (box.maxY - box.minY) * (double)l;
						double p = box.minZ + (box.maxZ - box.minZ) * (double)m;
						if (this.rayTrace(new Vec3d(n + g, o, p + h), pos) == null) {
							i++;
						}

						j++;
					}
				}
			}

			return (float)i / (float)j;
		} else {
			return 0.0F;
		}
	}

	public boolean extinguishFire(@Nullable PlayerEntity player, BlockPos pos, Direction direction) {
		pos = pos.offset(direction);
		if (this.getBlockState(pos).getBlock() == Blocks.FIRE) {
			this.syncWorldEvent(player, 1009, pos, 0);
			this.setAir(pos);
			return true;
		} else {
			return false;
		}
	}

	public String addDetailsToCrashReport() {
		return "All: " + this.loadedEntities.size();
	}

	public String getDebugString() {
		return this.chunkProvider.getChunkProviderName();
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		if (this.method_11475(pos)) {
			return null;
		} else {
			BlockEntity blockEntity = null;
			if (this.iteratingTickingBlockEntities) {
				blockEntity = this.method_11476(pos);
			}

			if (blockEntity == null) {
				blockEntity = this.getChunk(pos).getBlockEntity(pos, Chunk.Status.IMMEDIATE);
			}

			if (blockEntity == null) {
				blockEntity = this.method_11476(pos);
			}

			return blockEntity;
		}
	}

	@Nullable
	private BlockEntity method_11476(BlockPos blockPos) {
		for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
			BlockEntity blockEntity = (BlockEntity)this.pendingBlockEntities.get(i);
			if (!blockEntity.isRemoved() && blockEntity.getPos().equals(blockPos)) {
				return blockEntity;
			}
		}

		return null;
	}

	public void setBlockEntity(BlockPos pos, @Nullable BlockEntity blockEntity) {
		if (!this.method_11475(pos)) {
			if (blockEntity != null && !blockEntity.isRemoved()) {
				if (this.iteratingTickingBlockEntities) {
					blockEntity.setPosition(pos);
					Iterator<BlockEntity> iterator = this.pendingBlockEntities.iterator();

					while (iterator.hasNext()) {
						BlockEntity blockEntity2 = (BlockEntity)iterator.next();
						if (blockEntity2.getPos().equals(pos)) {
							blockEntity2.markRemoved();
							iterator.remove();
						}
					}

					this.pendingBlockEntities.add(blockEntity);
				} else {
					this.getChunk(pos).method_9136(pos, blockEntity);
					this.addBlockEntity(blockEntity);
				}
			}
		}
	}

	public void removeBlockEntity(BlockPos pos) {
		BlockEntity blockEntity = this.getBlockEntity(pos);
		if (blockEntity != null && this.iteratingTickingBlockEntities) {
			blockEntity.markRemoved();
			this.pendingBlockEntities.remove(blockEntity);
		} else {
			if (blockEntity != null) {
				this.pendingBlockEntities.remove(blockEntity);
				this.blockEntities.remove(blockEntity);
				this.tickingBlockEntities.remove(blockEntity);
			}

			this.getChunk(pos).method_9150(pos);
		}
	}

	public void queueBlockEntity(BlockEntity blockEntity) {
		this.unloadedBlockEntities.add(blockEntity);
	}

	public boolean method_11492(BlockPos blockPos) {
		Box box = this.getBlockState(blockPos).method_11726(this, blockPos);
		return box != Block.EMPTY_BOX && box.getAverage() >= 1.0;
	}

	public boolean renderAsNormalBlock(BlockPos pos, boolean defaultValue) {
		if (this.method_11475(pos)) {
			return false;
		} else {
			Chunk chunk = this.chunkProvider.getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4);
			if (chunk != null && !chunk.isEmpty()) {
				BlockState blockState = this.getBlockState(pos);
				return blockState.getMaterial().isOpaque() && blockState.method_11730();
			} else {
				return defaultValue;
			}
		}
	}

	public void calculateAmbientDarkness() {
		int i = this.method_3597(1.0F);
		if (i != this.ambientDarkness) {
			this.ambientDarkness = i;
		}
	}

	public void setMobSpawning(boolean spawnAnimals, boolean spawnMonsters) {
		this.spawnAnimals = spawnAnimals;
		this.spawnMonsters = spawnMonsters;
	}

	public void tick() {
		this.tickWeather();
	}

	protected void initWeatherGradients() {
		if (this.levelProperties.isRaining()) {
			this.rainGradient = 1.0F;
			if (this.levelProperties.isThundering()) {
				this.thunderGradient = 1.0F;
			}
		}
	}

	protected void tickWeather() {
		if (this.dimension.isOverworld()) {
			if (!this.isClient) {
				boolean bl = this.getGameRules().getBoolean("doWeatherCycle");
				if (bl) {
					int i = this.levelProperties.getClearWeatherTime();
					if (i > 0) {
						this.levelProperties.setClearWeatherTime(--i);
						this.levelProperties.setThunderTime(this.levelProperties.isThundering() ? 1 : 2);
						this.levelProperties.setRainTime(this.levelProperties.isRaining() ? 1 : 2);
					}

					int j = this.levelProperties.getThunderTime();
					if (j <= 0) {
						if (this.levelProperties.isThundering()) {
							this.levelProperties.setThunderTime(this.random.nextInt(12000) + 3600);
						} else {
							this.levelProperties.setThunderTime(this.random.nextInt(168000) + 12000);
						}
					} else {
						this.levelProperties.setThunderTime(--j);
						if (j <= 0) {
							this.levelProperties.setThundering(!this.levelProperties.isThundering());
						}
					}

					int k = this.levelProperties.getRainTime();
					if (k <= 0) {
						if (this.levelProperties.isRaining()) {
							this.levelProperties.setRainTime(this.random.nextInt(12000) + 12000);
						} else {
							this.levelProperties.setRainTime(this.random.nextInt(168000) + 12000);
						}
					} else {
						this.levelProperties.setRainTime(--k);
						if (k <= 0) {
							this.levelProperties.setRaining(!this.levelProperties.isRaining());
						}
					}
				}

				this.thunderGradientPrev = this.thunderGradient;
				if (this.levelProperties.isThundering()) {
					this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
				} else {
					this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
				}

				this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
				this.rainGradientPrev = this.rainGradient;
				if (this.levelProperties.isRaining()) {
					this.rainGradient = (float)((double)this.rainGradient + 0.01);
				} else {
					this.rainGradient = (float)((double)this.rainGradient - 0.01);
				}

				this.rainGradient = MathHelper.clamp(this.rainGradient, 0.0F, 1.0F);
			}
		}
	}

	protected void method_3605(int i, int j, Chunk chunk) {
		chunk.method_3923();
	}

	protected void tickBlocks() {
	}

	public void method_11482(BlockPos pos, BlockState state, Random random) {
		this.immediateUpdates = true;
		state.getBlock().onScheduledTick(this, pos, state, random);
		this.immediateUpdates = false;
	}

	public boolean canWaterFreezeAt(BlockPos pos) {
		return this.canWaterFreezeAt(pos, false);
	}

	public boolean canWaterNotFreezeAt(BlockPos pos) {
		return this.canWaterFreezeAt(pos, true);
	}

	public boolean canWaterFreezeAt(BlockPos pos, boolean noChange) {
		Biome biome = this.getBiome(pos);
		float f = biome.getTemperature(pos);
		if (f >= 0.15F) {
			return false;
		} else {
			if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightAtPos(LightType.BLOCK, pos) < 10) {
				BlockState blockState = this.getBlockState(pos);
				Block block = blockState.getBlock();
				if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && (Integer)blockState.get(AbstractFluidBlock.LEVEL) == 0) {
					if (!noChange) {
						return true;
					}

					boolean bl = this.isWater(pos.west()) && this.isWater(pos.east()) && this.isWater(pos.north()) && this.isWater(pos.south());
					if (!bl) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private boolean isWater(BlockPos pos) {
		return this.getBlockState(pos).getMaterial() == Material.WATER;
	}

	public boolean method_8552(BlockPos pos, boolean bl) {
		Biome biome = this.getBiome(pos);
		float f = biome.getTemperature(pos);
		if (f >= 0.15F) {
			return false;
		} else if (!bl) {
			return true;
		} else {
			if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightAtPos(LightType.BLOCK, pos) < 10) {
				BlockState blockState = this.getBlockState(pos);
				if (blockState.getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canBePlacedAtPos(this, pos)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean method_8568(BlockPos pos) {
		boolean bl = false;
		if (this.dimension.isOverworld()) {
			bl |= this.calculateLightAtPos(LightType.SKY, pos);
		}

		return bl | this.calculateLightAtPos(LightType.BLOCK, pos);
	}

	private int getLightAtPos(BlockPos pos, LightType type) {
		if (type == LightType.SKY && this.hasDirectSunlight(pos)) {
			return 15;
		} else {
			BlockState blockState = this.getBlockState(pos);
			int i = type == LightType.SKY ? 0 : blockState.getLuminance();
			int j = blockState.getOpacity();
			if (j >= 15 && blockState.getLuminance() > 0) {
				j = 1;
			}

			if (j < 1) {
				j = 1;
			}

			if (j >= 15) {
				return 0;
			} else if (i >= 14) {
				return i;
			} else {
				BlockPos.Pooled pooled = BlockPos.Pooled.get();

				try {
					for (Direction direction : Direction.values()) {
						pooled.set(pos).move(direction);
						int k = this.getLightAtPos(type, pooled) - j;
						if (k > i) {
							i = k;
						}

						if (i >= 14) {
							return i;
						}
					}

					return i;
				} finally {
					pooled.method_12576();
				}
			}
		}
	}

	public boolean calculateLightAtPos(LightType lightType, BlockPos pos) {
		if (!this.isRegionLoaded(pos, 17, false)) {
			return false;
		} else {
			int i = 0;
			int j = 0;
			this.profiler.push("getBrightness");
			int k = this.getLightAtPos(lightType, pos);
			int l = this.getLightAtPos(pos, lightType);
			int m = pos.getX();
			int n = pos.getY();
			int o = pos.getZ();
			if (l > k) {
				this.updateLightBlocks[j++] = 133152;
			} else if (l < k) {
				this.updateLightBlocks[j++] = 133152 | k << 18;

				while (i < j) {
					int p = this.updateLightBlocks[i++];
					int q = (p & 63) - 32 + m;
					int r = (p >> 6 & 63) - 32 + n;
					int s = (p >> 12 & 63) - 32 + o;
					int t = p >> 18 & 15;
					BlockPos blockPos = new BlockPos(q, r, s);
					int u = this.getLightAtPos(lightType, blockPos);
					if (u == t) {
						this.method_8491(lightType, blockPos, 0);
						if (t > 0) {
							int v = MathHelper.abs(q - m);
							int w = MathHelper.abs(r - n);
							int x = MathHelper.abs(s - o);
							if (v + w + x < 17) {
								BlockPos.Pooled pooled = BlockPos.Pooled.get();

								for (Direction direction : Direction.values()) {
									int y = q + direction.getOffsetX();
									int z = r + direction.getOffsetY();
									int aa = s + direction.getOffsetZ();
									pooled.setPosition(y, z, aa);
									int ab = Math.max(1, this.getBlockState(pooled).getOpacity());
									u = this.getLightAtPos(lightType, pooled);
									if (u == t - ab && j < this.updateLightBlocks.length) {
										this.updateLightBlocks[j++] = y - m + 32 | z - n + 32 << 6 | aa - o + 32 << 12 | t - ab << 18;
									}
								}

								pooled.method_12576();
							}
						}
					}
				}

				i = 0;
			}

			this.profiler.pop();
			this.profiler.push("checkedPosition < toCheckCount");

			while (i < j) {
				int ac = this.updateLightBlocks[i++];
				int ad = (ac & 63) - 32 + m;
				int ae = (ac >> 6 & 63) - 32 + n;
				int af = (ac >> 12 & 63) - 32 + o;
				BlockPos blockPos2 = new BlockPos(ad, ae, af);
				int ag = this.getLightAtPos(lightType, blockPos2);
				int ah = this.getLightAtPos(blockPos2, lightType);
				if (ah != ag) {
					this.method_8491(lightType, blockPos2, ah);
					if (ah > ag) {
						int ai = Math.abs(ad - m);
						int aj = Math.abs(ae - n);
						int ak = Math.abs(af - o);
						boolean bl = j < this.updateLightBlocks.length - 6;
						if (ai + aj + ak < 17 && bl) {
							if (this.getLightAtPos(lightType, blockPos2.west()) < ah) {
								this.updateLightBlocks[j++] = ad - 1 - m + 32 + (ae - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.east()) < ah) {
								this.updateLightBlocks[j++] = ad + 1 - m + 32 + (ae - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.down()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae - 1 - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.up()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae + 1 - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.north()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae - n + 32 << 6) + (af - 1 - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.south()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae - n + 32 << 6) + (af + 1 - o + 32 << 12);
							}
						}
					}
				}
			}

			this.profiler.pop();
			return true;
		}
	}

	public boolean method_3644(boolean bl) {
		return false;
	}

	@Nullable
	public List<ScheduledTick> getScheduledTicks(Chunk chunk, boolean bl) {
		return null;
	}

	@Nullable
	public List<ScheduledTick> getScheduledTicks(BlockBox box, boolean bl) {
		return null;
	}

	public List<Entity> getEntitiesIn(@Nullable Entity entity, Box box) {
		return this.getEntitiesIn(entity, box, EntityPredicate.EXCEPT_SPECTATOR);
	}

	public List<Entity> getEntitiesIn(@Nullable Entity entity, Box box, @Nullable Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.newArrayList();
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (this.isChunkLoaded(m, n, true)) {
					this.getChunk(m, n).method_9141(entity, box, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends Entity> List<T> method_8514(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.newArrayList();

		for (Entity entity : this.loadedEntities) {
			if (class_.isAssignableFrom(entity.getClass()) && predicate.apply(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public <T extends Entity> List<T> method_8536(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.newArrayList();

		for (Entity entity : this.playerEntities) {
			if (class_.isAssignableFrom(entity.getClass()) && predicate.apply(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public <T extends Entity> List<T> getEntitiesInBox(Class<? extends T> except, Box box) {
		return this.getEntitiesInBox(except, box, EntityPredicate.EXCEPT_SPECTATOR);
	}

	public <T extends Entity> List<T> getEntitiesInBox(Class<? extends T> clazz, Box box, @Nullable Predicate<? super T> entityPredicate) {
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
		List<T> list = Lists.newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				if (this.isChunkLoaded(m, n, true)) {
					this.getChunk(m, n).method_9140(clazz, box, list, entityPredicate);
				}
			}
		}

		return list;
	}

	@Nullable
	public <T extends Entity> T getEntitiesByClass(Class<? extends T> entityClass, Box box, T except) {
		List<T> list = this.getEntitiesInBox(entityClass, box);
		T entity = null;
		double d = Double.MAX_VALUE;

		for (int i = 0; i < list.size(); i++) {
			T entity2 = (T)list.get(i);
			if (entity2 != except && EntityPredicate.EXCEPT_SPECTATOR.apply(entity2)) {
				double e = except.squaredDistanceTo(entity2);
				if (!(e > d)) {
					entity = entity2;
					d = e;
				}
			}
		}

		return entity;
	}

	@Nullable
	public Entity getEntityById(int id) {
		return this.idToEntity.get(id);
	}

	public List<Entity> getLoadedEntities() {
		return this.loadedEntities;
	}

	public void markDirty(BlockPos pos, BlockEntity blockEntity) {
		if (this.blockExists(pos)) {
			this.getChunk(pos).setModified();
		}
	}

	public int getPersistentEntityCount(Class<?> entityClass) {
		int i = 0;

		for (Entity entity : this.loadedEntities) {
			if ((!(entity instanceof MobEntity) || !((MobEntity)entity).isPersistent()) && entityClass.isAssignableFrom(entity.getClass())) {
				i++;
			}
		}

		return i;
	}

	public void method_8537(Collection<Entity> collection) {
		this.loadedEntities.addAll(collection);

		for (Entity entity : collection) {
			this.onEntitySpawned(entity);
		}
	}

	public void unloadEntities(Collection<Entity> entities) {
		this.unloadedEntities.addAll(entities);
	}

	public boolean method_8493(Block block, BlockPos pos, boolean bl, Direction direction, @Nullable Entity entity) {
		BlockState blockState = this.getBlockState(pos);
		Box box = bl ? null : block.getDefaultState().method_11726(this, pos);
		if (box != Block.EMPTY_BOX && !this.hasEntityIn(box.offset(pos), entity)) {
			return false;
		} else {
			return blockState.getMaterial() == Material.DECORATION && block == Blocks.ANVIL
				? true
				: blockState.getMaterial().isReplaceable() && block.canBePlacedAdjacent(this, pos, direction);
		}
	}

	public int getSeaLevel() {
		return this.seaLevel;
	}

	public void setSeaLevel(int seaLevel) {
		this.seaLevel = seaLevel;
	}

	@Override
	public int getStrongRedstonePower(BlockPos pos, Direction direction) {
		return this.getBlockState(pos).getStrongRedstonePower(this, pos, direction);
	}

	@Override
	public LevelGeneratorType getGeneratorType() {
		return this.levelProperties.getGeneratorType();
	}

	public int getReceivedStrongRedstonePower(BlockPos pos) {
		int i = 0;
		i = Math.max(i, this.getStrongRedstonePower(pos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.getStrongRedstonePower(pos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.getStrongRedstonePower(pos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.getStrongRedstonePower(pos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.getStrongRedstonePower(pos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.getStrongRedstonePower(pos.east(), Direction.EAST));
							return i >= 15 ? i : i;
						}
					}
				}
			}
		}
	}

	public boolean isEmittingRedstonePower(BlockPos pos, Direction dir) {
		return this.getEmittedRedstonePower(pos, dir) > 0;
	}

	public int getEmittedRedstonePower(BlockPos pos, Direction dir) {
		BlockState blockState = this.getBlockState(pos);
		return blockState.method_11734() ? this.getReceivedStrongRedstonePower(pos) : blockState.getWeakRedstonePower(this, pos, dir);
	}

	public boolean isReceivingRedstonePower(BlockPos pos) {
		if (this.getEmittedRedstonePower(pos.down(), Direction.DOWN) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.up(), Direction.UP) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.north(), Direction.NORTH) > 0) {
			return true;
		} else if (this.getEmittedRedstonePower(pos.south(), Direction.SOUTH) > 0) {
			return true;
		} else {
			return this.getEmittedRedstonePower(pos.west(), Direction.WEST) > 0 ? true : this.getEmittedRedstonePower(pos.east(), Direction.EAST) > 0;
		}
	}

	public int getReceivedRedstonePower(BlockPos pos) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			int j = this.getEmittedRedstonePower(pos.offset(direction), direction);
			if (j >= 15) {
				return 15;
			}

			if (j > i) {
				i = j;
			}
		}

		return i;
	}

	@Nullable
	public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
		return this.method_11478(entity.x, entity.y, entity.z, maxDistance, false);
	}

	@Nullable
	public PlayerEntity method_11490(Entity entity, double d) {
		return this.method_11478(entity.x, entity.y, entity.z, d, true);
	}

	@Nullable
	public PlayerEntity method_11478(double d, double e, double f, double g, boolean bl) {
		Predicate<Entity> predicate = bl ? EntityPredicate.EXCEPT_CREATIVE_OR_SPECTATOR : EntityPredicate.EXCEPT_SPECTATOR;
		return this.method_13686(d, e, f, g, predicate);
	}

	@Nullable
	public PlayerEntity method_13686(double d, double e, double f, double g, Predicate<Entity> predicate) {
		double h = -1.0;
		PlayerEntity playerEntity = null;

		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(i);
			if (predicate.apply(playerEntity2)) {
				double j = playerEntity2.squaredDistanceTo(d, e, f);
				if ((g < 0.0 || j < g * g) && (h == -1.0 || j < h)) {
					h = j;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	public boolean isPlayerInRange(double x, double y, double z, double maxDistance) {
		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.playerEntities.get(i);
			if (EntityPredicate.EXCEPT_SPECTATOR.apply(playerEntity)) {
				double d = playerEntity.squaredDistanceTo(x, y, z);
				if (maxDistance < 0.0 || d < maxDistance * maxDistance) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	public PlayerEntity method_11484(Entity entity, double d, double e) {
		return this.method_11477(entity.x, entity.y, entity.z, d, e, null, null);
	}

	@Nullable
	public PlayerEntity method_11480(BlockPos blockPos, double d, double e) {
		return this.method_11477(
			(double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), d, e, null, null
		);
	}

	@Nullable
	public PlayerEntity method_11477(
		double d, double e, double f, double g, double h, @Nullable Function<PlayerEntity, Double> function, @Nullable Predicate<PlayerEntity> predicate
	) {
		double i = -1.0;
		PlayerEntity playerEntity = null;

		for (int j = 0; j < this.playerEntities.size(); j++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(j);
			if (!playerEntity2.abilities.invulnerable
				&& playerEntity2.isAlive()
				&& !playerEntity2.isSpectator()
				&& (predicate == null || predicate.apply(playerEntity2))) {
				double k = playerEntity2.squaredDistanceTo(d, playerEntity2.y, f);
				double l = g;
				if (playerEntity2.isSneaking()) {
					l = g * 0.8F;
				}

				if (playerEntity2.isInvisible()) {
					float m = playerEntity2.method_4575();
					if (m < 0.1F) {
						m = 0.1F;
					}

					l *= (double)(0.7F * m);
				}

				if (function != null) {
					l *= MoreObjects.firstNonNull(function.apply(playerEntity2), 1.0);
				}

				if ((h < 0.0 || Math.abs(playerEntity2.y - e) < h * h) && (g < 0.0 || k < l * l) && (i == -1.0 || k < i)) {
					i = k;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	@Nullable
	public PlayerEntity getPlayerByName(String name) {
		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.playerEntities.get(i);
			if (name.equals(playerEntity.getTranslationKey())) {
				return playerEntity;
			}
		}

		return null;
	}

	@Nullable
	public PlayerEntity getPlayerByUuid(UUID uuid) {
		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.playerEntities.get(i);
			if (uuid.equals(playerEntity.getUuid())) {
				return playerEntity;
			}
		}

		return null;
	}

	public void disconnect() {
	}

	public void readSaveLock() throws WorldSaveException {
		this.saveHandler.readSessionLock();
	}

	public void setTime(long time) {
		this.levelProperties.setTime(time);
	}

	public long getSeed() {
		return this.levelProperties.getSeed();
	}

	public long getLastUpdateTime() {
		return this.levelProperties.getTime();
	}

	public long getTimeOfDay() {
		return this.levelProperties.getTimeOfDay();
	}

	public void setTimeOfDay(long time) {
		this.levelProperties.setDayTime(time);
	}

	public BlockPos getSpawnPos() {
		BlockPos blockPos = new BlockPos(this.levelProperties.getSpawnX(), this.levelProperties.getSpawnY(), this.levelProperties.getSpawnZ());
		if (!this.getWorldBorder().contains(blockPos)) {
			blockPos = this.getHighestBlock(new BlockPos(this.getWorldBorder().getCenterX(), 0.0, this.getWorldBorder().getCenterZ()));
		}

		return blockPos;
	}

	public void setSpawnPos(BlockPos pos) {
		this.levelProperties.setSpawnPos(pos);
	}

	public void loadEntity(Entity entity) {
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		int k = 2;

		for (int l = -2; l <= 2; l++) {
			for (int m = -2; m <= 2; m++) {
				this.getChunk(i + l, j + m);
			}
		}

		if (!this.loadedEntities.contains(entity)) {
			this.loadedEntities.add(entity);
		}
	}

	public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
		return true;
	}

	public void sendEntityStatus(Entity entity, byte status) {
	}

	public ChunkProvider getChunkProvider() {
		return this.chunkProvider;
	}

	public void addBlockAction(BlockPos pos, Block block, int type, int data) {
		this.getBlockState(pos).onSyncedBlockEvent(this, pos, type, data);
	}

	public SaveHandler getSaveHandler() {
		return this.saveHandler;
	}

	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	public GameRuleManager getGameRules() {
		return this.levelProperties.getGamerules();
	}

	public void updateSleepingStatus() {
	}

	public float getThunderGradient(float offset) {
		return (this.thunderGradientPrev + (this.thunderGradient - this.thunderGradientPrev) * offset) * this.getRainGradient(offset);
	}

	public void setThunderGradient(float thunderGradient) {
		this.thunderGradientPrev = thunderGradient;
		this.thunderGradient = thunderGradient;
	}

	public float getRainGradient(float offset) {
		return this.rainGradientPrev + (this.rainGradient - this.rainGradientPrev) * offset;
	}

	public void setRainGradient(float rainGradient) {
		this.rainGradientPrev = rainGradient;
		this.rainGradient = rainGradient;
	}

	public boolean isThundering() {
		return (double)this.getThunderGradient(1.0F) > 0.9;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean hasRain(BlockPos pos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.hasDirectSunlight(pos)) {
			return false;
		} else if (this.method_8562(pos).getY() > pos.getY()) {
			return false;
		} else {
			Biome biome = this.getBiome(pos);
			if (biome.isMutated()) {
				return false;
			} else {
				return this.method_8552(pos, false) ? false : biome.method_3830();
			}
		}
	}

	public boolean hasHighHumidity(BlockPos pos) {
		Biome biome = this.getBiome(pos);
		return biome.hasHighHumidity();
	}

	@Nullable
	public PersistentStateManager getPersistentStateManager() {
		return this.persistentStateManager;
	}

	public void replaceState(String name, PersistentState state) {
		this.persistentStateManager.replace(name, state);
	}

	@Nullable
	public PersistentState getOrCreateState(Class<? extends PersistentState> cls, String name) {
		return this.persistentStateManager.getOrCreate(cls, name);
	}

	public int getIntState(String name) {
		return this.persistentStateManager.getInt(name);
	}

	public void method_4689(int i, BlockPos pos, int j) {
		for (int k = 0; k < this.eventListeners.size(); k++) {
			((WorldEventListener)this.eventListeners.get(k)).processGlobalEvent(i, pos, j);
		}
	}

	public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
		this.syncWorldEvent(null, eventId, pos, data);
	}

	public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {
		try {
			for (int i = 0; i < this.eventListeners.size(); i++) {
				((WorldEventListener)this.eventListeners.get(i)).processWorldEvent(player, eventId, pos, data);
			}
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Playing level event");
			CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
			crashReportSection.add("Block coordinates", CrashReportSection.createPositionString(pos));
			crashReportSection.add("Event source", player);
			crashReportSection.add("Event type", eventId);
			crashReportSection.add("Event data", data);
			throw new CrashException(crashReport);
		}
	}

	public int getMaxBuildHeight() {
		return 256;
	}

	public int getEffectiveHeight() {
		return this.dimension.hasNoSkylight() ? 128 : 256;
	}

	public Random getStructureRandom(int x, int z, int seed) {
		long l = (long)x * 341873128712L + (long)z * 132897987541L + this.getLevelProperties().getSeed() + (long)seed;
		this.random.setSeed(l);
		return this.random;
	}

	public double getHorizonHeight() {
		return this.levelProperties.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public CrashReportSection addToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Affected level", 1);
		crashReportSection.add("Level name", this.levelProperties == null ? "????" : this.levelProperties.getLevelName());
		crashReportSection.add("All players", new CrashCallable<String>() {
			public String call() {
				return World.this.playerEntities.size() + " total; " + World.this.playerEntities;
			}
		});
		crashReportSection.add("Chunk stats", new CrashCallable<String>() {
			public String call() {
				return World.this.chunkProvider.getChunkProviderName();
			}
		});

		try {
			this.levelProperties.addToCrashReport(crashReportSection);
		} catch (Throwable var4) {
			crashReportSection.add("Level Data Unobtainable", var4);
		}

		return crashReportSection;
	}

	public void setBlockBreakingInfo(int i, BlockPos pos, int j) {
		for (int k = 0; k < this.eventListeners.size(); k++) {
			WorldEventListener worldEventListener = (WorldEventListener)this.eventListeners.get(k);
			worldEventListener.setBlockBreakInfo(i, pos, j);
		}
	}

	public Calendar getCalenderInstance() {
		if (this.getLastUpdateTime() % 600L == 0L) {
			this.calender.setTimeInMillis(MinecraftServer.getTimeMillis());
		}

		return this.calender;
	}

	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public void updateHorizontalAdjacent(BlockPos pos, Block block) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (this.blockExists(blockPos)) {
				BlockState blockState = this.getBlockState(blockPos);
				if (Blocks.UNPOWERED_COMPARATOR.method_11603(blockState)) {
					blockState.neighbourUpdate(this, blockPos, block, pos);
				} else if (blockState.method_11734()) {
					blockPos = blockPos.offset(direction);
					blockState = this.getBlockState(blockPos);
					if (Blocks.UNPOWERED_COMPARATOR.method_11603(blockState)) {
						blockState.neighbourUpdate(this, blockPos, block, pos);
					}
				}
			}
		}
	}

	public LocalDifficulty getLocalDifficulty(BlockPos pos) {
		long l = 0L;
		float f = 0.0F;
		if (this.blockExists(pos)) {
			f = this.getMoonSize();
			l = this.getChunk(pos).getInhabitedTime();
		}

		return new LocalDifficulty(this.getGlobalDifficulty(), this.getTimeOfDay(), l, f);
	}

	public Difficulty getGlobalDifficulty() {
		return this.getLevelProperties().getDifficulty();
	}

	public int getAmbientDarkness() {
		return this.ambientDarkness;
	}

	public void setAmbientDarkness(int ambientDarkness) {
		this.ambientDarkness = ambientDarkness;
	}

	public int getLightningTicksLeft() {
		return this.field_4553;
	}

	public void setLightningTicksLeft(int i) {
		this.field_4553 = i;
	}

	public VillageState getVillageState() {
		return this.villageState;
	}

	public WorldBorder getWorldBorder() {
		return this.border;
	}

	public boolean isChunkInsideSpawnChunks(int chunkX, int chunkZ) {
		BlockPos blockPos = this.getSpawnPos();
		int i = chunkX * 16 + 8 - blockPos.getX();
		int j = chunkZ * 16 + 8 - blockPos.getZ();
		int k = 128;
		return i >= -128 && i <= 128 && j >= -128 && j <= 128;
	}

	public void method_11483(Packet<?> packet) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	public class_2787 method_11487() {
		return this.field_12435;
	}

	@Nullable
	public BlockPos method_13688(String string, BlockPos blockPos, boolean bl) {
		return null;
	}
}
