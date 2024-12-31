package net.minecraft.world.chunk.light;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.util.SectionRelativeLevelPropagator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public abstract class LightStorage<M extends WorldNibbleStorage<M>> extends SectionRelativeLevelPropagator {
	protected static final ChunkNibbleArray EMPTY = new ChunkNibbleArray();
	private static final Direction[] DIRECTIONS = Direction.values();
	private final LightType lightType;
	private final ChunkProvider chunkProvider;
	protected final LongSet field_15808 = new LongOpenHashSet();
	protected final LongSet field_15797 = new LongOpenHashSet();
	protected final LongSet field_15804 = new LongOpenHashSet();
	protected volatile M dataStorageUncached;
	protected final M dataStorage;
	protected final LongSet field_15802 = new LongOpenHashSet();
	protected final LongSet toNotify = new LongOpenHashSet();
	protected final Long2ObjectMap<ChunkNibbleArray> toUpdate = new Long2ObjectOpenHashMap();
	private final LongSet field_19342 = new LongOpenHashSet();
	private final LongSet toRemove = new LongOpenHashSet();
	protected volatile boolean hasLightUpdates;

	protected LightStorage(LightType lightType, ChunkProvider chunkProvider, M worldNibbleStorage) {
		super(3, 16, 256);
		this.lightType = lightType;
		this.chunkProvider = chunkProvider;
		this.dataStorage = worldNibbleStorage;
		this.dataStorageUncached = worldNibbleStorage.copy();
		this.dataStorageUncached.disableCache();
	}

	protected boolean hasChunk(long l) {
		return this.getDataForChunk(l, true) != null;
	}

	@Nullable
	protected ChunkNibbleArray getDataForChunk(long l, boolean bl) {
		return this.getDataForChunk(bl ? this.dataStorage : this.dataStorageUncached, l);
	}

	@Nullable
	protected ChunkNibbleArray getDataForChunk(M worldNibbleStorage, long l) {
		return worldNibbleStorage.getDataForChunk(l);
	}

	@Nullable
	public ChunkNibbleArray method_20533(long l) {
		ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.toUpdate.get(l);
		return chunkNibbleArray != null ? chunkNibbleArray : this.getDataForChunk(l, false);
	}

	protected abstract int getLight(long l);

	protected int get(long l) {
		long m = ChunkSectionPos.toChunkLong(l);
		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, true);
		return chunkNibbleArray.get(
			ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)),
			ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)),
			ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l))
		);
	}

	protected void set(long l, int i) {
		long m = ChunkSectionPos.toChunkLong(l);
		if (this.field_15802.add(m)) {
			this.dataStorage.cloneChunkData(m);
		}

		ChunkNibbleArray chunkNibbleArray = this.getDataForChunk(m, true);
		chunkNibbleArray.set(
			ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)),
			ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)),
			ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l)),
			i
		);

		for (int j = -1; j <= 1; j++) {
			for (int k = -1; k <= 1; k++) {
				for (int n = -1; n <= 1; n++) {
					this.toNotify.add(ChunkSectionPos.toChunkLong(BlockPos.add(l, k, n, j)));
				}
			}
		}
	}

	@Override
	protected int getLevel(long l) {
		if (l == Long.MAX_VALUE) {
			return 2;
		} else if (this.field_15808.contains(l)) {
			return 0;
		} else {
			return !this.toRemove.contains(l) && this.dataStorage.hasChunk(l) ? 1 : 2;
		}
	}

	@Override
	protected int getInitialLevel(long l) {
		if (this.field_15797.contains(l)) {
			return 2;
		} else {
			return !this.field_15808.contains(l) && !this.field_15804.contains(l) ? 2 : 0;
		}
	}

	@Override
	protected void setLevel(long l, int i) {
		int j = this.getLevel(l);
		if (j != 0 && i == 0) {
			this.field_15808.add(l);
			this.field_15804.remove(l);
		}

		if (j == 0 && i != 0) {
			this.field_15808.remove(l);
			this.field_15797.remove(l);
		}

		if (j >= 2 && i != 2) {
			if (this.toRemove.contains(l)) {
				this.toRemove.remove(l);
			} else {
				this.dataStorage.addForChunk(l, this.getDataForChunk(l));
				this.field_15802.add(l);
				this.method_15523(l);

				for (int k = -1; k <= 1; k++) {
					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							this.toNotify.add(ChunkSectionPos.toChunkLong(BlockPos.add(l, m, n, k)));
						}
					}
				}
			}
		}

		if (j != 2 && i >= 2) {
			this.toRemove.add(l);
		}

		this.hasLightUpdates = !this.toRemove.isEmpty();
	}

	protected ChunkNibbleArray getDataForChunk(long l) {
		ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.toUpdate.get(l);
		return chunkNibbleArray != null ? chunkNibbleArray : new ChunkNibbleArray();
	}

	protected void removeChunkData(ChunkLightProvider<?, ?> chunkLightProvider, long l) {
		int i = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(l));
		int j = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(l));
		int k = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(l));

		for (int m = 0; m < 16; m++) {
			for (int n = 0; n < 16; n++) {
				for (int o = 0; o < 16; o++) {
					long p = BlockPos.asLong(i + m, j + n, k + o);
					chunkLightProvider.remove(p);
				}
			}
		}
	}

	protected boolean hasLightUpdates() {
		return this.hasLightUpdates;
	}

	protected void processUpdates(ChunkLightProvider<M, ?> chunkLightProvider, boolean bl, boolean bl2) {
		if (this.hasLightUpdates() || !this.toUpdate.isEmpty()) {
			LongIterator objectIterator = this.toRemove.iterator();

			while (objectIterator.hasNext()) {
				long l = (Long)objectIterator.next();
				this.removeChunkData(chunkLightProvider, l);
				ChunkNibbleArray chunkNibbleArray = (ChunkNibbleArray)this.toUpdate.remove(l);
				ChunkNibbleArray chunkNibbleArray2 = this.dataStorage.removeChunk(l);
				if (this.field_19342.contains(ChunkSectionPos.toLightStorageIndex(l))) {
					if (chunkNibbleArray != null) {
						this.toUpdate.put(l, chunkNibbleArray);
					} else if (chunkNibbleArray2 != null) {
						this.toUpdate.put(l, chunkNibbleArray2);
					}
				}
			}

			this.dataStorage.clearCache();
			objectIterator = this.toRemove.iterator();

			while (objectIterator.hasNext()) {
				long m = (Long)objectIterator.next();
				this.onChunkRemoved(m);
			}

			this.toRemove.clear();
			this.hasLightUpdates = false;
			ObjectIterator var23 = this.toUpdate.long2ObjectEntrySet().iterator();

			while (var23.hasNext()) {
				Entry<ChunkNibbleArray> entry = (Entry<ChunkNibbleArray>)var23.next();
				long n = entry.getLongKey();
				if (this.hasChunk(n)) {
					ChunkNibbleArray chunkNibbleArray3 = (ChunkNibbleArray)entry.getValue();
					if (this.dataStorage.getDataForChunk(n) != chunkNibbleArray3) {
						this.removeChunkData(chunkLightProvider, n);
						this.dataStorage.addForChunk(n, chunkNibbleArray3);
						this.field_15802.add(n);
					}
				}
			}

			this.dataStorage.clearCache();
			if (!bl2) {
				objectIterator = this.toUpdate.keySet().iterator();

				while (objectIterator.hasNext()) {
					long o = (Long)objectIterator.next();
					if (this.hasChunk(o)) {
						int i = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongX(o));
						int j = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongY(o));
						int k = ChunkSectionPos.fromChunkCoord(ChunkSectionPos.unpackLongZ(o));

						for (Direction direction : DIRECTIONS) {
							long p = ChunkSectionPos.offsetPacked(o, direction);
							if (!this.toUpdate.containsKey(p) && this.hasChunk(p)) {
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

										chunkLightProvider.update(s, t, chunkLightProvider.getPropagatedLevel(s, t, chunkLightProvider.getLevel(s)), false);
										chunkLightProvider.update(t, s, chunkLightProvider.getPropagatedLevel(t, s, chunkLightProvider.getLevel(t)), false);
									}
								}
							}
						}
					}
				}
			}

			ObjectIterator<Entry<ChunkNibbleArray>> objectIteratorx = this.toUpdate.long2ObjectEntrySet().iterator();

			while (objectIteratorx.hasNext()) {
				Entry<ChunkNibbleArray> entry2 = (Entry<ChunkNibbleArray>)objectIteratorx.next();
				long ae = entry2.getLongKey();
				if (this.hasChunk(ae)) {
					objectIteratorx.remove();
				}
			}
		}
	}

	protected void method_15523(long l) {
	}

	protected void onChunkRemoved(long l) {
	}

	protected void method_15535(long l, boolean bl) {
	}

	public void method_20600(long l, boolean bl) {
		if (bl) {
			this.field_19342.add(l);
		} else {
			this.field_19342.remove(l);
		}
	}

	protected void scheduleToUpdate(long l, @Nullable ChunkNibbleArray chunkNibbleArray) {
		if (chunkNibbleArray != null) {
			this.toUpdate.put(l, chunkNibbleArray);
		} else {
			this.toUpdate.remove(l);
		}
	}

	protected void scheduleChunkLightUpdate(long l, boolean bl) {
		boolean bl2 = this.field_15808.contains(l);
		if (!bl2 && !bl) {
			this.field_15804.add(l);
			this.update(Long.MAX_VALUE, l, 0, true);
		}

		if (bl2 && bl) {
			this.field_15797.add(l);
			this.update(Long.MAX_VALUE, l, 2, false);
		}
	}

	protected void updateAll() {
		if (this.hasLevelUpdates()) {
			this.updateAllRecursively(Integer.MAX_VALUE);
		}
	}

	protected void notifyChunkProvider() {
		if (!this.field_15802.isEmpty()) {
			M worldNibbleStorage = this.dataStorage.copy();
			worldNibbleStorage.disableCache();
			this.dataStorageUncached = worldNibbleStorage;
			this.field_15802.clear();
		}

		if (!this.toNotify.isEmpty()) {
			LongIterator longIterator = this.toNotify.iterator();

			while (longIterator.hasNext()) {
				long l = longIterator.nextLong();
				this.chunkProvider.onLightUpdate(this.lightType, ChunkSectionPos.from(l));
			}

			this.toNotify.clear();
		}
	}
}
