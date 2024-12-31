package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.Attachment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class BellBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	private static final EnumProperty<Attachment> ATTACHMENT = Properties.ATTACHMENT;
	public static final BooleanProperty field_20648 = Properties.POWERED;
	private static final VoxelShape NORTH_SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
	private static final VoxelShape EAST_WEST_SHAPE = Block.createCuboidShape(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
	private static final VoxelShape BELL_WAIST_SHAPE = Block.createCuboidShape(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
	private static final VoxelShape BELL_LIP_SHAPE = Block.createCuboidShape(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
	private static final VoxelShape BELL_SHAPE = VoxelShapes.union(BELL_LIP_SHAPE, BELL_WAIST_SHAPE);
	private static final VoxelShape NORTH_SOUTH_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
	private static final VoxelShape EAST_WEST_WALLS_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape WEST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
	private static final VoxelShape EAST_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
	private static final VoxelShape NORTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
	private static final VoxelShape SOUTH_WALL_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
	private static final VoxelShape HANGING_SHAPE = VoxelShapes.union(BELL_SHAPE, Block.createCuboidShape(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));

	public BellBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.field_11043).with(ATTACHMENT, Attachment.field_17098).with(field_20648, Boolean.valueOf(false))
		);
	}

	@Override
	public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
		boolean bl2 = world.isReceivingRedstonePower(blockPos);
		if (bl2 != (Boolean)blockState.get(field_20648)) {
			if (bl2) {
				this.ring(world, blockPos, null);
			}

			world.setBlockState(blockPos, blockState.with(field_20648, Boolean.valueOf(bl2)), 3);
		}
	}

	@Override
	public void onProjectileHit(World world, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
		if (entity instanceof ProjectileEntity) {
			Entity entity2 = ((ProjectileEntity)entity).getOwner();
			PlayerEntity playerEntity = entity2 instanceof PlayerEntity ? (PlayerEntity)entity2 : null;
			this.ring(world, blockState, blockHitResult, playerEntity, true);
		}
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		return this.ring(world, blockState, blockHitResult, playerEntity, true) ? ActionResult.field_5812 : ActionResult.field_5811;
	}

	public boolean ring(World world, BlockState blockState, BlockHitResult blockHitResult, @Nullable PlayerEntity playerEntity, boolean bl) {
		Direction direction = blockHitResult.getSide();
		BlockPos blockPos = blockHitResult.getBlockPos();
		boolean bl2 = !bl || this.isPointOnBell(blockState, direction, blockHitResult.getPos().y - (double)blockPos.getY());
		if (bl2) {
			boolean bl3 = this.ring(world, blockPos, direction);
			if (bl3 && playerEntity != null) {
				playerEntity.incrementStat(Stats.field_19255);
			}

			return true;
		} else {
			return false;
		}
	}

	private boolean isPointOnBell(BlockState blockState, Direction direction, double d) {
		if (direction.getAxis() != Direction.Axis.field_11052 && !(d > 0.8124F)) {
			Direction direction2 = blockState.get(FACING);
			Attachment attachment = blockState.get(ATTACHMENT);
			switch (attachment) {
				case field_17098:
					return direction2.getAxis() == direction.getAxis();
				case field_17100:
				case field_17101:
					return direction2.getAxis() != direction.getAxis();
				case field_17099:
					return true;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	public boolean ring(World world, BlockPos blockPos, @Nullable Direction direction) {
		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (!world.isClient && blockEntity instanceof BellBlockEntity) {
			if (direction == null) {
				direction = world.getBlockState(blockPos).get(FACING);
			}

			((BellBlockEntity)blockEntity).activate(direction);
			world.playSound(null, blockPos, SoundEvents.field_17265, SoundCategory.field_15245, 2.0F, 1.0F);
			return true;
		} else {
			return false;
		}
	}

	private VoxelShape getShape(BlockState blockState) {
		Direction direction = blockState.get(FACING);
		Attachment attachment = blockState.get(ATTACHMENT);
		if (attachment == Attachment.field_17098) {
			return direction != Direction.field_11043 && direction != Direction.field_11035 ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
		} else if (attachment == Attachment.field_17099) {
			return HANGING_SHAPE;
		} else if (attachment == Attachment.field_17101) {
			return direction != Direction.field_11043 && direction != Direction.field_11035 ? EAST_WEST_WALLS_SHAPE : NORTH_SOUTH_WALLS_SHAPE;
		} else if (direction == Direction.field_11043) {
			return NORTH_WALL_SHAPE;
		} else if (direction == Direction.field_11035) {
			return SOUTH_WALL_SHAPE;
		} else {
			return direction == Direction.field_11034 ? EAST_WALL_SHAPE : WEST_WALL_SHAPE;
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.getShape(blockState);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return this.getShape(blockState);
	}

	@Override
	public BlockRenderType getRenderType(BlockState blockState) {
		return BlockRenderType.field_11458;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getSide();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		World world = itemPlacementContext.getWorld();
		Direction.Axis axis = direction.getAxis();
		if (axis == Direction.Axis.field_11052) {
			BlockState blockState = this.getDefaultState()
				.with(ATTACHMENT, direction == Direction.field_11033 ? Attachment.field_17099 : Attachment.field_17098)
				.with(FACING, itemPlacementContext.getPlayerFacing());
			if (blockState.canPlaceAt(itemPlacementContext.getWorld(), blockPos)) {
				return blockState;
			}
		} else {
			boolean bl = axis == Direction.Axis.field_11048
					&& world.getBlockState(blockPos.west()).isSideSolidFullSquare(world, blockPos.west(), Direction.field_11034)
					&& world.getBlockState(blockPos.east()).isSideSolidFullSquare(world, blockPos.east(), Direction.field_11039)
				|| axis == Direction.Axis.field_11051
					&& world.getBlockState(blockPos.north()).isSideSolidFullSquare(world, blockPos.north(), Direction.field_11035)
					&& world.getBlockState(blockPos.south()).isSideSolidFullSquare(world, blockPos.south(), Direction.field_11043);
			BlockState blockState2 = this.getDefaultState().with(FACING, direction.getOpposite()).with(ATTACHMENT, bl ? Attachment.field_17101 : Attachment.field_17100);
			if (blockState2.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
				return blockState2;
			}

			boolean bl2 = world.getBlockState(blockPos.down()).isSideSolidFullSquare(world, blockPos.down(), Direction.field_11036);
			blockState2 = blockState2.with(ATTACHMENT, bl2 ? Attachment.field_17098 : Attachment.field_17099);
			if (blockState2.canPlaceAt(itemPlacementContext.getWorld(), itemPlacementContext.getBlockPos())) {
				return blockState2;
			}
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2
	) {
		Attachment attachment = blockState.get(ATTACHMENT);
		Direction direction2 = getPlacementSide(blockState).getOpposite();
		if (direction2 == direction && !blockState.canPlaceAt(iWorld, blockPos) && attachment != Attachment.field_17101) {
			return Blocks.field_10124.getDefaultState();
		} else {
			if (direction.getAxis() == ((Direction)blockState.get(FACING)).getAxis()) {
				if (attachment == Attachment.field_17101 && !blockState2.isSideSolidFullSquare(iWorld, blockPos2, direction)) {
					return blockState.with(ATTACHMENT, Attachment.field_17100).with(FACING, direction.getOpposite());
				}

				if (attachment == Attachment.field_17100
					&& direction2.getOpposite() == direction
					&& blockState2.isSideSolidFullSquare(iWorld, blockPos2, blockState.get(FACING))) {
					return blockState.with(ATTACHMENT, Attachment.field_17101);
				}
			}

			return super.getStateForNeighborUpdate(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, WorldView worldView, BlockPos blockPos) {
		return WallMountedBlock.canPlaceAt(worldView, blockPos, getPlacementSide(blockState).getOpposite());
	}

	private static Direction getPlacementSide(BlockState blockState) {
		switch ((Attachment)blockState.get(ATTACHMENT)) {
			case field_17098:
				return Direction.field_11036;
			case field_17099:
				return Direction.field_11033;
			default:
				return ((Direction)blockState.get(FACING)).getOpposite();
		}
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState blockState) {
		return PistonBehavior.field_15971;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, ATTACHMENT, field_20648);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new BellBlockEntity();
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
