package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.OcelotEntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

public class OcelotEntityRenderer extends MobEntityRenderer<OcelotEntity, OcelotEntityModel<OcelotEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/cat/ocelot.png");

	public OcelotEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new OcelotEntityModel<>(0.0F), 0.4F);
	}

	protected Identifier method_16046(OcelotEntity ocelotEntity) {
		return SKIN;
	}
}
