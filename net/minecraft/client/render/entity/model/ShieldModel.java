package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;

public class ShieldModel extends EntityModel {
	private final ModelPart field_13392;
	private final ModelPart field_13393;

	public ShieldModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.field_13392 = new ModelPart(this, 0, 0);
		this.field_13392.addCuboid(-6.0F, -11.0F, -2.0F, 12, 22, 1, 0.0F);
		this.field_13393 = new ModelPart(this, 26, 0);
		this.field_13393.addCuboid(-1.0F, -3.0F, -1.0F, 2, 6, 6, 0.0F);
	}

	public void render() {
		this.field_13392.render(0.0625F);
		this.field_13393.render(0.0625F);
	}
}
