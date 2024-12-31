package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;

public class PlanksBlock extends Block {
	public static final EnumProperty<PlanksBlock.WoodType> VARIANT = EnumProperty.of("variant", PlanksBlock.WoodType.class);

	public PlanksBlock() {
		super(Material.WOOD);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, PlanksBlock.WoodType.OAK));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		for (PlanksBlock.WoodType woodType : PlanksBlock.WoodType.values()) {
			defaultedList.add(new ItemStack(item, 1, woodType.getId()));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, PlanksBlock.WoodType.getById(data));
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getMaterialColor();
	}

	@Override
	public int getData(BlockState state) {
		return ((PlanksBlock.WoodType)state.get(VARIANT)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum WoodType implements StringIdentifiable {
		OAK(0, "oak", MaterialColor.WOOD),
		SPRUCE(1, "spruce", MaterialColor.SPRUCE),
		BIRCH(2, "birch", MaterialColor.SAND),
		JUNGLE(3, "jungle", MaterialColor.DIRT),
		ACACIA(4, "acacia", MaterialColor.ORANGE),
		DARK_OAK(5, "dark_oak", "big_oak", MaterialColor.BROWN);

		private static final PlanksBlock.WoodType[] TYPES = new PlanksBlock.WoodType[values().length];
		private final int id;
		private final String name;
		private final String oldName;
		private final MaterialColor materialColor;

		private WoodType(int j, String string2, MaterialColor materialColor) {
			this(j, string2, string2, materialColor);
		}

		private WoodType(int j, String string2, String string3, MaterialColor materialColor) {
			this.id = j;
			this.name = string2;
			this.oldName = string3;
			this.materialColor = materialColor;
		}

		public int getId() {
			return this.id;
		}

		public MaterialColor getMaterialColor() {
			return this.materialColor;
		}

		public String toString() {
			return this.name;
		}

		public static PlanksBlock.WoodType getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getOldName() {
			return this.oldName;
		}

		static {
			for (PlanksBlock.WoodType woodType : values()) {
				TYPES[woodType.getId()] = woodType;
			}
		}
	}
}
