package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3825 extends class_3823 {
	@Override
	protected boolean method_17315(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
		int i = random.nextInt(3) + 1;

		for (int j = 0; j < i; j++) {
			if (!this.method_17316(iWorld, random, mutable, blockState)) {
				return true;
			}

			mutable.move(Direction.UP);
		}

		BlockPos blockPos2 = mutable.toImmutable();
		int k = random.nextInt(3) + 2;
		List<Direction> list = Lists.newArrayList(Direction.DirectionType.HORIZONTAL);
		Collections.shuffle(list, random);

		for (Direction direction : list.subList(0, k)) {
			mutable.set(blockPos2);
			mutable.move(direction);
			int l = random.nextInt(5) + 2;
			int m = 0;

			for (int n = 0; n < l && this.method_17316(iWorld, random, mutable, blockState); n++) {
				m++;
				mutable.move(Direction.UP);
				if (n == 0 || m >= 2 && random.nextFloat() < 0.25F) {
					mutable.move(direction);
					m = 0;
				}
			}
		}

		return true;
	}
}
