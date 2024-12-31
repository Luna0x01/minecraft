package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;

public class BatEntityModel extends EntityModel {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightWing;
	private final ModelPart leftWing;
	private final ModelPart rightWingTip;
	private final ModelPart leftWingTip;

	public BatEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.0F, -3.0F, -3.0F, 6, 6, 6);
		ModelPart modelPart = new ModelPart(this, 24, 0);
		modelPart.addCuboid(-4.0F, -6.0F, -2.0F, 3, 4, 1);
		this.head.add(modelPart);
		ModelPart modelPart2 = new ModelPart(this, 24, 0);
		modelPart2.mirror = true;
		modelPart2.addCuboid(1.0F, -6.0F, -2.0F, 3, 4, 1);
		this.head.add(modelPart2);
		this.body = new ModelPart(this, 0, 16);
		this.body.addCuboid(-3.0F, 4.0F, -3.0F, 6, 12, 6);
		this.body.setTextureOffset(0, 34).addCuboid(-5.0F, 16.0F, 0.0F, 10, 6, 1);
		this.rightWing = new ModelPart(this, 42, 0);
		this.rightWing.addCuboid(-12.0F, 1.0F, 1.5F, 10, 16, 1);
		this.rightWingTip = new ModelPart(this, 24, 16);
		this.rightWingTip.setPivot(-12.0F, 1.0F, 1.5F);
		this.rightWingTip.addCuboid(-8.0F, 1.0F, 0.0F, 8, 12, 1);
		this.leftWing = new ModelPart(this, 42, 0);
		this.leftWing.mirror = true;
		this.leftWing.addCuboid(2.0F, 1.0F, 1.5F, 10, 16, 1);
		this.leftWingTip = new ModelPart(this, 24, 16);
		this.leftWingTip.mirror = true;
		this.leftWingTip.setPivot(12.0F, 1.0F, 1.5F);
		this.leftWingTip.addCuboid(0.0F, 1.0F, 0.0F, 8, 12, 1);
		this.body.add(this.rightWing);
		this.body.add(this.leftWing);
		this.rightWing.add(this.rightWingTip);
		this.leftWing.add(this.leftWingTip);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.head.render(scale);
		this.body.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		if (((BatEntity)entity).isRoosting()) {
			this.head.posX = headPitch * (float) (Math.PI / 180.0);
			this.head.posY = (float) Math.PI - age * (float) (Math.PI / 180.0);
			this.head.posZ = (float) Math.PI;
			this.head.setPivot(0.0F, -2.0F, 0.0F);
			this.rightWing.setPivot(-3.0F, 0.0F, 3.0F);
			this.leftWing.setPivot(3.0F, 0.0F, 3.0F);
			this.body.posX = (float) Math.PI;
			this.rightWing.posX = (float) (-Math.PI / 20);
			this.rightWing.posY = (float) (-Math.PI * 2.0 / 5.0);
			this.rightWingTip.posY = -1.7278761F;
			this.leftWing.posX = this.rightWing.posX;
			this.leftWing.posY = -this.rightWing.posY;
			this.leftWingTip.posY = -this.rightWingTip.posY;
		} else {
			this.head.posX = headPitch * (float) (Math.PI / 180.0);
			this.head.posY = age * (float) (Math.PI / 180.0);
			this.head.posZ = 0.0F;
			this.head.setPivot(0.0F, 0.0F, 0.0F);
			this.rightWing.setPivot(0.0F, 0.0F, 0.0F);
			this.leftWing.setPivot(0.0F, 0.0F, 0.0F);
			this.body.posX = (float) (Math.PI / 4) + MathHelper.cos(tickDelta * 0.1F) * 0.15F;
			this.body.posY = 0.0F;
			this.rightWing.posY = MathHelper.cos(tickDelta * 1.3F) * (float) Math.PI * 0.25F;
			this.leftWing.posY = -this.rightWing.posY;
			this.rightWingTip.posY = this.rightWing.posY * 0.5F;
			this.leftWingTip.posY = -this.rightWing.posY * 0.5F;
		}
	}
}
