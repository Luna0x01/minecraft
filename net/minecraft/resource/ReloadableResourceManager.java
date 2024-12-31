package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager extends ResourceManager, AutoCloseable {
	default CompletableFuture<Unit> reload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
		return this.reload(prepareExecutor, applyExecutor, initialStage, packs).whenComplete();
	}

	ResourceReload reload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs);

	void registerReloader(ResourceReloader reloader);

	void close();
}
