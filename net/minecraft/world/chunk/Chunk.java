package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.TypeFilterableList;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LayeredBiomeSource;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
	private static final Logger LOGGER = LogManager.getLogger();
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
	private ConcurrentLinkedQueue<BlockPos> blocks = Queues.newConcurrentLinkedQueue();

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
		boolean bl = !world.dimension.hasNoSkylight();

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < k; n++) {
					int o = l * k * 16 | m * k | n;
					BlockState blockState = chunkBlockStateStorage.get(o);
					if (blockState.getBlock().getMaterial() != Material.AIR) {
						int p = n >> 4;
						if (this.chunkSections[p] == null) {
							this.chunkSections[p] = new ChunkSection(p << 4, bl);
						}

						this.chunkSections[p].setBlockState(l, n & 15, m, blockState);
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

	public int getHighestNonEmptySectionYOffset() {
		for (int i = this.chunkSections.length - 1; i >= 0; i--) {
			if (this.chunkSections[i] != null) {
				return this.chunkSections[i].getYOffset();
			}
		}

		return 0;
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
					Block block = this.getBlock(j, l - 1, k);
					if (block.getOpacity() != 0) {
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

				if (!this.world.dimension.hasNoSkylight()) {
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
							if (chunkSection != null) {
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
			if (!this.world.dimension.hasNoSkylight()) {
				if (j < i) {
					for (int m = j; m < i; m++) {
						ChunkSection chunkSection = this.chunkSections[m >> 4];
						if (chunkSection != null) {
							chunkSection.setSkyLight(x, m & 15, z, 15);
							this.world.onLightUpdate(new BlockPos((this.chunkX << 4) + x, m, (this.chunkZ << 4) + z));
						}
					}
				} else {
					for (int n = i; n < j; n++) {
						ChunkSection chunkSection2 = this.chunkSections[n >> 4];
						if (chunkSection2 != null) {
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
					if (chunkSection3 != null) {
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

			if (!this.world.dimension.hasNoSkylight()) {
				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					this.calculateSkyLightForRegion(k + direction.getOffsetX(), l + direction.getOffsetZ(), r, s);
				}

				this.calculateSkyLightForRegion(k, l, r, s);
			}

			this.modified = true;
		}
	}

	public int getBlockOpacityAtPos(BlockPos pos) {
		return this.getBlockAtPos(pos).getOpacity();
	}

	private int getBlockOpacity(int x, int y, int z) {
		return this.getBlock(x, y, z).getOpacity();
	}

	private Block getBlock(int x, int y, int z) {
		Block block = Blocks.AIR;
		if (y >= 0 && y >> 4 < this.chunkSections.length) {
			ChunkSection chunkSection = this.chunkSections[y >> 4];
			if (chunkSection != null) {
				try {
					block = chunkSection.getBlock(x, y & 15, z);
				} catch (Throwable var8) {
					CrashReport crashReport = CrashReport.create(var8, "Getting block");
					throw new CrashException(crashReport);
				}
			}
		}

		return block;
	}

	public Block method_9131(int x, int y, int z) {
		try {
			return this.getBlock(x & 15, y, z & 15);
		} catch (CrashException var6) {
			CrashReportSection crashReportSection = var6.getReport().addElement("Block being got");
			crashReportSection.add("Location", new Callable<String>() {
				public String call() throws Exception {
					return CrashReportSection.addBlockData(new BlockPos(Chunk.this.chunkX * 16 + x, y, Chunk.this.chunkZ * 16 + z));
				}
			});
			throw var6;
		}
	}

	public Block getBlockAtPos(BlockPos pos) {
		try {
			return this.getBlock(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
		} catch (CrashException var4) {
			CrashReportSection crashReportSection = var4.getReport().addElement("Block being got");
			crashReportSection.add("Location", new Callable<String>() {
				public String call() throws Exception {
					return CrashReportSection.addBlockData(pos);
				}
			});
			throw var4;
		}
	}

	public BlockState method_9154(BlockPos pos) {
		if (this.world.getGeneratorType() == LevelGeneratorType.DEBUG) {
			BlockState blockState = null;
			if (pos.getY() == 60) {
				blockState = Blocks.BARRIER.getDefaultState();
			}

			if (pos.getY() == 70) {
				blockState = DebugChunkGenerator.method_9190(pos.getX(), pos.getZ());
			}

			return blockState == null ? Blocks.AIR.getDefaultState() : blockState;
		} else {
			try {
				if (pos.getY() >= 0 && pos.getY() >> 4 < this.chunkSections.length) {
					ChunkSection chunkSection = this.chunkSections[pos.getY() >> 4];
					if (chunkSection != null) {
						int i = pos.getX() & 15;
						int j = pos.getY() & 15;
						int k = pos.getZ() & 15;
						return chunkSection.getBlockState(i, j, k);
					}
				}

				return Blocks.AIR.getDefaultState();
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Getting block state");
				CrashReportSection crashReportSection = crashReport.addElement("Block being got");
				crashReportSection.add("Location", new Callable<String>() {
					public String call() throws Exception {
						return CrashReportSection.addBlockData(pos);
					}
				});
				throw new CrashException(crashReport);
			}
		}
	}

	private int getBlockData(int x, int y, int z) {
		if (y >> 4 >= this.chunkSections.length) {
			return 0;
		} else {
			ChunkSection chunkSection = this.chunkSections[y >> 4];
			return chunkSection != null ? chunkSection.getBlockData(x, y & 15, z) : 0;
		}
	}

	public int getBlockData(BlockPos pos) {
		return this.getBlockData(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
	}

	public BlockState getBlockState(BlockPos pos, BlockState state) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		int l = k << 4 | i;
		if (j >= this.surfaceCache[l] - 1) {
			this.surfaceCache[l] = -999;
		}

		int m = this.heightmap[l];
		BlockState blockState = this.method_9154(pos);
		if (blockState == state) {
			return null;
		} else {
			Block block = state.getBlock();
			Block block2 = blockState.getBlock();
			ChunkSection chunkSection = this.chunkSections[j >> 4];
			boolean bl = false;
			if (chunkSection == null) {
				if (block == Blocks.AIR) {
					return null;
				}

				chunkSection = this.chunkSections[j >> 4] = new ChunkSection(j >> 4 << 4, !this.world.dimension.hasNoSkylight());
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

			if (chunkSection.getBlock(i, j & 15, k) != block) {
				return null;
			} else {
				if (bl) {
					this.calculateSkyLight();
				} else {
					int n = block.getOpacity();
					int o = block2.getOpacity();
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
		if (chunkSection == null) {
			return this.hasDirectSunlight(pos) ? lightType.defaultValue : 0;
		} else if (lightType == LightType.SKY) {
			return this.world.dimension.hasNoSkylight() ? 0 : chunkSection.getSkyLight(i, j & 15, k);
		} else {
			return lightType == LightType.BLOCK ? chunkSection.getBlockLight(i, j & 15, k) : lightType.defaultValue;
		}
	}

	public void setLightAtPos(LightType lightType, BlockPos pos, int lightLevel) {
		int i = pos.getX() & 15;
		int j = pos.getY();
		int k = pos.getZ() & 15;
		ChunkSection chunkSection = this.chunkSections[j >> 4];
		if (chunkSection == null) {
			chunkSection = this.chunkSections[j >> 4] = new ChunkSection(j >> 4 << 4, !this.world.dimension.hasNoSkylight());
			this.calculateSkyLight();
		}

		this.modified = true;
		if (lightType == LightType.SKY) {
			if (!this.world.dimension.hasNoSkylight()) {
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
		if (chunkSection == null) {
			return !this.world.dimension.hasNoSkylight() && darkness < LightType.SKY.defaultValue ? LightType.SKY.defaultValue - darkness : 0;
		} else {
			int l = this.world.dimension.hasNoSkylight() ? 0 : chunkSection.getSkyLight(i, j & 15, k);
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
			LOGGER.warn("Wrong location! (" + i + ", " + j + ") should be (" + this.chunkX + ", " + this.chunkZ + "), " + entity, new Object[]{entity});
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

	private BlockEntity createBlockEntity(BlockPos pos) {
		Block block = this.getBlockAtPos(pos);
		return !block.hasBlockEntity() ? null : ((BlockEntityProvider)block).createBlockEntity(this.world, this.getBlockData(pos));
	}

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
		be.setPos(pos);
		if (this.getBlockAtPos(pos) instanceof BlockEntityProvider) {
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

		for (int i = 0; i < this.entities.length; i++) {
			for (Entity entity : this.entities[i]) {
				entity.method_6097();
			}

			this.world.method_8537(this.entities[i]);
		}
	}

	public void unloadFromWorld() {
		this.loaded = false;

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			this.world.queueBlockEntity(blockEntity);
		}

		for (int i = 0; i < this.entities.length; i++) {
			this.world.unloadEntities(this.entities[i]);
		}
	}

	public void setModified() {
		this.modified = true;
	}

	public void method_9141(Entity entity, Box box, List<Entity> list, Predicate<? super Entity> pred) {
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
							for (int l = 0; l < entitys.length; l++) {
								entity2 = entitys[l];
								if (entity2 != entity && entity2.getBoundingBox().intersects(box) && (pred == null || pred.apply(entity2))) {
									list.add(entity2);
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

	public void decorateChunk(ChunkProvider provider1, ChunkProvider provider2, int chunkX, int chunkZ) {
		boolean bl = provider1.chunkExists(chunkX, chunkZ - 1);
		boolean bl2 = provider1.chunkExists(chunkX + 1, chunkZ);
		boolean bl3 = provider1.chunkExists(chunkX, chunkZ + 1);
		boolean bl4 = provider1.chunkExists(chunkX - 1, chunkZ);
		boolean bl5 = provider1.chunkExists(chunkX - 1, chunkZ - 1);
		boolean bl6 = provider1.chunkExists(chunkX + 1, chunkZ + 1);
		boolean bl7 = provider1.chunkExists(chunkX - 1, chunkZ + 1);
		boolean bl8 = provider1.chunkExists(chunkX + 1, chunkZ - 1);
		if (bl2 && bl3 && bl6) {
			if (!this.terrainPopulated) {
				provider1.decorateChunk(provider2, chunkX, chunkZ);
			} else {
				provider1.isChunkModified(provider2, this, chunkX, chunkZ);
			}
		}

		if (bl4 && bl3 && bl7) {
			Chunk chunk = provider1.getChunk(chunkX - 1, chunkZ);
			if (!chunk.terrainPopulated) {
				provider1.decorateChunk(provider2, chunkX - 1, chunkZ);
			} else {
				provider1.isChunkModified(provider2, chunk, chunkX - 1, chunkZ);
			}
		}

		if (bl && bl2 && bl8) {
			Chunk chunk2 = provider1.getChunk(chunkX, chunkZ - 1);
			if (!chunk2.terrainPopulated) {
				provider1.decorateChunk(provider2, chunkX, chunkZ - 1);
			} else {
				provider1.isChunkModified(provider2, chunk2, chunkX, chunkZ - 1);
			}
		}

		if (bl5 && bl && bl4) {
			Chunk chunk3 = provider1.getChunk(chunkX - 1, chunkZ - 1);
			if (!chunk3.terrainPopulated) {
				provider1.decorateChunk(provider2, chunkX - 1, chunkZ - 1);
			} else {
				provider1.isChunkModified(provider2, chunk3, chunkX - 1, chunkZ - 1);
			}
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
				Block block = this.getBlockAtPos(blockPos);
				Material material = block.getMaterial();
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
		if (this.isSkyLightOutdated && !this.world.dimension.hasNoSkylight() && !runningBehind) {
			this.recheckSkyLightGaps(this.world.isClient);
		}

		this.blockEntitiesPopulated = true;
		if (!this.lightPopulated && this.terrainPopulated) {
			this.populate();
		}

		while (!this.blocks.isEmpty()) {
			BlockPos blockPos = (BlockPos)this.blocks.poll();
			if (this.getBlockEntity(blockPos, Chunk.Status.CHECK) == null && this.getBlockAtPos(blockPos).hasBlockEntity()) {
				BlockEntity blockEntity = this.createBlockEntity(blockPos);
				this.world.setBlockEntity(blockPos, blockEntity);
				this.world.onRenderRegionUpdate(blockPos, blockPos);
			}
		}
	}

	public boolean isPopulated() {
		return this.blockEntitiesPopulated && this.terrainPopulated && this.lightPopulated;
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
			if (chunkSection != null && !chunkSection.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void setLevelChunkSections(ChunkSection[] chunkSections) {
		if (this.chunkSections.length != chunkSections.length) {
			LOGGER.warn("Could not set level chunk sections, array length is " + chunkSections.length + " instead of " + this.chunkSections.length);
		} else {
			for (int i = 0; i < this.chunkSections.length; i++) {
				this.chunkSections[i] = chunkSections[i];
			}
		}
	}

	public void method_3895(byte[] bs, int i, boolean bl) {
		int j = 0;
		boolean bl2 = !this.world.dimension.hasNoSkylight();

		for (int k = 0; k < this.chunkSections.length; k++) {
			if ((i & 1 << k) != 0) {
				if (this.chunkSections[k] == null) {
					this.chunkSections[k] = new ChunkSection(k << 4, bl2);
				}

				char[] cs = this.chunkSections[k].getBlockStates();

				for (int l = 0; l < cs.length; l++) {
					cs[l] = (char)((bs[j + 1] & 255) << 8 | bs[j] & 255);
					j += 2;
				}
			} else if (bl && this.chunkSections[k] != null) {
				this.chunkSections[k] = null;
			}
		}

		for (int m = 0; m < this.chunkSections.length; m++) {
			if ((i & 1 << m) != 0 && this.chunkSections[m] != null) {
				ChunkNibbleArray chunkNibbleArray = this.chunkSections[m].getBlockLight();
				System.arraycopy(bs, j, chunkNibbleArray.getValue(), 0, chunkNibbleArray.getValue().length);
				j += chunkNibbleArray.getValue().length;
			}
		}

		if (bl2) {
			for (int n = 0; n < this.chunkSections.length; n++) {
				if ((i & 1 << n) != 0 && this.chunkSections[n] != null) {
					ChunkNibbleArray chunkNibbleArray2 = this.chunkSections[n].getSkyLight();
					System.arraycopy(bs, j, chunkNibbleArray2.getValue(), 0, chunkNibbleArray2.getValue().length);
					j += chunkNibbleArray2.getValue().length;
				}
			}
		}

		if (bl) {
			System.arraycopy(bs, j, this.biomeArray, 0, this.biomeArray.length);
			j += this.biomeArray.length;
		}

		for (int o = 0; o < this.chunkSections.length; o++) {
			if (this.chunkSections[o] != null && (i & 1 << o) != 0) {
				this.chunkSections[o].calculateCounts();
			}
		}

		this.lightPopulated = true;
		this.terrainPopulated = true;
		this.generateHeightmap();

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			blockEntity.resetBlock();
		}
	}

	public Biome getBiomeAt(BlockPos pos, LayeredBiomeSource biomeSource) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		int k = this.biomeArray[j << 4 | i] & 255;
		if (k == 255) {
			Biome biome = biomeSource.getBiomeAt(pos, Biome.PLAINS);
			k = biome.id;
			this.biomeArray[j << 4 | i] = (byte)(k & 0xFF);
		}

		Biome biome2 = Biome.byId(k);
		return biome2 == null ? Biome.PLAINS : biome2;
	}

	public byte[] getBiomeArray() {
		return this.biomeArray;
	}

	public void setBiomeArray(byte[] biomeArray) {
		if (this.biomeArray.length != biomeArray.length) {
			LOGGER.warn("Could not set level chunk biomes, array length is " + biomeArray.length + " instead of " + this.biomeArray.length);
		} else {
			for (int i = 0; i < this.biomeArray.length; i++) {
				this.biomeArray[i] = biomeArray[i];
			}
		}
	}

	public void method_3922() {
		this.field_4743 = 0;
	}

	public void method_3923() {
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
				if (this.chunkSections[j] == null && bl || this.chunkSections[j] != null && this.chunkSections[j].getBlock(k, m, l).getMaterial() == Material.AIR) {
					for (Direction direction : Direction.values()) {
						BlockPos blockPos3 = blockPos2.offset(direction);
						if (this.world.getBlockState(blockPos3).getBlock().getLightLevel() > 0) {
							this.world.method_8568(blockPos3);
						}
					}

					this.world.method_8568(blockPos2);
				}
			}
		}
	}

	public void populate() {
		this.terrainPopulated = true;
		this.lightPopulated = true;
		BlockPos blockPos = new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
		if (!this.world.dimension.hasNoSkylight()) {
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
			if (this.getBlockAtPos(mutable).getLightLevel() > 0) {
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
			LOGGER.warn("Could not set level chunk heightmap, array length is " + heightmap.length + " instead of " + this.heightmap.length);
		} else {
			for (int i = 0; i < this.heightmap.length; i++) {
				this.heightmap[i] = heightmap[i];
			}
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
