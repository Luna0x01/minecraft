package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.util.thread.TaskQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StorageIoWorker implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final AtomicBoolean closed = new AtomicBoolean();
	private final TaskExecutor<TaskQueue.PrioritizedTask> executor;
	private final RegionBasedStorage storage;
	private final Map<ChunkPos, StorageIoWorker.Result> results = Maps.newLinkedHashMap();

	protected StorageIoWorker(File directory, boolean dsync, String name) {
		this.storage = new RegionBasedStorage(directory, dsync);
		this.executor = new TaskExecutor<>(new TaskQueue.Prioritized(StorageIoWorker.Priority.values().length), Util.getIoWorkerExecutor(), "IOWorker-" + name);
	}

	public CompletableFuture<Void> setResult(ChunkPos pos, @Nullable NbtCompound nbt) {
		return this.run(() -> {
			StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.computeIfAbsent(pos, chunkPosx -> new StorageIoWorker.Result(nbt));
			result.nbt = nbt;
			return Either.left(result.future);
		}).thenCompose(Function.identity());
	}

	@Nullable
	public NbtCompound getNbt(ChunkPos pos) throws IOException {
		CompletableFuture<NbtCompound> completableFuture = this.readChunkData(pos);

		try {
			return (NbtCompound)completableFuture.join();
		} catch (CompletionException var4) {
			if (var4.getCause() instanceof IOException) {
				throw (IOException)var4.getCause();
			} else {
				throw var4;
			}
		}
	}

	protected CompletableFuture<NbtCompound> readChunkData(ChunkPos pos) {
		return this.run(() -> {
			StorageIoWorker.Result result = (StorageIoWorker.Result)this.results.get(pos);
			if (result != null) {
				return Either.left(result.nbt);
			} else {
				try {
					NbtCompound nbtCompound = this.storage.getTagAt(pos);
					return Either.left(nbtCompound);
				} catch (Exception var4) {
					LOGGER.warn("Failed to read chunk {}", pos, var4);
					return Either.right(var4);
				}
			}
		});
	}

	public CompletableFuture<Void> completeAll(boolean bl) {
		CompletableFuture<Void> completableFuture = this.run(
				() -> Either.left(
						CompletableFuture.allOf((CompletableFuture[])this.results.values().stream().map(result -> result.future).toArray(CompletableFuture[]::new))
					)
			)
			.thenCompose(Function.identity());
		return bl ? completableFuture.thenCompose(void_ -> this.run(() -> {
				try {
					this.storage.sync();
					return Either.left(null);
				} catch (Exception var2x) {
					LOGGER.warn("Failed to synchronize chunks", var2x);
					return Either.right(var2x);
				}
			})) : completableFuture.thenCompose(void_ -> this.run(() -> Either.left(null)));
	}

	private <T> CompletableFuture<T> run(Supplier<Either<T, Exception>> task) {
		return this.executor.askFallible(messageListener -> new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.FOREGROUND.ordinal(), () -> {
				if (!this.closed.get()) {
					messageListener.send((Either)task.get());
				}

				this.writeRemainingResults();
			}));
	}

	private void writeResult() {
		if (!this.results.isEmpty()) {
			Iterator<Entry<ChunkPos, StorageIoWorker.Result>> iterator = this.results.entrySet().iterator();
			Entry<ChunkPos, StorageIoWorker.Result> entry = (Entry<ChunkPos, StorageIoWorker.Result>)iterator.next();
			iterator.remove();
			this.write((ChunkPos)entry.getKey(), (StorageIoWorker.Result)entry.getValue());
			this.writeRemainingResults();
		}
	}

	private void writeRemainingResults() {
		this.executor.send(new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.BACKGROUND.ordinal(), this::writeResult));
	}

	private void write(ChunkPos pos, StorageIoWorker.Result result) {
		try {
			this.storage.write(pos, result.nbt);
			result.future.complete(null);
		} catch (Exception var4) {
			LOGGER.error("Failed to store chunk {}", pos, var4);
			result.future.completeExceptionally(var4);
		}
	}

	public void close() throws IOException {
		if (this.closed.compareAndSet(false, true)) {
			this.executor
				.ask(messageListener -> new TaskQueue.PrioritizedTask(StorageIoWorker.Priority.SHUTDOWN.ordinal(), () -> messageListener.send(Unit.INSTANCE)))
				.join();
			this.executor.close();

			try {
				this.storage.close();
			} catch (Exception var2) {
				LOGGER.error("Failed to close storage", var2);
			}
		}
	}

	static enum Priority {
		FOREGROUND,
		BACKGROUND,
		SHUTDOWN;
	}

	static class Result {
		@Nullable
		NbtCompound nbt;
		final CompletableFuture<Void> future = new CompletableFuture();

		public Result(@Nullable NbtCompound nbt) {
			this.nbt = nbt;
		}
	}
}
