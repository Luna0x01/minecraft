package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_4253 extends MobEntityRenderer<CodEntity> {
	private static final Identifier field_20914 = new Identifier("textures/entity/fish/cod.png");

	public class_4253(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4183(), 0.2F);
	}

	@Nullable
	protected Identifier getTexture(CodEntity codEntity) {
		return field_20914;
	}

	protected void method_5777(CodEntity codEntity, float f, float g, float h) {
		super.method_5777(codEntity, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		GlStateManager.rotate(i, 0.0F, 1.0F, 0.0F);
		if (!codEntity.isTouchingWater()) {
			GlStateManager.translate(0.1F, 0.1F, -0.1F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
