package net.minecraft.block.entity;

import net.minecraft.util.math.Direction;

public class EndPortalBlockEntity extends BlockEntity {
	public EndPortalBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public EndPortalBlockEntity() {
		this(BlockEntityType.field_11898);
	}

	public boolean shouldDrawSide(Direction direction) {
		return direction == Direction.field_11036;
	}
}
