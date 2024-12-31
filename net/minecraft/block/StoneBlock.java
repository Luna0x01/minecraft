package net.minecraft.block;

import java.util.Random;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

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
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return ((StoneBlock.StoneType)state.get(VARIANT)).getColor();
	}

	@Override
	public Item getDropItem(BlockState state, Random random, int id) {
		return state.get(VARIANT) == StoneBlock.StoneType.STONE ? Item.fromBlock(Blocks.COBBLESTONE) : Item.fromBlock(Blocks.STONE);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((StoneBlock.StoneType)state.get(VARIANT)).byId();
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> stacks) {
		for (StoneBlock.StoneType stoneType : StoneBlock.StoneType.values()) {
			stacks.add(new ItemStack(this, 1, stoneType.byId()));
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
		STONE(0, MaterialColor.STONE, "stone", true),
		GRANITE(1, MaterialColor.DIRT, "granite", true),
		POLISHED_GRANITE(2, MaterialColor.DIRT, "smooth_granite", "graniteSmooth", false),
		DIORITE(3, MaterialColor.QUARTZ, "diorite", true),
		POLISHED_DIORITE(4, MaterialColor.QUARTZ, "smooth_diorite", "dioriteSmooth", false),
		ANDESITE(5, MaterialColor.STONE, "andesite", true),
		POLISHED_ANDESITE(6, MaterialColor.STONE, "smooth_andesite", "andesiteSmooth", false);

		private static final StoneBlock.StoneType[] TYPES = new StoneBlock.StoneType[values().length];
		private final int id;
		private final String name;
		private final String translationKey;
		private final MaterialColor color;
		private final boolean field_15146;

		private StoneType(int j, MaterialColor materialColor, String string2, boolean bl) {
			this(j, materialColor, string2, string2, bl);
		}

		private StoneType(int j, MaterialColor materialColor, String string2, String string3, boolean bl) {
			this.id = j;
			this.name = string2;
			this.translationKey = string3;
			this.color = materialColor;
			this.field_15146 = bl;
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

		public boolean method_13719() {
			return this.field_15146;
		}

		static {
			for (StoneBlock.StoneType stoneType : values()) {
				TYPES[stoneType.byId()] = stoneType;
			}
		}
	}
}
