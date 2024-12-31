package net.minecraft;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;

public class class_3320 extends class_4471 {
	private final RecipeDispatcher field_19901;
	private final Map<class_4113, List<class_3286>> field_19902 = Maps.newHashMap();
	private final List<class_3286> field_19903 = Lists.newArrayList();

	public class_3320(RecipeDispatcher recipeDispatcher) {
		this.field_19901 = recipeDispatcher;
	}

	public void method_18142() {
		this.field_19903.clear();
		this.field_19902.clear();
		Table<class_4113, String, class_3286> table = HashBasedTable.create();

		for (RecipeType recipeType : this.field_19901.method_16208()) {
			if (!recipeType.method_14251()) {
				class_4113 lv = method_18145(recipeType);
				String string = recipeType.method_14253();
				class_3286 lv2;
				if (string.isEmpty()) {
					lv2 = this.method_18139(lv);
				} else {
					lv2 = (class_3286)table.get(lv, string);
					if (lv2 == null) {
						lv2 = this.method_18139(lv);
						table.put(lv, string, lv2);
					}
				}

				lv2.method_14631(recipeType);
			}
		}
	}

	private class_3286 method_18139(class_4113 arg) {
		class_3286 lv = new class_3286();
		this.field_19903.add(lv);
		((List)this.field_19902.computeIfAbsent(arg, argx -> Lists.newArrayList())).add(lv);
		if (arg != class_4113.FURNACE_BLOCKS && arg != class_4113.FURNACE_FOOD && arg != class_4113.FURNACE_MISC) {
			((List)this.field_19902.computeIfAbsent(class_4113.SEARCH, argx -> Lists.newArrayList())).add(lv);
		} else {
			((List)this.field_19902.computeIfAbsent(class_4113.FURNACE_SEARCH, argx -> Lists.newArrayList())).add(lv);
		}

		return lv;
	}

	private static class_4113 method_18145(RecipeType recipeType) {
		if (recipeType instanceof class_3584) {
			if (recipeType.getOutput().getItem() instanceof FoodItem) {
				return class_4113.FURNACE_FOOD;
			} else {
				return recipeType.getOutput().getItem() instanceof BlockItem ? class_4113.FURNACE_BLOCKS : class_4113.FURNACE_MISC;
			}
		} else {
			ItemStack itemStack = recipeType.getOutput();
			ItemGroup itemGroup = itemStack.getItem().getItemGroup();
			if (itemGroup == ItemGroup.BUILDING_BLOCKS) {
				return class_4113.BUILDING_BLOCKS;
			} else if (itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.COMBAT) {
				return class_4113.EQUIPMENT;
			} else {
				return itemGroup == ItemGroup.REDSTONE ? class_4113.REDSTONE : class_4113.MISC;
			}
		}
	}

	public static List<class_4113> method_18137(ScreenHandler screenHandler) {
		if (screenHandler instanceof CraftingScreenHandler || screenHandler instanceof PlayerScreenHandler) {
			return Lists.newArrayList(new class_4113[]{class_4113.SEARCH, class_4113.EQUIPMENT, class_4113.BUILDING_BLOCKS, class_4113.MISC, class_4113.REDSTONE});
		} else {
			return screenHandler instanceof FurnaceScreenHandler
				? Lists.newArrayList(new class_4113[]{class_4113.FURNACE_SEARCH, class_4113.FURNACE_FOOD, class_4113.FURNACE_BLOCKS, class_4113.FURNACE_MISC})
				: Lists.newArrayList();
		}
	}

	public List<class_3286> method_18144() {
		return this.field_19903;
	}

	public List<class_3286> method_18138(class_4113 arg) {
		return (List<class_3286>)this.field_19902.getOrDefault(arg, Collections.emptyList());
	}
}
