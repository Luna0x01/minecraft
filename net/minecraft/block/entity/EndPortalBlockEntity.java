package net.minecraft.block.entity;

import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity extends BlockEntity {
	public boolean method_11689(Direction direction) {
		return direction == Direction.UP;
	}
}
