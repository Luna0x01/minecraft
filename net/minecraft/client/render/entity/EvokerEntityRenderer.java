package net.minecraft.client.render.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpellcastingIllagerEntity;
import net.minecraft.util.Identifier;

public class EvokerEntityRenderer<T extends SpellcastingIllagerEntity> extends IllagerEntityRenderer<T> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/evoker.png");

	public EvokerEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.EVOKER)), 0.5F);
		this.addFeature(
			new HeldItemFeatureRenderer<T, IllagerEntityModel<T>>(this) {
				public void render(
					MatrixStack matrixStack,
					VertexConsumerProvider vertexConsumerProvider,
					int i,
					T spellcastingIllagerEntity,
					float f,
					float g,
					float h,
					float j,
					float k,
					float l
				) {
					if (spellcastingIllagerEntity.isSpellcasting()) {
						super.render(matrixStack, vertexConsumerProvider, i, spellcastingIllagerEntity, f, g, h, j, k, l);
					}
				}
			}
		);
	}

	public Identifier getTexture(T spellcastingIllagerEntity) {
		return TEXTURE;
	}
}
