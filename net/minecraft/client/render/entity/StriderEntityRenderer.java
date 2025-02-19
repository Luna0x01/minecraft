package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.SaddleFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.util.Identifier;

public class StriderEntityRenderer extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/strider/strider.png");
	private static final Identifier COLD_TEXTURE = new Identifier("textures/entity/strider/strider_cold.png");

	public StriderEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER)), 0.5F);
		this.addFeature(
			new SaddleFeatureRenderer<>(
				this, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER_SADDLE)), new Identifier("textures/entity/strider/strider_saddle.png")
			)
		);
	}

	public Identifier getTexture(StriderEntity striderEntity) {
		return striderEntity.isCold() ? COLD_TEXTURE : TEXTURE;
	}

	protected void scale(StriderEntity striderEntity, MatrixStack matrixStack, float f) {
		if (striderEntity.isBaby()) {
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			this.shadowRadius = 0.25F;
		} else {
			this.shadowRadius = 0.5F;
		}
	}

	protected boolean isShaking(StriderEntity striderEntity) {
		return super.isShaking(striderEntity) || striderEntity.isCold();
	}
}
