package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class BiPedModel extends EntityModel {
	public ModelPart head;
	public ModelPart hat;
	public ModelPart body;
	public ModelPart rightArm;
	public ModelPart leftArm;
	public ModelPart rightLeg;
	public ModelPart leftLeg;
	public int leftArmPose;
	public int rightArmPose;
	public boolean sneaking;
	public boolean aiming;

	public BiPedModel() {
		this(0.0F);
	}

	public BiPedModel(float f) {
		this(f, 0.0F, 64, 32);
	}

	public BiPedModel(float f, float g, int i, int j) {
		this.textureWidth = i;
		this.textureHeight = j;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.head.setPivot(0.0F, 0.0F + g, 0.0F);
		this.hat = new ModelPart(this, 32, 0);
		this.hat.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.hat.setPivot(0.0F, 0.0F + g, 0.0F);
		this.body = new ModelPart(this, 16, 16);
		this.body.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.body.setPivot(0.0F, 0.0F + g, 0.0F);
		this.rightArm = new ModelPart(this, 40, 16);
		this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.rightArm.setPivot(-5.0F, 2.0F + g, 0.0F);
		this.leftArm = new ModelPart(this, 40, 16);
		this.leftArm.mirror = true;
		this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.leftArm.setPivot(5.0F, 2.0F + g, 0.0F);
		this.rightLeg = new ModelPart(this, 0, 16);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.rightLeg.setPivot(-1.9F, 12.0F + g, 0.0F);
		this.leftLeg = new ModelPart(this, 0, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.leftLeg.setPivot(1.9F, 12.0F + g, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		GlStateManager.pushMatrix();
		if (this.child) {
			float f = 2.0F;
			GlStateManager.scale(1.5F / f, 1.5F / f, 1.5F / f);
			GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
			this.head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.body.render(scale);
			this.rightArm.render(scale);
			this.leftArm.render(scale);
			this.rightLeg.render(scale);
			this.leftLeg.render(scale);
			this.hat.render(scale);
		} else {
			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			this.head.render(scale);
			this.body.render(scale);
			this.rightArm.render(scale);
			this.leftArm.render(scale);
			this.rightLeg.render(scale);
			this.leftLeg.render(scale);
			this.hat.render(scale);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.head.posY = age / (180.0F / (float)Math.PI);
		this.head.posX = headPitch / (180.0F / (float)Math.PI);
		this.rightArm.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 2.0F * handSwingAmount * 0.5F;
		this.leftArm.posX = MathHelper.cos(handSwing * 0.6662F) * 2.0F * handSwingAmount * 0.5F;
		this.rightArm.posZ = 0.0F;
		this.leftArm.posZ = 0.0F;
		this.rightLeg.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
		this.leftLeg.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
		this.rightLeg.posY = 0.0F;
		this.leftLeg.posY = 0.0F;
		if (this.riding) {
			this.rightArm.posX += (float) (-Math.PI / 5);
			this.leftArm.posX += (float) (-Math.PI / 5);
			this.rightLeg.posX = (float) (-Math.PI * 2.0 / 5.0);
			this.leftLeg.posX = (float) (-Math.PI * 2.0 / 5.0);
			this.rightLeg.posY = (float) (Math.PI / 10);
			this.leftLeg.posY = (float) (-Math.PI / 10);
		}

		if (this.leftArmPose != 0) {
			this.leftArm.posX = this.leftArm.posX * 0.5F - (float) (Math.PI / 10) * (float)this.leftArmPose;
		}

		this.rightArm.posY = 0.0F;
		this.rightArm.posZ = 0.0F;
		switch (this.rightArmPose) {
			case 0:
			case 2:
			default:
				break;
			case 1:
				this.rightArm.posX = this.rightArm.posX * 0.5F - (float) (Math.PI / 10) * (float)this.rightArmPose;
				break;
			case 3:
				this.rightArm.posX = this.rightArm.posX * 0.5F - (float) (Math.PI / 10) * (float)this.rightArmPose;
				this.rightArm.posY = (float) (-Math.PI / 6);
		}

		this.leftArm.posY = 0.0F;
		if (this.handSwingProgress > -9990.0F) {
			float f = this.handSwingProgress;
			this.body.posY = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI * 2.0F) * 0.2F;
			this.rightArm.pivotZ = MathHelper.sin(this.body.posY) * 5.0F;
			this.rightArm.pivotX = -MathHelper.cos(this.body.posY) * 5.0F;
			this.leftArm.pivotZ = -MathHelper.sin(this.body.posY) * 5.0F;
			this.leftArm.pivotX = MathHelper.cos(this.body.posY) * 5.0F;
			this.rightArm.posY = this.rightArm.posY + this.body.posY;
			this.leftArm.posY = this.leftArm.posY + this.body.posY;
			this.leftArm.posX = this.leftArm.posX + this.body.posY;
			f = 1.0F - this.handSwingProgress;
			f *= f;
			f *= f;
			f = 1.0F - f;
			float g = MathHelper.sin(f * (float) Math.PI);
			float h = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.posX - 0.7F) * 0.75F;
			this.rightArm.posX = (float)((double)this.rightArm.posX - ((double)g * 1.2 + (double)h));
			this.rightArm.posY = this.rightArm.posY + this.body.posY * 2.0F;
			this.rightArm.posZ = this.rightArm.posZ + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
		}

		if (this.sneaking) {
			this.body.posX = 0.5F;
			this.rightArm.posX += 0.4F;
			this.leftArm.posX += 0.4F;
			this.rightLeg.pivotZ = 4.0F;
			this.leftLeg.pivotZ = 4.0F;
			this.rightLeg.pivotY = 9.0F;
			this.leftLeg.pivotY = 9.0F;
			this.head.pivotY = 1.0F;
		} else {
			this.body.posX = 0.0F;
			this.rightLeg.pivotZ = 0.1F;
			this.leftLeg.pivotZ = 0.1F;
			this.rightLeg.pivotY = 12.0F;
			this.leftLeg.pivotY = 12.0F;
			this.head.pivotY = 0.0F;
		}

		this.rightArm.posZ = this.rightArm.posZ + MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F;
		this.leftArm.posZ = this.leftArm.posZ - (MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F);
		this.rightArm.posX = this.rightArm.posX + MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		this.leftArm.posX = this.leftArm.posX - MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		if (this.aiming) {
			float i = 0.0F;
			float j = 0.0F;
			this.rightArm.posZ = 0.0F;
			this.leftArm.posZ = 0.0F;
			this.rightArm.posY = -(0.1F - i * 0.6F) + this.head.posY;
			this.leftArm.posY = 0.1F - i * 0.6F + this.head.posY + 0.4F;
			this.rightArm.posX = (float) (-Math.PI / 2) + this.head.posX;
			this.leftArm.posX = (float) (-Math.PI / 2) + this.head.posX;
			this.rightArm.posX -= i * 1.2F - j * 0.4F;
			this.leftArm.posX -= i * 1.2F - j * 0.4F;
			this.rightArm.posZ = this.rightArm.posZ + MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F;
			this.leftArm.posZ = this.leftArm.posZ - (MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F);
			this.rightArm.posX = this.rightArm.posX + MathHelper.sin(tickDelta * 0.067F) * 0.05F;
			this.leftArm.posX = this.leftArm.posX - MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		}

		copyModelPart(this.head, this.hat);
	}

	@Override
	public void copy(EntityModel model) {
		super.copy(model);
		if (model instanceof BiPedModel) {
			BiPedModel biPedModel = (BiPedModel)model;
			this.leftArmPose = biPedModel.leftArmPose;
			this.rightArmPose = biPedModel.rightArmPose;
			this.sneaking = biPedModel.sneaking;
			this.aiming = biPedModel.aiming;
		}
	}

	public void setVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.body.visible = visible;
		this.rightArm.visible = visible;
		this.leftArm.visible = visible;
		this.rightLeg.visible = visible;
		this.leftLeg.visible = visible;
	}

	public void setArmAngle(float angle) {
		this.rightArm.preRender(angle);
	}
}
