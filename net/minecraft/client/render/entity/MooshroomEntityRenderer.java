package net.minecraft.client.render.entity;

import net.minecraft.class_4184;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.Identifier;

public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity> {
	private static final Identifier MOOSHROOM_TEX = new Identifier("textures/entity/cow/mooshroom.png");

	public MooshroomEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new class_4184(), 0.7F);
		this.addFeature(new MooshroomMushroomFeatureRenderer(this));
	}

	public class_4184 getModel() {
		return (class_4184)super.getModel();
	}

	protected Identifier getTexture(MooshroomEntity mooshroomEntity) {
		return MOOSHROOM_TEX;
	}
}
