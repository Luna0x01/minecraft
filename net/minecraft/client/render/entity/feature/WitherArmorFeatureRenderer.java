package net.minecraft.client.render.entity.feature;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WitherArmorFeatureRenderer extends EnergySwirlOverlayFeatureRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
	private final WitherEntityModel<WitherEntity> model = new WitherEntityModel<>(0.5F);

	public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	protected float getEnergySwirlX(float f) {
		return MathHelper.cos(f * 0.02F) * 3.0F;
	}

	@Override
	protected Identifier getEnergySwirlTexture() {
		return SKIN;
	}

	@Override
	protected EntityModel<WitherEntity> getEnergySwirlModel() {
		return this.model;
	}
}
