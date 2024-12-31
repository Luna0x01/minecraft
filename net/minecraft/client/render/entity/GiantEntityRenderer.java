package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.util.Identifier;

public class GiantEntityRenderer extends MobEntityRenderer<GiantEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");
	private final float scale;

	public GiantEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel entityModel, float f, float g) {
		super(entityRenderDispatcher, entityModel, f * g);
		this.scale = g;
		this.addFeature(new HeldItemRenderer(this));
		this.addFeature(new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new AbstractZombieModel(0.5F, true);
				this.firstLayer = new AbstractZombieModel(1.0F, true);
			}
		});
	}

	@Override
	public void translate() {
		GlStateManager.translate(0.0F, 0.1875F, 0.0F);
	}

	protected void scale(GiantEntity giantEntity, float f) {
		GlStateManager.scale(this.scale, this.scale, this.scale);
	}

	protected Identifier getTexture(GiantEntity giantEntity) {
		return TEXTURE;
	}
}
