package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.SingletonBiomeSource;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ChunkSection EMPTY = null;
	private final ChunkSection[] chunkSections = new ChunkSection[16];
	private final byte[] biomeArray = new byte[256];
	private final int[] surfaceCache = new int[256];
	private final boolean[] columnSkyLightOutdated = new boolean[256];
	private boolean loaded;
	private final World world;
	private final int[] heightmap;
	public final int chunkX;
	public final int chunkZ;
	private boolean isSkyLightOutdated;
	private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
	private final TypeFilterableList<Entity>[] entities;
	private boolean terrainPopulated;
	private boolean lightPopulated;
	private boolean blockEntitiesPopulated;
	private boolean modified;
	private boolean containsEntities;
	private long lastSaveTime;
	private int minimumHeightmap;
	private long inhabitedTime;
	private int field_4743 = 4096;
	private final ConcurrentLinkedQueue<BlockPos> blocks = Queues.newConcurrentLinkedQueue();
	public boolean unloaded;

	public Chunk(World world, int i, int j) {
		this.entities = new TypeFilterableList[16];
		this.world = world;
		this.chunkX = i;
		this.chunkZ = j;
		this.heightmap = new int[256];

		for (int k = 0; k < this.entities.length; k++) {
			this.entities[k] = new TypeFilterableList<>(Entity.class);
		}

		Arrays.fill(this.surfaceCache, -999);
		Arrays.fill(this.biomeArray, (byte)-1);
	}

	public Chunk(World world, ChunkBlockStateStorage chunkBlockStateStorage, int i, int j) {
		this(world, i, j);
		int k = 256;
		boolean bl = world.dimension.isOverworld();

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 256; n++) {
					BlockState blockState = chunkBlockStateStorage.get(l, n, m);
					if (blockState.getMaterial() != Material.AIR) {
						int o = n >> 4;
						if (this.chunkSections[o] == EMPTY) {
							this.chunkSections[o] = new ChunkSection(o << 4, bl);
						}

						this.chunkSections[o].setBlockState(l, n & 15, m, blockState);
					}
				}
			}
		}
	}

	public boolean isChunkEqual(int chunkX, int chunkZ) {
		return chunkX == this.chunkX && chunkZ == this.chunkZ;
	}

	public int getHighestBlockY(BlockPos pos) {
		return this.getHighestBlockY(pos.getX() & 15, pos.getZ() & 15);
	}

	public int getHighestBlockY(int x, int z) {
		return this.heightmap[z << 4 | x];
	}

	@Nullable
	private ChunkSection getHighestNonEmptySection() {
		for (int i = this.chunkSections.length - 1; i >= 0; i--) {
			if (this.chunkSections[i] != EMPTY) {
				return this.chunkSections[i];
			}
		}

		return null;
	}

	public int getHighestNonEmptySectionYOffset() {
		ChunkSection chunkSection = this.getHighestNonEmptySection();
		return chunkSection == null ? 0 : chunkSection.getYOffset();
	}

	public ChunkSection[] getBlockStorage() {
		return this.chunkSections;
	}

	protected void generateHeightmap() {
		int i = this.getHighestNonEmptySectionYOffset();
		this.minimumHeightmap = Integer.MAX_VALUE;

		for (int j = 0; j < 16; j++) {
			for (int k = 0; k < 16; k++) {
				this.surfaceCache[j + (k << 4)] = -999;

				for (int l = i + 16; l > 0; l--) {
					BlockState blockState = this.getBlockState(j, l - 1, k);
					if (blockState.getOpacity() != 0) {
						this.heightmap[k << 4 | j] = l;
						if (l < this.minimumHeightmap) {
							this.minimumHeightmap = l;
						}
						break;
					}
				}
			}
		}

		this.modified = true;
	}

	public void calculateSkyLight() {
		int i = this.getHighestNonEmptySectionYOffset();
		this.minimumHeightmap = Integer.MAX_VALUE;

		for (int j = 0; j < 16; j++) {
			for (int k = 0; k < 16; k++) {
				this.surfaceCache[j + (k << 4)] = -999;

				for (int l = i + 16; l > 0; l--) {
					if (this.getBlockOpacity(j, l - 1, k) != 0) {
						this.heightmap[k << 4 | j] = l;
						if (l < this.minimumHeightmap) {
							this.minimumHeightmap = l;
						}
						break;
					}
				}

				if (this.world.dimension.isOverworld()) {
					int m = 15;
					int n = i + 16 - 1;

					while (true) {
						int o = this.getBlockOpacity(j, n, k);
						if (o == 0 && m != 15) {
							o = 1;
						}

						m -= o;
						if (m > 0) {
							ChunkSection chunkSection = this.chunkSections[n >> 4];
							if (chunkSection != EMPTY) {
								chunkSection.setSkyLight(j, n & 15, k, m);
								this.world.onLightUpdate(new BlockPos((this.chunkX << 4) + j, n, (this.chunkZ << 4) + k));
							}
						}

						if (--n <= 0 || m <= 0) {
							break;
						}
					}
				}
			}
		}

		this.modified = true;
	}

	private void setColumnLightOutdated(int x, int z) {
		this.columnSkyLightOutdated[x + z * 16] = true;
		this.isSkyLightOutdated = true;
	}

	private void recheckSkyLightGaps(boolean onlyOneColumn) {
		this.world.profiler.push("recheckGaps");
		if (this.world.isRegionLoaded(new BlockPos(this.chunkX * 16 + 8, 0, this.chunkZ * 16 + 8), 16)) {
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (this.columnSkyLightOutdated[i + j * 16]) {
						this.columnSkyLightOutdated[i + j * 16] = false;
						int k = this.getHighestBlockY(i, j);
						int l = this.chunkX * 16 + i;
						int m = this.chunkZ * 16 + j;
						int n = Integer.MAX_VALUE;

						for (Direction direction : Direction.DirectionType.HORIZONTAL) {
							n = Math.min(n, this.world.getMinimumChunkHeightmap(l + direction.getOffsetX(), m + direction.getOffsetZ()));
						}

						this.calculateSkyLightForColumn(l, m, n);

						for (Direction direction2 : Direction.DirectionType.HORIZONTAL) {
							this.calculateSkyLightForColumn(l + direction2.getOffsetX(), m + direction2.getOffsetZ(), k);
						}

						if (onlyOneColumn) {
							this.world.profiler.pop();
							return;
						}
					}
				}
			}

			this.isSkyLightOutdated = false;
		}

		this.world.profiler.pop();
	}

	private void calculateSkyLightForColumn(int x, int z, int y) {
		int i = this.world.getHighestBlock(new BlockPos(x, 0, z)).getY();
		if (i > y) {
			this.calculateSkyLightForRegion(x, z, y, i + 1);
		} else if (i < y) {
			this.calculateSkyLightForRegion(x, z, i, y + 1);
		}
	}

	private void calculateSkyLightForRegion(int x, int z, int beginY, int endY) {
		if (endY > beginY && this.world.isRegionLoaded(new BlockPos(x, 0, z), 16)) {
			for (int i = beginY; i < endY; i++) {
				this.world.calculateLightAtPos(LightType.SKY, new BlockPos(x, i, z));
			}

			this.modified = true;
		}
	}

	private void lightBlock(int x, int y, int z) {
		int i = this.heightmap[z << 4 | x] & 0xFF;
		int j = i;
		if (y > i) {
			j = y;
		}

		while (j > 0 && this.getBlockOpacity(x, j - 1, z) == 0) {
			j--;
		}

		if (j != i) {
			this.world.method_3704(x + this.chunkX * 16, z + this.chunkZ * 16, j, i);
			this.heightmap[z << 4 | x] = j;
			int k = this.chunkX * 16 + x;
			int l = this.chunkZ * 16 + z;
			if (this.world.dimension.isOverworld()) {
				if (j < i) {
					for (int m = j; m < i; m++) {
						ChunkSection chunkSection = this.chunkSections[m >> 4];
						if (chunkSection != EMPTY) {
							chunkSection.setSkyLight(x, m & 15, z, 15);
							this.world.onLightUpdate(new BlockPos((this.chunkX << 4) + x, m, (this.chunkZ << 4) + z));
						}
					}
				} else {
					for (int n = i; n < j; n++) {
						ChunkSection chunkSection2 = this.chunkSections[n >> 4];
						if (chunkSection2 != EMPTY) {
							chunkSection2.setSkyLight(x, n & 15, z, 0);
							this.world.onLightUpdate(new BlockPos((this.chunkX << 4) + x, n, (this.chunkZ << 4) + z));
						}
					}
				}

				int o = 15;

				while (j > 0 && o > 0) {
					int p = this.getBlockOpacity(x, --j, z);
					if (p == 0) {
						p = 1;
					}

					o -= p;
					if (o < 0) {
						o = 0;
					}

					ChunkSection chunkSection3 = this.chunkSections[j >> 4];
					if (chunkSection3 != EMPTY) {
						chunkSection3.setSkyLight(x, j & 15, z, o);
					}
				}
			}

			int q = this.heightmap[z << 4 | x];
			int r = i;
			int s = q;
			if (q < i) {
				r = q;
				s = i;
			}

			if (q < this.minimumHeightmap) {
				this.minimumHeightmap = q;
			}

			if (this.world.dimension.isOverworld()) {
				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					this.calculateSkyLightForRegion(k + direction.getOffsetX(), l + direction.getOffsetZ(), r, s);
				}

				this.calculateSkyLightForRegion(k, l, r, s);
			}

			this.modified = true;
		}
	}

	public int getBlockOpacityAtPos(BlockPos pos) {
		return this.getBlockState(pos).getOpacity();
	}

	private int getBlockOpacity(int x, int y, int z) {
		return this.getBlockState(x, y, z).getOpacity();
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockState getBlockState(int x, int y, int z) {
		if (this.world.getGeneratorType() == LevelGeneratorType.DEBUG) {
			BlockState blockState = null;
			if (y == 60) {
				blockState = Blocks.BARRIER.getDefaultState();
			}

			if (y == 70) {
				blockState = DebugChunkGenerator.method_9190(x, z);
			}

			return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
		} else {
			try {
				if (y >= 0 && y >> 4 < this.chunkSections.length) {
					ChunkSection chunkSection = this.chunkSections[y >> 4];
					if (chunkSection != EMPTY) {
						return chunkSection.getBlockState(x & 15, y & 15, z & 15);
					}
				}

				return Blocks.AIR.getDefaultState();
			} catch (Throwable var7) {
				CrashReport crashReport = CrashReport.create(var7, "Getting block state");
				CrashReportSection crashReportSection = crashReport.addElement("Block being got");
				crashReportSection.add("Location", new CrashCallable<String>() {
					public String call() throws Exception {
						return CrashReportSection.createPositionString(x, y, z);
					}
				});
				throw new CrashException(crashReport);
			}
		}
	}

	@Nullable
	public BlockState getBlockState(BlockPos pos, BlockState state) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		int l = k << 4 | i;
		if (j >= this.surfaceCache[l] - 1) {
			this.surfaceCache[l] = -999;
		}

		int m = this.heightmap[l];
		BlockState blockState = this.getBlockState(pos);
		if (blockState == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block2 = blockState.getBlock();
			ChunkSection chunkSection = this.chunkSections[j >> 4];
			boolean bl = false;
			if (chunkSection == EMPTY) {
				if (block == Blocks.AIR) {
					return null;
				}

				chunkSection = new ChunkSection(j >> 4 << 4, this.world.dimension.isOverworld());
				this.chunkSections[j >> 4] = chunkSection;
				bl = j >= m;
			}

			chunkSection.setBlockState(i, j & 15, k, state);
			if (block2 != block) {
				if (!this.world.isClient) {
					block2.onBreaking(this.world, pos, blockState);
				} else if (block2 instanceof BlockEntityProvider) {
					this.world.removeBlockEntity(pos);
				}
			}

			if (chunkSection.getBlockState(i, j & 15, k).getBlock() != block) {
				return null;
			} else {
				if (bl) {
					this.calculateSkyLight();
				} else {
					int n = state.getOpacity();
					int o = blockState.getOpacity();
					if (n > 0) {
						if (j >= m) {
							this.lightBlock(i, j + 1, k);
						}
					} else if (j == m - 1) {
						this.lightBlock(i, j, k);
					}

					if (n != o && (n < o || this.getLightAtPos(LightType.SKY, pos) > 0 || this.getLightAtPos(LightType.BLOCK, pos) > 0)) {
						this.setColumnLightOutdated(i, k);
					}
				}

				if (block2 instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(pos, Chunk.Status.CHECK);
					if (blockEntity != null) {
						blockEntity.resetBlock();
					}
				}

				if (!this.world.isClient && block2 != block) {
					block.onCreation(this.world, pos, state);
				}

				if (block instanceof BlockEntityProvider) {
					BlockEntity blockEntity2 = this.getBlockEntity(pos, Chunk.Status.CHECK);
					if (blockEntity2 == null) {
						blockEntity2 = ((BlockEntityProvider)block).createBlockEntity(this.world, block.getData(state));
						this.world.setBlockEntity(pos, blockEntity2);
					}

					if (blockEntity2 != null) {
						blockEntity2.resetBlock();
					}
				}

				this.modified = true;
				return blockState;
			}
		}
	}

	public int getLightAtPos(LightType lightType, BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ChunkSection chunkSection = this.chunkSections[j >> 4];
		if (chunkSection == EMPTY) {
			return this.hasDirectSunlight(pos) ? lightType.defaultValue : 0;
		} else if (lightType == LightType.SKY) {
			return !this.world.dimension.isOverworld() ? 0 : chunkSection.getSkyLight(i, j & 15, k);
		} else {
			return lightType == LightType.BLOCK ? chunkSection.getBlockLight(i, j & 15, k) : lightType.defaultValue;
		}
	}

	public void setLightAtPos(LightType lightType, BlockPos pos, int lightLevel) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ChunkSection chunkSection = this.chunkSections[j >> 4];
		if (chunkSection == EMPTY) {
			chunkSection = new ChunkSection(j >> 4 << 4, this.world.dimension.isOverworld());
			this.chunkSections[j >> 4] = chunkSection;
			this.calculateSkyLight();
		}

		this.modified = true;
		if (lightType == LightType.SKY) {
			if (this.world.dimension.isOverworld()) {
				chunkSection.setSkyLight(i, j & 15, k, lightLevel);
			}
		} else if (lightType == LightType.BLOCK) {
			chunkSection.setBlockLight(i, j & 15, k, lightLevel);
		}
	}

	public int getLightLevel(BlockPos pos, int darkness) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ChunkSection chunkSection = this.chunkSections[j >> 4];
		if (chunkSection == EMPTY) {
			return this.world.dimension.isOverworld() && darkness < LightType.SKY.defaultValue ? LightType.SKY.defaultValue - darkness : 0;
		} else {
			int l = !this.world.dimension.isOverworld() ? 0 : chunkSection.getSkyLight(i, j & 15, k);
			l -= darkness;
			int m = chunkSection.getBlockLight(i, j & 15, k);
			if (m > l) {
				l = m;
			}

			return l;
		}
	}

	public void addEntity(Entity entity) {
		this.containsEntities = true;
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		if (i != this.chunkX || j != this.chunkZ) {
			LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", new Object[]{i, j, this.chunkX, this.chunkZ, entity});
			entity.remove();
		}

		int k = MathHelper.floor(entity.y / 16.0);
		if (k < 0) {
			k = 0;
		}

		if (k >= this.entities.length) {
			k = this.entities.length - 1;
		}

		entity.updateNeeded = true;
		entity.chunkX = this.chunkX;
		entity.chunkY = k;
		entity.chunkZ = this.chunkZ;
		this.entities[k].add(entity);
	}

	public void removeEntity(Entity entity) {
		this.removeEntity(entity, entity.chunkY);
	}

	public void removeEntity(Entity entity, int index) {
		if (index < 0) {
			index = 0;
		}

		if (index >= this.entities.length) {
			index = this.entities.length - 1;
		}

		this.entities[index].remove(entity);
	}

	public boolean hasDirectSunlight(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		return j >= this.heightmap[k << 4 | i];
	}

	@Nullable
	private BlockEntity createBlockEntity(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);
		Block block = blockState.getBlock();
		return !block.hasBlockEntity() ? null : ((BlockEntityProvider)block).createBlockEntity(this.world, blockState.getBlock().getData(blockState));
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos, Chunk.Status status) {
		BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(pos);
		if (blockEntity == null) {
			if (status == Chunk.Status.IMMEDIATE) {
				blockEntity = this.createBlockEntity(pos);
				this.world.setBlockEntity(pos, blockEntity);
			} else if (status == Chunk.Status.QUEUED) {
				this.blocks.add(pos);
			}
		} else if (blockEntity.isRemoved()) {
			this.blockEntities.remove(pos);
			return null;
		}

		return blockEntity;
	}

	public void addBlockEntity(BlockEntity be) {
		this.method_9136(be.getPos(), be);
		if (this.loaded) {
			this.world.addBlockEntity(be);
		}
	}

	public void method_9136(BlockPos pos, BlockEntity be) {
		be.setWorld(this.world);
		be.setPosition(pos);
		if (this.getBlockState(pos).getBlock() instanceof BlockEntityProvider) {
			if (this.blockEntities.containsKey(pos)) {
				((BlockEntity)this.blockEntities.get(pos)).markRemoved();
			}

			be.cancelRemoval();
			this.blockEntities.put(pos, be);
		}
	}

	public void method_9150(BlockPos pos) {
		if (this.loaded) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntities.remove(pos);
			if (blockEntity != null) {
				blockEntity.markRemoved();
			}
		}
	}

	public void loadToWorld() {
		this.loaded = true;
		this.world.addBlockEntities(this.blockEntities.values());

		for (TypeFilterableList<Entity> typeFilterableList : this.entities) {
			this.world.method_8537(typeFilterableList);
		}
	}

	public void unloadFromWorld() {
		this.loaded = false;

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			this.world.queueBlockEntity(blockEntity);
		}

		for (TypeFilterableList<Entity> typeFilterableList : this.entities) {
			this.world.unloadEntities(typeFilterableList);
		}
	}

	public void setModified() {
		this.modified = true;
	}

	public void method_9141(@Nullable Entity entity, Box box, List<Entity> list, Predicate<? super Entity> pred) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entities.length - 1);
		j = MathHelper.clamp(j, 0, this.entities.length - 1);

		for (int k = i; k <= j; k++) {
			if (!this.entities[k].isEmpty()) {
				for (Entity entity2 : this.entities[k]) {
					if (entity2.getBoundingBox().intersects(box) && entity2 != entity) {
						if (pred == null || pred.apply(entity2)) {
							list.add(entity2);
						}

						Entity[] entitys = entity2.getParts();
						if (entitys != null) {
							for (Entity entity3 : entitys) {
								if (entity3 != entity && entity3.getBoundingBox().intersects(box) && (pred == null || pred.apply(entity3))) {
									list.add(entity3);
								}
							}
						}
					}
				}
			}
		}
	}

	public <T extends Entity> void method_9140(Class<? extends T> clazz, Box box, List<T> list, Predicate<? super T> pred) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entities.length - 1);
		j = MathHelper.clamp(j, 0, this.entities.length - 1);

		for (int k = i; k <= j; k++) {
			for (T entity : this.entities[k].method_10806(clazz)) {
				if (entity.getBoundingBox().intersects(box) && (pred == null || pred.apply(entity))) {
					list.add(entity);
				}
			}
		}
	}

	public boolean shouldSave(boolean bl) {
		if (bl) {
			if (this.containsEntities && this.world.getLastUpdateTime() != this.lastSaveTime || this.modified) {
				return true;
			}
		} else if (this.containsEntities && this.world.getLastUpdateTime() >= this.lastSaveTime + 600L) {
			return true;
		}

		return this.modified;
	}

	public Random getRandom(long seed) {
		return new Random(
			this.world.getSeed()
					+ (long)(this.chunkX * this.chunkX * 4987142)
					+ (long)(this.chunkX * 5947611)
					+ (long)(this.chunkZ * this.chunkZ) * 4392871L
					+ (long)(this.chunkZ * 389711)
				^ seed
		);
	}

	public boolean isEmpty() {
		return false;
	}

	public void populateIfMissing(ChunkProvider chunkProvider, ChunkGenerator generator) {
		Chunk chunk = chunkProvider.getLoadedChunk(this.chunkX, this.chunkZ - 1);
		Chunk chunk2 = chunkProvider.getLoadedChunk(this.chunkX + 1, this.chunkZ);
		Chunk chunk3 = chunkProvider.getLoadedChunk(this.chunkX, this.chunkZ + 1);
		Chunk chunk4 = chunkProvider.getLoadedChunk(this.chunkX - 1, this.chunkZ);
		if (chunk2 != null && chunk3 != null && chunkProvider.getLoadedChunk(this.chunkX + 1, this.chunkZ + 1) != null) {
			this.populate(generator);
		}

		if (chunk4 != null && chunk3 != null && chunkProvider.getLoadedChunk(this.chunkX - 1, this.chunkZ + 1) != null) {
			chunk4.populate(generator);
		}

		if (chunk != null && chunk2 != null && chunkProvider.getLoadedChunk(this.chunkX + 1, this.chunkZ - 1) != null) {
			chunk.populate(generator);
		}

		if (chunk != null && chunk4 != null) {
			Chunk chunk5 = chunkProvider.getLoadedChunk(this.chunkX - 1, this.chunkZ - 1);
			if (chunk5 != null) {
				chunk5.populate(generator);
			}
		}
	}

	protected void populate(ChunkGenerator chunkGenerator) {
		if (this.isTerrainPopulated()) {
			if (chunkGenerator.method_11762(this, this.chunkX, this.chunkZ)) {
				this.setModified();
			}
		} else {
			this.populate();
			chunkGenerator.populate(this.chunkX, this.chunkZ);
			this.setModified();
		}
	}

	public BlockPos method_9156(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = i | j << 4;
		BlockPos blockPos = new BlockPos(pos.getX(), this.surfaceCache[k], pos.getZ());
		if (blockPos.getY() == -999) {
			int l = this.getHighestNonEmptySectionYOffset() + 15;
			blockPos = new BlockPos(pos.getX(), l, pos.getZ());
			int m = -1;

			while (blockPos.getY() > 0 && m == -1) {
				BlockState blockState = this.getBlockState(blockPos);
				Material material = blockState.getMaterial();
				if (!material.blocksMovement() && !material.isFluid()) {
					blockPos = blockPos.down();
				} else {
					m = blockPos.getY() + 1;
				}
			}

			this.surfaceCache[k] = m;
		}

		return new BlockPos(pos.getX(), this.surfaceCache[k], pos.getZ());
	}

	public void populateBlockEntities(boolean runningBehind) {
		if (this.isSkyLightOutdated && this.world.dimension.isOverworld() && !runningBehind) {
			this.recheckSkyLightGaps(this.world.isClient);
		}

		this.blockEntitiesPopulated = true;
		if (!this.lightPopulated && this.terrainPopulated) {
			this.populate();
		}

		while (!this.blocks.isEmpty()) {
			BlockPos blockPos = (BlockPos)this.blocks.poll();
			if (this.getBlockEntity(blockPos, Chunk.Status.CHECK) == null && this.getBlockState(blockPos).getBlock().hasBlockEntity()) {
				BlockEntity blockEntity = this.createBlockEntity(blockPos);
				this.world.setBlockEntity(blockPos, blockEntity);
				this.world.onRenderRegionUpdate(blockPos, blockPos);
			}
		}
	}

	public boolean isPopulated() {
		return this.blockEntitiesPopulated && this.terrainPopulated && this.lightPopulated;
	}

	public boolean hasPopulatedBlockEntities() {
		return this.blockEntitiesPopulated;
	}

	public ChunkPos getChunkPos() {
		return new ChunkPos(this.chunkX, this.chunkZ);
	}

	public boolean areSectionsEmptyBetween(int startY, int endY) {
		if (startY < 0) {
			startY = 0;
		}

		if (endY >= 256) {
			endY = 255;
		}

		for (int i = startY; i <= endY; i += 16) {
			ChunkSection chunkSection = this.chunkSections[i >> 4];
			if (chunkSection != EMPTY && !chunkSection.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void setLevelChunkSections(ChunkSection[] chunkSections) {
		if (this.chunkSections.length != chunkSections.length) {
			LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", new Object[]{chunkSections.length, this.chunkSections.length});
		} else {
			System.arraycopy(chunkSections, 0, this.chunkSections, 0, this.chunkSections.length);
		}
	}

	public void method_3895(PacketByteBuf packet, int i, boolean bl) {
		boolean bl2 = this.world.dimension.isOverworld();

		for (int j = 0; j < this.chunkSections.length; j++) {
			ChunkSection chunkSection = this.chunkSections[j];
			if ((i & 1 << j) == 0) {
				if (bl && chunkSection != EMPTY) {
					this.chunkSections[j] = EMPTY;
				}
			} else {
				if (chunkSection == EMPTY) {
					chunkSection = new ChunkSection(j << 4, bl2);
					this.chunkSections[j] = chunkSection;
				}

				chunkSection.getBlockData().read(packet);
				packet.readBytes(chunkSection.getBlockLight().getValue());
				if (bl2) {
					packet.readBytes(chunkSection.getSkyLight().getValue());
				}
			}
		}

		if (bl) {
			packet.readBytes(this.biomeArray);
		}

		for (int k = 0; k < this.chunkSections.length; k++) {
			if (this.chunkSections[k] != EMPTY && (i & 1 << k) != 0) {
				this.chunkSections[k].calculateCounts();
			}
		}

		this.lightPopulated = true;
		this.terrainPopulated = true;
		this.generateHeightmap();

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			blockEntity.resetBlock();
		}
	}

	public Biome method_11771(BlockPos pos, SingletonBiomeSource biomeSource) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = this.biomeArray[j << 4 | i] & 255;
		if (k == 255) {
			Biome biome = biomeSource.method_11536(pos, Biomes.PLAINS);
			k = Biome.getBiomeIndex(biome);
			this.biomeArray[j << 4 | i] = (byte)(k & 0xFF);
		}

		Biome biome2 = Biome.byId(k);
		return biome2 == null ? Biomes.PLAINS : biome2;
	}

	public byte[] getBiomeArray() {
		return this.biomeArray;
	}

	public void setBiomeArray(byte[] biomeArray) {
		if (this.biomeArray.length != biomeArray.length) {
			LOGGER.warn("Could not set level chunk biomes, array length is {} instead of {}", new Object[]{biomeArray.length, this.biomeArray.length});
		} else {
			System.arraycopy(biomeArray, 0, this.biomeArray, 0, this.biomeArray.length);
		}
	}

	public void method_3922() {
		this.field_4743 = 0;
	}

	public void method_3923() {
		if (this.field_4743 < 4096) {
			BlockPos blockPos = new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);

			for (int i = 0; i < 8; i++) {
				if (this.field_4743 >= 4096) {
					return;
				}

				int j = this.field_4743 % 16;
				int k = this.field_4743 / 16 % 16;
				int l = this.field_4743 / 256;
				this.field_4743++;

				for (int m = 0; m < 16; m++) {
					BlockPos blockPos2 = blockPos.add(k, (j << 4) + m, l);
					boolean bl = m == 0 || m == 15 || k == 0 || k == 15 || l == 0 || l == 15;
					if (this.chunkSections[j] == EMPTY && bl || this.chunkSections[j] != EMPTY && this.chunkSections[j].getBlockState(k, m, l).getMaterial() == Material.AIR) {
						for (Direction direction : Direction.values()) {
							BlockPos blockPos3 = blockPos2.offset(direction);
							if (this.world.getBlockState(blockPos3).getLuminance() > 0) {
								this.world.method_8568(blockPos3);
							}
						}

						this.world.method_8568(blockPos2);
					}
				}
			}
		}
	}

	public void populate() {
		this.terrainPopulated = true;
		this.lightPopulated = true;
		BlockPos blockPos = new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
		if (this.world.dimension.isOverworld()) {
			if (this.world.isRegionLoaded(blockPos.add(-1, 0, -1), blockPos.add(16, this.world.getSeaLevel(), 16))) {
				label44:
				for (int i = 0; i < 16; i++) {
					for (int j = 0; j < 16; j++) {
						if (!this.updateLight(i, j)) {
							this.lightPopulated = false;
							break label44;
						}
					}
				}

				if (this.lightPopulated) {
					for (Direction direction : Direction.DirectionType.HORIZONTAL) {
						int k = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 16 : 1;
						this.world.getChunk(blockPos.offset(direction, k)).updateLight(direction.getOpposite());
					}

					this.method_9167();
				}
			} else {
				this.lightPopulated = false;
			}
		}
	}

	private void method_9167() {
		for (int i = 0; i < this.columnSkyLightOutdated.length; i++) {
			this.columnSkyLightOutdated[i] = true;
		}

		this.recheckSkyLightGaps(false);
	}

	private void updateLight(Direction dir) {
		if (this.terrainPopulated) {
			if (dir == Direction.EAST) {
				for (int i = 0; i < 16; i++) {
					this.updateLight(15, i);
				}
			} else if (dir == Direction.WEST) {
				for (int j = 0; j < 16; j++) {
					this.updateLight(0, j);
				}
			} else if (dir == Direction.SOUTH) {
				for (int k = 0; k < 16; k++) {
					this.updateLight(k, 15);
				}
			} else if (dir == Direction.NORTH) {
				for (int l = 0; l < 16; l++) {
					this.updateLight(l, 0);
				}
			}
		}
	}

	private boolean updateLight(int x, int z) {
		int i = this.getHighestNonEmptySectionYOffset();
		boolean bl = false;
		boolean bl2 = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable((this.chunkX << 4) + x, 0, (this.chunkZ << 4) + z);

		for (int j = i + 16 - 1; j > this.world.getSeaLevel() || j > 0 && !bl2; j--) {
			mutable.setPosition(mutable.getX(), j, mutable.getZ());
			int k = this.getBlockOpacityAtPos(mutable);
			if (k == 255 && mutable.getY() < this.world.getSeaLevel()) {
				bl2 = true;
			}

			if (!bl && k > 0) {
				bl = true;
			} else if (bl && k == 0 && !this.world.method_8568(mutable)) {
				return false;
			}
		}

		for (int l = mutable.getY(); l > 0; l--) {
			mutable.setPosition(mutable.getX(), l, mutable.getZ());
			if (this.getBlockState(mutable).getLuminance() > 0) {
				this.world.method_8568(mutable);
			}
		}

		return true;
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public void setChunkLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public World getWorld() {
		return this.world;
	}

	public int[] getLevelHeightmap() {
		return this.heightmap;
	}

	public void setLevelHeightmap(int[] heightmap) {
		if (this.heightmap.length != heightmap.length) {
			LOGGER.warn("Could not set level chunk heightmap, array length is {} instead of {}", new Object[]{heightmap.length, this.heightmap.length});
		} else {
			System.arraycopy(heightmap, 0, this.heightmap, 0, this.heightmap.length);
		}
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public TypeFilterableList<Entity>[] getEntities() {
		return this.entities;
	}

	public boolean isTerrainPopulated() {
		return this.terrainPopulated;
	}

	public void setTerrainPopulated(boolean terrainPopulated) {
		this.terrainPopulated = terrainPopulated;
	}

	public boolean isLightPopulated() {
		return this.lightPopulated;
	}

	public void setLightPopulated(boolean lightPopulated) {
		this.lightPopulated = lightPopulated;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void setHasEntities(boolean containsEntities) {
		this.containsEntities = containsEntities;
	}

	public void setLastSaveTime(long lastSaveTime) {
		this.lastSaveTime = lastSaveTime;
	}

	public int getMinimumHeightMap() {
		return this.minimumHeightmap;
	}

	public long getInhabitedTime() {
		return this.inhabitedTime;
	}

	public void setInhabitedTime(long inhabitedTime) {
		this.inhabitedTime = inhabitedTime;
	}

	public static enum Status {
		IMMEDIATE,
		QUEUED,
		CHECK;
	}
}
