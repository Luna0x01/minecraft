package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StainedGlassPaneBlock extends PaneBlock {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

	public StainedGlassPaneBlock() {
		super(Material.GLASS, false);
		this.setDefaultState(
			this.stateManager.getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(COLOR, DyeColor.WHITE)
		);
		this.setItemGroup(ItemGroup.DECORATIONS);
	}

	@Override
	public int getMeta(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	public void appendItemStacks(Item item, ItemGroup group, List<ItemStack> stacks) {
		for (int i = 0; i < DyeColor.values().length; i++) {
			stacks.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getMaterialColor();
	}

	@Override
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
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
		return new StateManager(this, NORTH, EAST, WEST, SOUTH, COLOR);
	}

	@Override
	public void onCreation(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			BeaconBlock.updateState(world, pos);
		}
	}

	@Override
	public void onBreaking(World world, BlockPos pos, BlockState state) {
		if (!world.isClient) {
			BeaconBlock.updateState(world, pos);
		}
	}
}
