package net.minecraft.util;

import net.minecraft.util.math.Direction;

public enum BlockMirror {
	NONE("no_mirror"),
	LEFT_RIGHT("mirror_left_right"),
	FRONT_BACK("mirror_front_back");

	private final String name;
	private static final String[] NAMES = new String[values().length];

	private BlockMirror(String string2) {
		this.name = string2;
	}

	public int mirror(int rotation, int fullTurn) {
		int i = fullTurn / 2;
		int j = rotation > i ? rotation - fullTurn : rotation;
		switch (this) {
			case FRONT_BACK:
				return (fullTurn - j) % fullTurn;
			case LEFT_RIGHT:
				return (i - j + fullTurn) % fullTurn;
			default:
				return rotation;
		}
	}

	public BlockRotation getRotation(Direction direction) {
		Direction.Axis axis = direction.getAxis();
		return (this != LEFT_RIGHT || axis != Direction.Axis.Z) && (this != FRONT_BACK || axis != Direction.Axis.X)
			? BlockRotation.NONE
			: BlockRotation.CLOCKWISE_180;
	}

	public Direction apply(Direction direction) {
		switch (this) {
			case FRONT_BACK:
				if (direction == Direction.WEST) {
					return Direction.EAST;
				} else {
					if (direction == Direction.EAST) {
						return Direction.WEST;
					}

					return direction;
				}
			case LEFT_RIGHT:
				if (direction == Direction.NORTH) {
					return Direction.SOUTH;
				} else {
					if (direction == Direction.SOUTH) {
						return Direction.NORTH;
					}

					return direction;
				}
			default:
				return direction;
		}
	}

	static {
		int i = 0;

		for (BlockMirror blockMirror : values()) {
			NAMES[i++] = blockMirror.name;
		}
	}
}
