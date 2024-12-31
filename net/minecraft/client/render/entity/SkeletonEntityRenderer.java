package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3039;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.feature.StrayFeatureRenderer;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.util.Identifier;

public class SkeletonEntityRenderer extends BipedEntityRenderer<SkeletonEntity> {
	private static final Identifier SKELETON_TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");
	private static final Identifier WITHER_SKELETON_TEX = new Identifier("textures/entity/skeleton/wither_skeleton.png");
	private static final Identifier field_14986 = new Identifier("textures/entity/skeleton/stray.png");

	public SkeletonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new SkeletonEntityModel(), 0.5F);
		this.addFeature(new HeldItemRenderer(this));
		this.addFeature(new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new SkeletonEntityModel(0.5F, true);
				this.firstLayer = new SkeletonEntityModel(1.0F, true);
			}
		});
		this.addFeature(new StrayFeatureRenderer(this));
	}

	protected void scale(SkeletonEntity skeletonEntity, float f) {
		if (skeletonEntity.method_13539() == class_3039.WITHER) {
			GlStateManager.scale(1.2F, 1.2F, 1.2F);
		}
	}

	@Override
	public void translate() {
		GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
	}

	protected Identifier getTexture(SkeletonEntity skeletonEntity) {
		class_3039 lv = skeletonEntity.method_13539();
		if (lv == class_3039.WITHER) {
			return WITHER_SKELETON_TEX;
		} else {
			return lv == class_3039.STRAY ? field_14986 : SKELETON_TEXTURE;
		}
	}
}
