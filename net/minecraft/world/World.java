package net.minecraft.world;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_3593;
import net.minecraft.class_3595;
import net.minecraft.class_3600;
import net.minecraft.class_3804;
import net.minecraft.class_4070;
import net.minecraft.class_4079;
import net.minecraft.class_4488;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.material.Material;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.predicate.block.BlockMaterialPredicate;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.Sound;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.IntObjectStorage;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.village.VillageState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.WorldSaveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements class_3593, IWorld, class_3600, AutoCloseable {
	protected static final Logger field_17503 = LogManager.getLogger();
	private static final Direction[] field_17502 = Direction.values();
	private int seaLevel = 63;
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
	@Nullable
	private final class_4070 field_17501;
	protected VillageState villageState;
	public final Profiler profiler;
	public final boolean isClient;
	protected boolean spawnAnimals = true;
	protected boolean spawnMonsters = true;
	private boolean iteratingTickingBlockEntities;
	private final WorldBorder border;
	int[] updateLightBlocks = new int[32768];

	protected World(SaveHandler saveHandler, @Nullable class_4070 arg, LevelProperties levelProperties, Dimension dimension, Profiler profiler, boolean bl) {
		this.saveHandler = saveHandler;
		this.field_17501 = arg;
		this.profiler = profiler;
		this.levelProperties = levelProperties;
		this.dimension = dimension;
		this.isClient = bl;
		this.border = dimension.createWorldBorder();
	}

	@Override
	public Biome method_8577(BlockPos blockPos) {
		if (this.method_16359(blockPos)) {
			Chunk chunk = this.getChunk(blockPos);

			try {
				return chunk.method_17088(blockPos);
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Getting biome");
				CrashReportSection crashReportSection = crashReport.addElement("Coordinates of biome request");
				crashReportSection.add("Location", (CrashCallable<String>)(() -> CrashReportSection.createPositionString(blockPos)));
				throw new CrashException(crashReport);
			}
		} else {
			return this.chunkProvider.method_17046().method_17020().method_16480(blockPos, Biomes.PLAINS);
		}
	}

	protected abstract ChunkProvider getChunkCache();

	public void setPropertiesInitialized(LevelInfo info) {
		this.levelProperties.setInitialized(true);
	}

	@Override
	public boolean method_16390() {
		return this.isClient;
	}

	@Nullable
	public MinecraftServer getServer() {
		return null;
	}

	public void setDefaultSpawnClient() {
		this.setSpawnPos(new BlockPos(8, 64, 8));
	}

	public BlockState method_8540(BlockPos pos) {
		BlockPos blockPos = new BlockPos(pos.getX(), this.method_8483(), pos.getZ());

		while (!this.method_8579(blockPos.up())) {
			blockPos = blockPos.up();
		}

		return this.getBlockState(blockPos);
	}

	public static boolean method_11479(BlockPos blockPos) {
		return !method_11475(blockPos) && blockPos.getX() >= -30000000 && blockPos.getZ() >= -30000000 && blockPos.getX() < 30000000 && blockPos.getZ() < 30000000;
	}

	public static boolean method_11475(BlockPos blockPos) {
		return blockPos.getY() < 0 || blockPos.getY() >= 256;
	}

	@Override
	public boolean method_8579(BlockPos blockPos) {
		return this.getBlockState(blockPos).isAir();
	}

	public Chunk getChunk(BlockPos pos) {
		return this.method_16347(pos.getX() >> 4, pos.getZ() >> 4);
	}

	public Chunk method_16347(int chunkX, int chunkZ) {
		Chunk chunk = this.chunkProvider.method_17044(chunkX, chunkZ, true, true);
		if (chunk == null) {
			throw new IllegalStateException("Should always be able to create a chunk!");
		} else {
			return chunk;
		}
	}

	@Override
	public boolean setBlockState(BlockPos blockPos, BlockState blockState, int i) {
		if (method_11475(blockPos)) {
			return false;
		} else if (!this.isClient && this.levelProperties.getGeneratorType() == LevelGeneratorType.DEBUG) {
			return false;
		} else {
			Chunk chunk = this.getChunk(blockPos);
			Block block = blockState.getBlock();
			BlockState blockState2 = chunk.method_16994(blockPos, blockState, (i & 64) != 0);
			if (blockState2 == null) {
				return false;
			} else {
				BlockState blockState3 = this.getBlockState(blockPos);
				if (blockState3.method_16885(this, blockPos) != blockState2.method_16885(this, blockPos) || blockState3.getLuminance() != blockState2.getLuminance()) {
					this.profiler.push("checkLight");
					this.method_8568(blockPos);
					this.profiler.pop();
				}

				if (blockState3 == blockState) {
					if (blockState2 != blockState3) {
						this.onRenderRegionUpdate(blockPos, blockPos);
					}

					if ((i & 2) != 0 && (!this.isClient || (i & 4) == 0) && chunk.isPopulated()) {
						this.method_11481(blockPos, blockState2, blockState, i);
					}

					if (!this.isClient && (i & 1) != 0) {
						this.method_16342(blockPos, blockState2.getBlock());
						if (blockState.method_16910()) {
							this.updateHorizontalAdjacent(blockPos, block);
						}
					}

					if ((i & 16) == 0) {
						int j = i & -2;
						blockState2.method_16888(this, blockPos, j);
						blockState.method_16876(this, blockPos, j);
						blockState.method_16888(this, blockPos, j);
					}
				}

				return true;
			}
		}
	}

	@Override
	public boolean method_8553(BlockPos blockPos) {
		FluidState fluidState = this.getFluidState(blockPos);
		return this.setBlockState(blockPos, fluidState.method_17813(), 3);
	}

	@Override
	public boolean method_8535(BlockPos blockPos, boolean bl) {
		BlockState blockState = this.getBlockState(blockPos);
		if (blockState.isAir()) {
			return false;
		} else {
			FluidState fluidState = this.getFluidState(blockPos);
			this.syncGlobalEvent(2001, blockPos, Block.getRawIdFromState(blockState));
			if (bl) {
				blockState.method_16867(this, blockPos, 0);
			}

			return this.setBlockState(blockPos, fluidState.method_17813(), 3);
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

	@Override
	public void method_16342(BlockPos blockPos, Block block) {
		if (this.levelProperties.getGeneratorType() != LevelGeneratorType.DEBUG) {
			this.updateNeighborsAlways(blockPos, block);
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

	public void updateNeighborsAlways(BlockPos pos, Block block) {
		this.updateNeighbor(pos.west(), block, pos);
		this.updateNeighbor(pos.east(), block, pos);
		this.updateNeighbor(pos.down(), block, pos);
		this.updateNeighbor(pos.up(), block, pos);
		this.updateNeighbor(pos.north(), block, pos);
		this.updateNeighbor(pos.south(), block, pos);
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
				blockState.neighborUpdate(this, pos, sourceBlock, sourcePos);
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Exception while updating neighbours");
				CrashReportSection crashReportSection = crashReport.addElement("Block being updated");
				crashReportSection.add("Source block type", (CrashCallable<String>)(() -> {
					try {
						return String.format("ID #%s (%s // %s)", Registry.BLOCK.getId(sourceBlock), sourceBlock.getTranslationKey(), sourceBlock.getClass().getCanonicalName());
					} catch (Throwable var2) {
						return "ID #" + Registry.BLOCK.getId(sourceBlock);
					}
				}));
				CrashReportSection.addBlockInfo(crashReportSection, pos, blockState);
				throw new CrashException(crashReport);
			}
		}
	}

	@Override
	public boolean method_8555(BlockPos blockPos) {
		return this.getChunk(blockPos).method_9148(blockPos);
	}

	@Override
	public int method_16379(BlockPos blockPos, int i) {
		if (blockPos.getX() < -30000000 || blockPos.getZ() < -30000000 || blockPos.getX() >= 30000000 || blockPos.getZ() >= 30000000) {
			return 15;
		} else if (blockPos.getY() < 0) {
			return 0;
		} else {
			if (blockPos.getY() >= 256) {
				blockPos = new BlockPos(blockPos.getX(), 255, blockPos.getZ());
			}

			return this.getChunk(blockPos).getLightLevel(blockPos, i);
		}
	}

	@Override
	public int method_16372(class_3804.class_3805 arg, int i, int j) {
		int l;
		if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
			if (this.method_8487(i >> 4, j >> 4, true)) {
				l = this.method_16347(i >> 4, j >> 4).method_16992(arg, i & 15, j & 15) + 1;
			} else {
				l = 0;
			}
		} else {
			l = this.method_8483() + 1;
		}

		return l;
	}

	@Deprecated
	public int getMinimumChunkHeightmap(int x, int z) {
		if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
			if (!this.method_8487(x >> 4, z >> 4, true)) {
				return 0;
			} else {
				Chunk chunk = this.method_16347(x >> 4, z >> 4);
				return chunk.getMinimumHeightMap();
			}
		} else {
			return this.method_8483() + 1;
		}
	}

	public int getLuminance(LightType lightType, BlockPos pos) {
		if (!this.dimension.isOverworld() && lightType == LightType.SKY) {
			return 0;
		} else {
			if (pos.getY() < 0) {
				pos = new BlockPos(pos.getX(), 0, pos.getZ());
			}

			if (!method_11479(pos)) {
				return lightType.defaultValue;
			} else if (!this.method_16359(pos)) {
				return lightType.defaultValue;
			} else if (this.getBlockState(pos).method_16889(this, pos)) {
				int i = this.method_16370(lightType, pos.up());
				int j = this.method_16370(lightType, pos.east());
				int k = this.method_16370(lightType, pos.west());
				int l = this.method_16370(lightType, pos.south());
				int m = this.method_16370(lightType, pos.north());
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
				return this.getChunk(pos).method_17071(lightType, pos);
			}
		}
	}

	@Override
	public int method_16370(LightType lightType, BlockPos blockPos) {
		if (blockPos.getY() < 0) {
			blockPos = new BlockPos(blockPos.getX(), 0, blockPos.getZ());
		}

		if (!method_11479(blockPos)) {
			return lightType.defaultValue;
		} else {
			return !this.method_16359(blockPos) ? lightType.defaultValue : this.getChunk(blockPos).method_17071(lightType, blockPos);
		}
	}

	@Override
	public void method_16403(LightType lightType, BlockPos blockPos, int i) {
		if (method_11479(blockPos)) {
			if (this.method_16359(blockPos)) {
				this.getChunk(blockPos).method_17072(lightType, blockPos, i);
				this.onLightUpdate(blockPos);
			}
		}
	}

	public void onLightUpdate(BlockPos pos) {
		for (int i = 0; i < this.eventListeners.size(); i++) {
			((WorldEventListener)this.eventListeners.get(i)).onLightUpdate(pos);
		}
	}

	@Override
	public int method_8578(BlockPos blockPos, int i) {
		int j = this.getLuminance(LightType.SKY, blockPos);
		int k = this.getLuminance(LightType.BLOCK, blockPos);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (method_11475(pos)) {
			return Blocks.VOID_AIR.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getBlockState(pos);
		}
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		if (method_11475(pos)) {
			return Fluids.EMPTY.getDefaultState();
		} else {
			Chunk chunk = this.getChunk(pos);
			return chunk.getFluidState(pos);
		}
	}

	public boolean isDay() {
		return this.ambientDarkness < 4;
	}

	@Nullable
	public BlockHitResult rayTrace(Vec3d start, Vec3d end) {
		return this.method_3615(start, end, class_4079.NEVER, false, false);
	}

	@Nullable
	public BlockHitResult method_3614(Vec3d vec3d, Vec3d vec3d2, class_4079 arg) {
		return this.method_3615(vec3d, vec3d2, arg, false, false);
	}

	@Nullable
	public BlockHitResult method_3615(Vec3d vec3d, Vec3d vec3d2, class_4079 arg, boolean bl, boolean bl2) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (Double.isNaN(d) || Double.isNaN(e) || Double.isNaN(f)) {
			return null;
		} else if (!Double.isNaN(vec3d2.x) && !Double.isNaN(vec3d2.y) && !Double.isNaN(vec3d2.z)) {
			int i = MathHelper.floor(vec3d2.x);
			int j = MathHelper.floor(vec3d2.y);
			int k = MathHelper.floor(vec3d2.z);
			int l = MathHelper.floor(d);
			int m = MathHelper.floor(e);
			int n = MathHelper.floor(f);
			BlockPos blockPos = new BlockPos(l, m, n);
			BlockState blockState = this.getBlockState(blockPos);
			FluidState fluidState = this.getFluidState(blockPos);
			if (!bl || !blockState.getCollisionShape(this, blockPos).isEmpty()) {
				boolean bl3 = blockState.getBlock().method_400(blockState);
				boolean bl4 = arg.field_19815.test(fluidState);
				if (bl3 || bl4) {
					BlockHitResult blockHitResult = null;
					if (bl3) {
						blockHitResult = Block.method_414(blockState, this, blockPos, vec3d, vec3d2);
					}

					if (blockHitResult == null && bl4) {
						blockHitResult = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)fluidState.method_17810(), 1.0).rayTrace(vec3d, vec3d2, blockPos);
					}

					if (blockHitResult != null) {
						return blockHitResult;
					}
				}
			}

			BlockHitResult blockHitResult2 = null;
			int o = 200;

			while (o-- >= 0) {
				if (Double.isNaN(d) || Double.isNaN(e) || Double.isNaN(f)) {
					return null;
				}

				if (l == i && m == j && n == k) {
					return bl2 ? blockHitResult2 : null;
				}

				boolean bl5 = true;
				boolean bl6 = true;
				boolean bl7 = true;
				double g = 999.0;
				double h = 999.0;
				double p = 999.0;
				if (i > l) {
					g = (double)l + 1.0;
				} else if (i < l) {
					g = (double)l + 0.0;
				} else {
					bl5 = false;
				}

				if (j > m) {
					h = (double)m + 1.0;
				} else if (j < m) {
					h = (double)m + 0.0;
				} else {
					bl6 = false;
				}

				if (k > n) {
					p = (double)n + 1.0;
				} else if (k < n) {
					p = (double)n + 0.0;
				} else {
					bl7 = false;
				}

				double q = 999.0;
				double r = 999.0;
				double s = 999.0;
				double t = vec3d2.x - d;
				double u = vec3d2.y - e;
				double v = vec3d2.z - f;
				if (bl5) {
					q = (g - d) / t;
				}

				if (bl6) {
					r = (h - e) / u;
				}

				if (bl7) {
					s = (p - f) / v;
				}

				if (q == -0.0) {
					q = -1.0E-4;
				}

				if (r == -0.0) {
					r = -1.0E-4;
				}

				if (s == -0.0) {
					s = -1.0E-4;
				}

				Direction direction;
				if (q < r && q < s) {
					direction = i > l ? Direction.WEST : Direction.EAST;
					d = g;
					e += u * q;
					f += v * q;
				} else if (r < s) {
					direction = j > m ? Direction.DOWN : Direction.UP;
					d += t * r;
					e = h;
					f += v * r;
				} else {
					direction = k > n ? Direction.NORTH : Direction.SOUTH;
					d += t * s;
					e += u * s;
					f = p;
				}

				l = MathHelper.floor(d) - (direction == Direction.EAST ? 1 : 0);
				m = MathHelper.floor(e) - (direction == Direction.UP ? 1 : 0);
				n = MathHelper.floor(f) - (direction == Direction.SOUTH ? 1 : 0);
				blockPos = new BlockPos(l, m, n);
				BlockState blockState2 = this.getBlockState(blockPos);
				FluidState fluidState2 = this.getFluidState(blockPos);
				if (!bl || blockState2.getMaterial() == Material.PORTAL || !blockState2.getCollisionShape(this, blockPos).isEmpty()) {
					boolean bl8 = blockState2.getBlock().method_400(blockState2);
					boolean bl9 = arg.field_19815.test(fluidState2);
					if (!bl8 && !bl9) {
						blockHitResult2 = new BlockHitResult(BlockHitResult.Type.MISS, new Vec3d(d, e, f), direction, blockPos);
					} else {
						BlockHitResult blockHitResult3 = null;
						if (bl8) {
							blockHitResult3 = Block.method_414(blockState2, this, blockPos, vec3d, vec3d2);
						}

						if (blockHitResult3 == null && bl9) {
							blockHitResult3 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, (double)fluidState2.method_17810(), 1.0).rayTrace(vec3d, vec3d2, blockPos);
						}

						if (blockHitResult3 != null) {
							return blockHitResult3;
						}
					}
				}
			}

			return bl2 ? blockHitResult2 : null;
		} else {
			return null;
		}
	}

	@Override
	public void playSound(@Nullable PlayerEntity playerEntity, BlockPos blockPos, Sound sound, SoundCategory soundCategory, float f, float g) {
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

	@Override
	public void method_16343(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.eventListeners.size(); j++) {
			((WorldEventListener)this.eventListeners.get(j)).method_3746(particleEffect, particleEffect.particleType().getAlwaysShow(), d, e, f, g, h, i);
		}
	}

	public void method_16323(ParticleEffect particleEffect, boolean bl, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.eventListeners.size(); j++) {
			((WorldEventListener)this.eventListeners.get(j)).method_3746(particleEffect, particleEffect.particleType().getAlwaysShow() || bl, d, e, f, g, h, i);
		}
	}

	public void method_16333(ParticleEffect particleEffect, double d, double e, double f, double g, double h, double i) {
		for (int j = 0; j < this.eventListeners.size(); j++) {
			((WorldEventListener)this.eventListeners.get(j)).method_13696(particleEffect, false, true, d, e, f, g, h, i);
		}
	}

	public boolean addEntity(Entity entity) {
		this.entities.add(entity);
		return true;
	}

	@Override
	public boolean method_3686(Entity entity) {
		int i = MathHelper.floor(entity.x / 16.0);
		int j = MathHelper.floor(entity.z / 16.0);
		boolean bl = entity.teleporting;
		if (entity instanceof PlayerEntity) {
			bl = true;
		}

		if (!bl && !this.method_8487(i, j, false)) {
			return false;
		} else {
			if (entity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)entity;
				this.playerEntities.add(playerEntity);
				this.updateSleepingStatus();
			}

			this.method_16347(i, j).method_3887(entity);
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
		if (entity.updateNeeded && this.method_8487(i, j, true)) {
			this.method_16347(i, j).removeEntity(entity);
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

	public int method_3597(float f) {
		float g = this.method_16349(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		h = 1.0F - h;
		return (int)(h * 11.0F);
	}

	public float method_3649(float f) {
		float g = this.method_16349(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.2F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		h = 1.0F - h;
		h = (float)((double)h * (1.0 - (double)(this.getRainGradient(f) * 5.0F) / 16.0));
		h = (float)((double)h * (1.0 - (double)(this.getThunderGradient(f) * 5.0F) / 16.0));
		return h * 0.8F + 0.2F;
	}

	public Vec3d method_3631(Entity entity, float f) {
		float g = this.method_16349(f);
		float h = MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		int i = MathHelper.floor(entity.x);
		int j = MathHelper.floor(entity.y);
		int k = MathHelper.floor(entity.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		Biome biome = this.method_8577(blockPos);
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

	public float getSkyAngleRadians(float tickDelta) {
		float f = this.method_16349(tickDelta);
		return f * (float) (Math.PI * 2);
	}

	public Vec3d getCloudColor(float tickDelta) {
		float f = this.method_16349(tickDelta);
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
		float f = this.method_16349(tickDelta);
		return this.dimension.getFogColor(f, tickDelta);
	}

	public float method_3707(float f) {
		float g = this.method_16349(f);
		float h = 1.0F - (MathHelper.cos(g * (float) (Math.PI * 2)) * 2.0F + 0.25F);
		h = MathHelper.clamp(h, 0.0F, 1.0F);
		return h * h * 0.5F;
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
			if (entity2.updateNeeded && this.method_8487(k, l, true)) {
				this.method_16347(k, l).removeEntity(entity2);
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
				if (entity3.updateNeeded && this.method_8487(o, p, true)) {
					this.method_16347(o, p).removeEntity(entity3);
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
				if (this.method_16359(blockPos) && this.border.contains(blockPos)) {
					try {
						this.profiler.push((Supplier<String>)(() -> String.valueOf(BlockEntityType.method_16785(blockEntity.method_16780()))));
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
				if (this.method_16359(blockEntity.getPos())) {
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

					if (this.method_16359(blockEntity2.getPos())) {
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
			if (bl && !this.method_16362(i - 32, 0, j - 32, i + 32, 0, j + 32, true)) {
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
				this.profiler.push((Supplier<String>)(() -> Registry.ENTITY_TYPE.getId(entity.method_15557()).toString()));
				entity.tick();
				this.profiler.pop();
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
			if (entity.updateNeeded && this.method_8487(entity.chunkX, entity.chunkZ, true)) {
				this.method_16347(entity.chunkX, entity.chunkZ).removeEntity(entity, entity.chunkY);
			}

			if (!entity.teleportRequested() && !this.method_8487(l, n, true)) {
				entity.updateNeeded = false;
			} else {
				this.method_16347(l, n).method_3887(entity);
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

	@Override
	public boolean method_16368(@Nullable Entity entity, VoxelShape voxelShape) {
		if (voxelShape.isEmpty()) {
			return true;
		} else {
			List<Entity> list = this.getEntities(null, voxelShape.getBoundingBox());

			for (int i = 0; i < list.size(); i++) {
				Entity entity2 = (Entity)list.get(i);
				if (!entity2.removed
					&& entity2.inanimate
					&& entity2 != entity
					&& (entity == null || !entity2.isConnectedThroughVehicle(entity))
					&& VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.method_18049(entity2.getBoundingBox()), BooleanBiFunction.AND)) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean isBoxNotEmpty(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						BlockState blockState = this.getBlockState(pooled.setPosition(o, p, q));
						if (!blockState.isAir()) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	public boolean containsFireSource(Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (this.method_16362(i, k, m, j, l, n, true)) {
			try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							Block block = this.getBlockState(pooled.setPosition(o, p, q)).getBlock();
							if (block == Blocks.FIRE || block == Blocks.LAVA) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	@Nullable
	public BlockState method_16322(Box box, Block block) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (this.method_16362(i, k, m, j, l, n, true)) {
			try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
				for (int o = i; o < j; o++) {
					for (int p = k; p < l; p++) {
						for (int q = m; q < n; q++) {
							BlockState blockState = this.getBlockState(pooled.setPosition(o, p, q));
							if (blockState.getBlock() == block) {
								return blockState;
							}
						}
					}
				}

				return null;
			}
		} else {
			return null;
		}
	}

	public boolean containsMaterial(Box box, Material material) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockMaterialPredicate blockMaterialPredicate = BlockMaterialPredicate.create(material);

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						if (blockMaterialPredicate.test(this.getBlockState(pooled.setPosition(o, p, q)))) {
							return true;
						}
					}
				}
			}

			return false;
		}
	}

	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean destructive) {
		return this.method_16320(entity, null, x, y, z, power, false, destructive);
	}

	public Explosion createExplosion(@Nullable Entity entity, double x, double y, double z, float power, boolean createFire, boolean destructive) {
		return this.method_16320(entity, null, x, y, z, power, createFire, destructive);
	}

	public Explosion method_16320(@Nullable Entity entity, @Nullable DamageSource damageSource, double d, double e, double f, float g, boolean bl, boolean bl2) {
		Explosion explosion = new Explosion(this, entity, d, e, f, g, bl, bl2);
		if (damageSource != null) {
			explosion.method_16294(damageSource);
		}

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
			this.method_8553(pos);
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
		if (method_11475(pos)) {
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
		if (!method_11475(pos)) {
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
		return Block.isShapeFullCube(this.getBlockState(blockPos).getCollisionShape(this, blockPos));
	}

	public boolean method_16338(BlockPos blockPos) {
		if (method_11475(blockPos)) {
			return false;
		} else {
			Chunk chunk = this.chunkProvider.method_17044(blockPos.getX() >> 4, blockPos.getZ() >> 4, false, false);
			return chunk != null && !chunk.isEmpty();
		}
	}

	public boolean method_16339(BlockPos blockPos) {
		return this.method_16338(blockPos) && this.getBlockState(blockPos).method_16913();
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

	public void method_16327(BooleanSupplier booleanSupplier) {
		this.border.method_16975();
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

	public void close() {
		this.chunkProvider.close();
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

	public boolean method_8568(BlockPos pos) {
		boolean bl = false;
		if (this.dimension.isOverworld()) {
			bl |= this.calculateLightAtPos(LightType.SKY, pos);
		}

		return bl | this.calculateLightAtPos(LightType.BLOCK, pos);
	}

	private int getLightAtPos(BlockPos pos, LightType type) {
		if (type == LightType.SKY && this.method_8555(pos)) {
			return 15;
		} else {
			BlockState blockState = this.getBlockState(pos);
			int i = type == LightType.SKY ? 0 : blockState.getLuminance();
			int j = blockState.method_16885(this, pos);
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
				try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
					for (Direction direction : field_17502) {
						pooled.set(pos).move(direction);
						int k = this.method_16370(type, pooled) - j;
						if (k > i) {
							i = k;
						}

						if (i >= 14) {
							return i;
						}
					}

					return i;
				}
			}
		}
	}

	public boolean calculateLightAtPos(LightType lightType, BlockPos pos) {
		if (!this.method_16380(pos, 17, false)) {
			return false;
		} else {
			int i = 0;
			int j = 0;
			this.profiler.push("getBrightness");
			int k = this.method_16370(lightType, pos);
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
					int u = this.method_16370(lightType, blockPos);
					if (u == t) {
						this.method_16403(lightType, blockPos, 0);
						if (t > 0) {
							int v = MathHelper.abs(q - m);
							int w = MathHelper.abs(r - n);
							int x = MathHelper.abs(s - o);
							if (v + w + x < 17) {
								try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
									for (Direction direction : field_17502) {
										int y = q + direction.getOffsetX();
										int z = r + direction.getOffsetY();
										int aa = s + direction.getOffsetZ();
										pooled.setPosition(y, z, aa);
										int ab = Math.max(1, this.getBlockState(pooled).method_16885(this, pooled));
										u = this.method_16370(lightType, pooled);
										if (u == t - ab && j < this.updateLightBlocks.length) {
											this.updateLightBlocks[j++] = y - m + 32 | z - n + 32 << 6 | aa - o + 32 << 12 | t - ab << 18;
										}
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
				int ac = this.updateLightBlocks[i++];
				int ad = (ac & 63) - 32 + m;
				int ae = (ac >> 6 & 63) - 32 + n;
				int af = (ac >> 12 & 63) - 32 + o;
				BlockPos blockPos2 = new BlockPos(ad, ae, af);
				int ag = this.method_16370(lightType, blockPos2);
				int ah = this.getLightAtPos(blockPos2, lightType);
				if (ah != ag) {
					this.method_16403(lightType, blockPos2, ah);
					if (ah > ag) {
						int ai = Math.abs(ad - m);
						int aj = Math.abs(ae - n);
						int ak = Math.abs(af - o);
						boolean bl = j < this.updateLightBlocks.length - 6;
						if (ai + aj + ak < 17 && bl) {
							if (this.method_16370(lightType, blockPos2.west()) < ah) {
								this.updateLightBlocks[j++] = ad - 1 - m + 32 + (ae - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.method_16370(lightType, blockPos2.east()) < ah) {
								this.updateLightBlocks[j++] = ad + 1 - m + 32 + (ae - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.method_16370(lightType, blockPos2.down()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae - 1 - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.method_16370(lightType, blockPos2.up()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae + 1 - n + 32 << 6) + (af - o + 32 << 12);
							}

							if (this.method_16370(lightType, blockPos2.north()) < ah) {
								this.updateLightBlocks[j++] = ad - m + 32 + (ae - n + 32 << 6) + (af - 1 - o + 32 << 12);
							}

							if (this.method_16370(lightType, blockPos2.south()) < ah) {
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

	@Override
	public Stream<VoxelShape> method_16369(@Nullable Entity entity, VoxelShape voxelShape, VoxelShape voxelShape2, Set<Entity> set) {
		Stream<VoxelShape> stream = IWorld.super.method_16369(entity, voxelShape, voxelShape2, set);
		return entity == null ? stream : Stream.concat(stream, this.method_16289(entity, voxelShape, set));
	}

	@Override
	public List<Entity> method_16288(@Nullable Entity entity, Box box, @Nullable Predicate<? super Entity> predicate) {
		List<Entity> list = Lists.newArrayList();
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.floor((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.floor((box.maxZ + 2.0) / 16.0);

		for (int m = i; m <= j; m++) {
			for (int n = k; n <= l; n++) {
				if (this.method_8487(m, n, true)) {
					this.method_16347(m, n).method_17070(entity, box, list, predicate);
				}
			}
		}

		return list;
	}

	public <T extends Entity> List<T> method_16326(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.newArrayList();

		for (Entity entity : this.loadedEntities) {
			if (class_.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public <T extends Entity> List<T> method_16334(Class<? extends T> class_, Predicate<? super T> predicate) {
		List<T> list = Lists.newArrayList();

		for (Entity entity : this.playerEntities) {
			if (class_.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
				list.add(entity);
			}
		}

		return list;
	}

	public <T extends Entity> List<T> getEntitiesInBox(Class<? extends T> except, Box box) {
		return this.method_16325(except, box, EntityPredicate.field_16705);
	}

	public <T extends Entity> List<T> method_16325(Class<? extends T> class_, Box box, @Nullable Predicate<? super T> predicate) {
		int i = MathHelper.floor((box.minX - 2.0) / 16.0);
		int j = MathHelper.ceil((box.maxX + 2.0) / 16.0);
		int k = MathHelper.floor((box.minZ - 2.0) / 16.0);
		int l = MathHelper.ceil((box.maxZ + 2.0) / 16.0);
		List<T> list = Lists.newArrayList();

		for (int m = i; m < j; m++) {
			for (int n = k; n < l; n++) {
				if (this.method_8487(m, n, true)) {
					this.method_16347(m, n).method_17075(class_, box, list, predicate);
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
			if (entity2 != except && EntityPredicate.field_16705.test(entity2)) {
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

	public int method_16316() {
		return this.loadedEntities.size();
	}

	public void markDirty(BlockPos pos, BlockEntity blockEntity) {
		if (this.method_16359(pos)) {
			this.getChunk(pos).setModified();
		}
	}

	public int method_16324(Class<?> class_, int i) {
		int j = 0;

		for (Entity entity : this.loadedEntities) {
			if (!(entity instanceof MobEntity) || !((MobEntity)entity).isPersistent()) {
				if (class_.isAssignableFrom(entity.getClass())) {
					j++;
				}

				if (j > i) {
					return j;
				}
			}
		}

		return j;
	}

	public void method_16328(Stream<Entity> stream) {
		stream.forEach(entity -> {
			this.loadedEntities.add(entity);
			this.onEntitySpawned(entity);
		});
	}

	public void unloadEntities(Collection<Entity> entities) {
		this.unloadedEntities.addAll(entities);
	}

	@Override
	public int method_8483() {
		return this.seaLevel;
	}

	@Override
	public World method_16348() {
		return this;
	}

	public void setSeaLevel(int seaLevel) {
		this.seaLevel = seaLevel;
	}

	@Override
	public int method_8576(BlockPos blockPos, Direction direction) {
		return this.getBlockState(blockPos).getStrongRedstonePower(this, blockPos, direction);
	}

	public LevelGeneratorType method_8575() {
		return this.levelProperties.getGeneratorType();
	}

	public int getReceivedStrongRedstonePower(BlockPos pos) {
		int i = 0;
		i = Math.max(i, this.method_8576(pos.down(), Direction.DOWN));
		if (i >= 15) {
			return i;
		} else {
			i = Math.max(i, this.method_8576(pos.up(), Direction.UP));
			if (i >= 15) {
				return i;
			} else {
				i = Math.max(i, this.method_8576(pos.north(), Direction.NORTH));
				if (i >= 15) {
					return i;
				} else {
					i = Math.max(i, this.method_8576(pos.south(), Direction.SOUTH));
					if (i >= 15) {
						return i;
					} else {
						i = Math.max(i, this.method_8576(pos.west(), Direction.WEST));
						if (i >= 15) {
							return i;
						} else {
							i = Math.max(i, this.method_8576(pos.east(), Direction.EAST));
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
		return blockState.method_16907() ? this.getReceivedStrongRedstonePower(pos) : blockState.getWeakRedstonePower(this, pos, dir);
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

		for (Direction direction : field_17502) {
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
	@Override
	public PlayerEntity method_16360(double d, double e, double f, double g, Predicate<Entity> predicate) {
		double h = -1.0;
		PlayerEntity playerEntity = null;

		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(i);
			if (predicate.test(playerEntity2)) {
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
			if (EntityPredicate.field_16705.test(playerEntity)) {
				double d = playerEntity.squaredDistanceTo(x, y, z);
				if (maxDistance < 0.0 || d < maxDistance * maxDistance) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_16331(double d, double e, double f, double g) {
		for (PlayerEntity playerEntity : this.playerEntities) {
			if (EntityPredicate.field_16705.test(playerEntity) && EntityPredicate.field_16701.test(playerEntity)) {
				double h = playerEntity.squaredDistanceTo(d, e, f);
				if (g < 0.0 || h < g * g) {
					return true;
				}
			}
		}

		return false;
	}

	@Nullable
	public PlayerEntity method_16318(double d, double e, double f) {
		double g = -1.0;
		PlayerEntity playerEntity = null;

		for (int i = 0; i < this.playerEntities.size(); i++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(i);
			if (EntityPredicate.field_16705.test(playerEntity2)) {
				double h = playerEntity2.squaredDistanceTo(d, playerEntity2.y, e);
				if ((f < 0.0 || h < f * f) && (g == -1.0 || h < g)) {
					g = h;
					playerEntity = playerEntity2;
				}
			}
		}

		return playerEntity;
	}

	@Nullable
	public PlayerEntity method_11484(Entity entity, double d, double e) {
		return this.method_16319(entity.x, entity.y, entity.z, d, e, null, null);
	}

	@Nullable
	public PlayerEntity method_11480(BlockPos blockPos, double d, double e) {
		return this.method_16319(
			(double)((float)blockPos.getX() + 0.5F), (double)((float)blockPos.getY() + 0.5F), (double)((float)blockPos.getZ() + 0.5F), d, e, null, null
		);
	}

	@Nullable
	public PlayerEntity method_16319(
		double d, double e, double f, double g, double h, @Nullable Function<PlayerEntity, Double> function, @Nullable Predicate<PlayerEntity> predicate
	) {
		double i = -1.0;
		PlayerEntity playerEntity = null;

		for (int j = 0; j < this.playerEntities.size(); j++) {
			PlayerEntity playerEntity2 = (PlayerEntity)this.playerEntities.get(j);
			if (!playerEntity2.abilities.invulnerable && playerEntity2.isAlive() && !playerEntity2.isSpectator() && (predicate == null || predicate.test(playerEntity2))
				)
			 {
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
			if (name.equals(playerEntity.method_15540().getString())) {
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

	@Override
	public long method_3581() {
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

	@Override
	public BlockPos method_3585() {
		BlockPos blockPos = new BlockPos(this.levelProperties.getSpawnX(), this.levelProperties.getSpawnY(), this.levelProperties.getSpawnZ());
		if (!this.method_8524().contains(blockPos)) {
			blockPos = this.method_16373(class_3804.class_3805.MOTION_BLOCKING, new BlockPos(this.method_8524().getCenterX(), 0.0, this.method_8524().getCenterZ()));
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
				this.method_16347(i + l, j + m);
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

	@Override
	public ChunkProvider method_3586() {
		return this.chunkProvider;
	}

	public void addBlockAction(BlockPos pos, Block block, int type, int data) {
		this.getBlockState(pos).method_16868(this, pos, type, data);
	}

	@Override
	public SaveHandler method_3587() {
		return this.saveHandler;
	}

	@Override
	public LevelProperties method_3588() {
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
		return this.dimension.isOverworld() && !this.dimension.hasNoSkylight() ? (double)this.getThunderGradient(1.0F) > 0.9 : false;
	}

	public boolean isRaining() {
		return (double)this.getRainGradient(1.0F) > 0.2;
	}

	public boolean hasRain(BlockPos pos) {
		if (!this.isRaining()) {
			return false;
		} else if (!this.method_8555(pos)) {
			return false;
		} else {
			return this.method_16373(class_3804.class_3805.MOTION_BLOCKING, pos).getY() > pos.getY()
				? false
				: this.method_8577(pos).getPrecipitation() == Biome.Precipitation.RAIN;
		}
	}

	public boolean hasHighHumidity(BlockPos pos) {
		Biome biome = this.method_8577(pos);
		return biome.hasHighHumidity();
	}

	@Nullable
	@Override
	public class_4070 method_16399() {
		return this.field_17501;
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

	public double getHorizonHeight() {
		return this.levelProperties.getGeneratorType() == LevelGeneratorType.FLAT ? 0.0 : 63.0;
	}

	public CrashReportSection addToCrashReport(CrashReport report) {
		CrashReportSection crashReportSection = report.addElement("Affected level", 1);
		crashReportSection.add("Level name", this.levelProperties == null ? "????" : this.levelProperties.getLevelName());
		crashReportSection.add("All players", (CrashCallable<String>)(() -> this.playerEntities.size() + " total; " + this.playerEntities));
		crashReportSection.add("Chunk stats", (CrashCallable<String>)(() -> this.chunkProvider.getChunkProviderName()));

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

	public void addFireworkParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, @Nullable NbtCompound nbt) {
	}

	public abstract Scoreboard getScoreboard();

	public void updateHorizontalAdjacent(BlockPos pos, Block block) {
		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			if (this.method_16359(blockPos)) {
				BlockState blockState = this.getBlockState(blockPos);
				if (blockState.getBlock() == Blocks.COMPARATOR) {
					blockState.neighborUpdate(this, blockPos, block, pos);
				} else if (blockState.method_16907()) {
					blockPos = blockPos.offset(direction);
					blockState = this.getBlockState(blockPos);
					if (blockState.getBlock() == Blocks.COMPARATOR) {
						blockState.neighborUpdate(this, blockPos, block, pos);
					}
				}
			}
		}
	}

	@Override
	public LocalDifficulty method_8482(BlockPos blockPos) {
		long l = 0L;
		float f = 0.0F;
		if (this.method_16359(blockPos)) {
			f = this.method_16344();
			l = this.getChunk(blockPos).getInhabitedTime();
		}

		return new LocalDifficulty(this.method_16346(), this.getTimeOfDay(), l, f);
	}

	@Override
	public int method_8520() {
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

	@Override
	public WorldBorder method_8524() {
		return this.border;
	}

	public boolean isChunkInsideSpawnChunks(int chunkX, int chunkZ) {
		BlockPos blockPos = this.method_3585();
		int i = chunkX * 16 + 8 - blockPos.getX();
		int j = chunkZ * 16 + 8 - blockPos.getZ();
		int k = 128;
		return i >= -128 && i <= 128 && j >= -128 && j <= 128;
	}

	public LongSet method_16329() {
		class_3595 lv = this.method_16398(this.dimension.method_11789(), class_3595::new, "chunks");
		return (LongSet)(lv != null ? LongSets.unmodifiable(lv.method_16296()) : LongSets.EMPTY_SET);
	}

	public boolean method_16335(int i, int j) {
		class_3595 lv = this.method_16398(this.dimension.method_11789(), class_3595::new, "chunks");
		return lv != null && lv.method_16296().contains(ChunkPos.getIdFromCoords(i, j));
	}

	public boolean method_16332(int i, int j, boolean bl) {
		String string = "chunks";
		class_3595 lv = this.method_16398(this.dimension.method_11789(), class_3595::new, "chunks");
		if (lv == null) {
			lv = new class_3595("chunks");
			this.method_16397(this.dimension.method_11789(), "chunks", lv);
		}

		long l = ChunkPos.getIdFromCoords(i, j);
		boolean bl2;
		if (bl) {
			bl2 = lv.method_16296().add(l);
			if (bl2) {
				this.method_16347(i, j);
			}
		} else {
			bl2 = lv.method_16296().remove(l);
		}

		lv.setDirty(bl2);
		return bl2;
	}

	public void method_11483(Packet<?> packet) {
		throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
	}

	@Nullable
	public BlockPos method_13688(String string, BlockPos blockPos, int i, boolean bl) {
		return null;
	}

	@Override
	public Dimension method_16393() {
		return this.dimension;
	}

	@Override
	public Random getRandom() {
		return this.random;
	}

	public abstract RecipeDispatcher method_16313();

	public abstract class_4488 method_16314();
}
