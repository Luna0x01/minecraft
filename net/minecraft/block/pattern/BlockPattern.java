package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BlockPattern {
	private final Predicate<CachedBlockPosition>[][][] resultPredicates;
	private final int depth;
	private final int height;
	private final int width;

	public BlockPattern(Predicate<CachedBlockPosition>[][][] predicates) {
		this.resultPredicates = predicates;
		this.depth = predicates.length;
		if (this.depth > 0) {
			this.height = predicates[0].length;
			if (this.height > 0) {
				this.width = predicates[0][0].length;
			} else {
				this.width = 0;
			}
		} else {
			this.height = 0;
			this.width = 0;
		}
	}

	public int method_11746() {
		return this.depth;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	@Nullable
	private BlockPattern.Result testTransform(BlockPos pos, Direction forwards, Direction up, LoadingCache<BlockPos, CachedBlockPosition> cache) {
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				for (int k = 0; k < this.depth; k++) {
					if (!this.resultPredicates[k][j][i].apply(cache.getUnchecked(translate(pos, forwards, up, i, j, k)))) {
						return null;
					}
				}
			}
		}

		return new BlockPattern.Result(pos, forwards, up, cache, this.width, this.height, this.depth);
	}

	@Nullable
	public BlockPattern.Result searchAround(World world, BlockPos pos) {
		LoadingCache<BlockPos, CachedBlockPosition> loadingCache = createLoadingCache(world, false);
		int i = Math.max(Math.max(this.width, this.height), this.depth);

		for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(i - 1, i - 1, i - 1))) {
			for (Direction direction : Direction.values()) {
				for (Direction direction2 : Direction.values()) {
					if (direction2 != direction && direction2 != direction.getOpposite()) {
						BlockPattern.Result result = this.testTransform(blockPos, direction, direction2, loadingCache);
						if (result != null) {
							return result;
						}
					}
				}
			}
		}

		return null;
	}

	public static LoadingCache<BlockPos, CachedBlockPosition> createLoadingCache(World world, boolean forceLoad) {
		return CacheBuilder.newBuilder().build(new BlockPattern.BlockStateCacheLoader(world, forceLoad));
	}

	protected static BlockPos translate(BlockPos pos, Direction forwards, Direction up, int offsetLeft, int offsetDown, int offsetForwards) {
		if (forwards != up && forwards != up.getOpposite()) {
			Vec3i vec3i = new Vec3i(forwards.getOffsetX(), forwards.getOffsetY(), forwards.getOffsetZ());
			Vec3i vec3i2 = new Vec3i(up.getOffsetX(), up.getOffsetY(), up.getOffsetZ());
			Vec3i vec3i3 = vec3i.crossProduct(vec3i2);
			return pos.add(
				vec3i2.getX() * -offsetDown + vec3i3.getX() * offsetLeft + vec3i.getX() * offsetForwards,
				vec3i2.getY() * -offsetDown + vec3i3.getY() * offsetLeft + vec3i.getY() * offsetForwards,
				vec3i2.getZ() * -offsetDown + vec3i3.getZ() * offsetLeft + vec3i.getZ() * offsetForwards
			);
		} else {
			throw new IllegalArgumentException("Invalid forwards & up combination");
		}
	}

	static class BlockStateCacheLoader extends CacheLoader<BlockPos, CachedBlockPosition> {
		private final World world;
		private final boolean forceLoad;

		public BlockStateCacheLoader(World world, boolean bl) {
			this.world = world;
			this.forceLoad = bl;
		}

		public CachedBlockPosition load(BlockPos blockPos) throws Exception {
			return new CachedBlockPosition(this.world, blockPos, this.forceLoad);
		}
	}

	public static class Result {
		private final BlockPos frontTopLeft;
		private final Direction forward;
		private final Direction up;
		private final LoadingCache<BlockPos, CachedBlockPosition> cache;
		private final int width;
		private final int height;
		private final int depth;

		public Result(BlockPos blockPos, Direction direction, Direction direction2, LoadingCache<BlockPos, CachedBlockPosition> loadingCache, int i, int j, int k) {
			this.frontTopLeft = blockPos;
			this.forward = direction;
			this.up = direction2;
			this.cache = loadingCache;
			this.width = i;
			this.height = j;
			this.depth = k;
		}

		public BlockPos getFrontTopLeft() {
			return this.frontTopLeft;
		}

		public Direction getForwards() {
			return this.forward;
		}

		public Direction getUp() {
			return this.up;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public CachedBlockPosition translate(int offsetLeft, int offsetDown, int offsetForwards) {
			return (CachedBlockPosition)this.cache
				.getUnchecked(BlockPattern.translate(this.frontTopLeft, this.getForwards(), this.getUp(), offsetLeft, offsetDown, offsetForwards));
		}

		public String toString() {
			return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forward).add("frontTopLeft", this.frontTopLeft).toString();
		}
	}
}
