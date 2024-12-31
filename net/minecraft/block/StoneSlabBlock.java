package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class StoneSlabBlock extends SlabBlock {
	public static final BooleanProperty SEAMLESS = BooleanProperty.of("seamless");
	public static final EnumProperty<StoneSlabBlock.SlabType> VARIANT = EnumProperty.of("variant", StoneSlabBlock.SlabType.class);

	public StoneSlabBlock() {
		super(Material.STONE);
		BlockState blockState = this.stateManager.getDefaultState();
		if (this.isDoubleSlab()) {
			blockState = blockState.with(SEAMLESS, false);
		} else {
			blockState = blockState.with(HALF, SlabBlock.SlabType.BOTTOM);
		}

		this.setDefaultState(blockState.with(VARIANT, StoneSlabBlock.SlabType.STONE));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.STONE_SLAB);
	}

	@Override
	public Item getPickItem(World world, BlockPos pos) {
		return Item.fromBlock(Blocks.STONE_SLAB);
	}

	@Override
	public String getVariantTranslationKey(int slabType) {
		return super.getTranslationKey() + "." + StoneSlabBlock.SlabType.getById(slabType).getTranslationKey();
	}

	@Override
	public Property<?> getSlabProperty() {
		return VARIANT;
	}

	@Override
	public Object getSlabType(ItemStack stack) {
		return StoneSlabBlock.SlabType.getById(stack.getData() & 7);
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		if (item != Item.fromBlock(Blocks.DOUBLE_STONE_SLAB)) {
			for (StoneSlabBlock.SlabType slabType : StoneSlabBlock.SlabType.values()) {
				if (slabType != StoneSlabBlock.SlabType.WOOD) {
					stacks.add(new ItemStack(item, 1, slabType.getId()));
				}
			}
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(VARIANT, StoneSlabBlock.SlabType.getById(data & 7));
		if (this.isDoubleSlab()) {
			blockState = blockState.with(SEAMLESS, (data & 8) != 0);
		} else {
			blockState = blockState.with(HALF, (data & 8) == 0 ? SlabBlock.SlabType.BOTTOM : SlabBlock.SlabType.TOP);
		}

		return blockState;
	}

	@Override
	public int getData(BlockState state) {
		int i = 0;
		i |= ((StoneSlabBlock.SlabType)state.get(VARIANT)).getId();
		if (this.isDoubleSlab()) {
			if ((Boolean)state.get(SEAMLESS)) {
				i |= 8;
			}
		} else if (state.get(HALF) == SlabBlock.SlabType.TOP) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected StateManager appendProperties() {
		return this.isDoubleSlab() ? new StateManager(this, SEAMLESS, VARIANT) : new StateManager(this, HALF, VARIANT);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((StoneSlabBlock.SlabType)state.get(VARIANT)).getId();
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((StoneSlabBlock.SlabType)state.get(VARIANT)).getMaterialColor();
	}

	public static enum SlabType implements StringIdentifiable {
		STONE(0, MaterialColor.STONE, "stone"),
		SANDSTONE(1, MaterialColor.SAND, "sandstone", "sand"),
		WOOD(2, MaterialColor.WOOD, "wood_old", "wood"),
		COBBLESTONE(3, MaterialColor.STONE, "cobblestone", "cobble"),
		BRICK(4, MaterialColor.RED, "brick"),
		STONE_BRICK(5, MaterialColor.STONE, "stone_brick", "smoothStoneBrick"),
		NETHER_BRICK(6, MaterialColor.NETHER, "nether_brick", "netherBrick"),
		QUARTZ(7, MaterialColor.QUARTZ, "quartz");

		private static final StoneSlabBlock.SlabType[] TYPES = new StoneSlabBlock.SlabType[values().length];
		private final int id;
		private final MaterialColor color;
		private final String name;
		private final String translationKey;

		private SlabType(int j, MaterialColor materialColor, String string2) {
			this(j, materialColor, string2, string2);
		}

		private SlabType(int j, MaterialColor materialColor, String string2, String string3) {
			this.id = j;
			this.color = materialColor;
			this.name = string2;
			this.translationKey = string3;
		}

		public int getId() {
			return this.id;
		}

		public MaterialColor getMaterialColor() {
			return this.color;
		}

		public String toString() {
			return this.name;
		}

		public static StoneSlabBlock.SlabType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getTranslationKey() {
			return this.translationKey;
		}

		static {
			for (StoneSlabBlock.SlabType slabType : values()) {
				TYPES[slabType.getId()] = slabType;
			}
		}
	}
}
