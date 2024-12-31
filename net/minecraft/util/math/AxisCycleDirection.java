package net.minecraft.util.math;

public enum AxisCycleDirection {
	field_10962 {
		@Override
		public int choose(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(i, j, k);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return axis;
		}

		@Override
		public AxisCycleDirection opposite() {
			return this;
		}
	},
	field_10963 {
		@Override
		public int choose(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(k, i, j);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AXES[Math.floorMod(axis.ordinal() + 1, 3)];
		}

		@Override
		public AxisCycleDirection opposite() {
			return field_10965;
		}
	},
	field_10965 {
		@Override
		public int choose(int i, int j, int k, Direction.Axis axis) {
			return axis.choose(j, k, i);
		}

		@Override
		public Direction.Axis cycle(Direction.Axis axis) {
			return AXES[Math.floorMod(axis.ordinal() - 1, 3)];
		}

		@Override
		public AxisCycleDirection opposite() {
			return field_10963;
		}
	};

	public static final Direction.Axis[] AXES = Direction.Axis.values();
	public static final AxisCycleDirection[] VALUES = values();

	private AxisCycleDirection() {
	}

	public abstract int choose(int i, int j, int k, Direction.Axis axis);

	public abstract Direction.Axis cycle(Direction.Axis axis);

	public abstract AxisCycleDirection opposite();

	public static AxisCycleDirection between(Direction.Axis axis, Direction.Axis axis2) {
		return VALUES[Math.floorMod(axis2.ordinal() - axis.ordinal(), 3)];
	}
}
