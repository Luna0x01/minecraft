package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.class_3135;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class LlamaEntityModel extends QuadruPedEntityModel {
	private final ModelPart field_15267;
	private final ModelPart field_15268;

	public LlamaEntityModel(float f) {
		super(15, f);
		this.textureWidth = 128;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -14.0F, -10.0F, 4, 4, 9, f);
		this.head.setPivot(0.0F, 7.0F, -6.0F);
		this.head.setTextureOffset(0, 14).addCuboid(-4.0F, -16.0F, -6.0F, 8, 18, 6, f);
		this.head.setTextureOffset(17, 0).addCuboid(-4.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.head.setTextureOffset(17, 0).addCuboid(1.0F, -19.0F, -4.0F, 3, 3, 2, f);
		this.torso = new ModelPart(this, 29, 0);
		this.torso.addCuboid(-6.0F, -10.0F, -7.0F, 12, 18, 10, f);
		this.torso.setPivot(0.0F, 5.0F, 2.0F);
		this.field_15267 = new ModelPart(this, 45, 28);
		this.field_15267.addCuboid(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_15267.setPivot(-8.5F, 3.0F, 3.0F);
		this.field_15267.posY = (float) (Math.PI / 2);
		this.field_15268 = new ModelPart(this, 45, 41);
		this.field_15268.addCuboid(-3.0F, 0.0F, 0.0F, 8, 8, 3, f);
		this.field_15268.setPivot(5.5F, 3.0F, 3.0F);
		this.field_15268.posY = (float) (Math.PI / 2);
		int i = 4;
		int j = 14;
		this.backRightLeg = new ModelPart(this, 29, 29);
		this.backRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.backRightLeg.setPivot(-2.5F, 10.0F, 6.0F);
		this.backLeftLeg = new ModelPart(this, 29, 29);
		this.backLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.backLeftLeg.setPivot(2.5F, 10.0F, 6.0F);
		this.frontRightLeg = new ModelPart(this, 29, 29);
		this.frontRightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.frontRightLeg.setPivot(-2.5F, 10.0F, -4.0F);
		this.frontLeftLeg = new ModelPart(this, 29, 29);
		this.frontLeftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 14, 4, f);
		this.frontLeftLeg.setPivot(2.5F, 10.0F, -4.0F);
		this.backRightLeg.pivotX--;
		this.backLeftLeg.pivotX++;
		this.backRightLeg.pivotZ += 0.0F;
		this.backLeftLeg.pivotZ += 0.0F;
		this.frontRightLeg.pivotX--;
		this.frontLeftLeg.pivotX++;
		this.frontRightLeg.pivotZ--;
		this.frontLeftLeg.pivotZ--;
		this.field_1515 += 2.0F;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		class_3135 lv = (class_3135)entity;
		boolean bl = !lv.isBaby() && lv.method_13963();
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.child) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, this.field_1514 * scale, this.field_1515 * scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float g = 0.7F;
			GlStateManager.scale(0.71428573F, 0.64935064F, 0.7936508F);
			GlStateManager.translate(0.0F, 21.0F * scale, 0.22F);
			this.head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float h = 1.1F;
			GlStateManager.scale(0.625F, 0.45454544F, 0.45454544F);
			GlStateManager.translate(0.0F, 33.0F * scale, 0.0F);
			this.torso.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.45454544F, 0.41322312F, 0.45454544F);
			GlStateManager.translate(0.0F, 33.0F * scale, 0.0F);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.head.render(scale);
			this.torso.render(scale);
			this.backRightLeg.render(scale);
			this.backLeftLeg.render(scale);
			this.frontRightLeg.render(scale);
			this.frontLeftLeg.render(scale);
		}

		if (bl) {
			this.field_15267.render(scale);
			this.field_15268.render(scale);
		}
	}
}
