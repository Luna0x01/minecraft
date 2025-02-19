package net.minecraft.client.render.entity.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SnowGolemEntityModel;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class SnowmanPumpkinFeatureRenderer extends FeatureRenderer<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> {
	public SnowmanPumpkinFeatureRenderer(FeatureRendererContext<SnowGolemEntity, SnowGolemEntityModel<SnowGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		SnowGolemEntity snowGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		if (snowGolemEntity.hasPumpkin()) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			boolean bl = minecraftClient.hasOutline(snowGolemEntity) && snowGolemEntity.isInvisible();
			if (!snowGolemEntity.isInvisible() || bl) {
				matrixStack.push();
				this.getContextModel().getHead().rotate(matrixStack);
				float m = 0.625F;
				matrixStack.translate(0.0, -0.34375, 0.0);
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.scale(0.625F, -0.625F, -0.625F);
				ItemStack itemStack = new ItemStack(Blocks.CARVED_PUMPKIN);
				if (bl) {
					BlockState blockState = Blocks.CARVED_PUMPKIN.getDefaultState();
					BlockRenderManager blockRenderManager = minecraftClient.getBlockRenderManager();
					BakedModel bakedModel = blockRenderManager.getModel(blockState);
					int n = LivingEntityRenderer.getOverlay(snowGolemEntity, 0.0F);
					matrixStack.translate(-0.5, -0.5, -0.5);
					blockRenderManager.getModelRenderer()
						.render(
							matrixStack.peek(),
							vertexConsumerProvider.getBuffer(RenderLayer.getOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)),
							blockState,
							bakedModel,
							0.0F,
							0.0F,
							0.0F,
							i,
							n
						);
				} else {
					minecraftClient.getItemRenderer()
						.renderItem(
							snowGolemEntity,
							itemStack,
							ModelTransformation.Mode.HEAD,
							false,
							matrixStack,
							vertexConsumerProvider,
							snowGolemEntity.world,
							i,
							LivingEntityRenderer.getOverlay(snowGolemEntity, 0.0F),
							snowGolemEntity.getId()
						);
				}

				matrixStack.pop();
			}
		}
	}
}
