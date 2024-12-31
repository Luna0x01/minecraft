package net.minecraft.block;

import java.util.Random;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.RenderBlockView;
import net.minecraft.world.World;

public class ChorusPlantBlock extends ConnectingBlock {
	protected ChorusPlantBlock(Block.Builder builder) {
		super(0.3125F, builder);
		this.setDefaultState(
			this.stateManager
				.method_16923()
				.withProperty(NORTH, Boolean.valueOf(false))
				.withProperty(EAST, Boolean.valueOf(false))
				.withProperty(SOUTH, Boolean.valueOf(false))
				.withProperty(WEST, Boolean.valueOf(false))
				.withProperty(UP, Boolean.valueOf(false))
				.withProperty(DOWN, Boolean.valueOf(false))
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.withConnectionProperties(context.getWorld(), context.getBlockPos());
	}

	public BlockState withConnectionProperties(BlockView world, BlockPos pos) {
		Block block = world.getBlockState(pos.down()).getBlock();
		Block block2 = world.getBlockState(pos.up()).getBlock();
		Block block3 = world.getBlockState(pos.north()).getBlock();
		Block block4 = world.getBlockState(pos.east()).getBlock();
		Block block5 = world.getBlockState(pos.south()).getBlock();
		Block block6 = world.getBlockState(pos.west()).getBlock();
		return this.getDefaultState()
			.withProperty(DOWN, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE))
			.withProperty(UP, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER))
			.withProperty(NORTH, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER))
			.withProperty(EAST, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER))
			.withProperty(SOUTH, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER))
			.withProperty(WEST, Boolean.valueOf(block6 == this || block6 == Blocks.CHORUS_FLOWER));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		if (!state.canPlaceAt(world, pos)) {
			world.getBlockTickScheduler().schedule(pos, this, 1);
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
		} else {
			Block block = neighborState.getBlock();
			boolean bl = block == this || block == Blocks.CHORUS_FLOWER || direction == Direction.DOWN && block == Blocks.END_STONE;
			return state.withProperty((Property)FACING_TO_PROPERTY.get(direction), Boolean.valueOf(bl));
		}
	}

	@Override
	public void scheduledTick(BlockState state, World world, BlockPos pos, Random random) {
		if (!state.canPlaceAt(world, pos)) {
			world.method_8535(pos, true);
		}
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.CHORUS_FRUIT;
	}

	@Override
	public int getDropCount(BlockState state, Random random) {
		return random.nextInt(2);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean canPlaceAt(BlockState state, RenderBlockView world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos.down());
		boolean bl = !world.getBlockState(pos.up()).isAir() && !blockState.isAir();

		for (Direction direction : Direction.DirectionType.HORIZONTAL) {
			BlockPos blockPos = pos.offset(direction);
			Block block = world.getBlockState(blockPos).getBlock();
			if (block == this) {
				if (bl) {
					return false;
				}

				Block block2 = world.getBlockState(blockPos.down()).getBlock();
				if (block2 == this || block2 == Blocks.END_STONE) {
					return true;
				}
			}
		}

		Block block3 = blockState.getBlock();
		return block3 == this || block3 == Blocks.END_STONE;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.method_16928(NORTH, EAST, SOUTH, WEST, UP, DOWN);
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
