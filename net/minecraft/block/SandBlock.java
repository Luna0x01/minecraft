package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

public class SandBlock extends FallingBlock {
	public static final EnumProperty<SandBlock.SandType> sandType = EnumProperty.of("variant", SandBlock.SandType.class);

	public SandBlock() {
		this.setDefaultState(this.stateManager.getDefaultState().with(sandType, SandBlock.SandType.SAND));
	}

	@Override
	public int getMeta(BlockState state) {
		return ((SandBlock.SandType)state.get(sandType)).getId();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (SandBlock.SandType sandType : SandBlock.SandType.values()) {
			stacks.add(new ItemStack(item, 1, sandType.getId()));
		}
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((SandBlock.SandType)state.get(sandType)).getColor();
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(sandType, SandBlock.SandType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((SandBlock.SandType)state.get(sandType)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, sandType);
	}

	@Override
	public int getColor(BlockState state) {
		SandBlock.SandType sandType = state.get(SandBlock.sandType);
		return sandType.method_13318();
	}

	public static enum SandType implements StringIdentifiable {
		SAND(0, "sand", "default", MaterialColor.SAND, -2370656),
		RED_SAND(1, "red_sand", "red", MaterialColor.ORANGE, -5679071);

		private static final SandBlock.SandType[] TYPES = new SandBlock.SandType[values().length];
		private final int id;
		private final String name;
		private final MaterialColor color;
		private final String translationKey;
		private final int field_14835;

		private SandType(int j, String string2, String string3, MaterialColor materialColor, int k) {
			this.id = j;
			this.name = string2;
			this.color = materialColor;
			this.translationKey = string3;
			this.field_14835 = k;
		}

		public int method_13318() {
			return this.field_14835;
		}

		public int getId() {
			return this.id;
		}

		public String toString() {
			return this.name;
		}

		public MaterialColor getColor() {
			return this.color;
		}

		public static SandBlock.SandType getById(int id) {
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
			for (SandBlock.SandType sandType : values()) {
				TYPES[sandType.getId()] = sandType;
			}
		}
	}
}
