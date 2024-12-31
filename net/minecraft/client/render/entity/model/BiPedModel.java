package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class BiPedModel extends EntityModel {
	public ModelPart head;
	public ModelPart hat;
	public ModelPart body;
	public ModelPart rightArm;
	public ModelPart leftArm;
	public ModelPart rightLeg;
	public ModelPart leftLeg;
	public BiPedModel.class_2850 field_13384 = BiPedModel.class_2850.EMPTY;
	public BiPedModel.class_2850 field_13385 = BiPedModel.class_2850.EMPTY;
	public boolean sneaking;

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
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
			this.head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
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
		boolean bl = entity instanceof LivingEntity && ((LivingEntity)entity).method_13056() > 4;
		this.head.posY = age * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.posX = (float) (-Math.PI / 4);
		} else {
			this.head.posX = headPitch * (float) (Math.PI / 180.0);
		}

		this.body.posY = 0.0F;
		this.rightArm.pivotZ = 0.0F;
		this.rightArm.pivotX = -5.0F;
		this.leftArm.pivotZ = 0.0F;
		this.leftArm.pivotX = 5.0F;
		float f = 1.0F;
		if (bl) {
			f = (float)(entity.velocityX * entity.velocityX + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ);
			f /= 0.2F;
			f *= f * f;
		}

		if (f < 1.0F) {
			f = 1.0F;
		}

		this.rightArm.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 2.0F * handSwingAmount * 0.5F / f;
		this.leftArm.posX = MathHelper.cos(handSwing * 0.6662F) * 2.0F * handSwingAmount * 0.5F / f;
		this.rightArm.posZ = 0.0F;
		this.leftArm.posZ = 0.0F;
		this.rightLeg.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount / f;
		this.leftLeg.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount / f;
		this.rightLeg.posY = 0.0F;
		this.leftLeg.posY = 0.0F;
		this.rightLeg.posZ = 0.0F;
		this.leftLeg.posZ = 0.0F;
		if (this.riding) {
			this.rightArm.posX += (float) (-Math.PI / 5);
			this.leftArm.posX += (float) (-Math.PI / 5);
			this.rightLeg.posX = -1.4137167F;
			this.rightLeg.posY = (float) (Math.PI / 10);
			this.rightLeg.posZ = 0.07853982F;
			this.leftLeg.posX = -1.4137167F;
			this.leftLeg.posY = (float) (-Math.PI / 10);
			this.leftLeg.posZ = -0.07853982F;
		}

		this.rightArm.posY = 0.0F;
		this.rightArm.posZ = 0.0F;
		switch (this.field_13384) {
			case EMPTY:
				this.leftArm.posY = 0.0F;
				break;
			case BLOCK:
				this.leftArm.posX = this.leftArm.posX * 0.5F - 0.9424779F;
				this.leftArm.posY = (float) (Math.PI / 6);
				break;
			case ITEM:
				this.leftArm.posX = this.leftArm.posX * 0.5F - (float) (Math.PI / 10);
				this.leftArm.posY = 0.0F;
		}

		switch (this.field_13385) {
			case EMPTY:
				this.rightArm.posY = 0.0F;
				break;
			case BLOCK:
				this.rightArm.posX = this.rightArm.posX * 0.5F - 0.9424779F;
				this.rightArm.posY = (float) (-Math.PI / 6);
				break;
			case ITEM:
				this.rightArm.posX = this.rightArm.posX * 0.5F - (float) (Math.PI / 10);
				this.rightArm.posY = 0.0F;
		}

		if (this.handSwingProgress > 0.0F) {
			HandOption handOption = this.method_12222(entity);
			ModelPart modelPart = this.method_12223(handOption);
			float g = this.handSwingProgress;
			this.body.posY = MathHelper.sin(MathHelper.sqrt(g) * (float) (Math.PI * 2)) * 0.2F;
			if (handOption == HandOption.LEFT) {
				this.body.posY *= -1.0F;
			}

			this.rightArm.pivotZ = MathHelper.sin(this.body.posY) * 5.0F;
			this.rightArm.pivotX = -MathHelper.cos(this.body.posY) * 5.0F;
			this.leftArm.pivotZ = -MathHelper.sin(this.body.posY) * 5.0F;
			this.leftArm.pivotX = MathHelper.cos(this.body.posY) * 5.0F;
			this.rightArm.posY = this.rightArm.posY + this.body.posY;
			this.leftArm.posY = this.leftArm.posY + this.body.posY;
			this.leftArm.posX = this.leftArm.posX + this.body.posY;
			g = 1.0F - this.handSwingProgress;
			g *= g;
			g *= g;
			g = 1.0F - g;
			float h = MathHelper.sin(g * (float) Math.PI);
			float i = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.posX - 0.7F) * 0.75F;
			modelPart.posX = (float)((double)modelPart.posX - ((double)h * 1.2 + (double)i));
			modelPart.posY = modelPart.posY + this.body.posY * 2.0F;
			modelPart.posZ = modelPart.posZ + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
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
		if (this.field_13385 == BiPedModel.class_2850.BOW_AND_ARROW) {
			this.rightArm.posY = -0.1F + this.head.posY;
			this.leftArm.posY = 0.1F + this.head.posY + 0.4F;
			this.rightArm.posX = (float) (-Math.PI / 2) + this.head.posX;
			this.leftArm.posX = (float) (-Math.PI / 2) + this.head.posX;
		} else if (this.field_13384 == BiPedModel.class_2850.BOW_AND_ARROW) {
			this.rightArm.posY = -0.1F + this.head.posY - 0.4F;
			this.leftArm.posY = 0.1F + this.head.posY;
			this.rightArm.posX = (float) (-Math.PI / 2) + this.head.posX;
			this.leftArm.posX = (float) (-Math.PI / 2) + this.head.posX;
		}

		copyModelPart(this.head, this.hat);
	}

	@Override
	public void copy(EntityModel model) {
		super.copy(model);
		if (model instanceof BiPedModel) {
			BiPedModel biPedModel = (BiPedModel)model;
			this.field_13384 = biPedModel.field_13384;
			this.field_13385 = biPedModel.field_13385;
			this.sneaking = biPedModel.sneaking;
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

	public void method_12221(float f, HandOption handOption) {
		this.method_12223(handOption).preRender(f);
	}

	protected ModelPart method_12223(HandOption handOption) {
		return handOption == HandOption.LEFT ? this.leftArm : this.rightArm;
	}

	protected HandOption method_12222(Entity entity) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			HandOption handOption = livingEntity.getDurability();
			return livingEntity.mainHand == Hand.MAIN_HAND ? handOption : handOption.method_13037();
		} else {
			return HandOption.RIGHT;
		}
	}

	public static enum class_2850 {
		EMPTY,
		ITEM,
		BLOCK,
		BOW_AND_ARROW;
	}
}
