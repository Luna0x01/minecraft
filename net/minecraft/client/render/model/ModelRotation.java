package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_4305;
import net.minecraft.class_4306;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

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

	private static final Map<Integer, ModelRotation> field_21055 = (Map<Integer, ModelRotation>)Arrays.stream(values())
		.sorted(Comparator.comparingInt(modelRotation -> modelRotation.field_21056))
		.collect(Collectors.toMap(modelRotation -> modelRotation.field_21056, modelRotation -> modelRotation));
	private final int field_21056;
	private final class_4305 field_21057;
	private final int quarterX;
	private final int quarterY;

	private static int getIndex(int x, int y) {
		return x * 360 + y;
	}

	private ModelRotation(int j, int k) {
		this.field_21056 = getIndex(j, k);
		class_4305 lv = new class_4305(new class_4306(0.0F, 1.0F, 0.0F), (float)(-k), true);
		lv.method_19657(new class_4305(new class_4306(1.0F, 0.0F, 0.0F), (float)(-j), true));
		this.field_21057 = lv;
		this.quarterX = MathHelper.abs(j / 90);
		this.quarterY = MathHelper.abs(k / 90);
	}

	public class_4305 method_10378() {
		return this.field_21057;
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
		return (ModelRotation)field_21055.get(getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
	}
}
