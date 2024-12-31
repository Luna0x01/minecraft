package net.minecraft;

import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.VindicationIllagerEntity;
import net.minecraft.util.math.MathHelper;

public class class_3091 extends class_3087 {
	public class_3091(float f) {
		this(f, 0.0F, 64, 64);
	}

	public class_3091(float f, float g, int i, int j) {
		super(f, g, i, j);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		VindicationIllagerEntity vindicationIllagerEntity = (VindicationIllagerEntity)entity;
		if (vindicationIllagerEntity.method_13600()) {
			this.field_15265.render(scale);
			this.field_15266.render(scale);
		} else {
			this.field_15261.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		float f = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
		float g = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
		this.field_15265.posZ = 0.0F;
		this.field_15266.posZ = 0.0F;
		this.field_15265.posY = (float) (Math.PI / 20);
		this.field_15266.posY = (float) (-Math.PI / 20);
		if (((LivingEntity)entity).getDurability() == HandOption.RIGHT) {
			this.field_15265.posX = -1.8849558F + MathHelper.cos(tickDelta * 0.09F) * 0.15F;
			this.field_15266.posX = -0.0F + MathHelper.cos(tickDelta * 0.19F) * 0.5F;
			this.field_15265.posX += f * 2.2F - g * 0.4F;
			this.field_15266.posX += f * 1.2F - g * 0.4F;
		} else {
			this.field_15265.posX = -0.0F + MathHelper.cos(tickDelta * 0.19F) * 0.5F;
			this.field_15266.posX = -1.8849558F + MathHelper.cos(tickDelta * 0.09F) * 0.15F;
			this.field_15265.posX += f * 1.2F - g * 0.4F;
			this.field_15266.posX += f * 2.2F - g * 0.4F;
		}

		this.field_15265.posZ = this.field_15265.posZ + MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F;
		this.field_15266.posZ = this.field_15266.posZ - (MathHelper.cos(tickDelta * 0.09F) * 0.05F + 0.05F);
		this.field_15265.posX = this.field_15265.posX + MathHelper.sin(tickDelta * 0.067F) * 0.05F;
		this.field_15266.posX = this.field_15266.posX - MathHelper.sin(tickDelta * 0.067F) * 0.05F;
	}
}
