package net.minecraft.client.world;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.render.BlockBufferBuilderStorage;

public class ChunkBuilder implements Comparable<ChunkBuilder> {
	private final BuiltChunk builtChunk;
	private final ReentrantLock lock = new ReentrantLock();
	private final List<Runnable> tasks = Lists.newArrayList();
	private final ChunkBuilder.FunctionType functionType;
	private final double field_13606;
	private BlockBufferBuilderStorage renderLaterBuffers;
	private ChunkAssemblyHelper chunkAssemblyHelper;
	private ChunkBuilder.RenderStatus renderStatus = ChunkBuilder.RenderStatus.PENDING;
	private boolean field_11026;

	public ChunkBuilder(BuiltChunk builtChunk, ChunkBuilder.FunctionType functionType, double d) {
		this.builtChunk = builtChunk;
		this.functionType = functionType;
		this.field_13606 = d;
	}

	public ChunkBuilder.RenderStatus getRenderStatus() {
		return this.renderStatus;
	}

	public BuiltChunk getBuiltChunk() {
		return this.builtChunk;
	}

	public ChunkAssemblyHelper getChunkAssemblyHelper() {
		return this.chunkAssemblyHelper;
	}

	public void setChunkAssemblyHelper(ChunkAssemblyHelper chunkAssemblyHelper) {
		this.chunkAssemblyHelper = chunkAssemblyHelper;
	}

	public BlockBufferBuilderStorage getRenderLaterBuffers() {
		return this.renderLaterBuffers;
	}

	public void setRenderLayerBuffers(BlockBufferBuilderStorage renderLaterBuffers) {
		this.renderLaterBuffers = renderLaterBuffers;
	}

	public void setRenderStatus(ChunkBuilder.RenderStatus renderStatus) {
		this.lock.lock();

		try {
			this.renderStatus = renderStatus;
		} finally {
			this.lock.unlock();
		}
	}

	public void method_10118() {
		this.lock.lock();

		try {
			if (this.functionType == ChunkBuilder.FunctionType.REBUILD_CHUNK && this.renderStatus != ChunkBuilder.RenderStatus.DONE) {
				this.builtChunk.method_10162(false);
			}

			this.field_11026 = true;
			this.renderStatus = ChunkBuilder.RenderStatus.DONE;

			for (Runnable runnable : this.tasks) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public void method_10114(Runnable runnable) {
		this.lock.lock();

		try {
			this.tasks.add(runnable);
			if (this.field_11026) {
				runnable.run();
			}
		} finally {
			this.lock.unlock();
		}
	}

	public ReentrantLock getLock() {
		return this.lock;
	}

	public ChunkBuilder.FunctionType method_10120() {
		return this.functionType;
	}

	public boolean method_10121() {
		return this.field_11026;
	}

	public int compareTo(ChunkBuilder chunkBuilder) {
		return Doubles.compare(this.field_13606, chunkBuilder.field_13606);
	}

	public double method_12418() {
		return this.field_13606;
	}

	public static enum FunctionType {
		REBUILD_CHUNK,
		RESORT_TRANSPARENCY;
	}

	public static enum RenderStatus {
		PENDING,
		COMPILING,
		UPLOADING,
		DONE;
	}
}
