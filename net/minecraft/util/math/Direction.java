package net.minecraft.util.math;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
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
	private static final Direction[] ALL = new Direction[6];
	private static final Direction[] HORIZONTAL = new Direction[4];
	private static final Map<String, Direction> DIRECTION_MAP = Maps.newHashMap();

	private Direction(int j, int k, int l, String string2, Direction.AxisDirection axisDirection, Direction.Axis axis, Vec3i vec3i) {
		this.id = j;
		this.idHorizontal = l;
		this.idOpposite = k;
		this.name = string2;
		this.axis = axis;
		this.axisDirection = axisDirection;
		this.vec = vec3i;
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
	public static Direction byName(String name) {
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

	public float method_12578() {
		return (float)((this.idHorizontal & 3) * 90);
	}

	public static Direction random(Random random) {
		return values()[random.nextInt(values().length)];
	}

	public static Direction getFacing(float x, float y, float z) {
		Direction direction = NORTH;
		float f = Float.MIN_VALUE;

		for (Direction direction2 : values()) {
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

	public static Direction getLookingDirection(BlockPos pos, LivingEntity entity) {
		if (Math.abs(entity.x - (double)((float)pos.getX() + 0.5F)) < 2.0 && Math.abs(entity.z - (double)((float)pos.getZ() + 0.5F)) < 2.0) {
			double d = entity.y + (double)entity.getEyeHeight();
			if (d - (double)pos.getY() > 2.0) {
				return UP;
			}

			if ((double)pos.getY() - d > 0.0) {
				return DOWN;
			}
		}

		return entity.getHorizontalDirection().getOpposite();
	}

	public Vec3i getVector() {
		return this.vec;
	}

	static {
		for (Direction direction : values()) {
			ALL[direction.id] = direction;
			if (direction.getAxis().isHorizontal()) {
				HORIZONTAL[direction.idHorizontal] = direction;
			}

			DIRECTION_MAP.put(direction.getName().toLowerCase(Locale.ROOT), direction);
		}
	}

	public static enum Axis implements Predicate<Direction>, StringIdentifiable {
		X("x", Direction.DirectionType.HORIZONTAL),
		Y("y", Direction.DirectionType.VERTICAL),
		Z("z", Direction.DirectionType.HORIZONTAL);

		private static final Map<String, Direction.Axis> BY_NAME = Maps.newHashMap();
		private final String name;
		private final Direction.DirectionType directionType;

		private Axis(String string2, Direction.DirectionType directionType) {
			this.name = string2;
			this.directionType = directionType;
		}

		@Nullable
		public static Direction.Axis fromName(String name) {
			return name == null ? null : (Direction.Axis)BY_NAME.get(name.toLowerCase(Locale.ROOT));
		}

		public String getName() {
			return this.name;
		}

		public boolean isVertical() {
			return this.directionType == Direction.DirectionType.VERTICAL;
		}

		public boolean isHorizontal() {
			return this.directionType == Direction.DirectionType.HORIZONTAL;
		}

		public String toString() {
			return this.name;
		}

		public boolean apply(@Nullable Direction direction) {
			return direction != null && direction.getAxis() == this;
		}

		public Direction.DirectionType getDirectionType() {
			return this.directionType;
		}

		@Override
		public String asString() {
			return this.name;
		}

		static {
			for (Direction.Axis axis : values()) {
				BY_NAME.put(axis.getName().toLowerCase(Locale.ROOT), axis);
			}
		}
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

	public static enum DirectionType implements Predicate<Direction>, Iterable<Direction> {
		HORIZONTAL,
		VERTICAL;

		public Direction[] getDirections() {
			switch (this) {
				case HORIZONTAL:
					return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
				case VERTICAL:
					return new Direction[]{Direction.UP, Direction.DOWN};
				default:
					throw new Error("Someone's been tampering with the universe!");
			}
		}

		public Direction getRandomDirection(Random random) {
			Direction[] directions = this.getDirections();
			return directions[random.nextInt(directions.length)];
		}

		public boolean apply(@Nullable Direction direction) {
			return direction != null && direction.getAxis().getDirectionType() == this;
		}

		public Iterator<Direction> iterator() {
			return Iterators.forArray(this.getDirections());
		}
	}
}
