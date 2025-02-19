package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public abstract class SinglePreparationResourceReloader<T> implements ResourceReloader {
	@Override
	public final CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return CompletableFuture.supplyAsync(() -> this.prepare(manager, prepareProfiler), prepareExecutor)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(object -> this.apply((T)object, manager, applyProfiler), applyExecutor);
	}

	protected abstract T prepare(ResourceManager manager, Profiler profiler);

	protected abstract void apply(T prepared, ResourceManager manager, Profiler profiler);
}
