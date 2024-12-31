package net.minecraft.client.render.entity;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;

public class ArrowEntityRenderer extends BaseArrowEntityRenderer<ArrowEntity> {
	public static final Identifier ARROW_TEXTURE = new Identifier("textures/entity/projectiles/arrow.png");
	public static final Identifier TIPPED_ARROW_TEXTURE = new Identifier("textures/entity/projectiles/tipped_arrow.png");

	public ArrowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected Identifier getTexture(ArrowEntity arrowEntity) {
		return arrowEntity.getColor() > 0 ? TIPPED_ARROW_TEXTURE : ARROW_TEXTURE;
	}
}
