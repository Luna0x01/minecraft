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
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		BlockState blockState = view.getBlockState(pos.offset(direction));
		Block block = blockState.getBlock();
		if (this == Blocks.GLASS || this == Blocks.STAINED_GLASS) {
			if (state != blockState) {
				return true;
			}

			if (block == this) {
				return false;
			}
		}

		return !this.ignoreSimilar && block == this ? false : super.method_8654(state, view, pos, direction);
	}
}
