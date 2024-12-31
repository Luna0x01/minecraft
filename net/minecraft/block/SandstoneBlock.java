package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public class SandstoneBlock extends Block {
	public static final EnumProperty<SandstoneBlock.SandstoneType> VARIANT = EnumProperty.of("type", SandstoneBlock.SandstoneType.class);

	public SandstoneBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, SandstoneBlock.SandstoneType.DEFAULT));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((SandstoneBlock.SandstoneType)state.get(VARIANT)).getId();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (SandstoneBlock.SandstoneType sandstoneType : SandstoneBlock.SandstoneType.values()) {
			stacks.add(new ItemStack(item, 1, sandstoneType.getId()));
		}
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return MaterialColor.SAND;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, SandstoneBlock.SandstoneType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((SandstoneBlock.SandstoneType)state.get(VARIANT)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum SandstoneType implements StringIdentifiable {
		DEFAULT(0, "sandstone", "default"),
		CHISELED(1, "chiseled_sandstone", "chiseled"),
		SMOOTH(2, "smooth_sandstone", "smooth");

		private static final SandstoneBlock.SandstoneType[] TYPES = new SandstoneBlock.SandstoneType[values().length];
		private final int id;
		private final String name;
		private final String stateName;

		private SandstoneType(int j, String string2, String string3) {
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

		public static SandstoneBlock.SandstoneType getById(int id) {
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
			for (SandstoneBlock.SandstoneType sandstoneType : values()) {
				TYPES[sandstoneType.getId()] = sandstoneType;
			}
		}
	}
}
