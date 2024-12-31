package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.LeadItem;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class FenceBlock extends Block {
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	protected static final Box[] field_12667 = new Box[]{
		new Box(0.375, 0.0, 0.375, 0.625, 1.0, 0.625),
		new Box(0.375, 0.0, 0.375, 0.625, 1.0, 1.0),
		new Box(0.0, 0.0, 0.375, 0.625, 1.0, 0.625),
		new Box(0.0, 0.0, 0.375, 0.625, 1.0, 1.0),
		new Box(0.375, 0.0, 0.0, 0.625, 1.0, 0.625),
		new Box(0.375, 0.0, 0.0, 0.625, 1.0, 1.0),
		new Box(0.0, 0.0, 0.0, 0.625, 1.0, 0.625),
		new Box(0.0, 0.0, 0.0, 0.625, 1.0, 1.0),
		new Box(0.375, 0.0, 0.375, 1.0, 1.0, 0.625),
		new Box(0.375, 0.0, 0.375, 1.0, 1.0, 1.0),
		new Box(0.0, 0.0, 0.375, 1.0, 1.0, 0.625),
		new Box(0.0, 0.0, 0.375, 1.0, 1.0, 1.0),
		new Box(0.375, 0.0, 0.0, 1.0, 1.0, 0.625),
		new Box(0.375, 0.0, 0.0, 1.0, 1.0, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.625),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
	};
	public static final Box field_12668 = new Box(0.375, 0.0, 0.375, 0.625, 1.5, 0.625);
	public static final Box field_12669 = new Box(0.375, 0.0, 0.625, 0.625, 1.5, 1.0);
	public static final Box field_12664 = new Box(0.0, 0.0, 0.375, 0.375, 1.5, 0.625);
	public static final Box field_12665 = new Box(0.375, 0.0, 0.0, 0.625, 1.5, 0.375);
	public static final Box field_12666 = new Box(0.625, 0.0, 0.375, 1.0, 1.5, 0.625);

	public FenceBlock(Material material, MaterialColor materialColor) {
		super(material, materialColor);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity) {
		state = state.getBlockState(world, pos);
		appendCollisionBoxes(pos, entityBox, boxes, field_12668);
		if ((Boolean)state.get(NORTH)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12665);
		}

		if ((Boolean)state.get(EAST)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12666);
		}

		if ((Boolean)state.get(SOUTH)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12669);
		}

		if ((Boolean)state.get(WEST)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12664);
		}
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = this.getBlockState(state, view, pos);
		return field_12667[method_11612(state)];
	}

	private static int method_11612(BlockState blockState) {
		int i = 0;
		if ((Boolean)blockState.get(NORTH)) {
			i |= 1 << Direction.NORTH.getHorizontal();
		}

		if ((Boolean)blockState.get(EAST)) {
			i |= 1 << Direction.EAST.getHorizontal();
		}

		if ((Boolean)blockState.get(SOUTH)) {
			i |= 1 << Direction.SOUTH.getHorizontal();
		}

		if ((Boolean)blockState.get(WEST)) {
			i |= 1 << Direction.WEST.getHorizontal();
		}

		return i;
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
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return false;
	}

	public boolean canConnect(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block == Blocks.BARRIER) {
			return false;
		} else if ((!(block instanceof FenceBlock) || block.material != this.material) && !(block instanceof FenceGateBlock)) {
			return block.material.isOpaque() && blockState.method_11730() ? block.material != Material.PUMPKIN : false;
		} else {
			return true;
		}
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return true;
	}

	@Override
	public boolean method_421(
		World world,
		BlockPos blockPos,
		BlockState blockState,
		PlayerEntity playerEntity,
		Hand hand,
		@Nullable ItemStack itemStack,
		Direction direction,
		float f,
		float g,
		float h
	) {
		return world.isClient ? true : LeadItem.useLead(playerEntity, world, blockPos);
	}

	@Override
	public int getData(BlockState state) {
		return 0;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(NORTH, this.canConnect(view, pos.north()))
			.with(EAST, this.canConnect(view, pos.east()))
			.with(SOUTH, this.canConnect(view, pos.south()))
			.with(WEST, this.canConnect(view, pos.west()));
	}

	@Override
	public BlockState withRotation(BlockState state, BlockRotation rotation) {
		switch (rotation) {
			case CLOCKWISE_180:
				return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
			case COUNTERCLOCKWISE_90:
				return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
			case CLOCKWISE_90:
				return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
			default:
				return state;
		}
	}

	@Override
	public BlockState withMirror(BlockState state, BlockMirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT:
				return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
			case FRONT_BACK:
				return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
			default:
				return super.withMirror(state, mirror);
		}
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, NORTH, EAST, WEST, SOUTH);
	}
}
