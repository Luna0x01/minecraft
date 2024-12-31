package net.minecraft;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class class_3736 extends Block {
	protected class_3736(Block.Builder builder) {
		super(builder);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction direction = Direction.random(random);
		if (direction != Direction.UP && !world.getBlockState(pos.offset(direction)).method_16913()) {
			double d = (double)pos.getX();
			double e = (double)pos.getY();
			double f = (double)pos.getZ();
			if (direction == Direction.DOWN) {
				e -= 0.05;
				d += random.nextDouble();
				f += random.nextDouble();
			} else {
				e += random.nextDouble() * 0.8;
				if (direction.getAxis() == Direction.Axis.X) {
					f += random.nextDouble();
					if (direction == Direction.EAST) {
						d++;
					} else {
						d += 0.05;
					}
				} else {
					d += random.nextDouble();
					if (direction == Direction.SOUTH) {
						f++;
					} else {
						f += 0.05;
					}
				}
			}

			world.method_16343(class_4342.field_21386, d, e, f, 0.0, 0.0, 0.0);
		}
	}
}
