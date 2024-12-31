package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class StairsBlock extends Block {
	public static final DirectionProperty FACING = HorizontalFacingBlock.DIRECTION;
	public static final EnumProperty<StairsBlock.Half> HALF = EnumProperty.of("half", StairsBlock.Half.class);
	public static final EnumProperty<StairsBlock.Shape> SHAPE = EnumProperty.of("shape", StairsBlock.Shape.class);
	protected static final Box field_12791 = new Box(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12792 = new Box(0.0, 0.5, 0.0, 0.5, 1.0, 1.0);
	protected static final Box field_12793 = new Box(0.5, 0.5, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12794 = new Box(0.0, 0.5, 0.0, 1.0, 1.0, 0.5);
	protected static final Box field_12777 = new Box(0.0, 0.5, 0.5, 1.0, 1.0, 1.0);
	protected static final Box field_12778 = new Box(0.0, 0.5, 0.0, 0.5, 1.0, 0.5);
	protected static final Box field_12779 = new Box(0.5, 0.5, 0.0, 1.0, 1.0, 0.5);
	protected static final Box field_12780 = new Box(0.0, 0.5, 0.5, 0.5, 1.0, 1.0);
	protected static final Box field_12781 = new Box(0.5, 0.5, 0.5, 1.0, 1.0, 1.0);
	protected static final Box field_12782 = new Box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
	protected static final Box field_12783 = new Box(0.0, 0.0, 0.0, 0.5, 0.5, 1.0);
	protected static final Box field_12784 = new Box(0.5, 0.0, 0.0, 1.0, 0.5, 1.0);
	protected static final Box field_12785 = new Box(0.0, 0.0, 0.0, 1.0, 0.5, 0.5);
	protected static final Box field_12786 = new Box(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
	protected static final Box field_12787 = new Box(0.0, 0.0, 0.0, 0.5, 0.5, 0.5);
	protected static final Box field_12788 = new Box(0.5, 0.0, 0.0, 1.0, 0.5, 0.5);
	protected static final Box field_12789 = new Box(0.0, 0.0, 0.5, 0.5, 0.5, 1.0);
	protected static final Box field_12790 = new Box(0.5, 0.0, 0.5, 1.0, 0.5, 1.0);
	private final Block block;
	private final BlockState state;

	protected StairsBlock(BlockState blockState) {
		super(blockState.getBlock().material);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(HALF, StairsBlock.Half.BOTTOM).with(SHAPE, StairsBlock.Shape.STRAIGHT)
		);
		this.block = blockState.getBlock();
		this.state = blockState;
		this.setStrength(this.block.hardness);
		this.setResistance(this.block.blastResistance / 3.0F);
		this.setBlockSoundGroup(this.block.blockSoundGroup);
		this.setOpacity(255);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
		if (!isActualState) {
			state = this.getBlockState(state, world, pos);
		}

		for (Box box : method_11634(state)) {
			appendCollisionBoxes(pos, entityBox, boxes, box);
		}
	}

	private static List<Box> method_11634(BlockState blockState) {
		List<Box> list = Lists.newArrayList();
		boolean bl = blockState.get(HALF) == StairsBlock.Half.TOP;
		list.add(bl ? field_12791 : field_12782);
		StairsBlock.Shape shape = blockState.get(SHAPE);
		if (shape == StairsBlock.Shape.STRAIGHT || shape == StairsBlock.Shape.INNER_LEFT || shape == StairsBlock.Shape.INNER_RIGHT) {
			list.add(method_11635(blockState));
		}

		if (shape != StairsBlock.Shape.STRAIGHT) {
			list.add(method_11636(blockState));
		}

		return list;
	}

	private static Box method_11635(BlockState blockState) {
		boolean bl = blockState.get(HALF) == StairsBlock.Half.TOP;
		switch ((Direction)blockState.get(FACING)) {
			case NORTH:
			default:
				return bl ? field_12785 : field_12794;
			case SOUTH:
				return bl ? field_12786 : field_12777;
			case WEST:
				return bl ? field_12783 : field_12792;
			case EAST:
				return bl ? field_12784 : field_12793;
		}
	}

	private static Box method_11636(BlockState blockState) {
		Direction direction = blockState.get(FACING);
		Direction direction2;
		switch ((StairsBlock.Shape)blockState.get(SHAPE)) {
			case OUTER_LEFT:
			default:
				direction2 = direction;
				break;
			case OUTER_RIGHT:
				direction2 = direction.rotateYClockwise();
				break;
			case INNER_RIGHT:
				direction2 = direction.getOpposite();
				break;
			case INNER_LEFT:
				direction2 = direction.rotateYCounterclockwise();
		}

		boolean bl = blockState.get(HALF) == StairsBlock.Half.TOP;
		switch (direction2) {
			case NORTH:
			default:
				return bl ? field_12787 : field_12778;
			case SOUTH:
				return bl ? field_12790 : field_12781;
			case WEST:
				return bl ? field_12789 : field_12780;
			case EAST:
				return bl ? field_12788 : field_12779;
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
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		this.block.randomDisplayTick(state, world, pos, random);
	}

	@Override
	public void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player) {
		this.block.onBlockBreakStart(world, pos, player);
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state) {
		this.block.onBreakByPlayer(world, pos, state);
	}

	@Override
	public int method_11564(BlockState state, BlockView view, BlockPos pos) {
		return this.state.method_11712(view, pos);
	}

	@Override
	public float getBlastResistance(Entity entity) {
		return this.block.getBlastResistance(entity);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return this.block.getRenderLayerType();
	}

	@Override
	public int getTickRate(World world) {
		return this.block.getTickRate(world);
	}

	@Override
	public Box method_11563(BlockState blockState, World world, BlockPos blockPos) {
		return this.state.method_11722(world, blockPos);
	}

	@Override
	public Vec3d onEntityCollision(World world, BlockPos pos, Entity entity, Vec3d velocity) {
		return this.block.onEntityCollision(world, pos, entity, velocity);
	}

	@Override
	public boolean hasCollision() {
		return this.block.hasCollision();
	}

	@Override
	public boolean canCollide(BlockState state, boolean bl) {
		return this.block.canCollide(state, bl);
	}

	@Override
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return this.block.canBePlacedAtPos(world, pos);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		this.state.neighbourUpdate(world, pos, Blocks.AIR, pos);
		this.block.onCreation(world, pos, this.state);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		this.block.onBreaking(world, pos, this.state);
	}

	@Override
	public void onSteppedOn(World world, BlockPos pos, Entity entity) {
		this.block.onSteppedOn(world, pos, entity);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		this.block.onScheduledTick(world, pos, state, rand);
	}

	@Override
	public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction direction, float f, float g, float h) {
		return this.block.use(world, pos, this.state, player, hand, Direction.DOWN, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
		this.block.onDestroyedByExplosion(world, pos, explosion);
	}

	@Override
	public boolean method_11568(BlockState state) {
		return state.get(HALF) == StairsBlock.Half.TOP;
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return this.block.getMaterialColor(this.state);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		BlockState blockState = super.getStateFromData(world, pos, dir, x, y, z, id, entity);
		blockState = blockState.with(FACING, entity.getHorizontalDirection()).with(SHAPE, StairsBlock.Shape.STRAIGHT);
		return dir != Direction.DOWN && (dir == Direction.UP || !((double)y > 0.5))
			? blockState.with(HALF, StairsBlock.Half.BOTTOM)
			: blockState.with(HALF, StairsBlock.Half.TOP);
	}

	@Nullable
	@Override
	public BlockHitResult method_414(BlockState blockState, World world, BlockPos blockPos, Vec3d vec3d, Vec3d vec3d2) {
		List<BlockHitResult> list = Lists.newArrayList();

		for (Box box : method_11634(this.getBlockState(blockState, world, blockPos))) {
			list.add(this.method_11559(blockPos, vec3d, vec3d2, box));
		}

		BlockHitResult blockHitResult = null;
		double d = 0.0;

		for (BlockHitResult blockHitResult2 : list) {
			if (blockHitResult2 != null) {
				double e = blockHitResult2.pos.squaredDistanceTo(vec3d2);
				if (e > d) {
					blockHitResult = blockHitResult2;
					d = e;
				}
			}
		}

		return blockHitResult;
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(HALF, (data & 4) > 0 ? StairsBlock.Half.TOP : StairsBlock.Half.BOTTOM);
		return blockState.with(FACING, Direction.getById(5 - (data & 3)));
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		if (state.get(HALF) == StairsBlock.Half.TOP) {
			i |= 4;
		}

		return i | 5 - ((Direction)state.get(FACING)).getId();
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(SHAPE, method_11631(state, view, pos));
	}

	private static StairsBlock.Shape method_11631(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		Direction direction = blockState.get(FACING);
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		if (method_11633(blockState2) && blockState.get(HALF) == blockState2.get(HALF)) {
			Direction direction2 = blockState2.get(FACING);
			if (direction2.getAxis() != ((Direction)blockState.get(FACING)).getAxis() && method_11632(blockState, blockView, blockPos, direction2.getOpposite())) {
				if (direction2 == direction.rotateYCounterclockwise()) {
					return StairsBlock.Shape.OUTER_LEFT;
				}

				return StairsBlock.Shape.OUTER_RIGHT;
			}
		}

		BlockState blockState3 = blockView.getBlockState(blockPos.offset(direction.getOpposite()));
		if (method_11633(blockState3) && blockState.get(HALF) == blockState3.get(HALF)) {
			Direction direction3 = blockState3.get(FACING);
			if (direction3.getAxis() != ((Direction)blockState.get(FACING)).getAxis() && method_11632(blockState, blockView, blockPos, direction3)) {
				if (direction3 == direction.rotateYCounterclockwise()) {
					return StairsBlock.Shape.INNER_LEFT;
				}

				return StairsBlock.Shape.INNER_RIGHT;
			}
		}

		return StairsBlock.Shape.STRAIGHT;
	}

	private static boolean method_11632(BlockState blockState, BlockView blockView, BlockPos blockPos, Direction direction) {
		BlockState blockState2 = blockView.getBlockState(blockPos.offset(direction));
		return !method_11633(blockState2) || blockState2.get(FACING) != blockState.get(FACING) || blockState2.get(HALF) != blockState.get(HALF);
	}

	public static boolean method_11633(BlockState blockState) {
		return blockState.getBlock() instanceof StairsBlock;
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		Direction direction = state.get(FACING);
		StairsBlock.Shape shape = state.get(SHAPE);
		switch (mirror) {
			case LEFT_RIGHT:
				if (direction.getAxis() == Direction.Axis.Z) {
					switch (shape) {
						case OUTER_LEFT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.OUTER_LEFT);
						case INNER_RIGHT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.INNER_LEFT);
						case INNER_LEFT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.INNER_RIGHT);
						default:
							return state.withRotation(BlockRotation.CLOCKWISE_180);
					}
				}
				break;
			case FRONT_BACK:
				if (direction.getAxis() == Direction.Axis.X) {
					switch (shape) {
						case OUTER_LEFT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.OUTER_RIGHT);
						case OUTER_RIGHT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.OUTER_LEFT);
						case INNER_RIGHT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.INNER_RIGHT);
						case INNER_LEFT:
							return state.withRotation(BlockRotation.CLOCKWISE_180).with(SHAPE, StairsBlock.Shape.INNER_LEFT);
						case STRAIGHT:
							return state.withRotation(BlockRotation.CLOCKWISE_180);
					}
				}
		}

		return super.withMirror(state, mirror);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, FACING, HALF, SHAPE);
	}

	public static enum Half implements StringIdentifiable {
		TOP("top"),
		BOTTOM("bottom");

		private final String name;

		private Half(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}

	public static enum Shape implements StringIdentifiable {
		STRAIGHT("straight"),
		INNER_LEFT("inner_left"),
		INNER_RIGHT("inner_right"),
		OUTER_LEFT("outer_left"),
		OUTER_RIGHT("outer_right");

		private final String name;

		private Shape(String string2) {
			this.name = string2;
		}

		public String toString() {
			return this.name;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
