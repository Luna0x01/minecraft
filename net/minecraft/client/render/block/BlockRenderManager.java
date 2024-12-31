package net.minecraft.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.entity.ChestOpeningRenderHelper;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.WeightedBakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.level.LevelGeneratorType;

public class BlockRenderManager implements ResourceReloadListener {
	private BlockModelShapes models;
	private final GameOptions options;
	private final BlockModelRenderer blockModelRenderer = new BlockModelRenderer();
	private final ChestOpeningRenderHelper field_10858 = new ChestOpeningRenderHelper();
	private final FluidRenderer fluidRenderer = new FluidRenderer();

	public BlockRenderManager(BlockModelShapes blockModelShapes, GameOptions gameOptions) {
		this.models = blockModelShapes;
		this.options = gameOptions;
	}

	public BlockModelShapes getModels() {
		return this.models;
	}

	public void renderDamage(BlockState state, BlockPos pos, Sprite sprite, BlockView world) {
		Block block = state.getBlock();
		int i = block.getBlockType();
		if (i == 3) {
			state = block.getBlockState(state, world, pos);
			BakedModel bakedModel = this.models.getBakedModel(state);
			BakedModel bakedModel2 = new BasicBakedModel.Builder(bakedModel, sprite).build();
			this.blockModelRenderer.render(world, bakedModel2, state, pos, Tessellator.getInstance().getBuffer());
		}
	}

	public boolean renderBlock(BlockState state, BlockPos pos, BlockView world, BufferBuilder buffer) {
		try {
			int i = state.getBlock().getBlockType();
			if (i == -1) {
				return false;
			} else {
				switch (i) {
					case 1:
						return this.fluidRenderer.render(world, state, pos, buffer);
					case 2:
						return false;
					case 3:
						BakedModel bakedModel = this.getModel(state, world, pos);
						return this.blockModelRenderer.render(world, bakedModel, state, pos, buffer);
					default:
						return false;
				}
			}
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Tesselating block in world");
			CrashReportSection crashReportSection = crashReport.addElement("Block being tesselated");
			CrashReportSection.addBlockData(crashReportSection, pos, state.getBlock(), state.getBlock().getData(state));
			throw new CrashException(crashReport);
		}
	}

	public BlockModelRenderer getModelRenderer() {
		return this.blockModelRenderer;
	}

	private BakedModel getModel(BlockState state, BlockPos pos) {
		BakedModel bakedModel = this.models.getBakedModel(state);
		if (pos != null && this.options.alternativeBlocks && bakedModel instanceof WeightedBakedModel) {
			bakedModel = ((WeightedBakedModel)bakedModel).method_10425(MathHelper.hashCode(pos));
		}

		return bakedModel;
	}

	public BakedModel getModel(BlockState state, BlockView world, BlockPos pos) {
		Block block = state.getBlock();
		if (world.getGeneratorType() != LevelGeneratorType.DEBUG) {
			try {
				state = block.getBlockState(state, world, pos);
			} catch (Exception var6) {
			}
		}

		BakedModel bakedModel = this.models.getBakedModel(state);
		if (pos != null && this.options.alternativeBlocks && bakedModel instanceof WeightedBakedModel) {
			bakedModel = ((WeightedBakedModel)bakedModel).method_10425(MathHelper.hashCode(pos));
		}

		return bakedModel;
	}

	public void renderBlockEntity(BlockState state, float light) {
		int i = state.getBlock().getBlockType();
		if (i != -1) {
			switch (i) {
				case 1:
				default:
					break;
				case 2:
					this.field_10858.render(state.getBlock(), light);
					break;
				case 3:
					BakedModel bakedModel = this.getModel(state, null);
					this.blockModelRenderer.render(bakedModel, state, light, true);
			}
		}
	}

	public boolean method_9948(Block block, int i) {
		if (block == null) {
			return false;
		} else {
			int j = block.getBlockType();
			return j == 3 ? false : j == 2;
		}
	}

	@Override
	public void reload(ResourceManager resourceManager) {
		this.fluidRenderer.onResourceReload();
	}
}
