package net.minecraft.util.math;

import com.google.common.collect.Iterators;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringIdentifiable;

public enum Direction implements StringIdentifiable {
	DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
	UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
	NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
	SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
	WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
	EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

	private final int id;
	private final int idOpposite;
	private final int idHorizontal;
	private final String name;
	private final Direction.Axis axis;
	private final Direction.AxisDirection axisDirection;
	private final Vec3i vec;
	private static final Direction[] field_21287 = values();
	private static final Map<String, Direction> DIRECTION_MAP = (Map<String, Direction>)Arrays.stream(field_21287)
		.collect(Collectors.toMap(Direction::getName, direction -> direction));
	private static final Direction[] ALL = (Direction[])Arrays.stream(field_21287)
		.sorted(Comparator.comparingInt(direction -> direction.id))
		.toArray(Direction[]::new);
	private static final Direction[] HORIZONTAL = (Direction[])Arrays.stream(field_21287)
		.filter(direction -> direction.getAxis().isHorizontal())
		.sorted(Comparator.comparingInt(direction -> direction.idHorizontal))
		.toArray(Direction[]::new);

	private Direction(int j, int k, int l, String string2, Direction.AxisDirection axisDirection, Direction.Axis axis, Vec3i vec3i) {
		this.id = j;
		this.idHorizontal = l;
		this.idOpposite = k;
		this.name = string2;
		this.axis = axis;
		this.axisDirection = axisDirection;
		this.vec = vec3i;
	}

	public static Direction[] method_19938(Entity entity) {
		float f = entity.method_15589(1.0F) * (float) (Math.PI / 180.0);
		float g = -entity.method_15591(1.0F) * (float) (Math.PI / 180.0);
		float h = MathHelper.sin(f);
		float i = MathHelper.cos(f);
		float j = MathHelper.sin(g);
		float k = MathHelper.cos(g);
		boolean bl = j > 0.0F;
		boolean bl2 = h < 0.0F;
		boolean bl3 = k > 0.0F;
		float l = bl ? j : -j;
		float m = bl2 ? -h : h;
		float n = bl3 ? k : -k;
		float o = l * i;
		float p = n * i;
		Direction direction = bl ? EAST : WEST;
		Direction direction2 = bl2 ? UP : DOWN;
		Direction direction3 = bl3 ? SOUTH : NORTH;
		if (l > n) {
			if (m > o) {
				return method_19941(direction2, direction, direction3);
			} else {
				return p > m ? method_19941(direction, direction3, direction2) : method_19941(direction, direction2, direction3);
			}
		} else if (m > p) {
			return method_19941(direction2, direction3, direction);
		} else {
			return o > m ? method_19941(direction3, direction, direction2) : method_19941(direction3, direction2, direction);
		}
	}

	private static Direction[] method_19941(Direction direction, Direction direction2, Direction direction3) {
		return new Direction[]{direction, direction2, direction3, direction3.getOpposite(), direction2.getOpposite(), direction.getOpposite()};
	}

	public int getId() {
		return this.id;
	}

	public int getHorizontal() {
		return this.idHorizontal;
	}

	public Direction.AxisDirection getAxisDirection() {
		return this.axisDirection;
	}

	public Direction getOpposite() {
		return getById(this.idOpposite);
	}

	public Direction getClockWiseFacingByAxis(Direction.Axis axis) {
		switch (axis) {
			case X:
				if (this != WEST && this != EAST) {
					return this.rotateYClockWise();
				}

				return this;
			case Y:
				if (this != UP && this != DOWN) {
					return this.rotateYClockwise();
				}

				return this;
			case Z:
				if (this != NORTH && this != SOUTH) {
					return this.rotateZClockWise();
				}

				return this;
			default:
				throw new IllegalStateException("Unable to get CW facing for axis " + axis);
		}
	}

