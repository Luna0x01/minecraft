package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class DoublePlantBlock extends PlantBlock {
	public static final EnumProperty<DoubleBlockHalf> field_18302 = Properties.DOUBLE_BLOCK_HALF;

	public DoublePlantBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18302, DoubleBlockHalf.LOWER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf doubleBlockHalf = state.getProperty(field_18302);
		if (direction.getAxis() != Direction.Axis.Y
			|| doubleBlockHalf == DoubleBlockHalf.LOWER != (direction == Direction.UP)
			|| neighborState.getBlock() == this && neighborState.getProperty(field_18302) != doubleBlockHalf) {
			return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)
				? Blocks.AIR.getDefaultState()
				: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			return Blocks.AIR.getDefaultState();
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockPos blockPos = context.getBlockPos();
		return blockPos.getY() < 255 && context.getWorld().getBlockState(blockPos.up()).canReplace(context) ? super.getPlacementState(context) : null;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlockState(pos.up(), this.getDefaultState().withProperty(field_18302, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		if (state.getProperty(field_18302) != DoubleBlockHalf.UPPER) {
			return super.canPlaceAt(state, world, pos);
		} else {
			BlockState blockState = world.getBlockState(pos.down());
			return blockState.getBlock() == this && blockState.getProperty(field_18302) == DoubleBlockHalf.LOWER;
		}
	}

	public void method_16669(IWorld iWorld, BlockPos blockPos, int i) {
		iWorld.setBlockState(blockPos, this.getDefaultState().withProperty(field_18302, DoubleBlockHalf.LOWER), i);
		iWorld.setBlockState(blockPos.up(), this.getDefaultState().withProperty(field_18302, DoubleBlockHalf.UPPER), i);
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		super.method_8651(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, stack);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleBlockHalf = state.getProperty(field_18302);
		boolean bl = doubleBlockHalf == DoubleBlockHalf.LOWER;
		BlockPos blockPos = bl ? pos.up() : pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() == this && blockState.getProperty(field_18302) != doubleBlockHalf) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 35);
			world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
			if (!world.isClient && !player.isCreative()) {
				if (bl) {
					this.method_16670(state, world, pos, player.getMainHandStack());
				} else {
					this.method_16670(blockState, world, blockPos, player.getMainHandStack());
				}
			}
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	protected void method_16670(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
		blockState.method_16867(world, blockPos, 0);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return (Itemable)(state.getProperty(field_18302) == DoubleBlockHalf.LOWER ? super.getDroppedItem(state, world, pos, fortuneLevel) : Items.AIR);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18302);
	}

	@Override
	public Block.OffsetType getOffsetType() {
		return Block.OffsetType.XZ;
	}

	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		return MathHelper.hashCode(pos.getX(), pos.down(state.getProperty(field_18302) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
	}
}
