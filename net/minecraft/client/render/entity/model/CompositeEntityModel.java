package net.minecraft.client.render.entity.model;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public abstract class CompositeEntityModel<E extends Entity> extends EntityModel<E> {
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, f, g, h, k));
	}

	public abstract Iterable<ModelPart> getParts();
}
