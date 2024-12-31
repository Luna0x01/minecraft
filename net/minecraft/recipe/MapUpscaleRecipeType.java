package net.minecraft.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class MapUpscaleRecipeType extends ShapedRecipeType {
	public MapUpscaleRecipeType() {
		super(
			3,
			3,
			new ItemStack[]{
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.FILLED_MAP, 0, 32767),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER)
			},
			new ItemStack(Items.MAP, 0, 0)
		);
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		if (!super.matches(inventory, world)) {
			return false;
		} else {
			ItemStack itemStack = null;

			for (int i = 0; i < inventory.getInvSize() && itemStack == null; i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (itemStack2 != null && itemStack2.getItem() == Items.FILLED_MAP) {
					itemStack = itemStack2;
				}
			}

			if (itemStack == null) {
				return false;
			} else {
				MapState mapState = Items.FILLED_MAP.getMapState(itemStack, world);
				return mapState == null ? false : mapState.scale < 4;
			}
		}
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = null;

		for (int i = 0; i < inventory.getInvSize() && itemStack == null; i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (itemStack2 != null && itemStack2.getItem() == Items.FILLED_MAP) {
				itemStack = itemStack2;
			}
		}

		itemStack = itemStack.copy();
		itemStack.count = 1;
		if (itemStack.getNbt() == null) {
			itemStack.setNbt(new NbtCompound());
		}

		itemStack.getNbt().putBoolean("map_is_scaling", true);
		return itemStack;
	}
}
