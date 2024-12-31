package net.minecraft.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class LogBlock extends PillarBlock {
	private final MaterialColor field_18406;

	public LogBlock(MaterialColor materialColor, Block.Builder builder) {
		super(builder);
		this.field_18406 = materialColor;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return state.getProperty(PILLAR_AXIS) == Direction.Axis.Y ? this.field_18406 : this.materialColor;
	}
}
