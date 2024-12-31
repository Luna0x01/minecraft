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
		this.field_1488[0].addCuboid((float)(-i / 2), (float)(-k / 2), -1.0F, i, k, 2, 0.0F);
		this.field_1488[0].setPivot(0.0F, (float)l, 0.0F);
		this.field_1488[5].addCuboid((float)(-i / 2 + 1), (float)(-k / 2 + 1), -1.0F, i - 2, k - 2, 1, 0.0F);
		this.field_1488[5].setPivot(0.0F, (float)l, 0.0F);
		this.field_1488[1].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.field_1488[1].setPivot((float)(-i / 2 + 1), (float)l, 0.0F);
		this.field_1488[2].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.field_1488[2].setPivot((float)(i / 2 - 1), (float)l, 0.0F);
		this.field_1488[3].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.field_1488[3].setPivot(0.0F, (float)l, (float)(-k / 2 + 1));
		this.field_1488[4].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.field_1488[4].setPivot(0.0F, (float)l, (float)(k / 2 - 1));
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
