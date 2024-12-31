package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.material.Material;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class DoorBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18293 = Properties.OPEN;
	public static final EnumProperty<DoorHinge> field_18294 = Properties.DOOR_HINGE;
	public static final BooleanProperty field_18295 = Properties.POWERED;
	public static final EnumProperty<DoubleBlockHalf> field_18296 = Properties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape field_18297 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
	protected static final VoxelShape field_18298 = Block.createCuboidShape(0.0, 0.0, 13.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18299 = Block.createCuboidShape(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
	protected static final VoxelShape field_18300 = Block.createCuboidShape(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);

	protected DoorBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(FACING, Direction.NORTH)
				.withProperty(field_18293, Boolean.valueOf(false))
				.withProperty(field_18294, DoorHinge.LEFT)
				.withProperty(field_18295, Boolean.valueOf(false))
				.withProperty(field_18296, DoubleBlockHalf.LOWER)
		);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		Direction direction = state.getProperty(FACING);
		boolean bl = !(Boolean)state.getProperty(field_18293);
		boolean bl2 = state.getProperty(field_18294) == DoorHinge.RIGHT;
		switch (direction) {
			case EAST:
			default:
				return bl ? field_18300 : (bl2 ? field_18298 : field_18297);
			case SOUTH:
				return bl ? field_18297 : (bl2 ? field_18300 : field_18299);
			case WEST:
				return bl ? field_18299 : (bl2 ? field_18297 : field_18298);
			case NORTH:
				return bl ? field_18298 : (bl2 ? field_18299 : field_18300);
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf doubleBlockHalf = state.getProperty(field_18296);
		if (direction.getAxis() != Direction.Axis.Y || doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			return neighborState.getBlock() == this && neighborState.getProperty(field_18296) != doubleBlockHalf
				? state.withProperty(FACING, neighborState.getProperty(FACING))
					.withProperty(field_18293, neighborState.getProperty(field_18293))
					.withProperty(field_18294, neighborState.getProperty(field_18294))
					.withProperty(field_18295, neighborState.getProperty(field_18295))
				: Blocks.AIR.getDefaultState();
		}
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.method_8651(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleBlockHalf = state.getProperty(field_18296);
		boolean bl = doubleBlockHalf == DoubleBlockHalf.LOWER;
		BlockPos blockPos = bl ? pos.up() : pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && blockState.getProperty(field_18296) != doubleBlockHalf) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
			world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			if (!world.isClient && !player.isCreative()) {
				if (bl) {
					state.method_16867(world, pos, 0);
				} else {
					blockState.method_16867(world, blockPos, 0);
				}
			}
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		switch (environment) {
			case LAND:
				return (Boolean)state.getProperty(field_18293);
			case WATER:
				return false;
			case AIR:
				return (Boolean)state.getProperty(field_18293);
			default:
				return false;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	private int method_11604() {
		return this.material == Material.IRON ? 1011 : 1012;
	}

	private int method_11605() {
		return this.material == Material.IRON ? 1005 : 1006;
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos blockPos = context.getBlockPos();
		if (blockPos.getY() < 255 && context.getWorld().getBlockState(blockPos.up()).canReplace(context)) {
			World world = context.getWorld();
			boolean bl = world.isReceivingRedstonePower(blockPos) || world.isReceivingRedstonePower(blockPos.up());
			return this.getDefaultState()
				.withProperty(FACING, context.method_16145())
				.withProperty(field_18294, this.method_16667(context))
				.withProperty(field_18295, Boolean.valueOf(bl))
				.withProperty(field_18293, Boolean.valueOf(bl))
				.withProperty(field_18296, DoubleBlockHalf.LOWER);
		} else {
			return null;
		}
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), state.withProperty(field_18296, DoubleBlockHalf.UPPER), 3);
	}

	private DoorHinge method_16667(ItemPlacementContext itemPlacementContext) {
		BlockView blockView = itemPlacementContext.getWorld();
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		Direction direction = itemPlacementContext.method_16145();
		BlockPos blockPos2 = blockPos.up();
		Direction direction2 = direction.rotateYCounterclockwise();
		BlockState blockState = blockView.getBlockState(blockPos.offset(direction2));
		BlockState blockState2 = blockView.getBlockState(blockPos2.offset(direction2));
		Direction direction3 = direction.rotateYClockwise();
		BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction3));
		BlockState blockState4 = blockView.getBlockState(blockPos2.offset(direction3));
		int i = (blockState.method_16905() ? -1 : 0)
			+ (blockState2.method_16905() ? -1 : 0)
			+ (blockState3.method_16905() ? 1 : 0)
			+ (blockState4.method_16905() ? 1 : 0);
		boolean bl = blockState.getBlock() == this && blockState.getProperty(field_18296) == DoubleBlockHalf.LOWER;
		boolean bl2 = blockState3.getBlock() == this && blockState3.getProperty(field_18296) == DoubleBlockHalf.LOWER;
		if ((!bl || bl2) && i <= 0) {
			if ((!bl2 || bl) && i >= 0) {
				int j = direction.getOffsetX();
				int k = direction.getOffsetZ();
				float f = itemPlacementContext.method_16152();
				float g = itemPlacementContext.method_16154();
				return (j >= 0 || !(g < 0.5F)) && (j <= 0 || !(g > 0.5F)) && (k >= 0 || !(f > 0.5F)) && (k <= 0 || !(f < 0.5F)) ? DoorHinge.LEFT : DoorHinge.RIGHT;
			} else {
				return DoorHinge.LEFT;
			}
		} else {
			return DoorHinge.RIGHT;
		}
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (this.material == Material.IRON) {
			return false;
		} else {
			state = state.method_16930(field_18293);
			world.setBlockState(pos, state, 10);
			world.syncWorldEvent(player, state.getProperty(field_18293) ? this.method_11605() : this.method_11604(), pos, 0);
			return true;
		}
	}

	public void activateDoor(World world, BlockPos pos, boolean isOpen) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock() == this && (Boolean)blockState.getProperty(field_18293) != isOpen) {
			world.setBlockState(pos, blockState.withProperty(field_18293, Boolean.valueOf(isOpen)), 10);
			this.method_16668(world, pos, isOpen);
		}
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		boolean bl = world.isReceivingRedstonePower(pos)
			|| world.isReceivingRedstonePower(pos.offset(state.getProperty(field_18296) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (block != this && bl != (Boolean)state.getProperty(field_18295)) {
			if (bl != (Boolean)state.getProperty(field_18293)) {
				this.method_16668(world, pos, bl);
			}

			world.setBlockState(pos, state.withProperty(field_18295, Boolean.valueOf(bl)).withProperty(field_18293, Boolean.valueOf(bl)), 2);
		}
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		return state.getProperty(field_18296) == DoubleBlockHalf.LOWER ? blockState.method_16913() : blockState.getBlock() == this;
	}

	private void method_16668(World world, BlockPos blockPos, boolean bl) {
		world.syncWorldEvent(null, bl ? this.method_11605() : this.method_11604(), blockPos, 0);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return (Itemable)(state.getProperty(field_18296) == DoubleBlockHalf.UPPER ? Items.AIR : super.getDroppedItem(state, world, pos, fortuneLevel));
	}

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) {
		return PistonBehavior.DESTROY;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return mirror == BlockMirror.NONE ? state : state.rotate(mirror.getRotation(state.getProperty(FACING))).method_16930(field_18294);
	}

	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.down(state.getProperty(field_18296) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18296, FACING, field_18293, field_18294, field_18295);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}
}
