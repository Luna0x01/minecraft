package net.minecraft.client.render.entity.model;

import net.minecraft.client.gui.screen.options.HandOption;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.VexEntity;
import net.minecraft.util.math.MathHelper;

public class VexEntityModel extends BiPedModel {
	protected ModelPart field_15270;
	protected ModelPart field_15271;

	public VexEntityModel() {
		this(0.0F);
	}

	public VexEntityModel(float f) {
		super(f, 0.0F, 64, 64);
		this.leftLeg.visible = false;
		this.hat.visible = false;
		this.rightLeg = new ModelPart(this, 32, 0);
		this.rightLeg.addCuboid(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.field_15271 = new ModelPart(this, 0, 32);
		this.field_15271.addCuboid(-20.0F, 0.0F, 0.0F, 20, 12, 1);
		this.field_15270 = new ModelPart(this, 0, 32);
		this.field_15270.mirror = true;
		this.field_15270.addCuboid(0.0F, 0.0F, 0.0F, 20, 12, 1);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		super.render(entity, handSwing, handSwingAmount, tickDelta, age, headPitch, scale);
		this.field_15271.render(scale);
		this.field_15270.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		super.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		VexEntity vexEntity = (VexEntity)entity;
		if (vexEntity.isCharging()) {
			if (vexEntity.getDurability() == HandOption.RIGHT) {
				this.rightArm.posX = 3.7699115F;
			} else {
				this.leftArm.posX = 3.7699115F;
			}
		}

		this.rightLeg.posX += (float) (Math.PI / 5);
		this.field_15271.pivotZ = 2.0F;
		this.field_15270.pivotZ = 2.0F;
		this.field_15271.pivotY = 1.0F;
		this.field_15270.pivotY = 1.0F;
		this.field_15271.posY = 0.47123894F + MathHelper.cos(tickDelta * 0.8F) * (float) Math.PI * 0.05F;
		this.field_15270.posY = -this.field_15271.posY;
		this.field_15270.posZ = -0.47123894F;
		this.field_15270.posX = 0.47123894F;
		this.field_15271.posX = 0.47123894F;
		this.field_15271.posZ = 0.47123894F;
	}

	public int method_13841() {
		return 23;
	}
}
