package net.minecraft.client.render.entity;

import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseBaseEntity;

public abstract class HorseBaseEntityRenderer<T extends HorseBaseEntity, M extends HorseEntityModel<T>> extends MobEntityRenderer<T, M> {
	private final float scale;

	public HorseBaseEntityRenderer(EntityRendererFactory.Context ctx, M model, float scale) {
		super(ctx, model, 0.75F);
		this.scale = scale;
	}

	protected void scale(T horseBaseEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(this.scale, this.scale, this.scale);
		super.scale(horseBaseEntity, matrixStack, f);
	}
}
