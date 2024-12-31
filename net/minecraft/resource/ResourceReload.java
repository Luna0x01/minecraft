package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

public interface ResourceReload {
	CompletableFuture<Unit> whenComplete();

	float getProgress();

	boolean isComplete();

	void throwException();
}
