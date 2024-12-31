package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = PistonHeadBlock.FACING;
	public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.field_18667;

	public PistonExtensionBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(TYPE, PistonType.DEFAULT));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView world) {
		return null;
	}

	public static BlockEntity createPistonEntity(BlockState pushedBlock, Direction dir, boolean extending, boolean source) {
		return new PistonBlockEntity(pushedBlock, dir, extending, source);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			} else {
				super.onStateReplaced(state, world, pos, newState, moved);
			}
		}
	}

	@Override
	public void method_8674(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.offset(((Direction)blockState.getProperty(FACING)).getOpposite());
		BlockState blockState2 = iWorld.getBlockState(blockPos2);
		if (blockState2.getBlock() instanceof PistonBlock && (Boolean)blockState2.getProperty(PistonBlock.field_18654)) {
			iWorld.method_8553(blockPos2);
		}
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (!world.isClient && world.getBlockEntity(pos) == null) {
			world.method_8553(pos);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public void method_410(BlockState blockState, World world, BlockPos blockPos, float f, int i) {
		if (!world.isClient) {
			PistonBlockEntity pistonBlockEntity = this.getPistonEntity(world, blockPos);
			if (pistonBlockEntity != null) {
				pistonBlockEntity.getPushedBlock().method_16867(world, blockPos, 0);
			}
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		PistonBlockEntity pistonBlockEntity = this.getPistonEntity(world, pos);
		return pistonBlockEntity != null ? pistonBlockEntity.method_16853(world, pos) : VoxelShapes.empty();
	}

	@Nullable
	private PistonBlockEntity getPistonEntity(BlockView view, BlockPos pos) {
		BlockEntity blockEntity = view.getBlockEntity(pos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Override
	public ItemStack getPickBlock(BlockView world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.withProperty(FACING, rotation.rotate(state.getProperty(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.getProperty(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(FACING, TYPE);
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
