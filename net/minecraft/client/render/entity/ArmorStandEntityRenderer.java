package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.feature.ArmorRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/entity/armorstand/wood.png");

	public ArmorStandEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new ArmorStandEntityModel(), 0.0F);
		ArmorRenderer armorRenderer = new ArmorRenderer(this) {
			@Override
			protected void init() {
				this.secondLayer = new ArmorStandArmorEntityModel(0.5F);
				this.firstLayer = new ArmorStandArmorEntityModel(1.0F);
			}
		};
		this.addFeature(armorRenderer);
		this.addFeature(new HeldItemRenderer(this));
		this.addFeature(new HeadFeatureRenderer(this.getModel().head));
	}

	protected Identifier getTexture(ArmorStandEntity armorStandEntity) {
		return TEXTURE;
	}

	public ArmorStandEntityModel getModel() {
		return (ArmorStandEntityModel)super.getModel();
	}

	protected void method_5777(ArmorStandEntity armorStandEntity, float f, float g, float h) {
		GlStateManager.rotate(180.0F - g, 0.0F, 1.0F, 0.0F);
		float i = (float)(armorStandEntity.world.getLastUpdateTime() - armorStandEntity.lastHitTime) + h;
		if (i < 5.0F) {
			GlStateManager.rotate(MathHelper.sin(i / 1.5F * (float) Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	protected boolean hasLabel(ArmorStandEntity armorStandEntity) {
		return armorStandEntity.isCustomNameVisible();
	}

	public void render(ArmorStandEntity armorStandEntity, double d, double e, double f, float g, float h) {
		if (armorStandEntity.shouldShowName()) {
			this.field_11124 = true;
		}

		super.render(armorStandEntity, d, e, f, g, h);
		if (armorStandEntity.shouldShowName()) {
			this.field_11124 = false;
		}
	}
}
