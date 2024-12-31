package net.minecraft.block;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RailBlock extends AbstractRailBlock {
	public static final EnumProperty<AbstractRailBlock.RailShapeType> SHAPE = EnumProperty.of("shape", AbstractRailBlock.RailShapeType.class);

	protected RailBlock() {
		super(false);
		this.setDefaultState(this.stateManager.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_SOUTH));
	}

	@Override
	protected void updateBlockState(World world, BlockPos pos, BlockState state, Block block) {
		if (block.emitsRedstonePower() && new AbstractRailBlock.RailPlacementHelper(world, pos, state).getVerticalNearbyRailCount() == 3) {
			this.updateBlockState(world, pos, state, false);
		}
	}

	@Override
	public Property<AbstractRailBlock.RailShapeType> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((AbstractRailBlock.RailShapeType)state.get(SHAPE)).getData();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, SHAPE);
	}
}
