package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.class_3798;
import net.minecraft.class_3844;
import net.minecraft.class_3871;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

public class class_2754 extends class_3844<class_3871> {
	public boolean method_17343(IWorld iWorld, ChunkGenerator<? extends class_3798> chunkGenerator, Random random, BlockPos blockPos, class_3871 arg) {
		float f = (float)(random.nextInt(3) + 4);

		for (int i = 0; f > 0.5F; i--) {
			for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); j++) {
				for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); k++) {
					if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
						this.method_17344(iWorld, blockPos.add(j, i, k), Blocks.END_STONE.getDefaultState());
					}
				}
			}

			f = (float)((double)f - ((double)random.nextInt(2) + 0.5));
		}

		return true;
	}
}
