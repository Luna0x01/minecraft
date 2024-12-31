package net.minecraft.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import org.apache.commons.lang3.mutable.MutableInt;

public final class ChunkBlockLightProvider extends ChunkLightProvider<BlockLightStorage.Data, BlockLightStorage> {
	private static final Direction[] DIRECTIONS = Direction.values();
	private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();

	public ChunkBlockLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.field_9282, new BlockLightStorage(chunkProvider));
	}

	private int getLightSourceLuminance(long l) {
		int i = BlockPos.unpackLongX(l);
		int j = BlockPos.unpackLongY(l);
		int k = BlockPos.unpackLongZ(l);
		BlockView blockView = this.chunkProvider.getChunk(i >> 4, k >> 4);
		return blockView != null ? blockView.getLuminance(this.mutablePos.set(i, j, k)) : 0;
	}

	@Override
	protected int getPropagatedLevel(long l, long m, int i) {
		if (m == Long.MAX_VALUE) {
			return 15;
		} else if (l == Long.MAX_VALUE) {
			return i + 15 - this.getLightSourceLuminance(m);
		} else if (i >= 15) {
			return i;
		} else {
			int j = Integer.signum(BlockPos.unpackLongX(m) - BlockPos.unpackLongX(l));
			int k = Integer.signum(BlockPos.unpackLongY(m) - BlockPos.unpackLongY(l));
			int n = Integer.signum(BlockPos.unpackLongZ(m) - BlockPos.unpackLongZ(l));
			Direction direction = Direction.fromVector(j, k, n);
			if (direction == null) {
				return 15;
			} else {
				MutableInt mutableInt = new MutableInt();
				BlockState blockState = this.getStateForLighting(m, mutableInt);
				if (mutableInt.getValue() >= 15) {
					return 15;
				} else {
					BlockState blockState2 = this.getStateForLighting(l, null);
					VoxelShape voxelShape = this.getOpaqueShape(blockState2, l, direction);
					VoxelShape voxelShape2 = this.getOpaqueShape(blockState, m, direction.getOpposite());
					return VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2) ? 15 : i + Math.max(1, mutableInt.getValue());
				}
			}
		}
	}

	@Override
	protected void propagateLevel(long l, int i, boolean bl) {
		long m = ChunkSectionPos.fromGlobalPos(l);

		for (Direction direction : DIRECTIONS) {
			long n = BlockPos.offset(l, direction);
			long o = ChunkSectionPos.fromGlobalPos(n);
			if (m == o || this.lightStorage.hasLight(o)) {
				this.propagateLevel(l, n, i, bl);
			}
		}
	}

	@Override
	protected int recalculateLevel(long l, long m, int i) {
		int j = i;
		if (Long.MAX_VALUE != m) {
			int k = this.getPropagatedLevel(Long.MAX_VALUE, l, 0);
			if (i > k) {
				j = k;
			}

			if (j == 0) {
				return j;
			}
		}

		long n = ChunkSectionPos.fromGlobalPos(l);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getLightArray(n, true);

		for (Direction direction : DIRECTIONS) {
			long o = BlockPos.offset(l, direction);
			if (o != m) {
				long p = ChunkSectionPos.fromGlobalPos(o);
				ChunkNibbleArray chunkNibbleArray2;
				if (n == p) {
					chunkNibbleArray2 = chunkNibbleArray;
				} else {
					chunkNibbleArray2 = this.lightStorage.getLightArray(p, true);
				}

				if (chunkNibbleArray2 != null) {
					int q = this.getPropagatedLevel(o, l, this.getCurrentLevelFromArray(chunkNibbleArray2, o));
					if (j > q) {
						j = q;
					}

					if (j == 0) {
						return j;
					}
				}
			}
		}

		return j;
	}

	@Override
	public void addLightSource(BlockPos blockPos, int i) {
		this.lightStorage.updateAll();
		this.updateLevel(Long.MAX_VALUE, blockPos.asLong(), 15 - i, true);
	}
}
