package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SnowLayerBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ScheduledTick;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.VillageState;
import net.minecraft.world.biome.Biome;
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
	private long cloudColor = 16777215L;
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
	protected List<WorldEventListener> eventListeners = Lists.newArrayList();
	protected ChunkProvider chunkProvider;
	protected final SaveHandler saveHandler;
	protected LevelProperties levelProperties;
	protected boolean field_4523;
	protected PersistentStateManager persistentStateManager;
	protected VillageState villageState;
	public final Profiler profiler;
	private final Calendar calender = Calendar.getInstance();
	protected Scoreboard scoreboard = new Scoreboard();
	public final boolean isClient;
	protected Set<ChunkPos> field_4530 = Sets.newHashSet();
	private int field_4534 = this.random.nextInt(12000);
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
				return chunk.getBiomeAt(pos, this.dimension.getBiomeSource());
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Getting biome");
				CrashReportSection crashReportSection = crashReport.addElement("Coordinates of biome request");
				crashReportSection.add("Location", new Callable<String>() {
					public String call() throws Exception {
						return CrashReportSection.addBlockData(pos);
					}
				});
				throw new CrashException(crashReport);
			}
		} else {
			return this.dimension.getBiomeSource().getBiomeAt(pos, Biome.PLAINS);
		}
	}

	public LayeredBiomeSource getBiomeSource() {
		return this.dimension.getBiomeSource();
	}

	protected abstract ChunkProvider getChunkCache();

	public void setPropertiesInitialized(LevelInfo info) {
		this.levelProperties.setInitialized(true);
	}

	public void setDefaultSpawnClient() {
		this.setSpawnPos(new BlockPos(8, 64, 8));
	}

	public Block getBlockAt(BlockPos pos) {
		BlockPos blockPos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());

		while (!this.isAir(blockPos.up())) {
			blockPos = blockPos.up();
		}

		return this.getBlockState(blockPos).getBlock();
	}

	private boolean isValidPos(BlockPos pos) {
		return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 && pos.getY() >= 0 && pos.getY() < 256;
	}

	@Override
	public boolean isAir(BlockPos pos) {
		return this.getBlockState(pos).getBlock().getMaterial() == Material.AIR;
	}

	public boolean blockExists(BlockPos pos) {
		return this.isLoaded(pos, true);
	}

	public boolean isLoaded(BlockPos pos, boolean canBeEmpty) {
		return !this.isValidPos(pos) ? false : this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, canBeEmpty);
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

	protected boolean isChunkLoaded(int chunkX, int chunkZ, boolean canBeEmpty) {
		return this.chunkProvider.chunkExists(chunkX, chunkZ) && (canBeEmpty || !this.chunkProvider.getChunk(chunkX, chunkZ).isEmpty());
	}

	public Chunk getChunk(BlockPos pos) {
		return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public Chunk getChunk(int chunkX, int chunkZ) {
		return this.chunkProvider.getChunk(chunkX, chunkZ);
	}

	public boolean setBlockState(BlockPos pos, BlockState state, int flags) {
		if (!this.isValidPos(pos)) {
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
				Block block2 = blockState.getBlock();
				if (block.getOpacity() != block2.getOpacity() || block.getLightLevel() != block2.getLightLevel()) {
					this.profiler.push("checkLight");
					this.method_8568(pos);
					this.profiler.pop();
				}

				if ((flags & 2) != 0 && (!this.isClient || (flags & 4) == 0) && chunk.isPopulated()) {
					this.onBlockUpdate(pos);
				}

				if (!this.isClient && (flags & 1) != 0) {
					this.updateNeighbors(pos, blockState.getBlock());
					if (block.hasComparatorOutput()) {
						this.updateHorizontalAdjacent(pos, block);
					}
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
		if (block.getMaterial() == Material.AIR) {
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

	public void onBlockUpdate(BlockPos pos) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).onBlockUpdate(pos);
		}
	}

	public void updateNeighbors(BlockPos pos, Block block) {
		if (this.levelProperties.getGeneratorType() != LevelGeneratorType.DEBUG) {
			this.updateNeighborsAlways(pos, block);
		}
	}

	public void method_3704(int x, int z, int minY, int maxY) {
		if (minY > maxY) {
			int i = maxY;
			maxY = minY;
			minY = i;
		}

		if (!this.dimension.hasNoSkylight()) {
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

	public void updateNeighborsAlways(BlockPos pos, Block block) {
		this.neighbourUpdate(pos.west(), block);
		this.neighbourUpdate(pos.east(), block);
		this.neighbourUpdate(pos.down(), block);
		this.neighbourUpdate(pos.up(), block);
		this.neighbourUpdate(pos.north(), block);
		this.neighbourUpdate(pos.south(), block);
	}

	public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction dir) {
		if (dir != Direction.WEST) {
			this.neighbourUpdate(pos.west(), sourceBlock);
		}

		if (dir != Direction.EAST) {
			this.neighbourUpdate(pos.east(), sourceBlock);
		}

		if (dir != Direction.DOWN) {
			this.neighbourUpdate(pos.down(), sourceBlock);
		}

		if (dir != Direction.UP) {
			this.neighbourUpdate(pos.up(), sourceBlock);
		}

		if (dir != Direction.NORTH) {
			this.neighbourUpdate(pos.north(), sourceBlock);
		}

		if (dir != Direction.SOUTH) {
			this.neighbourUpdate(pos.south(), sourceBlock);
		}
	}

	public void neighbourUpdate(BlockPos pos, Block block) {
		if (!this.isClient) {
			BlockState blockState = this.getBlockState(pos);

			try {
				blockState.getBlock().neighborUpdate(this, pos, blockState, block);
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", new Callable<String>() {
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
					Block block = this.getBlockState(var4).getBlock();
					if (block.getOpacity() > 0 && !block.getMaterial().isFluid()) {
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
		} else if (checkNeighbours && this.getBlockState(pos).getBlock().usesNeighbourLight()) {
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
		int j;
		if (pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000) {
			if (this.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4, true)) {
				j = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4).getHighestBlockY(pos.getX() & 15, pos.getZ() & 15);
			} else {
				j = 0;
			}
		} else {
			j = this.getSeaLevel() + 1;
		}

		return new BlockPos(pos.getX(), j, pos.getZ());
	}

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
		if (this.dimension.hasNoSkylight() && lightType == LightType.SKY) {
			return 0;
		} else {
			if (pos.getY() < 0) {
				pos = new BlockPos(pos.getX(), 0, pos.getZ());
			}

			if (!this.isValidPos(pos)) {
				return lightType.defaultValue;
			} else if (!this.blockExists(pos)) {
				return lightType.defaultValue;
			} else if (this.getBlockState(pos).getBlock().usesNeighbourLight()) {
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

		if (!this.isValidPos(pos)) {
			return lightType.defaultValue;
		} else if (!this.blockExists(pos)) {
			return lightType.defaultValue;
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getLightAtPos(lightType, pos);
		}
	}

	public void method_8491(LightType lightType, BlockPos pos, int lightLevel) {
		if (this.isValidPos(pos)) {
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
		if (!this.isValidPos(pos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.method_9154(pos);
		}
	}

	public boolean isDay() {
		return this.ambientDarkness < 4;
	}

	public BlockHitResult rayTrace(Vec3d start, Vec3d end) {
		return this.rayTrace(start, end, false, false, false);
	}

	public BlockHitResult rayTrace(Vec3d start, Vec3d end, boolean bl) {
		return this.rayTrace(start, end, bl, false, false);
	}

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
			if ((!bl2 || block.getCollisionBox(this, blockPos, blockState) != null) && block.canCollide(blockState, bl)) {
				BlockHitResult blockHitResult = block.rayTrace(this, blockPos, start, end);
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
				if (!bl2 || block2.getCollisionBox(this, blockPos, blockState2) != null) {
					if (block2.canCollide(blockState2, bl)) {
						BlockHitResult blockHitResult3 = block2.rayTrace(this, blockPos, start, end);
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

	public void playSound(Entity entity, String sound, float volume, float pitch) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).playSound(sound, entity.x, entity.y, entity.z, volume, pitch);
		}
	}

	public void playSound(PlayerEntity player, String sound, float volume, float pitch) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).playSound(player, sound, player.x, player.y, player.z, volume, pitch);
		}
	}

	public void playSound(double x, double y, double z, String sound, float volume, float pitch) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).playSound(sound, x, y, z, volume, pitch);
		}
	}

	public void playSound(double x, double y, double z, String sound, float volume, float pitch, boolean useDistance) {
	}

	public void playMusicDisc(BlockPos pos, String id) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).playMusicDisc(id, pos);
		}
	}

	public void addParticle(ParticleType type, double d, double e, double f, double g, double h, double i, int... is) {
		this.addParticle(type.getId(), type.getAlwaysShow(), d, e, f, g, h, i, is);
	}

	public void addParticle(ParticleType type, boolean bl, double d, double e, double f, double g, double h, double i, int... is) {
		this.addParticle(type.getId(), type.getAlwaysShow() | bl, d, e, f, g, h, i, is);
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

		if (!bl && !this.isChunkLoaded(i, j, true)) {
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
		if (entity.rider != null) {
			entity.rider.startRiding(null);
		}

		if (entity.vehicle != null) {
			entity.startRiding(null);
		}

		entity.remove();
		if (entity instanceof PlayerEntity) {
			this.playerEntities.remove(entity);
			this.updateSleepingStatus();
			this.onEntityRemoved(entity);
		}
	}

	public void method_3700(Entity entity) {
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

	public List<Box> doesBoxCollide(Entity entity, Box box) {
		List<Box> list = Lists.newArrayList();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX + 1.0);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY + 1.0);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ + 1.0);
		WorldBorder worldBorder = this.getWorldBorder();
		boolean bl = entity.isOutsideWorldBorder();
		boolean bl2 = this.isInsideWorld(worldBorder, entity);
		BlockState blockState = Blocks.STONE.getDefaultState();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = m; p < n; p++) {
				if (this.blockExists(mutable.setPosition(o, 64, p))) {
					for (int q = k - 1; q < l; q++) {
						mutable.setPosition(o, q, p);
						if (bl && bl2) {
							entity.setOutsideWorldBorder(false);
						} else if (!bl && !bl2) {
							entity.setOutsideWorldBorder(true);
						}

						BlockState blockState2 = blockState;
						if (worldBorder.contains(mutable) || !bl2) {
							blockState2 = this.getBlockState(mutable);
						}

						blockState2.getBlock().appendCollisionBoxes(this, mutable, blockState2, box, list, entity);
					}
				}
			}
		}

		double d = 0.25;
		List<Entity> list2 = this.getEntitiesIn(entity, box.expand(d, d, d));

		for (int r = 0; r < list2.size(); r++) {
			if (entity.rider != list2 && entity.vehicle != list2) {
				Box box2 = ((Entity)list2.get(r)).getBox();
				if (box2 != null && box2.intersects(box)) {
					list.add(box2);
				}

				box2 = entity.getHardCollisionBox((Entity)list2.get(r));
				if (box2 != null && box2.intersects(box)) {
					list.add(box2);
				}
			}
		}

		return list;
	}

	public boolean isInsideWorld(WorldBorder border, Entity entity) {
		double d = border.getBoundWest();
		double e = border.getBoundNorth();
		double f = border.getBoundEast();
		double g = border.getBoundSouth();
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

	public List<Box> method_3608(Box box) {
		List<Box> list = Lists.newArrayList();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX + 1.0);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY + 1.0);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ + 1.0);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = m; p < n; p++) {
				if (this.blockExists(mutable.setPosition(o, 64, p))) {
					for (int q = k - 1; q < l; q++) {
						mutable.setPosition(o, q, p);
						BlockState blockState2;
						if (o >= -30000000 && o < 30000000 && p >= -30000000 && p < 30000000) {
							blockState2 = this.getBlockState(mutable);
						} else {
							blockState2 = Blocks.BEDROCK.getDefaultState();
						}

						blockState2.getBlock().appendCollisionBoxes(this, mutable, blockState2, box, list, null);
					}
				}
			}
		}

		return list;
	}

	public int method_3597(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		h = 1.0F - h;
		return (int)(h * 11.0F);
	}

	public float method_3649(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	public Vec3d method_3631(Entity entity, float f) {
		float g = this.getSkyAngle(f);
		float h = MathHelper.cos(g * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
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
		return f * (float) Math.PI * 2.0F;
	}

	public Vec3d getCloudColor(float tickDelta) {
		float f = this.getSkyAngle(tickDelta);
		float g = MathHelper.cos(f * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		float h = (float)(this.cloudColor >> 16 & 255L) / 255.0F;
		float i = (float)(this.cloudColor >> 8 & 255L) / 255.0F;
		float j = (float)(this.cloudColor & 255L) / 255.0F;
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
			Material material = chunk.getBlockAtPos(blockPos2).getMaterial();
			if (material.blocksMovement() && material != Material.FOLIAGE) {
				break;
			}

			blockPos = blockPos2;
		}

		return blockPos;
	}

	public float method_3707(float f) {
		float g = this.getSkyAngle(f);
		float h = 1.0F - (MathHelper.cos(g * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
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
		this.profiler.swap("regular");

		for (int n = 0; n < this.loadedEntities.size(); n++) {
			Entity entity3 = (Entity)this.loadedEntities.get(n);
			if (entity3.vehicle != null) {
				if (!entity3.vehicle.removed && entity3.vehicle.rider == entity3) {
					continue;
				}

				entity3.vehicle.rider = null;
				entity3.vehicle = null;
			}

			this.profiler.push("tick");
			if (!entity3.removed) {
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
		this.iteratingTickingBlockEntities = true;
		Iterator<BlockEntity> iterator = this.tickingBlockEntities.iterator();

		while (iterator.hasNext()) {
			BlockEntity blockEntity = (BlockEntity)iterator.next();
			if (!blockEntity.isRemoved() && blockEntity.hasWorld()) {
				BlockPos blockPos = blockEntity.getPos();
				if (this.blockExists(blockPos) && this.border.contains(blockPos)) {
					try {
						((Tickable)blockEntity).tick();
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
		if (!this.unloadedBlockEntities.isEmpty()) {
			this.tickingBlockEntities.removeAll(this.unloadedBlockEntities);
			this.blockEntities.removeAll(this.unloadedBlockEntities);
			this.unloadedBlockEntities.clear();
		}

		this.profiler.swap("pendingBlockEntities");
		if (!this.pendingBlockEntities.isEmpty()) {
			for (int q = 0; q < this.pendingBlockEntities.size(); q++) {
				BlockEntity blockEntity2 = (BlockEntity)this.pendingBlockEntities.get(q);
				if (!blockEntity2.isRemoved()) {
					if (!this.blockEntities.contains(blockEntity2)) {
						this.addBlockEntity(blockEntity2);
					}

					if (this.blockExists(blockEntity2.getPos())) {
						this.getChunk(blockEntity2.getPos()).method_9136(blockEntity2.getPos(), blockEntity2);
					}

					this.onBlockUpdate(blockEntity2.getPos());
				}
			}

			this.pendingBlockEntities.clear();
		}

		this.profiler.pop();
		this.profiler.pop();
	}

	public boolean addBlockEntity(BlockEntity blockEntity) {
		boolean bl = this.blockEntities.add(blockEntity);
		if (bl && blockEntity instanceof Tickable) {
			this.tickingBlockEntities.add(blockEntity);
		}

		return bl;
	}

	public void addBlockEntities(Collection<BlockEntity> collection) {
		if (this.iteratingTickingBlockEntities) {
			this.pendingBlockEntities.addAll(collection);
		} else {
			for (BlockEntity blockEntity : collection) {
				this.blockEntities.add(blockEntity);
				if (blockEntity instanceof Tickable) {
					this.tickingBlockEntities.add(blockEntity);
				}
			}
		}
	}

	public void checkChunk(Entity entity) {
		this.checkChunk(entity, true);
	}

	public void checkChunk(Entity entity, boolean bl) {
		int i = MathHelper.floor(entity.x);
		int j = MathHelper.floor(entity.z);
		int k = 32;
		if (!bl || this.isRegionLoaded(i - k, 0, j - k, i + k, 0, j + k, true)) {
			entity.prevTickX = entity.x;
			entity.prevTickY = entity.y;
			entity.prevTickZ = entity.z;
			entity.prevYaw = entity.yaw;
			entity.prevPitch = entity.pitch;
			if (bl && entity.updateNeeded) {
				entity.ticksAlive++;
				if (entity.vehicle != null) {
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

				if (this.isChunkLoaded(l, n, true)) {
					entity.updateNeeded = true;
					this.getChunk(l, n).addEntity(entity);
				} else {
					entity.updateNeeded = false;
				}
			}

			this.profiler.pop();
			if (bl && entity.updateNeeded && entity.rider != null) {
				if (!entity.rider.removed && entity.rider.vehicle == entity) {
					this.checkChunk(entity.rider);
				} else {
					entity.rider.vehicle = null;
					entity.rider = null;
				}
			}
		}
	}

	public boolean hasEntityIn(Box box) {
		return this.hasEntityIn(box, null);
	}

	public boolean hasEntityIn(Box box, Entity except) {
		List<Entity> list = this.getEntitiesIn(null, box);

		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (!entity.removed && entity.inanimate && entity != except && (except == null || except.vehicle != entity && except.rider != entity)) {
				return false;
			}
		}

		return true;
	}

	public boolean isBoxNotEmpty(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= j; o++) {
			for (int p = k; p <= l; p++) {
				for (int q = m; q <= n; q++) {
					Block block = this.getBlockState(mutable.setPosition(o, p, q)).getBlock();
					if (block.getMaterial() != Material.AIR) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean containsFluid(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o <= j; o++) {
			for (int p = k; p <= l; p++) {
				for (int q = m; q <= n; q++) {
					Block block = this.getBlockState(mutable.setPosition(o, p, q)).getBlock();
					if (block.getMaterial().isFluid()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean containsFireSource(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX + 1.0);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY + 1.0);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ + 1.0);
		if (this.isRegionLoaded(i, k, m, j, l, n, true)) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						Block block = this.getBlockState(mutable.setPosition(o, p, q)).getBlock();
						if (block == Blocks.FIRE || block == Blocks.FLOWING_LAVA || block == Blocks.LAVA) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean method_3610(Box box, Material material, Entity entity) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX + 1.0);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY + 1.0);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ + 1.0);
		if (!this.isRegionLoaded(i, k, m, j, l, n, true)) {
			return false;
		} else {
			boolean bl = false;
			Vec3d vec3d = new Vec3d(0.0, 0.0, 0.0);
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						mutable.setPosition(o, p, q);
						BlockState blockState = this.getBlockState(mutable);
						Block block = blockState.getBlock();
						if (block.getMaterial() == material) {
							double d = (double)((float)(p + 1) - AbstractFluidBlock.getHeightPercent((Integer)blockState.get(AbstractFluidBlock.LEVEL)));
							if ((double)l >= d) {
								bl = true;
								vec3d = block.onEntityCollision(this, mutable, entity, vec3d);
							}
						}
					}
				}
			}

			if (vec3d.length() > 0.0 && entity.canFly()) {
				vec3d = vec3d.normalize();
				double e = 0.014;
				entity.velocityX = entity.velocityX + vec3d.x * e;
				entity.velocityY = entity.velocityY + vec3d.y * e;
				entity.velocityZ = entity.velocityZ + vec3d.z * e;
			}

			return bl;
		}
	}

	public boolean containsMaterial(Box box, Material material) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX + 1.0);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY + 1.0);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ + 1.0);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					if (this.getBlockState(mutable.setPosition(o, p, q)).getBlock().getMaterial() == material) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean containsBlockWithMaterial(Box box, Material material) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.maxX + 1.0);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.floor(box.maxY + 1.0);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.floor(box.maxZ + 1.0);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					BlockState blockState = this.getBlockState(mutable.setPosition(o, p, q));
					Block block = blockState.getBlock();
					if (block.getMaterial() == material) {
						int r = (Integer)blockState.get(AbstractFluidBlock.LEVEL);
						double d = (double)(p + 1);
						if (r < 8) {
							d = (double)(p + 1) - (double)r / 8.0;
						}

						if (d >= box.minY) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public Explosion createExplosion(Entity entity, double x, double y, double z, float power, boolean destructive) {
		return this.createExplosion(entity, x, y, z, power, false, destructive);
	}

	public Explosion createExplosion(Entity entity, double x, double y, double z, float power, boolean createFire, boolean destructive) {
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

	public boolean extinguishFire(PlayerEntity player, BlockPos pos, Direction direction) {
		pos = pos.offset(direction);
		if (this.getBlockState(pos).getBlock() == Blocks.FIRE) {
			this.syncWorldEvent(player, 1004, pos, 0);
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

	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		if (!this.isValidPos(pos)) {
			return null;
		} else {
			BlockEntity blockEntity = null;
			if (this.iteratingTickingBlockEntities) {
				for (int i = 0; i < this.pendingBlockEntities.size(); i++) {
					BlockEntity blockEntity2 = (BlockEntity)this.pendingBlockEntities.get(i);
					if (!blockEntity2.isRemoved() && blockEntity2.getPos().equals(pos)) {
						blockEntity = blockEntity2;
						break;
					}
				}
			}

			if (blockEntity == null) {
				blockEntity = this.getChunk(pos).getBlockEntity(pos, Chunk.Status.IMMEDIATE);
			}

			if (blockEntity == null) {
				for (int j = 0; j < this.pendingBlockEntities.size(); j++) {
					BlockEntity blockEntity3 = (BlockEntity)this.pendingBlockEntities.get(j);
					if (!blockEntity3.isRemoved() && blockEntity3.getPos().equals(pos)) {
						blockEntity = blockEntity3;
						break;
					}
				}
			}

			return blockEntity;
		}
	}

	public void setBlockEntity(BlockPos pos, BlockEntity blockEntity) {
		if (blockEntity != null && !blockEntity.isRemoved()) {
			if (this.iteratingTickingBlockEntities) {
				blockEntity.setPos(pos);
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
				this.addBlockEntity(blockEntity);
				this.getChunk(pos).method_9136(pos, blockEntity);
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

	public boolean method_8565(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);
		Box box = blockState.getBlock().getCollisionBox(this, pos, blockState);
		return box != null && box.getAverage() >= 1.0;
	}

	public static boolean isOpaque(BlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block.getMaterial().isOpaque() && block.renderAsNormalBlock()) {
			return true;
		} else if (block instanceof StairsBlock) {
			return blockState.get(StairsBlock.HALF) == StairsBlock.Half.TOP;
		} else if (block instanceof SlabBlock) {
			return blockState.get(SlabBlock.HALF) == SlabBlock.SlabType.TOP;
		} else if (block instanceof HopperBlock) {
			return true;
		} else {
			return block instanceof SnowLayerBlock ? (Integer)blockState.get(SnowLayerBlock.LAYERS) == 7 : false;
		}
	}

	public boolean renderAsNormalBlock(BlockPos pos, boolean defaultValue) {
		if (!this.isValidPos(pos)) {
			return defaultValue;
		} else {
			Chunk chunk = this.chunkProvider.getChunk(pos);
			if (chunk.isEmpty()) {
				return defaultValue;
			} else {
				Block block = this.getBlockState(pos).getBlock();
				return block.getMaterial().isOpaque() && block.renderAsNormalBlock();
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
		if (!this.dimension.hasNoSkylight()) {
			if (!this.isClient) {
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

				this.thunderGradientPrev = this.thunderGradient;
				if (this.levelProperties.isThundering()) {
					this.thunderGradient = (float)((double)this.thunderGradient + 0.01);
				} else {
					this.thunderGradient = (float)((double)this.thunderGradient - 0.01);
				}

				this.thunderGradient = MathHelper.clamp(this.thunderGradient, 0.0F, 1.0F);
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

	protected void updateLighting() {
		this.field_4530.clear();
		this.profiler.push("buildList");

		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.playerEntities.get(i);
			int j = MathHelper.floor(playerEntity.x / 16.0);
			int k = MathHelper.floor(playerEntity.z / 16.0);
			int l = this.getNextMapId();

			for (int m = -l; m <= l; m++) {
				for (int n = -l; n <= l; n++) {
					this.field_4530.add(new ChunkPos(m + j, n + k));
				}
			}
		}

		this.profiler.pop();
		if (this.field_4534 > 0) {
			this.field_4534--;
		}

		this.profiler.push("playerCheckLight");
		if (!this.playerEntities.isEmpty()) {
			int o = this.random.nextInt(this.playerEntities.size());
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(o);
			int p = MathHelper.floor(playerEntity2.x) + this.random.nextInt(11) - 5;
			int q = MathHelper.floor(playerEntity2.y) + this.random.nextInt(11) - 5;
			int r = MathHelper.floor(playerEntity2.z) + this.random.nextInt(11) - 5;
			this.method_8568(new BlockPos(p, q, r));
		}

		this.profiler.pop();
	}

	protected abstract int getNextMapId();

	protected void method_3605(int i, int j, Chunk chunk) {
		this.profiler.swap("moodSound");
		if (this.field_4534 == 0 && !this.isClient) {
			this.lcgBlockSeed = this.lcgBlockSeed * 3 + 1013904223;
			int k = this.lcgBlockSeed >> 2;
			int l = k & 15;
			int m = k >> 8 & 15;
			int n = k >> 16 & 0xFF;
			BlockPos blockPos = new BlockPos(l, n, m);
			Block block = chunk.getBlockAtPos(blockPos);
			l += i;
			m += j;
			if (block.getMaterial() == Material.AIR && this.getLightLevel(blockPos) <= this.random.nextInt(8) && this.getLightAtPos(LightType.SKY, blockPos) <= 0) {
				PlayerEntity playerEntity = this.getClosestPlayer((double)l + 0.5, (double)n + 0.5, (double)m + 0.5, 8.0);
				if (playerEntity != null && playerEntity.squaredDistanceTo((double)l + 0.5, (double)n + 0.5, (double)m + 0.5) > 4.0) {
					this.playSound((double)l + 0.5, (double)n + 0.5, (double)m + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.random.nextFloat() * 0.2F);
					this.field_4534 = this.random.nextInt(12000) + 6000;
				}
			}
		}

		this.profiler.swap("checkLight");
		chunk.method_3923();
	}

	protected void tickBlocks() {
		this.updateLighting();
	}

	public void scheduleTick(Block block, BlockPos pos, Random random) {
		this.immediateUpdates = true;
		block.onScheduledTick(this, pos, this.getBlockState(pos), random);
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
		if (f > 0.15F) {
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
		return this.getBlockState(pos).getBlock().getMaterial() == Material.WATER;
	}

	public boolean method_8552(BlockPos pos, boolean bl) {
		Biome biome = this.getBiome(pos);
		float f = biome.getTemperature(pos);
		if (f > 0.15F) {
			return false;
		} else if (!bl) {
			return true;
		} else {
			if (pos.getY() >= 0 && pos.getY() < 256 && this.getLightAtPos(LightType.BLOCK, pos) < 10) {
				Block block = this.getBlockState(pos).getBlock();
				if (block.getMaterial() == Material.AIR && Blocks.SNOW_LAYER.canBePlacedAtPos(this, pos)) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean method_8568(BlockPos pos) {
		boolean bl = false;
		if (!this.dimension.hasNoSkylight()) {
			bl |= this.calculateLightAtPos(LightType.SKY, pos);
		}

		return bl | this.calculateLightAtPos(LightType.BLOCK, pos);
	}

	private int getLightAtPos(BlockPos pos, LightType type) {
		if (type == LightType.SKY && this.hasDirectSunlight(pos)) {
			return 15;
		} else {
			Block block = this.getBlockState(pos).getBlock();
			int i = type == LightType.SKY ? 0 : block.getLightLevel();
			int j = block.getOpacity();
			if (j >= 15 && block.getLightLevel() > 0) {
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
				for (Direction direction : Direction.values()) {
					BlockPos blockPos = pos.offset(direction);
					int m = this.getLightAtPos(type, blockPos) - j;
					if (m > i) {
						i = m;
					}

					if (i >= 14) {
						return i;
					}
				}

				return i;
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
								BlockPos.Mutable mutable = new BlockPos.Mutable();

								for (Direction direction : Direction.values()) {
									int aa = q + direction.getOffsetX();
									int ab = r + direction.getOffsetY();
									int ac = s + direction.getOffsetZ();
									mutable.setPosition(aa, ab, ac);
									int ad = Math.max(1, this.getBlockState(mutable).getBlock().getOpacity());
									u = this.getLightAtPos(lightType, mutable);
									if (u == t - ad && j < this.updateLightBlocks.length) {
										this.updateLightBlocks[j++] = aa - m + 32 | ab - n + 32 << 6 | ac - o + 32 << 12 | t - ad << 18;
									}
								}
							}
						}
					}
				}

				i = 0;
			}

			this.profiler.pop();
			this.profiler.push("checkedPosition < toCheckCount");

			while (i < j) {
				int ae = this.updateLightBlocks[i++];
				int af = (ae & 63) - 32 + m;
				int ag = (ae >> 6 & 63) - 32 + n;
				int ah = (ae >> 12 & 63) - 32 + o;
				BlockPos blockPos2 = new BlockPos(af, ag, ah);
				int ai = this.getLightAtPos(lightType, blockPos2);
				int aj = this.getLightAtPos(blockPos2, lightType);
				if (aj != ai) {
					this.method_8491(lightType, blockPos2, aj);
					if (aj > ai) {
						int ak = Math.abs(af - m);
						int al = Math.abs(ag - n);
						int am = Math.abs(ah - o);
						boolean bl = j < this.updateLightBlocks.length - 6;
						if (ak + al + am < 17 && bl) {
							if (this.getLightAtPos(lightType, blockPos2.west()) < aj) {
								this.updateLightBlocks[j++] = af - 1 - m + 32 + (ag - n + 32 << 6) + (ah - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.east()) < aj) {
								this.updateLightBlocks[j++] = af + 1 - m + 32 + (ag - n + 32 << 6) + (ah - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.down()) < aj) {
								this.updateLightBlocks[j++] = af - m + 32 + (ag - 1 - n + 32 << 6) + (ah - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.up()) < aj) {
								this.updateLightBlocks[j++] = af - m + 32 + (ag + 1 - n + 32 << 6) + (ah - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.north()) < aj) {
								this.updateLightBlocks[j++] = af - m + 32 + (ag - n + 32 << 6) + (ah - 1 - o + 32 << 12);
							}

							if (this.getLightAtPos(lightType, blockPos2.south()) < aj) {
								this.updateLightBlocks[j++] = af - m + 32 + (ag - n + 32 << 6) + (ah + 1 - o + 32 << 12);
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

	public List<ScheduledTick> getScheduledTicks(Chunk chunk, boolean bl) {
		return null;
	}

	public List<ScheduledTick> getScheduledTicks(BlockBox box, boolean bl) {
		return null;
	}

	public List<Entity> getEntitiesIn(Entity entity, Box box) {
		return this.getEntitiesIn(entity, box, EntityPredicate.EXCEPT_SPECTATOR);
	}

	public List<Entity> getEntitiesIn(Entity entity, Box box, Predicate<? super Entity> predicate) {
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

	public <T extends Entity> List<T> getEntitiesInBox(Class<? extends T> clazz, Box box, Predicate<? super T> entityPredicate) {
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);
		List<T> list = Lists.newArrayList();

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (this.isChunkLoaded(m, n, true)) {
					this.getChunk(m, n).method_9140(clazz, box, list, entityPredicate);
				}
			}
		}

		return list;
	}

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

	public boolean canBlockBePlaced(Block block, BlockPos pos, boolean bl, Direction direction, Entity entity, ItemStack item) {
		Block block2 = this.getBlockState(pos).getBlock();
		Box box = bl ? null : block.getCollisionBox(this, pos, block.getDefaultState());
		if (box != null && !this.hasEntityIn(box, entity)) {
			return false;
		} else {
			return block2.getMaterial() == Material.DECORATION && block == Blocks.ANVIL
				? true
				: block2.getMaterial().isReplaceable() && block.canBeReplaced(this, pos, direction, item);
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
		BlockState blockState = this.getBlockState(pos);
		return blockState.getBlock().getStrongRedstonePower(this, pos, blockState, direction);
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
		Block block = blockState.getBlock();
		return block.isFullCube() ? this.getReceivedStrongRedstonePower(pos) : block.getWeakRedstonePower(this, pos, blockState, dir);
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
			int l = this.getEmittedRedstonePower(pos.offset(direction), direction);
			if (l >= 15) {
				return 15;
			}

			if (l > i) {
				i = l;
			}
		}

		return i;
	}

	public PlayerEntity getClosestPlayer(Entity entity, double maxDistance) {
		return this.getClosestPlayer(entity.x, entity.y, entity.z, maxDistance);
	}

	public PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance) {
		double d = -1.0;
		PlayerEntity playerEntity = null;

		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(i);
			if (EntityPredicate.EXCEPT_SPECTATOR.apply(playerEntity2)) {
				double e = playerEntity2.squaredDistanceTo(x, y, z);
				if ((maxDistance < 0.0 || e < maxDistance * maxDistance) && (d == -1.0 || e < d)) {
					d = e;
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

	public PlayerEntity getPlayerByName(String name) {
		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity = (PlayerEntity)this.playerEntities.get(i);
			if (name.equals(playerEntity.getTranslationKey())) {
				return playerEntity;
			}
		}

		return null;
	}

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

		for (int l = i - k; l <= i + k; l++) {
			for (int m = j - k; m <= j + k; m++) {
				this.getChunk(l, m);
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
		block.onEvent(this, pos, this.getBlockState(pos), type, data);
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

	public PersistentStateManager getPersistentStateManager() {
		return this.persistentStateManager;
	}

	public void replaceState(String name, PersistentState state) {
		this.persistentStateManager.replace(name, state);
	}

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

	public void syncWorldEvent(PlayerEntity player, int eventId, BlockPos pos, int data) {
		try {
			for (int i = 0; i < this.eventListeners.size(); i++) {
				((WorldEventListener)this.eventListeners.get(i)).processWorldEvent(player, eventId, pos, data);
			}
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Playing level event");
			CrashReportSection crashReportSection = crashReport.addElement("Level event being played");
			crashReportSection.add("Block coordinates", CrashReportSection.addBlockData(pos));
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

	public BlockPos getNearestStructurePos(String structureName, BlockPos pos) {
		return this.getChunkProvider().getNearestStructurePos(this, structureName, pos);
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	public double getHorizonHeight() {
		return this.levelProperties.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public CrashReportSection addToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Affected level", 1);
		crashReportSection.add("Level name", this.levelProperties == null ? "????" : this.levelProperties.getLevelName());
		crashReportSection.add("All players", new Callable<String>() {
			public String call() {
				return World.this.playerEntities.size() + " total; " + World.this.playerEntities.toString();
			}
		});
		crashReportSection.add("Chunk stats", new Callable<String>() {
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

	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, NbtCompound nbt) {
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public void updateHorizontalAdjacent(BlockPos pos, Block block) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (this.blockExists(blockPos)) {
				BlockState blockState = this.getBlockState(blockPos);
				if (Blocks.UNPOWERED_COMPARATOR.isComparator(blockState.getBlock())) {
					blockState.getBlock().neighborUpdate(this, blockPos, blockState, block);
				} else if (blockState.getBlock().isFullCube()) {
					blockPos = blockPos.offset(direction);
					blockState = this.getBlockState(blockPos);
					if (Blocks.UNPOWERED_COMPARATOR.isComparator(blockState.getBlock())) {
						blockState.getBlock().neighborUpdate(this, blockPos, blockState, block);
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

	public boolean method_8522() {
		return this.field_4523;
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
		return i >= -k && i <= k && j >= -k && j <= k;
	}
}
