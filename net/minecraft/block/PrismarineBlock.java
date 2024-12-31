package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;

public class PrismarineBlock extends Block {
	public static final EnumProperty<PrismarineBlock.PrismarineType> VARIANT = EnumProperty.of("variant", PrismarineBlock.PrismarineType.class);
	public static final int ROUGH_ID = PrismarineBlock.PrismarineType.ROUGH.getId();
	public static final int BRICKS_ID = PrismarineBlock.PrismarineType.BRICKS.getId();
	public static final int DARK_ID = PrismarineBlock.PrismarineType.DARK.getId();

	public PrismarineBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, PrismarineBlock.PrismarineType.ROUGH));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + "." + PrismarineBlock.PrismarineType.ROUGH.getStateName() + ".name");
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return state.get(VARIANT) == PrismarineBlock.PrismarineType.ROUGH ? MaterialColor.CYAN : MaterialColor.DIAMOND;
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PrismarineBlock.PrismarineType)state.get(VARIANT)).getId();
	}

	@Override
	public int getData(BlockState state) {
		return ((PrismarineBlock.PrismarineType)state.get(VARIANT)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, PrismarineBlock.PrismarineType.getById(data));
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(item, 1, ROUGH_ID));
		defaultedList.add(new ItemStack(item, 1, BRICKS_ID));
		defaultedList.add(new ItemStack(item, 1, DARK_ID));
	}

	public static enum PrismarineType implements StringIdentifiable {
		ROUGH(0, "prismarine", "rough"),
		BRICKS(1, "prismarine_bricks", "bricks"),
		DARK(2, "dark_prismarine", "dark");

		private static final PrismarineBlock.PrismarineType[] TYPES = new PrismarineBlock.PrismarineType[values().length];
		private final int id;
		private final String name;
		private final String stateName;

		private PrismarineType(int j, String string2, String string3) {
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

		public static PrismarineBlock.PrismarineType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getStateName() {
			return this.stateName;
		}

		static {
			for (PrismarineBlock.PrismarineType prismarineType : values()) {
				TYPES[prismarineType.getId()] = prismarineType;
			}
		}
	}
}
