package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;

public class RabbitEntityModel extends EntityModel {
	ModelPart field_10568;
	ModelPart field_10569;
	ModelPart field_10570;
	ModelPart field_10571;
	ModelPart field_10572;
	ModelPart field_10573;
	ModelPart field_10574;
	ModelPart field_10575;
	ModelPart field_10576;
	ModelPart field_10577;
	ModelPart field_10578;
	ModelPart field_10579;
	private float field_10580 = 0.0F;
	private float field_10581 = 0.0F;

	public RabbitEntityModel() {
		this.putTexture("head.main", 0, 0);
		this.putTexture("head.nose", 0, 24);
		this.putTexture("head.ear1", 0, 10);
		this.putTexture("head.ear2", 6, 10);
		this.field_10568 = new ModelPart(this, 26, 24);
		this.field_10568.addCuboid(-1.0F, 5.5F, -3.7F, 2, 1, 7);
		this.field_10568.setPivot(3.0F, 17.5F, 3.7F);
		this.field_10568.mirror = true;
		this.method_9645(this.field_10568, 0.0F, 0.0F, 0.0F);
		this.field_10569 = new ModelPart(this, 8, 24);
		this.field_10569.addCuboid(-1.0F, 5.5F, -3.7F, 2, 1, 7);
		this.field_10569.setPivot(-3.0F, 17.5F, 3.7F);
		this.field_10569.mirror = true;
		this.method_9645(this.field_10569, 0.0F, 0.0F, 0.0F);
		this.field_10570 = new ModelPart(this, 30, 15);
		this.field_10570.addCuboid(-1.0F, 0.0F, 0.0F, 2, 4, 5);
		this.field_10570.setPivot(3.0F, 17.5F, 3.7F);
		this.field_10570.mirror = true;
		this.method_9645(this.field_10570, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_10571 = new ModelPart(this, 16, 15);
		this.field_10571.addCuboid(-1.0F, 0.0F, 0.0F, 2, 4, 5);
		this.field_10571.setPivot(-3.0F, 17.5F, 3.7F);
		this.field_10571.mirror = true;
		this.method_9645(this.field_10571, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_10572 = new ModelPart(this, 0, 0);
		this.field_10572.addCuboid(-3.0F, -2.0F, -10.0F, 6, 5, 10);
		this.field_10572.setPivot(0.0F, 19.0F, 8.0F);
		this.field_10572.mirror = true;
		this.method_9645(this.field_10572, (float) (-Math.PI / 9), 0.0F, 0.0F);
		this.field_10573 = new ModelPart(this, 8, 15);
		this.field_10573.addCuboid(-1.0F, 0.0F, -1.0F, 2, 7, 2);
		this.field_10573.setPivot(3.0F, 17.0F, -1.0F);
		this.field_10573.mirror = true;
		this.method_9645(this.field_10573, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.field_10574 = new ModelPart(this, 0, 15);
		this.field_10574.addCuboid(-1.0F, 0.0F, -1.0F, 2, 7, 2);
		this.field_10574.setPivot(-3.0F, 17.0F, -1.0F);
		this.field_10574.mirror = true;
		this.method_9645(this.field_10574, (float) (-Math.PI / 18), 0.0F, 0.0F);
		this.field_10575 = new ModelPart(this, 32, 0);
		this.field_10575.addCuboid(-2.5F, -4.0F, -5.0F, 5, 4, 5);
		this.field_10575.setPivot(0.0F, 16.0F, -1.0F);
		this.field_10575.mirror = true;
		this.method_9645(this.field_10575, 0.0F, 0.0F, 0.0F);
		this.field_10576 = new ModelPart(this, 52, 0);
		this.field_10576.addCuboid(-2.5F, -9.0F, -1.0F, 2, 5, 1);
		this.field_10576.setPivot(0.0F, 16.0F, -1.0F);
		this.field_10576.mirror = true;
		this.method_9645(this.field_10576, 0.0F, (float) (-Math.PI / 12), 0.0F);
		this.field_10577 = new ModelPart(this, 58, 0);
		this.field_10577.addCuboid(0.5F, -9.0F, -1.0F, 2, 5, 1);
		this.field_10577.setPivot(0.0F, 16.0F, -1.0F);
		this.field_10577.mirror = true;
		this.method_9645(this.field_10577, 0.0F, (float) (Math.PI / 12), 0.0F);
		this.field_10578 = new ModelPart(this, 52, 6);
		this.field_10578.addCuboid(-1.5F, -1.5F, 0.0F, 3, 3, 2);
		this.field_10578.setPivot(0.0F, 20.0F, 7.0F);
		this.field_10578.mirror = true;
		this.method_9645(this.field_10578, -0.3490659F, 0.0F, 0.0F);
		this.field_10579 = new ModelPart(this, 32, 9);
		this.field_10579.addCuboid(-0.5F, -2.5F, -5.5F, 1, 1, 1);
		this.field_10579.setPivot(0.0F, 16.0F, -1.0F);
		this.field_10579.mirror = true;
		this.method_9645(this.field_10579, 0.0F, 0.0F, 0.0F);
	}

	private void method_9645(ModelPart modelPart, float f, float g, float h) {
		modelPart.posX = f;
		modelPart.posY = g;
		modelPart.posZ = h;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		if (this.child) {
			float f = 1.5F;
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.85F / f, 0.85F / f, 0.85F / f);
			GlStateManager.translate(0.0F, 22.0F * scale, 2.0F * scale);
			this.field_10575.render(scale);
			this.field_10577.render(scale);
			this.field_10576.render(scale);
			this.field_10579.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6F / f, 0.6F / f, 0.6F / f);
			GlStateManager.translate(0.0F, 36.0F * scale, 0.0F);
			this.field_10568.render(scale);
			this.field_10569.render(scale);
			this.field_10570.render(scale);
			this.field_10571.render(scale);
			this.field_10572.render(scale);
			this.field_10573.render(scale);
			this.field_10574.render(scale);
			this.field_10578.render(scale);
			GlStateManager.popMatrix();
		} else {
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.6F, 0.6F, 0.6F);
			GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
			this.field_10568.render(scale);
			this.field_10569.render(scale);
			this.field_10570.render(scale);
			this.field_10571.render(scale);
			this.field_10572.render(scale);
			this.field_10573.render(scale);
			this.field_10574.render(scale);
			this.field_10575.render(scale);
			this.field_10576.render(scale);
			this.field_10577.render(scale);
			this.field_10578.render(scale);
			this.field_10579.render(scale);
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		float f = tickDelta - (float)entity.ticksAlive;
		RabbitEntity rabbitEntity = (RabbitEntity)entity;
		this.field_10579.posX = this.field_10575.posX = this.field_10576.posX = this.field_10577.posX = headPitch * (float) (Math.PI / 180.0);
		this.field_10579.posY = this.field_10575.posY = age * (float) (Math.PI / 180.0);
		this.field_10576.posY = this.field_10579.posY - (float) (Math.PI / 12);
		this.field_10577.posY = this.field_10579.posY + (float) (Math.PI / 12);
		this.field_10580 = MathHelper.sin(rabbitEntity.getJumpProgress(f) * (float) Math.PI);
		this.field_10570.posX = this.field_10571.posX = (this.field_10580 * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
		this.field_10568.posX = this.field_10569.posX = this.field_10580 * 50.0F * (float) (Math.PI / 180.0);
		this.field_10573.posX = this.field_10574.posX = (this.field_10580 * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
		this.field_10580 = MathHelper.sin(((RabbitEntity)entity).getJumpProgress(tickDelta) * (float) Math.PI);
	}
}
