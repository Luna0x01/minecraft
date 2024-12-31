package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;

public class IronGolemEntityModel extends EntityModel {
	public ModelPart field_1551;
	public ModelPart field_1552;
	public ModelPart field_1553;
	public ModelPart field_1554;
	public ModelPart field_1555;
	public ModelPart field_1556;

	public IronGolemEntityModel() {
		this(0.0F);
	}

	public IronGolemEntityModel(float f) {
		this(f, -7.0F);
	}

	public IronGolemEntityModel(float f, float g) {
		int i = 128;
		int j = 128;
		this.field_1551 = new ModelPart(this).setTextureSize(i, j);
		this.field_1551.setPivot(0.0F, 0.0F + g, -2.0F);
		this.field_1551.setTextureOffset(0, 0).addCuboid(-4.0F, -12.0F, -5.5F, 8, 10, 8, f);
		this.field_1551.setTextureOffset(24, 0).addCuboid(-1.0F, -5.0F, -7.5F, 2, 4, 2, f);
		this.field_1552 = new ModelPart(this).setTextureSize(i, j);
		this.field_1552.setPivot(0.0F, 0.0F + g, 0.0F);
		this.field_1552.setTextureOffset(0, 40).addCuboid(-9.0F, -2.0F, -6.0F, 18, 12, 11, f);
		this.field_1552.setTextureOffset(0, 70).addCuboid(-4.5F, 10.0F, -3.0F, 9, 5, 6, f + 0.5F);
		this.field_1553 = new ModelPart(this).setTextureSize(i, j);
		this.field_1553.setPivot(0.0F, -7.0F, 0.0F);
		this.field_1553.setTextureOffset(60, 21).addCuboid(-13.0F, -2.5F, -3.0F, 4, 30, 6, f);
		this.field_1554 = new ModelPart(this).setTextureSize(i, j);
		this.field_1554.setPivot(0.0F, -7.0F, 0.0F);
		this.field_1554.setTextureOffset(60, 58).addCuboid(9.0F, -2.5F, -3.0F, 4, 30, 6, f);
		this.field_1555 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_1555.setPivot(-4.0F, 18.0F + g, 0.0F);
		this.field_1555.setTextureOffset(37, 0).addCuboid(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
		this.field_1556 = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.field_1556.mirror = true;
		this.field_1556.setTextureOffset(60, 0).setPivot(5.0F, 18.0F + g, 0.0F);
		this.field_1556.addCuboid(-3.5F, -3.0F, -3.0F, 6, 16, 5, f);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1551.render(scale);
		this.field_1552.render(scale);
		this.field_1555.render(scale);
		this.field_1556.render(scale);
		this.field_1553.render(scale);
		this.field_1554.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_1551.posY = age * (float) (Math.PI / 180.0);
		this.field_1551.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_1555.posX = -1.5F * this.method_1182(handSwing, 13.0F) * handSwingAmount;
		this.field_1556.posX = 1.5F * this.method_1182(handSwing, 13.0F) * handSwingAmount;
		this.field_1555.posY = 0.0F;
		this.field_1556.posY = 0.0F;
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		IronGolemEntity ironGolemEntity = (IronGolemEntity)entity;
		int i = ironGolemEntity.getAttackTicksLeft();
		if (i > 0) {
			this.field_1553.posX = -2.0F + 1.5F * this.method_1182((float)i - tickDelta, 10.0F);
			this.field_1554.posX = -2.0F + 1.5F * this.method_1182((float)i - tickDelta, 10.0F);
		} else {
			int j = ironGolemEntity.getLookingAtVillagerTicks();
			if (j > 0) {
				this.field_1553.posX = -0.8F + 0.025F * this.method_1182((float)j, 70.0F);
				this.field_1554.posX = 0.0F;
			} else {
				this.field_1553.posX = (-0.2F + 1.5F * this.method_1182(limbAngle, 13.0F)) * limbDistance;
				this.field_1554.posX = (-0.2F - 1.5F * this.method_1182(limbAngle, 13.0F)) * limbDistance;
			}
		}
	}

	private float method_1182(float f, float g) {
		return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
	}
}
