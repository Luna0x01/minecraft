package net.minecraft.block;

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
import net.minecraft.util.CommonI18n;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class RedSandstoneSlabBlock extends SlabBlock {
	public static final BooleanProperty SEAMLESS = BooleanProperty.of("seamless");
	public static final EnumProperty<RedSandstoneSlabBlock.SlabType> VARIANT = EnumProperty.of("variant", RedSandstoneSlabBlock.SlabType.class);

	public RedSandstoneSlabBlock() {
		super(Material.STONE);
		BlockState blockState = this.stateManager.getDefaultState();
		if (this.isDoubleSlab()) {
			blockState = blockState.with(SEAMLESS, false);
		} else {
			blockState = blockState.with(HALF, SlabBlock.SlabType.BOTTOM);
		}

		this.setDefaultState(blockState.with(VARIANT, RedSandstoneSlabBlock.SlabType.RED_SANDSTONE));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + ".red_sandstone.name");
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return Item.fromBlock(Blocks.STONE_SLAB2);
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(Blocks.STONE_SLAB2, 1, ((RedSandstoneSlabBlock.SlabType)blockState.get(VARIANT)).getId());
	}

	@Override
	public String getVariantTranslationKey(int slabType) {
		return super.getTranslationKey() + "." + RedSandstoneSlabBlock.SlabType.getById(slabType).getName();
	}

	@Override
	public Property<?> getSlabProperty() {
		return VARIANT;
	}

	@Override
	public Comparable<?> method_11615(ItemStack itemStack) {
		return RedSandstoneSlabBlock.SlabType.getById(itemStack.getData() & 7);
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		if (item != Item.fromBlock(Blocks.DOUBLE_STONE_SLAB2)) {
			for (RedSandstoneSlabBlock.SlabType slabType : RedSandstoneSlabBlock.SlabType.values()) {
				defaultedList.add(new ItemStack(item, 1, slabType.getId()));
			}
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		BlockState blockState = this.getDefaultState().with(VARIANT, RedSandstoneSlabBlock.SlabType.getById(data & 7));
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
		i |= ((RedSandstoneSlabBlock.SlabType)state.get(VARIANT)).getId();
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
	public MaterialColor getMaterialColor(BlockState state) {
		return ((RedSandstoneSlabBlock.SlabType)state.get(VARIANT)).getColor();
	}

	@Override
	public int getMeta(BlockState state) {
		return ((RedSandstoneSlabBlock.SlabType)state.get(VARIANT)).getId();
	}

	public static enum SlabType implements StringIdentifiable {
		RED_SANDSTONE(0, "red_sandstone", SandBlock.SandType.RED_SAND.getColor());

		private static final RedSandstoneSlabBlock.SlabType[] TYPES = new RedSandstoneSlabBlock.SlabType[values().length];
		private final int id;
		private final String name;
		private final MaterialColor color;

		private SlabType(int j, String string2, MaterialColor materialColor) {
			this.id = j;
			this.name = string2;
			this.color = materialColor;
		}

		public int getId() {
			return this.id;
		}

		public MaterialColor getColor() {
			return this.color;
		}

		public String toString() {
			return this.name;
		}

		public static RedSandstoneSlabBlock.SlabType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getName() {
			return this.name;
		}

		static {
			for (RedSandstoneSlabBlock.SlabType slabType : values()) {
				TYPES[slabType.getId()] = slabType;
			}
		}
	}
}
