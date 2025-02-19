package net.minecraft.client.render.model.json;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class ModelRotation {
	public final Vec3f origin;
	public final Direction.Axis axis;
	public final float angle;
	public final boolean rescale;

	public ModelRotation(Vec3f origin, Direction.Axis axis, float angle, boolean rescale) {
		this.origin = origin;
		this.axis = axis;
		this.angle = angle;
		this.rescale = rescale;
	}
}
