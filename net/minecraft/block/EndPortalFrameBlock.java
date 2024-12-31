package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Itemable;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.states.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shapes.VoxelShape;
import net.minecraft.util.shapes.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty field_18306 = Properties.EYE;
	protected static final VoxelShape field_18307 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);
	protected static final VoxelShape field_18308 = Block.createCuboidShape(4.0, 13.0, 4.0, 12.0, 16.0, 12.0);
	protected static final VoxelShape field_18309 = VoxelShapes.union(field_18307, field_18308);
	private static BlockPattern field_12657;

	public EndPortalFrameBlock(Block.Builder builder) {
		super(builder);
		this.setDefaultState(this.stateManager.method_16923().withProperty(FACING, Direction.NORTH).withProperty(field_18306, Boolean.valueOf(false)));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos) {
		return state.getProperty(field_18306) ? field_18309 : field_18307;
	}

	@Override
	public Itemable getDroppedItem(BlockState state, World world, BlockPos pos, int fortuneLevel) {
		return Items.AIR;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext context) {
		return this.getDefaultState().withProperty(FACING, context.method_16145().getOpposite()).withProperty(field_18306, Boolean.valueOf(false));
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return state.getProperty(field_18306) ? 15 : 0;
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
		builder.method_16928(FACING, field_18306);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	public static BlockPattern method_11610() {
		if (field_12657 == null) {
			field_12657 = BlockPatternBuilder.start()
				.aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.method_16940('?', CachedBlockPosition.method_16935(BlockStatePredicate.TRUE))
				.method_16940(
					'^',
					CachedBlockPosition.method_16935(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).addTest(field_18306, Predicates.equalTo(true)).addTest(FACING, Predicates.equalTo(Direction.SOUTH))
					)
				)
				.method_16940(
					'>',
					CachedBlockPosition.method_16935(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).addTest(field_18306, Predicates.equalTo(true)).addTest(FACING, Predicates.equalTo(Direction.WEST))
					)
				)
				.method_16940(
					'v',
					CachedBlockPosition.method_16935(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).addTest(field_18306, Predicates.equalTo(true)).addTest(FACING, Predicates.equalTo(Direction.NORTH))
					)
				)
				.method_16940(
					'<',
					CachedBlockPosition.method_16935(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).addTest(field_18306, Predicates.equalTo(true)).addTest(FACING, Predicates.equalTo(Direction.EAST))
					)
				)
				.build();
		}

		return field_12657;
	}

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? BlockRenderLayer.SOLID : BlockRenderLayer.UNDEFINED;
	}

	@Override
	public boolean canPlaceAtSide(BlockState state, BlockView world, BlockPos pos, BlockPlacementEnvironment environment) {
		return false;
	}
}
