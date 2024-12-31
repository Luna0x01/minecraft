package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;

public class WitherEntityRenderer extends MobEntityRenderer<WitherEntity> {
	private static final Identifier WITHER_INVULNERABLE_TEX = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier WITHER_TEX = new Identifier("textures/entity/wither/wither.png");

	public WitherEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new WitherEntityModel(0.0F), 1.0F);
		this.addFeature(new WitherArmorFeatureRenderer(this));
	}

	public void render(WitherEntity witherEntity, double d, double e, double f, float g, float h) {
		BossBar.update(witherEntity, true);
		super.render(witherEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(WitherEntity witherEntity) {
		int i = witherEntity.getInvulnerabilityTime();
		return i > 0 && (i > 80 || i / 5 % 2 != 1) ? WITHER_INVULNERABLE_TEX : WITHER_TEX;
	}

	protected void scale(WitherEntity witherEntity, float f) {
		float g = 2.0F;
		int i = witherEntity.getInvulnerabilityTime();
		if (i > 0) {
			g -= ((float)i - f) / 220.0F * 0.5F;
		}

		GlStateManager.scale(g, g, g);
	}
}
