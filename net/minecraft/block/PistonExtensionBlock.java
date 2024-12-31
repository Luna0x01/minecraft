package net.minecraft.block;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.enums.PistonType;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PistonExtensionBlock extends BlockWithEntity {
	public static final DirectionProperty FACING = PistonHeadBlock.FACING;
	public static final EnumProperty<PistonType> TYPE = PistonHeadBlock.TYPE;

	public PistonExtensionBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.field_11043).with(TYPE, PistonType.field_12637));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return null;
	}

	public static BlockEntity createBlockEntityPiston(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
		return new PistonBlockEntity(blockState, direction, bl, bl2);
	}

	@Override
	public void onBlockRemoved(BlockState blockState, World world, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (blockState.getBlock() != blockState2.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(blockPos);
			if (blockEntity instanceof PistonBlockEntity) {
				((PistonBlockEntity)blockEntity).finish();
			}
		}
	}

	@Override
	public void onBroken(IWorld iWorld, BlockPos blockPos, BlockState blockState) {
		BlockPos blockPos2 = blockPos.offset(((Direction)blockState.get(FACING)).getOpposite());
		BlockState blockState2 = iWorld.getBlockState(blockPos2);
		if (blockState2.getBlock() instanceof PistonBlock && (Boolean)blockState2.get(PistonBlock.EXTENDED)) {
			iWorld.removeBlock(blockPos2, false);
		}
	}

	@Override
	public boolean isSimpleFullBlock(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public boolean canSuffocate(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return false;
	}

	@Override
	public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockHitResult blockHitResult) {
		if (!world.isClient && world.getBlockEntity(blockPos) == null) {
			world.removeBlock(blockPos, false);
			return ActionResult.field_21466;
		} else {
			return ActionResult.field_5811;
		}
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState blockState, LootContext.Builder builder) {
		PistonBlockEntity pistonBlockEntity = this.getPistonBlockEntity(builder.getWorld(), builder.get(LootContextParameters.field_1232));
		return pistonBlockEntity == null ? Collections.emptyList() : pistonBlockEntity.getPushedBlock().getDroppedStacks(builder);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return VoxelShapes.empty();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		PistonBlockEntity pistonBlockEntity = this.getPistonBlockEntity(blockView, blockPos);
		return pistonBlockEntity != null ? pistonBlockEntity.getCollisionShape(blockView, blockPos) : VoxelShapes.empty();
	}

	@Nullable
	private PistonBlockEntity getPistonBlockEntity(BlockView blockView, BlockPos blockPos) {
		BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
		return blockEntity instanceof PistonBlockEntity ? (PistonBlockEntity)blockEntity : null;
	}

	@Override
	public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
		return ItemStack.EMPTY;
	}

	@Override
	public BlockState rotate(BlockState blockState, BlockRotation blockRotation) {
		return blockState.with(FACING, blockRotation.rotate(blockState.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState blockState, BlockMirror blockMirror) {
		return blockState.rotate(blockMirror.getRotation(blockState.get(FACING)));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, TYPE);
	}

	@Override
	public boolean canPlaceAtSide(BlockState blockState, BlockView blockView, BlockPos blockPos, BlockPlacementEnvironment blockPlacementEnvironment) {
		return false;
	}
}
