package net.minecraft.server.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntMaps;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ChunkPosDistanceLevelPropagator;
import net.minecraft.util.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getTargetGenerationRadius(ChunkStatus.field_12803) - 2;
	private final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap();
	private final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap();
	private final ChunkTicketManager.TicketDistanceLevelPropagator distanceFromTicketTracker = new ChunkTicketManager.TicketDistanceLevelPropagator();
	private final ChunkTicketManager.DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new ChunkTicketManager.DistanceFromNearestPlayerTracker(8);
	private final ChunkTicketManager.NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new ChunkTicketManager.NearbyChunkTicketUpdater(33);
	private final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
	private final ChunkTaskPrioritySystem levelUpdateListener;
	private final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> playerTicketThrottler;
	private final MessageListener<ChunkTaskPrioritySystem.SorterMessage> playerTicketThrottlerSorter;
	private final LongSet chunkPositions = new LongOpenHashSet();
	private final Executor mainThreadExecutor;
	private long age;

	protected ChunkTicketManager(Executor executor, Executor executor2) {
		MessageListener<Runnable> messageListener = MessageListener.create("player ticket throttler", executor2::execute);
		ChunkTaskPrioritySystem chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(messageListener), executor, 4);
		this.levelUpdateListener = chunkTaskPrioritySystem;
		this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutor(messageListener, true);
		this.playerTicketThrottlerSorter = chunkTaskPrioritySystem.createSorterExecutor(messageListener);
		this.mainThreadExecutor = executor2;
	}

	protected void purge() {
		this.age++;
		ObjectIterator<Entry<SortedArraySet<ChunkTicket<?>>>> objectIterator = this.ticketsByPosition.long2ObjectEntrySet().fastIterator();

		while (objectIterator.hasNext()) {
			Entry<SortedArraySet<ChunkTicket<?>>> entry = (Entry<SortedArraySet<ChunkTicket<?>>>)objectIterator.next();
			if (((SortedArraySet)entry.getValue()).removeIf(chunkTicket -> chunkTicket.isExpired(this.age))) {
				this.distanceFromTicketTracker.updateLevel(entry.getLongKey(), getLevel((SortedArraySet<ChunkTicket<?>>)entry.getValue()), false);
			}

			if (((SortedArraySet)entry.getValue()).isEmpty()) {
				objectIterator.remove();
			}
		}
	}

	private static int getLevel(SortedArraySet<ChunkTicket<?>> sortedArraySet) {
		return !sortedArraySet.isEmpty() ? sortedArraySet.first().getLevel() : ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
	}

	protected abstract boolean isUnloaded(long l);

	@Nullable
	protected abstract ChunkHolder getChunkHolder(long l);

	@Nullable
	protected abstract ChunkHolder setLevel(long l, int i, @Nullable ChunkHolder chunkHolder, int j);

	public boolean tick(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		this.nearbyChunkTicketUpdater.updateLevels();
		int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.update(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.chunkHolders.isEmpty()) {
			this.chunkHolders.forEach(chunkHolderx -> chunkHolderx.tick(threadedAnvilChunkStorage));
			this.chunkHolders.clear();
			return true;
		} else {
			if (!this.chunkPositions.isEmpty()) {
				LongIterator longIterator = this.chunkPositions.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					if (this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.getType() == ChunkTicketType.field_14033)) {
						ChunkHolder chunkHolder = threadedAnvilChunkStorage.getCurrentChunkHolder(l);
						if (chunkHolder == null) {
							throw new IllegalStateException();
						}

						CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getEntityTickingFuture();
						completableFuture.thenAccept(
							either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {
									}, l, false)))
						);
					}
				}

				this.chunkPositions.clear();
			}

			return bl;
		}
	}

	private void addTicket(long l, ChunkTicket<?> chunkTicket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(l);
		int i = getLevel(sortedArraySet);
		ChunkTicket<?> chunkTicket2 = sortedArraySet.addAndGet(chunkTicket);
		chunkTicket2.setTickCreated(this.age);
		if (chunkTicket.getLevel() < i) {
			this.distanceFromTicketTracker.updateLevel(l, chunkTicket.getLevel(), true);
		}
	}

	private void removeTicket(long l, ChunkTicket<?> chunkTicket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(l);
		if (sortedArraySet.remove(chunkTicket)) {
		}

		if (sortedArraySet.isEmpty()) {
			this.ticketsByPosition.remove(l);
		}

		this.distanceFromTicketTracker.updateLevel(l, getLevel(sortedArraySet), false);
	}

	public <T> void addTicketWithLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.addTicket(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, i, object));
	}

	public <T> void removeTicketWithLevel(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, i, object);
		this.removeTicket(chunkPos.toLong(), chunkTicket);
	}

	public <T> void addTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		this.addTicket(chunkPos.toLong(), new ChunkTicket<>(chunkTicketType, 33 - i, object));
	}

	public <T> void removeTicket(ChunkTicketType<T> chunkTicketType, ChunkPos chunkPos, int i, T object) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(chunkTicketType, 33 - i, object);
		this.removeTicket(chunkPos.toLong(), chunkTicket);
	}

	private SortedArraySet<ChunkTicket<?>> getTicketSet(long l) {
		return (SortedArraySet<ChunkTicket<?>>)this.ticketsByPosition.computeIfAbsent(l, lx -> SortedArraySet.create(4));
	}

	protected void setChunkForced(ChunkPos chunkPos, boolean bl) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.field_14031, 31, chunkPos);
		if (bl) {
			this.addTicket(chunkPos.toLong(), chunkTicket);
		} else {
			this.removeTicket(chunkPos.toLong(), chunkTicket);
		}
	}

	public void handleChunkEnter(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
		long l = chunkSectionPos.toChunkPos().toLong();
		((ObjectSet)this.playersByChunkPos.computeIfAbsent(l, lx -> new ObjectOpenHashSet())).add(serverPlayerEntity);
		this.distanceFromNearestPlayerTracker.updateLevel(l, 0, true);
		this.nearbyChunkTicketUpdater.updateLevel(l, 0, true);
	}

	public void handleChunkLeave(ChunkSectionPos chunkSectionPos, ServerPlayerEntity serverPlayerEntity) {
		long l = chunkSectionPos.toChunkPos().toLong();
		ObjectSet<ServerPlayerEntity> objectSet = (ObjectSet<ServerPlayerEntity>)this.playersByChunkPos.get(l);
		objectSet.remove(serverPlayerEntity);
		if (objectSet.isEmpty()) {
			this.playersByChunkPos.remove(l);
			this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
			this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
		}
	}

	protected String method_21623(long l) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = (SortedArraySet<ChunkTicket<?>>)this.ticketsByPosition.get(l);
		String string2;
		if (sortedArraySet != null && !sortedArraySet.isEmpty()) {
			string2 = sortedArraySet.first().toString();
		} else {
			string2 = "no_ticket";
		}

		return string2;
	}

	protected void setWatchDistance(int i) {
		this.nearbyChunkTicketUpdater.setWatchDistance(i);
	}

	public int getLevelCount() {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
	}

	public boolean method_20800(long l) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(l);
	}

	public String method_21683() {
		return this.levelUpdateListener.method_21680();
	}

	class DistanceFromNearestPlayerTracker extends ChunkPosDistanceLevelPropagator {
		protected final Long2ByteMap distanceFromNearestPlayer = new Long2ByteOpenHashMap();
		protected final int maxDistance;

		protected DistanceFromNearestPlayerTracker(int i) {
			super(i + 2, 16, 256);
			this.maxDistance = i;
			this.distanceFromNearestPlayer.defaultReturnValue((byte)(i + 2));
		}

		@Override
		protected int getLevel(long l) {
			return this.distanceFromNearestPlayer.get(l);
		}

		@Override
		protected void setLevel(long l, int i) {
			byte b;
			if (i > this.maxDistance) {
				b = this.distanceFromNearestPlayer.remove(l);
			} else {
				b = this.distanceFromNearestPlayer.put(l, (byte)i);
			}

			this.onDistanceChange(l, b, i);
		}

		protected void onDistanceChange(long l, int i, int j) {
		}

		@Override
		protected int getInitialLevel(long l) {
			return this.isPlayerInChunk(l) ? 0 : Integer.MAX_VALUE;
		}

		private boolean isPlayerInChunk(long l) {
			ObjectSet<ServerPlayerEntity> objectSet = (ObjectSet<ServerPlayerEntity>)ChunkTicketManager.this.playersByChunkPos.get(l);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void updateLevels() {
			this.applyPendingUpdates(Integer.MAX_VALUE);
		}
	}

	class NearbyChunkTicketUpdater extends ChunkTicketManager.DistanceFromNearestPlayerTracker {
		private int watchDistance;
		private final Long2IntMap distances = Long2IntMaps.synchronize(new Long2IntOpenHashMap());
		private final LongSet positionsAffected = new LongOpenHashSet();

		protected NearbyChunkTicketUpdater(int i) {
			super(i);
			this.watchDistance = 0;
			this.distances.defaultReturnValue(i + 2);
		}

		@Override
		protected void onDistanceChange(long l, int i, int j) {
			this.positionsAffected.add(l);
		}

		public void setWatchDistance(int i) {
			ObjectIterator var2 = this.distanceFromNearestPlayer.long2ByteEntrySet().iterator();

			while (var2.hasNext()) {
				it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var2.next();
				byte b = entry.getByteValue();
				long l = entry.getLongKey();
				this.updateTicket(l, b, this.isWithinViewDistance(b), b <= i - 2);
			}

			this.watchDistance = i;
		}

		private void updateTicket(long l, int i, boolean bl, boolean bl2) {
			if (bl != bl2) {
				ChunkTicket<?> chunkTicket = new ChunkTicket<>(ChunkTicketType.field_14033, ChunkTicketManager.NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(l));
				if (bl2) {
					ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
							if (this.isWithinViewDistance(this.getLevel(l))) {
								ChunkTicketManager.this.addTicket(l, chunkTicket);
								ChunkTicketManager.this.chunkPositions.add(l);
							} else {
								ChunkTicketManager.this.playerTicketThrottlerSorter.send(ChunkTaskPrioritySystem.createSorterMessage(() -> {
								}, l, false));
							}
						}), l, () -> i));
				} else {
					ChunkTicketManager.this.playerTicketThrottlerSorter
						.send(
							ChunkTaskPrioritySystem.createSorterMessage(
								() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(l, chunkTicket)), l, true
							)
						);
				}
			}
		}

		@Override
		public void updateLevels() {
			super.updateLevels();
			if (!this.positionsAffected.isEmpty()) {
				LongIterator longIterator = this.positionsAffected.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					int i = this.distances.get(l);
					int j = this.getLevel(l);
					if (i != j) {
						ChunkTicketManager.this.levelUpdateListener.updateLevel(new ChunkPos(l), () -> this.distances.get(l), j, ix -> {
							if (ix >= this.distances.defaultReturnValue()) {
								this.distances.remove(l);
							} else {
								this.distances.put(l, ix);
							}
						});
						this.updateTicket(l, j, this.isWithinViewDistance(i), this.isWithinViewDistance(j));
					}
				}

				this.positionsAffected.clear();
			}
		}

		private boolean isWithinViewDistance(int i) {
			return i <= this.watchDistance - 2;
		}
	}

	class TicketDistanceLevelPropagator extends ChunkPosDistanceLevelPropagator {
		public TicketDistanceLevelPropagator() {
			super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
		}

		@Override
		protected int getInitialLevel(long l) {
			SortedArraySet<ChunkTicket<?>> sortedArraySet = (SortedArraySet<ChunkTicket<?>>)ChunkTicketManager.this.ticketsByPosition.get(l);
			if (sortedArraySet == null) {
				return Integer.MAX_VALUE;
			} else {
				return sortedArraySet.isEmpty() ? Integer.MAX_VALUE : sortedArraySet.first().getLevel();
			}
		}

		@Override
		protected int getLevel(long l) {
			if (!ChunkTicketManager.this.isUnloaded(l)) {
				ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
				if (chunkHolder != null) {
					return chunkHolder.getLevel();
				}
			}

			return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
		}

		@Override
		protected void setLevel(long l, int i) {
			ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(l);
			int j = chunkHolder == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : chunkHolder.getLevel();
			if (j != i) {
				chunkHolder = ChunkTicketManager.this.setLevel(l, i, chunkHolder, j);
				if (chunkHolder != null) {
					ChunkTicketManager.this.chunkHolders.add(chunkHolder);
				}
			}
		}

		public int update(int i) {
			return this.applyPendingUpdates(i);
		}
	}
}
