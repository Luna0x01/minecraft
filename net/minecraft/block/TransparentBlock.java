package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public class TransparentBlock extends Block {
	private boolean ignoreSimilar;

	protected TransparentBlock(Material material, boolean bl) {
		this(material, bl, material.getColor());
	}

	protected TransparentBlock(Material material, boolean bl, MaterialColor materialColor) {
		super(material, materialColor);
		this.ignoreSimilar = bl;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		BlockState blockState = view.getBlockState(pos);
		Block block = blockState.getBlock();
		if (this == Blocks.GLASS || this == Blocks.STAINED_GLASS) {
			if (view.getBlockState(pos.offset(facing.getOpposite())) != blockState) {
				return true;
			}

			if (block == this) {
				return false;
			}
		}

		return !this.ignoreSimilar && block == this ? false : super.isSideInvisible(view, pos, facing);
	}
}
