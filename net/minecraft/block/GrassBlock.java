package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.class_3726;
import net.minecraft.class_3847;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GrassBlock extends class_3726 implements Growable {
	public GrassBlock(Block.Builder builder) {
		super(builder);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		BlockPos blockPos = pos.up();
		BlockState blockState = Blocks.GRASS.getDefaultState();

		label48:
		for (int i = 0; i < 128; i++) {
			BlockPos blockPos2 = blockPos;

			for (int j = 0; j < i / 16; j++) {
				blockPos2 = blockPos2.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
				if (world.getBlockState(blockPos2.down()).getBlock() != this || world.getBlockState(blockPos2).method_16905()) {
					continue label48;
				}
			}

			BlockState blockState2 = world.getBlockState(blockPos2);
			if (blockState2.getBlock() == blockState.getBlock() && random.nextInt(10) == 0) {
				((Growable)blockState.getBlock()).grow(world, random, blockPos2, blockState2);
			}

			if (blockState2.isAir()) {
				BlockState blockState3;
				if (random.nextInt(8) == 0) {
					List<class_3847<?>> list = world.method_8577(blockPos2).method_16444();
					if (list.isEmpty()) {
						continue;
					}

					blockState3 = ((class_3847)list.get(0)).method_17348(random, blockPos2);
				} else {
					blockState3 = blockState;
				}

				if (blockState3.canPlaceAt(world, blockPos2)) {
					world.setBlockState(blockPos2, blockState3, 3);
				}
			}
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return true;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}
}
