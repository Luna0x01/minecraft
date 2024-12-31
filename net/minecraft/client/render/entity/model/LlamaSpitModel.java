package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.model.ModelPart;
import net.minecraft.entity.Entity;

public class LlamaSpitModel extends EntityModel {
	private final ModelPart field_15269 = new ModelPart(this);

	public LlamaSpitModel() {
		this(0.0F);
	}

	public LlamaSpitModel(float f) {
		int i = 2;
		this.field_15269.setTextureOffset(0, 0).addCuboid(-4.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_15269.setTextureOffset(0, 0).addCuboid(0.0F, -4.0F, 0.0F, 2, 2, 2, f);
		this.field_15269.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, -4.0F, 2, 2, 2, f);
		this.field_15269.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_15269.setTextureOffset(0, 0).addCuboid(2.0F, 0.0F, 0.0F, 2, 2, 2, f);
		this.field_15269.setTextureOffset(0, 0).addCuboid(0.0F, 2.0F, 0.0F, 2, 2, 2, f);
		this.field_15269.setTextureOffset(0, 0).addCuboid(0.0F, 0.0F, 2.0F, 2, 2, 2, f);
		this.field_15269.setPivot(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void render(Entity entity, float handSwing, float handSwingAmount, float tickDelta, float age, float headPitch, float scale) {
		this.setAngles(handSwing, handSwingAmount, tickDelta, age, headPitch, scale, entity);
		this.field_15269.render(scale);
	}
}
