package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_3716;
import net.minecraft.block.enums.RailShape;
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
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class DetectorRailBlock extends AbstractRailBlock {
	public static final EnumProperty<RailShape> field_18279 = Properties.STRAIGHT_RAIL_SHAPE;
	public static final BooleanProperty field_18280 = Properties.POWERED;

	public DetectorRailBlock(Block.Builder builder) {
		super(true, builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18280, Boolean.valueOf(false)).withProperty(field_18279, RailShape.NORTH_SOUTH));
	}

	@Override
	public int getTickDelay(RenderBlockView world) {
		return 20;
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)state.getProperty(field_18280)) {
				this.updatePoweredStatus(world, pos, state);
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient && (Boolean)state.getProperty(field_18280)) {
			this.updatePoweredStatus(world, pos, state);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.getProperty(field_18280) ? 15 : 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		if (!(Boolean)state.getProperty(field_18280)) {
			return 0;
		} else {
			return direction == Direction.UP ? 15 : 0;
		}
	}

	private void updatePoweredStatus(World world, BlockPos pos, BlockState state) {
		boolean bl = (Boolean)state.getProperty(field_18280);
		boolean bl2 = false;
		List<AbstractMinecartEntity> list = this.method_16661(world, pos, AbstractMinecartEntity.class, null);
		if (!list.isEmpty()) {
			bl2 = true;
		}

		if (bl2 && !bl) {
			world.setBlockState(pos, state.withProperty(field_18280, Boolean.valueOf(true)), 3);
			this.method_11600(world, pos, state, true);
			world.updateNeighborsAlways(pos, this);
			world.updateNeighborsAlways(pos.down(), this);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (!bl2 && bl) {
			world.setBlockState(pos, state.withProperty(field_18280, Boolean.valueOf(false)), 3);
			this.method_11600(world, pos, state, false);
			world.updateNeighborsAlways(pos, this);
			world.updateNeighborsAlways(pos.down(), this);
			world.onRenderRegionUpdate(pos, pos);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(pos, this, this.getTickDelay(world));
		}

		world.updateHorizontalAdjacent(pos, this);
	}

	protected void method_11600(World world, BlockPos blockPos, BlockState blockState, boolean bl) {
		class_3716 lv = new class_3716(world, blockPos, blockState);

		for (BlockPos blockPos2 : lv.method_16714()) {
			BlockState blockState2 = world.getBlockState(blockPos2);
			blockState2.neighborUpdate(world, blockPos2, blockState2.getBlock(), blockPos);
		}
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			super.onBlockAdded(state, world, pos, oldState);
			this.updatePoweredStatus(world, pos, state);
		}
	}

	@Override
	public Property<RailShape> getShapeProperty() {
		return field_18279;
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		if ((Boolean)state.getProperty(field_18280)) {
			List<CommandBlockMinecartEntity> list = this.method_16661(world, pos, CommandBlockMinecartEntity.class, null);
			if (!list.isEmpty()) {
				return ((CommandBlockMinecartEntity)list.get(0)).getCommandExecutor().getSuccessCount();
			}

			List<AbstractMinecartEntity> list2 = this.method_16661(world, pos, AbstractMinecartEntity.class, EntityPredicate.field_16703);
			if (!list2.isEmpty()) {
				return ScreenHandler.calculateComparatorOutput((Inventory)list2.get(0));
			}
		}

		return 0;
	}

	protected <T extends AbstractMinecartEntity> List<T> method_16661(World world, BlockPos blockPos, Class<T> class_, @Nullable Predicate<Entity> predicate) {
		return world.method_16325(class_, this.getCartDetectionBox(blockPos), predicate);
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
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				switch ((RailShape)state.getProperty(field_18279)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18279, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(field_18279, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(field_18279, RailShape.NORTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18279, RailShape.SOUTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18279, RailShape.SOUTH_WEST);
				}
			case COUNTERCLOCKWISE_90:
				switch ((RailShape)state.getProperty(field_18279)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18279, RailShape.ASCENDING_NORTH);
					case ASCENDING_WEST:
						return state.withProperty(field_18279, RailShape.ASCENDING_SOUTH);
					case ASCENDING_NORTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_WEST);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_EAST);
					case SOUTH_EAST:
						return state.withProperty(field_18279, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(field_18279, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18279, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(field_18279, RailShape.NORTH_WEST);
					case NORTH_SOUTH:
						return state.withProperty(field_18279, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_SOUTH);
				}
			case CLOCKWISE_90:
				switch ((RailShape)state.getProperty(field_18279)) {
					case ASCENDING_EAST:
						return state.withProperty(field_18279, RailShape.ASCENDING_SOUTH);
					case ASCENDING_WEST:
						return state.withProperty(field_18279, RailShape.ASCENDING_NORTH);
					case ASCENDING_NORTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_EAST);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_WEST);
					case SOUTH_EAST:
						return state.withProperty(field_18279, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18279, RailShape.SOUTH_EAST);
					case NORTH_SOUTH:
						return state.withProperty(field_18279, RailShape.EAST_WEST);
					case EAST_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_SOUTH);
				}
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		RailShape railShape = state.getProperty(field_18279);
		switch (mirror) {
			case LEFT_RIGHT:
				switch (railShape) {
					case ASCENDING_NORTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_SOUTH);
					case ASCENDING_SOUTH:
						return state.withProperty(field_18279, RailShape.ASCENDING_NORTH);
					case SOUTH_EAST:
						return state.withProperty(field_18279, RailShape.NORTH_EAST);
					case SOUTH_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_WEST);
					case NORTH_WEST:
						return state.withProperty(field_18279, RailShape.SOUTH_WEST);
					case NORTH_EAST:
						return state.withProperty(field_18279, RailShape.SOUTH_EAST);
					default:
						return super.withMirror(state, mirror);
				}
			case FRONT_BACK:
				switch (railShape) {
					case ASCENDING_EAST:
						return state.withProperty(field_18279, RailShape.ASCENDING_WEST);
					case ASCENDING_WEST:
						return state.withProperty(field_18279, RailShape.ASCENDING_EAST);
					case ASCENDING_NORTH:
					case ASCENDING_SOUTH:
					default:
						break;
					case SOUTH_EAST:
						return state.withProperty(field_18279, RailShape.SOUTH_WEST);
					case SOUTH_WEST:
						return state.withProperty(field_18279, RailShape.SOUTH_EAST);
					case NORTH_WEST:
						return state.withProperty(field_18279, RailShape.NORTH_EAST);
					case NORTH_EAST:
						return state.withProperty(field_18279, RailShape.NORTH_WEST);
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18279, field_18280);
	}
}
