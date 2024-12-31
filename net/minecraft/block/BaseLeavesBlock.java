package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class BaseLeavesBlock extends Block {
	protected boolean fancyGraphics;

	protected BaseLeavesBlock(Material material, boolean bl) {
		super(material);
		this.fancyGraphics = bl;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return !this.fancyGraphics && view.getBlockState(pos).getBlock() == this ? false : super.isSideInvisible(view, pos, facing);
	}
}
