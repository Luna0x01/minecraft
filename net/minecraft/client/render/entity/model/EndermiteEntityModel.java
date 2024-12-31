package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class EndermiteEntityModel extends EntityModel {
	private static final int[][] field_10547 = new int[][]{{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
	private static final int[][] field_10548 = new int[][]{{0, 0}, {0, 5}, {0, 14}, {0, 18}};
	private static final int field_10549 = field_10547.length;
	private final ModelPart[] field_10550 = new ModelPart[field_10549];

	public EndermiteEntityModel() {
		float f = -3.5F;

		for (int i = 0; i < this.field_10550.length; i++) {
			this.field_10550[i] = new ModelPart(this, field_10548[i][0], field_10548[i][1]);
			this.field_10550[i]
				.addCuboid((float)field_10547[i][0] * -0.5F, 0.0F, (float)field_10547[i][2] * -0.5F, field_10547[i][0], field_10547[i][1], field_10547[i][2]);
			this.field_10550[i].setPivot(0.0F, (float)(24 - field_10547[i][1]), f);
			if (i < this.field_10550.length - 1) {
				f += (float)(field_10547[i][2] + field_10547[i + 1][2]) * 0.5F;
			}
		}
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);

		for (int i = 0; i < this.field_10550.length; i++) {
			this.field_10550[i].render(scale);
		}
	}

	@Override
	public void setAngles(float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale, Entity entity) {
		for (int i = 0; i < this.field_10550.length; i++) {
			this.field_10550[i].posY = MathHelper.cos(tickDelta * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.01F * (float)(1 + Math.abs(i - 2));
			this.field_10550[i].pivotX = MathHelper.sin(tickDelta * 0.9F + (float)i * 0.15F * (float) Math.PI) * (float) Math.PI * 0.1F * (float)Math.abs(i - 2);
		}
	}
}
