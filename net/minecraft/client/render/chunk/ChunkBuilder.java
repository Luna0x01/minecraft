package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBufferBuilderStorage;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexBufferUploader;
import net.minecraft.client.render.world.ChunkRenderHelperImpl;
import net.minecraft.client.world.BuiltChunk;
import net.minecraft.client.world.ChunkAssemblyHelper;
import net.minecraft.client.world.ChunkRenderThread;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
	private final int field_13607;
	private final List<Thread> field_13608 = Lists.newArrayList();
	private final List<ChunkRenderThread> field_11037 = Lists.newArrayList();
	private final PriorityBlockingQueue<net.minecraft.client.world.ChunkBuilder> field_13609 = Queues.newPriorityBlockingQueue();
	private final BlockingQueue<BlockBufferBuilderStorage> threadBuffers;
	private final BufferRenderer bufferRenderer = new BufferRenderer();
	private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
	private final Queue<ChunkBuilder.class_2889> uploadQueue = Queues.newPriorityQueue();
	private final ChunkRenderThread renderThread;

	public ChunkBuilder() {
		int i = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
		int j = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, i / 5));
		this.field_13607 = MathHelper.clamp(j * 10, 1, i);
		if (j > 1) {
			for (int k = 0; k < j; k++) {
				ChunkRenderThread chunkRenderThread = new ChunkRenderThread(this);
				Thread thread = threadFactory.newThread(chunkRenderThread);
				thread.start();
				this.field_11037.add(chunkRenderThread);
				this.field_13608.add(thread);
			}
		}

		this.threadBuffers = Queues.newArrayBlockingQueue(this.field_13607);

		for (int l = 0; l < this.field_13607; l++) {
			this.threadBuffers.add(new BlockBufferBuilderStorage());
		}

		this.renderThread = new ChunkRenderThread(this, new BlockBufferBuilderStorage());
	}

	public String getDebugString() {
		return this.field_13608.isEmpty()
			? String.format("pC: %03d, single-threaded", this.field_13609.size())
			: String.format("pC: %03d, pU: %1d, aB: %1d", this.field_13609.size(), this.uploadQueue.size(), this.threadBuffers.size());
	}

	public boolean upload(long timeout) {
		boolean bl = false;

		boolean bl2;
		do {
			bl2 = false;
			if (this.field_13608.isEmpty()) {
				net.minecraft.client.world.ChunkBuilder chunkBuilder = (net.minecraft.client.world.ChunkBuilder)this.field_13609.poll();
				if (chunkBuilder != null) {
					try {
						this.renderThread.method_10137(chunkBuilder);
						bl2 = true;
					} catch (InterruptedException var8) {
						LOGGER.warn("Skipped task due to interrupt");
					}
				}
			}

			synchronized (this.uploadQueue) {
				if (!this.uploadQueue.isEmpty()) {
					((ChunkBuilder.class_2889)this.uploadQueue.poll()).field_13612.run();
					bl2 = true;
					bl = true;
				}
			}
		} while (timeout != 0L && bl2 && timeout >= System.nanoTime());

		return bl;
	}

	public boolean send(BuiltChunk chunk) {
		chunk.method_10166().lock();

		boolean var4;
		try {
			final net.minecraft.client.world.ChunkBuilder chunkBuilder = chunk.method_10167();
			chunkBuilder.method_10114(new Runnable() {
				public void run() {
					ChunkBuilder.this.field_13609.remove(chunkBuilder);
				}
			});
			boolean bl = this.field_13609.offer(chunkBuilder);
			if (!bl) {
				chunkBuilder.method_10118();
			}

			var4 = bl;
		} finally {
			chunk.method_10166().unlock();
		}

		return var4;
	}

	public boolean upload(BuiltChunk chunk) {
		chunk.method_10166().lock();

		boolean var3;
		try {
			net.minecraft.client.world.ChunkBuilder chunkBuilder = chunk.method_10167();

			try {
				this.renderThread.method_10137(chunkBuilder);
			} catch (InterruptedException var7) {
			}

			var3 = true;
		} finally {
			chunk.method_10166().unlock();
		}

		return var3;
	}

	public void stop() {
		this.clear();
		List<BlockBufferBuilderStorage> list = Lists.newArrayList();

		while (list.size() != this.field_13607) {
			this.upload(Long.MAX_VALUE);

			try {
				list.add(this.takeBuffer());
			} catch (InterruptedException var3) {
			}
		}

		this.threadBuffers.addAll(list);
	}

	public void addThreadBuffer(BlockBufferBuilderStorage storage) {
		this.threadBuffers.add(storage);
	}

	public BlockBufferBuilderStorage takeBuffer() throws InterruptedException {
		return (BlockBufferBuilderStorage)this.threadBuffers.take();
	}

	public net.minecraft.client.world.ChunkBuilder takeRebuildQueue() throws InterruptedException {
		return (net.minecraft.client.world.ChunkBuilder)this.field_13609.take();
	}

	public boolean method_10133(BuiltChunk chunk) {
		chunk.method_10166().lock();

		boolean var3;
		try {
			final net.minecraft.client.world.ChunkBuilder chunkBuilder = chunk.method_10168();
			if (chunkBuilder == null) {
				return true;
			}

			chunkBuilder.method_10114(new Runnable() {
				public void run() {
					ChunkBuilder.this.field_13609.remove(chunkBuilder);
				}
			});
			var3 = this.field_13609.offer(chunkBuilder);
		} finally {
			chunk.method_10166().unlock();
		}

		return var3;
	}

	public ListenableFuture<Object> method_12419(
		RenderLayer renderLayer, BufferBuilder bufferBuilder, BuiltChunk builtChunk, ChunkAssemblyHelper chunkAssemblyHelper, double d
	) {
		if (MinecraftClient.getInstance().isOnThread()) {
			if (GLX.supportsVbo()) {
				this.uploadVertexBuffer(bufferBuilder, builtChunk.method_10165(renderLayer.ordinal()));
			} else {
				this.uploadGlList(bufferBuilder, ((ChunkRenderHelperImpl)builtChunk).method_10153(renderLayer, chunkAssemblyHelper), builtChunk);
			}

			bufferBuilder.offset(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Object> listenableFutureTask = ListenableFutureTask.create(new Runnable() {
				public void run() {
					ChunkBuilder.this.method_12419(renderLayer, bufferBuilder, builtChunk, chunkAssemblyHelper, d);
				}
			}, null);
			synchronized (this.uploadQueue) {
				this.uploadQueue.add(new ChunkBuilder.class_2889(listenableFutureTask, d));
				return listenableFutureTask;
			}
		}
	}

	private void uploadGlList(BufferBuilder bufferBuilder, int id, BuiltChunk chunk) {
		GlStateManager.method_12312(id, 4864);
		GlStateManager.pushMatrix();
		chunk.method_10169();
		this.bufferRenderer.draw(bufferBuilder);
		GlStateManager.popMatrix();
		GlStateManager.method_12270();
	}

	private void uploadVertexBuffer(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
		this.vertexUploader.setBuffer(vertexBuffer);
		this.vertexUploader.draw(bufferBuilder);
	}

	public void clear() {
		while (!this.field_13609.isEmpty()) {
			net.minecraft.client.world.ChunkBuilder chunkBuilder = (net.minecraft.client.world.ChunkBuilder)this.field_13609.poll();
			if (chunkBuilder != null) {
				chunkBuilder.method_10118();
			}
		}
	}

	public boolean method_12420() {
		return this.field_13609.isEmpty() && this.uploadQueue.isEmpty();
	}

	public void method_12421() {
		this.clear();

		for (ChunkRenderThread chunkRenderThread : this.field_11037) {
			chunkRenderThread.method_12425();
		}

		for (Thread thread : this.field_13608) {
			try {
				thread.interrupt();
				thread.join();
			} catch (InterruptedException var4) {
				LOGGER.warn("Interrupted whilst waiting for worker to die", var4);
			}
		}

		this.threadBuffers.clear();
	}

	public boolean method_12422() {
		return this.threadBuffers.isEmpty();
	}

	class class_2889 implements Comparable<ChunkBuilder.class_2889> {
		private final ListenableFutureTask<Object> field_13612;
		private final double field_13613;

		public class_2889(ListenableFutureTask<Object> listenableFutureTask, double d) {
			this.field_13612 = listenableFutureTask;
			this.field_13613 = d;
		}

		public int compareTo(ChunkBuilder.class_2889 arg) {
			return Doubles.compare(this.field_13613, arg.field_13613);
		}
	}
}
