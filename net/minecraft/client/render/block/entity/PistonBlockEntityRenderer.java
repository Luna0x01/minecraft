package net.minecraft.client.render.block.entity;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PistonBlockEntityRenderer extends BlockEntityRenderer<PistonBlockEntity> {
	private final BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();

	public PistonBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void render(PistonBlockEntity pistonBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		World world = pistonBlockEntity.getWorld();
		if (world != null) {
			BlockPos blockPos = pistonBlockEntity.getPos().offset(pistonBlockEntity.getMovementDirection().getOpposite());
			BlockState blockState = pistonBlockEntity.getPushedBlock();
			if (!blockState.isAir() && !(pistonBlockEntity.getProgress(f) >= 1.0F)) {
				BlockModelRenderer.enableBrightnessCache();
				matrixStack.push();
				matrixStack.translate(
					(double)pistonBlockEntity.getRenderOffsetX(f), (double)pistonBlockEntity.getRenderOffsetY(f), (double)pistonBlockEntity.getRenderOffsetZ(f)
				);
				if (blockState.getBlock() == Blocks.field_10379 && pistonBlockEntity.getProgress(f) <= 4.0F) {
					blockState = blockState.with(PistonHeadBlock.SHORT, Boolean.valueOf(true));
					this.method_3575(blockPos, blockState, matrixStack, vertexConsumerProvider, world, false, j);
				} else if (pistonBlockEntity.isSource() && !pistonBlockEntity.isExtending()) {
					PistonType pistonType = blockState.getBlock() == Blocks.field_10615 ? PistonType.field_12634 : PistonType.field_12637;
					BlockState blockState2 = Blocks.field_10379
						.getDefaultState()
						.with(PistonHeadBlock.TYPE, pistonType)
						.with(PistonHeadBlock.FACING, blockState.get(PistonBlock.FACING));
					blockState2 = blockState2.with(PistonHeadBlock.SHORT, Boolean.valueOf(pistonBlockEntity.getProgress(f) >= 0.5F));
					this.method_3575(blockPos, blockState2, matrixStack, vertexConsumerProvider, world, false, j);
					BlockPos blockPos2 = blockPos.offset(pistonBlockEntity.getMovementDirection());
					matrixStack.pop();
					matrixStack.push();
					blockState = blockState.with(PistonBlock.EXTENDED, Boolean.valueOf(true));
					this.method_3575(blockPos2, blockState, matrixStack, vertexConsumerProvider, world, true, j);
				} else {
					this.method_3575(blockPos, blockState, matrixStack, vertexConsumerProvider, world, false, j);
				}

				matrixStack.pop();
				BlockModelRenderer.disableBrightnessCache();
			}
		}
	}

	private void method_3575(
		BlockPos blockPos, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, World world, boolean bl, int i
	) {
		RenderLayer renderLayer = RenderLayers.getBlockLayer(blockState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
		this.manager
			.getModelRenderer()
			.render(
				world, this.manager.getModel(blockState), blockState, blockPos, matrixStack, vertexConsumer, bl, new Random(), blockState.getRenderingSeed(blockPos), i
			);
	}
}
