package net.minecraft.recipe;

import net.minecraft.class_3082;
import net.minecraft.class_3578;
import net.minecraft.class_3579;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class MapUpscaleRecipeType extends ShapedRecipeType {
	public MapUpscaleRecipeType(Identifier identifier) {
		super(
			identifier,
			"",
			3,
			3,
			DefaultedList.copyOf(
				Ingredient.field_15680,
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.FILLED_MAP),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER),
				Ingredient.ofItems(Items.PAPER)
			),
			new ItemStack(Items.MAP)
		);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!super.method_3500(inventory, world)) {
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
				MapState mapState = FilledMapItem.method_16111(itemStack, world);
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
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getInvSize() && itemStack.isEmpty(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (itemStack2.getItem() == Items.FILLED_MAP) {
				itemStack = itemStack2;
			}
		}

		itemStack = itemStack.copy();
		itemStack.setCount(1);
		itemStack.getOrCreateNbt().putInt("map_scale_direction", 1);
		return itemStack;
	}

	@Override
	public boolean method_14251() {
		return true;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17452;
	}
}
