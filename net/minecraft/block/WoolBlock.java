package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class WoolBlock extends Block {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

	public WoolBlock(Material material) {
		super(material);
		this.setDefaultState(this.stateManager.getDefaultState().with(COLOR, DyeColor.WHITE));
		this.setItemGroup(ItemGroup.BUILDING_BLOCKS);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	public void addStacksForDisplay(ItemGroup group, DefaultedList<ItemStack> stacks) {
		for (DyeColor dyeColor : DyeColor.values()) {
			stacks.add(new ItemStack(this, 1, dyeColor.getId()));
		}
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, BlockView view, BlockPos pos) {
		return MaterialColor.fromDye(state.get(COLOR));
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(COLOR, DyeColor.byId(data));
	}

	@Override
	public int getData(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, COLOR);
	}
}
