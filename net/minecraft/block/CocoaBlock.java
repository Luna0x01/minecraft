package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CocoaBlock extends HorizontalFacingBlock implements Growable {
	public static final IntProperty AGE = IntProperty.of("age", 0, 2);
	protected static final Box[] field_12632 = new Box[]{
		new Box(0.6875, 0.4375, 0.375, 0.9375, 0.75, 0.625),
		new Box(0.5625, 0.3125, 0.3125, 0.9375, 0.75, 0.6875),
		new Box(0.5625, 0.3125, 0.3125, 0.9375, 0.75, 0.6875)
	};
	protected static final Box[] field_12633 = new Box[]{
		new Box(0.0625, 0.4375, 0.375, 0.3125, 0.75, 0.625),
		new Box(0.0625, 0.3125, 0.3125, 0.4375, 0.75, 0.6875),
		new Box(0.0625, 0.3125, 0.3125, 0.4375, 0.75, 0.6875)
	};
	protected static final Box[] field_12634 = new Box[]{
		new Box(0.375, 0.4375, 0.0625, 0.625, 0.75, 0.3125),
		new Box(0.3125, 0.3125, 0.0625, 0.6875, 0.75, 0.4375),
		new Box(0.3125, 0.3125, 0.0625, 0.6875, 0.75, 0.4375)
	};
	protected static final Box[] field_12635 = new Box[]{
		new Box(0.375, 0.4375, 0.6875, 0.625, 0.75, 0.9375),
		new Box(0.3125, 0.3125, 0.5625, 0.6875, 0.75, 0.9375),
		new Box(0.3125, 0.3125, 0.5625, 0.6875, 0.75, 0.9375)
	};

	public CocoaBlock() {
		super(Material.PLANT);
		this.setDefaultState(this.stateManager.getDefaultState().with(DIRECTION, Direction.NORTH).with(AGE, 0));
		this.setTickRandomly(true);
	}

	@Override
	public void onScheduledTick(World world, BlockPos pos, BlockState state, Random rand) {
		if (!this.isOnJungleWood(world, pos, state)) {
			this.replaceAndDrop(world, pos, state);
		} else if (world.random.nextInt(5) == 0) {
			int i = (Integer)state.get(AGE);
			if (i < 2) {
				world.setBlockState(pos, state.with(AGE, i + 1), 2);
			}
		}
	}

	public boolean isOnJungleWood(World world, BlockPos pos, BlockState state) {
		pos = pos.offset(state.get(DIRECTION));
		BlockState blockState = world.getBlockState(pos);
		return blockState.getBlock() == Blocks.LOG && blockState.get(Log1Block.VARIANT) == PlanksBlock.WoodType.JUNGLE;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		int i = (Integer)state.get(AGE);
		switch ((Direction)state.get(DIRECTION)) {
			case SOUTH:
				return field_12635[i];
			case NORTH:
			default:
				return field_12634[i];
			case WEST:
				return field_12633[i];
			case EAST:
				return field_12632[i];
		}
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		return state.with(DIRECTION, rotation.rotate(state.get(DIRECTION)));
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		return state.withRotation(mirror.getRotation(state.get(DIRECTION)));
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		Direction direction = Direction.fromRotation((double)placer.yaw);
		world.setBlockState(pos, state.with(DIRECTION, direction), 2);
	}

	@Override
	public BlockState getStateFromData(World world, BlockPos pos, Direction dir, float x, float y, float z, int id, LivingEntity entity) {
		if (!dir.getAxis().isHorizontal()) {
			dir = Direction.NORTH;
		}

		return this.getDefaultState().with(DIRECTION, dir.getOpposite()).with(AGE, 0);
	}

	@Override
	public void method_8641(BlockState blockState, World world, BlockPos blockPos, Block block) {
		if (!this.isOnJungleWood(world, blockPos, blockState)) {
			this.replaceAndDrop(world, blockPos, blockState);
		}
	}

	private void replaceAndDrop(World world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		this.dropAsItem(world, pos, state, 0);
	}

	@Override
	public void randomDropAsItem(World world, BlockPos pos, BlockState state, float chance, int id) {
		int i = (Integer)state.get(AGE);
		int j = 1;
		if (i >= 2) {
			j = 3;
		}

		for (int k = 0; k < j; k++) {
			onBlockBreak(world, pos, new ItemStack(Items.DYE, 1, DyeColor.BROWN.getSwappedId()));
		}
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Items.DYE, 1, DyeColor.BROWN.getSwappedId());
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, BlockState state, boolean bl) {
		return (Integer)state.get(AGE) < 2;
	}

	@Override
	public boolean canBeFertilized(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(World world, Random random, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state.with(AGE, (Integer)state.get(AGE) + 1), 2);
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(DIRECTION, Direction.fromHorizontal(data)).with(AGE, (data & 15) >> 2);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(DIRECTION)).getHorizontal();
		return i | (Integer)state.get(AGE) << 2;
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, DIRECTION, AGE);
	}
}
