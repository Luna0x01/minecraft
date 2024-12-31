package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DoublePlantFeature extends Feature {
	private DoublePlantBlock.DoublePlantType type;

	public void setType(DoublePlantBlock.DoublePlantType type) {
		this.type = type;
	}

	@Override
	public boolean generate(World world, Random random, BlockPos blockPos) {
		boolean bl = false;

		for (int i = 0; i < 64; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(blockPos2) && (!world.dimension.hasNoSkylight() || blockPos2.getY() < 254) && Blocks.DOUBLE_PLANT.canBePlacedAtPos(world, blockPos2)) {
				Blocks.DOUBLE_PLANT.plantAt(world, blockPos2, this.type, 2);
				bl = true;
			}
		}

		return bl;
	}
}
