package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.util.Identifier;

public class EntityEntityRenderer extends EntityRenderer<AreaEffectCloudEntity> {
	public EntityEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	@Nullable
	protected Identifier getTexture(AreaEffectCloudEntity areaEffectCloudEntity) {
		return null;
	}
}
