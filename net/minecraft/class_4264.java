package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class class_4264 extends MobEntityRenderer<TropicalFishEntity> {
	private final class_4198 field_20948 = new class_4198();
	private final class_4199 field_20949 = new class_4199();

	public class_4264(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4198(), 0.15F);
		this.addFeature(new class_4274(this));
	}

	@Nullable
	protected Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
		return tropicalFishEntity.method_15782();
	}

	public void render(TropicalFishEntity tropicalFishEntity, double d, double e, double f, float g, float h) {
		this.model = (EntityModel)(tropicalFishEntity.method_15780() == 0 ? this.field_20948 : this.field_20949);
		float[] fs = tropicalFishEntity.method_15778();
		GlStateManager.color(fs[0], fs[1], fs[2]);
		super.render(tropicalFishEntity, d, e, f, g, h);
	}

	protected void method_5777(TropicalFishEntity tropicalFishEntity, float f, float g, float h) {
		super.method_5777(tropicalFishEntity, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		GlStateManager.rotate(i, 0.0F, 1.0F, 0.0F);
		if (!tropicalFishEntity.isTouchingWater()) {
			GlStateManager.translate(0.2F, 0.1F, 0.0F);
			GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		}
	}
}
