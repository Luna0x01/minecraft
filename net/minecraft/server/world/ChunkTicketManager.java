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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.SortedArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.world.ChunkPosDistanceLevelPropagator;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ChunkTicketManager {
	static final Logger LOGGER = LogManager.getLogger();
	private static final int field_29764 = 2;
	static final int NEARBY_PLAYER_TICKET_LEVEL = 33 + ChunkStatus.getDistanceFromFull(ChunkStatus.FULL) - 2;
	private static final int field_29765 = 4;
	final Long2ObjectMap<ObjectSet<ServerPlayerEntity>> playersByChunkPos = new Long2ObjectOpenHashMap();
	final Long2ObjectOpenHashMap<SortedArraySet<ChunkTicket<?>>> ticketsByPosition = new Long2ObjectOpenHashMap();
	private final ChunkTicketManager.TicketDistanceLevelPropagator distanceFromTicketTracker = new ChunkTicketManager.TicketDistanceLevelPropagator();
	private final ChunkTicketManager.DistanceFromNearestPlayerTracker distanceFromNearestPlayerTracker = new ChunkTicketManager.DistanceFromNearestPlayerTracker(8);
	private final ChunkTicketManager.NearbyChunkTicketUpdater nearbyChunkTicketUpdater = new ChunkTicketManager.NearbyChunkTicketUpdater(33);
	final Set<ChunkHolder> chunkHolders = Sets.newHashSet();
	final ChunkTaskPrioritySystem levelUpdateListener;
	final MessageListener<ChunkTaskPrioritySystem.Task<Runnable>> playerTicketThrottler;
	final MessageListener<ChunkTaskPrioritySystem.UnblockingMessage> playerTicketThrottlerUnblocker;
	final LongSet chunkPositions = new LongOpenHashSet();
	final Executor mainThreadExecutor;
	private long age;

	protected ChunkTicketManager(Executor workerExecutor, Executor mainThreadExecutor) {
		MessageListener<Runnable> messageListener = MessageListener.create("player ticket throttler", mainThreadExecutor::execute);
		ChunkTaskPrioritySystem chunkTaskPrioritySystem = new ChunkTaskPrioritySystem(ImmutableList.of(messageListener), workerExecutor, 4);
		this.levelUpdateListener = chunkTaskPrioritySystem;
		this.playerTicketThrottler = chunkTaskPrioritySystem.createExecutor(messageListener, true);
		this.playerTicketThrottlerUnblocker = chunkTaskPrioritySystem.createUnblockingExecutor(messageListener);
		this.mainThreadExecutor = mainThreadExecutor;
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

	protected abstract boolean isUnloaded(long pos);

	@Nullable
	protected abstract ChunkHolder getChunkHolder(long pos);

	@Nullable
	protected abstract ChunkHolder setLevel(long pos, int level, @Nullable ChunkHolder holder, int i);

	public boolean tick(ThreadedAnvilChunkStorage threadedAnvilChunkStorage) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		this.nearbyChunkTicketUpdater.updateLevels();
		int i = Integer.MAX_VALUE - this.distanceFromTicketTracker.update(Integer.MAX_VALUE);
		boolean bl = i != 0;
		if (bl) {
		}

		if (!this.chunkHolders.isEmpty()) {
			this.chunkHolders.forEach(chunkHolderx -> chunkHolderx.tick(threadedAnvilChunkStorage, this.mainThreadExecutor));
			this.chunkHolders.clear();
			return true;
		} else {
			if (!this.chunkPositions.isEmpty()) {
				LongIterator longIterator = this.chunkPositions.iterator();

				while (longIterator.hasNext()) {
					long l = longIterator.nextLong();
					if (this.getTicketSet(l).stream().anyMatch(chunkTicket -> chunkTicket.getType() == ChunkTicketType.PLAYER)) {
						ChunkHolder chunkHolder = threadedAnvilChunkStorage.getCurrentChunkHolder(l);
						if (chunkHolder == null) {
							throw new IllegalStateException();
						}

						CompletableFuture<Either<WorldChunk, ChunkHolder.Unloaded>> completableFuture = chunkHolder.getEntityTickingFuture();
						completableFuture.thenAccept(
							either -> this.mainThreadExecutor.execute(() -> this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {
									}, l, false)))
						);
					}
				}

				this.chunkPositions.clear();
			}

			return bl;
		}
	}

	void addTicket(long position, ChunkTicket<?> ticket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(position);
		int i = getLevel(sortedArraySet);
		ChunkTicket<?> chunkTicket = sortedArraySet.addAndGet(ticket);
		chunkTicket.setTickCreated(this.age);
		if (ticket.getLevel() < i) {
			this.distanceFromTicketTracker.updateLevel(position, ticket.getLevel(), true);
		}
	}

	void removeTicket(long pos, ChunkTicket<?> ticket) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = this.getTicketSet(pos);
		if (sortedArraySet.remove(ticket)) {
		}

		if (sortedArraySet.isEmpty()) {
			this.ticketsByPosition.remove(pos);
		}

		this.distanceFromTicketTracker.updateLevel(pos, getLevel(sortedArraySet), false);
	}

	public <T> void addTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
		this.addTicket(pos.toLong(), new ChunkTicket<>(type, level, argument));
	}

	public <T> void removeTicketWithLevel(ChunkTicketType<T> type, ChunkPos pos, int level, T argument) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(type, level, argument);
		this.removeTicket(pos.toLong(), chunkTicket);
	}

	public <T> void addTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
		this.addTicket(pos.toLong(), new ChunkTicket<>(type, 33 - radius, argument));
	}

	public <T> void removeTicket(ChunkTicketType<T> type, ChunkPos pos, int radius, T argument) {
		ChunkTicket<T> chunkTicket = new ChunkTicket<>(type, 33 - radius, argument);
		this.removeTicket(pos.toLong(), chunkTicket);
	}

	private SortedArraySet<ChunkTicket<?>> getTicketSet(long position) {
		return (SortedArraySet<ChunkTicket<?>>)this.ticketsByPosition.computeIfAbsent(position, l -> SortedArraySet.create(4));
	}

	protected void setChunkForced(ChunkPos pos, boolean forced) {
		ChunkTicket<ChunkPos> chunkTicket = new ChunkTicket<>(ChunkTicketType.FORCED, 31, pos);
		if (forced) {
			this.addTicket(pos.toLong(), chunkTicket);
		} else {
			this.removeTicket(pos.toLong(), chunkTicket);
		}
	}

	public void handleChunkEnter(ChunkSectionPos pos, ServerPlayerEntity player) {
		long l = pos.toChunkPos().toLong();
		((ObjectSet)this.playersByChunkPos.computeIfAbsent(l, lx -> new ObjectOpenHashSet())).add(player);
		this.distanceFromNearestPlayerTracker.updateLevel(l, 0, true);
		this.nearbyChunkTicketUpdater.updateLevel(l, 0, true);
	}

	public void handleChunkLeave(ChunkSectionPos pos, ServerPlayerEntity player) {
		long l = pos.toChunkPos().toLong();
		ObjectSet<ServerPlayerEntity> objectSet = (ObjectSet<ServerPlayerEntity>)this.playersByChunkPos.get(l);
		objectSet.remove(player);
		if (objectSet.isEmpty()) {
			this.playersByChunkPos.remove(l);
			this.distanceFromNearestPlayerTracker.updateLevel(l, Integer.MAX_VALUE, false);
			this.nearbyChunkTicketUpdater.updateLevel(l, Integer.MAX_VALUE, false);
		}
	}

	protected String getTicket(long pos) {
		SortedArraySet<ChunkTicket<?>> sortedArraySet = (SortedArraySet<ChunkTicket<?>>)this.ticketsByPosition.get(pos);
		String string2;
		if (sortedArraySet != null && !sortedArraySet.isEmpty()) {
			string2 = sortedArraySet.first().toString();
		} else {
			string2 = "no_ticket";
		}

		return string2;
	}

	protected void setWatchDistance(int viewDistance) {
		this.nearbyChunkTicketUpdater.setWatchDistance(viewDistance);
	}

	public int getSpawningChunkCount() {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.size();
	}

	public boolean method_20800(long l) {
		this.distanceFromNearestPlayerTracker.updateLevels();
		return this.distanceFromNearestPlayerTracker.distanceFromNearestPlayer.containsKey(l);
	}

	public String toDumpString() {
		return this.levelUpdateListener.getDebugString();
	}

	private void method_34876(String string) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(new File(string));

			try {
				ObjectIterator var3 = this.ticketsByPosition.long2ObjectEntrySet().iterator();

				while (var3.hasNext()) {
					Entry<SortedArraySet<ChunkTicket<?>>> entry = (Entry<SortedArraySet<ChunkTicket<?>>>)var3.next();
					ChunkPos chunkPos = new ChunkPos(entry.getLongKey());

					for (ChunkTicket<?> chunkTicket : (SortedArraySet)entry.getValue()) {
						fileOutputStream.write(
							(chunkPos.x + "\t" + chunkPos.z + "\t" + chunkTicket.getType() + "\t" + chunkTicket.getLevel() + "\t\n").getBytes(StandardCharsets.UTF_8)
						);
					}
				}
			} catch (Throwable var9) {
				try {
					fileOutputStream.close();
				} catch (Throwable var8) {
					var9.addSuppressed(var8);
				}

				throw var9;
			}

			fileOutputStream.close();
		} catch (IOException var10) {
			LOGGER.error(var10);
		}
	}

	class DistanceFromNearestPlayerTracker extends ChunkPosDistanceLevelPropagator {
		protected final Long2ByteMap distanceFromNearestPlayer = new Long2ByteOpenHashMap();
		protected final int maxDistance;

		protected DistanceFromNearestPlayerTracker(int maxDistance) {
			super(maxDistance + 2, 16, 256);
			this.maxDistance = maxDistance;
			this.distanceFromNearestPlayer.defaultReturnValue((byte)(maxDistance + 2));
		}

		@Override
		protected int getLevel(long id) {
			return this.distanceFromNearestPlayer.get(id);
		}

		@Override
		protected void setLevel(long id, int level) {
			byte b;
			if (level > this.maxDistance) {
				b = this.distanceFromNearestPlayer.remove(id);
			} else {
				b = this.distanceFromNearestPlayer.put(id, (byte)level);
			}

			this.onDistanceChange(id, b, level);
		}

		protected void onDistanceChange(long pos, int oldDistance, int distance) {
		}

		@Override
		protected int getInitialLevel(long id) {
			return this.isPlayerInChunk(id) ? 0 : Integer.MAX_VALUE;
		}

		private boolean isPlayerInChunk(long chunkPos) {
			ObjectSet<ServerPlayerEntity> objectSet = (ObjectSet<ServerPlayerEntity>)ChunkTicketManager.this.playersByChunkPos.get(chunkPos);
			return objectSet != null && !objectSet.isEmpty();
		}

		public void updateLevels() {
			this.applyPendingUpdates(Integer.MAX_VALUE);
		}

		private void method_34878(String string) {
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(new File(string));

				try {
					ObjectIterator var3 = this.distanceFromNearestPlayer.long2ByteEntrySet().iterator();

					while (var3.hasNext()) {
						it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var3.next();
						ChunkPos chunkPos = new ChunkPos(entry.getLongKey());
						String string2 = Byte.toString(entry.getByteValue());
						fileOutputStream.write((chunkPos.x + "\t" + chunkPos.z + "\t" + string2 + "\n").getBytes(StandardCharsets.UTF_8));
					}
				} catch (Throwable var8) {
					try {
						fileOutputStream.close();
					} catch (Throwable var7) {
						var8.addSuppressed(var7);
					}

					throw var8;
				}

				fileOutputStream.close();
			} catch (IOException var9) {
				ChunkTicketManager.LOGGER.error(var9);
			}
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
		protected void onDistanceChange(long pos, int oldDistance, int distance) {
			this.positionsAffected.add(pos);
		}

		public void setWatchDistance(int watchDistance) {
			ObjectIterator var2 = this.distanceFromNearestPlayer.long2ByteEntrySet().iterator();

			while (var2.hasNext()) {
				it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry entry = (it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry)var2.next();
				byte b = entry.getByteValue();
				long l = entry.getLongKey();
				this.updateTicket(l, b, this.isWithinViewDistance(b), b <= watchDistance - 2);
			}

			this.watchDistance = watchDistance;
		}

		private void updateTicket(long pos, int distance, boolean oldWithinViewDistance, boolean withinViewDistance) {
			if (oldWithinViewDistance != withinViewDistance) {
				ChunkTicket<?> chunkTicket = new ChunkTicket<>(ChunkTicketType.PLAYER, ChunkTicketManager.NEARBY_PLAYER_TICKET_LEVEL, new ChunkPos(pos));
				if (withinViewDistance) {
					ChunkTicketManager.this.playerTicketThrottler.send(ChunkTaskPrioritySystem.createMessage(() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> {
							if (this.isWithinViewDistance(this.getLevel(pos))) {
								ChunkTicketManager.this.addTicket(pos, chunkTicket);
								ChunkTicketManager.this.chunkPositions.add(pos);
							} else {
								ChunkTicketManager.this.playerTicketThrottlerUnblocker.send(ChunkTaskPrioritySystem.createUnblockingMessage(() -> {
								}, pos, false));
							}
						}), pos, () -> distance));
				} else {
					ChunkTicketManager.this.playerTicketThrottlerUnblocker
						.send(
							ChunkTaskPrioritySystem.createUnblockingMessage(
								() -> ChunkTicketManager.this.mainThreadExecutor.execute(() -> ChunkTicketManager.this.removeTicket(pos, chunkTicket)), pos, true
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

		private boolean isWithinViewDistance(int distance) {
			return distance <= this.watchDistance - 2;
		}
	}

	class TicketDistanceLevelPropagator extends ChunkPosDistanceLevelPropagator {
		public TicketDistanceLevelPropagator() {
			super(ThreadedAnvilChunkStorage.MAX_LEVEL + 2, 16, 256);
		}

		@Override
		protected int getInitialLevel(long id) {
			SortedArraySet<ChunkTicket<?>> sortedArraySet = (SortedArraySet<ChunkTicket<?>>)ChunkTicketManager.this.ticketsByPosition.get(id);
			if (sortedArraySet == null) {
				return Integer.MAX_VALUE;
			} else {
				return sortedArraySet.isEmpty() ? Integer.MAX_VALUE : sortedArraySet.first().getLevel();
			}
		}

		@Override
		protected int getLevel(long id) {
			if (!ChunkTicketManager.this.isUnloaded(id)) {
				ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(id);
				if (chunkHolder != null) {
					return chunkHolder.getLevel();
				}
			}

			return ThreadedAnvilChunkStorage.MAX_LEVEL + 1;
		}

		@Override
		protected void setLevel(long id, int level) {
			ChunkHolder chunkHolder = ChunkTicketManager.this.getChunkHolder(id);
			int i = chunkHolder == null ? ThreadedAnvilChunkStorage.MAX_LEVEL + 1 : chunkHolder.getLevel();
			if (i != level) {
				chunkHolder = ChunkTicketManager.this.setLevel(id, level, chunkHolder, i);
				if (chunkHolder != null) {
					ChunkTicketManager.this.chunkHolders.add(chunkHolder);
				}
			}
		}

		public int update(int distance) {
			return this.applyPendingUpdates(distance);
		}
	}
}
