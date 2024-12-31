package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
	private final List<ChunkRenderThread> field_11037 = Lists.newArrayList();
	private final BlockingQueue<net.minecraft.client.world.ChunkBuilder> rebuildQueue = Queues.newArrayBlockingQueue(100);
	private final BlockingQueue<BlockBufferBuilderStorage> threadBuffers = Queues.newArrayBlockingQueue(5);
	private final BufferRenderer bufferRenderer = new BufferRenderer();
	private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
	private final Queue<ListenableFutureTask<?>> uploadQueue = Queues.newArrayDeque();
	private final ChunkRenderThread renderThread;

	public ChunkBuilder() {
		for (int i = 0; i < 2; i++) {
			ChunkRenderThread chunkRenderThread = new ChunkRenderThread(this);
			Thread thread = threadFactory.newThread(chunkRenderThread);
			thread.start();
			this.field_11037.add(chunkRenderThread);
		}

		for (int j = 0; j < 5; j++) {
			this.threadBuffers.add(new BlockBufferBuilderStorage());
		}

		this.renderThread = new ChunkRenderThread(this, new BlockBufferBuilderStorage());
	}

	public String getDebugString() {
		return String.format("pC: %03d, pU: %1d, aB: %1d", this.rebuildQueue.size(), this.uploadQueue.size(), this.threadBuffers.size());
	}

	public boolean upload(long timeout) {
		boolean bl = false;

		long l;
		do {
			boolean bl2 = false;
			synchronized (this.uploadQueue) {
				if (!this.uploadQueue.isEmpty()) {
					((ListenableFutureTask)this.uploadQueue.poll()).run();
					bl2 = true;
					bl = true;
				}
			}

			if (timeout == 0L || !bl2) {
				break;
			}

			l = timeout - System.nanoTime();
		} while (l >= 0L);

		return bl;
	}

	public boolean send(BuiltChunk chunk) {
		chunk.method_10166().lock();

		boolean var4;
		try {
			final net.minecraft.client.world.ChunkBuilder chunkBuilder = chunk.method_10167();
			chunkBuilder.method_10114(new Runnable() {
				public void run() {
					ChunkBuilder.this.rebuildQueue.remove(chunkBuilder);
				}
			});
			boolean bl = this.rebuildQueue.offer(chunkBuilder);
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

		while (this.upload(0L)) {
		}

		List<BlockBufferBuilderStorage> list = Lists.newArrayList();

		while (list.size() != 5) {
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
		return (net.minecraft.client.world.ChunkBuilder)this.rebuildQueue.take();
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
					ChunkBuilder.this.rebuildQueue.remove(chunkBuilder);
				}
			});
			var3 = this.rebuildQueue.offer(chunkBuilder);
		} finally {
			chunk.method_10166().unlock();
		}

		return var3;
	}

	public ListenableFuture<Object> upload(RenderLayer layer, BufferBuilder bufferBuilder, BuiltChunk chunk, ChunkAssemblyHelper chunkAssemblyHelper) {
		if (MinecraftClient.getInstance().isOnThread()) {
			if (GLX.supportsVbo()) {
				this.uploadVertexBuffer(bufferBuilder, chunk.method_10165(layer.ordinal()));
			} else {
				this.uploadGlList(bufferBuilder, ((ChunkRenderHelperImpl)chunk).method_10153(layer, chunkAssemblyHelper), chunk);
			}

			bufferBuilder.offset(0.0, 0.0, 0.0);
			return Futures.immediateFuture(null);
		} else {
			ListenableFutureTask<Object> listenableFutureTask = ListenableFutureTask.create(new Runnable() {
				public void run() {
					ChunkBuilder.this.upload(layer, bufferBuilder, chunk, chunkAssemblyHelper);
				}
			}, null);
			synchronized (this.uploadQueue) {
				this.uploadQueue.add(listenableFutureTask);
				return listenableFutureTask;
			}
		}
	}

	private void uploadGlList(BufferBuilder bufferBuilder, int id, BuiltChunk chunk) {
		GL11.glNewList(id, 4864);
		GlStateManager.pushMatrix();
		chunk.method_10169();
		this.bufferRenderer.draw(bufferBuilder);
		GlStateManager.popMatrix();
		GL11.glEndList();
	}

	private void uploadVertexBuffer(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
		this.vertexUploader.setBuffer(vertexBuffer);
		this.vertexUploader.draw(bufferBuilder);
	}

	public void clear() {
		while (!this.rebuildQueue.isEmpty()) {
			net.minecraft.client.world.ChunkBuilder chunkBuilder = (net.minecraft.client.world.ChunkBuilder)this.rebuildQueue.poll();
			if (chunkBuilder != null) {
				chunkBuilder.method_10118();
			}
		}
	}
}
