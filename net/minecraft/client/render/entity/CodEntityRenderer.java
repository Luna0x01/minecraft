package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.CodEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class CodEntityRenderer extends MobEntityRenderer<CodEntity, CodEntityModel<CodEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/fish/cod.png");

	public CodEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new CodEntityModel<>(context.getPart(EntityModelLayers.COD)), 0.3F);
	}

	public Identifier getTexture(CodEntity codEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(CodEntity codEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(codEntity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!codEntity.isTouchingWater()) {
			matrixStack.translate(0.1F, 0.1F, -0.1F);
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
