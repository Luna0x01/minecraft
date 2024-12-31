package net.minecraft.client.render.model.json;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

public class ModelRotation {
	public final Vector3f origin;
	public final Direction.Axis axis;
	public final float angle;
	public final boolean rescale;

	public ModelRotation(Vector3f vector3f, Direction.Axis axis, float f, boolean bl) {
		this.origin = vector3f;
		this.axis = axis;
		this.angle = f;
		this.rescale = bl;
	}
}
