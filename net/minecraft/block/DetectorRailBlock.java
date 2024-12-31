package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.predicate.EntityPredicate;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DetectorRailBlock extends AbstractRailBlock {
	public static final EnumProperty<AbstractRailBlock.RailShapeType> SHAPE = EnumProperty.of(
		"shape",
		AbstractRailBlock.RailShapeType.class,
		new Predicate<AbstractRailBlock.RailShapeType>() {
			public boolean apply(AbstractRailBlock.RailShapeType railShapeType) {
				return railShapeType != AbstractRailBlock.RailShapeType.NORTH_EAST
					&& railShapeType != AbstractRailBlock.RailShapeType.NORTH_WEST
					&& railShapeType != AbstractRailBlock.RailShapeType.SOUTH_EAST
					&& railShapeType != AbstractRailBlock.RailShapeType.SOUTH_WEST;
			}
		}
	);
	public static final BooleanProperty POWERED = BooleanProperty.of("powered");

	public DetectorRailBlock() {
		super(true);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(SHAPE, AbstractRailBlock.RailShapeType.NORTH_SOUTH));
		this.setTickRandomly(true);
	}

	@Override
	public int getTickRate(World world) {
		return 20;
	}

	@Override
	public boolean emitsRedstonePower() {
		return true;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)state.get(POWERED)) {
				this.updatePoweredStatus(world, pos, state);
			}
		}
	}

	@Override
	public void onRandomTick(World world, BlockPos pos, BlockState state, Random rand) {
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!world.isClient && (Boolean)state.get(POWERED)) {
			this.updatePoweredStatus(world, pos, state);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		return state.get(POWERED) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockView view, BlockPos pos, BlockState state, Direction facing) {
		if (!(Boolean)state.get(POWERED)) {
			return 0;
		} else {
			return facing == Direction.UP ? 15 : 0;
		}
	}

	private void updatePoweredStatus(World world, BlockPos pos, BlockState state) {
		boolean bl = (Boolean)state.get(POWERED);
		boolean bl2 = false;
		List<AbstractMinecartEntity> list = this.getDetectedMinecarts(world, pos, AbstractMinecartEntity.class);
		if (!list.isEmpty()) {
			bl2 = true;
		}

		if (bl2 && !bl) {
			world.setBlockState(pos, state.with(POWERED, true), 3);
			world.updateNeighborsAlways(pos, this);
			world.updateNeighborsAlways(pos.down(), this);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (!bl2 && bl) {
			world.setBlockState(pos, state.with(POWERED, false), 3);
			world.updateNeighborsAlways(pos, this);
			world.updateNeighborsAlways(pos.down(), this);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (bl2) {
			world.createAndScheduleBlockTick(pos, this, this.getTickRate(world));
		}

		world.updateHorizontalAdjacent(pos, this);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		super.onCreation(world, pos, state);
		this.updatePoweredStatus(world, pos, state);
	}

	@Override
	public Property<AbstractRailBlock.RailShapeType> getShapeProperty() {
		return SHAPE;
	}

	@Override
	public boolean hasComparatorOutput() {
		return true;
	}

	@Override
	public int getComparatorOutput(World world, BlockPos pos) {
		if ((Boolean)world.getBlockState(pos).get(POWERED)) {
			List<CommandBlockMinecartEntity> list = this.getDetectedMinecarts(world, pos, CommandBlockMinecartEntity.class);
			if (!list.isEmpty()) {
				return ((CommandBlockMinecartEntity)list.get(0)).getCommandExecutor().getSuccessCount();
			}

			List<AbstractMinecartEntity> list2 = this.getDetectedMinecarts(world, pos, AbstractMinecartEntity.class, EntityPredicate.VALID_INVENTORY);
			if (!list2.isEmpty()) {
				return ScreenHandler.calculateComparatorOutput((Inventory)list2.get(0));
			}
		}

		return 0;
	}

	protected <T extends AbstractMinecartEntity> List<T> getDetectedMinecarts(World world, BlockPos pos, Class<T> minecartType, Predicate<Entity>... entities) {
		Box box = this.getCartDetectionBox(pos);
		return entities.length != 1 ? world.getEntitiesInBox(minecartType, box) : world.getEntitiesInBox(minecartType, box, entities[0]);
	}

	private Box getCartDetectionBox(BlockPos pos) {
		float f = 0.2F;
		return new Box(
			(double)((float)pos.getX() + 0.2F),
			(double)pos.getY(),
			(double)((float)pos.getZ() + 0.2F),
			(double)((float)(pos.getX() + 1) - 0.2F),
			(double)((float)(pos.getY() + 1) - 0.2F),
			(double)((float)(pos.getZ() + 1) - 0.2F)
		);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(SHAPE, AbstractRailBlock.RailShapeType.getById(data & 7)).with(POWERED, (data & 8) > 0);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((AbstractRailBlock.RailShapeType)state.get(SHAPE)).getData();
		if ((Boolean)state.get(POWERED)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, SHAPE, POWERED);
	}
}
