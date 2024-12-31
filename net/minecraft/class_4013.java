package net.minecraft;

import net.minecraft.block.BlockState;

public class class_4013 implements class_4014 {
	private final BlockState field_19473;
	private final BlockState field_19474;
	private final BlockState field_19475;

	public class_4013(BlockState blockState, BlockState blockState2, BlockState blockState3) {
		this.field_19473 = blockState;
		this.field_19474 = blockState2;
		this.field_19475 = blockState3;
	}

	@Override
	public BlockState method_17720() {
		return this.field_19473;
	}

	@Override
	public BlockState method_17721() {
		return this.field_19474;
	}

	public BlockState method_17719() {
		return this.field_19475;
	}
}
