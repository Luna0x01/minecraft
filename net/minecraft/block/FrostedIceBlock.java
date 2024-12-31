package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FrostedIceBlock extends IceBlock {
	public static final IntProperty field_18347 = Properties.AGE_3;

	public FrostedIceBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18347, Integer.valueOf(0)));
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((random.nextInt(3) == 0 || this.method_16681(world, pos, 4))
			&& world.method_16358(pos) > 11 - (Integer)state.getProperty(field_18347) - state.method_16885(world, pos)
			&& this.method_16682(state, world, pos)) {
			try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
				for (Direction direction : Direction.values()) {
					pooled.set(pos).move(direction);
					BlockState blockState = world.getBlockState(pooled);
					if (blockState.getBlock() == this && !this.method_16682(blockState, world, pooled)) {
						world.getBlockTickScheduler().schedule(pooled, this, MathHelper.nextInt(random, 20, 40));
					}
				}
			}
		} else {
			world.getBlockTickScheduler().schedule(pos, this, MathHelper.nextInt(random, 20, 40));
		}
	}

	private boolean method_16682(BlockState blockState, World world, BlockPos blockPos) {
		int i = (Integer)blockState.getProperty(field_18347);
		if (i < 3) {
			world.setBlockState(blockPos, blockState.withProperty(field_18347, Integer.valueOf(i + 1)), 2);
			return false;
		} else {
			this.method_11617(blockState, world, blockPos);
			return true;
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (block == this && this.method_16681(world, pos, 2)) {
			this.method_11617(state, world, pos);
		}

		super.neighborUpdate(state, world, pos, block, neighborPos);
	}

	private boolean method_16681(BlockView blockView, BlockPos blockPos, int i) {
		int j = 0;

		try (BlockPos.Pooled pooled = BlockPos.Pooled.get()) {
			for (Direction direction : Direction.values()) {
				pooled.set(blockPos).move(direction);
				if (blockView.getBlockState(pooled).getBlock() == this) {
					if (++j >= i) {
						return false;
					}
				}
			}

			return true;
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18347);
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}
}
