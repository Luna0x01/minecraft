package net.minecraft.client.render.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public enum ModelRotation {
	X0_Y0(0, 0),
	X0_Y90(0, 90),
	X0_Y180(0, 180),
	X0_Y270(0, 270),
	X90_Y0(90, 0),
	X90_Y90(90, 90),
	X90_Y180(90, 180),
	X90_Y270(90, 270),
	X180_Y0(180, 0),
	X180_Y90(180, 90),
	X180_Y180(180, 180),
	X180_Y270(180, 270),
	X270_Y0(270, 0),
	X270_Y90(270, 90),
	X270_Y180(270, 180),
	X270_Y270(270, 270);

	private static final Map<Integer, ModelRotation> ROTATIONS = Maps.newHashMap();
	private final int index;
	private final Matrix4f matrix;
	private final int quarterX;
	private final int quarterY;

	private static int getIndex(int x, int y) {
		return x * 360 + y;
	}

	private ModelRotation(int j, int k) {
		this.index = getIndex(j, k);
		this.matrix = new Matrix4f();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.setIdentity();
		Matrix4f.rotate((float)(-j) * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
		this.quarterX = MathHelper.abs(j / 90);
		Matrix4f matrix4f2 = new Matrix4f();
		matrix4f2.setIdentity();
		Matrix4f.rotate((float)(-k) * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrix4f2, matrix4f2);
		this.quarterY = MathHelper.abs(k / 90);
		Matrix4f.mul(matrix4f2, matrix4f, this.matrix);
	}

	public Matrix4f getMatrix() {
		return this.matrix;
	}

	public Direction rotate(Direction direction) {
		Direction direction2 = direction;

		for (int i = 0; i < this.quarterX; i++) {
			direction2 = direction2.getClockWiseFacingByAxis(Direction.Axis.X);
		}

		if (direction2.getAxis() != Direction.Axis.Y) {
			for (int j = 0; j < this.quarterY; j++) {
				direction2 = direction2.getClockWiseFacingByAxis(Direction.Axis.Y);
			}
		}

		return direction2;
	}

	public int rotate(Direction direction, int vertex) {
		int i = vertex;
		if (direction.getAxis() == Direction.Axis.X) {
			i = (vertex + this.quarterX) % 4;
		}

		Direction direction2 = direction;

		for (int j = 0; j < this.quarterX; j++) {
			direction2 = direction2.getClockWiseFacingByAxis(Direction.Axis.X);
		}

		if (direction2.getAxis() == Direction.Axis.Y) {
			i = (i + this.quarterY) % 4;
		}

		return i;
	}

	public static ModelRotation get(int x, int y) {
		return (ModelRotation)ROTATIONS.get(getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
	}

	static {
		for (ModelRotation modelRotation : values()) {
			ROTATIONS.put(modelRotation.index, modelRotation);
		}
	}
}
