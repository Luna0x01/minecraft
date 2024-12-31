package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class PaneBlock extends Block {
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	protected static final Box[] field_12801 = new Box[]{
		new Box(0.4375, 0.0, 0.4375, 0.5625, 1.0, 0.5625),
		new Box(0.4375, 0.0, 0.4375, 0.5625, 1.0, 1.0),
		new Box(0.0, 0.0, 0.4375, 0.5625, 1.0, 0.5625),
		new Box(0.0, 0.0, 0.4375, 0.5625, 1.0, 1.0),
		new Box(0.4375, 0.0, 0.0, 0.5625, 1.0, 0.5625),
		new Box(0.4375, 0.0, 0.0, 0.5625, 1.0, 1.0),
		new Box(0.0, 0.0, 0.0, 0.5625, 1.0, 0.5625),
		new Box(0.0, 0.0, 0.0, 0.5625, 1.0, 1.0),
		new Box(0.4375, 0.0, 0.4375, 1.0, 1.0, 0.5625),
		new Box(0.4375, 0.0, 0.4375, 1.0, 1.0, 1.0),
		new Box(0.0, 0.0, 0.4375, 1.0, 1.0, 0.5625),
		new Box(0.0, 0.0, 0.4375, 1.0, 1.0, 1.0),
		new Box(0.4375, 0.0, 0.0, 1.0, 1.0, 0.5625),
		new Box(0.4375, 0.0, 0.0, 1.0, 1.0, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.5625),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
	};
	private final boolean canDrop;

	protected PaneBlock(Material material, boolean bl) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
		this.canDrop = bl;
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
		if (!isActualState) {
			state = this.getBlockState(state, world, pos);
		}

		appendCollisionBoxes(pos, entityBox, boxes, field_12801[0]);
		if ((Boolean)state.get(NORTH)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12801[method_11637(Direction.NORTH)]);
		}

		if ((Boolean)state.get(SOUTH)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12801[method_11637(Direction.SOUTH)]);
		}

		if ((Boolean)state.get(EAST)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12801[method_11637(Direction.EAST)]);
		}

		if ((Boolean)state.get(WEST)) {
			appendCollisionBoxes(pos, entityBox, boxes, field_12801[method_11637(Direction.WEST)]);
		}
	}

	private static int method_11637(Direction direction) {
		return 1 << direction.getHorizontal();
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = this.getBlockState(state, view, pos);
		return field_12801[method_11638(state)];
	}

	private static int method_11638(BlockState blockState) {
		int i = 0;
		if ((Boolean)blockState.get(NORTH)) {
			i |= method_11637(Direction.NORTH);
		}

		if ((Boolean)blockState.get(EAST)) {
			i |= method_11637(Direction.EAST);
		}

		if ((Boolean)blockState.get(SOUTH)) {
			i |= method_11637(Direction.SOUTH);
		}

		if ((Boolean)blockState.get(WEST)) {
			i |= method_11637(Direction.WEST);
		}

		return i;
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		return state.with(NORTH, this.method_14347(view, view.getBlockState(pos.north()), pos.north(), Direction.SOUTH))
			.with(SOUTH, this.method_14347(view, view.getBlockState(pos.south()), pos.south(), Direction.NORTH))
			.with(WEST, this.method_14347(view, view.getBlockState(pos.west()), pos.west(), Direction.EAST))
			.with(EAST, this.method_14347(view, view.getBlockState(pos.east()), pos.east(), Direction.WEST));
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return !this.canDrop ? Items.AIR : super.getDropItem(state, random, id);
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
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return view.getBlockState(pos.offset(direction)).getBlock() == this ? false : super.method_8654(state, view, pos, direction);
	}

	public final boolean method_14347(BlockView blockView, BlockState blockState, BlockPos blockPos, Direction direction) {
		Block block = blockState.getBlock();
		BlockRenderLayer blockRenderLayer = blockState.getRenderLayer(blockView, blockPos, direction);
		return !method_14348(block) && blockRenderLayer == BlockRenderLayer.SOLID || blockRenderLayer == BlockRenderLayer.MIDDLE_POLE_THIN;
	}

	protected static boolean method_14348(Block block) {
		return block instanceof ShulkerBoxBlock
			|| block instanceof LeavesBlock
			|| block == Blocks.BEACON
			|| block == Blocks.CAULDRON
			|| block == Blocks.GLOWSTONE
			|| block == Blocks.ICE
			|| block == Blocks.SEA_LANTERN
			|| block == Blocks.PISTON
			|| block == Blocks.STICKY_PISTON
			|| block == Blocks.PISTON_HEAD
			|| block == Blocks.MELON_BLOCK
			|| block == Blocks.PUMPKIN
			|| block == Blocks.JACK_O_LANTERN
			|| block == Blocks.BARRIER;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public int getData(BlockState state) {
		return 0;
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

	@Override
	public BlockRenderLayer getRenderLayer(BlockView world, BlockState state, BlockPos pos, Direction direction) {
		return direction != Direction.UP && direction != Direction.DOWN ? BlockRenderLayer.MIDDLE_POLE_THIN : BlockRenderLayer.CENTER_SMALL;
	}
}
