package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class VexEntityRenderer extends BipedEntityRenderer<VexEntity, VexEntityModel> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier CHARGING_TEXTURE = new Identifier("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new VexEntityModel(context.getPart(EntityModelLayers.VEX)), 0.3F);
	}

	protected int getBlockLight(VexEntity vexEntity, BlockPos blockPos) {
		return 15;
	}

	public Identifier getTexture(VexEntity vexEntity) {
		return vexEntity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
	}

	protected void scale(VexEntity vexEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(0.4F, 0.4F, 0.4F);
	}
}
