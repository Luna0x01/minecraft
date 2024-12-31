package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class NetherWartBlock extends PlantBlock {
	public static final IntProperty field_18413 = Properties.AGE_3;
	private static final VoxelShape[] field_18414 = new VoxelShape[]{
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 11.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0)
	};

	protected NetherWartBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18413, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18414[state.getProperty(field_18413)];
	}

	@Override
	protected boolean canPlantOnTop(BlockState state, BlockView world, BlockPos pos) {
		return state.getBlock() == Blocks.SOULSAND;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		int i = (Integer)state.getProperty(field_18413);
		if (i < 3 && random.nextInt(10) == 0) {
			state = state.withProperty(field_18413, Integer.valueOf(i + 1));
			world.setBlockState(pos, state, 2);
		}

		super.scheduledTick(state, world, pos, random);
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!world.isClient) {
			int j = 1;
			if ((Integer)blockState.getProperty(field_18413) >= 3) {
				j = 2 + world.random.nextInt(3);
				if (i > 0) {
					j += world.random.nextInt(i + 1);
				}
			}

			for (int k = 0; k < j; k++) {
				onBlockBreak(world, blockPos, new ItemStack(Items.NETHER_WART));
			}
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return new ItemStack(Items.NETHER_WART);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18413);
	}
}
