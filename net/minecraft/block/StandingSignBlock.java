package net.minecraft.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StandingSignBlock extends AbstractSignBlock {
	public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 15);

	public StandingSignBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!world.getBlockState(blockPos.down()).getMaterial().isSolid()) {
			this.dropAsItem(world, blockPos, blockState, 0);
			world.setAir(blockPos);
		}

		super.method_8641(blockState, world, blockPos, block);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(ROTATION, data);
	}

	@Override
	public int getData(BlockState state) {
		return (Integer)state.get(ROTATION);
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(ROTATION, rotation.rotate((Integer)state.get(ROTATION), 16));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.with(ROTATION, mirror.mirror((Integer)state.get(ROTATION), 16));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, ROTATION);
	}
}
