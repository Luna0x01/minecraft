package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class WitchEntityModel extends VillagerEntityModel {
	public boolean field_5133;
	private final ModelPart field_5134 = new ModelPart(this).setTextureSize(64, 128);
	private final ModelPart field_5135;

	public WitchEntityModel(float f) {
		super(f, 0.0F, 64, 128);
		this.field_5134.setPivot(0.0F, -2.0F, 0.0F);
		this.field_5134.setTextureOffset(0, 0).addCuboid(0.0F, 3.0F, -6.75F, 1, 1, 1, -0.25F);
		this.field_5132.add(this.field_5134);
		this.field_5135 = new ModelPart(this).setTextureSize(64, 128);
		this.field_5135.setPivot(-5.0F, -10.03125F, -5.0F);
		this.field_5135.setTextureOffset(0, 64).addCuboid(0.0F, 0.0F, 0.0F, 10, 2, 10);
		this.field_1557.add(this.field_5135);
		ModelPart modelPart = new ModelPart(this).setTextureSize(64, 128);
		modelPart.setPivot(1.75F, -4.0F, 2.0F);
		modelPart.setTextureOffset(0, 76).addCuboid(0.0F, 0.0F, 0.0F, 7, 4, 7);
		modelPart.posX = -0.05235988F;
		modelPart.posZ = 0.02617994F;
		this.field_5135.add(modelPart);
		ModelPart modelPart2 = new ModelPart(this).setTextureSize(64, 128);
		modelPart2.setPivot(1.75F, -4.0F, 2.0F);
		modelPart2.setTextureOffset(0, 87).addCuboid(0.0F, 0.0F, 0.0F, 4, 4, 4);
		modelPart2.posX = -0.10471976F;
		modelPart2.posZ = 0.05235988F;
		modelPart.add(modelPart2);
		ModelPart modelPart3 = new ModelPart(this).setTextureSize(64, 128);
		modelPart3.setPivot(1.75F, -2.0F, 2.0F);
		modelPart3.setTextureOffset(0, 95).addCuboid(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.25F);
		modelPart3.posX = (float) (-Math.PI / 15);
		modelPart3.posZ = 0.10471976F;
		modelPart2.add(modelPart3);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_5132.offsetX = 0.0F;
		this.field_5132.offsetY = 0.0F;
		this.field_5132.offsetZ = 0.0F;
		float f = 0.01F * (float)(entity.getEntityId() % 10);
		this.field_5132.posX = MathHelper.sin((float)entity.ticksAlive * f) * 4.5F * (float) (Math.PI / 180.0);
		this.field_5132.posY = 0.0F;
		this.field_5132.posZ = MathHelper.cos((float)entity.ticksAlive * f) * 2.5F * (float) (Math.PI / 180.0);
		if (this.field_5133) {
			this.field_5132.posX = -0.9F;
			this.field_5132.offsetZ = -0.09375F;
			this.field_5132.offsetY = 0.1875F;
		}
	}
}