	public Direction rotateYClockwise() {
		switch (this) {
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	private Direction rotateYClockWise() {
		switch (this) {
			case NORTH:
				return DOWN;
			case EAST:
			case WEST:
			default:
				throw new IllegalStateException("Unable to get X-rotated facing of " + this);
			case SOUTH:
				return UP;
			case UP:
				return NORTH;
			case DOWN:
				return SOUTH;
		}
	}

	private Direction rotateZClockWise() {
		switch (this) {
			case EAST:
				return DOWN;
			case SOUTH:
			default:
				throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case WEST:
				return UP;
			case UP:
				return EAST;
			case DOWN:
				return WEST;
		}
	}

	public Direction rotateYCounterclockwise() {
		switch (this) {
			case NORTH:
				return WEST;
			case EAST:
				return NORTH;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int getOffsetX() {
		return this.axis == Direction.Axis.X ? this.axisDirection.offset() : 0;
	}

	public int getOffsetY() {
		return this.axis == Direction.Axis.Y ? this.axisDirection.offset() : 0;
	}

	public int getOffsetZ() {
		return this.axis == Direction.Axis.Z ? this.axisDirection.offset() : 0;
	}

	public String getName() {
		return this.name;
	}

	public Direction.Axis getAxis() {
		return this.axis;
	}

	@Nullable
	public static Direction byName(@Nullable String name) {
		return name == null ? null : (Direction)DIRECTION_MAP.get(name.toLowerCase(Locale.ROOT));
	}

	public static Direction getById(int id) {
		return ALL[MathHelper.abs(id % ALL.length)];
	}

	public static Direction fromHorizontal(int value) {
		return HORIZONTAL[MathHelper.abs(value % HORIZONTAL.length)];
	}

	public static Direction fromRotation(double rotation) {
		return fromHorizontal(MathHelper.floor(rotation / 90.0 + 0.5) & 3);
	}

	public static Direction method_19939(Direction.Axis axis, Direction.AxisDirection axisDirection) {
		switch (axis) {
			case X:
				return axisDirection == Direction.AxisDirection.POSITIVE ? EAST : WEST;
			case Y:
				return axisDirection == Direction.AxisDirection.POSITIVE ? UP : DOWN;
			case Z:
			default:
				return axisDirection == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
		}
	}

	public float method_12578() {
		return (float)((this.idHorizontal & 3) * 90);
	}

	public static Direction random(Random random) {
		return values()[random.nextInt(values().length)];
	}

	public static Direction getFacing(double d, double e, double f) {
		return getFacing((float)d, (float)e, (float)f);
	}

	public static Direction getFacing(float x, float y, float z) {
		Direction direction = NORTH;
		float f = Float.MIN_VALUE;

		for (Direction direction2 : field_21287) {
			float g = x * (float)direction2.vec.getX() + y * (float)direction2.vec.getY() + z * (float)direction2.vec.getZ();
			if (g > f) {
				f = g;
				direction = direction2;
			}
		}

		return direction;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

	public static Direction get(Direction.AxisDirection direction, Direction.Axis axis) {
		for (Direction direction2 : values()) {
			if (direction2.getAxisDirection() == direction && direction2.getAxis() == axis) {
				return direction2;
			}
		}

		throw new IllegalArgumentException("No such direction: " + direction + " " + axis);
	}

	public Vec3i getVector() {
		return this.vec;
	}

	public static enum Axis implements Predicate<Direction>, StringIdentifiable {
		X("x") {
			@Override
			public int choose(int i, int j, int k) {
				return i;
			}

			@Override
			public double method_19947(double d, double e, double f) {
				return d;
			}
		},
		Y("y") {
			@Override
			public int choose(int i, int j, int k) {
				return j;
			}

			@Override
			public double method_19947(double d, double e, double f) {
				return e;
			}
		},
		Z("z") {
			@Override
			public int choose(int i, int j, int k) {
				return k;
			}

			@Override
			public double method_19947(double d, double e, double f) {
				return f;
			}
		};

		private static final Map<String, Direction.Axis> field_21289 = (Map<String, Direction.Axis>)Arrays.stream(values())
			.collect(Collectors.toMap(Direction.Axis::getName, axis -> axis));
		private final String name;

		private Axis(String string2) {
			this.name = string2;
		}

		@Nullable
		public static Direction.Axis fromName(String name) {
			return (Direction.Axis)field_21289.get(name.toLowerCase(Locale.ROOT));
		}

		public String getName() {
			return this.name;
		}

		public boolean method_19950() {
			return this == Y;
		}

		public boolean isHorizontal() {
			return this == X || this == Z;
		}

		public String toString() {
			return this.name;
		}

		public boolean test(@Nullable Direction direction) {
			return direction != null && direction.getAxis() == this;
		}

		public Direction.DirectionType getDirectionType() {
			switch (this) {
				case X:
				case Z:
					return Direction.DirectionType.HORIZONTAL;
				case Y:
					return Direction.DirectionType.VERTICAL;
				default:
					throw new Error("Someone's been tampering with the universe!");
			}
		}

		@Override
		public String asString() {
			return this.name;
		}

		public abstract int choose(int i, int j, int k);

		public abstract double method_19947(double d, double e, double f);
	}

	public static enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String name;

		private AxisDirection(int j, String string2) {
			this.offset = j;
			this.name = string2;
		}

		public int offset() {
			return this.offset;
		}

		public String toString() {
			return this.name;
		}
	}

	public static enum DirectionType implements Iterable<Direction>, Predicate<Direction> {
		HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
		VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

		private final Direction[] field_21291;
		private final Direction.Axis[] field_21292;

		private DirectionType(Direction[] directions, Direction.Axis[] axiss) {
			this.field_21291 = directions;
			this.field_21292 = axiss;
		}

		public Direction getRandomDirection(Random random) {
			return this.field_21291[random.nextInt(this.field_21291.length)];
		}

		public boolean test(@Nullable Direction direction) {
			return direction != null && direction.getAxis().getDirectionType() == this;
		}

		public Iterator<Direction> iterator() {
			return Iterators.forArray(this.field_21291);
		}
	}
}
