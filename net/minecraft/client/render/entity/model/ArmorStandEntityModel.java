package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;

public class ArmorStandEntityModel extends ArmorStandArmorEntityModel {
	public ModelPart rightTorso;
	public ModelPart leftTorso;
	public ModelPart hip;
	public ModelPart plate;

	public ArmorStandEntityModel() {
		this(0.0F);
	}

	public ArmorStandEntityModel(float f) {
		super(f, 64, 64);
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-1.0F, -7.0F, -1.0F, 2, 7, 2, f);
		this.head.setPivot(0.0F, 0.0F, 0.0F);
		this.body = new ModelPart(this, 0, 26);
		this.body.addCuboid(-6.0F, 0.0F, -1.5F, 12, 3, 3, f);
		this.body.setPivot(0.0F, 0.0F, 0.0F);
		this.rightArm = new ModelPart(this, 24, 0);
		this.rightArm.addCuboid(-2.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		this.leftArm = new ModelPart(this, 32, 16);
		this.leftArm.mirror = true;
		this.leftArm.addCuboid(0.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
		this.rightLeg = new ModelPart(this, 8, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 40, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.rightTorso = new ModelPart(this, 16, 0);
		this.rightTorso.addCuboid(-3.0F, 3.0F, -1.0F, 2, 7, 2, f);
		this.rightTorso.setPivot(0.0F, 0.0F, 0.0F);
		this.rightTorso.visible = true;
		this.leftTorso = new ModelPart(this, 48, 16);
		this.leftTorso.addCuboid(1.0F, 3.0F, -1.0F, 2, 7, 2, f);
		this.leftTorso.setPivot(0.0F, 0.0F, 0.0F);
		this.hip = new ModelPart(this, 0, 48);
		this.hip.addCuboid(-4.0F, 10.0F, -1.0F, 8, 2, 2, f);
		this.hip.setPivot(0.0F, 0.0F, 0.0F);
		this.plate = new ModelPart(this, 0, 32);
		this.plate.addCuboid(-6.0F, 11.0F, -6.0F, 12, 1, 12, f);
		this.plate.setPivot(0.0F, 12.0F, 0.0F);
		this.hat.visible = false;
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (entity instanceof ArmorStandEntity) {
			ArmorStandEntity armorStandEntity = (ArmorStandEntity)entity;
			this.leftArm.visible = armorStandEntity.shouldShowArms();
			this.rightArm.visible = armorStandEntity.shouldShowArms();
			this.plate.visible = !armorStandEntity.hasNoBasePlate();
			this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
			this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
			this.rightTorso.posX = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getPitch();
			this.rightTorso.posY = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getYaw();
			this.rightTorso.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getRoll();
			this.leftTorso.posX = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getPitch();
			this.leftTorso.posY = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getYaw();
			this.leftTorso.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getRoll();
			this.hip.posX = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getPitch();
			this.hip.posY = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getYaw();
			this.hip.posZ = (float) (Math.PI / 180.0) * armorStandEntity.getBodyAngle().getRoll();
			this.plate.posX = 0.0F;
			this.plate.posY = (float) (Math.PI / 180.0) * -entity.yaw;
			this.plate.posZ = 0.0F;
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		GlStateManager.pushMatrix();
		if (this.child) {
			float f = 2.0F;
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			this.rightTorso.render(scale);
			this.leftTorso.render(scale);
			this.hip.render(scale);
			this.plate.render(scale);
		} else {
			if (entity.isSneaking()) {
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}

			this.rightTorso.render(scale);
			this.leftTorso.render(scale);
			this.hip.render(scale);
			this.plate.render(scale);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void method_12221(float f, HandOption handOption) {
		ModelPart modelPart = this.method_12223(handOption);
		boolean bl = modelPart.visible;
		modelPart.visible = true;
		super.method_12221(f, handOption);
		modelPart.visible = bl;
	}
}
