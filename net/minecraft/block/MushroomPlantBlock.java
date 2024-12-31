package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.EntityContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;

public class MushroomPlantBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

	public MushroomPlantBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return SHAPE;
	}

	@Override
	public void scheduledTick(BlockState blockState, ServerWorld serverWorld, BlockPos blockPos, Random random) {
		if (random.nextInt(25) == 0) {
			int i = 5;
			int j = 4;

			for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-4, -1, -4), blockPos.add(4, 1, 4))) {
				if (serverWorld.getBlockState(blockPos2).getBlock() == this) {
					if (--i <= 0) {
						return;
					}
				}
			}

			BlockPos blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

			for (int k = 0; k < 4; k++) {
				if (serverWorld.isAir(blockPos3) && blockState.canPlaceAt(serverWorld, blockPos3)) {
					blockPos = blockPos3;
				}

				blockPos3 = blockPos.add(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
			}

			if (serverWorld.isAir(blockPos3) && blockState.canPlaceAt(serverWorld, blockPos3)) {
				serverWorld.setBlockState(blockPos3, blockState, 2);
			}
		}
	}

	@Override
	protected boolean canPlantOnTop(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return blockState.isFullOpaque(blockView, blockPos);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = worldView.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		return block != Blocks.field_10402 && block != Blocks.field_10520
			? worldView.getBaseLightLevel(blockPos, 0) < 13 && this.canPlantOnTop(blockState2, worldView, blockPos2)
			: true;
	}

	public boolean trySpawningBigMushroom(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState, Random random) {
		serverWorld.removeBlock(blockPos, false);
		ConfiguredFeature<HugeMushroomFeatureConfig, ?> configuredFeature;
		if (this == Blocks.field_10251) {
			configuredFeature = Feature.field_13531.configure(DefaultBiomeFeatures.HUGE_BROWN_MUSHROOM_CONFIG);
		} else {
			if (this != Blocks.field_10559) {
				serverWorld.setBlockState(blockPos, blockState, 3);
				return false;
			}

			configuredFeature = Feature.field_13571.configure(DefaultBiomeFeatures.HUGE_RED_MUSHROOM_CONFIG);
		}

		if (configuredFeature.generate(
			serverWorld, (ChunkGenerator<? extends ChunkGeneratorConfig>)serverWorld.getChunkManager().getChunkGenerator(), random, blockPos
		)) {
			return true;
		} else {
			serverWorld.setBlockState(blockPos, blockState, 3);
			return false;
		}
	}

	@Override
	public boolean isFertilizable(BlockView blockView, BlockPos blockPos, BlockState blockState, boolean bl) {
		return true;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos blockPos, BlockState blockState) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(ServerWorld serverWorld, Random random, BlockPos blockPos, BlockState blockState) {
		this.trySpawningBigMushroom(serverWorld, blockPos, blockState, random);
	}

	@Override
	public boolean shouldPostProcess(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return true;
	}
}
