package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlacementEnvironment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class class_3724 extends Block {
	public static final IntProperty field_18492 = Properties.LAYERS;
	protected static final VoxelShape[] field_18493 = new VoxelShape[]{
		VoxelShapes.empty(),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
		Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
	};

	protected class_3724(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(field_18492, Integer.valueOf(1)));
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		switch (environment) {
			case LAND:
				return (Integer)state.getProperty(field_18492) < 5;
			case WATER:
				return false;
			case AIR:
				return false;
			default:
				return false;
		}
	}

	@Override
	public boolean method_11562(BlockState state) {
		return (Integer)state.getProperty(field_18492) == 8;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18493[state.getProperty(field_18492)];
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos) {
		return field_18493[state.getProperty(field_18492) - 1];
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		Block block = blockState.getBlock();
		if (block != Blocks.ICE && block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
			BlockRenderLayer blockRenderLayer = blockState.getRenderLayer(world, pos.down(), Direction.UP);
			return blockRenderLayer == BlockRenderLayer.SOLID || blockState.isIn(BlockTags.LEAVES) || block == this && (Integer)blockState.getProperty(field_18492) == 8;
		} else {
			return false;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void method_8651(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
		Integer integer = state.getProperty(field_18492);
		if (this.requiresSilkTouch() && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0) {
			if (integer == 8) {
				onBlockBreak(world, pos, new ItemStack(Blocks.SNOW_BLOCK));
			} else {
				for (int i = 0; i < integer; i++) {
					onBlockBreak(world, pos, this.createStackFromBlock(state));
				}
			}
		} else {
			onBlockBreak(world, pos, new ItemStack(Items.SNOWBALL, integer));
		}

		world.method_8553(pos);
		player.method_15932(Stats.MINED.method_21429(this));
		player.addExhaustion(0.005F);
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (world.method_16370(LightType.BLOCK, pos) > 11) {
			state.method_16867(world, pos, 0);
			world.method_8553(pos);
		}
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext itemPlacementContext) {
		int i = (Integer)state.getProperty(field_18492);
		if (itemPlacementContext.getItemStack().getItem() != this.getItem() || i >= 8) {
			return i == 1;
		} else {
			return itemPlacementContext.method_16019() ? itemPlacementContext.method_16151() == Direction.UP : true;
		}
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
		if (blockState.getBlock() == this) {
			int i = (Integer)blockState.getProperty(field_18492);
			return blockState.withProperty(field_18492, Integer.valueOf(Math.min(8, i + 1)));
		} else {
			return super.getPlacementState(context);
		}
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(field_18492);
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}
}
