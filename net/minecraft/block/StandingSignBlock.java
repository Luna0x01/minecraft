package net.minecraft.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StandingSignBlock extends AbstractSignBlock {
	public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 15);

	public StandingSignBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		if (!world.getBlockState(pos.down()).getBlock().getMaterial().isSolid()) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}

		super.neighborUpdate(world, pos, state, block);
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
	protected StateManager appendProperties() {
		return new StateManager(this, ROTATION);
	}
}
