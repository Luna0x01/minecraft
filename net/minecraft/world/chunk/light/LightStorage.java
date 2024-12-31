package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.SectionDistanceLevelPropagator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;

public abstract class LightStorage<M extends ChunkToNibbleArrayMap<M>> extends SectionDistanceLevelPropagator {
	protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
	private static final Direction[] DIRECTIONS = Direction.values();
	private final LightType lightType;
	private final ChunkProvider chunkProvider;
	protected final LongSet nonEmptySections = new LongOpenHashSet();
	protected final LongSet field_15797 = new LongOpenHashSet();
	protected final LongSet field_15804 = new LongOpenHashSet();
	protected volatile M uncachedLightArrays;
	protected final M lightArrays;
	protected final LongSet field_15802 = new LongOpenHashSet();
	protected final LongSet dirtySections = new LongOpenHashSet();
	protected final Long2ObjectMap<ChunkNibbleArray> lightArraysToAdd = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap());
	private final LongSet field_19342 = new LongOpenHashSet();
	private final LongSet lightArraysToRemove = new LongOpenHashSet();
	protected volatile boolean hasLightUpdates;

	protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M chunkToNibbleArrayMap) {
		super(3, 16, 256);
		this.lightType = lightType;
		this.chunkProvider = chunkProvider;
		this.lightArrays = chunkToNibbleArrayMap;
		this.uncachedLightArrays = chunkToNibbleArrayMap.copy();
		this.uncachedLightArrays.disableCache();
	}

	protected boolean hasLight(long l) {
		return this.getLightArray(l, true) != null;
	}

	@Nullable
	protected ChunkNibbleArray getLightArray(long l, boolean bl) {
		return this.getLightArray(bl ? this.lightArrays : this.uncachedLightArrays, l);
	}

	@Nullable
	protected ChunkNibbleArray getLightArray(M chunkToNibbleArrayMap, long l) {
		return chunkToNibbleArrayMap.get(l);
	}

	@Nullable
	public ChunkNibbleArray getLightArray(long l) {
		ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
		return chunkNibbleArray != null ? chunkNibbleArray : this.getLightArray(l, false);
	}

	protected abstract int getLight(long l);

	protected int get(long l) {
		long m = ChunkSectionPos.fromGlobalPos(l);
		ChunkNibbleArray chunkNibbleArray = this.getLightArray(m, true);
		return chunkNibbleArray.get(
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l))
		);
	}

	protected void set(long l, int i) {
		long m = ChunkSectionPos.fromGlobalPos(l);
		if (this.field_15802.add(m)) {
			this.lightArrays.replaceWithCopy(m);
		}

		ChunkNibbleArray chunkNibbleArray = this.getLightArray(m, true);
		chunkNibbleArray.set(
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)),
			ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l)),
			i
		);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int n = -1; n <= 1; n++) {
					this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(l, k, n, j)));
				}
			}
		}
	}

	@Override
	protected int getLevel(long l) {
		if (l == Long.MAX_VALUE) {
			return 2;
		} else if (this.nonEmptySections.contains(l)) {
			return 0;
		} else {
			return !this.lightArraysToRemove.contains(l) && this.lightArrays.containsKey(l) ? 1 : 2;
		}
	}

	@Override
	protected int getInitialLevel(long l) {
		if (this.field_15797.contains(l)) {
			return 2;
		} else {
			return !this.nonEmptySections.contains(l) && !this.field_15804.contains(l) ? 2 : 0;
		}
	}

	@Override
	protected void setLevel(long l, int i) {
		int j = this.getLevel(l);
		if (j != 0 && i == 0) {
			this.nonEmptySections.add(l);
			this.field_15804.remove(l);
		}

		if (j == 0 && i != 0) {
			this.nonEmptySections.remove(l);
			this.field_15797.remove(l);
		}

		if (j >= 2 && i != 2) {
			if (this.lightArraysToRemove.contains(l)) {
				this.lightArraysToRemove.remove(l);
			} else {
				this.lightArrays.put(l, this.createLightArray(l));
				this.field_15802.add(l);
				this.onLightArrayCreated(l);

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							this.dirtySections.add(ChunkSectionPos.fromGlobalPos(BlockPos.add(l, m, n, k)));
						}
					}
				}
			}
		}

		if (j != 2 && i >= 2) {
			this.lightArraysToRemove.add(l);
		}

		this.hasLightUpdates = !this.lightArraysToRemove.isEmpty();
	}

	protected ChunkNibbleArray createLightArray(long l) {
		ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.get(l);
		return chunkNibbleArray != null ? chunkNibbleArray : new ChunkNibbleArray();
	}

	protected void removeChunkData(ChunkLightProvider<?, ?> chunkLightProvider, long l) {
		if (chunkLightProvider.method_24208() < 8192) {
			chunkLightProvider.method_24206(mx -> ChunkSectionPos.fromGlobalPos(mx) == l);
		} else {
			int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(l));
			int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(l));
			int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(l));

			for (int m = 0; m < 16; m++) {
				for (int n = 0; n < 16; n++) {
					for (int o = 0; o < 16; o++) {
						long p = BlockPos.asLong(i + m, j + n, k + o);
						chunkLightProvider.removePendingUpdate(p);
					}
				}
			}
		}
	}

	protected boolean hasLightUpdates() {
		return this.hasLightUpdates;
	}

	protected void updateLightArrays(ChunkLightProvider<M, ?> chunkLightProvider, boolean bl, boolean bl2) {
		if (this.hasLightUpdates() || !this.lightArraysToAdd.isEmpty()) {
			LongIterator objectIterator = this.lightArraysToRemove.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.removeChunkData(chunkLightProvider, l);
				ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.lightArraysToAdd.remove(l);
				ChunkNibbleArray chunkNibbleArray2 = this.lightArrays.removeChunk(l);
				if (this.field_19342.contains(ChunkSectionPos.withZeroZ(l))) {
					if (chunkNibbleArray != null) {
						this.lightArraysToAdd.put(l, chunkNibbleArray);
					} else if (chunkNibbleArray2 != null) {
						this.lightArraysToAdd.put(l, chunkNibbleArray2);
					}
				}
			}

			this.lightArrays.clearCache();
			objectIterator = this.lightArraysToRemove.iterator();

			while (objectIterator.hasNext()) {
				long m = (Long)objectIterator.next();
				this.onChunkRemoved(m);
			}

			this.lightArraysToRemove.clear();
			this.hasLightUpdates = false;
			ObjectIterator var23 = this.lightArraysToAdd.long2ObjectEntrySet().iterator();

			while (var23.hasNext()) {
				Entry<ChunkNibbleArray> entry = (Entry<ChunkNibbleArray>)var23.next();
				long n = entry.getLongKey();
				if (this.hasLight(n)) {
					ChunkNibbleArray chunkNibbleArray3 = (ChunkNibbleArray)entry.getValue();
					if (this.lightArrays.get(n) != chunkNibbleArray3) {
						this.removeChunkData(chunkLightProvider, n);
						this.lightArrays.put(n, chunkNibbleArray3);
						this.field_15802.add(n);
					}
				}
			}

			this.lightArrays.clearCache();
			if (!bl2) {
				objectIterator = this.lightArraysToAdd.keySet().iterator();

				while (objectIterator.hasNext()) {
					long o = (Long)objectIterator.next();
					if (this.hasLight(o)) {
						int i = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getX(o));
						int j = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getY(o));
						int k = ChunkSectionPos.getWorldCoord(ChunkSectionPos.getZ(o));

						for (Direction direction : DIRECTIONS) {
							long p = ChunkSectionPos.offset(o, direction);
							if (!this.lightArraysToAdd.containsKey(p) && this.hasLight(p)) {
								for (int q = 0; q < 16; q++) {
									for (int r = 0; r < 16; r++) {
										long s;
										long t;
										switch (direction) {
											case field_11033:
												s = BlockPos.asLong(i + r, j, k + q);
												t = BlockPos.asLong(i + r, j - 1, k + q);
												break;
											case field_11036:
												s = BlockPos.asLong(i + r, j + 16 - 1, k + q);
												t = BlockPos.asLong(i + r, j + 16, k + q);
												break;
											case field_11043:
												s = BlockPos.asLong(i + q, j + r, k);
												t = BlockPos.asLong(i + q, j + r, k - 1);
												break;
											case field_11035:
												s = BlockPos.asLong(i + q, j + r, k + 16 - 1);
												t = BlockPos.asLong(i + q, j + r, k + 16);
												break;
											case field_11039:
												s = BlockPos.asLong(i, j + q, k + r);
												t = BlockPos.asLong(i - 1, j + q, k + r);
												break;
											default:
												s = BlockPos.asLong(i + 16 - 1, j + q, k + r);
												t = BlockPos.asLong(i + 16, j + q, k + r);
										}

										chunkLightProvider.updateLevel(s, t, chunkLightProvider.getPropagatedLevel(s, t, chunkLightProvider.getLevel(s)), false);
										chunkLightProvider.updateLevel(t, s, chunkLightProvider.getPropagatedLevel(t, s, chunkLightProvider.getLevel(t)), false);
									}
								}
							}
						}
					}
				}
			}

			ObjectIterator<Entry<ChunkNibbleArray>> objectIteratorx = this.lightArraysToAdd.long2ObjectEntrySet().iterator();

			while (objectIteratorx.hasNext()) {
				Entry<ChunkNibbleArray> entry2 = (Entry<ChunkNibbleArray>)objectIteratorx.next();
				long ae = entry2.getLongKey();
				if (this.hasLight(ae)) {
					objectIteratorx.remove();
				}
			}
		}
	}

	protected void onLightArrayCreated(long l) {
	}

	protected void onChunkRemoved(long l) {
	}

	protected void setLightEnabled(long l, boolean bl) {
	}

	public void setRetainData(long l, boolean bl) {
		if (bl) {
			this.field_19342.add(l);
		} else {
			this.field_19342.remove(l);
		}
	}

	protected void setLightArray(long l, @Nullable ChunkNibbleArray chunkNibbleArray) {
		if (chunkNibbleArray != null) {
			this.lightArraysToAdd.put(l, chunkNibbleArray);
		} else {
			this.lightArraysToAdd.remove(l);
		}
	}

	protected void updateSectionStatus(long l, boolean bl) {
		boolean bl2 = this.nonEmptySections.contains(l);
		if (!bl2 && !bl) {
			this.field_15804.add(l);
			this.updateLevel(Long.MAX_VALUE, l, 0, true);
		}

		if (bl2 && bl) {
			this.field_15797.add(l);
			this.updateLevel(Long.MAX_VALUE, l, 2, false);
		}
	}

	protected void updateAll() {
		if (this.hasPendingUpdates()) {
			this.applyPendingUpdates(Integer.MAX_VALUE);
		}
	}

	protected void notifyChunkProvider() {
		if (!this.field_15802.isEmpty()) {
			M chunkToNibbleArrayMap = this.lightArrays.copy();
			chunkToNibbleArrayMap.disableCache();
			this.uncachedLightArrays = chunkToNibbleArrayMap;
			this.field_15802.clear();
		}

		if (!this.dirtySections.isEmpty()) {
			LongIterator longIterator = this.dirtySections.iterator();

			while (longIterator.hasNext()) {
				long l = longIterator.nextLong();
				this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
			}

			this.dirtySections.clear();
		}
	}
}
