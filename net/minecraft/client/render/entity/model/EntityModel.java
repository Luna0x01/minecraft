package net.minecraft.client.render.entity.model;

import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

public abstract class EntityModel<T extends Entity> extends Model {
	public float handSwingProgress;
	public boolean riding;
	public boolean child = true;

	protected EntityModel() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	protected EntityModel(Function<Identifier, RenderLayer> function) {
		super(function);
	}

	public abstract void setAngles(T entity, float f, float g, float h, float i, float j);

	public void animateModel(T entity, float f, float g, float h) {
	}

	public void copyStateTo(EntityModel<T> entityModel) {
		entityModel.handSwingProgress = this.handSwingProgress;
		entityModel.riding = this.riding;
		entityModel.child = this.child;
	}
}
