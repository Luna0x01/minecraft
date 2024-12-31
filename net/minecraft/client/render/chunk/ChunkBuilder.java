package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.TaskExecutor;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkBuilder {
	private static final Logger LOGGER = LogManager.getLogger();
	private final PriorityQueue<ChunkBuilder.BuiltChunk.Task> rebuildQueue = Queues.newPriorityQueue();
	private final Queue<BlockBufferBuilderStorage> threadBuffers;
	private final Queue<Runnable> uploadQueue = Queues.newConcurrentLinkedQueue();
	private volatile int queuedTaskCount;
	private volatile int bufferCount;
	private final BlockBufferBuilderStorage buffers;
	private final TaskExecutor<Runnable> mailbox;
	private final Executor executor;
	private World world;
	private final WorldRenderer worldRenderer;
	private Vec3d cameraPosition = Vec3d.ZERO;

	public ChunkBuilder(World world, WorldRenderer worldRenderer, Executor executor, boolean bl, BlockBufferBuilderStorage blockBufferBuilderStorage) {
		this.world = world;
		this.worldRenderer = worldRenderer;
		int i = Math.max(
			1,
			(int)((double)Runtime.getRuntime().maxMemory() * 0.3) / (RenderLayer.getBlockLayers().stream().mapToInt(RenderLayer::getExpectedBufferSize).sum() * 4) - 1
		);
		int j = Runtime.getRuntime().availableProcessors();
		int k = bl ? j : Math.min(j, 4);
		int l = Math.max(1, Math.min(k, i));
		this.buffers = blockBufferBuilderStorage;
		List<BlockBufferBuilderStorage> list = Lists.newArrayListWithExpectedSize(l);

		try {
			for (int m = 0; m < l; m++) {
				list.add(new BlockBufferBuilderStorage());
			}
		} catch (OutOfMemoryError var14) {
			LOGGER.warn("Allocated only {}/{} buffers", list.size(), l);
			int n = Math.min(list.size() * 2 / 3, list.size() - 1);

			for (int o = 0; o < n; o++) {
				list.remove(list.size() - 1);
			}

			System.gc();
		}

		this.threadBuffers = Queues.newArrayDeque(list);
		this.bufferCount = this.threadBuffers.size();
		this.executor = executor;
		this.mailbox = TaskExecutor.create(executor, "Chunk Renderer");
		this.mailbox.send(this::scheduleRunTasks);
	}

	public void setWorld(World world) {
		this.world = world;
	}

	private void scheduleRunTasks() {
		if (!this.threadBuffers.isEmpty()) {
			ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)this.rebuildQueue.poll();
			if (task != null) {
				BlockBufferBuilderStorage blockBufferBuilderStorage = (BlockBufferBuilderStorage)this.threadBuffers.poll();
				this.queuedTaskCount = this.rebuildQueue.size();
				this.bufferCount = this.threadBuffers.size();
				CompletableFuture.runAsync(() -> {
				}, this.executor).thenCompose(void_ -> task.run(blockBufferBuilderStorage)).whenComplete((result, throwable) -> {
					if (throwable != null) {
						CrashReport crashReport = CrashReport.create(throwable, "Batching chunks");
						MinecraftClient.getInstance().setCrashReport(MinecraftClient.getInstance().addDetailsToCrashReport(crashReport));
					} else {
						this.mailbox.send(() -> {
							if (result == ChunkBuilder.Result.field_21438) {
								blockBufferBuilderStorage.clear();
							} else {
								blockBufferBuilderStorage.reset();
							}

							this.threadBuffers.add(blockBufferBuilderStorage);
							this.bufferCount = this.threadBuffers.size();
							this.scheduleRunTasks();
						});
					}
				});
			}
		}
	}

	public String getDebugString() {
		return String.format("pC: %03d, pU: %02d, aB: %02d", this.queuedTaskCount, this.uploadQueue.size(), this.bufferCount);
	}

	public void setCameraPosition(Vec3d vec3d) {
		this.cameraPosition = vec3d;
	}

	public Vec3d getCameraPosition() {
		return this.cameraPosition;
	}

	public boolean upload() {
		boolean bl;
		Runnable runnable;
		for (bl = false; (runnable = (Runnable)this.uploadQueue.poll()) != null; bl = true) {
			runnable.run();
		}

		return bl;
	}

	public void rebuild(ChunkBuilder.BuiltChunk builtChunk) {
		builtChunk.rebuild();
	}

	public void reset() {
		this.clear();
	}

	public void send(ChunkBuilder.BuiltChunk.Task task) {
		this.mailbox.send(() -> {
			this.rebuildQueue.offer(task);
			this.queuedTaskCount = this.rebuildQueue.size();
			this.scheduleRunTasks();
		});
	}

	public CompletableFuture<Void> scheduleUpload(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
		return CompletableFuture.runAsync(() -> {
		}, this.uploadQueue::add).thenCompose(void_ -> this.upload(bufferBuilder, vertexBuffer));
	}

	private CompletableFuture<Void> upload(BufferBuilder bufferBuilder, VertexBuffer vertexBuffer) {
		return vertexBuffer.submitUpload(bufferBuilder);
	}

	private void clear() {
		while (!this.rebuildQueue.isEmpty()) {
			ChunkBuilder.BuiltChunk.Task task = (ChunkBuilder.BuiltChunk.Task)this.rebuildQueue.poll();
			if (task != null) {
				task.cancel();
			}
		}

		this.queuedTaskCount = 0;
	}

	public boolean isEmpty() {
		return this.queuedTaskCount == 0 && this.uploadQueue.isEmpty();
	}

	public void stop() {
		this.clear();
		this.mailbox.close();
		this.threadBuffers.clear();
	}

	public class BuiltChunk {
		public final AtomicReference<ChunkBuilder.ChunkData> data = new AtomicReference(ChunkBuilder.ChunkData.EMPTY);
		@Nullable
		private ChunkBuilder.BuiltChunk.RebuildTask rebuildTask;
		@Nullable
		private ChunkBuilder.BuiltChunk.SortTask sortTask;
		private final Set<BlockEntity> blockEntities = Sets.newHashSet();
		private final Map<RenderLayer, VertexBuffer> buffers = (Map<RenderLayer, VertexBuffer>)RenderLayer.getBlockLayers()
			.stream()
			.collect(Collectors.toMap(renderLayer -> renderLayer, renderLayer -> new VertexBuffer(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL)));
		public Box boundingBox;
		private int rebuildFrame = -1;
		private boolean needsRebuild = true;
		private final BlockPos.Mutable origin = new BlockPos.Mutable(-1, -1, -1);
		private final BlockPos.Mutable[] neighborPositions = Util.make(new BlockPos.Mutable[6], mutables -> {
			for (int i = 0; i < mutables.length; i++) {
				mutables[i] = new BlockPos.Mutable();
			}
		});
		private boolean needsImportantRebuild;

		private boolean isChunkNonEmpty(BlockPos blockPos) {
			return ChunkBuilder.this.world.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.field_12803, false) != null;
		}

		public boolean shouldBuild() {
			int i = 24;
			return !(this.getSquaredCameraDistance() > 576.0)
				? true
				: this.isChunkNonEmpty(this.neighborPositions[Direction.field_11039.ordinal()])
					&& this.isChunkNonEmpty(this.neighborPositions[Direction.field_11043.ordinal()])
					&& this.isChunkNonEmpty(this.neighborPositions[Direction.field_11034.ordinal()])
					&& this.isChunkNonEmpty(this.neighborPositions[Direction.field_11035.ordinal()]);
		}

		public boolean setRebuildFrame(int i) {
			if (this.rebuildFrame == i) {
				return false;
			} else {
				this.rebuildFrame = i;
				return true;
			}
		}

		public VertexBuffer getBuffer(RenderLayer renderLayer) {
			return (VertexBuffer)this.buffers.get(renderLayer);
		}

		public void setOrigin(int i, int j, int k) {
			if (i != this.origin.getX() || j != this.origin.getY() || k != this.origin.getZ()) {
				this.clear();
				this.origin.set(i, j, k);
				this.boundingBox = new Box((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));

				for (Direction direction : Direction.values()) {
					this.neighborPositions[direction.ordinal()].set(this.origin).setOffset(direction, 16);
				}
			}
		}

		protected double getSquaredCameraDistance() {
			Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
			double d = this.boundingBox.x1 + 8.0 - camera.getPos().x;
			double e = this.boundingBox.y1 + 8.0 - camera.getPos().y;
			double f = this.boundingBox.z1 + 8.0 - camera.getPos().z;
			return d * d + e * e + f * f;
		}

		private void beginBufferBuilding(BufferBuilder bufferBuilder) {
			bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		}

		public ChunkBuilder.ChunkData getData() {
			return (ChunkBuilder.ChunkData)this.data.get();
		}

		private void clear() {
			this.cancel();
			this.data.set(ChunkBuilder.ChunkData.EMPTY);
			this.needsRebuild = true;
		}

		public void delete() {
			this.clear();
			this.buffers.values().forEach(VertexBuffer::close);
		}

		public BlockPos getOrigin() {
			return this.origin;
		}

		public void scheduleRebuild(boolean bl) {
			boolean bl2 = this.needsRebuild;
			this.needsRebuild = true;
			this.needsImportantRebuild = bl | (bl2 && this.needsImportantRebuild);
		}

		public void cancelRebuild() {
			this.needsRebuild = false;
			this.needsImportantRebuild = false;
		}

		public boolean needsRebuild() {
			return this.needsRebuild;
		}

		public boolean needsImportantRebuild() {
			return this.needsRebuild && this.needsImportantRebuild;
		}

		public BlockPos getNeighborPosition(Direction direction) {
			return this.neighborPositions[direction.ordinal()];
		}

		public boolean scheduleSort(RenderLayer renderLayer, ChunkBuilder chunkBuilder) {
			ChunkBuilder.ChunkData chunkData = this.getData();
			if (this.sortTask != null) {
				this.sortTask.cancel();
			}

			if (!chunkData.initializedLayers.contains(renderLayer)) {
				return false;
			} else {
				this.sortTask = new ChunkBuilder.BuiltChunk.SortTask(this.getSquaredCameraDistance(), chunkData);
				chunkBuilder.send(this.sortTask);
				return true;
			}
		}

		protected void cancel() {
			if (this.rebuildTask != null) {
				this.rebuildTask.cancel();
				this.rebuildTask = null;
			}

			if (this.sortTask != null) {
				this.sortTask.cancel();
				this.sortTask = null;
			}
		}

		public ChunkBuilder.BuiltChunk.Task createRebuildTask() {
			this.cancel();
			BlockPos blockPos = this.origin.toImmutable();
			int i = 1;
			ChunkRendererRegion chunkRendererRegion = ChunkRendererRegion.create(ChunkBuilder.this.world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			this.rebuildTask = new ChunkBuilder.BuiltChunk.RebuildTask(this.getSquaredCameraDistance(), chunkRendererRegion);
			return this.rebuildTask;
		}

		public void scheduleRebuild(ChunkBuilder chunkBuilder) {
			ChunkBuilder.BuiltChunk.Task task = this.createRebuildTask();
			chunkBuilder.send(task);
		}

		private void setNoCullingBlockEntities(Set<BlockEntity> set) {
			Set<BlockEntity> set2 = Sets.newHashSet(set);
			Set<BlockEntity> set3 = Sets.newHashSet(this.blockEntities);
			set2.removeAll(this.blockEntities);
			set3.removeAll(set);
			this.blockEntities.clear();
			this.blockEntities.addAll(set);
			ChunkBuilder.this.worldRenderer.updateNoCullingBlockEntities(set3, set2);
		}

		public void rebuild() {
			ChunkBuilder.BuiltChunk.Task task = this.createRebuildTask();
			task.run(ChunkBuilder.this.buffers);
		}

		class RebuildTask extends ChunkBuilder.BuiltChunk.Task {
			@Nullable
			protected ChunkRendererRegion region;

			public RebuildTask(double d, ChunkRendererRegion chunkRendererRegion) {
				super(d);
				this.region = chunkRendererRegion;
			}

			@Override
			public CompletableFuture<ChunkBuilder.Result> run(BlockBufferBuilderStorage blockBufferBuilderStorage) {
				if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
				} else if (!BuiltChunk.this.shouldBuild()) {
					this.region = null;
					BuiltChunk.this.scheduleRebuild(false);
					this.cancelled.set(true);
					return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
				} else if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
				} else {
					Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					ChunkBuilder.ChunkData chunkData = new ChunkBuilder.ChunkData();
					Set<BlockEntity> set = this.render(f, g, h, chunkData, blockBufferBuilderStorage);
					BuiltChunk.this.setNoCullingBlockEntities(set);
					if (this.cancelled.get()) {
						return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
					} else {
						List<CompletableFuture<Void>> list = Lists.newArrayList();
						chunkData.initializedLayers
							.forEach(renderLayer -> list.add(ChunkBuilder.this.scheduleUpload(blockBufferBuilderStorage.get(renderLayer), BuiltChunk.this.getBuffer(renderLayer))));
						return Util.combine(list).handle((listx, throwable) -> {
							if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
								MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
							}

							if (this.cancelled.get()) {
								return ChunkBuilder.Result.field_21439;
							} else {
								BuiltChunk.this.data.set(chunkData);
								return ChunkBuilder.Result.field_21438;
							}
						});
					}
				}
			}

			private Set<BlockEntity> render(float f, float g, float h, ChunkBuilder.ChunkData chunkData, BlockBufferBuilderStorage blockBufferBuilderStorage) {
				int i = 1;
				BlockPos blockPos = BuiltChunk.this.origin.toImmutable();
				BlockPos blockPos2 = blockPos.add(15, 15, 15);
				ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
				Set<BlockEntity> set = Sets.newHashSet();
				ChunkRendererRegion chunkRendererRegion = this.region;
				this.region = null;
				MatrixStack matrixStack = new MatrixStack();
				if (chunkRendererRegion != null) {
					BlockModelRenderer.enableBrightnessCache();
					Random random = new Random();
					BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

					for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
						BlockState blockState = chunkRendererRegion.getBlockState(blockPos3);
						Block block = blockState.getBlock();
						if (blockState.isFullOpaque(chunkRendererRegion, blockPos3)) {
							chunkOcclusionDataBuilder.markClosed(blockPos3);
						}

						if (block.hasBlockEntity()) {
							BlockEntity blockEntity = chunkRendererRegion.getBlockEntity(blockPos3, WorldChunk.CreationType.field_12859);
							if (blockEntity != null) {
								this.addBlockEntity(chunkData, set, blockEntity);
							}
						}

						FluidState fluidState = chunkRendererRegion.getFluidState(blockPos3);
						if (!fluidState.isEmpty()) {
							RenderLayer renderLayer = RenderLayers.getFluidLayer(fluidState);
							BufferBuilder bufferBuilder = blockBufferBuilderStorage.get(renderLayer);
							if (chunkData.initializedLayers.add(renderLayer)) {
								BuiltChunk.this.beginBufferBuilding(bufferBuilder);
							}

							if (blockRenderManager.renderFluid(blockPos3, chunkRendererRegion, bufferBuilder, fluidState)) {
								chunkData.empty = false;
								chunkData.nonEmptyLayers.add(renderLayer);
							}
						}

						if (blockState.getRenderType() != BlockRenderType.field_11455) {
							RenderLayer renderLayer2 = RenderLayers.getBlockLayer(blockState);
							BufferBuilder bufferBuilder2 = blockBufferBuilderStorage.get(renderLayer2);
							if (chunkData.initializedLayers.add(renderLayer2)) {
								BuiltChunk.this.beginBufferBuilding(bufferBuilder2);
							}

							matrixStack.push();
							matrixStack.translate((double)(blockPos3.getX() & 15), (double)(blockPos3.getY() & 15), (double)(blockPos3.getZ() & 15));
							if (blockRenderManager.renderBlock(blockState, blockPos3, chunkRendererRegion, matrixStack, bufferBuilder2, true, random)) {
								chunkData.empty = false;
								chunkData.nonEmptyLayers.add(renderLayer2);
							}

							matrixStack.pop();
						}
					}

					if (chunkData.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
						BufferBuilder bufferBuilder3 = blockBufferBuilderStorage.get(RenderLayer.getTranslucent());
						bufferBuilder3.sortQuads(f - (float)blockPos.getX(), g - (float)blockPos.getY(), h - (float)blockPos.getZ());
						chunkData.bufferState = bufferBuilder3.popState();
					}

					chunkData.initializedLayers.stream().map(blockBufferBuilderStorage::get).forEach(BufferBuilder::end);
					BlockModelRenderer.disableBrightnessCache();
				}

				chunkData.occlusionGraph = chunkOcclusionDataBuilder.build();
				return set;
			}

			private <E extends BlockEntity> void addBlockEntity(ChunkBuilder.ChunkData chunkData, Set<BlockEntity> set, E blockEntity) {
				BlockEntityRenderer<E> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.get(blockEntity);
				if (blockEntityRenderer != null) {
					chunkData.blockEntities.add(blockEntity);
					if (blockEntityRenderer.rendersOutsideBoundingBox(blockEntity)) {
						set.add(blockEntity);
					}
				}
			}

			@Override
			public void cancel() {
				this.region = null;
				if (this.cancelled.compareAndSet(false, true)) {
					BuiltChunk.this.scheduleRebuild(false);
				}
			}
		}

		class SortTask extends ChunkBuilder.BuiltChunk.Task {
			private final ChunkBuilder.ChunkData data;

			public SortTask(double d, ChunkBuilder.ChunkData chunkData) {
				super(d);
				this.data = chunkData;
			}

			@Override
			public CompletableFuture<ChunkBuilder.Result> run(BlockBufferBuilderStorage blockBufferBuilderStorage) {
				if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
				} else if (!BuiltChunk.this.shouldBuild()) {
					this.cancelled.set(true);
					return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
				} else if (this.cancelled.get()) {
					return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
				} else {
					Vec3d vec3d = ChunkBuilder.this.getCameraPosition();
					float f = (float)vec3d.x;
					float g = (float)vec3d.y;
					float h = (float)vec3d.z;
					BufferBuilder.State state = this.data.bufferState;
					if (state != null && this.data.nonEmptyLayers.contains(RenderLayer.getTranslucent())) {
						BufferBuilder bufferBuilder = blockBufferBuilderStorage.get(RenderLayer.getTranslucent());
						BuiltChunk.this.beginBufferBuilding(bufferBuilder);
						bufferBuilder.restoreState(state);
						bufferBuilder.sortQuads(f - (float)BuiltChunk.this.origin.getX(), g - (float)BuiltChunk.this.origin.getY(), h - (float)BuiltChunk.this.origin.getZ());
						this.data.bufferState = bufferBuilder.popState();
						bufferBuilder.end();
						if (this.cancelled.get()) {
							return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
						} else {
							CompletableFuture<ChunkBuilder.Result> completableFuture = ChunkBuilder.this.scheduleUpload(
									blockBufferBuilderStorage.get(RenderLayer.getTranslucent()), BuiltChunk.this.getBuffer(RenderLayer.getTranslucent())
								)
								.thenApply(void_ -> ChunkBuilder.Result.field_21439);
							return completableFuture.handle((result, throwable) -> {
								if (throwable != null && !(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
									MinecraftClient.getInstance().setCrashReport(CrashReport.create(throwable, "Rendering chunk"));
								}

								return this.cancelled.get() ? ChunkBuilder.Result.field_21439 : ChunkBuilder.Result.field_21438;
							});
						}
					} else {
						return CompletableFuture.completedFuture(ChunkBuilder.Result.field_21439);
					}
				}
			}

			@Override
			public void cancel() {
				this.cancelled.set(true);
			}
		}

		abstract class Task implements Comparable<ChunkBuilder.BuiltChunk.Task> {
			protected final double distance;
			protected final AtomicBoolean cancelled = new AtomicBoolean(false);

			public Task(double d) {
				this.distance = d;
			}

			public abstract CompletableFuture<ChunkBuilder.Result> run(BlockBufferBuilderStorage blockBufferBuilderStorage);

			public abstract void cancel();

			public int compareTo(ChunkBuilder.BuiltChunk.Task task) {
				return Doubles.compare(this.distance, task.distance);
			}
		}
	}

	public static class ChunkData {
		public static final ChunkBuilder.ChunkData EMPTY = new ChunkBuilder.ChunkData() {
			@Override
			public boolean isVisibleThrough(Direction direction, Direction direction2) {
				return false;
			}
		};
		private final Set<RenderLayer> nonEmptyLayers = new ObjectArraySet();
		private final Set<RenderLayer> initializedLayers = new ObjectArraySet();
		private boolean empty = true;
		private final List<BlockEntity> blockEntities = Lists.newArrayList();
		private ChunkOcclusionData occlusionGraph = new ChunkOcclusionData();
		@Nullable
		private BufferBuilder.State bufferState;

		public boolean isEmpty() {
			return this.empty;
		}

		public boolean isEmpty(RenderLayer renderLayer) {
			return !this.nonEmptyLayers.contains(renderLayer);
		}

		public List<BlockEntity> getBlockEntities() {
			return this.blockEntities;
		}

		public boolean isVisibleThrough(Direction direction, Direction direction2) {
			return this.occlusionGraph.isVisibleThrough(direction, direction2);
		}
	}

	static enum Result {
		field_21438,
		field_21439;
	}
}
