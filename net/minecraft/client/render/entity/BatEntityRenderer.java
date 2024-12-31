package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BatEntityModel;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BatEntityRenderer extends MobEntityRenderer<BatEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/bat.png");

	public BatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new BatEntityModel(), 0.25F);
	}

	protected Identifier getTexture(BatEntity batEntity) {
		return TEXTURE;
	}

	protected void scale(BatEntity batEntity, float f) {
		GlStateManager.scale(0.35F, 0.35F, 0.35F);
	}

	protected void method_5777(BatEntity batEntity, float f, float g, float h) {
		if (!batEntity.isRoosting()) {
			GlStateManager.translate(0.0F, MathHelper.cos(f * 0.3F) * 0.1F, 0.0F);
		} else {
			GlStateManager.translate(0.0F, -0.1F, 0.0F);
		}

		super.method_5777(batEntity, f, g, h);
	}
}
