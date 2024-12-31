package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;
import net.minecraft.world.chunk.ColumnChunkNibbleArray;

public class SkyLightStorage extends LightStorage<SkyLightStorage.Data> {
	private static final Direction[] LIGHT_REDUCTION_DIRECTIONS = new Direction[]{
		Direction.field_11043, Direction.field_11035, Direction.field_11039, Direction.field_11034
	};
	private final LongSet field_15820 = new LongOpenHashSet();
	private final LongSet pendingSkylightUpdates = new LongOpenHashSet();
	private final LongSet field_15816 = new LongOpenHashSet();
	private final LongSet lightEnabled = new LongOpenHashSet();
	private volatile boolean hasSkyLightUpdates;

	protected SkyLightStorage(ChunkProvider chunkProvider) {
		super(LightType.field_9284, chunkProvider, new SkyLightStorage.Data(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
	}

	@Override
	protected int getLight(long l) {
		long m = ChunkSectionPos.fromGlobalPos(l);
		int i = ChunkSectionPos.getY(m);
		SkyLightStorage.Data data = this.uncachedLightArrays;
		int j = data.topArraySectionY.get(ChunkSectionPos.withZeroZ(m));
		if (j != data.defaultTopArraySectionY && i < j) {
			ChunkNibbleArray chunkNibbleArray = this.getLightArray(data, m);
			if (chunkNibbleArray == null) {
				for (l = BlockPos.removeChunkSectionLocalY(l); chunkNibbleArray == null; chunkNibbleArray = this.getLightArray(data, m)) {
					m = ChunkSectionPos.offset(m, Direction.field_11036);
					if (++i >= j) {
						return 15;
					}

					l = BlockPos.add(l, 0, 16, 0);
				}
			}

			return chunkNibbleArray.get(
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l))
			);
		} else {
			return 15;
		}
	}

	@Override
	protected void onLightArrayCreated(long l) {
		int i = ChunkSectionPos.getY(l);
		if (this.lightArrays.defaultTopArraySectionY > i) {
			this.lightArrays.defaultTopArraySectionY = i;
			this.lightArrays.topArraySectionY.defaultReturnValue(this.lightArrays.defaultTopArraySectionY);
		}

		long m = ChunkSectionPos.withZeroZ(l);
		int j = this.lightArrays.topArraySectionY.get(m);
		if (j < i + 1) {
			this.lightArrays.topArraySectionY.put(m, i + 1);
			if (this.lightEnabled.contains(m)) {
				this.method_20810(l);
				if (j > this.lightArrays.defaultTopArraySectionY) {
					long n = ChunkSectionPos.asLong(ChunkSectionPos.getX(l), j - 1, ChunkSectionPos.getZ(l));
					this.method_20809(n);
				}

				this.checkForUpdates();
			}
		}
	}

	private void method_20809(long l) {
		this.field_15816.add(l);
		this.pendingSkylightUpdates.remove(l);
	}

	private void method_20810(long l) {
		this.pendingSkylightUpdates.add(l);
		this.field_15816.remove(l);
	}

	private void checkForUpdates() {
		this.hasSkyLightUpdates = !this.pendingSkylightUpdates.isEmpty() || !this.field_15816.isEmpty();
	}

	@Override
	protected void onChunkRemoved(long l) {
		long m = ChunkSectionPos.withZeroZ(l);
		boolean bl = this.lightEnabled.contains(m);
		if (bl) {
			this.method_20809(l);
		}

		int i = ChunkSectionPos.getY(l);
		if (this.lightArrays.topArraySectionY.get(m) == i + 1) {
			long n;
			for (n = l; !this.hasLight(n) && this.isAboveMinimumHeight(i); n = ChunkSectionPos.offset(n, Direction.field_11033)) {
				i--;
			}

			if (this.hasLight(n)) {
				this.lightArrays.topArraySectionY.put(m, i + 1);
				if (bl) {
					this.method_20810(n);
				}
			} else {
				this.lightArrays.topArraySectionY.remove(m);
			}
		}

		if (bl) {
			this.checkForUpdates();
		}
	}

	@Override
	protected void setLightEnabled(long l, boolean bl) {
		this.updateAll();
		if (bl && this.lightEnabled.add(l)) {
			int i = this.lightArrays.topArraySectionY.get(l);
			if (i != this.lightArrays.defaultTopArraySectionY) {
				long m = ChunkSectionPos.asLong(ChunkSectionPos.getX(l), i - 1, ChunkSectionPos.getZ(l));
				this.method_20810(m);
				this.checkForUpdates();
			}
		} else if (!bl) {
			this.lightEnabled.remove(l);
		}
	}

	@Override
	protected boolean hasLightUpdates() {
		return super.hasLightUpdates() || this.hasSkyLightUpdates;
	}

	@Override
	protected ChunkNibbleArray createLightArray(long l) {
		ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
		if (chunkNibbleArray != null) {
			return chunkNibbleArray;
		} else {
			long m = ChunkSectionPos.offset(l, Direction.field_11036);
			int i = this.lightArrays.topArraySectionY.get(ChunkSectionPos.withZeroZ(l));
			if (i != this.lightArrays.defaultTopArraySectionY && ChunkSectionPos.getY(m) < i) {
				ChunkNibbleArray chunkNibbleArray2;
				while ((chunkNibbleArray2 = this.getLightArray(m, true)) == null) {
					m = ChunkSectionPos.offset(m, Direction.field_11036);
				}

				return new ChunkNibbleArray(new ColumnChunkNibbleArray(chunkNibbleArray2, 0).asByteArray());
			} else {
				return new ChunkNibbleArray();
			}
		}
	}

