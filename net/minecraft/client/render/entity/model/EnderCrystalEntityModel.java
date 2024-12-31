package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class EnderCrystalEntityModel extends EntityModel {
	private final ModelPart core;
	private final ModelPart frame = new ModelPart(this, "glass");
	private ModelPart bottom;

	public EnderCrystalEntityModel(float f, boolean bl) {
		this.frame.setTextureOffset(0, 0).addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		this.core = new ModelPart(this, "cube");
		this.core.setTextureOffset(32, 0).addCuboid(-4.0F, -4.0F, -4.0F, 8, 8, 8);
		if (bl) {
			this.bottom = new ModelPart(this, "base");
			this.bottom.setTextureOffset(0, 16).addCuboid(-6.0F, 0.0F, -6.0F, 12, 4, 12);
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		GlStateManager.translate(0.0F, -0.5F, 0.0F);
		if (this.bottom != null) {
			this.bottom.render(scale);
		}

		GlStateManager.rotate(handSwingAmount, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(0.0F, 0.8F + tickDelta, 0.0F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		this.frame.render(scale);
		float f = 0.875F;
		GlStateManager.scale(0.875F, 0.875F, 0.875F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotate(handSwingAmount, 0.0F, 1.0F, 0.0F);
		this.frame.render(scale);
		GlStateManager.scale(0.875F, 0.875F, 0.875F);
		GlStateManager.rotate(60.0F, 0.7071F, 0.0F, 0.7071F);
		GlStateManager.rotate(handSwingAmount, 0.0F, 1.0F, 0.0F);
		this.core.render(scale);
		GlStateManager.popMatrix();
	}
}
