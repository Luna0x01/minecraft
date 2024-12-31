package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;

public class SignBlockEntityModel extends EntityModel {
	private final ModelPart plate = new ModelPart(this, 0, 0);
	private final ModelPart stick;

	public SignBlockEntityModel() {
		this.plate.addCuboid(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
		this.stick = new ModelPart(this, 0, 14);
		this.stick.addCuboid(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
	}

	public void render() {
		this.plate.render(0.0625F);
		this.stick.render(0.0625F);
	}

	public ModelPart method_18937() {
		return this.stick;
	}
}
