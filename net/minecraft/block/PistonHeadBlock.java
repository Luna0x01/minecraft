package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PistonHeadBlock extends FacingBlock {
	public static final EnumProperty<PistonHeadBlock.PistonHeadType> TYPE = EnumProperty.of("type", PistonHeadBlock.PistonHeadType.class);
	public static final BooleanProperty SHORT = BooleanProperty.of("short");
	protected static final Box field_12894 = new Box(0.75, 0.0, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12895 = new Box(0.0, 0.0, 0.0, 0.25, 1.0, 1.0);
	protected static final Box field_12896 = new Box(0.0, 0.0, 0.75, 1.0, 1.0, 1.0);
	protected static final Box field_12897 = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.25);
	protected static final Box field_12898 = new Box(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
	protected static final Box field_12887 = new Box(0.0, 0.0, 0.0, 1.0, 0.25, 1.0);
	protected static final Box field_12888 = new Box(0.375, -0.25, 0.375, 0.625, 0.75, 0.625);
	protected static final Box field_12889 = new Box(0.375, 0.25, 0.375, 0.625, 1.25, 0.625);
	protected static final Box field_12890 = new Box(0.375, 0.375, -0.25, 0.625, 0.625, 0.75);
	protected static final Box field_12891 = new Box(0.375, 0.375, 0.25, 0.625, 0.625, 1.25);
	protected static final Box field_12892 = new Box(-0.25, 0.375, 0.375, 0.75, 0.625, 0.625);
	protected static final Box field_12893 = new Box(0.25, 0.375, 0.375, 1.25, 0.625, 0.625);
	protected static final Box field_15174 = new Box(0.375, 0.0, 0.375, 0.625, 0.75, 0.625);
	protected static final Box field_15175 = new Box(0.375, 0.25, 0.375, 0.625, 1.0, 0.625);
	protected static final Box field_15176 = new Box(0.375, 0.375, 0.0, 0.625, 0.625, 0.75);
	protected static final Box field_15177 = new Box(0.375, 0.375, 0.25, 0.625, 0.625, 1.0);
	protected static final Box field_15178 = new Box(0.0, 0.375, 0.375, 0.75, 0.625, 0.625);
	protected static final Box field_15179 = new Box(0.25, 0.375, 0.375, 1.0, 0.625, 0.625);

	public PistonHeadBlock() {
		super(Material.PISTON);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(TYPE, PistonHeadBlock.PistonHeadType.DEFAULT).with(SHORT, false));
		this.setBlockSoundGroup(BlockSoundGroup.STONE);
		this.setStrength(0.5F);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		switch ((Direction)state.get(FACING)) {
			case DOWN:
			default:
				return field_12887;
			case UP:
				return field_12898;
			case NORTH:
				return field_12897;
			case SOUTH:
				return field_12896;
			case WEST:
				return field_12895;
			case EAST:
				return field_12894;
		}
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
		appendCollisionBoxes(pos, entityBox, boxes, state.getCollisionBox(world, pos));
		appendCollisionBoxes(pos, entityBox, boxes, this.method_11700(state));
	}

	private Box method_11700(BlockState blockState) {
		boolean bl = (Boolean)blockState.get(SHORT);
		switch ((Direction)blockState.get(FACING)) {
			case DOWN:
			default:
				return bl ? field_15175 : field_12889;
			case UP:
				return bl ? field_15174 : field_12888;
			case NORTH:
				return bl ? field_15177 : field_12891;
			case SOUTH:
				return bl ? field_15176 : field_12890;
			case WEST:
				return bl ? field_15179 : field_12893;
			case EAST:
				return bl ? field_15178 : field_12892;
		}
	}

	@Override
	public boolean method_11568(BlockState state) {
		return state.get(FACING) == Direction.UP;
	}

	@Override
	public void onBreakByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (player.abilities.creativeMode) {
			BlockPos blockPos = pos.offset(((Direction)state.get(FACING)).getOpposite());
			Block block = world.getBlockState(blockPos).getBlock();
			if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
				world.setAir(blockPos);
			}
		}

		super.onBreakByPlayer(world, pos, state, player);
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		super.onBreaking(world, pos, state);
		Direction direction = ((Direction)state.get(FACING)).getOpposite();
		pos = pos.offset(direction);
		BlockState blockState = world.getBlockState(pos);
		if ((blockState.getBlock() == Blocks.PISTON || blockState.getBlock() == Blocks.STICKY_PISTON) && (Boolean)blockState.get(PistonBlock.EXTENDED)) {
			blockState.getBlock().dropAsItem(world, pos, blockState, 0);
			world.setAir(pos);
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
	public boolean canBePlacedAtPos(World world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canBePlacedAdjacent(World world, BlockPos pos, Direction direction) {
		return false;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos) {
		Direction direction = state.get(FACING);
		BlockPos blockPos = pos.offset(direction.getOpposite());
		BlockState blockState = world.getBlockState(blockPos);
		if (blockState.getBlock() != Blocks.PISTON && blockState.getBlock() != Blocks.STICKY_PISTON) {
			world.setAir(pos);
		} else {
			blockState.neighbourUpdate(world, blockPos, block, neighborPos);
		}
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return true;
	}

	@Nullable
	public static Direction getDirection(int data) {
		int i = data & 7;
		return i > 5 ? null : Direction.getById(i);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(blockState.get(TYPE) == PistonHeadBlock.PistonHeadType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState()
			.with(FACING, getDirection(data))
			.with(TYPE, (data & 8) > 0 ? PistonHeadBlock.PistonHeadType.STICKY : PistonHeadBlock.PistonHeadType.DEFAULT);
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((Direction)state.get(FACING)).getId();
		if (state.get(TYPE) == PistonHeadBlock.PistonHeadType.STICKY) {
			i |= 8;
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
		return new StateManager(this, FACING, TYPE, SHORT);
	}

	public static enum PistonHeadType implements StringIdentifiable {
		DEFAULT("normal"),
		STICKY("sticky");

		private final String name;

		private PistonHeadType(String string2) {
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
