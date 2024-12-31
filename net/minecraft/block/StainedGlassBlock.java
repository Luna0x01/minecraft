package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class StainedGlassBlock extends TransparentBlock {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.of("color", DyeColor.class);

	public StainedGlassBlock(Material material) {
		super(material, false);
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
	public RenderLayer getRenderLayerType() {
		return RenderLayer.TRANSLUCENT;
	}

	@Override
	public int getDropCount(Random rand) {
		return 0;
	}

	@Override
	protected boolean requiresSilkTouch() {
		return true;
	}

	@Override
	public boolean method_11562(BlockState state) {
		return false;
	}

	@Override
	public BlockState stateFromData(int data) {
		return this.getDefaultState().with(COLOR, DyeColor.byId(data));
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

	@Override
	public int getData(BlockState state) {
		return ((DyeColor)state.get(COLOR)).getId();
	}

	@Override
	protected StateManager appendProperties() {
		return new StateManager(this, COLOR);
	}
}
