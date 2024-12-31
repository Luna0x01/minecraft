package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.StringIdentifiable;
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
		this.setSound(block.sound);
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + "." + WallBlock.WallType.NORMAL.getBlockStateName() + ".name");
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean blocksMovement(BlockView view, BlockPos pos) {
		return false;
	}

	@Override
	public boolean hasTransparency() {
		return false;
	}

	@Override
	public void setBoundingBox(BlockView view, BlockPos pos) {
		boolean bl = this.canWallConnectTo(view, pos.north());
		boolean bl2 = this.canWallConnectTo(view, pos.south());
		boolean bl3 = this.canWallConnectTo(view, pos.west());
		boolean bl4 = this.canWallConnectTo(view, pos.east());
		float f = 0.25F;
		float g = 0.75F;
		float h = 0.25F;
		float i = 0.75F;
		float j = 1.0F;
		if (bl) {
			h = 0.0F;
		}

		if (bl2) {
			i = 1.0F;
		}

		if (bl3) {
			f = 0.0F;
		}

		if (bl4) {
			g = 1.0F;
		}

		if (bl && bl2 && !bl3 && !bl4) {
			j = 0.8125F;
			f = 0.3125F;
			g = 0.6875F;
		} else if (!bl && !bl2 && bl3 && bl4) {
			j = 0.8125F;
			h = 0.3125F;
			i = 0.6875F;
		}

		this.setBoundingBox(f, 0.0F, h, g, j, i);
	}

	@Override
	public Box getCollisionBox(World world, BlockPos pos, BlockState state) {
		this.setBoundingBox(world, pos);
		this.boundingBoxMaxY = 1.5;
		return super.getCollisionBox(world, pos, state);
	}

	public boolean canWallConnectTo(BlockView view, BlockPos pos) {
		Block block = view.getBlockState(pos).getBlock();
		if (block == Blocks.BARRIER) {
			return false;
		} else if (block == this || block instanceof FenceGateBlock) {
			return true;
		} else {
			return block.material.isOpaque() && block.renderAsNormalBlock() ? block.material != Material.PUMPKIN : false;
		}
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (WallBlock.WallType wallType : WallBlock.WallType.values()) {
			stacks.add(new ItemStack(item, 1, wallType.getId()));
		}
	}

	@Override
	public int getMeta(BlockState state) {
		return ((WallBlock.WallType)state.get(VARIANT)).getId();
	}

	@Override
	public boolean isSideInvisible(BlockView view, BlockPos pos, Direction facing) {
		return facing == Direction.DOWN ? super.isSideInvisible(view, pos, facing) : true;
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
		return state.with(UP, !view.isAir(pos.up()))
			.with(NORTH, this.canWallConnectTo(view, pos.north()))
			.with(EAST, this.canWallConnectTo(view, pos.east()))
			.with(SOUTH, this.canWallConnectTo(view, pos.south()))
			.with(WEST, this.canWallConnectTo(view, pos.west()));
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
		private String stateName;

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
