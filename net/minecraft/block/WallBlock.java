package net.minecraft.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class WallBlock extends Block {
	public static final BooleanProperty UP = BooleanProperty.of("up");
	public static final BooleanProperty NORTH = BooleanProperty.of("north");
	public static final BooleanProperty EAST = BooleanProperty.of("east");
	public static final BooleanProperty SOUTH = BooleanProperty.of("south");
	public static final BooleanProperty WEST = BooleanProperty.of("west");
	public static final EnumProperty<WallBlock.WallType> VARIANT = EnumProperty.of("variant", WallBlock.WallType.class);
	protected static final Box[] field_12831 = new Box[]{
		new Box(0.25, 0.0, 0.25, 0.75, 1.0, 0.75),
		new Box(0.25, 0.0, 0.25, 0.75, 1.0, 1.0),
		new Box(0.0, 0.0, 0.25, 0.75, 1.0, 0.75),
		new Box(0.0, 0.0, 0.25, 0.75, 1.0, 1.0),
		new Box(0.25, 0.0, 0.0, 0.75, 1.0, 0.75),
		new Box(0.3125, 0.0, 0.0, 0.6875, 0.875, 1.0),
		new Box(0.0, 0.0, 0.0, 0.75, 1.0, 0.75),
		new Box(0.0, 0.0, 0.0, 0.75, 1.0, 1.0),
		new Box(0.25, 0.0, 0.25, 1.0, 1.0, 0.75),
		new Box(0.25, 0.0, 0.25, 1.0, 1.0, 1.0),
		new Box(0.0, 0.0, 0.3125, 1.0, 0.875, 0.6875),
		new Box(0.0, 0.0, 0.25, 1.0, 1.0, 1.0),
		new Box(0.25, 0.0, 0.0, 1.0, 1.0, 0.75),
		new Box(0.25, 0.0, 0.0, 1.0, 1.0, 1.0),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 0.75),
		new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
	};
	protected static final Box[] field_12830 = new Box[]{
		field_12831[0].withMaxY(1.5),
		field_12831[1].withMaxY(1.5),
		field_12831[2].withMaxY(1.5),
		field_12831[3].withMaxY(1.5),
		field_12831[4].withMaxY(1.5),
		field_12831[5].withMaxY(1.5),
		field_12831[6].withMaxY(1.5),
		field_12831[7].withMaxY(1.5),
		field_12831[8].withMaxY(1.5),
		field_12831[9].withMaxY(1.5),
		field_12831[10].withMaxY(1.5),
		field_12831[11].withMaxY(1.5),
		field_12831[12].withMaxY(1.5),
		field_12831[13].withMaxY(1.5),
		field_12831[14].withMaxY(1.5),
		field_12831[15].withMaxY(1.5)
	};

	public WallBlock(Block block) {
		super(block.material);
		this.setDefaultState(
			this.stateManager
				.getDefaultState()
				.with(UP, false)
				.with(NORTH, false)
				.with(EAST, false)
				.with(SOUTH, false)
				.with(WEST, false)
				.with(VARIANT, WallBlock.WallType.NORMAL)
		);
		this.setStrength(block.hardness);
		this.setResistance(block.blastResistance / 3.0F);
		this.setBlockSoundGroup(block.blockSoundGroup);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public Box getCollisionBox(BlockState state, BlockView view, BlockPos pos) {
		state = this.getBlockState(state, view, pos);
		return field_12831[method_11643(state)];
	}

	@Override
	public void appendCollisionBoxes(BlockState state, World world, BlockPos pos, Box entityBox, List<Box> boxes, @Nullable Entity entity, boolean isActualState) {
		if (!isActualState) {
			state = this.getBlockState(state, world, pos);
		}

		appendCollisionBoxes(pos, entityBox, boxes, field_12830[method_11643(state)]);
	}

	@Nullable
	@Override
	public Box method_8640(BlockState state, BlockView view, BlockPos pos) {
		state = this.getBlockState(state, view, pos);
		return field_12830[method_11643(state)];
	}

	private static int method_11643(BlockState blockState) {
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
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + "." + WallBlock.WallType.NORMAL.getBlockStateName() + ".name");
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public boolean isFullBoundsCubeForCulling(BlockState blockState) {
		return false;
	}

	private boolean canWallConnectTo(BlockView view, BlockPos pos) {
		BlockState blockState = view.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block == Blocks.BARRIER) {
			return false;
		} else if (block == this || block instanceof FenceGateBlock) {
			return true;
		} else {
			return block.material.isOpaque() && blockState.method_11730() ? block.material != Material.PUMPKIN : false;
		}
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		for (WallBlock.WallType wallType : WallBlock.WallType.values()) {
			defaultedList.add(new ItemStack(item, 1, wallType.getId()));
		}
	}

	@Override
	public int getMeta(BlockState state) {
		return ((WallBlock.WallType)state.get(VARIANT)).getId();
	}

	@Override
	public boolean method_8654(BlockState state, BlockView view, BlockPos pos, Direction direction) {
		return direction == Direction.DOWN ? super.method_8654(state, view, pos, direction) : true;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, WallBlock.WallType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((WallBlock.WallType)state.get(VARIANT)).getId();
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		boolean bl = this.canWallConnectTo(view, pos.north());
		boolean bl2 = this.canWallConnectTo(view, pos.east());
		boolean bl3 = this.canWallConnectTo(view, pos.south());
		boolean bl4 = this.canWallConnectTo(view, pos.west());
		boolean bl5 = bl && !bl2 && bl3 && !bl4 || !bl && bl2 && !bl3 && bl4;
		return state.with(UP, !bl5 || !view.isAir(pos.up())).with(NORTH, bl).with(EAST, bl2).with(SOUTH, bl3).with(WEST, bl4);
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, UP, NORTH, EAST, WEST, SOUTH, VARIANT);
	}

	public static enum WallType implements StringIdentifiable {
		NORMAL(0, "cobblestone", "normal"),
		MOSSY(1, "mossy_cobblestone", "mossy");

		private static final WallBlock.WallType[] TYPES = new WallBlock.WallType[values().length];
		private final int id;
		private final String name;
		private final String stateName;

		private WallType(int j, String string2, String string3) {
			this.id = j;
			this.name = string2;
			this.stateName = string3;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public static WallBlock.WallType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getBlockStateName() {
			return this.stateName;
		}

		static {
			for (WallBlock.WallType wallType : values()) {
				TYPES[wallType.getId()] = wallType;
			}
		}
	}
}
