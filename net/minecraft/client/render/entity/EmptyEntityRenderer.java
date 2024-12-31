package net.minecraft.client.render.entity;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public class EmptyEntityRenderer<T extends Entity> extends EntityRenderer<T> {
	public EmptyEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public Identifier getTexture(T entity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
