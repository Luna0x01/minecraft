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
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		if (!world.getBlockState(pos.down()).getMaterial().isSolid()) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}

		super.neighborUpdate(state, world, pos, block, neighborPos);
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
