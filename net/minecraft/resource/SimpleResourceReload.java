package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

public class SimpleResourceReload<S> implements ResourceReload {
	private static final int FIRST_PREPARE_APPLY_WEIGHT = 2;
	private static final int SECOND_PREPARE_APPLY_WEIGHT = 2;
	private static final int RELOADER_WEIGHT = 1;
	protected final ResourceManager manager;
	protected final CompletableFuture<Unit> prepareStageFuture = new CompletableFuture();
	protected final CompletableFuture<List<S>> applyStageFuture;
	final Set<ResourceReloader> waitingReloaders;
	private final int reloaderCount;
	private int toApplyCount;
	private int appliedCount;
	private final AtomicInteger toPrepareCount = new AtomicInteger();
	private final AtomicInteger preparedCount = new AtomicInteger();

	public static SimpleResourceReload<Void> create(
		ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage
	) {
		return new SimpleResourceReload<>(
			prepareExecutor,
			applyExecutor,
			manager,
			reloaders,
			(synchronizer, resourceManager, reloader, prepare, apply) -> reloader.reload(
					synchronizer, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, prepareExecutor, apply
				),
			initialStage
		);
	}

	protected SimpleResourceReload(
		Executor prepareExecutor,
		Executor applyExecutor,
		ResourceManager manager,
		List<ResourceReloader> reloaders,
		SimpleResourceReload.Factory<S> factory,
		CompletableFuture<Unit> initialStage
	) {
		this.manager = manager;
		this.reloaderCount = reloaders.size();
		this.toPrepareCount.incrementAndGet();
		initialStage.thenRun(this.preparedCount::incrementAndGet);
		List<CompletableFuture<S>> list = Lists.newArrayList();
		CompletableFuture<?> completableFuture = initialStage;
		this.waitingReloaders = Sets.newHashSet(reloaders);

		for (final ResourceReloader resourceReloader : reloaders) {
			final CompletableFuture<?> completableFuture2 = completableFuture;
			CompletableFuture<S> completableFuture3 = factory.create(new ResourceReloader.Synchronizer() {
				@Override
				public <T> CompletableFuture<T> whenPrepared(T preparedObject) {
					applyExecutor.execute(() -> {
						SimpleResourceReload.this.waitingReloaders.remove(resourceReloader);
						if (SimpleResourceReload.this.waitingReloaders.isEmpty()) {
							SimpleResourceReload.this.prepareStageFuture.complete(Unit.INSTANCE);
						}
					});
					return SimpleResourceReload.this.prepareStageFuture.thenCombine(completableFuture2, (unit, object2) -> preparedObject);
				}
			}, manager, resourceReloader, preparation -> {
				this.toPrepareCount.incrementAndGet();
				prepareExecutor.execute(() -> {
					preparation.run();
					this.preparedCount.incrementAndGet();
				});
			}, application -> {
				this.toApplyCount++;
				applyExecutor.execute(() -> {
					application.run();
					this.appliedCount++;
				});
			});
			list.add(completableFuture3);
			completableFuture = completableFuture3;
		}

		this.applyStageFuture = Util.combine(list);
	}

	@Override
	public CompletableFuture<Unit> whenComplete() {
		return this.applyStageFuture.thenApply(results -> Unit.INSTANCE);
	}

	@Override
	public float getProgress() {
		int i = this.reloaderCount - this.waitingReloaders.size();
		float f = (float)(this.preparedCount.get() * 2 + this.appliedCount * 2 + i * 1);
		float g = (float)(this.toPrepareCount.get() * 2 + this.toApplyCount * 2 + this.reloaderCount * 1);
		return f / g;
	}

	@Override
	public boolean isComplete() {
		return this.applyStageFuture.isDone();
	}

	@Override
	public void throwException() {
		if (this.applyStageFuture.isCompletedExceptionally()) {
			this.applyStageFuture.join();
		}
	}

	protected interface Factory<S> {
		CompletableFuture<S> create(
			ResourceReloader.Synchronizer synchronizer, ResourceManager manager, ResourceReloader reloader, Executor prepareExecutor, Executor applyExecutor
		);
	}
}
