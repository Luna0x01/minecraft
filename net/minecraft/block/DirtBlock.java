package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DirtBlock extends Block {
	public static final EnumProperty<DirtBlock.DirtType> VARIANT = EnumProperty.of("variant", DirtBlock.DirtType.class);
	public static final BooleanProperty SNOWY = BooleanProperty.of("snowy");

	protected DirtBlock() {
		super(Material.DIRT);
		this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, DirtBlock.DirtType.DIRT).with(SNOWY, false));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((DirtBlock.DirtType)state.get(VARIANT)).getColor();
	}

	@Override
	public BlockState getBlockState(BlockState state, BlockView view, BlockPos pos) {
		if (state.get(VARIANT) == DirtBlock.DirtType.PODZOL) {
			Block block = view.getBlockState(pos.up()).getBlock();
			state = state.with(SNOWY, block == Blocks.SNOW || block == Blocks.SNOW_LAYER);
		}

		return state;
	}

	@Override
	public void method_13700(Item item, ItemGroup itemGroup, DefaultedList<ItemStack> defaultedList) {
		defaultedList.add(new ItemStack(this, 1, DirtBlock.DirtType.DIRT.getId()));
		defaultedList.add(new ItemStack(this, 1, DirtBlock.DirtType.COARSE_DIRT.getId()));
		defaultedList.add(new ItemStack(this, 1, DirtBlock.DirtType.PODZOL.getId()));
	}

	@Override
	public ItemStack getItemStack(World world, BlockPos blockPos, BlockState blockState) {
		return new ItemStack(this, 1, ((DirtBlock.DirtType)blockState.get(VARIANT)).getId());
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(VARIANT, DirtBlock.DirtType.getById(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((DirtBlock.DirtType)state.get(VARIANT)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, VARIANT, SNOWY);
	}

	@Override
	public int getMeta(BlockState state) {
		DirtBlock.DirtType dirtType = state.get(VARIANT);
		if (dirtType == DirtBlock.DirtType.PODZOL) {
			dirtType = DirtBlock.DirtType.DIRT;
		}

		return dirtType.getId();
	}

	public static enum DirtType implements StringIdentifiable {
		DIRT(0, "dirt", "default", MaterialColor.DIRT),
		COARSE_DIRT(1, "coarse_dirt", "coarse", MaterialColor.DIRT),
		PODZOL(2, "podzol", MaterialColor.SPRUCE);

		private static final DirtBlock.DirtType[] VARIANTS = new DirtBlock.DirtType[values().length];
		private final int id;
		private final String name;
		private final String stateName;
		private final MaterialColor color;

		private DirtType(int j, String string2, MaterialColor materialColor) {
			this(j, string2, string2, materialColor);
		}

		private DirtType(int j, String string2, String string3, MaterialColor materialColor) {
			this.id = j;
			this.name = string2;
			this.stateName = string3;
			this.color = materialColor;
		}

		public int getId() {
			return this.id;
		}

		public String getStateName() {
			return this.stateName;
		}

		public MaterialColor getColor() {
			return this.color;
		}

		public String toString() {
			return this.name;
		}

		public static DirtBlock.DirtType getById(int id) {
			if (id < 0 || id >= VARIANTS.length) {
				id = 0;
			}

			return VARIANTS[id];
		}

		@Override
		public String asString() {
			return this.name;
		}

		static {
			for (DirtBlock.DirtType dirtType : values()) {
				VARIANTS[dirtType.getId()] = dirtType;
			}
		}
	}
}
