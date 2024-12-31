package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class CakeBlock extends Block {
	public static final IntProperty BITES = Properties.BITES;
	protected static final VoxelShape[] BITE_TO_SHAPE = new VoxelShape[]{
		Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(3.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(5.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(7.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(9.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(11.0, 0.0, 1.0, 15.0, 8.0, 15.0),
		Block.createCuboidShape(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)
	};

	protected CakeBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(BITES, Integer.valueOf(0)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return BITE_TO_SHAPE[state.getProperty(BITES)];
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean onUse(
		BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction, float distanceX, float distanceY, float distanceZ
	) {
		if (!world.isClient) {
			return this.tryEat(world, pos, state, player);
		} else {
			ItemStack itemStack = player.getStackInHand(hand);
			return this.tryEat(world, pos, state, player) || itemStack.isEmpty();
		}
	}

	private boolean tryEat(IWorld world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!player.canConsume(false)) {
			return false;
		} else {
			player.method_15928(Stats.EAT_CAKE_SLICE);
			player.getHungerManager().add(2, 0.1F);
			int i = (Integer)state.getProperty(BITES);
			if (i < 6) {
				world.setBlockState(pos, state.withProperty(BITES, Integer.valueOf(i + 1)), 3);
			} else {
				world.method_8553(pos);
			}

			return true;
		}
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !state.canPlaceAt(world, pos)
			? Blocks.AIR.getDefaultState()
			: super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		return world.getBlockState(pos.down()).getMaterial().isSolid();
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(BITES);
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return (7 - (Integer)state.getProperty(BITES)) * 2;
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
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
