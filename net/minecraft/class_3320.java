package net.minecraft;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.itemgroup.ItemGroup;
import net.minecraft.recipe.RecipeDispatcher;
import net.minecraft.recipe.RecipeType;

public class class_3320 extends class_3355 {
	public static final Map<ItemGroup, List<class_3286>> field_16242 = Maps.newHashMap();
	public static final List<class_3286> field_16243 = Lists.newArrayList();

	private static class_3286 method_14742(ItemGroup itemGroup) {
		class_3286 lv = new class_3286();
		field_16243.add(lv);
		((List)field_16242.computeIfAbsent(itemGroup, itemGroupx -> new ArrayList())).add(lv);
		((List)field_16242.computeIfAbsent(ItemGroup.SEARCH, itemGroupx -> new ArrayList())).add(lv);
		return lv;
	}

	private static ItemGroup method_14743(ItemStack itemStack) {
		ItemGroup itemGroup = itemStack.getItem().getItemGroup();
		if (itemGroup == ItemGroup.BUILDING_BLOCKS || itemGroup == ItemGroup.TOOLS || itemGroup == ItemGroup.REDSTONE) {
			return itemGroup;
		} else {
			return itemGroup == ItemGroup.COMBAT ? ItemGroup.TOOLS : ItemGroup.MISC;
		}
	}

	static {
		Table<ItemGroup, String, class_3286> table = HashBasedTable.create();

		for (RecipeType recipeType : RecipeDispatcher.REGISTRY) {
			if (!recipeType.method_14251()) {
				ItemGroup itemGroup = method_14743(recipeType.getOutput());
				String string = recipeType.method_14253();
				class_3286 lv;
				if (string.isEmpty()) {
					lv = method_14742(itemGroup);
				} else {
					lv = (class_3286)table.get(itemGroup, string);
					if (lv == null) {
						lv = method_14742(itemGroup);
						table.put(itemGroup, string, lv);
					}
				}

				lv.method_14631(recipeType);
			}
		}
	}
}
