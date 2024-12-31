package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.util.collection.IntegerStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ChunkOcclusionDataBuilder {
	private static final int STEP_X = (int)Math.pow(16.0, 0.0);
	private static final int STEP_Z = (int)Math.pow(16.0, 1.0);
	private static final int STEP_Y = (int)Math.pow(16.0, 2.0);
	private final BitSet closed = new BitSet(4096);
	private static final int[] EDGE_POINTS = new int[1352];
	private int openCount = 4096;

	public void markClosed(BlockPos pos) {
		this.closed.set(pack(pos), true);
		this.openCount--;
	}

	private static int pack(BlockPos pos) {
		return pack(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
	}

	private static int pack(int x, int y, int z) {
		return x << 0 | y << 8 | z << 4;
	}

	public ChunkOcclusionData build() {
		ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
		if (4096 - this.openCount < 256) {
			chunkOcclusionData.fill(true);
		} else if (this.openCount == 0) {
			chunkOcclusionData.fill(false);
		} else {
			for (int k : EDGE_POINTS) {
				if (!this.closed.get(k)) {
					chunkOcclusionData.addOpenEdgeFaces(this.getOpenFaces(k));
				}
			}
		}

		return chunkOcclusionData;
	}

	public Set<Direction> getOpenFaces(BlockPos pos) {
		return this.getOpenFaces(pack(pos));
	}

	private Set<Direction> getOpenFaces(int pos) {
		Set<Direction> set = EnumSet.noneOf(Direction.class);
		Queue<Integer> queue = Lists.newLinkedList();
		queue.add(IntegerStorage.get(pos));
		this.closed.set(pos, true);

		while (!queue.isEmpty()) {
			int i = (Integer)queue.poll();
			this.addEdgeFaces(i, set);

			for (Direction direction : Direction.values()) {
				int l = this.offset(i, direction);
				if (l >= 0 && !this.closed.get(l)) {
					this.closed.set(l, true);
					queue.add(IntegerStorage.get(l));
				}
			}
		}

		return set;
	}

	private void addEdgeFaces(int pos, Set<Direction> openFaces) {
		int i = pos >> 0 & 15;
		if (i == 0) {
			openFaces.add(Direction.WEST);
		} else if (i == 15) {
			openFaces.add(Direction.EAST);
		}

		int j = pos >> 8 & 15;
		if (j == 0) {
			openFaces.add(Direction.DOWN);
		} else if (j == 15) {
			openFaces.add(Direction.UP);
		}

		int k = pos >> 4 & 15;
		if (k == 0) {
			openFaces.add(Direction.NORTH);
		} else if (k == 15) {
			openFaces.add(Direction.SOUTH);
		}
	}

	private int offset(int pos, Direction direction) {
		switch (direction) {
			case DOWN:
				if ((pos >> 8 & 15) == 0) {
					return -1;
				}

				return pos - STEP_Y;
			case UP:
				if ((pos >> 8 & 15) == 15) {
					return -1;
				}

				return pos + STEP_Y;
			case NORTH:
				if ((pos >> 4 & 15) == 0) {
					return -1;
				}

				return pos - STEP_Z;
			case SOUTH:
				if ((pos >> 4 & 15) == 15) {
					return -1;
				}

				return pos + STEP_Z;
			case WEST:
				if ((pos >> 0 & 15) == 0) {
					return -1;
				}

				return pos - STEP_X;
			case EAST:
				if ((pos >> 0 & 15) == 15) {
					return -1;
				}

				return pos + STEP_X;
			default:
				return -1;
		}
	}

	static {
		int i = 0;
		int j = 15;
		int k = 0;

		for (int l = 0; l < 16; l++) {
			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 16; n++) {
					if (l == 0 || l == 15 || m == 0 || m == 15 || n == 0 || n == 15) {
						EDGE_POINTS[k++] = pack(l, m, n);
					}
				}
			}
		}
	}
}
