package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;

public class SheepEntityModel extends QuadruPedEntityModel {
	private float field_1517;

	public SheepEntityModel() {
		super(12, 0.0F);
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-3.0F, -4.0F, -6.0F, 6, 6, 8, 0.0F);
		this.head.setPivot(0.0F, 6.0F, -8.0F);
		this.torso = new ModelPart(this, 28, 8);
		this.torso.addCuboid(-4.0F, -10.0F, -7.0F, 8, 16, 6, 0.0F);
		this.torso.setPivot(0.0F, 5.0F, 2.0F);
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
		this.head.pivotY = 6.0F + ((SheepEntity)entity).method_2864(tickDelta) * 9.0F;
		this.field_1517 = ((SheepEntity)entity).method_2865(tickDelta);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.head.posX = this.field_1517;
	}
}
