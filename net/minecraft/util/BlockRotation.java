package net.minecraft.util;

import net.minecraft.util.math.Direction;

public enum BlockRotation {
	NONE("rotate_0"),
	CLOCKWISE_90("rotate_90"),
	CLOCKWISE_180("rotate_180"),
	COUNTERCLOCKWISE_90("rotate_270");

	private final String name;
	private static final String[] NAMES = new String[values().length];

	private BlockRotation(String string2) {
		this.name = string2;
	}

	public BlockRotation rotate(BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch (this) {
					case NONE:
						return CLOCKWISE_180;
					case CLOCKWISE_90:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_180:
						return NONE;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_90;
				}
			case COUNTERCLOCKWISE_90:
				switch (this) {
					case NONE:
						return COUNTERCLOCKWISE_90;
					case CLOCKWISE_90:
						return NONE;
					case CLOCKWISE_180:
						return CLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return CLOCKWISE_180;
				}
			case CLOCKWISE_90:
				switch (this) {
					case NONE:
						return CLOCKWISE_90;
					case CLOCKWISE_90:
						return CLOCKWISE_180;
					case CLOCKWISE_180:
						return COUNTERCLOCKWISE_90;
					case COUNTERCLOCKWISE_90:
						return NONE;
				}
			default:
				return this;
		}
	}

	public Direction rotate(Direction direction) {
		if (direction.getAxis() == Direction.Axis.Y) {
			return direction;
		} else {
			switch (this) {
				case CLOCKWISE_90:
					return direction.rotateYClockwise();
				case CLOCKWISE_180:
					return direction.getOpposite();
				case COUNTERCLOCKWISE_90:
					return direction.rotateYCounterclockwise();
				default:
					return direction;
			}
		}
	}

	public int rotate(int rotation, int fullTurn) {
		switch (this) {
			case CLOCKWISE_90:
				return (rotation + fullTurn / 4) % fullTurn;
			case CLOCKWISE_180:
				return (rotation + fullTurn / 2) % fullTurn;
			case COUNTERCLOCKWISE_90:
				return (rotation + fullTurn * 3 / 4) % fullTurn;
			default:
				return rotation;
		}
	}

	static {
		int i = 0;

		for (BlockRotation blockRotation : values()) {
			NAMES[i++] = blockRotation.name;
		}
	}
}
