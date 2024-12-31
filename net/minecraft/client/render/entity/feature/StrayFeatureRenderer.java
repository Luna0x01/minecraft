package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.StrayEntity;
import net.minecraft.util.Identifier;

public class StrayFeatureRenderer implements FeatureRenderer<StrayEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final LivingEntityRenderer<?> field_14989;
	private final SkeletonEntityModel field_14990 = new SkeletonEntityModel(0.25F, true);

	public StrayFeatureRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.field_14989 = livingEntityRenderer;
	}

	public void render(StrayEntity strayEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.field_14990.copy(this.field_14989.getModel());
		this.field_14990.animateModel(strayEntity, f, g, h);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.field_14989.bindTexture(TEXTURE);
		this.field_14990.render(strayEntity, f, g, i, j, k, l);
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
