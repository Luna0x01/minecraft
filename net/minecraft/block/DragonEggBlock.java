package net.minecraft.block;

import net.minecraft.class_4342;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class DragonEggBlock extends FallingBlock {
	protected static final VoxelShape field_18303 = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

	public DragonEggBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18303;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		this.method_8759(state, world, pos);
		return true;
	}

	@Override
	public void method_420(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		this.method_8759(blockState, world, blockPos);
	}

	private void method_8759(BlockState blockState, World world, BlockPos blockPos) {
		for (int i = 0; i < 1000; i++) {
			BlockPos blockPos2 = blockPos.add(
				world.random.nextInt(16) - world.random.nextInt(16), world.random.nextInt(8) - world.random.nextInt(8), world.random.nextInt(16) - world.random.nextInt(16)
			);
			if (world.getBlockState(blockPos2).isAir()) {
				if (world.isClient) {
					for (int j = 0; j < 128; j++) {
						double d = world.random.nextDouble();
						float f = (world.random.nextFloat() - 0.5F) * 0.2F;
						float g = (world.random.nextFloat() - 0.5F) * 0.2F;
						float h = (world.random.nextFloat() - 0.5F) * 0.2F;
						double e = (double)blockPos2.getX() + (double)(blockPos.getX() - blockPos2.getX()) * d + (world.random.nextDouble() - 0.5) + 0.5;
						double k = (double)blockPos2.getY() + (double)(blockPos.getY() - blockPos2.getY()) * d + world.random.nextDouble() - 0.5;
						double l = (double)blockPos2.getZ() + (double)(blockPos.getZ() - blockPos2.getZ()) * d + (world.random.nextDouble() - 0.5) + 0.5;
						world.method_16343(class_4342.field_21361, e, k, l, (double)f, (double)g, (double)h);
					}
				} else {
					world.setBlockState(blockPos2, blockState, 2);
					world.method_8553(blockPos);
				}

				return;
			}
		}
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 5;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
