package net.minecraft;

import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3824 extends class_3823 {
	@Override
	protected boolean method_17315(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		int i = random.nextInt(3) + 3;
		int j = random.nextInt(3) + 3;
		int k = random.nextInt(3) + 3;
		int l = random.nextInt(3) + 1;
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);

		for (int m = 0; m <= j; m++) {
			for (int n = 0; n <= i; n++) {
				for (int o = 0; o <= k; o++) {
					mutable.setPosition(m + blockPos.getX(), n + blockPos.getY(), o + blockPos.getZ());
					mutable.move(Direction.DOWN, l);
					if ((m != 0 && m != j || n != 0 && n != i)
						&& (o != 0 && o != k || n != 0 && n != i)
						&& (m != 0 && m != j || o != 0 && o != k)
						&& (m == 0 || m == j || n == 0 || n == i || o == 0 || o == k)
						&& !(random.nextFloat() < 0.1F)
						&& !this.method_17316(iWorld, random, mutable, blockState)) {
					}
				}
			}
		}

		return true;
	}
}
