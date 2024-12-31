package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;

public class MagmaCubeEntityModel extends EntityModel {
	private final ModelPart[] field_1486 = new ModelPart[8];
	private final ModelPart field_1487;

	public MagmaCubeEntityModel() {
		for (int i = 0; i < this.field_1486.length; i++) {
			int j = 0;
			int k = i;
			if (i == 2) {
				j = 24;
				k = 10;
			} else if (i == 3) {
				j = 24;
				k = 19;
			}

			this.field_1486[i] = new ModelPart(this, j, k);
			this.field_1486[i].addCuboid(-4.0F, (float)(16 + i), -4.0F, 8, 1, 8);
		}

		this.field_1487 = new ModelPart(this, 0, 16);
		this.field_1487.addCuboid(-2.0F, 18.0F, -2.0F, 4, 4, 4);
	}

	@Override
	public void animateModel(LivingEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		MagmaCubeEntity magmaCubeEntity = (MagmaCubeEntity)entity;
		float f = magmaCubeEntity.lastStretch + (magmaCubeEntity.stretch - magmaCubeEntity.lastStretch) * tickDelta;
		if (f < 0.0F) {
			f = 0.0F;
		}

		for (int i = 0; i < this.field_1486.length; i++) {
			this.field_1486[i].pivotY = (float)(-(4 - i)) * f * 1.7F;
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_1487.render(scale);

		for (ModelPart modelPart : this.field_1486) {
			modelPart.render(scale);
		}
	}
}
