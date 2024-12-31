package net.minecraft.client.render.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Direction;

public class ChunkRenderData {
	public static final ChunkRenderData EMPTY = new ChunkRenderData() {
		@Override
		protected void setNonEmpty(BlockRenderLayer blockRenderLayer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void markBufferInitialized(BlockRenderLayer blockRenderLayer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isVisibleThrough(Direction direction, Direction direction2) {
			return false;
		}
	};
	private final boolean[] nonEmpty = new boolean[BlockRenderLayer.values().length];
	private final boolean[] initialized = new boolean[BlockRenderLayer.values().length];
	private boolean empty = true;
	private final List<BlockEntity> blockEntities = Lists.newArrayList();
	private ChunkOcclusionGraph occlusionGraph = new ChunkOcclusionGraph();
	private BufferBuilder.State bufferState;

	public boolean isEmpty() {
		return this.empty;
	}

	protected void setNonEmpty(BlockRenderLayer blockRenderLayer) {
		this.empty = false;
		this.nonEmpty[blockRenderLayer.ordinal()] = true;
	}

	public boolean isEmpty(BlockRenderLayer blockRenderLayer) {
		return !this.nonEmpty[blockRenderLayer.ordinal()];
	}

	public void markBufferInitialized(BlockRenderLayer blockRenderLayer) {
		this.initialized[blockRenderLayer.ordinal()] = true;
	}

	public boolean isBufferInitialized(BlockRenderLayer blockRenderLayer) {
		return this.initialized[blockRenderLayer.ordinal()];
	}

	public List<BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.blockEntities.add(blockEntity);
	}

	public boolean isVisibleThrough(Direction direction, Direction direction2) {
		return this.occlusionGraph.isVisibleThrough(direction, direction2);
	}

	public void setOcclusionGraph(ChunkOcclusionGraph chunkOcclusionGraph) {
		this.occlusionGraph = chunkOcclusionGraph;
	}

	public BufferBuilder.State getBufferState() {
		return this.bufferState;
	}

	public void setBufferState(BufferBuilder.State state) {
		this.bufferState = state;
	}
}
