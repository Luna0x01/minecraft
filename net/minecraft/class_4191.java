package net.minecraft;

import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class class_4191 extends EntityModel {
	private final ModelPart field_20549;
	private final ModelPart field_20550;
	private final ModelPart field_20551;
	private final ModelPart field_20552;
	private final ModelPart field_20553;
	private final ModelPart field_20554;
	private final ModelPart field_20555;
	private final ModelPart field_20556;
	private final ModelPart field_20557;
	private final ModelPart field_20558;
	private final ModelPart field_20559;
	private final ModelPart field_20560;
	private final ModelPart field_20561;

	public class_4191() {
		this.textureWidth = 32;
		this.textureHeight = 32;
		int i = 22;
		this.field_20549 = new ModelPart(this, 0, 0);
		this.field_20549.addCuboid(-4.0F, -8.0F, -4.0F, 8, 8, 8);
		this.field_20549.setPivot(0.0F, 22.0F, 0.0F);
		this.field_20550 = new ModelPart(this, 24, 0);
		this.field_20550.addCuboid(-2.0F, 0.0F, -1.0F, 2, 1, 2);
		this.field_20550.setPivot(-4.0F, 15.0F, -2.0F);
		this.field_20551 = new ModelPart(this, 24, 3);
		this.field_20551.addCuboid(0.0F, 0.0F, -1.0F, 2, 1, 2);
		this.field_20551.setPivot(4.0F, 15.0F, -2.0F);
		this.field_20552 = new ModelPart(this, 15, 17);
		this.field_20552.addCuboid(-4.0F, -1.0F, 0.0F, 8, 1, 0);
		this.field_20552.setPivot(0.0F, 14.0F, -4.0F);
		this.field_20552.posX = (float) (Math.PI / 4);
		this.field_20553 = new ModelPart(this, 14, 16);
		this.field_20553.addCuboid(-4.0F, -1.0F, 0.0F, 8, 1, 1);
		this.field_20553.setPivot(0.0F, 14.0F, 0.0F);
		this.field_20554 = new ModelPart(this, 23, 18);
		this.field_20554.addCuboid(-4.0F, -1.0F, 0.0F, 8, 1, 0);
		this.field_20554.setPivot(0.0F, 14.0F, 4.0F);
		this.field_20554.posX = (float) (-Math.PI / 4);
		this.field_20555 = new ModelPart(this, 5, 17);
		this.field_20555.addCuboid(-1.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_20555.setPivot(-4.0F, 22.0F, -4.0F);
		this.field_20555.posY = (float) (-Math.PI / 4);
		this.field_20556 = new ModelPart(this, 1, 17);
		this.field_20556.addCuboid(0.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_20556.setPivot(4.0F, 22.0F, -4.0F);
		this.field_20556.posY = (float) (Math.PI / 4);
		this.field_20557 = new ModelPart(this, 15, 20);
		this.field_20557.addCuboid(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_20557.setPivot(0.0F, 22.0F, -4.0F);
		this.field_20557.posX = (float) (-Math.PI / 4);
		this.field_20559 = new ModelPart(this, 15, 20);
		this.field_20559.addCuboid(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_20559.setPivot(0.0F, 22.0F, 0.0F);
		this.field_20558 = new ModelPart(this, 15, 20);
		this.field_20558.addCuboid(-4.0F, 0.0F, 0.0F, 8, 1, 0);
		this.field_20558.setPivot(0.0F, 22.0F, 4.0F);
		this.field_20558.posX = (float) (Math.PI / 4);
		this.field_20560 = new ModelPart(this, 9, 17);
		this.field_20560.addCuboid(-1.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_20560.setPivot(-4.0F, 22.0F, 4.0F);
		this.field_20560.posY = (float) (Math.PI / 4);
		this.field_20561 = new ModelPart(this, 9, 17);
		this.field_20561.addCuboid(0.0F, -8.0F, 0.0F, 1, 8, 0);
		this.field_20561.setPivot(4.0F, 22.0F, 4.0F);
		this.field_20561.posY = (float) (-Math.PI / 4);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_20549.render(scale);
		this.field_20550.render(scale);
		this.field_20551.render(scale);
		this.field_20552.render(scale);
		this.field_20553.render(scale);
		this.field_20554.render(scale);
		this.field_20555.render(scale);
		this.field_20556.render(scale);
		this.field_20557.render(scale);
		this.field_20559.render(scale);
		this.field_20558.render(scale);
		this.field_20560.render(scale);
		this.field_20561.render(scale);
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		this.field_20550.posZ = -0.2F + 0.4F * MathHelper.sin(tickDelta * 0.2F);
		this.field_20551.posZ = 0.2F - 0.4F * MathHelper.sin(tickDelta * 0.2F);
	}
}
