package net.minecraft.block;

import java.util.Random;
import net.minecraft.class_3748;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SaplingBlock extends PlantBlock implements Growable {
	public static final IntProperty field_18462 = Properties.STAGE;
	protected static final VoxelShape field_18463 = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
	private final class_3748 field_18464;

	protected SaplingBlock(class_3748 arg, Block.Builder builder) {
		super(builder);
		this.field_18464 = arg;
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18462, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18463;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		super.scheduledTick(state, world, pos, random);
		if (world.method_16358(pos.up()) >= 9 && random.nextInt(7) == 0) {
			this.method_16734(world, pos, state, random);
		}
	}

	public void method_16734(IWorld iWorld, BlockPos blockPos, BlockState blockState, Random random) {
		if ((Integer)blockState.getProperty(field_18462) == 0) {
			iWorld.setBlockState(blockPos, blockState.method_16930(field_18462), 4);
		} else {
			this.field_18464.method_16849(iWorld, blockPos, blockState, random);
		}
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return (double)world.random.nextFloat() < 0.45;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		this.method_16734(world, pos, state, random);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18462);
	}
}
