package net.minecraft.client.render.entity;

import net.minecraft.class_4184;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.Identifier;

public class CowEntityRenderer extends MobEntityRenderer<CowEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/cow/cow.png");

	public CowEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4184(), 0.7F);
	}

	protected Identifier getTexture(CowEntity cowEntity) {
		return TEXTURE;
	}
}
