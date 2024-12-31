package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;

public class class_3822 extends class_3823 {
	@Override
	protected boolean method_17315(IWorld iWorld, Random random, BlockPos blockPos, BlockState blockState) {
		if (!this.method_17316(iWorld, random, blockPos, blockState)) {
			return false;
		} else {
			Direction direction = Direction.DirectionType.HORIZONTAL.getRandomDirection(random);
			int i = random.nextInt(2) + 2;
			List<Direction> list = Lists.newArrayList(new Direction[]{direction, direction.rotateYClockwise(), direction.rotateYCounterclockwise()});
			Collections.shuffle(list, random);

			for (Direction direction2 : list.subList(0, i)) {
				BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
				int j = random.nextInt(2) + 1;
				mutable.move(direction2);
				int k;
				Direction direction3;
				if (direction2 == direction) {
					direction3 = direction;
					k = random.nextInt(3) + 2;
				} else {
					mutable.move(Direction.UP);
					Direction[] directions = new Direction[]{direction2, Direction.UP};
					direction3 = directions[random.nextInt(directions.length)];
					k = random.nextInt(3) + 3;
				}

				for (int m = 0; m < j && this.method_17316(iWorld, random, mutable, blockState); m++) {
					mutable.move(direction3);
				}

				mutable.move(direction3.getOpposite());
				mutable.move(Direction.UP);

				for (int n = 0; n < k; n++) {
					mutable.move(direction);
					if (!this.method_17316(iWorld, random, mutable, blockState)) {
						break;
					}

					if (random.nextFloat() < 0.25F) {
						mutable.move(Direction.UP);
					}
				}
			}

			return true;
		}
	}
}
