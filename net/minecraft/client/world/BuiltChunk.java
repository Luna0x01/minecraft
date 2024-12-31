package net.minecraft.client.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BuiltChunk {
	private World world;
	private final WorldRenderer renderer;
	public static int chunkUpdates;
	private BlockPos pos;
	public ChunkAssemblyHelper field_11070 = ChunkAssemblyHelper.UNSUPPORTED;
	private final ReentrantLock field_11075 = new ReentrantLock();
	private final ReentrantLock field_11076 = new ReentrantLock();
	private ChunkBuilder field_11077 = null;
	private final Set<BlockEntity> field_11078 = Sets.newHashSet();
	private final int field_11079;
	private final FloatBuffer field_11080 = GlAllocationUtils.allocateFloatBuffer(16);
	private final VertexBuffer[] field_11081 = new VertexBuffer[RenderLayer.values().length];
	public Box field_11071;
	private int field_11082 = -1;
	private boolean field_11083 = true;
	private EnumMap<Direction, BlockPos> field_11084 = Maps.newEnumMap(Direction.class);

	public BuiltChunk(World world, WorldRenderer worldRenderer, BlockPos blockPos, int i) {
		this.world = world;
		this.renderer = worldRenderer;
		this.field_11079 = i;
		if (!blockPos.equals(this.getPos())) {
			this.method_10160(blockPos);
		}

		if (GLX.supportsVbo()) {
			for (int j = 0; j < RenderLayer.values().length; j++) {
				this.field_11081[j] = new VertexBuffer(VertexFormats.BLOCK);
			}
		}
	}

	public boolean method_10156(int i) {
		if (this.field_11082 == i) {
			return false;
		} else {
			this.field_11082 = i;
			return true;
		}
	}

	public VertexBuffer method_10165(int i) {
		return this.field_11081[i];
	}

	public void method_10160(BlockPos blockPos) {
		this.method_10171();
		this.pos = blockPos;
		this.field_11071 = new Box(blockPos, blockPos.add(16, 16, 16));

		for (Direction direction : Direction.values()) {
			this.field_11084.put(direction, blockPos.offset(direction, 16));
		}

		this.method_10174();
	}

	public void method_10155(float f, float g, float h, ChunkBuilder chunkBuilder) {
		ChunkAssemblyHelper chunkAssemblyHelper = chunkBuilder.getChunkAssemblyHelper();
		if (chunkAssemblyHelper.getDrawArrayParameters() != null && !chunkAssemblyHelper.method_10149(RenderLayer.TRANSLUCENT)) {
			this.method_10158(chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT), this.pos);
			chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT).restoreState(chunkAssemblyHelper.getDrawArrayParameters());
			this.method_10157(RenderLayer.TRANSLUCENT, f, g, h, chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT), chunkAssemblyHelper);
		}
	}

	public void method_10164(float f, float g, float h, ChunkBuilder chunkBuilder) {
		ChunkAssemblyHelper chunkAssemblyHelper = new ChunkAssemblyHelper();
		int i = 1;
		BlockPos blockPos = this.pos;
		BlockPos blockPos2 = blockPos.add(15, 15, 15);
		chunkBuilder.getLock().lock();

		BlockView blockView;
		try {
			if (chunkBuilder.getRenderStatus() != ChunkBuilder.RenderStatus.COMPILING) {
				return;
			}

			blockView = new ChunkRenderCache(this.world, blockPos.add(-1, -1, -1), blockPos2.add(1, 1, 1), 1);
			chunkBuilder.setChunkAssemblyHelper(chunkAssemblyHelper);
		} finally {
			chunkBuilder.getLock().unlock();
		}

		ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
		HashSet set = Sets.newHashSet();
		if (!blockView.isEmpty()) {
			chunkUpdates++;
			boolean[] bls = new boolean[RenderLayer.values().length];
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

			for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos, blockPos2)) {
				BlockState blockState = blockView.getBlockState(mutable);
				Block block = blockState.getBlock();
				if (block.hasTransparency()) {
					chunkOcclusionDataBuilder.markClosed(mutable);
				}

				if (block.hasBlockEntity()) {
					BlockEntity blockEntity = blockView.getBlockEntity(new BlockPos(mutable));
					BlockEntityRenderer<BlockEntity> blockEntityRenderer = BlockEntityRenderDispatcher.INSTANCE.getRenderer(blockEntity);
					if (blockEntity != null && blockEntityRenderer != null) {
						chunkAssemblyHelper.addBlockEntity(blockEntity);
						if (blockEntityRenderer.rendersOutsideBoundingBox()) {
							set.add(blockEntity);
						}
					}
				}

				RenderLayer renderLayer = block.getRenderLayerType();
				int j = renderLayer.ordinal();
				if (block.getBlockType() != -1) {
					BufferBuilder bufferBuilder = chunkBuilder.getRenderLaterBuffers().get(j);
					if (!chunkAssemblyHelper.isUnused(renderLayer)) {
						chunkAssemblyHelper.setUnused(renderLayer);
						this.method_10158(bufferBuilder, blockPos);
					}

					bls[j] |= blockRenderManager.renderBlock(blockState, mutable, blockView, bufferBuilder);
				}
			}

			for (RenderLayer renderLayer2 : RenderLayer.values()) {
				if (bls[renderLayer2.ordinal()]) {
					chunkAssemblyHelper.setUsed(renderLayer2);
				}

				if (chunkAssemblyHelper.isUnused(renderLayer2)) {
					this.method_10157(renderLayer2, f, g, h, chunkBuilder.getRenderLaterBuffers().get(renderLayer2), chunkAssemblyHelper);
				}
			}
		}

		chunkAssemblyHelper.setChunkOcclusionData(chunkOcclusionDataBuilder.build());
		this.field_11075.lock();

		try {
			Set<BlockEntity> set2 = Sets.newHashSet(set);
			Set<BlockEntity> set3 = Sets.newHashSet(this.field_11078);
			set2.removeAll(this.field_11078);
			set3.removeAll(set);
			this.field_11078.clear();
			this.field_11078.addAll(set);
			this.renderer.updateNoCullingBlockEntities(set3, set2);
		} finally {
			this.field_11075.unlock();
		}
	}

	protected void method_10163() {
		this.field_11075.lock();

		try {
			if (this.field_11077 != null && this.field_11077.getRenderStatus() != ChunkBuilder.RenderStatus.DONE) {
				this.field_11077.method_10118();
				this.field_11077 = null;
			}
		} finally {
			this.field_11075.unlock();
		}
	}

	public ReentrantLock method_10166() {
		return this.field_11075;
	}

	public ChunkBuilder method_10167() {
		this.field_11075.lock();

		ChunkBuilder var1;
		try {
			this.method_10163();
			this.field_11077 = new ChunkBuilder(this, ChunkBuilder.FunctionType.REBUILD_CHUNK);
			var1 = this.field_11077;
		} finally {
			this.field_11075.unlock();
		}

		return var1;
	}

	public ChunkBuilder method_10168() {
		this.field_11075.lock();

		Object var1;
		try {
			if (this.field_11077 == null || this.field_11077.getRenderStatus() != ChunkBuilder.RenderStatus.PENDING) {
				if (this.field_11077 != null && this.field_11077.getRenderStatus() != ChunkBuilder.RenderStatus.DONE) {
					this.field_11077.method_10118();
					this.field_11077 = null;
				}

				this.field_11077 = new ChunkBuilder(this, ChunkBuilder.FunctionType.RESORT_TRANSPARENCY);
				this.field_11077.setChunkAssemblyHelper(this.field_11070);
				return this.field_11077;
			}

			var1 = null;
		} finally {
			this.field_11075.unlock();
		}

		return (ChunkBuilder)var1;
	}

	private void method_10158(BufferBuilder bufferBuilder, BlockPos blockPos) {
		bufferBuilder.begin(7, VertexFormats.BLOCK);
		bufferBuilder.offset((double)(-blockPos.getX()), (double)(-blockPos.getY()), (double)(-blockPos.getZ()));
	}

	private void method_10157(RenderLayer renderLayer, float f, float g, float h, BufferBuilder bufferBuilder, ChunkAssemblyHelper chunkAssemblyHelper) {
		if (renderLayer == RenderLayer.TRANSLUCENT && !chunkAssemblyHelper.method_10149(renderLayer)) {
			bufferBuilder.sortQuads(f, g, h);
			chunkAssemblyHelper.setDrawArrayParameters(bufferBuilder.method_9727());
		}

		bufferBuilder.end();
	}

	private void method_10174() {
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		float f = 1.000001F;
		GlStateManager.translate(-8.0F, -8.0F, -8.0F);
		GlStateManager.scale(f, f, f);
		GlStateManager.translate(8.0F, 8.0F, 8.0F);
		GlStateManager.getFloat(2982, this.field_11080);
		GlStateManager.popMatrix();
	}

	public void method_10169() {
		GlStateManager.multiMatrix(this.field_11080);
	}

	public ChunkAssemblyHelper method_10170() {
		return this.field_11070;
	}

	public void method_10159(ChunkAssemblyHelper chunkAssemblyHelper) {
		this.field_11076.lock();

		try {
			this.field_11070 = chunkAssemblyHelper;
		} finally {
			this.field_11076.unlock();
		}
	}

	public void method_10171() {
		this.method_10163();
		this.field_11070 = ChunkAssemblyHelper.UNSUPPORTED;
	}

	public void delete() {
		this.method_10171();
		this.world = null;

		for (int i = 0; i < RenderLayer.values().length; i++) {
			if (this.field_11081[i] != null) {
				this.field_11081[i].delete();
			}
		}
	}

	public BlockPos getPos() {
		return this.pos;
	}

	public void method_10162(boolean bl) {
		this.field_11083 = bl;
	}

	public boolean method_10173() {
		return this.field_11083;
	}

	public BlockPos method_10161(Direction direction) {
		return (BlockPos)this.field_11084.get(direction);
	}
}
