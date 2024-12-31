package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.AbstractHorseEntity;

public abstract class class_3099<T extends AbstractHorseEntity> extends MobEntityRenderer<AbstractHorseEntity> {
	private final float field_15293;

	public class_3099(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f) {
		super(entityRenderDispatcher, entityModel, 0.75F);
		this.field_15293 = f;
	}

	protected void scale(AbstractHorseEntity abstractHorseEntity, float f) {
		GlStateManager.scale(this.field_15293, this.field_15293, this.field_15293);
		super.scale((T)abstractHorseEntity, f);
	}
}
