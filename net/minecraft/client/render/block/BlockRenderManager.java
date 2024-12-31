package net.minecraft.client.render.block;

import java.util.Random;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class BlockRenderManager implements SynchronousResourceReloadListener {
	private final BlockModels models;
	private final BlockModelRenderer blockModelRenderer;
	private final FluidRenderer fluidRenderer;
	private final Random random = new Random();
	private final BlockColors blockColors;

	public BlockRenderManager(BlockModels blockModels, BlockColors blockColors) {
		this.models = blockModels;
		this.blockColors = blockColors;
		this.blockModelRenderer = new BlockModelRenderer(this.blockColors);
		this.fluidRenderer = new FluidRenderer();
	}

	public BlockModels getModels() {
		return this.models;
	}

	public void renderDamage(BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer) {
		if (blockState.getRenderType() == BlockRenderType.field_11458) {
			BakedModel bakedModel = this.models.getModel(blockState);
			long l = blockState.getRenderingSeed(blockPos);
			this.blockModelRenderer
				.render(blockRenderView, bakedModel, blockState, blockPos, matrixStack, vertexConsumer, true, this.random, l, OverlayTexture.DEFAULT_UV);
		}
	}

	public boolean renderBlock(
		BlockState blockState, BlockPos blockPos, BlockRenderView blockRenderView, MatrixStack matrixStack, VertexConsumer vertexConsumer, boolean bl, Random random
	) {
		try {
			BlockRenderType blockRenderType = blockState.getRenderType();
			return blockRenderType != BlockRenderType.field_11458
				? false
				: this.blockModelRenderer
					.render(
						blockRenderView,
						this.getModel(blockState),
						blockState,
						blockPos,
						matrixStack,
						vertexConsumer,
						bl,
						random,
						blockState.getRenderingSeed(blockPos),
						OverlayTexture.DEFAULT_UV
					);
		} catch (Throwable var11) {
			CrashReport crashReport = CrashReport.create(var11, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			throw new CrashException(crashReport);
		}
	}

	public boolean renderFluid(BlockPos blockPos, BlockRenderView blockRenderView, VertexConsumer vertexConsumer, FluidState fluidState) {
		try {
			return this.fluidRenderer.render(blockRenderView, blockPos, vertexConsumer, fluidState);
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Tesselating liquid in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, null);
			throw new CrashException(crashReport);
		}
	}

	public BlockModelRenderer getModelRenderer() {
		return this.blockModelRenderer;
	}

	public BakedModel getModel(BlockState blockState) {
		return this.models.getModel(blockState);
	}

	public void renderBlockAsEntity(BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		BlockRenderType blockRenderType = blockState.getRenderType();
		if (blockRenderType != BlockRenderType.field_11455) {
			switch (blockRenderType) {
				case field_11458:
					BakedModel bakedModel = this.getModel(blockState);
					int k = this.blockColors.getColor(blockState, null, null, 0);
					float f = (float)(k >> 16 & 0xFF) / 255.0F;
					float g = (float)(k >> 8 & 0xFF) / 255.0F;
					float h = (float)(k & 0xFF) / 255.0F;
					this.blockModelRenderer
						.render(matrixStack.peek(), vertexConsumerProvider.getBuffer(RenderLayers.getEntityBlockLayer(blockState)), blockState, bakedModel, f, g, h, i, j);
					break;
				case field_11456:
					BuiltinModelItemRenderer.INSTANCE.render(new ItemStack(blockState.getBlock()), matrixStack, vertexConsumerProvider, i, j);
			}
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.fluidRenderer.onResourceReload();
	}
}
