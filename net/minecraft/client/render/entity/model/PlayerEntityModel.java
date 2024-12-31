package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class PlayerEntityModel extends BiPedModel {
	public ModelPart leftSleeve;
	public ModelPart rightSleeve;
	public ModelPart leftPants;
	public ModelPart rightPants;
	public ModelPart jacket;
	private ModelPart cloak;
	private ModelPart ear;
	private boolean thinArms;

	public PlayerEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 64);
		this.thinArms = bl;
		this.ear = new ModelPart(this, 24, 0);
		this.ear.addCuboid(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
		this.cloak = new ModelPart(this, 0, 0);
		this.cloak.setTextureSize(64, 32);
		this.cloak.addCuboid(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
		if (bl) {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.leftArm.setPivot(5.0F, 2.5F, 0.0F);
			this.rightArm = new ModelPart(this, 40, 16);
			this.rightArm.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.rightArm.setPivot(-5.0F, 2.5F, 0.0F);
			this.leftSleeve = new ModelPart(this, 48, 48);
			this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.leftSleeve.setPivot(5.0F, 2.5F, 0.0F);
			this.rightSleeve = new ModelPart(this, 40, 32);
			this.rightSleeve.addCuboid(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.rightSleeve.setPivot(-5.0F, 2.5F, 10.0F);
		} else {
			this.leftArm = new ModelPart(this, 32, 48);
			this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.leftSleeve = new ModelPart(this, 48, 48);
			this.leftSleeve.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.leftSleeve.setPivot(5.0F, 2.0F, 0.0F);
			this.rightSleeve = new ModelPart(this, 40, 32);
			this.rightSleeve.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.rightSleeve.setPivot(-5.0F, 2.0F, 10.0F);
		}

		this.leftLeg = new ModelPart(this, 16, 48);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.leftPants = new ModelPart(this, 0, 48);
		this.leftPants.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.leftPants.setPivot(1.9F, 12.0F, 0.0F);
		this.rightPants = new ModelPart(this, 0, 32);
		this.rightPants.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.rightPants.setPivot(-1.9F, 12.0F, 0.0F);
		this.jacket = new ModelPart(this, 16, 32);
		this.jacket.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
		this.jacket.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		GlStateManager.pushMatrix();
		if (this.child) {
			float f = 2.0F;
			GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.leftPants.render(scale);
			this.rightPants.render(scale);
			this.leftSleeve.render(scale);
			this.rightSleeve.render(scale);
			this.jacket.render(scale);
		} else {
			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			this.leftPants.render(scale);
			this.rightPants.render(scale);
			this.leftSleeve.render(scale);
			this.rightSleeve.render(scale);
			this.jacket.render(scale);
		}

		GlStateManager.popMatrix();
	}

	public void renderEars(float scale) {
		copyModelPart(this.head, this.ear);
		this.ear.pivotX = 0.0F;
		this.ear.pivotY = 0.0F;
		this.ear.render(scale);
	}

	public void renderCape(float scale) {
		this.cloak.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		copyModelPart(this.leftLeg, this.leftPants);
		copyModelPart(this.rightLeg, this.rightPants);
		copyModelPart(this.leftArm, this.leftSleeve);
		copyModelPart(this.rightArm, this.rightSleeve);
		copyModelPart(this.body, this.jacket);
		if (entity.isSneaking()) {
			this.cloak.pivotY = 2.0F;
		} else {
			this.cloak.pivotY = 0.0F;
		}
	}

	public void renderRightArm() {
		this.rightArm.render(0.0625F);
		this.rightSleeve.render(0.0625F);
	}

	public void renderLeftArm() {
		this.leftArm.render(0.0625F);
		this.leftSleeve.render(0.0625F);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPants.visible = visible;
		this.rightPants.visible = visible;
		this.jacket.visible = visible;
		this.cloak.visible = visible;
		this.ear.visible = visible;
	}

	@Override
	public void setArmAngle(float angle) {
		if (this.thinArms) {
			this.rightArm.pivotX++;
			this.rightArm.preRender(angle);
			this.rightArm.pivotX--;
		} else {
			this.rightArm.preRender(angle);
		}
	}
}
