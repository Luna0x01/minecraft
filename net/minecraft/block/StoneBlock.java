package net.minecraft.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.StringIdentifiable;

public class StoneBlock extends Block {
	public static final EnumProperty<StoneBlock.StoneType> VARIANT = EnumProperty.of("variant", StoneBlock.StoneType.class);

	public StoneBlock() {
		super(Material.STONE);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, StoneBlock.StoneType.STONE));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public String getTranslatedName() {
		return CommonI18n.translate(this.getTranslationKey() + "." + StoneBlock.StoneType.STONE.getTranslationKey() + ".name");
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((StoneBlock.StoneType)state.get(VARIANT)).getColor();
	}

	@Nullable
	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(VARIANT) == StoneBlock.StoneType.STONE ? Item.fromBlock(Blocks.COBBLESTONE) : Item.fromBlock(Blocks.STONE);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((StoneBlock.StoneType)state.get(VARIANT)).byId();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (StoneBlock.StoneType stoneType : StoneBlock.StoneType.values()) {
			stacks.add(new ItemStack(item, 1, stoneType.byId()));
		}
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, StoneBlock.StoneType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((StoneBlock.StoneType)state.get(VARIANT)).byId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT);
	}

	public static enum StoneType implements StringIdentifiable {
		STONE(0, MaterialColor.STONE, "stone"),
		GRANITE(1, MaterialColor.DIRT, "granite"),
		POLISHED_GRANITE(2, MaterialColor.DIRT, "smooth_granite", "graniteSmooth"),
		DIORITE(3, MaterialColor.QUARTZ, "diorite"),
		POLISHED_DIORITE(4, MaterialColor.QUARTZ, "smooth_diorite", "dioriteSmooth"),
		ANDESITE(5, MaterialColor.STONE, "andesite"),
		POLISHED_ANDESITE(6, MaterialColor.STONE, "smooth_andesite", "andesiteSmooth");

		private static final StoneBlock.StoneType[] TYPES = new StoneBlock.StoneType[values().length];
		private final int id;
		private final String name;
		private final String translationKey;
		private final MaterialColor color;

		private StoneType(int j, MaterialColor materialColor, String string2) {
			this(j, materialColor, string2, string2);
		}

		private StoneType(int j, MaterialColor materialColor, String string2, String string3) {
			this.id = j;
			this.name = string2;
			this.translationKey = string3;
			this.color = materialColor;
		}

		public int byId() {
			return this.id;
		}

		public MaterialColor getColor() {
			return this.color;
		}

		public String toString() {
			return this.name;
		}

		public static StoneBlock.StoneType getById(int id) {
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
			for (StoneBlock.StoneType stoneType : values()) {
				TYPES[stoneType.byId()] = stoneType;
			}
		}
	}
}
