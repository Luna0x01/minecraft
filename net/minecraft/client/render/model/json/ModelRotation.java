package net.minecraft.client.render.model.json;

import net.minecraft.util.math.Direction;
import org.lwjgl.util.vector.Vector3f;

public class ModelRotation {
	public final Vector3f rotation;
	public final Direction.Axis axis;
	public final float angle;
	public final boolean rescale;

	public ModelRotation(Vector3f vector3f, Direction.Axis axis, float f, boolean bl) {
		this.rotation = vector3f;
		this.axis = axis;
		this.angle = f;
		this.rescale = bl;
	}
}
