package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderThread implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final net.minecraft.client.render.chunk.ChunkBuilder chunkBuilder;
	private final BlockBufferBuilderStorage buffers;

	public ChunkRenderThread(net.minecraft.client.render.chunk.ChunkBuilder chunkBuilder) {
		this(chunkBuilder, null);
	}

	public ChunkRenderThread(net.minecraft.client.render.chunk.ChunkBuilder chunkBuilder, BlockBufferBuilderStorage blockBufferBuilderStorage) {
		this.chunkBuilder = chunkBuilder;
		this.buffers = blockBufferBuilderStorage;
	}

	public void run() {
		while (true) {
			try {
				this.method_10137(this.chunkBuilder.takeRebuildQueue());
			} catch (InterruptedException var3) {
				LOGGER.debug("Stopping due to interrupt");
				return;
			} catch (Throwable var4) {
				CrashReport crashReport = CrashReport.create(var4, "Batching chunks");
				MinecraftClient.getInstance().crash(MinecraftClient.getInstance().addSystemDetailsToCrashReport(crashReport));
				return;
			}
		}
	}

	protected void method_10137(ChunkBuilder chunkBuilder) throws InterruptedException {
		chunkBuilder.getLock().lock();

		try {
			if (chunkBuilder.getRenderStatus() != ChunkBuilder.RenderStatus.PENDING) {
				if (!chunkBuilder.method_10121()) {
					LOGGER.warn("Chunk render task was " + chunkBuilder.getRenderStatus() + " when I expected it to be pending; ignoring task");
				}

				return;
			}

			chunkBuilder.setRenderStatus(ChunkBuilder.RenderStatus.COMPILING);
		} finally {
			chunkBuilder.getLock().unlock();
		}

		Entity entity = MinecraftClient.getInstance().getCameraEntity();
		if (entity == null) {
			chunkBuilder.method_10118();
		} else {
			chunkBuilder.setRenderLayerBuffers(this.method_10139());
			float f = (float)entity.x;
			float g = (float)entity.y + entity.getEyeHeight();
			float h = (float)entity.z;
			ChunkBuilder.FunctionType functionType = chunkBuilder.method_10120();
			if (functionType == ChunkBuilder.FunctionType.REBUILD_CHUNK) {
				chunkBuilder.getBuiltChunk().method_10164(f, g, h, chunkBuilder);
			} else if (functionType == ChunkBuilder.FunctionType.RESORT_TRANSPARENCY) {
				chunkBuilder.getBuiltChunk().method_10155(f, g, h, chunkBuilder);
			}

			chunkBuilder.getLock().lock();

			try {
				if (chunkBuilder.getRenderStatus() != ChunkBuilder.RenderStatus.COMPILING) {
					if (!chunkBuilder.method_10121()) {
						LOGGER.warn("Chunk render task was " + chunkBuilder.getRenderStatus() + " when I expected it to be compiling; aborting task");
					}

					this.method_10140(chunkBuilder);
					return;
				}

				chunkBuilder.setRenderStatus(ChunkBuilder.RenderStatus.UPLOADING);
			} finally {
				chunkBuilder.getLock().unlock();
			}

			final ChunkAssemblyHelper chunkAssemblyHelper = chunkBuilder.getChunkAssemblyHelper();
			ArrayList list = Lists.newArrayList();
			if (functionType == ChunkBuilder.FunctionType.REBUILD_CHUNK) {
				for (RenderLayer renderLayer : RenderLayer.values()) {
					if (chunkAssemblyHelper.isUnused(renderLayer)) {
						list.add(this.chunkBuilder.upload(renderLayer, chunkBuilder.getRenderLaterBuffers().get(renderLayer), chunkBuilder.getBuiltChunk(), chunkAssemblyHelper));
					}
				}
			} else if (functionType == ChunkBuilder.FunctionType.RESORT_TRANSPARENCY) {
				list.add(
					this.chunkBuilder
						.upload(RenderLayer.TRANSLUCENT, chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT), chunkBuilder.getBuiltChunk(), chunkAssemblyHelper)
				);
			}

			final ListenableFuture<List<Object>> listenableFuture = Futures.allAsList(list);
			chunkBuilder.method_10114(new Runnable() {
				public void run() {
					listenableFuture.cancel(false);
				}
			});
			Futures.addCallback(listenableFuture, new FutureCallback<List<Object>>() {
				public void onSuccess(List<Object> list) {
					ChunkRenderThread.this.method_10140(chunkBuilder);
					chunkBuilder.getLock().lock();

					label44: {
						try {
							if (chunkBuilder.getRenderStatus() == ChunkBuilder.RenderStatus.UPLOADING) {
								chunkBuilder.setRenderStatus(ChunkBuilder.RenderStatus.DONE);
								break label44;
							}

							if (!chunkBuilder.method_10121()) {
								ChunkRenderThread.LOGGER.warn("Chunk render task was " + chunkBuilder.getRenderStatus() + " when I expected it to be uploading; aborting task");
							}
						} finally {
							chunkBuilder.getLock().unlock();
						}

						return;
					}

					chunkBuilder.getBuiltChunk().method_10159(chunkAssemblyHelper);
				}

				public void onFailure(Throwable throwable) {
					ChunkRenderThread.this.method_10140(chunkBuilder);
					if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
						MinecraftClient.getInstance().crash(CrashReport.create(throwable, "Rendering chunk"));
					}
				}
			});
		}
	}

	private BlockBufferBuilderStorage method_10139() throws InterruptedException {
		return this.buffers != null ? this.buffers : this.chunkBuilder.takeBuffer();
	}

	private void method_10140(ChunkBuilder chunkBuilder) {
		if (this.buffers == null) {
			this.chunkBuilder.addThreadBuffer(chunkBuilder.getRenderLaterBuffers());
		}
	}
}
