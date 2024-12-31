package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4337;
import net.minecraft.class_4342;
import net.minecraft.block.material.Material;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class FallingBlock extends Block {
	public static boolean instantFall;

	public FallingBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			this.scheduledTick(world, pos);
		}
	}

	private void scheduledTick(World world, BlockPos pos) {
		if (canFallThough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
			int i = 32;
			if (!instantFall && world.method_16385(pos.add(-32, -32, -32), pos.add(32, 32, 32))) {
				if (!world.isClient) {
					FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
						world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, world.getBlockState(pos)
					);
					this.configureFallingBlockEntity(fallingBlockEntity);
					world.method_3686(fallingBlockEntity);
				}
			} else {
				if (world.getBlockState(pos).getBlock() == this) {
					world.method_8553(pos);
				}

				BlockPos blockPos = pos.down();

				while (canFallThough(world.getBlockState(blockPos)) && blockPos.getY() > 0) {
					blockPos = blockPos.down();
				}

				if (blockPos.getY() > 0) {
					world.setBlockState(blockPos.up(), this.getDefaultState());
				}
			}
		}
	}

	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 2;
	}

	public static boolean canFallThough(BlockState pos) {
		Block block = pos.getBlock();
		Material material = pos.getMaterial();
		return pos.isAir() || block == Blocks.FIRE || material.isFluid() || material.isReplaceable();
	}

	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
	}

	public void method_13705(World world, BlockPos blockPos) {
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos blockPos = pos.down();
			if (canFallThough(world.getBlockState(blockPos))) {
				double d = (double)((float)pos.getX() + random.nextFloat());
				double e = (double)pos.getY() - 0.05;
				double f = (double)((float)pos.getZ() + random.nextFloat());
				world.method_16343(new class_4337(class_4342.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	public int getColor(BlockState state) {
		return -16777216;
	}
}
