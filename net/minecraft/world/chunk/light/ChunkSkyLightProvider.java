package net.minecraft.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import org.apache.commons.lang3.mutable.MutableInt;

public final class ChunkSkyLightProvider extends ChunkLightProvider<SkyLightStorage.Data, SkyLightStorage> {
	private static final Direction[] DIRECTIONS = Direction.values();
	private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

	public ChunkSkyLightProvider(ChunkProvider chunkProvider) {
		super(chunkProvider, LightType.SKY, new SkyLightStorage(chunkProvider));
	}

	@Override
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		if (targetId == Long.MAX_VALUE || sourceId == Long.MAX_VALUE) {
			return 15;
		} else if (level >= 15) {
			return level;
		} else {
			MutableInt mutableInt = new MutableInt();
			BlockState blockState = this.getStateForLighting(targetId, mutableInt);
			if (mutableInt.getValue() >= 15) {
				return 15;
			} else {
				int i = BlockPos.unpackLongX(sourceId);
				int j = BlockPos.unpackLongY(sourceId);
				int k = BlockPos.unpackLongZ(sourceId);
				int l = BlockPos.unpackLongX(targetId);
				int m = BlockPos.unpackLongY(targetId);
				int n = BlockPos.unpackLongZ(targetId);
				int o = Integer.signum(l - i);
				int p = Integer.signum(m - j);
				int q = Integer.signum(n - k);
				Direction direction = Direction.fromVector(o, p, q);
				if (direction == null) {
					throw new IllegalStateException(String.format("Light was spread in illegal direction %d, %d, %d", o, p, q));
				} else {
					BlockState blockState2 = this.getStateForLighting(sourceId, null);
					VoxelShape voxelShape = this.getOpaqueShape(blockState2, sourceId, direction);
					VoxelShape voxelShape2 = this.getOpaqueShape(blockState, targetId, direction.getOpposite());
					if (VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2)) {
						return 15;
					} else {
						boolean bl = i == l && k == n;
						boolean bl2 = bl && j > m;
						return bl2 && level == 0 && mutableInt.getValue() == 0 ? 0 : level + Math.max(1, mutableInt.getValue());
					}
				}
			}
		}
	}

	@Override
	protected void propagateLevel(long id, int level, boolean decrease) {
		long l = ChunkSectionPos.fromBlockPos(id);
		int i = BlockPos.unpackLongY(id);
		int j = ChunkSectionPos.getLocalCoord(i);
		int k = ChunkSectionPos.getSectionCoord(i);
		int m;
		if (j != 0) {
			m = 0;
		} else {
			int n = 0;

			while (!this.lightStorage.hasSection(ChunkSectionPos.offset(l, 0, -n - 1, 0)) && this.lightStorage.isAboveMinHeight(k - n - 1)) {
				n++;
			}

			m = n;
		}

		long p = BlockPos.add(id, 0, -1 - m * 16, 0);
		long q = ChunkSectionPos.fromBlockPos(p);
		if (l == q || this.lightStorage.hasSection(q)) {
			this.propagateLevel(id, p, level, decrease);
		}

		long r = BlockPos.offset(id, Direction.UP);
		long s = ChunkSectionPos.fromBlockPos(r);
		if (l == s || this.lightStorage.hasSection(s)) {
			this.propagateLevel(id, r, level, decrease);
		}

		for (Direction direction : HORIZONTAL_DIRECTIONS) {
			int t = 0;

			do {
				long u = BlockPos.add(id, direction.getOffsetX(), -t, direction.getOffsetZ());
				long v = ChunkSectionPos.fromBlockPos(u);
				if (l == v) {
					this.propagateLevel(id, u, level, decrease);
					break;
				}

				if (this.lightStorage.hasSection(v)) {
					long w = BlockPos.add(id, 0, -t, 0);
					this.propagateLevel(w, u, level, decrease);
				}
			} while (++t > m * 16);
		}
	}

	@Override
	protected int recalculateLevel(long id, long excludedId, int maxLevel) {
		int i = maxLevel;
		long l = ChunkSectionPos.fromBlockPos(id);
		ChunkNibbleArray chunkNibbleArray = this.lightStorage.getLightSection(l, true);

		for (Direction direction : DIRECTIONS) {
			long m = BlockPos.offset(id, direction);
			if (m != excludedId) {
				long n = ChunkSectionPos.fromBlockPos(m);
				ChunkNibbleArray chunkNibbleArray2;
				if (l == n) {
					chunkNibbleArray2 = chunkNibbleArray;
				} else {
					chunkNibbleArray2 = this.lightStorage.getLightSection(n, true);
				}

				int j;
				if (chunkNibbleArray2 != null) {
					j = this.getCurrentLevelFromSection(chunkNibbleArray2, m);
				} else {
					if (direction == Direction.DOWN) {
						continue;
					}

					j = 15 - this.lightStorage.method_31931(m, true);
				}

				int o = this.getPropagatedLevel(m, id, j);
				if (i > o) {
					i = o;
				}

				if (i == 0) {
					return i;
				}
			}
		}

		return i;
	}

	@Override
	protected void resetLevel(long id) {
		this.lightStorage.updateAll();
		long l = ChunkSectionPos.fromBlockPos(id);
		if (this.lightStorage.hasSection(l)) {
			super.resetLevel(id);
		} else {
			for (id = BlockPos.removeChunkSectionLocalY(id);
				!this.lightStorage.hasSection(l) && !this.lightStorage.isAtOrAboveTopmostSection(l);
				id = BlockPos.add(id, 0, 16, 0)
			) {
				l = ChunkSectionPos.offset(l, Direction.UP);
			}

			if (this.lightStorage.hasSection(l)) {
				super.resetLevel(id);
			}
		}
	}

	@Override
	public String displaySectionLevel(long sectionPos) {
		return super.displaySectionLevel(sectionPos) + (this.lightStorage.isAtOrAboveTopmostSection(sectionPos) ? "*" : "");
	}
}
