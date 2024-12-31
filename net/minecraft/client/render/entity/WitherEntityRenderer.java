package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.feature.WitherArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;

public class WitherEntityRenderer extends MobEntityRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
	private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");

	public WitherEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new WitherEntityModel<>(0.0F), 1.0F);
		this.addFeature(new WitherArmorFeatureRenderer(this));
	}

	protected int getBlockLight(WitherEntity witherEntity, float f) {
		return 15;
	}

	public Identifier getTexture(WitherEntity witherEntity) {
		int i = witherEntity.getInvulnerableTimer();
		return i > 0 && (i > 80 || i / 5 % 2 != 1) ? INVINCIBLE_SKIN : SKIN;
	}

	protected void scale(WitherEntity witherEntity, MatrixStack matrixStack, float f) {
		float g = 2.0F;
		int i = witherEntity.getInvulnerableTimer();
		if (i > 0) {
			g -= ((float)i - f) / 220.0F * 0.5F;
		}

		matrixStack.scale(g, g, g);
	}
}
