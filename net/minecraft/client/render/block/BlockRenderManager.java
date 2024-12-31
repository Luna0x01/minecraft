package net.minecraft.client.render.block;

import java.util.Random;
import net.minecraft.class_3600;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.ChestOpeningRenderHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;

public class BlockRenderManager implements ResourceReloadListener {
	private final BlockModelShapes models;
	private final BlockModelRenderer blockModelRenderer;
	private final ChestOpeningRenderHelper field_10858 = new ChestOpeningRenderHelper();
	private final FluidRenderer fluidRenderer;
	private final Random field_20771 = new Random();

	public BlockRenderManager(BlockModelShapes blockModelShapes, BlockColors blockColors) {
		this.models = blockModelShapes;
		this.blockModelRenderer = new BlockModelRenderer(blockColors);
		this.fluidRenderer = new FluidRenderer();
	}

	public BlockModelShapes getModels() {
		return this.models;
	}

	public void method_9953(BlockState blockState, BlockPos blockPos, Sprite sprite, class_3600 arg) {
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			BakedModel bakedModel = this.models.getBakedModel(blockState);
			long l = blockState.getRenderingSeed(blockPos);
			BakedModel bakedModel2 = new BasicBakedModel.Builder(blockState, bakedModel, sprite, this.field_20771, l).build();
			this.blockModelRenderer.method_19197(arg, bakedModel2, blockState, blockPos, Tessellator.getInstance().getBuffer(), true, this.field_20771, l);
		}
	}

	public boolean method_19188(BlockState blockState, BlockPos blockPos, class_3600 arg, BufferBuilder bufferBuilder, Random random) {
		try {
			BlockRenderType blockRenderType = blockState.getRenderType();
			if (blockRenderType == BlockRenderType.INVISIBLE) {
				return false;
			} else {
				switch (blockRenderType) {
					case MODEL:
						return this.blockModelRenderer
							.method_19197(arg, this.method_12346(blockState), blockState, blockPos, bufferBuilder, true, random, blockState.getRenderingSeed(blockPos));
					case ENTITYBLOCK_ANIMATED:
						return false;
					default:
						return false;
				}
			}
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockInfo(crashReportSection, blockPos, blockState);
			throw new CrashException(crashReport);
		}
	}

	public boolean method_19189(BlockPos blockPos, class_3600 arg, BufferBuilder bufferBuilder, FluidState fluidState) {
		try {
			return this.fluidRenderer.method_19194(arg, blockPos, bufferBuilder, fluidState);
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

	public BakedModel method_12346(BlockState blockState) {
		return this.models.getBakedModel(blockState);
	}

	public void renderBlockEntity(BlockState state, float light) {
		BlockRenderType blockRenderType = state.getRenderType();
		if (blockRenderType != BlockRenderType.INVISIBLE) {
			switch (blockRenderType) {
				case MODEL:
					BakedModel bakedModel = this.method_12346(state);
					this.blockModelRenderer.render(bakedModel, state, light, true);
					break;
				case ENTITYBLOCK_ANIMATED:
					this.field_10858.render(state.getBlock(), light);
			}
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.fluidRenderer.onResourceReload();
	}
}
