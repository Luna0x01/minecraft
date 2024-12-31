package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;

public class StoneBrickBlock extends Block {
	public static final EnumProperty<StoneBrickBlock.Type> VARIANT = EnumProperty.of("variant", StoneBrickBlock.Type.class);
	public static final int DEFAULT_ID = StoneBrickBlock.Type.DEFAULT.byId();
	public static final int MOSSY_ID = StoneBrickBlock.Type.MOSSY.byId();
	public static final int CRACKED_ID = StoneBrickBlock.Type.CRACKED.byId();
	public static final int CHISELED_ID = StoneBrickBlock.Type.CHISELED.byId();

	public StoneBrickBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, StoneBrickBlock.Type.DEFAULT));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((StoneBrickBlock.Type)state.get(VARIANT)).byId();
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> stacks) {
		for (StoneBrickBlock.Type type : StoneBrickBlock.Type.values()) {
			stacks.add(new ItemStack(this, 1, type.byId()));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, StoneBrickBlock.Type.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((StoneBrickBlock.Type)state.get(VARIANT)).byId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum Type implements StringIdentifiable {
		DEFAULT(0, "stonebrick", "default"),
		MOSSY(1, "mossy_stonebrick", "mossy"),
		CRACKED(2, "cracked_stonebrick", "cracked"),
		CHISELED(3, "chiseled_stonebrick", "chiseled");

		private static final StoneBrickBlock.Type[] TYPES = new StoneBrickBlock.Type[values().length];
		private final int id;
		private final String key;
		private final String name;

		private Type(int j, String string2, String string3) {
			this.id = j;
			this.key = string2;
			this.name = string3;
		}

		public int byId() {
			return this.id;
		}

		public String toString() {
			return this.key;
		}

		public static StoneBrickBlock.Type getById(int id) {
			if (id < 0 || id >= TYPES.length) {
				id = 0;
			}

			return TYPES[id];
		}

		@Override
		public String asString() {
			return this.key;
		}

		public String getValueName() {
			return this.name;
		}

		static {
			for (StoneBrickBlock.Type type : values()) {
				TYPES[type.byId()] = type;
			}
		}
	}
}
