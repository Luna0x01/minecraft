package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

public interface ResourceReloadMonitor {
	CompletableFuture<Unit> whenComplete();

	float getProgress();

	boolean isLoadStageComplete();

	boolean isApplyStageComplete();

	void throwExceptions();
}
