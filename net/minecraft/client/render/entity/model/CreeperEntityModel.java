package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CreeperEntityModel extends EntityModel {
	private final ModelPart head;
	private final ModelPart helmet;
	private final ModelPart torso;
	private final ModelPart rightBackLeg;
	private final ModelPart leftBackLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;

	public CreeperEntityModel() {
		this(0.0F);
	}

	public CreeperEntityModel(float f) {
		int i = 6;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.head.setPivot(0.0F, 6.0F, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.helmet.setPivot(0.0F, 6.0F, 0.0F);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.torso.setPivot(0.0F, 6.0F, 0.0F);
		this.rightBackLeg = new ModelPart(this, 0, 16);
		this.rightBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.rightBackLeg.setPivot(-2.0F, 18.0F, 4.0F);
		this.leftBackLeg = new ModelPart(this, 0, 16);
		this.leftBackLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.leftBackLeg.setPivot(2.0F, 18.0F, 4.0F);
		this.rightFrontLeg = new ModelPart(this, 0, 16);
		this.rightFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.rightFrontLeg.setPivot(-2.0F, 18.0F, -4.0F);
		this.leftFrontLeg = new ModelPart(this, 0, 16);
		this.leftFrontLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4, 6, 4, f);
		this.leftFrontLeg.setPivot(2.0F, 18.0F, -4.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.head.render(scale);
		this.torso.render(scale);
		this.rightBackLeg.render(scale);
		this.leftBackLeg.render(scale);
		this.rightFrontLeg.render(scale);
		this.leftFrontLeg.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.head.posY = age * (float) (Math.PI / 180.0);
		this.head.posX = headPitch * (float) (Math.PI / 180.0);
		this.rightBackLeg.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
		this.leftBackLeg.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
		this.rightFrontLeg.posX = MathHelper.cos(handSwing * 0.6662F + (float) Math.PI) * 1.4F * handSwingAmount;
		this.leftFrontLeg.posX = MathHelper.cos(handSwing * 0.6662F) * 1.4F * handSwingAmount;
	}
}
