package net.minecraft.block.entity;

import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity extends BlockEntity {
	public EndPortalBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public EndPortalBlockEntity() {
		this(BlockEntityType.END_PORTAL);
	}

	public boolean method_11689(Direction direction) {
		return direction == Direction.UP;
	}
}
