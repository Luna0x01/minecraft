package net.minecraft.block;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PotatoesBlock extends CropBlock {
	private static final VoxelShape[] field_18428 = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 9.0, 16.0)
	};

	public PotatoesBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	protected Itemable getSeedsItem() {
		return Items.POTATO;
	}

	@Override
	protected Itemable getHarvestItem() {
		return Items.POTATO;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, i);
		if (!world.isClient) {
			if (this.isMature(blockState) && world.random.nextInt(50) == 0) {
				onBlockBreak(world, blockPos, new ItemStack(Items.POISONOUS_POTATO));
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18428[state.getProperty(this.getAge())];
	}
}
