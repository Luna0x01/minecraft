package net.minecraft.block;

import java.util.Random;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WetSpongeBlock extends Block {
	protected WetSpongeBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (world.getDimension().doesWaterVaporize()) {
			world.setBlockState(blockPos, Blocks.field_10258.getDefaultState(), 3);
			world.playLevelEvent(2009, blockPos, 0);
			world.playSound(null, blockPos, SoundEvents.field_15102, SoundCategory.field_15245, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
		}
	}

	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		Direction direction = Direction.random(random);
		if (direction != Direction.field_11036) {
			BlockPos blockPos2 = blockPos.offset(direction);
			BlockState blockState2 = world.getBlockState(blockPos2);
			if (!blockState.isOpaque() || !blockState2.isSideSolidFullSquare(world, blockPos2, direction.getOpposite())) {
				double d = (double)blockPos.getX();
				double e = (double)blockPos.getY();
				double f = (double)blockPos.getZ();
				if (direction == Direction.field_11033) {
					e -= 0.05;
					d += random.nextDouble();
					f += random.nextDouble();
				} else {
					e += random.nextDouble() * 0.8;
					if (direction.getAxis() == Direction.Axis.field_11048) {
						f += random.nextDouble();
						if (direction == Direction.field_11034) {
							d++;
						} else {
							d += 0.05;
						}
					} else {
						d += random.nextDouble();
						if (direction == Direction.field_11035) {
							f++;
						} else {
							f += 0.05;
						}
					}
				}

				world.addParticle(ParticleTypes.field_11232, d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}
}
