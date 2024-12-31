package net.minecraft.util;

import com.google.common.util.concurrent.ListenableFuture;

public interface ThreadExecutor {
	ListenableFuture<Object> submit(Runnable task);

	boolean isOnThread();
}
