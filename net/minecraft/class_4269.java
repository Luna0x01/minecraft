package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.util.Identifier;

public class class_4269 implements FeatureRenderer<DrownedEntity> {
	private static final Identifier field_20957 = new Identifier("textures/entity/zombie/drowned_outer_layer.png");
	private final class_4256 field_20958;
	private final class_4185 field_20959 = new class_4185(0.25F, 0.0F, 64, 64);

	public class_4269(class_4256 arg) {
		this.field_20958 = arg;
	}

	public void render(DrownedEntity drownedEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!drownedEntity.isInvisible()) {
			this.field_20959.copy(this.field_20958.getModel());
			this.field_20959.animateModel(drownedEntity, f, g, h);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_20958.bindTexture(field_20957);
			this.field_20959.render(drownedEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
