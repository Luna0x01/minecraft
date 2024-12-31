package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class FrostedIceBlock extends IceBlock {
	public static final IntProperty field_12680 = IntProperty.of("age", 0, 3);

	public FrostedIceBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(field_12680, 0));
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(field_12680);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(field_12680, MathHelper.clamp(data, 0, 3));
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if ((rand.nextInt(3) == 0 || this.method_11614(world, pos) < 4)
			&& world.getLightLevelWithNeighbours(pos) > 11 - (Integer)state.get(field_12680) - state.getOpacity()) {
			this.method_11613(world, pos, state, rand, true);
		} else {
			world.createAndScheduleBlockTick(pos, this, MathHelper.nextInt(rand, 20, 40));
		}
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (block == this) {
			int i = this.method_11614(world, blockPos);
			if (i < 2) {
				this.method_11617(world, blockPos);
			}
		}
	}

	private int method_11614(World world, BlockPos blockPos) {
		int i = 0;

		for (Direction direction : Direction.values()) {
			if (world.getBlockState(blockPos.offset(direction)).getBlock() == this) {
				if (++i >= 4) {
					return i;
				}
			}
		}

		return i;
	}

	protected void method_11613(World world, BlockPos blockPos, BlockState blockState, Random random, boolean bl) {
		int i = (Integer)blockState.get(field_12680);
		if (i < 3) {
			world.setBlockState(blockPos, blockState.with(field_12680, i + 1), 2);
			world.createAndScheduleBlockTick(blockPos, this, MathHelper.nextInt(random, 20, 40));
		} else {
			this.method_11617(world, blockPos);
			if (bl) {
				for (Direction direction : Direction.values()) {
					BlockPos blockPos2 = blockPos.offset(direction);
					BlockState blockState2 = world.getBlockState(blockPos2);
					if (blockState2.getBlock() == this) {
						this.method_11613(world, blockPos2, blockState2, random, false);
					}
				}
			}
		}
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, field_12680);
	}

	@Nullable
	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return null;
	}
}
