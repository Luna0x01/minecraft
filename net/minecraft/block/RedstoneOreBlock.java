package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_4338;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneOreBlock extends Block {
	public static final BooleanProperty field_18442 = RedstoneTorchBlock.field_18451;

	public RedstoneOreBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.getDefaultState().withProperty(field_18442, Boolean.valueOf(false)));
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.getProperty(field_18442) ? super.getLuminance(state) : 0;
	}

	@Override
	public void method_420(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity) {
		method_16727(blockState, world, blockPos);
		super.method_420(blockState, world, blockPos, playerEntity);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		method_16727(world.getBlockState(pos), world, pos);
		super.onSteppedOn(world, pos, entity);
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		method_16727(state, world, pos);
		return super.onUse(state, world, pos, player, hand, direction, distanceX, distanceY, distanceZ);
	}

	private static void method_16727(BlockState blockState, World world, BlockPos blockPos) {
		emitParticles(world, blockPos);
		if (!(Boolean)blockState.getProperty(field_18442)) {
			world.setBlockState(blockPos, blockState.withProperty(field_18442, Boolean.valueOf(true)), 3);
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18442)) {
			world.setBlockState(pos, state.withProperty(field_18442, Boolean.valueOf(false)), 3);
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.REDSTONE;
	}

	@Override
	public int method_397(BlockState blockState, int i, World world, BlockPos blockPos, Random random) {
		return this.getDropCount(blockState, random) + random.nextInt(i + 1);
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return 4 + random.nextInt(2);
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		super.method_410(blockState, world, blockPos, f, i);
		if (this.getDroppedItem(blockState, world, blockPos, i) != this) {
			int j = 1 + world.random.nextInt(5);
			this.dropExperience(world, blockPos, j);
		}
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if ((Boolean)state.getProperty(field_18442)) {
			emitParticles(world, pos);
		}
	}

	private static void emitParticles(World world2, BlockPos world) {
		double d = 0.5625;
		Random random = world2.random;

		for (Direction direction : Direction.values()) {
			BlockPos blockPos = world.offset(direction);
			if (!world2.getBlockState(blockPos).isFullOpaque(world2, blockPos)) {
				Direction.Axis axis = direction.getAxis();
				double e = axis == Direction.Axis.X ? 0.5 + 0.5625 * (double)direction.getOffsetX() : (double)random.nextFloat();
				double f = axis == Direction.Axis.Y ? 0.5 + 0.5625 * (double)direction.getOffsetY() : (double)random.nextFloat();
				double g = axis == Direction.Axis.Z ? 0.5 + 0.5625 * (double)direction.getOffsetZ() : (double)random.nextFloat();
				world2.method_16343(class_4338.field_21339, (double)world.getX() + e, (double)world.getY() + f, (double)world.getZ() + g, 0.0, 0.0, 0.0);
			}
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18442);
	}
}
