package net.minecraft;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;

public class class_3746 extends ChestBlockEntity {
	public class_3746() {
		super(BlockEntityType.TRAPPED_CHEST);
	}

	@Override
	protected void method_16795() {
		super.method_16795();
		this.world.updateNeighborsAlways(this.pos.down(), this.method_16783().getBlock());
	}
}
