package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.CreeperLightningFeatureRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CreeperEntityRenderer extends MobEntityRenderer<CreeperEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/creeper/creeper.png");

	public CreeperEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new CreeperEntityModel(), 0.5F);
		this.addFeature(new CreeperLightningFeatureRenderer(this));
	}

	protected void scale(CreeperEntity creeperEntity, float f) {
		float g = creeperEntity.getClientFuseTime(f);
		float h = 1.0F + MathHelper.sin(g * 100.0F) * g * 0.01F;
		g = MathHelper.clamp(g, 0.0F, 1.0F);
		g *= g;
		g *= g;
		float i = (1.0F + g * 0.4F) * h;
		float j = (1.0F + g * 0.1F) / h;
		GlStateManager.scale(i, j, i);
	}

	protected int method_5776(CreeperEntity creeperEntity, float f, float g) {
		float h = creeperEntity.getClientFuseTime(g);
		if ((int)(h * 10.0F) % 2 == 0) {
			return 0;
		} else {
			int i = (int)(h * 0.2F * 255.0F);
			i = MathHelper.clamp(i, 0, 255);
			return i << 24 | 822083583;
		}
	}

	protected Identifier getTexture(CreeperEntity creeperEntity) {
		return TEXTURE;
	}
}
