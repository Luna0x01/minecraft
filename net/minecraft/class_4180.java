package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.AbstractHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class class_4180 extends EntityModel {
	protected ModelPart field_20511;
	protected ModelPart field_20512;
	private final ModelPart field_20513;
	private final ModelPart field_20514;
	private final ModelPart field_20515;
	private final ModelPart field_20516;
	private final ModelPart field_20517;
	private final ModelPart[] field_20518;
	private final ModelPart[] field_20519;

	public class_4180() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_20511 = new ModelPart(this, 0, 32);
		this.field_20511.addCuboid(-5.0F, -8.0F, -17.0F, 10, 10, 22, 0.05F);
		this.field_20511.setPivot(0.0F, 11.0F, 5.0F);
		this.field_20512 = new ModelPart(this, 0, 35);
		this.field_20512.addCuboid(-2.05F, -6.0F, -2.0F, 4, 12, 7);
		this.field_20512.posX = (float) (Math.PI / 6);
		ModelPart modelPart = new ModelPart(this, 0, 13);
		modelPart.addCuboid(-3.0F, -11.0F, -2.0F, 6, 5, 7);
		ModelPart modelPart2 = new ModelPart(this, 56, 36);
		modelPart2.addCuboid(-1.0F, -11.0F, 5.01F, 2, 16, 2);
		ModelPart modelPart3 = new ModelPart(this, 0, 25);
		modelPart3.addCuboid(-2.0F, -11.0F, -7.0F, 4, 5, 5);
		this.field_20512.add(modelPart);
		this.field_20512.add(modelPart2);
		this.field_20512.add(modelPart3);
		this.method_18901(this.field_20512);
		this.field_20513 = new ModelPart(this, 48, 21);
		this.field_20513.mirror = true;
		this.field_20513.addCuboid(-3.0F, -1.01F, -1.0F, 4, 11, 4);
		this.field_20513.setPivot(4.0F, 14.0F, 7.0F);
		this.field_20514 = new ModelPart(this, 48, 21);
		this.field_20514.addCuboid(-1.0F, -1.01F, -1.0F, 4, 11, 4);
		this.field_20514.setPivot(-4.0F, 14.0F, 7.0F);
		this.field_20515 = new ModelPart(this, 48, 21);
		this.field_20515.mirror = true;
		this.field_20515.addCuboid(-3.0F, -1.01F, -1.9F, 4, 11, 4);
		this.field_20515.setPivot(4.0F, 6.0F, -12.0F);
		this.field_20516 = new ModelPart(this, 48, 21);
		this.field_20516.addCuboid(-1.0F, -1.01F, -1.9F, 4, 11, 4);
		this.field_20516.setPivot(-4.0F, 6.0F, -12.0F);
		this.field_20517 = new ModelPart(this, 42, 36);
		this.field_20517.addCuboid(-1.5F, 0.0F, 0.0F, 3, 14, 4);
		this.field_20517.setPivot(0.0F, -5.0F, 2.0F);
		this.field_20517.posX = (float) (Math.PI / 6);
		this.field_20511.add(this.field_20517);
		ModelPart modelPart4 = new ModelPart(this, 26, 0);
		modelPart4.addCuboid(-5.0F, -8.0F, -9.0F, 10, 9, 9, 0.5F);
		this.field_20511.add(modelPart4);
		ModelPart modelPart5 = new ModelPart(this, 29, 5);
		modelPart5.addCuboid(2.0F, -9.0F, -6.0F, 1, 2, 2);
		this.field_20512.add(modelPart5);
		ModelPart modelPart6 = new ModelPart(this, 29, 5);
		modelPart6.addCuboid(-3.0F, -9.0F, -6.0F, 1, 2, 2);
		this.field_20512.add(modelPart6);
		ModelPart modelPart7 = new ModelPart(this, 32, 2);
		modelPart7.addCuboid(3.1F, -6.0F, -8.0F, 0, 3, 16);
		modelPart7.posX = (float) (-Math.PI / 6);
		this.field_20512.add(modelPart7);
		ModelPart modelPart8 = new ModelPart(this, 32, 2);
		modelPart8.addCuboid(-3.1F, -6.0F, -8.0F, 0, 3, 16);
		modelPart8.posX = (float) (-Math.PI / 6);
		this.field_20512.add(modelPart8);
		ModelPart modelPart9 = new ModelPart(this, 1, 1);
		modelPart9.addCuboid(-3.0F, -11.0F, -1.9F, 6, 5, 6, 0.2F);
		this.field_20512.add(modelPart9);
		ModelPart modelPart10 = new ModelPart(this, 19, 0);
		modelPart10.addCuboid(-2.0F, -11.0F, -4.0F, 4, 5, 2, 0.2F);
		this.field_20512.add(modelPart10);
		this.field_20518 = new ModelPart[]{modelPart4, modelPart5, modelPart6, modelPart9, modelPart10};
		this.field_20519 = new ModelPart[]{modelPart7, modelPart8};
	}

	protected void method_18901(ModelPart modelPart) {
		ModelPart modelPart2 = new ModelPart(this, 19, 16);
		modelPart2.addCuboid(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
		ModelPart modelPart3 = new ModelPart(this, 19, 16);
		modelPart3.addCuboid(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
		modelPart.add(modelPart2);
		modelPart.add(modelPart3);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entity;
		boolean bl = abstractHorseEntity.isBaby();
		float f = abstractHorseEntity.method_13992();
		boolean bl2 = abstractHorseEntity.method_13975();
		boolean bl3 = abstractHorseEntity.hasPassengers();

		for (ModelPart modelPart : this.field_20518) {
			modelPart.visible = bl2;
		}

		for (ModelPart modelPart2 : this.field_20519) {
			modelPart2.visible = bl3 && bl2;
		}

		if (bl) {
			GlStateManager.pushMatrix();
			GlStateManager.scale(f, 0.5F + f * 0.5F, f);
			GlStateManager.translate(0.0F, 0.95F * (1.0F - f), 0.0F);
		}

		this.field_20513.render(scale);
		this.field_20514.render(scale);
		this.field_20515.render(scale);
		this.field_20516.render(scale);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(f, f, f);
			GlStateManager.translate(0.0F, 2.3F * (1.0F - f), 0.0F);
		}

		this.field_20511.render(scale);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float g = f + 0.1F * f;
			GlStateManager.scale(g, g, g);
			GlStateManager.translate(0.0F, 2.25F * (1.0F - g), 0.1F * (1.4F - g));
		}

		this.field_20512.render(scale);
		if (bl) {
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		super.animateModel(entity, limbAngle, limbDistance, tickDelta);
		float f = this.method_18900(entity.prevBodyYaw, entity.bodyYaw, tickDelta);
		float g = this.method_18900(entity.prevHeadYaw, entity.headYaw, tickDelta);
		float h = entity.prevPitch + (entity.pitch - entity.prevPitch) * tickDelta;
		float i = g - f;
		float j = h * (float) (Math.PI / 180.0);
		if (i > 20.0F) {
			i = 20.0F;
		}

		if (i < -20.0F) {
			i = -20.0F;
		}

		if (limbDistance > 0.2F) {
			j += MathHelper.cos(limbAngle * 0.4F) * 0.15F * limbDistance;
		}

		AbstractHorseEntity abstractHorseEntity = (AbstractHorseEntity)entity;
		float k = abstractHorseEntity.method_14008(tickDelta);
		float l = abstractHorseEntity.method_14010(tickDelta);
		float m = 1.0F - l;
		float n = abstractHorseEntity.method_14012(tickDelta);
		boolean bl = abstractHorseEntity.field_15509 != 0;
		float o = (float)entity.ticksAlive + tickDelta;
		this.field_20512.pivotY = 4.0F;
		this.field_20512.pivotZ = -12.0F;
		this.field_20511.posX = 0.0F;
		this.field_20512.posX = (float) (Math.PI / 6) + j;
		this.field_20512.posY = i * (float) (Math.PI / 180.0);
		float p = abstractHorseEntity.isTouchingWater() ? 0.2F : 1.0F;
		float q = MathHelper.cos(p * limbAngle * 0.6662F + (float) Math.PI);
		float r = q * 0.8F * limbDistance;
		float s = (1.0F - Math.max(l, k)) * ((float) (Math.PI / 6) + j + n * MathHelper.sin(o) * 0.05F);
		this.field_20512.posX = l * ((float) (Math.PI / 12) + j) + k * (2.1816616F + MathHelper.sin(o) * 0.05F) + s;
		this.field_20512.posY = l * i * (float) (Math.PI / 180.0) + (1.0F - Math.max(l, k)) * this.field_20512.posY;
		this.field_20512.pivotY = l * -4.0F + k * 11.0F + (1.0F - Math.max(l, k)) * this.field_20512.pivotY;
		this.field_20512.pivotZ = l * -4.0F + k * -12.0F + (1.0F - Math.max(l, k)) * this.field_20512.pivotZ;
		this.field_20511.posX = l * (float) (-Math.PI / 4) + m * this.field_20511.posX;
		float t = (float) (Math.PI / 12) * l;
		float u = MathHelper.cos(o * 0.6F + (float) Math.PI);
		this.field_20515.pivotY = 2.0F * l + 14.0F * m;
		this.field_20515.pivotZ = -6.0F * l - 10.0F * m;
		this.field_20516.pivotY = this.field_20515.pivotY;
		this.field_20516.pivotZ = this.field_20515.pivotZ;
		float v = ((float) (-Math.PI / 3) + u) * l + r * m;
		float w = ((float) (-Math.PI / 3) - u) * l - r * m;
		this.field_20513.posX = t - q * 0.5F * limbDistance * m;
		this.field_20514.posX = t + q * 0.5F * limbDistance * m;
		this.field_20515.posX = v;
		this.field_20516.posX = w;
		this.field_20517.posX = (float) (Math.PI / 6) + limbDistance * 0.75F;
		this.field_20517.pivotY = -5.0F + limbDistance;
		this.field_20517.pivotZ = 2.0F + limbDistance * 2.0F;
		if (bl) {
			this.field_20517.posY = MathHelper.cos(o * 0.7F);
		} else {
			this.field_20517.posY = 0.0F;
		}
	}

	private float method_18900(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
