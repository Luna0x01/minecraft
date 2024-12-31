package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public class RedSandstoneBlock extends Block {
	public static final EnumProperty<RedSandstoneBlock.RedSandstoneType> TYPE = EnumProperty.of("type", RedSandstoneBlock.RedSandstoneType.class);

	public RedSandstoneBlock() {
		super(Material.STONE, SandBlock.SandType.RED_SAND.getColor());
		this.setDefaultState(this.stateManager.getDefaultState().with(TYPE, RedSandstoneBlock.RedSandstoneType.DEFAULT));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((RedSandstoneBlock.RedSandstoneType)state.get(TYPE)).getId();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (RedSandstoneBlock.RedSandstoneType redSandstoneType : RedSandstoneBlock.RedSandstoneType.values()) {
			stacks.add(new ItemStack(item, 1, redSandstoneType.getId()));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(TYPE, RedSandstoneBlock.RedSandstoneType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((RedSandstoneBlock.RedSandstoneType)state.get(TYPE)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, TYPE);
	}

	public static enum RedSandstoneType implements StringIdentifiable {
		DEFAULT(0, "red_sandstone", "default"),
		CHISELED(1, "chiseled_red_sandstone", "chiseled"),
		SMOOTH(2, "smooth_red_sandstone", "smooth");

		private static final RedSandstoneBlock.RedSandstoneType[] TYPES = new RedSandstoneBlock.RedSandstoneType[values().length];
		private final int id;
		private final String name;
		private final String stateName;

		private RedSandstoneType(int j, String string2, String string3) {
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

		public static RedSandstoneBlock.RedSandstoneType getById(int id) {
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
			for (RedSandstoneBlock.RedSandstoneType redSandstoneType : values()) {
				TYPES[redSandstoneType.getId()] = redSandstoneType;
			}
		}
	}
}
