package net.minecraft.client.world;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.chunk.ChunkOcclusionData;
import net.minecraft.util.math.Direction;

public class ChunkAssemblyHelper {
	public static final ChunkAssemblyHelper UNSUPPORTED = new ChunkAssemblyHelper() {
		@Override
		protected void setUsed(RenderLayer layer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setUnused(RenderLayer layer) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isVisibleThrough(Direction dir1, Direction dir2) {
			return false;
		}
	};
	private final boolean[] usedLayers = new boolean[RenderLayer.values().length];
	private final boolean[] unusedLayers = new boolean[RenderLayer.values().length];
	private boolean field_11064 = true;
	private final List<BlockEntity> blockEntities = Lists.newArrayList();
	private ChunkOcclusionData chunkOcclusionData = new ChunkOcclusionData();
	private BufferBuilder.DrawArrayParameters drawArrayParameters;

	public boolean method_10142() {
		return this.field_11064;
	}

	protected void setUsed(RenderLayer layer) {
		this.field_11064 = false;
		this.usedLayers[layer.ordinal()] = true;
	}

	public boolean method_10149(RenderLayer layer) {
		return !this.usedLayers[layer.ordinal()];
	}

	public void setUnused(RenderLayer layer) {
		this.unusedLayers[layer.ordinal()] = true;
	}

	public boolean isUnused(RenderLayer layer) {
		return this.unusedLayers[layer.ordinal()];
	}

	public List<BlockEntity> getBlockEntities() {
		return this.blockEntities;
	}

	public void addBlockEntity(BlockEntity blockEntity) {
		this.blockEntities.add(blockEntity);
	}

	public boolean isVisibleThrough(Direction dir1, Direction dir2) {
		return this.chunkOcclusionData.isVisibleThrough(dir1, dir2);
	}

	public void setChunkOcclusionData(ChunkOcclusionData data) {
		this.chunkOcclusionData = data;
	}

	public BufferBuilder.DrawArrayParameters getDrawArrayParameters() {
		return this.drawArrayParameters;
	}

	public void setDrawArrayParameters(BufferBuilder.DrawArrayParameters parameters) {
		this.drawArrayParameters = parameters;
	}
}
