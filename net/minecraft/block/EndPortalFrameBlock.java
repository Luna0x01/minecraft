package net.minecraft.block;

import com.google.common.base.Predicates;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndPortalFrameBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final BooleanProperty EYE = BooleanProperty.of("eye");
	protected static final Box PORTAL_FRAME = new Box(0.0, 0.0, 0.0, 1.0, 0.8125, 1.0);
	protected static final Box PORTAL_EYE = new Box(0.3125, 0.8125, 0.3125, 0.6875, 1.0, 0.6875);
	private static BlockPattern field_12657;

	public EndPortalFrameBlock() {
		super(Material.STONE, MaterialColor.GREEN);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(EYE, false));
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		return PORTAL_FRAME;
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
		appendCollisionBoxes(pos, entityBox, boxes, PORTAL_FRAME);
		if ((Boolean)world.getBlockState(pos).get(EYE)) {
			appendCollisionBoxes(pos, entityBox, boxes, PORTAL_EYE);
		}
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Items.AIR;
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		return this.getDefaultState().with(FACING, entity.getHorizontalDirection().getOpposite()).with(EYE, false);
	}

	@Override
	public boolean method_11577(BlockState state) {
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return state.get(EYE) ? 15 : 0;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(EYE, (data & 4) != 0).with(FACING, Direction.fromHorizontal(data & 3));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getHorizontal();
		if ((Boolean)state.get(EYE)) {
			i |= 4;
		}

		return i;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(FACING)));
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, EYE);
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	public static BlockPattern method_11610() {
		if (field_12657 == null) {
			field_12657 = BlockPatternBuilder.start()
				.aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?")
				.where('?', CachedBlockPosition.matchesBlockState(BlockStatePredicate.field_12902))
				.where(
					'^',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).setProperty(EYE, Predicates.equalTo(true)).setProperty(FACING, Predicates.equalTo(Direction.SOUTH))
					)
				)
				.where(
					'>',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).setProperty(EYE, Predicates.equalTo(true)).setProperty(FACING, Predicates.equalTo(Direction.WEST))
					)
				)
				.where(
					'v',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).setProperty(EYE, Predicates.equalTo(true)).setProperty(FACING, Predicates.equalTo(Direction.NORTH))
					)
				)
				.where(
					'<',
					CachedBlockPosition.matchesBlockState(
						BlockStatePredicate.create(Blocks.END_PORTAL_FRAME).setProperty(EYE, Predicates.equalTo(true)).setProperty(FACING, Predicates.equalTo(Direction.EAST))
					)
				)
				.build();
		}

		return field_12657;
	}
}
