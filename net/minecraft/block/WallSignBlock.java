package net.minecraft.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WallSignBlock extends AbstractSignBlock {
	public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.DirectionType.HORIZONTAL);

	public WallSignBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		Direction direction = view.getBlockState(pos).get(FACING);
		float f = 0.28125F;
		float g = 0.78125F;
		float h = 0.0F;
		float i = 1.0F;
		float j = 0.125F;
		this.setBoundingBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		switch (direction) {
			case NORTH:
				this.setBoundingBox(h, f, 1.0F - j, i, g, 1.0F);
				break;
			case SOUTH:
				this.setBoundingBox(h, f, 0.0F, i, g, j);
				break;
			case WEST:
				this.setBoundingBox(1.0F - j, f, h, 1.0F, g, i);
				break;
			case EAST:
				this.setBoundingBox(0.0F, f, h, j, g, i);
		}
	}

	@Override
	public void neighborUpdate(World world, BlockPos pos, BlockState state, Block block) {
		Direction direction = state.get(FACING);
		if (!world.getBlockState(pos.offset(direction.getOpposite())).getBlock().getMaterial().isSolid()) {
			this.dropAsItem(world, pos, state, 0);
			world.setAir(pos);
		}

		super.neighborUpdate(world, pos, state, block);
	}

	@Override
	public BlockState stateFromData(int data) {
		Direction direction = Direction.getById(data);
		if (direction.getAxis() == Direction.Axis.Y) {
			direction = Direction.NORTH;
		}

		return this.getDefaultState().with(FACING, direction);
	}

	@Override
	public int getData(BlockState state) {
		return ((Direction)state.get(FACING)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING);
	}
}
