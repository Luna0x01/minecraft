package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class BoatEntityModel extends EntityModel {
	public ModelPart[] boatParts = new ModelPart[5];

	public BoatEntityModel() {
		this.boatParts[0] = new ModelPart(this, 0, 8);
		this.boatParts[1] = new ModelPart(this, 0, 0);
		this.boatParts[2] = new ModelPart(this, 0, 0);
		this.boatParts[3] = new ModelPart(this, 0, 0);
		this.boatParts[4] = new ModelPart(this, 0, 0);
		int i = 24;
		int j = 6;
		int k = 20;
		int l = 4;
		this.boatParts[0].addCuboid((float)(-i / 2), (float)(-k / 2 + 2), -3.0F, i, k - 4, 4, 0.0F);
		this.boatParts[0].setPivot(0.0F, (float)l, 0.0F);
		this.boatParts[1].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.boatParts[1].setPivot((float)(-i / 2 + 1), (float)l, 0.0F);
		this.boatParts[2].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.boatParts[2].setPivot((float)(i / 2 - 1), (float)l, 0.0F);
		this.boatParts[3].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.boatParts[3].setPivot(0.0F, (float)l, (float)(-k / 2 + 1));
		this.boatParts[4].addCuboid((float)(-i / 2 + 2), (float)(-j - 1), -1.0F, i - 4, j, 2, 0.0F);
		this.boatParts[4].setPivot(0.0F, (float)l, (float)(k / 2 - 1));
		this.boatParts[0].posX = (float) (Math.PI / 2);
		this.boatParts[1].posY = (float) (Math.PI * 3.0 / 2.0);
		this.boatParts[2].posY = (float) (Math.PI / 2);
		this.boatParts[3].posY = (float) Math.PI;
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		for (int i = 0; i < 5; i++) {
			this.boatParts[i].render(scale);
		}
	}
}
