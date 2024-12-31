package net.minecraft.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.BlockColors;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.ChestOpeningRenderHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.level.LevelGeneratorType;

public class BlockRenderManager implements ResourceReloadListener {
	private final BlockModelShapes models;
	private final BlockModelRenderer blockModelRenderer;
	private final ChestOpeningRenderHelper field_10858 = new ChestOpeningRenderHelper();
	private final FluidRenderer fluidRenderer;

	public BlockRenderManager(BlockModelShapes blockModelShapes, BlockColors blockColors) {
		this.models = blockModelShapes;
		this.blockModelRenderer = new BlockModelRenderer(blockColors);
		this.fluidRenderer = new FluidRenderer(blockColors);
	}

	public BlockModelShapes getModels() {
		return this.models;
	}

	public void renderDamage(BlockState state, BlockPos pos, Sprite sprite, BlockView world) {
		if (state.getRenderType() == BlockRenderType.MODEL) {
			state = state.getBlockState(world, pos);
			BakedModel bakedModel = this.models.getBakedModel(state);
			BakedModel bakedModel2 = new BasicBakedModel.Builder(state, bakedModel, sprite, pos).build();
			this.blockModelRenderer.method_12349(world, bakedModel2, state, pos, Tessellator.getInstance().getBuffer(), true);
		}
	}

	public boolean renderBlock(BlockState state, BlockPos pos, BlockView world, BufferBuilder buffer) {
		try {
			BlockRenderType blockRenderType = state.getRenderType();
			if (blockRenderType == BlockRenderType.INVISIBLE) {
				return false;
			} else {
				if (world.getGeneratorType() != LevelGeneratorType.DEBUG) {
					try {
						state = state.getBlockState(world, pos);
					} catch (Exception var8) {
					}
				}

				switch (blockRenderType) {
					case MODEL:
						return this.blockModelRenderer.method_12349(world, this.method_12346(state), state, pos, buffer, true);
					case ENTITYBLOCK_ANIMATED:
						return false;
					case LIQUID:
						return this.fluidRenderer.render(world, state, pos, buffer);
					default:
						return false;
				}
			}
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockData(crashReportSection, pos, state.getBlock(), state.getBlock().getData(state));
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
				case LIQUID:
			}
		}
	}

	public boolean method_12345(Block block) {
		if (block == null) {
			return false;
		} else {
			BlockRenderType blockRenderType = block.getDefaultState().getRenderType();
			return blockRenderType == BlockRenderType.MODEL ? false : blockRenderType == BlockRenderType.ENTITYBLOCK_ANIMATED;
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.fluidRenderer.onResourceReload();
	}
}
