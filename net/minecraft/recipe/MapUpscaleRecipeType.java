package net.minecraft.recipe;

import net.minecraft.class_3082;
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
				new ItemStack(Items.FILLED_MAP, 1, 32767),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER),
				new ItemStack(Items.PAPER)
			},
			new ItemStack(Items.MAP)
		);
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		if (!super.matches(inventory, world)) {
			return false;
		} else {
			ItemStack itemStack = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize() && itemStack.isEmpty(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (itemStack2.getItem() == Items.FILLED_MAP) {
					itemStack = itemStack2;
				}
			}

			if (itemStack.isEmpty()) {
				return false;
			} else {
				MapState mapState = Items.FILLED_MAP.getMapState(itemStack, world);
				if (mapState == null) {
					return false;
				} else {
					return this.method_13669(mapState) ? false : mapState.scale < 4;
				}
			}
		}
	}

	private boolean method_13669(MapState mapState) {
		if (mapState.icons != null) {
			for (class_3082 lv : mapState.icons.values()) {
				if (lv.method_13820() == class_3082.class_3083.MANSION || lv.method_13820() == class_3082.class_3083.MONUMENT) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getInvSize() && itemStack.isEmpty(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (itemStack2.getItem() == Items.FILLED_MAP) {
				itemStack = itemStack2;
			}
		}

		itemStack = itemStack.copy();
		itemStack.setCount(1);
		if (itemStack.getNbt() == null) {
			itemStack.setNbt(new NbtCompound());
		}

		itemStack.getNbt().putInt("map_scale_direction", 1);
		return itemStack;
	}
}
