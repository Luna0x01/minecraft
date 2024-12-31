package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class RedstoneBlock extends Block {
	public RedstoneBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return 15;
	}
}
