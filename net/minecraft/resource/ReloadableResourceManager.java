package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager extends ResourceManager {
	CompletableFuture<Unit> beginReload(Executor executor, Executor executor2, List<ResourcePack> list, CompletableFuture<Unit> completableFuture);

	ResourceReloadMonitor beginMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<ResourcePack> list);

	void registerListener(ResourceReloadListener resourceReloadListener);
}
