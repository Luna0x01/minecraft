package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public interface ResourceReloader {
	CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	);

	default String getName() {
		return this.getClass().getSimpleName();
	}

	public interface Synchronizer {
		<T> CompletableFuture<T> whenPrepared(T preparedObject);
	}
}
