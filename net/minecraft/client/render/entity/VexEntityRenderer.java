package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.entity.VexEntity;
import net.minecraft.util.Identifier;

public class VexEntityRenderer extends BipedEntityRenderer<VexEntity> {
	private static final Identifier TEXTURE_VEX = new Identifier("textures/entity/illager/vex.png");
	private static final Identifier TEXTURE_CHARGING = new Identifier("textures/entity/illager/vex_charging.png");

	public VexEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new VexEntityModel(), 0.3F);
	}

	protected Identifier getTexture(VexEntity vexEntity) {
		return vexEntity.isCharging() ? TEXTURE_CHARGING : TEXTURE_VEX;
	}

	protected void scale(VexEntity vexEntity, float f) {
		GlStateManager.scale(0.4F, 0.4F, 0.4F);
	}
}
