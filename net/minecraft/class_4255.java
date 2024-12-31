package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.util.Identifier;

public class class_4255 extends MobEntityRenderer<DolphinEntity> {
	private static final Identifier field_20919 = new Identifier("textures/entity/dolphin.png");

	public class_4255(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4254(), 0.7F);
		this.addFeature(new class_4268(this));
	}

	protected Identifier getTexture(DolphinEntity dolphinEntity) {
		return field_20919;
	}

	protected void scale(DolphinEntity dolphinEntity, float f) {
		float g = 1.0F;
		GlStateManager.scale(1.0F, 1.0F, 1.0F);
	}

	protected void method_5777(DolphinEntity dolphinEntity, float f, float g, float h) {
		super.method_5777(dolphinEntity, f, g, h);
	}
}
