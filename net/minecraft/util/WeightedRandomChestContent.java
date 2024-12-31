package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.Weighting;

public class WeightedRandomChestContent extends Weighting.Weight {
	private ItemStack content;
	private int min;
	private int max;

	public WeightedRandomChestContent(Item item, int i, int j, int k, int l) {
		super(l);
		this.content = new ItemStack(item, 1, i);
		this.min = j;
		this.max = k;
	}

	public WeightedRandomChestContent(ItemStack itemStack, int i, int j, int k) {
		super(k);
		this.content = itemStack;
		this.min = i;
		this.max = j;
	}

	public static void fillInventory(Random rand, List<WeightedRandomChestContent> lootTable, Inventory inventory, int maxItemsToGenerate) {
		for (int i = 0; i < maxItemsToGenerate; i++) {
			WeightedRandomChestContent weightedRandomChestContent = Weighting.rand(rand, lootTable);
			int j = weightedRandomChestContent.min + rand.nextInt(weightedRandomChestContent.max - weightedRandomChestContent.min + 1);
			if (weightedRandomChestContent.content.getMaxCount() >= j) {
				ItemStack itemStack = weightedRandomChestContent.content.copy();
				itemStack.count = j;
				inventory.setInvStack(rand.nextInt(inventory.getInvSize()), itemStack);
			} else {
				for (int k = 0; k < j; k++) {
					ItemStack itemStack2 = weightedRandomChestContent.content.copy();
					itemStack2.count = 1;
					inventory.setInvStack(rand.nextInt(inventory.getInvSize()), itemStack2);
				}
			}
		}
	}

	public static void fillDispenser(Random rand, List<WeightedRandomChestContent> lootTable, DispenserBlockEntity dispenserBlockEntity, int maxItemsToGenerate) {
		for (int i = 0; i < maxItemsToGenerate; i++) {
			WeightedRandomChestContent weightedRandomChestContent = Weighting.rand(rand, lootTable);
			int j = weightedRandomChestContent.min + rand.nextInt(weightedRandomChestContent.max - weightedRandomChestContent.min + 1);
			if (weightedRandomChestContent.content.getMaxCount() >= j) {
				ItemStack itemStack = weightedRandomChestContent.content.copy();
				itemStack.count = j;
				dispenserBlockEntity.setInvStack(rand.nextInt(dispenserBlockEntity.getInvSize()), itemStack);
			} else {
				for (int k = 0; k < j; k++) {
					ItemStack itemStack2 = weightedRandomChestContent.content.copy();
					itemStack2.count = 1;
					dispenserBlockEntity.setInvStack(rand.nextInt(dispenserBlockEntity.getInvSize()), itemStack2);
				}
			}
		}
	}

	public static List<WeightedRandomChestContent> combineLootTables(List<WeightedRandomChestContent> existingLootTable, WeightedRandomChestContent... additions) {
		List<WeightedRandomChestContent> list = Lists.newArrayList(existingLootTable);
		Collections.addAll(list, additions);
		return list;
	}
}
