package net.minecraft.client.render.entity;

import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.Identifier;

public class SpectralArrowEntityRenderer extends ProjectileEntityRenderer<SpectralArrowEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/spectral_arrow.png");

	public SpectralArrowEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public Identifier getTexture(SpectralArrowEntity spectralArrowEntity) {
		return TEXTURE;
	}
}
