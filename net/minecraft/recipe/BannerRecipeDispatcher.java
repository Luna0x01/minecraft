package net.minecraft.recipe;

import javax.annotation.Nullable;
import net.minecraft.block.BannerPattern;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BannerRecipeDispatcher {
	void registerRecipes(RecipeDispatcher recipes) {
		for (DyeColor dyeColor : DyeColor.values()) {
			recipes.registerShapedRecipe(
				BannerItem.method_13645(dyeColor, null), "###", "###", " | ", '#', new ItemStack(Blocks.WOOL, 1, dyeColor.getId()), '|', Items.STICK
			);
		}

		recipes.addRecipeType(new BannerRecipeDispatcher.PatternRecipeType());
		recipes.addRecipeType(new BannerRecipeDispatcher.CopyingRecipeType());
	}

	static class CopyingRecipeType implements RecipeType {
		private CopyingRecipeType() {
		}

		@Override
		public boolean matches(CraftingInventory inventory, World world) {
			boolean bl = false;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (itemStack.getItem() == Items.BANNER) {
					if (bl) {
						return false;
					}

					if (BannerBlockEntity.getPatternCount(itemStack) >= 6) {
						return false;
					}

					bl = true;
				}
			}

			return !bl ? false : this.method_8442(inventory) != null;
		}

		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			ItemStack itemStack = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (!itemStack2.isEmpty() && itemStack2.getItem() == Items.BANNER) {
					itemStack = itemStack2.copy();
					itemStack.setCount(1);
					break;
				}
			}

			BannerPattern bannerPattern = this.method_8442(inventory);
			if (bannerPattern != null) {
				int j = 0;

				for (int k = 0; k < inventory.getInvSize(); k++) {
					ItemStack itemStack3 = inventory.getInvStack(k);
					if (itemStack3.getItem() == Items.DYE) {
						j = itemStack3.getData();
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
				nbtCompound2.putInt("Color", j);
				nbtList.add(nbtCompound2);
			}

			return itemStack;
		}

		@Override
		public int getSize() {
			return 10;
		}

		@Override
		public ItemStack getOutput() {
			return ItemStack.EMPTY;
		}

		@Override
		public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

			for (int i = 0; i < defaultedList.size(); i++) {
				ItemStack itemStack = craftingInventory.getInvStack(i);
				if (itemStack.getItem().isFood()) {
					defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
				}
			}

			return defaultedList;
		}

		@Nullable
		private BannerPattern method_8442(CraftingInventory craftingInventory) {
			for (BannerPattern bannerPattern : BannerPattern.values()) {
				if (bannerPattern.hasRecipeOrItem()) {
					boolean bl = true;
					if (bannerPattern.hasRecipeItem()) {
						boolean bl2 = false;
						boolean bl3 = false;

						for (int i = 0; i < craftingInventory.getInvSize() && bl; i++) {
							ItemStack itemStack = craftingInventory.getInvStack(i);
							if (!itemStack.isEmpty() && itemStack.getItem() != Items.BANNER) {
								if (itemStack.getItem() == Items.DYE) {
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
					} else if (craftingInventory.getInvSize() == bannerPattern.getRecipeString().length * bannerPattern.getRecipeString()[0].length()) {
						int j = -1;

						for (int k = 0; k < craftingInventory.getInvSize() && bl; k++) {
							int l = k / 3;
							int m = k % 3;
							ItemStack itemStack2 = craftingInventory.getInvStack(k);
							if (!itemStack2.isEmpty() && itemStack2.getItem() != Items.BANNER) {
								if (itemStack2.getItem() != Items.DYE) {
									bl = false;
									break;
								}

								if (j != -1 && j != itemStack2.getData()) {
									bl = false;
									break;
								}

								if (bannerPattern.getRecipeString()[l].charAt(m) == ' ') {
									bl = false;
									break;
								}

								j = itemStack2.getData();
							} else if (bannerPattern.getRecipeString()[l].charAt(m) != ' ') {
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
	}

	static class PatternRecipeType implements RecipeType {
		private PatternRecipeType() {
		}

		@Override
		public boolean matches(CraftingInventory inventory, World world) {
			ItemStack itemStack = ItemStack.EMPTY;
			ItemStack itemStack2 = ItemStack.EMPTY;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (!itemStack3.isEmpty()) {
					if (itemStack3.getItem() != Items.BANNER) {
						return false;
					}

					if (!itemStack.isEmpty() && !itemStack2.isEmpty()) {
						return false;
					}

					DyeColor dyeColor = BannerItem.getDyeColor(itemStack3);
					boolean bl = BannerBlockEntity.getPatternCount(itemStack3) > 0;
					if (!itemStack.isEmpty()) {
						if (bl) {
							return false;
						}

						if (dyeColor != BannerItem.getDyeColor(itemStack)) {
							return false;
						}

						itemStack2 = itemStack3;
					} else if (!itemStack2.isEmpty()) {
						if (!bl) {
							return false;
						}

						if (dyeColor != BannerItem.getDyeColor(itemStack2)) {
							return false;
						}

						itemStack = itemStack3;
					} else if (bl) {
						itemStack = itemStack3;
					} else {
						itemStack2 = itemStack3;
					}
				}
			}

			return !itemStack.isEmpty() && !itemStack2.isEmpty();
		}

		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (!itemStack.isEmpty() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(1);
					return itemStack2;
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public int getSize() {
			return 2;
		}

		@Override
		public ItemStack getOutput() {
			return ItemStack.EMPTY;
		}

		@Override
		public DefaultedList<ItemStack> method_13670(CraftingInventory craftingInventory) {
			DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(craftingInventory.getInvSize(), ItemStack.EMPTY);

			for (int i = 0; i < defaultedList.size(); i++) {
				ItemStack itemStack = craftingInventory.getInvStack(i);
				if (!itemStack.isEmpty()) {
					if (itemStack.getItem().isFood()) {
						defaultedList.set(i, new ItemStack(itemStack.getItem().getRecipeRemainder()));
					} else if (itemStack.hasNbt() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
						ItemStack itemStack2 = itemStack.copy();
						itemStack2.setCount(1);
						defaultedList.set(i, itemStack2);
					}
				}
			}

			return defaultedList;
		}
	}
}
