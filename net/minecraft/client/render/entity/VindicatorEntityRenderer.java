package net.minecraft.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.util.Identifier;

public class VindicatorEntityRenderer extends IllagerEntityRenderer<VindicatorEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vindicator.png");

	public VindicatorEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.VINDICATOR)), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<VindicatorEntity, IllagerEntityModel<VindicatorEntity>>(this) {
				public void render(
					MatrixStack matrixStack,
					VertexConsumerProvider vertexConsumerProvider,
					int i,
					VindicatorEntity vindicatorEntity,
					float f,
					float g,
					float h,
					float j,
					float k,
					float l
				) {
					if (vindicatorEntity.isAttacking()) {
						super.render(matrixStack, vertexConsumerProvider, i, vindicatorEntity, f, g, h, j, k, l);
					}
				}
			}
		);
	}

	public Identifier getTexture(VindicatorEntity vindicatorEntity) {
		return TEXTURE;
	}
}
