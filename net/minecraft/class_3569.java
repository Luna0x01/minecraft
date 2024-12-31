package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.block.BannerPattern;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class class_3569 extends class_3571 {
	public class_3569(Identifier identifier) {
		super(identifier);
	}

	@Override
	public boolean method_3500(Inventory inventory, World world) {
		if (!(inventory instanceof CraftingInventory)) {
			return false;
		} else {
			boolean bl = false;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (itemStack.getItem() instanceof BannerItem) {
					if (bl) {
						return false;
					}

					if (BannerBlockEntity.getPatternCount(itemStack) >= 6) {
						return false;
					}

					bl = true;
				}
			}

			return bl && this.method_16177(inventory) != null;
		}
	}

	@Override
	public ItemStack method_16201(Inventory inventory) {
		ItemStack itemStack = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getInvSize(); i++) {
			ItemStack itemStack2 = inventory.getInvStack(i);
			if (!itemStack2.isEmpty() && itemStack2.getItem() instanceof BannerItem) {
				itemStack = itemStack2.copy();
				itemStack.setCount(1);
				break;
			}
		}

		BannerPattern bannerPattern = this.method_16177(inventory);
		if (bannerPattern != null) {
			DyeColor dyeColor = DyeColor.WHITE;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				Item item = inventory.getInvStack(j).getItem();
				if (item instanceof DyeItem) {
					dyeColor = ((DyeItem)item).method_16047();
					break;
				}
			}

			NbtCompound nbtCompound = itemStack.getOrCreateNbtCompound("BlockEntityTag");
			NbtList nbtList;
			if (nbtCompound.contains("Patterns", 9)) {
				nbtList = nbtCompound.getList("Patterns", 10);
			} else {
				nbtList = new NbtList();
				nbtCompound.put("Patterns", nbtList);
			}

			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.putString("Pattern", bannerPattern.getId());
			nbtCompound2.putInt("Color", dyeColor.getId());
			nbtList.add((NbtElement)nbtCompound2);
		}

		return itemStack;
	}

	@Nullable
	private BannerPattern method_16177(Inventory inventory) {
		for (BannerPattern bannerPattern : BannerPattern.values()) {
			if (bannerPattern.hasRecipeOrItem()) {
				boolean bl = true;
				if (bannerPattern.hasRecipeItem()) {
					boolean bl2 = false;
					boolean bl3 = false;

					for (int i = 0; i < inventory.getInvSize() && bl; i++) {
						ItemStack itemStack = inventory.getInvStack(i);
						if (!itemStack.isEmpty() && !(itemStack.getItem() instanceof BannerItem)) {
							if (itemStack.getItem() instanceof DyeItem) {
								if (bl3) {
									bl = false;
									break;
								}

								bl3 = true;
							} else {
								if (bl2 || !itemStack.equalsIgnoreNbt(bannerPattern.getRecipeItem())) {
									bl = false;
									break;
								}

								bl2 = true;
							}
						}
					}

					if (!bl2 || !bl3) {
						bl = false;
					}
				} else if (inventory.getInvSize() == bannerPattern.getRecipeString().length * bannerPattern.getRecipeString()[0].length()) {
					DyeColor dyeColor = null;

					for (int j = 0; j < inventory.getInvSize() && bl; j++) {
						int k = j / 3;
						int l = j % 3;
						ItemStack itemStack2 = inventory.getInvStack(j);
						Item item = itemStack2.getItem();
						if (!itemStack2.isEmpty() && !(item instanceof BannerItem)) {
							if (!(item instanceof DyeItem)) {
								bl = false;
								break;
							}

							DyeColor dyeColor2 = ((DyeItem)item).method_16047();
							if (dyeColor != null && dyeColor != dyeColor2) {
								bl = false;
								break;
							}

							if (bannerPattern.getRecipeString()[k].charAt(l) == ' ') {
								bl = false;
								break;
							}

							dyeColor = dyeColor2;
						} else if (bannerPattern.getRecipeString()[k].charAt(l) != ' ') {
							bl = false;
							break;
						}
					}
				} else {
					bl = false;
				}

				if (bl) {
					return bannerPattern;
				}
			}
		}

		return null;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i >= 3 && j >= 3;
	}

	@Override
	public class_3578<?> method_16200() {
		return class_3579.field_17459;
	}
}
