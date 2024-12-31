package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.client.render.entity.model.SheepWoolEntityModel;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class SheepWoolFeatureRenderer implements FeatureRenderer<SheepEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/sheep/sheep_fur.png");
	private final SheepEntityRenderer sheepRenderer;
	private final SheepWoolEntityModel model = new SheepWoolEntityModel();

	public SheepWoolFeatureRenderer(SheepEntityRenderer sheepEntityRenderer) {
		this.sheepRenderer = sheepEntityRenderer;
	}

	public void render(SheepEntity sheepEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!sheepEntity.isSheared() && !sheepEntity.isInvisible()) {
			this.sheepRenderer.bindTexture(TEXTURE);
			if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getCustomName())) {
				int m = 25;
				int n = sheepEntity.ticksAlive / 25 + sheepEntity.getEntityId();
				int o = DyeColor.values().length;
				int p = n % o;
				int q = (n + 1) % o;
				float r = ((float)(sheepEntity.ticksAlive % 25) + h) / 25.0F;
				float[] fs = SheepEntity.getDyedColor(DyeColor.byId(p));
				float[] gs = SheepEntity.getDyedColor(DyeColor.byId(q));
				GlStateManager.color(fs[0] * (1.0F - r) + gs[0] * r, fs[1] * (1.0F - r) + gs[1] * r, fs[2] * (1.0F - r) + gs[2] * r);
			} else {
				float[] hs = SheepEntity.getDyedColor(sheepEntity.getColor());
				GlStateManager.color(hs[0], hs[1], hs[2]);
			}

			this.model.copy(this.sheepRenderer.getModel());
			this.model.animateModel(sheepEntity, f, g, h);
			this.model.render(sheepEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean combineTextures() {
		return true;
	}
}
