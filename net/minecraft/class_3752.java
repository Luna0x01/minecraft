package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.FoliageFeature;
import net.minecraft.world.gen.feature.GiantJungleTreeFeature;

public class class_3752 extends class_3747 {
	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16850(Random random) {
		return new class_3910(true, 4 + random.nextInt(7), Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), false);
	}

	@Nullable
	@Override
	protected FoliageFeature<class_3871> method_16848(Random random) {
		return new GiantJungleTreeFeature(true, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState());
	}
}
