package net.minecraft.client.render.entity.feature;

import net.minecraft.class_3039;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.util.Identifier;

public class StrayFeatureRenderer implements FeatureRenderer<SkeletonEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/skeleton/stray_overlay.png");
	private final LivingEntityRenderer<?> field_14989;
	private SkeletonEntityModel field_14990;

	public StrayFeatureRenderer(LivingEntityRenderer<?> livingEntityRenderer) {
		this.field_14989 = livingEntityRenderer;
		this.field_14990 = new SkeletonEntityModel(0.25F, true);
	}

	public void render(SkeletonEntity skeletonEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (skeletonEntity.method_13539() == class_3039.STRAY) {
			this.field_14990.copy(this.field_14989.getModel());
			this.field_14990.animateModel(skeletonEntity, f, g, h);
			this.field_14989.bindTexture(TEXTURE);
			this.field_14990.render(skeletonEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
