package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;

public class ChestBlockEntityModel extends EntityModel {
	public ModelPart lid = new ModelPart(this, 0, 0).setTextureSize(64, 64);
	public ModelPart base;
	public ModelPart lock;

	public ChestBlockEntityModel() {
		this.lid.addCuboid(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		this.lid.pivotX = 1.0F;
		this.lid.pivotY = 7.0F;
		this.lid.pivotZ = 15.0F;
		this.lock = new ModelPart(this, 0, 0).setTextureSize(64, 64);
		this.lock.addCuboid(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		this.lock.pivotX = 8.0F;
		this.lock.pivotY = 7.0F;
		this.lock.pivotZ = 15.0F;
		this.base = new ModelPart(this, 0, 19).setTextureSize(64, 64);
		this.base.addCuboid(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		this.base.pivotX = 1.0F;
		this.base.pivotY = 6.0F;
		this.base.pivotZ = 1.0F;
	}

	public void renderParts() {
		this.lock.posX = this.lid.posX;
		this.lid.render(0.0625F);
		this.lock.render(0.0625F);
		this.base.render(0.0625F);
	}
}
