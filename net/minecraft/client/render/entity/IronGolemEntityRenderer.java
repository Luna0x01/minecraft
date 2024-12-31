package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.IronGolemCrackFeatureRenderer;
import net.minecraft.client.render.entity.feature.IronGolemFlowerFeatureRenderer;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;

public class IronGolemEntityRenderer extends MobEntityRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/iron_golem/iron_golem.png");

	public IronGolemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new IronGolemEntityModel<>(), 0.7F);
		this.addFeature(new IronGolemCrackFeatureRenderer(this));
		this.addFeature(new IronGolemFlowerFeatureRenderer(this));
	}

	public Identifier getTexture(IronGolemEntity ironGolemEntity) {
		return SKIN;
	}

	protected void setupTransforms(IronGolemEntity ironGolemEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(ironGolemEntity, matrixStack, f, g, h);
		if (!((double)ironGolemEntity.limbDistance < 0.01)) {
			float i = 13.0F;
			float j = ironGolemEntity.limbAngle - ironGolemEntity.limbDistance * (1.0F - h) + 6.0F;
			float k = (Math.abs(j % 13.0F - 6.5F) - 3.25F) / 3.25F;
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(6.5F * k));
		}
	}
}
