package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_4262 extends MobEntityRenderer<SalmonEntity> {
	private static final Identifier field_20943 = new Identifier("textures/entity/fish/salmon.png");

	public class_4262(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4194(), 0.2F);
	}

	@Nullable
	protected Identifier getTexture(SalmonEntity salmonEntity) {
		return field_20943;
	}

	protected void method_5777(SalmonEntity salmonEntity, float f, float g, float h) {
		super.method_5777(salmonEntity, f, g, h);
		float i = 1.0F;
		float j = 1.0F;
		if (!salmonEntity.isTouchingWater()) {
			i = 1.3F;
			j = 1.7F;
		}

		float k = i * 4.3F * MathHelper.sin(j * 0.6F * f);
		GlStateManager.rotate(k, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.0F, -0.4F);
		if (!salmonEntity.isTouchingWater()) {
			GlStateManager.translate(0.2F, 0.1F, 0.0F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
