package net.minecraft.client.render.entity;

import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.Identifier;

public class SpectralArrowEntityRenderer extends ProjectileEntityRenderer<SpectralArrowEntity> {
	public static final Identifier SKIN = new Identifier("textures/entity/projectiles/spectral_arrow.png");

	public SpectralArrowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected Identifier method_4120(SpectralArrowEntity spectralArrowEntity) {
		return SKIN;
	}
}
