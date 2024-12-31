package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SilverfishEntityModel extends EntityModel {
	private ModelPart[] field_1521;
	private ModelPart[] field_1522;
	private float[] field_1523 = new float[7];
	private static final int[][] field_1524 = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] field_1525 = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

	public SilverfishEntityModel() {
		this.field_1521 = new ModelPart[7];
		float f = -3.5F;

		for (int i = 0; i < this.field_1521.length; i++) {
			this.field_1521[i] = new ModelPart(this, field_1525[i][0], field_1525[i][1]);
			this.field_1521[i].addCuboid((float)field_1524[i][0] * -0.5F, 0.0F, (float)field_1524[i][2] * -0.5F, field_1524[i][0], field_1524[i][1], field_1524[i][2]);
			this.field_1521[i].setPivot(0.0F, (float)(24 - field_1524[i][1]), f);
			this.field_1523[i] = f;
			if (i < this.field_1521.length - 1) {
				f += (float)(field_1524[i][2] + field_1524[i + 1][2]) * 0.5F;
			}
		}

		this.field_1522 = new ModelPart[3];
		this.field_1522[0] = new ModelPart(this, 20, 0);
		this.field_1522[0].addCuboid(-5.0F, 0.0F, (float)field_1524[2][2] * -0.5F, 10, 8, field_1524[2][2]);
		this.field_1522[0].setPivot(0.0F, 16.0F, this.field_1523[2]);
		this.field_1522[1] = new ModelPart(this, 20, 11);
		this.field_1522[1].addCuboid(-3.0F, 0.0F, (float)field_1524[4][2] * -0.5F, 6, 4, field_1524[4][2]);
		this.field_1522[1].setPivot(0.0F, 20.0F, this.field_1523[4]);
		this.field_1522[2] = new ModelPart(this, 20, 18);
		this.field_1522[2].addCuboid(-3.0F, 0.0F, (float)field_1524[4][2] * -0.5F, 6, 5, field_1524[1][2]);
		this.field_1522[2].setPivot(0.0F, 19.0F, this.field_1523[1]);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);

		for (int i = 0; i < this.field_1521.length; i++) {
			this.field_1521[i].render(scale);
		}

		for (int j = 0; j < this.field_1522.length; j++) {
			this.field_1522[j].render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		for (int i = 0; i < this.field_1521.length; i++) {
			this.field_1521[i].posY = MathHelper.cos(tickDelta * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
			this.field_1521[i].pivotX = MathHelper.sin(tickDelta * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * (float)Math.abs(i - 2);
		}

		this.field_1522[0].posY = this.field_1521[2].posY;
		this.field_1522[1].posY = this.field_1521[4].posY;
		this.field_1522[1].pivotX = this.field_1521[4].pivotX;
		this.field_1522[2].posY = this.field_1521[1].posY;
		this.field_1522[2].pivotX = this.field_1521[1].pivotX;
	}
}
