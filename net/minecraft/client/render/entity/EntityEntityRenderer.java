package net.minecraft.client.render.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.Identifier;

public class EntityEntityRenderer extends EntityRenderer<AreaEffectCloudEntity> {
	public EntityEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void render(AreaEffectCloudEntity areaEffectCloudEntity, double d, double e, double f, float g, float h) {
		super.render(areaEffectCloudEntity, d, e, f, g, h);
	}

	protected Identifier getTexture(AreaEffectCloudEntity areaEffectCloudEntity) {
		return null;
	}
}
