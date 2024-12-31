package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderThread implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger();
	private final net.minecraft.client.render.chunk.ChunkBuilder chunkBuilder;
	private final BlockBufferBuilderStorage buffers;
	private boolean field_13614 = true;

	public ChunkRenderThread(net.minecraft.client.render.chunk.ChunkBuilder chunkBuilder) {
		this(chunkBuilder, null);
	}

	public ChunkRenderThread(net.minecraft.client.render.chunk.ChunkBuilder chunkBuilder, @Nullable BlockBufferBuilderStorage blockBufferBuilderStorage) {
		this.chunkBuilder = chunkBuilder;
		this.buffers = blockBufferBuilderStorage;
	}

	public void run() {
		while (this.field_13614) {
			try {
				this.method_10137(this.chunkBuilder.takeRebuildQueue());
			} catch (InterruptedException var3) {
				LOGGER.debug("Stopping chunk worker due to interrupt");
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
					LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", chunkBuilder.getRenderStatus());
				}

				return;
			}

			BlockPos blockPos = new BlockPos(MinecraftClient.getInstance().player);
			BlockPos blockPos2 = chunkBuilder.getBuiltChunk().getPos();
			int i = 16;
			int j = 8;
			int k = 24;
			if (blockPos2.add(8, 8, 8).getSquaredDistance(blockPos) > 576.0) {
				World world = chunkBuilder.getBuiltChunk().getWorld();
				BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos2);
				if (!this.method_12426(mutable.set(blockPos2).move(Direction.WEST, 16), world)
					|| !this.method_12426(mutable.set(blockPos2).move(Direction.NORTH, 16), world)
					|| !this.method_12426(mutable.set(blockPos2).move(Direction.EAST, 16), world)
					|| !this.method_12426(mutable.set(blockPos2).move(Direction.SOUTH, 16), world)) {
					return;
				}
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
			Vec3d vec3d = Camera.getEntityPos(entity, 1.0);
			float f = (float)vec3d.x;
			float g = (float)vec3d.y;
			float h = (float)vec3d.z;
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
						LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", chunkBuilder.getRenderStatus());
					}

					this.method_10140(chunkBuilder);
					return;
				}

				chunkBuilder.setRenderStatus(ChunkBuilder.RenderStatus.UPLOADING);
			} finally {
				chunkBuilder.getLock().unlock();
			}

			final ChunkAssemblyHelper var26 = chunkBuilder.getChunkAssemblyHelper();
			ArrayList list = Lists.newArrayList();
			if (functionType == ChunkBuilder.FunctionType.REBUILD_CHUNK) {
				for (RenderLayer renderLayer : RenderLayer.values()) {
					if (var26.isUnused(renderLayer)) {
						list.add(
							this.chunkBuilder
								.method_12419(renderLayer, chunkBuilder.getRenderLaterBuffers().get(renderLayer), chunkBuilder.getBuiltChunk(), var26, chunkBuilder.method_12418())
						);
					}
				}
			} else if (functionType == ChunkBuilder.FunctionType.RESORT_TRANSPARENCY) {
				list.add(
					this.chunkBuilder
						.method_12419(
							RenderLayer.TRANSLUCENT,
							chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT),
							chunkBuilder.getBuiltChunk(),
							var26,
							chunkBuilder.method_12418()
						)
				);
			}

			ListenableFuture<List<Object>> listenableFuture = Futures.allAsList(list);
			chunkBuilder.method_10114(() -> listenableFuture.cancel(false));
			Futures.addCallback(listenableFuture, new FutureCallback<List<Object>>() {
				public void onSuccess(@Nullable List<Object> list) {
					ChunkRenderThread.this.method_10140(chunkBuilder);
					chunkBuilder.getLock().lock();

					label43: {
						try {
							if (chunkBuilder.getRenderStatus() == ChunkBuilder.RenderStatus.UPLOADING) {
								chunkBuilder.setRenderStatus(ChunkBuilder.RenderStatus.DONE);
								break label43;
							}

							if (!chunkBuilder.method_10121()) {
								ChunkRenderThread.LOGGER.warn("Chunk render task was {} when I expected it to be uploading; aborting task", chunkBuilder.getRenderStatus());
							}
						} finally {
							chunkBuilder.getLock().unlock();
						}

						return;
					}

					chunkBuilder.getBuiltChunk().method_10159(var26);
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

	private boolean method_12426(BlockPos blockPos, World world) {
		return !world.method_16347(blockPos.getX() >> 4, blockPos.getZ() >> 4).isEmpty();
	}

	private BlockBufferBuilderStorage method_10139() throws InterruptedException {
		return this.buffers != null ? this.buffers : this.chunkBuilder.takeBuffer();
	}

	private void method_10140(ChunkBuilder chunkBuilder) {
		if (this.buffers == null) {
			this.chunkBuilder.addThreadBuffer(chunkBuilder.getRenderLaterBuffers());
		}
	}

	public void method_12425() {
		this.field_13614 = false;
	}
}
