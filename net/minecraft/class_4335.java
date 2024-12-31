package net.minecraft;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;
import net.minecraft.util.math.Direction;

public enum class_4335 {
	NORTH(Direction.NORTH),
	NORTH_EAST(Direction.NORTH, Direction.EAST),
	EAST(Direction.EAST),
	SOUTH_EAST(Direction.SOUTH, Direction.EAST),
	SOUTH(Direction.SOUTH),
	SOUTH_WEST(Direction.SOUTH, Direction.WEST),
	WEST(Direction.WEST),
	NORTH_WEST(Direction.NORTH, Direction.WEST);

	private static final int field_21301 = 1 << NORTH_WEST.ordinal();
	private static final int field_21302 = 1 << WEST.ordinal();
	private static final int field_21303 = 1 << SOUTH_WEST.ordinal();
	private static final int field_21304 = 1 << SOUTH.ordinal();
	private static final int field_21305 = 1 << SOUTH_EAST.ordinal();
	private static final int field_21306 = 1 << EAST.ordinal();
	private static final int field_21307 = 1 << NORTH_EAST.ordinal();
	private static final int field_21308 = 1 << NORTH.ordinal();
	private final Set<Direction> field_21309;

	private class_4335(Direction... directions) {
		this.field_21309 = Sets.immutableEnumSet(Arrays.asList(directions));
	}

	public Set<Direction> method_19951() {
		return this.field_21309;
	}
}
