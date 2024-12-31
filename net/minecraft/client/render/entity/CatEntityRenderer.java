package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.util.Identifier;

public class CatEntityRenderer extends MobEntityRenderer<OcelotEntity> {
	private static final Identifier BLACK_CAT = new Identifier("textures/entity/cat/black.png");
	private static final Identifier OCELOT = new Identifier("textures/entity/cat/ocelot.png");
	private static final Identifier RED_CAT = new Identifier("textures/entity/cat/red.png");
	private static final Identifier SIAMESE_CAT = new Identifier("textures/entity/cat/siamese.png");

	public CatEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, f);
	}

	protected Identifier getTexture(OcelotEntity ocelotEntity) {
		switch (ocelotEntity.getCatVariant()) {
			case 0:
			default:
				return OCELOT;
			case 1:
				return BLACK_CAT;
			case 2:
				return RED_CAT;
			case 3:
				return SIAMESE_CAT;
		}
	}

	protected void scale(OcelotEntity ocelotEntity, float f) {
		super.scale(ocelotEntity, f);
		if (ocelotEntity.isTamed()) {
			GlStateManager.scale(0.8F, 0.8F, 0.8F);
		}
	}
}
