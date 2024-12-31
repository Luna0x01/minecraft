package net.minecraft.client.render.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

public class ArrowEntityRenderer extends ProjectileEntityRenderer<ArrowEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/projectiles/arrow.png");
	public static final Identifier TIPPED_TEXTURE = new Identifier("textures/entity/projectiles/tipped_arrow.png");

	public ArrowEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public Identifier getTexture(ArrowEntity arrowEntity) {
		return arrowEntity.getColor() > 0 ? TIPPED_TEXTURE : TEXTURE;
	}
}
