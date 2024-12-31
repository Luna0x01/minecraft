package net.minecraft.server;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.MailboxProcessor;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.ChunkStatus;

public class QueueingWorldGenerationProgressListener implements WorldGenerationProgressListener {
	private final WorldGenerationProgressListener progressListener;
	private final MailboxProcessor<Runnable> queue;

	public QueueingWorldGenerationProgressListener(WorldGenerationProgressListener worldGenerationProgressListener, Executor executor) {
		this.progressListener = worldGenerationProgressListener;
		this.queue = MailboxProcessor.create(executor, "progressListener");
	}

	@Override
	public void start(ChunkPos chunkPos) {
		this.queue.send(() -> this.progressListener.start(chunkPos));
	}

	@Override
	public void setChunkStatus(ChunkPos chunkPos, @Nullable ChunkStatus chunkStatus) {
		this.queue.send(() -> this.progressListener.setChunkStatus(chunkPos, chunkStatus));
	}

	@Override
	public void stop() {
		this.queue.send(this.progressListener::stop);
	}
}
