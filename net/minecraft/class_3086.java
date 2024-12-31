package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EvocationIllagerEntity;
import net.minecraft.util.math.MathHelper;

public class class_3086 extends class_3087 {
	public class_3086(float f) {
		super(f, 0.0F, 64, 64);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		EvocationIllagerEntity evocationIllagerEntity = (EvocationIllagerEntity)entity;
		if (evocationIllagerEntity.method_14082()) {
			this.field_15265.render(scale);
			this.field_15266.render(scale);
		} else {
			this.field_15261.render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_15265.pivotZ = 0.0F;
		this.field_15265.pivotX = -5.0F;
		this.field_15266.pivotZ = 0.0F;
		this.field_15266.pivotX = 5.0F;
		this.field_15265.posX = MathHelper.cos(tickDelta * 0.6662F) * 0.25F;
		this.field_15266.posX = MathHelper.cos(tickDelta * 0.6662F) * 0.25F;
		this.field_15265.posZ = (float) (Math.PI * 3.0 / 4.0);
		this.field_15266.posZ = (float) (-Math.PI * 3.0 / 4.0);
		this.field_15265.posY = 0.0F;
		this.field_15266.posY = 0.0F;
	}
}
