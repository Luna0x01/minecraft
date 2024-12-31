package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.class_3703;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TripwireBlock extends Block {
	public static final BooleanProperty field_18542 = Properties.POWERED;
	public static final BooleanProperty field_18543 = Properties.ATTACHED;
	public static final BooleanProperty field_18544 = Properties.DISARMED;
	public static final BooleanProperty field_18545 = ConnectingBlock.NORTH;
	public static final BooleanProperty field_18546 = ConnectingBlock.EAST;
	public static final BooleanProperty field_18547 = ConnectingBlock.SOUTH;
	public static final BooleanProperty field_18548 = ConnectingBlock.WEST;
	private static final Map<Direction, BooleanProperty> field_18551 = class_3703.field_18270;
	protected static final VoxelShape field_18549 = Block.createCuboidShape(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
	protected static final VoxelShape field_18550 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
	private final TripwireHookBlock field_18552;

	public TripwireBlock(TripwireHookBlock tripwireHookBlock, Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(field_18542, Boolean.valueOf(false))
				.withProperty(field_18543, Boolean.valueOf(false))
				.withProperty(field_18544, Boolean.valueOf(false))
				.withProperty(field_18545, Boolean.valueOf(false))
				.withProperty(field_18546, Boolean.valueOf(false))
				.withProperty(field_18547, Boolean.valueOf(false))
				.withProperty(field_18548, Boolean.valueOf(false))
		);
		this.field_18552 = tripwireHookBlock;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return state.getProperty(field_18543) ? field_18549 : field_18550;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockView blockView = context.getWorld();
		BlockPos blockPos = context.getBlockPos();
		return this.getDefaultState()
			.withProperty(field_18545, Boolean.valueOf(this.method_16752(blockView.getBlockState(blockPos.north()), Direction.NORTH)))
			.withProperty(field_18546, Boolean.valueOf(this.method_16752(blockView.getBlockState(blockPos.east()), Direction.EAST)))
			.withProperty(field_18547, Boolean.valueOf(this.method_16752(blockView.getBlockState(blockPos.south()), Direction.SOUTH)))
			.withProperty(field_18548, Boolean.valueOf(this.method_16752(blockView.getBlockState(blockPos.west()), Direction.WEST)));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction.getAxis().isHorizontal()
			? state.withProperty((Property)field_18551.get(direction), Boolean.valueOf(this.method_16752(neighborState, direction)))
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState) {
		if (oldState.getBlock() != state.getBlock()) {
			this.update(world, pos, state);
		}
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && state.getBlock() != newState.getBlock()) {
			this.update(world, pos, state.withProperty(field_18542, Boolean.valueOf(true)));
		}
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClient && !player.getMainHandStack().isEmpty() && player.getMainHandStack().getItem() == Items.SHEARS) {
			world.setBlockState(pos, state.withProperty(field_18544, Boolean.valueOf(true)), 4);
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	private void update(World world, BlockPos pos, BlockState state) {
		for (Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
			for (int i = 1; i < 42; i++) {
				BlockPos blockPos = pos.offset(direction, i);
				BlockState blockState = world.getBlockState(blockPos);
				if (blockState.getBlock() == this.field_18552) {
					if (blockState.getProperty(TripwireHookBlock.FACING) == direction.getOpposite()) {
						this.field_18552.update(world, blockPos, blockState, false, true, i, state);
					}
					break;
				}

				if (blockState.getBlock() != this) {
					break;
				}
			}
		}
	}

	@Override
	public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!world.isClient) {
			if (!(Boolean)state.getProperty(field_18542)) {
				this.updatePowered(world, pos);
			}
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!world.isClient) {
			if ((Boolean)world.getBlockState(pos).getProperty(field_18542)) {
				this.updatePowered(world, pos);
			}
		}
	}

	private void updatePowered(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		boolean bl = (Boolean)blockState.getProperty(field_18542);
		boolean bl2 = false;
		List<? extends Entity> list = world.getEntities(null, blockState.getOutlineShape(world, pos).getBoundingBox().offset(pos));
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (!entity.canAvoidTraps()) {
					bl2 = true;
					break;
				}
			}
		}

		if (bl2 != bl) {
			blockState = blockState.withProperty(field_18542, Boolean.valueOf(bl2));
			world.setBlockState(pos, blockState, 3);
			this.update(world, pos, blockState);
		}

		if (bl2) {
			world.getBlockTickScheduler().schedule(new BlockPos(pos), this, this.getTickDelay(world));
		}
	}

	public boolean method_16752(BlockState blockState, Direction direction) {
		Block block = blockState.getBlock();
		return block == this.field_18552 ? blockState.getProperty(TripwireHookBlock.FACING) == direction.getOpposite() : block == this;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.withProperty(field_18545, state.getProperty(field_18547))
					.withProperty(field_18546, state.getProperty(field_18548))
					.withProperty(field_18547, state.getProperty(field_18545))
					.withProperty(field_18548, state.getProperty(field_18546));
			case COUNTERCLOCKWISE_90:
				return state.withProperty(field_18545, state.getProperty(field_18546))
					.withProperty(field_18546, state.getProperty(field_18547))
					.withProperty(field_18547, state.getProperty(field_18548))
					.withProperty(field_18548, state.getProperty(field_18545));
			case CLOCKWISE_90:
				return state.withProperty(field_18545, state.getProperty(field_18548))
					.withProperty(field_18546, state.getProperty(field_18545))
					.withProperty(field_18547, state.getProperty(field_18546))
					.withProperty(field_18548, state.getProperty(field_18547));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.withProperty(field_18545, state.getProperty(field_18547)).withProperty(field_18547, state.getProperty(field_18545));
			case FRONT_BACK:
				return state.withProperty(field_18546, state.getProperty(field_18548)).withProperty(field_18548, state.getProperty(field_18546));
			default:
				return super.withMirror(state, mirror);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18542, field_18543, field_18544, field_18545, field_18546, field_18548, field_18547);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
