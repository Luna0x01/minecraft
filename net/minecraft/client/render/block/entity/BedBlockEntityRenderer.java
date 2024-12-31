package net.minecraft.client.render.block.entity;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BedBlockEntityRenderer extends BlockEntityRenderer<BedBlockEntity> {
	private final ModelPart field_20813;
	private final ModelPart field_20814;
	private final ModelPart[] field_20815 = new ModelPart[4];

	public BedBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
		this.field_20813 = new ModelPart(64, 64, 0, 0);
		this.field_20813.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
		this.field_20814 = new ModelPart(64, 64, 0, 22);
		this.field_20814.addCuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
		this.field_20815[0] = new ModelPart(64, 64, 50, 0);
		this.field_20815[1] = new ModelPart(64, 64, 50, 6);
		this.field_20815[2] = new ModelPart(64, 64, 50, 12);
		this.field_20815[3] = new ModelPart(64, 64, 50, 18);
		this.field_20815[0].addCuboid(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[1].addCuboid(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[2].addCuboid(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[3].addCuboid(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
		this.field_20815[0].pitch = (float) (Math.PI / 2);
		this.field_20815[1].pitch = (float) (Math.PI / 2);
		this.field_20815[2].pitch = (float) (Math.PI / 2);
		this.field_20815[3].pitch = (float) (Math.PI / 2);
		this.field_20815[0].roll = 0.0F;
		this.field_20815[1].roll = (float) (Math.PI / 2);
		this.field_20815[2].roll = (float) (Math.PI * 3.0 / 2.0);
		this.field_20815[3].roll = (float) Math.PI;
	}

	public void render(BedBlockEntity bedBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
		SpriteIdentifier spriteIdentifier = TexturedRenderLayers.BED_TEXTURES[bedBlockEntity.getColor().getId()];
		World world = bedBlockEntity.getWorld();
		if (world != null) {
			BlockState blockState = bedBlockEntity.getCachedState();
			DoubleBlockProperties.PropertySource<? extends BedBlockEntity> propertySource = DoubleBlockProperties.toPropertySource(
				BlockEntityType.field_11910,
				BedBlock::method_24164,
				BedBlock::method_24163,
				ChestBlock.FACING,
				blockState,
				world,
				bedBlockEntity.getPos(),
				(iWorld, blockPos) -> false
			);
			int k = propertySource.apply(new LightmapCoordinatesRetriever<>()).get(i);
			this.method_3558(
				matrixStack, vertexConsumerProvider, blockState.get(BedBlock.PART) == BedPart.field_12560, blockState.get(BedBlock.FACING), spriteIdentifier, k, j, false
			);
		} else {
			this.method_3558(matrixStack, vertexConsumerProvider, true, Direction.field_11035, spriteIdentifier, i, j, false);
			this.method_3558(matrixStack, vertexConsumerProvider, false, Direction.field_11035, spriteIdentifier, i, j, true);
		}
	}

	private void method_3558(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		boolean bl,
		Direction direction,
		SpriteIdentifier spriteIdentifier,
		int i,
		int j,
		boolean bl2
	) {
		this.field_20813.visible = bl;
		this.field_20814.visible = !bl;
		this.field_20815[0].visible = !bl;
		this.field_20815[1].visible = bl;
		this.field_20815[2].visible = !bl;
		this.field_20815[3].visible = bl;
		matrixStack.push();
		matrixStack.translate(0.0, 0.5625, bl2 ? -1.0 : 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
		matrixStack.translate(0.5, 0.5, 0.5);
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F + direction.asRotation()));
		matrixStack.translate(-0.5, -0.5, -0.5);
		VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
		this.field_20813.render(matrixStack, vertexConsumer, i, j);
		this.field_20814.render(matrixStack, vertexConsumer, i, j);
		this.field_20815[0].render(matrixStack, vertexConsumer, i, j);
		this.field_20815[1].render(matrixStack, vertexConsumer, i, j);
		this.field_20815[2].render(matrixStack, vertexConsumer, i, j);
		this.field_20815[3].render(matrixStack, vertexConsumer, i, j);
		matrixStack.pop();
	}
}
