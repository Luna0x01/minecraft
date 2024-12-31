package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3592;
import net.minecraft.class_3604;
import net.minecraft.class_3781;
import net.minecraft.class_3786;
import net.minecraft.class_3789;
import net.minecraft.class_3790;
import net.minecraft.class_3801;
import net.minecraft.class_3804;
import net.minecraft.class_3992;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.ThreadExecutor;
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
import net.minecraft.util.registry.Registry;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.LevelGeneratorType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements class_3781 {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ChunkSection EMPTY = null;
	private final ChunkSection[] chunkSections = new ChunkSection[16];
	private final Biome[] field_18887;
	private final boolean[] columnSkyLightOutdated = new boolean[256];
	private final Map<BlockPos, NbtCompound> field_18888 = Maps.newHashMap();
	private boolean loaded;
	private final World world;
	private final Map<class_3804.class_3805, class_3804> field_18889 = Maps.newEnumMap(class_3804.class_3805.class);
	public final int chunkX;
	public final int chunkZ;
	private boolean isSkyLightOutdated;
	private final class_3790 field_18890;
	private final Map<BlockPos, BlockEntity> blockEntities = Maps.newHashMap();
	private final TypeFilterableList<Entity>[] entities;
	private final Map<String, class_3992> field_18891 = Maps.newHashMap();
	private final Map<String, LongSet> field_18892 = Maps.newHashMap();
	private final ShortList[] field_18893 = new ShortList[16];
	private final class_3604<Block> field_18894;
	private final class_3604<Fluid> field_18895;
	private boolean blockEntitiesPopulated;
	private boolean containsEntities;
	private long lastSaveTime;
	private boolean modified;
	private int minimumHeightmap;
	private long inhabitedTime;
	private int field_4743 = 4096;
	private final ConcurrentLinkedQueue<BlockPos> blocks = Queues.newConcurrentLinkedQueue();
	private class_3786 field_18883 = class_3786.EMPTY;
	private int field_18884;
	private final AtomicInteger field_18885 = new AtomicInteger();
	private final ChunkPos field_18886;

	public Chunk(World world, int i, int j, Biome[] biomes) {
		this(world, i, j, biomes, class_3790.field_18935, class_3592.method_16285(), class_3592.method_16285(), 0L);
	}

	public Chunk(World world, int i, int j, Biome[] biomes, class_3790 arg, class_3604<Block> arg2, class_3604<Fluid> arg3, long l) {
		this.entities = new TypeFilterableList[16];
		this.world = world;
		this.chunkX = i;
		this.chunkZ = j;
		this.field_18886 = new ChunkPos(i, j);
		this.field_18890 = arg;

		for (class_3804.class_3805 lv : class_3804.class_3805.values()) {
			if (lv.method_17251() == class_3804.class_3806.LIVE_WORLD) {
				this.field_18889.put(lv, new class_3804(this, lv));
			}
		}

		for (int k = 0; k < this.entities.length; k++) {
			this.entities[k] = new TypeFilterableList<>(Entity.class);
		}

		this.field_18887 = biomes;
		this.field_18894 = arg2;
		this.field_18895 = arg3;
		this.inhabitedTime = l;
	}

	public Chunk(World world, ChunkBlockStateStorage chunkBlockStateStorage, int i, int j) {
		this(
			world,
			i,
			j,
			chunkBlockStateStorage.method_17007(),
			chunkBlockStateStorage.method_17145(),
			chunkBlockStateStorage.method_17011(),
			chunkBlockStateStorage.method_17012(),
			chunkBlockStateStorage.method_17136()
		);

		for (int k = 0; k < this.chunkSections.length; k++) {
			this.chunkSections[k] = chunkBlockStateStorage.method_17003()[k];
		}

		for (NbtCompound nbtCompound : chunkBlockStateStorage.method_17142()) {
			ThreadedAnvilChunkStorage.method_11783(nbtCompound, world, this);
		}

		for (BlockEntity blockEntity : chunkBlockStateStorage.method_17141().values()) {
			this.addBlockEntity(blockEntity);
		}

		this.field_18888.putAll(chunkBlockStateStorage.method_17146());

		for (int l = 0; l < chunkBlockStateStorage.method_17144().length; l++) {
			this.field_18893[l] = chunkBlockStateStorage.method_17144()[l];
		}

		this.method_17076(chunkBlockStateStorage.method_17004());
		this.method_17080(chunkBlockStateStorage.method_17006());

		for (class_3804.class_3805 lv : chunkBlockStateStorage.method_17143()) {
			if (lv.method_17251() == class_3804.class_3806.LIVE_WORLD) {
				((class_3804)this.field_18889.computeIfAbsent(lv, arg -> new class_3804(this, arg))).method_17244(chunkBlockStateStorage.method_17123(lv).method_17245());
			}
		}

		this.modified = true;
		this.method_16990(class_3786.FULLCHUNK);
	}

	public Set<BlockPos> method_17091() {
		Set<BlockPos> set = Sets.newHashSet(this.field_18888.keySet());
		set.addAll(this.blockEntities.keySet());
		return set;
	}

	public boolean isChunkEqual(int chunkX, int chunkZ) {
		return chunkX == this.chunkX && chunkZ == this.chunkZ;
	}

	@Override
	public ChunkSection[] method_17003() {
		return this.chunkSections;
	}

	protected void generateHeightmap() {
		for (class_3804 lv : this.field_18889.values()) {
			lv.method_17238();
		}

		this.modified = true;
	}

	public void calculateSkyLight() {
		int i = this.method_17001();
		this.minimumHeightmap = Integer.MAX_VALUE;

		for (class_3804 lv : this.field_18889.values()) {
			lv.method_17238();
		}

		for (int j = 0; j < 16; j++) {
			for (int k = 0; k < 16; k++) {
				if (this.world.dimension.isOverworld()) {
					int l = 15;
					int m = i + 16 - 1;

					while (true) {
						int n = this.getBlockOpacity(j, m, k);
						if (n == 0 && l != 15) {
							n = 1;
						}

						l -= n;
						if (l > 0) {
							ChunkSection chunkSection = this.chunkSections[m >> 4];
							if (chunkSection != EMPTY) {
								chunkSection.setSkyLight(j, m & 15, k, l);
								this.world.onLightUpdate(new BlockPos((this.chunkX << 4) + j, m, (this.chunkZ << 4) + k));
							}
						}

						if (--m <= 0 || l <= 0) {
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
		if (this.world.method_16391(new BlockPos(this.chunkX * 16 + 8, 0, this.chunkZ * 16 + 8), 16)) {
			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					if (this.columnSkyLightOutdated[i + j * 16]) {
						this.columnSkyLightOutdated[i + j * 16] = false;
						int k = this.method_16992(class_3804.class_3805.LIGHT_BLOCKING, i, j);
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
		int i = this.world.method_16373(class_3804.class_3805.MOTION_BLOCKING, new BlockPos(x, 0, z)).getY();
		if (i > y) {
			this.calculateSkyLightForRegion(x, z, y, i + 1);
		} else if (i < y) {
			this.calculateSkyLightForRegion(x, z, i, y + 1);
		}
	}

	private void calculateSkyLightForRegion(int x, int z, int beginY, int endY) {
		if (endY > beginY && this.world.method_16391(new BlockPos(x, 0, z), 16)) {
			for (int i = beginY; i < endY; i++) {
				this.world.calculateLightAtPos(LightType.SKY, new BlockPos(x, i, z));
			}

			this.modified = true;
		}
	}

	private void method_3917(int i, int j, int k, BlockState blockState) {
		class_3804 lv = (class_3804)this.field_18889.get(class_3804.class_3805.LIGHT_BLOCKING);
		int l = lv.method_17240(i & 15, k & 15) & 0xFF;
		if (lv.method_17242(i, j, k, blockState)) {
			int m = lv.method_17240(i & 15, k & 15);
			int n = this.chunkX * 16 + i;
			int o = this.chunkZ * 16 + k;
			this.world.method_3704(n, o, m, l);
			if (this.world.dimension.isOverworld()) {
				int p = Math.min(l, m);
				int q = Math.max(l, m);
				int r = m < l ? 15 : 0;

				for (int s = p; s < q; s++) {
					ChunkSection chunkSection = this.chunkSections[s >> 4];
					if (chunkSection != EMPTY) {
						chunkSection.setSkyLight(i, s & 15, k, r);
						this.world.onLightUpdate(new BlockPos((this.chunkX << 4) + i, s, (this.chunkZ << 4) + k));
					}
				}

				int t = 15;

				while (m > 0 && t > 0) {
					int u = this.getBlockOpacity(i, --m, k);
					u = u == 0 ? 1 : u;
					t -= u;
					t = Math.max(0, t);
					ChunkSection chunkSection2 = this.chunkSections[m >> 4];
					if (chunkSection2 != EMPTY) {
						chunkSection2.setSkyLight(i, m & 15, k, t);
					}
				}
			}

			if (m < this.minimumHeightmap) {
				this.minimumHeightmap = m;
			}

			if (this.world.dimension.isOverworld()) {
				int v = lv.method_17240(i & 15, k & 15);
				int w = Math.min(l, v);
				int x = Math.max(l, v);

				for (Direction direction : Direction.DirectionType.HORIZONTAL) {
					this.calculateSkyLightForRegion(n + direction.getOffsetX(), o + direction.getOffsetZ(), w, x);
				}

				this.calculateSkyLightForRegion(n, o, w, x);
			}

			this.modified = true;
		}
	}

	private int getBlockOpacity(int x, int y, int z) {
		return this.getBlockState(x, y, z).method_16885(this.world, new BlockPos(x, y, z));
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	public BlockState getBlockState(int x, int y, int z) {
		if (this.world.method_8575() == LevelGeneratorType.DEBUG) {
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
				crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(x, y, z)));
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.method_17078(pos.getX(), pos.getY(), pos.getZ());
	}

	public FluidState method_17078(int i, int j, int k) {
		try {
			if (j >= 0 && j >> 4 < this.chunkSections.length) {
				ChunkSection chunkSection = this.chunkSections[j >> 4];
				if (chunkSection != EMPTY) {
					return chunkSection.method_17093(i & 15, j & 15, k & 15);
				}
			}

			return Fluids.EMPTY.getDefaultState();
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Getting fluid state");
			CrashReportSection crashReportSection = crashReport.addElement("Block being got");
			crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(i, j, k)));
			throw new CrashException(crashReport);
		}
	}

	@Nullable
	@Override
	public BlockState method_16994(BlockPos blockPos, BlockState blockState, boolean bl) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		int l = ((class_3804)this.field_18889.get(class_3804.class_3805.LIGHT_BLOCKING)).method_17240(i, k);
		BlockState blockState2 = this.getBlockState(blockPos);
		if (blockState2 == blockState) {
			return null;
		} else {
			Block block = blockState.getBlock();
			Block block2 = blockState2.getBlock();
			ChunkSection chunkSection = this.chunkSections[j >> 4];
			boolean bl2 = false;
			if (chunkSection == EMPTY) {
				if (blockState.isAir()) {
					return null;
				}

				chunkSection = new ChunkSection(j >> 4 << 4, this.world.dimension.isOverworld());
				this.chunkSections[j >> 4] = chunkSection;
				bl2 = j >= l;
			}

			chunkSection.setBlockState(i, j & 15, k, blockState);
			((class_3804)this.field_18889.get(class_3804.class_3805.MOTION_BLOCKING)).method_17242(i, j, k, blockState);
			((class_3804)this.field_18889.get(class_3804.class_3805.MOTION_BLOCKING_NO_LEAVES)).method_17242(i, j, k, blockState);
			((class_3804)this.field_18889.get(class_3804.class_3805.OCEAN_FLOOR)).method_17242(i, j, k, blockState);
			((class_3804)this.field_18889.get(class_3804.class_3805.WORLD_SURFACE)).method_17242(i, j, k, blockState);
			if (!this.world.isClient) {
				blockState2.onStateReplaced(this.world, blockPos, blockState, bl);
			} else if (block2 != block && block2 instanceof BlockEntityProvider) {
				this.world.removeBlockEntity(blockPos);
			}

			if (chunkSection.getBlockState(i, j & 15, k).getBlock() != block) {
				return null;
			} else {
				if (bl2) {
					this.calculateSkyLight();
				} else {
					int m = blockState.method_16885(this.world, blockPos);
					int n = blockState2.method_16885(this.world, blockPos);
					this.method_3917(i, j, k, blockState);
					if (m != n && (m < n || this.method_17071(LightType.SKY, blockPos) > 0 || this.method_17071(LightType.BLOCK, blockPos) > 0)) {
						this.setColumnLightOutdated(i, k);
					}
				}

				if (block2 instanceof BlockEntityProvider) {
					BlockEntity blockEntity = this.getBlockEntity(blockPos, Chunk.Status.CHECK);
					if (blockEntity != null) {
						blockEntity.resetBlock();
					}
				}

				if (!this.world.isClient) {
					blockState.onBlockAdded(this.world, blockPos, blockState2);
				}

				if (block instanceof BlockEntityProvider) {
					BlockEntity blockEntity2 = this.getBlockEntity(blockPos, Chunk.Status.CHECK);
					if (blockEntity2 == null) {
						blockEntity2 = ((BlockEntityProvider)block).createBlockEntity(this.world);
						this.world.setBlockEntity(blockPos, blockEntity2);
					} else {
						blockEntity2.resetBlock();
					}
				}

				this.modified = true;
				return blockState2;
			}
		}
	}

	public int method_17071(LightType lightType, BlockPos blockPos) {
		return this.method_9132(lightType, blockPos, this.world.method_16393().isOverworld());
	}

	@Override
	public int method_9132(LightType lightType, BlockPos blockPos, boolean bl) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		int l = j >> 4;
		if (l >= 0 && l <= this.chunkSections.length - 1) {
			ChunkSection chunkSection = this.chunkSections[l];
			if (chunkSection == EMPTY) {
				return this.method_9148(blockPos) ? lightType.defaultValue : 0;
			} else if (lightType == LightType.SKY) {
				return !bl ? 0 : chunkSection.getSkyLight(i, j & 15, k);
			} else {
				return lightType == LightType.BLOCK ? chunkSection.getBlockLight(i, j & 15, k) : lightType.defaultValue;
			}
		} else {
			return (lightType != LightType.SKY || !bl) && lightType != LightType.BLOCK ? 0 : lightType.defaultValue;
		}
	}

	public void method_17072(LightType lightType, BlockPos blockPos, int i) {
		this.method_3891(lightType, this.world.method_16393().isOverworld(), blockPos, i);
	}

	@Override
	public void method_3891(LightType lightType, boolean bl, BlockPos blockPos, int i) {
		int j = blockPos.getX() & 15;
		int k = blockPos.getY();
		int l = blockPos.getZ() & 15;
		int m = k >> 4;
		if (m < 16 && m >= 0) {
			ChunkSection chunkSection = this.chunkSections[m];
			if (chunkSection == EMPTY) {
				if (i == lightType.defaultValue) {
					return;
				}

				chunkSection = new ChunkSection(m << 4, bl);
				this.chunkSections[m] = chunkSection;
				this.calculateSkyLight();
			}

			if (lightType == LightType.SKY) {
				if (this.world.dimension.isOverworld()) {
					chunkSection.setSkyLight(j, k & 15, l, i);
				}
			} else if (lightType == LightType.BLOCK) {
				chunkSection.setBlockLight(j, k & 15, l, i);
			}

			this.modified = true;
		}
	}

	public int getLightLevel(BlockPos pos, int darkness) {
		return this.method_16993(pos, darkness, this.world.method_16393().isOverworld());
	}

	@Override
	public int method_16993(BlockPos blockPos, int i, boolean bl) {
		int j = blockPos.getX() & 15;
		int k = blockPos.getY();
		int l = blockPos.getZ() & 15;
		int m = k >> 4;
		if (m >= 0 && m <= this.chunkSections.length - 1) {
			ChunkSection chunkSection = this.chunkSections[m];
			if (chunkSection != EMPTY) {
				int n = bl ? chunkSection.getSkyLight(j, k & 15, l) : 0;
				n -= i;
				int o = chunkSection.getBlockLight(j, k & 15, l);
				if (o > n) {
					n = o;
				}

				return n;
			} else {
				return bl && i < LightType.SKY.defaultValue ? LightType.SKY.defaultValue - i : 0;
			}
		} else {
			return 0;
		}
	}

	@Override
	public void method_3887(Entity entity) {
		this.containsEntities = true;
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		if (i != this.chunkX || j != this.chunkZ) {
			LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", i, j, this.chunkX, this.chunkZ, entity);
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

	public void method_17073(class_3804.class_3805 arg, long[] ls) {
		((class_3804)this.field_18889.get(arg)).method_17244(ls);
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

	@Override
	public boolean method_9148(BlockPos blockPos) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getY();
		int k = blockPos.getZ() & 15;
		return j >= ((class_3804)this.field_18889.get(class_3804.class_3805.LIGHT_BLOCKING)).method_17240(i, k);
	}

	@Override
	public int method_16992(class_3804.class_3805 arg, int i, int j) {
		return ((class_3804)this.field_18889.get(arg)).method_17240(i & 15, j & 15) - 1;
	}

	@Nullable
	private BlockEntity createBlockEntity(BlockPos pos) {
		BlockState blockState = this.getBlockState(pos);
		Block block = blockState.getBlock();
		return !block.hasBlockEntity() ? null : ((BlockEntityProvider)block).createBlockEntity(this.world);
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return this.getBlockEntity(pos, Chunk.Status.CHECK);
	}

	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos, Chunk.Status status) {
		BlockEntity blockEntity = (BlockEntity)this.blockEntities.get(pos);
		if (blockEntity == null) {
			NbtCompound nbtCompound = (NbtCompound)this.field_18888.remove(pos);
			if (nbtCompound != null) {
				BlockEntity blockEntity2 = this.method_17074(pos, nbtCompound);
				if (blockEntity2 != null) {
					return blockEntity2;
				}
			}
		}

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

	@Override
	public void method_9136(BlockPos blockPos, BlockEntity blockEntity) {
		blockEntity.setWorld(this.world);
		blockEntity.setPosition(blockPos);
		if (this.getBlockState(blockPos).getBlock() instanceof BlockEntityProvider) {
			if (this.blockEntities.containsKey(blockPos)) {
				((BlockEntity)this.blockEntities.get(blockPos)).markRemoved();
			}

			blockEntity.cancelRemoval();
			this.blockEntities.put(blockPos.toImmutable(), blockEntity);
		}
	}

	@Override
	public void method_16995(NbtCompound nbtCompound) {
		this.field_18888.put(new BlockPos(nbtCompound.getInt("x"), nbtCompound.getInt("y"), nbtCompound.getInt("z")), nbtCompound);
	}

	@Override
	public void method_9150(BlockPos blockPos) {
		if (this.loaded) {
			BlockEntity blockEntity = (BlockEntity)this.blockEntities.remove(blockPos);
			if (blockEntity != null) {
				blockEntity.markRemoved();
			}
		}
	}

	public void loadToWorld() {
		this.loaded = true;
		this.world.addBlockEntities(this.blockEntities.values());

		for (TypeFilterableList<Entity> typeFilterableList : this.entities) {
			this.world.method_16328(typeFilterableList.stream().filter(entity -> !(entity instanceof PlayerEntity)));
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

	public void method_17070(@Nullable Entity entity, Box box, List<Entity> list, Predicate<? super Entity> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entities.length - 1);
		j = MathHelper.clamp(j, 0, this.entities.length - 1);

		for (int k = i; k <= j; k++) {
			if (!this.entities[k].isEmpty()) {
				for (Entity entity2 : this.entities[k]) {
					if (entity2.getBoundingBox().intersects(box) && entity2 != entity) {
						if (predicate == null || predicate.test(entity2)) {
							list.add(entity2);
						}

						Entity[] entitys = entity2.getParts();
						if (entitys != null) {
							for (Entity entity3 : entitys) {
								if (entity3 != entity && entity3.getBoundingBox().intersects(box) && (predicate == null || predicate.test(entity3))) {
									list.add(entity3);
								}
							}
						}
					}
				}
			}
		}
	}

	public <T extends Entity> void method_17075(Class<? extends T> class_, Box box, List<T> list, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((box.minY - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxY + 2.0) / 16.0);
		i = MathHelper.clamp(i, 0, this.entities.length - 1);
		j = MathHelper.clamp(j, 0, this.entities.length - 1);

		for (int k = i; k <= j; k++) {
			for (T entity : this.entities[k].method_10806(class_)) {
				if (entity.getBoundingBox().intersects(box) && (predicate == null || predicate.test(entity))) {
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

	public boolean isEmpty() {
		return false;
	}

	public void populateBlockEntities(boolean runningBehind) {
		if (this.isSkyLightOutdated && this.world.dimension.isOverworld() && !runningBehind) {
			this.recheckSkyLightGaps(this.world.isClient);
		}

		this.blockEntitiesPopulated = true;

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
		return this.field_18883.method_17049(class_3786.POSTPROCESSED);
	}

	public boolean hasPopulatedBlockEntities() {
		return this.blockEntitiesPopulated;
	}

	@Override
	public ChunkPos method_3920() {
		return this.field_18886;
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
			LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", chunkSections.length, this.chunkSections.length);
		} else {
			System.arraycopy(chunkSections, 0, this.chunkSections, 0, this.chunkSections.length);
		}
	}

	public void method_3895(PacketByteBuf packet, int i, boolean bl) {
		if (bl) {
			this.blockEntities.clear();
		} else {
			Iterator<BlockPos> iterator = this.blockEntities.keySet().iterator();

			while (iterator.hasNext()) {
				BlockPos blockPos = (BlockPos)iterator.next();
				int j = blockPos.getY() >> 4;
				if ((i & 1 << j) != 0) {
					iterator.remove();
				}
			}
		}

		boolean bl2 = this.world.dimension.isOverworld();

		for (int k = 0; k < this.chunkSections.length; k++) {
			ChunkSection chunkSection = this.chunkSections[k];
			if ((i & 1 << k) == 0) {
				if (bl && chunkSection != EMPTY) {
					this.chunkSections[k] = EMPTY;
				}
			} else {
				if (chunkSection == EMPTY) {
					chunkSection = new ChunkSection(k << 4, bl2);
					this.chunkSections[k] = chunkSection;
				}

				chunkSection.getBlockData().read(packet);
				packet.readBytes(chunkSection.getBlockLight().getValue());
				if (bl2) {
					packet.readBytes(chunkSection.getSkyLight().getValue());
				}
			}
		}

		if (bl) {
			for (int l = 0; l < this.field_18887.length; l++) {
				this.field_18887[l] = Registry.BIOME.getByRawId(packet.readInt());
			}
		}

		for (int m = 0; m < this.chunkSections.length; m++) {
			if (this.chunkSections[m] != EMPTY && (i & 1 << m) != 0) {
				this.chunkSections[m].calculateCounts();
			}
		}

		this.generateHeightmap();

		for (BlockEntity blockEntity : this.blockEntities.values()) {
			blockEntity.resetBlock();
		}
	}

	public Biome method_17088(BlockPos blockPos) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getZ() & 15;
		return this.field_18887[j << 4 | i];
	}

	@Override
	public Biome[] method_17007() {
		return this.field_18887;
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
					if (this.chunkSections[j] == EMPTY && bl || this.chunkSections[j] != EMPTY && this.chunkSections[j].getBlockState(k, m, l).isAir()) {
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

	public boolean isLoaded() {
		return this.loaded;
	}

	public void setChunkLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public World getWorld() {
		return this.world;
	}

	public Set<class_3804.class_3805> method_17063() {
		return this.field_18889.keySet();
	}

	public class_3804 method_17079(class_3804.class_3805 arg) {
		return (class_3804)this.field_18889.get(arg);
	}

	public Map<BlockPos, BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public TypeFilterableList<Entity>[] getEntities() {
		return this.entities;
	}

	@Override
	public NbtCompound method_17008(BlockPos blockPos) {
		return (NbtCompound)this.field_18888.get(blockPos);
	}

	@Override
	public class_3604<Block> method_17011() {
		return this.field_18894;
	}

	@Override
	public class_3604<Fluid> method_17012() {
		return this.field_18895;
	}

	@Override
	public BitSet method_16991(class_3801.class_3802 arg) {
		throw new RuntimeException("Not yet implemented");
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void setHasEntities(boolean containsEntities) {
		this.containsEntities = containsEntities;
	}

	@Override
	public void method_9143(long l) {
		this.lastSaveTime = l;
	}

	@Nullable
	@Override
	public class_3992 method_16996(String string) {
		return (class_3992)this.field_18891.get(string);
	}

	@Override
	public void method_16998(String string, class_3992 arg) {
		this.field_18891.put(string, arg);
	}

	@Override
	public Map<String, class_3992> method_17004() {
		return this.field_18891;
	}

	public void method_17076(Map<String, class_3992> map) {
		this.field_18891.clear();
		this.field_18891.putAll(map);
	}

	@Nullable
	@Override
	public LongSet method_17002(String string) {
		return (LongSet)this.field_18892.computeIfAbsent(string, stringx -> new LongOpenHashSet());
	}

	@Override
	public void method_16997(String string, long l) {
		((LongSet)this.field_18892.computeIfAbsent(string, stringx -> new LongOpenHashSet())).add(l);
	}

	@Override
	public Map<String, LongSet> method_17006() {
		return this.field_18892;
	}

	public void method_17080(Map<String, LongSet> map) {
		this.field_18892.clear();
		this.field_18892.putAll(map);
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

	public void method_17064() {
		if (!this.field_18883.method_17049(class_3786.POSTPROCESSED) && this.field_18884 == 8) {
			ChunkPos chunkPos = this.method_3920();

			for (int i = 0; i < this.field_18893.length; i++) {
				if (this.field_18893[i] != null) {
					ShortListIterator blockPos2 = this.field_18893[i].iterator();

					while (blockPos2.hasNext()) {
						Short short_ = (Short)blockPos2.next();
						BlockPos blockPos = ChunkBlockStateStorage.method_17116(short_, i, chunkPos);
						BlockState blockState = this.world.getBlockState(blockPos);
						BlockState blockState2 = Block.method_16583(blockState, this.world, blockPos);
						this.world.setBlockState(blockPos, blockState2, 20);
					}

					this.field_18893[i].clear();
				}
			}

			if (this.field_18894 instanceof class_3789) {
				((class_3789)this.field_18894).method_17149(this.world.getBlockTickScheduler(), blockPosx -> this.world.getBlockState(blockPosx).getBlock());
			}

			if (this.field_18895 instanceof class_3789) {
				((class_3789)this.field_18895).method_17149(this.world.method_16340(), blockPosx -> this.world.getFluidState(blockPosx).getFluid());
			}

			for (BlockPos blockPos2 : new HashSet(this.field_18888.keySet())) {
				this.getBlockEntity(blockPos2);
			}

			this.field_18888.clear();
			this.method_16990(class_3786.POSTPROCESSED);
			this.field_18890.method_17154(this);
		}
	}

	@Nullable
	private BlockEntity method_17074(BlockPos blockPos, NbtCompound nbtCompound) {
		BlockEntity blockEntity;
		if ("DUMMY".equals(nbtCompound.getString("id"))) {
			Block block = this.getBlockState(blockPos).getBlock();
			if (block instanceof BlockEntityProvider) {
				blockEntity = ((BlockEntityProvider)block).createBlockEntity(this.world);
			} else {
				blockEntity = null;
				LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", blockPos, this.getBlockState(blockPos));
			}
		} else {
			blockEntity = BlockEntity.method_16781(nbtCompound);
		}

		if (blockEntity != null) {
			blockEntity.setPosition(blockPos);
			this.addBlockEntity(blockEntity);
		} else {
			LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", this.getBlockState(blockPos), blockPos);
		}

		return blockEntity;
	}

	public class_3790 method_17065() {
		return this.field_18890;
	}

	public ShortList[] method_17066() {
		return this.field_18893;
	}

	public void method_17077(short s, int i) {
		ChunkBlockStateStorage.method_17119(this.field_18893, i).add(s);
	}

	@Override
	public class_3786 method_17009() {
		return this.field_18883;
	}

	@Override
	public void method_16990(class_3786 arg) {
		this.field_18883 = arg;
	}

	public void method_17083(String string) {
		this.method_16990(class_3786.method_17050(string));
	}

	public void method_17067() {
		this.field_18884++;
		if (this.field_18884 > 8) {
			throw new RuntimeException("Error while adding chunk to cache. Too many neighbors");
		} else {
			if (this.method_17069()) {
				((ThreadExecutor)this.world).submit(this::method_17064);
			}
		}
	}

	public void method_17068() {
		this.field_18884--;
		if (this.field_18884 < 0) {
			throw new RuntimeException("Error while removing chunk from cache. Not enough neighbors");
		}
	}

	public boolean method_17069() {
		return this.field_18884 == 8;
	}

	public static enum Status {
		IMMEDIATE,
		QUEUED,
		CHECK;
	}
}
