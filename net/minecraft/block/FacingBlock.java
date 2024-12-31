package net.minecraft.block;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;

public abstract class FacingBlock extends Block {
	public static final DirectionProperty FACING = Properties.FACING;

	protected FacingBlock(Block.Builder builder) {
		super(builder);
	}
}
