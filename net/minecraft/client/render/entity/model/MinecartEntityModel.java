package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class MinecartEntityModel extends EntityModel {
	public ModelPart[] field_1488 = new ModelPart[7];

	public MinecartEntityModel() {
		this.field_1488[0] = new ModelPart(this, 0, 10);
		this.field_1488[1] = new ModelPart(this, 0, 0);
		this.field_1488[2] = new ModelPart(this, 0, 0);
		this.field_1488[3] = new ModelPart(this, 0, 0);
		this.field_1488[4] = new ModelPart(this, 0, 0);
		this.field_1488[5] = new ModelPart(this, 44, 10);
		int i = 20;
		int j = 8;
		int k = 16;
		int l = 4;
		this.field_1488[0].addCuboid(-10.0F, -8.0F, -1.0F, 20, 16, 2, 0.0F);
		this.field_1488[0].setPivot(0.0F, 4.0F, 0.0F);
		this.field_1488[5].addCuboid(-9.0F, -7.0F, -1.0F, 18, 14, 1, 0.0F);
		this.field_1488[5].setPivot(0.0F, 4.0F, 0.0F);
		this.field_1488[1].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_1488[1].setPivot(-9.0F, 4.0F, 0.0F);
		this.field_1488[2].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_1488[2].setPivot(9.0F, 4.0F, 0.0F);
		this.field_1488[3].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_1488[3].setPivot(0.0F, 4.0F, -7.0F);
		this.field_1488[4].addCuboid(-8.0F, -9.0F, -1.0F, 16, 8, 2, 0.0F);
		this.field_1488[4].setPivot(0.0F, 4.0F, 7.0F);
		this.field_1488[0].posX = (float) (Math.PI / 2);
		this.field_1488[1].posY = (float) (Math.PI * 3.0 / 2.0);
		this.field_1488[2].posY = (float) (Math.PI / 2);
		this.field_1488[3].posY = (float) Math.PI;
		this.field_1488[5].posX = (float) (-Math.PI / 2);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.field_1488[5].pivotY = 4.0F - tickDelta;

		for (int i = 0; i < 6; i++) {
			this.field_1488[i].render(scale);
		}
	}
}
