package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ChickenEntityModel extends EntityModel {
	public ModelPart head;
	public ModelPart torso;
	public ModelPart rightLeg;
	public ModelPart leftLeg;
	public ModelPart rightWing;
	public ModelPart leftWing;
	public ModelPart beak;
	public ModelPart wattle;

	public ChickenEntityModel() {
		int i = 16;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
		this.head.setPivot(0.0F, 15.0F, -4.0F);
		this.beak = new ModelPart(this, 14, 0);
		this.beak.addCuboid(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
		this.beak.setPivot(0.0F, 15.0F, -4.0F);
		this.wattle = new ModelPart(this, 14, 4);
		this.wattle.addCuboid(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
		this.wattle.setPivot(0.0F, 15.0F, -4.0F);
		this.torso = new ModelPart(this, 0, 9);
		this.torso.addCuboid(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.torso.setPivot(0.0F, 16.0F, 0.0F);
		this.rightLeg = new ModelPart(this, 26, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.rightLeg.setPivot(-2.0F, 19.0F, 1.0F);
		this.leftLeg = new ModelPart(this, 26, 0);
		this.leftLeg.addCuboid(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.leftLeg.setPivot(1.0F, 19.0F, 1.0F);
		this.rightWing = new ModelPart(this, 24, 13);
		this.rightWing.addCuboid(0.0F, 0.0F, -3.0F, 1, 4, 6);
		this.rightWing.setPivot(-4.0F, 13.0F, 0.0F);
		this.leftWing = new ModelPart(this, 24, 13);
		this.leftWing.addCuboid(-1.0F, 0.0F, -3.0F, 1, 4, 6);
		this.leftWing.setPivot(4.0F, 13.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.child) {
			float f = 2.0F;
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 5.0F * scale, 2.0F * scale);
			this.head.render(scale);
			this.beak.render(scale);
			this.wattle.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.torso.render(scale);
			this.rightLeg.render(scale);
			this.leftLeg.render(scale);
			this.rightWing.render(scale);
			this.leftWing.render(scale);
			GlStateManager.popMatrix();
		} else {
			this.head.render(scale);
			this.beak.render(scale);
			this.wattle.render(scale);
			this.torso.render(scale);
			this.rightLeg.render(scale);
			this.leftLeg.render(scale);
			this.rightWing.render(scale);
			this.leftWing.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.head.posX = headPitch * (float) (Math.PI / 180.0);
		this.head.posY = age * (float) (Math.PI / 180.0);
		this.beak.posX = this.head.posX;
		this.beak.posY = this.head.posY;
		this.wattle.posX = this.head.posX;
		this.wattle.posY = this.head.posY;
		this.torso.posX = (float) (Math.PI / 2);
		this.rightLeg.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
		this.leftLeg.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
		this.rightWing.posZ = tickDelta;
		this.leftWing.posZ = -tickDelta;
	}
}
