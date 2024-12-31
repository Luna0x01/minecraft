package net.minecraft.block;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeMushroomFeature;

public class MushroomPlantBlock extends PlantBlock implements Growable {
	protected MushroomPlantBlock() {
		float f = 0.2F;
		this.setBoundingBox(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
		this.setTickRandomly(true);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (rand.nextInt(25) == 0) {
			int i = 5;
			int j = 4;

			for (BlockPos blockPos : BlockPos.mutableIterate(pos.add(-4, -1, -4), pos.add(4, 1, 4))) {
				if (world.getBlockState(blockPos).getBlock() == this) {
					if (--i <= 0) {
						return;
					}
				}
			}

			BlockPos blockPos2 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);

			for (int k = 0; k < 4; k++) {
				if (world.isAir(blockPos2) && this.canPlantAt(world, blockPos2, this.getDefaultState())) {
					pos = blockPos2;
				}

				blockPos2 = pos.add(rand.nextInt(3) - 1, rand.nextInt(2) - rand.nextInt(2), rand.nextInt(3) - 1);
			}

			if (world.isAir(blockPos2) && this.canPlantAt(world, blockPos2, this.getDefaultState())) {
				world.setBlockState(blockPos2, this.getDefaultState(), 2);
			}
		}
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return super.canBePlacedAtPos(world, pos) && this.canPlantAt(world, pos, this.getDefaultState());
	}

	@Override
	protected boolean canPlantOnTop(Block block) {
		return block.isFullBlock();
	}

	@Override
	public boolean canPlantAt(World world, BlockPos pos, BlockState state) {
		if (pos.getY() >= 0 && pos.getY() < 256) {
			BlockState blockState = world.getBlockState(pos.down());
			if (blockState.getBlock() == Blocks.MYCELIUM) {
				return true;
			} else {
				return blockState.getBlock() == Blocks.DIRT && blockState.get(DirtBlock.VARIANT) == DirtBlock.DirtType.PODZOL
					? true
					: world.getLightLevel(pos) < 13 && this.canPlantOnTop(blockState.getBlock());
			}
		} else {
			return false;
		}
	}

	public boolean growIntoGiantMushroom(World world, BlockPos pos, BlockState state, Random random) {
		world.setAir(pos);
		Feature feature = null;
		if (this == Blocks.BROWN_MUSHROOM) {
			feature = new HugeMushroomFeature(Blocks.BROWN_MUSHROOM_BLOCK);
		} else if (this == Blocks.RED_MUSHROOM) {
			feature = new HugeMushroomFeature(Blocks.RED_MUSHROOM_BLOCK);
		}

		if (feature != null && feature.generate(world, random, pos)) {
			return true;
		} else {
			world.setBlockState(pos, state, 3);
			return false;
		}
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.growIntoGiantMushroom(world, pos, state, random);
	}
}
