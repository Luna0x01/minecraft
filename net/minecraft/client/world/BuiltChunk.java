package net.minecraft.client.world;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.class_4239;
import net.minecraft.class_4245;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BuiltChunk {
	private volatile World world;
	private final WorldRenderer renderer;
	public static int chunkUpdates;
	public ChunkAssemblyHelper field_11070 = ChunkAssemblyHelper.UNSUPPORTED;
	private final ReentrantLock field_11075 = new ReentrantLock();
	private final ReentrantLock field_11076 = new ReentrantLock();
	private ChunkBuilder field_11077;
	private final Set<BlockEntity> field_11078 = Sets.newHashSet();
	private final FloatBuffer field_11080 = GlAllocationUtils.allocateFloatBuffer(16);
	private final VertexBuffer[] field_11081 = new VertexBuffer[RenderLayer.values().length];
	public Box field_13615;
	private int field_11082 = -1;
	private boolean field_11083 = true;
	private final BlockPos.Mutable position = new BlockPos.Mutable(-1, -1, -1);
	private final BlockPos.Mutable[] field_13617 = Util.make(new BlockPos.Mutable[6], mutables -> {
		for (int ix = 0; ix < mutables.length; ix++) {
			mutables[ix] = new BlockPos.Mutable();
		}
	});
	private boolean field_13618;

	public BuiltChunk(World world, WorldRenderer worldRenderer) {
		this.world = world;
		this.renderer = worldRenderer;
		if (GLX.supportsVbo()) {
			for (int i = 0; i < RenderLayer.values().length; i++) {
				this.field_11081[i] = new VertexBuffer(VertexFormats.BLOCK);
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

	public void method_12427(int i, int j, int k) {
		if (i != this.position.getX() || j != this.position.getY() || k != this.position.getZ()) {
			this.method_10171();
			this.position.setPosition(i, j, k);
			this.field_13615 = new Box((double)i, (double)j, (double)k, (double)(i + 16), (double)(j + 16), (double)(k + 16));

			for (Direction direction : Direction.values()) {
				this.field_13617[direction.ordinal()].set(this.position).move(direction, 16);
			}

			this.method_10174();
		}
	}

	public void method_10155(float f, float g, float h, ChunkBuilder chunkBuilder) {
		ChunkAssemblyHelper chunkAssemblyHelper = chunkBuilder.getChunkAssemblyHelper();
		if (chunkAssemblyHelper.getDrawArrayParameters() != null && !chunkAssemblyHelper.method_10149(RenderLayer.TRANSLUCENT)) {
			this.method_10158(chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT), this.position);
			chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT).restoreState(chunkAssemblyHelper.getDrawArrayParameters());
			this.method_10157(RenderLayer.TRANSLUCENT, f, g, h, chunkBuilder.getRenderLaterBuffers().get(RenderLayer.TRANSLUCENT), chunkAssemblyHelper);
		}
	}

	public void method_10164(float f, float g, float h, ChunkBuilder chunkBuilder) {
		ChunkAssemblyHelper chunkAssemblyHelper = new ChunkAssemblyHelper();
		int i = 1;
		BlockPos blockPos = this.position.toImmutable();
		BlockPos blockPos2 = blockPos.add(15, 15, 15);
		World world = this.world;
		if (world != null) {
			chunkBuilder.getLock().lock();

			try {
				if (chunkBuilder.getRenderStatus() != ChunkBuilder.RenderStatus.COMPILING) {
					return;
				}

				chunkBuilder.setChunkAssemblyHelper(chunkAssemblyHelper);
			} finally {
				chunkBuilder.getLock().unlock();
			}

			class_4245 lv = class_4245.method_19346(world, blockPos.add(-1, -1, -1), blockPos.add(16, 16, 16), 1);
			ChunkOcclusionDataBuilder chunkOcclusionDataBuilder = new ChunkOcclusionDataBuilder();
			HashSet set = Sets.newHashSet();
			if (lv != null) {
				chunkUpdates++;
				boolean[] bls = new boolean[RenderLayer.values().length];
				BlockModelRenderer.method_19195();
				Random random = new Random();
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();

				for (BlockPos.Mutable mutable : BlockPos.mutableIterate(blockPos, blockPos2)) {
					BlockState blockState = lv.getBlockState(mutable);
					Block block = blockState.getBlock();
					if (blockState.isFullOpaque(lv, mutable)) {
						chunkOcclusionDataBuilder.markClosed(mutable);
					}

					if (block.hasBlockEntity()) {
						BlockEntity blockEntity = lv.method_19348(mutable, Chunk.Status.CHECK);
						if (blockEntity != null) {
							class_4239<BlockEntity> lv2 = BlockEntityRenderDispatcher.INSTANCE.method_1630(blockEntity);
							if (lv2 != null) {
								chunkAssemblyHelper.addBlockEntity(blockEntity);
								if (lv2.method_12410(blockEntity)) {
									set.add(blockEntity);
								}
							}
						}
					}

					FluidState fluidState = lv.getFluidState(mutable);
					if (!fluidState.isEmpty()) {
						RenderLayer renderLayer = fluidState.getRenderLayer();
						int j = renderLayer.ordinal();
						BufferBuilder bufferBuilder = chunkBuilder.getRenderLaterBuffers().get(j);
						if (!chunkAssemblyHelper.isUnused(renderLayer)) {
							chunkAssemblyHelper.setUnused(renderLayer);
							this.method_10158(bufferBuilder, blockPos);
						}

						bls[j] |= blockRenderManager.method_19189(mutable, lv, bufferBuilder, fluidState);
					}

					if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
						RenderLayer renderLayer2 = block.getRenderLayerType();
						int k = renderLayer2.ordinal();
						BufferBuilder bufferBuilder2 = chunkBuilder.getRenderLaterBuffers().get(k);
						if (!chunkAssemblyHelper.isUnused(renderLayer2)) {
							chunkAssemblyHelper.setUnused(renderLayer2);
							this.method_10158(bufferBuilder2, blockPos);
						}

						bls[k] |= blockRenderManager.method_19188(blockState, mutable, lv, bufferBuilder2, random);
					}
				}

				for (RenderLayer renderLayer3 : RenderLayer.values()) {
					if (bls[renderLayer3.ordinal()]) {
						chunkAssemblyHelper.setUsed(renderLayer3);
					}

					if (chunkAssemblyHelper.isUnused(renderLayer3)) {
						this.method_10157(renderLayer3, f, g, h, chunkBuilder.getRenderLaterBuffers().get(renderLayer3), chunkAssemblyHelper);
					}
				}

				BlockModelRenderer.method_19199();
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
			this.field_11077 = new ChunkBuilder(this, ChunkBuilder.FunctionType.REBUILD_CHUNK, this.method_12429());
			var1 = this.field_11077;
		} finally {
			this.field_11075.unlock();
		}

		return var1;
	}

	@Nullable
	public ChunkBuilder method_10168() {
		this.field_11075.lock();

		Object var1;
		try {
			if (this.field_11077 == null || this.field_11077.getRenderStatus() != ChunkBuilder.RenderStatus.PENDING) {
				if (this.field_11077 != null && this.field_11077.getRenderStatus() != ChunkBuilder.RenderStatus.DONE) {
					this.field_11077.method_10118();
					this.field_11077 = null;
				}

				this.field_11077 = new ChunkBuilder(this, ChunkBuilder.FunctionType.RESORT_TRANSPARENCY, this.method_12429());
				this.field_11077.setChunkAssemblyHelper(this.field_11070);
				return this.field_11077;
			}

			var1 = null;
		} finally {
			this.field_11075.unlock();
		}

		return (ChunkBuilder)var1;
	}

	protected double method_12429() {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		double d = this.field_13615.minX + 8.0 - clientPlayerEntity.x;
		double e = this.field_13615.minY + 8.0 - clientPlayerEntity.y;
		double f = this.field_13615.minZ + 8.0 - clientPlayerEntity.z;
		return d * d + e * e + f * f;
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
		GlStateManager.scale(1.000001F, 1.000001F, 1.000001F);
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
		return this.position;
	}

	public void method_10162(boolean bl) {
		if (this.field_11083) {
			bl |= this.field_13618;
		}

		this.field_11083 = true;
		this.field_13618 = bl;
	}

	public void method_12430() {
		this.field_11083 = false;
		this.field_13618 = false;
	}

	public boolean method_10173() {
		return this.field_11083;
	}

	public boolean method_12431() {
		return this.field_11083 && this.field_13618;
	}

	public BlockPos method_12428(Direction direction) {
		return this.field_13617[direction.ordinal()];
	}

	public World getWorld() {
		return this.world;
	}
}