	@Override
	protected void updateLightArrays(ChunkLightProvider<SkyLightStorage.Data, ?> chunkLightProvider, boolean bl, boolean bl2) {
		super.updateLightArrays(chunkLightProvider, bl, bl2);
		if (bl) {
			if (!this.pendingSkylightUpdates.isEmpty()) {
				LongIterator var4 = this.pendingSkylightUpdates.iterator();

				while (var4.hasNext()) {
					long l = (Long)var4.next();
					int i = this.getLevel(l);
					if (i != 2 && !this.field_15816.contains(l) && this.field_15820.add(l)) {
						if (i == 1) {
							this.removeChunkData(chunkLightProvider, l);
							if (this.field_15802.add(l)) {
								this.lightArrays.replaceWithCopy(l);
							}

							Arrays.fill(this.getLightArray(l, true).asByteArray(), (byte)-1);
							int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
							int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
							int m = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));

							for (Direction direction : LIGHT_REDUCTION_DIRECTIONS) {
								long n = ChunkSectionPos.offset(l, direction);
								if ((this.field_15816.contains(n) || !this.field_15820.contains(n) && !this.pendingSkylightUpdates.contains(n)) && this.hasLight(n)) {
									for (int o = 0; o < 16; o++) {
										for (int p = 0; p < 16; p++) {
											long q;
											long r;
											switch (direction) {
												case field_11043:
													q = BlockPos.asLong(j + o, k + p, m);
													r = BlockPos.asLong(j + o, k + p, m - 1);
													break;
												case field_11035:
													q = BlockPos.asLong(j + o, k + p, m + 16 - 1);
													r = BlockPos.asLong(j + o, k + p, m + 16);
													break;
												case field_11039:
													q = BlockPos.asLong(j, k + o, m + p);
													r = BlockPos.asLong(j - 1, k + o, m + p);
													break;
												default:
													q = BlockPos.asLong(j + 16 - 1, k + o, m + p);
													r = BlockPos.asLong(j + 16, k + o, m + p);
											}

											chunkLightProvider.updateLevel(q, r, chunkLightProvider.getPropagatedLevel(q, r, 0), true);
										}
									}
								}
							}

							for (int y = 0; y < 16; y++) {
								for (int z = 0; z < 16; z++) {
									long aa = BlockPos.asLong(
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + y,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)),
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + z
									);
									long ab = BlockPos.asLong(
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + y,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) - 1,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + z
									);
									chunkLightProvider.updateLevel(aa, ab, chunkLightProvider.getPropagatedLevel(aa, ab, 0), true);
								}
							}
						} else {
							for (int ac = 0; ac < 16; ac++) {
								for (int ad = 0; ad < 16; ad++) {
									long ae = BlockPos.asLong(
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l)) + ac,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l)) + 16 - 1,
										ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l)) + ad
									);
									chunkLightProvider.updateLevel(Long.MAX_VALUE, ae, 0, true);
								}
							}
						}
					}
				}
			}

			this.pendingSkylightUpdates.clear();
			if (!this.field_15816.isEmpty()) {
				LongIterator var23 = this.field_15816.iterator();

				while (var23.hasNext()) {
					long af = (Long)var23.next();
					if (this.field_15820.remove(af) && this.hasLight(af)) {
						for (int ag = 0; ag < 16; ag++) {
							for (int ah = 0; ah < 16; ah++) {
								long ai = BlockPos.asLong(
									ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(af)) + ag,
									ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(af)) + 16 - 1,
									ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(af)) + ah
								);
								chunkLightProvider.updateLevel(Long.MAX_VALUE, ai, 15, false);
							}
						}
					}
				}
			}

			this.field_15816.clear();
			this.hasSkyLightUpdates = false;
		}
	}

	protected boolean isAboveMinimumHeight(int i) {
		return i >= this.lightArrays.defaultTopArraySectionY;
	}

	protected boolean method_15565(long l) {
		int i = BlockPos.unpackLongY(l);
		if ((i & 15) != 15) {
			return false;
		} else {
			long m = ChunkSectionPos.fromGlobalPos(l);
			long n = ChunkSectionPos.withZeroZ(m);
			if (!this.lightEnabled.contains(n)) {
				return false;
			} else {
				int j = this.lightArrays.topArraySectionY.get(n);
				return ChunkSectionPos.getWorldCoord(j) == i + 16;
			}
		}
	}

	protected boolean isAboveTopmostLightArray(long l) {
		long m = ChunkSectionPos.withZeroZ(l);
		int i = this.lightArrays.topArraySectionY.get(m);
		return i == this.lightArrays.defaultTopArraySectionY || ChunkSectionPos.getY(l) >= i;
	}

	protected boolean isLightEnabled(long l) {
		long m = ChunkSectionPos.withZeroZ(l);
		return this.lightEnabled.contains(m);
	}

	public static final class Data extends ChunkToNibbleArrayMap<SkyLightStorage.Data> {
		private int defaultTopArraySectionY;
		private final Long2IntOpenHashMap topArraySectionY;

		public Data(Long2ObjectOpenHashMap<ChunkNibbleArray> long2ObjectOpenHashMap, Long2IntOpenHashMap long2IntOpenHashMap, int i) {
			super(long2ObjectOpenHashMap);
			this.topArraySectionY = long2IntOpenHashMap;
			long2IntOpenHashMap.defaultReturnValue(i);
			this.defaultTopArraySectionY = i;
		}

		public SkyLightStorage.Data copy() {
			return new SkyLightStorage.Data(this.arrays.clone(), this.topArraySectionY.clone(), this.defaultTopArraySectionY);
		}
	}
}
