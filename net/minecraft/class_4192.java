package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4192 extends EntityModel {
	private final ModelPart field_20562;
	private final ModelPart field_20563;
	private final ModelPart field_20564;
	private final ModelPart field_20565;
	private final ModelPart field_20566;
	private final ModelPart field_20567;
	private final ModelPart field_20568;
	private final ModelPart field_20569;
	private final ModelPart field_20570;
	private final ModelPart field_20571;
	private final ModelPart field_20572;

	public class_4192() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_20562 = new ModelPart(this, 12, 22);
		this.field_20562.addCuboid(-2.5F, -5.0F, -2.5F, 5, 5, 5);
		this.field_20562.setPivot(0.0F, 22.0F, 0.0F);
		this.field_20563 = new ModelPart(this, 24, 0);
		this.field_20563.addCuboid(-2.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_20563.setPivot(-2.5F, 17.0F, -1.5F);
		this.field_20564 = new ModelPart(this, 24, 3);
		this.field_20564.addCuboid(0.0F, 0.0F, 0.0F, 2, 0, 2);
		this.field_20564.setPivot(2.5F, 17.0F, -1.5F);
		this.field_20565 = new ModelPart(this, 15, 16);
		this.field_20565.addCuboid(-2.5F, -1.0F, 0.0F, 5, 1, 1);
		this.field_20565.setPivot(0.0F, 17.0F, -2.5F);
		this.field_20565.posX = (float) (Math.PI / 4);
		this.field_20566 = new ModelPart(this, 10, 16);
		this.field_20566.addCuboid(-2.5F, -1.0F, -1.0F, 5, 1, 1);
		this.field_20566.setPivot(0.0F, 17.0F, 2.5F);
		this.field_20566.posX = (float) (-Math.PI / 4);
		this.field_20567 = new ModelPart(this, 8, 16);
		this.field_20567.addCuboid(-1.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_20567.setPivot(-2.5F, 22.0F, -2.5F);
		this.field_20567.posY = (float) (-Math.PI / 4);
		this.field_20568 = new ModelPart(this, 8, 16);
		this.field_20568.addCuboid(-1.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_20568.setPivot(-2.5F, 22.0F, 2.5F);
		this.field_20568.posY = (float) (Math.PI / 4);
		this.field_20569 = new ModelPart(this, 4, 16);
		this.field_20569.addCuboid(0.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_20569.setPivot(2.5F, 22.0F, 2.5F);
		this.field_20569.posY = (float) (-Math.PI / 4);
		this.field_20570 = new ModelPart(this, 0, 16);
		this.field_20570.addCuboid(0.0F, -5.0F, 0.0F, 1, 5, 1);
		this.field_20570.setPivot(2.5F, 22.0F, -2.5F);
		this.field_20570.posY = (float) (Math.PI / 4);
		this.field_20571 = new ModelPart(this, 8, 22);
		this.field_20571.addCuboid(0.0F, 0.0F, 0.0F, 1, 1, 1);
		this.field_20571.setPivot(0.5F, 22.0F, 2.5F);
		this.field_20571.posX = (float) (Math.PI / 4);
		this.field_20572 = new ModelPart(this, 17, 21);
		this.field_20572.addCuboid(-2.5F, 0.0F, 0.0F, 5, 1, 1);
		this.field_20572.setPivot(0.0F, 22.0F, -2.5F);
		this.field_20572.posX = (float) (-Math.PI / 4);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20562.render(scale);
		this.field_20563.render(scale);
		this.field_20564.render(scale);
		this.field_20565.render(scale);
		this.field_20566.render(scale);
		this.field_20567.render(scale);
		this.field_20568.render(scale);
		this.field_20569.render(scale);
		this.field_20570.render(scale);
		this.field_20571.render(scale);
		this.field_20572.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_20563.posZ = -0.2F + 0.4F * MathHelper.sin(tickDelta * 0.2F);
		this.field_20564.posZ = 0.2F - 0.4F * MathHelper.sin(tickDelta * 0.2F);
	}
}
