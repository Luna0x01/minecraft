package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.TropicalFishEntity;

public class class_4274 implements FeatureRenderer<TropicalFishEntity> {
	private final class_4264 field_20971;
	private final class_4198 field_20972;
	private final class_4199 field_20973;

	public class_4274(class_4264 arg) {
		this.field_20971 = arg;
		this.field_20972 = new class_4198(0.008F);
		this.field_20973 = new class_4199(0.008F);
	}

	public void render(TropicalFishEntity tropicalFishEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!tropicalFishEntity.isInvisible()) {
			EntityModel entityModel = (EntityModel)(tropicalFishEntity.method_15780() == 0 ? this.field_20972 : this.field_20973);
			this.field_20971.bindTexture(tropicalFishEntity.method_15781());
			float[] fs = tropicalFishEntity.method_15779();
			GlStateManager.color(fs[0], fs[1], fs[2]);
			entityModel.copy(this.field_20971.getModel());
			entityModel.animateModel(tropicalFishEntity, f, g, h);
			entityModel.render(tropicalFishEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
