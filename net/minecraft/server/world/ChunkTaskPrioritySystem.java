package net.minecraft.server.world;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.MessageListener;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkTaskPrioritySystem implements AutoCloseable, ChunkHolder.LevelUpdateListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<MessageListener<?>, LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>> queues;
	private final Set<MessageListener<?>> actors;
	private final TaskExecutor<TaskQueue.PrioritizedTask> sorter;

	public ChunkTaskPrioritySystem(List<MessageListener<?>> list, Executor executor, int i) {
		this.queues = (Map<MessageListener<?>, LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>>)list.stream()
			.collect(Collectors.toMap(Function.identity(), messageListener -> new LevelPrioritizedQueue(messageListener.getName() + "_queue", i)));
		this.actors = Sets.newHashSet(list);
		this.sorter = new TaskExecutor<>(new TaskQueue.Prioritized(4), executor, "sorter");
	}

	public static ChunkTaskPrioritySystem.Task<Runnable> createMessage(Runnable runnable, long l, IntSupplier intSupplier) {
		return new ChunkTaskPrioritySystem.Task<>(messageListener -> () -> {
				runnable.run();
				messageListener.send(Unit.field_17274);
			}, l, intSupplier);
	}

	public static ChunkTaskPrioritySystem.Task<Runnable> createMessage(ChunkHolder chunkHolder, Runnable runnable) {
		return createMessage(runnable, chunkHolder.getPos().toLong(), chunkHolder::getCompletedLevel);
	}

	public static ChunkTaskPrioritySystem.SorterMessage createSorterMessage(Runnable runnable, long l, boolean bl) {
		return new ChunkTaskPrioritySystem.SorterMessage(runnable, l, bl);
	}

	public <T> MessageListener<ChunkTaskPrioritySystem.Task<T>> createExecutor(MessageListener<T> messageListener, boolean bl) {
		return (MessageListener<ChunkTaskPrioritySystem.Task<T>>)this.sorter
			.ask(
				messageListener2 -> new TaskQueue.PrioritizedTask(
						0,
						() -> {
							this.getQueue(messageListener);
							messageListener2.send(
								MessageListener.create(
									"chunk priority sorter around " + messageListener.getName(),
									task -> this.execute(messageListener, task.function, task.pos, task.lastLevelUpdatedToProvider, bl)
								)
							);
						}
					)
			)
			.join();
	}

	public MessageListener<ChunkTaskPrioritySystem.SorterMessage> createSorterExecutor(MessageListener<Runnable> messageListener) {
		return (MessageListener<ChunkTaskPrioritySystem.SorterMessage>)this.sorter
			.ask(
				messageListener2 -> new TaskQueue.PrioritizedTask(
						0,
						() -> messageListener2.send(
								MessageListener.create(
									"chunk priority sorter around " + messageListener.getName(),
									sorterMessage -> this.sort(messageListener, sorterMessage.pos, sorterMessage.runnable, sorterMessage.field_17451)
								)
							)
					)
			)
			.join();
	}

	@Override
	public void updateLevel(ChunkPos chunkPos, IntSupplier intSupplier, int i, IntConsumer intConsumer) {
		this.sorter.send(new TaskQueue.PrioritizedTask(0, () -> {
			int j = intSupplier.getAsInt();
			this.queues.values().forEach(levelPrioritizedQueue -> levelPrioritizedQueue.updateLevel(j, chunkPos, i));
			intConsumer.accept(i);
		}));
	}

	private <T> void sort(MessageListener<T> messageListener, long l, Runnable runnable, boolean bl) {
		this.sorter.send(new TaskQueue.PrioritizedTask(1, () -> {
			LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue = this.getQueue(messageListener);
			levelPrioritizedQueue.clearPosition(l, bl);
			if (this.actors.remove(messageListener)) {
				this.method_17630(levelPrioritizedQueue, messageListener);
			}

			runnable.run();
		}));
	}

	private <T> void execute(MessageListener<T> messageListener, Function<MessageListener<Unit>, T> function, long l, IntSupplier intSupplier, boolean bl) {
		this.sorter.send(new TaskQueue.PrioritizedTask(2, () -> {
			LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue = this.getQueue(messageListener);
			int i = intSupplier.getAsInt();
			levelPrioritizedQueue.add(Optional.of(function), l, i);
			if (bl) {
				levelPrioritizedQueue.add(Optional.empty(), l, i);
			}

			if (this.actors.remove(messageListener)) {
				this.method_17630(levelPrioritizedQueue, messageListener);
			}
		}));
	}

	private <T> void method_17630(LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> levelPrioritizedQueue, MessageListener<T> messageListener) {
		this.sorter.send(new TaskQueue.PrioritizedTask(3, () -> {
			Stream<Either<Function<MessageListener<Unit>, T>, Runnable>> stream = levelPrioritizedQueue.poll();
			if (stream == null) {
				this.actors.add(messageListener);
			} else {
				Util.combine((List)stream.map(either -> (CompletableFuture)either.map(messageListener::ask, runnable -> {
						runnable.run();
						return CompletableFuture.completedFuture(Unit.field_17274);
					})).collect(Collectors.toList())).thenAccept(list -> this.method_17630(levelPrioritizedQueue, messageListener));
			}
		}));
	}

	private <T> LevelPrioritizedQueue<Function<MessageListener<Unit>, T>> getQueue(MessageListener<T> messageListener) {
		LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>> levelPrioritizedQueue = (LevelPrioritizedQueue<? extends Function<MessageListener<Unit>, ?>>)this.queues
			.get(messageListener);
		if (levelPrioritizedQueue == null) {
			throw (IllegalArgumentException)Util.throwOrPause((T)(new IllegalArgumentException("No queue for: " + messageListener)));
		} else {
			return (LevelPrioritizedQueue<Function<MessageListener<Unit>, T>>)levelPrioritizedQueue;
		}
	}

	@VisibleForTesting
	public String method_21680() {
		return (String)this.queues
				.entrySet()
				.stream()
				.map(
					entry -> ((MessageListener)entry.getKey()).getName()
							+ "=["
							+ (String)((LevelPrioritizedQueue)entry.getValue())
								.method_21679()
								.stream()
								.map(long_ -> long_ + ":" + new ChunkPos(long_))
								.collect(Collectors.joining(","))
							+ "]"
				)
				.collect(Collectors.joining(","))
			+ ", s="
			+ this.actors.size();
	}

	public void close() {
		this.queues.keySet().forEach(MessageListener::close);
	}

	public static final class SorterMessage {
		private final Runnable runnable;
		private final long pos;
		private final boolean field_17451;

		private SorterMessage(Runnable runnable, long l, boolean bl) {
			this.runnable = runnable;
			this.pos = l;
			this.field_17451 = bl;
		}
	}

	public static final class Task<T> {
		private final Function<MessageListener<Unit>, T> function;
		private final long pos;
		private final IntSupplier lastLevelUpdatedToProvider;

		private Task(Function<MessageListener<Unit>, T> function, long l, IntSupplier intSupplier) {
			this.function = function;
			this.pos = l;
			this.lastLevelUpdatedToProvider = intSupplier;
		}
	}
}
