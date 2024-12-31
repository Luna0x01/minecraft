package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.World;

public class class_3574 extends class_3571 {
	private static final Ingredient field_17434 = Ingredient.ofItems(
		Items.FIRE_CHARGE,
		Items.FEATHER,
		Items.GOLD_NUGGET,
		Items.SKELETON_SKULL,
		Items.WITHER_SKELETON_SKULL,
		Items.CREEPER_HEAD,
		Items.PLAYER_HEAD,
		Items.DRAGON_HEAD,
		Items.ZOMBIE_HEAD
	);
	private static final Ingredient field_17435 = Ingredient.ofItems(Items.DIAMOND);
	private static final Ingredient field_17436 = Ingredient.ofItems(Items.GLOWSTONE_DUST);
	private static final Map<Item, FireworkItem.class_3551> field_17437 = Util.make(Maps.newHashMap(), hashMap -> {
		hashMap.put(Items.FIRE_CHARGE, FireworkItem.class_3551.LARGE_BALL);
		hashMap.put(Items.FEATHER, FireworkItem.class_3551.BURST);
		hashMap.put(Items.GOLD_NUGGET, FireworkItem.class_3551.STAR);
		hashMap.put(Items.SKELETON_SKULL, FireworkItem.class_3551.CREEPER);
		hashMap.put(Items.WITHER_SKELETON_SKULL, FireworkItem.class_3551.CREEPER);
		hashMap.put(Items.CREEPER_HEAD, FireworkItem.class_3551.CREEPER);
		hashMap.put(Items.PLAYER_HEAD, FireworkItem.class_3551.CREEPER);
		hashMap.put(Items.DRAGON_HEAD, FireworkItem.class_3551.CREEPER);
		hashMap.put(Items.ZOMBIE_HEAD, FireworkItem.class_3551.CREEPER);
	});
	private static final Ingredient field_17438 = Ingredient.ofItems(Items.GUNPOWDER);

	public class_3574(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			boolean bl = false;
			boolean bl2 = false;
			boolean bl3 = false;
			boolean bl4 = false;
			boolean bl5 = false;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					if (field_17434.test(itemStack)) {
						if (bl3) {
							return false;
						}

						bl3 = true;
					} else if (field_17436.test(itemStack)) {
						if (bl5) {
							return false;
						}

						bl5 = true;
					} else if (field_17435.test(itemStack)) {
						if (bl4) {
							return false;
						}

						bl4 = true;
					} else if (field_17438.test(itemStack)) {
						if (bl) {
							return false;
						}

						bl = true;
					} else {
						if (!(itemStack.getItem() instanceof DyeItem)) {
							return false;
						}

						bl2 = true;
					}
				}
			}

			return bl && bl2;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = new ItemStack(Items.FIREWORK_STAR);
		NbtCompound nbtCompound = itemStack.getOrCreateNbtCompound("Explosion");
		FireworkItem.class_3551 lv = FireworkItem.class_3551.SMALL_BALL;
		List<Integer> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (!itemStack2.isEmpty()) {
				if (field_17434.test(itemStack2)) {
					lv = (FireworkItem.class_3551)field_17437.get(itemStack2.getItem());
				} else if (field_17436.test(itemStack2)) {
					nbtCompound.putBoolean("Flicker", true);
				} else if (field_17435.test(itemStack2)) {
					nbtCompound.putBoolean("Trail", true);
				} else if (itemStack2.getItem() instanceof DyeItem) {
					list.add(((DyeItem)itemStack2.getItem()).method_16047().getSwappedId());
				}
			}
		}

		nbtCompound.putIntArray("Colors", list);
		nbtCompound.putByte("Type", (byte)lv.method_16053());
		return itemStack;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 2;
	}

	@Override
	public ItemStack getOutput() {
		return new ItemStack(Items.FIREWORK_STAR);
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17454;
	}
}
