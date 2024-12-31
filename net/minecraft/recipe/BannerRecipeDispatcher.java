package net.minecraft.recipe;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class BannerRecipeDispatcher {
	void registerRecipes(RecipeDispatcher recipes) {
		for (DyeColor dyeColor : DyeColor.values()) {
			recipes.registerShapedRecipe(
				new ItemStack(Items.BANNER, 1, dyeColor.getSwappedId()), "###", "###", " | ", '#', new ItemStack(Blocks.WOOL, 1, dyeColor.getId()), '|', Items.STICK
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
				if (itemStack != null && itemStack.getItem() == Items.BANNER) {
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

		@Nullable
		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			ItemStack itemStack = null;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (itemStack2 != null && itemStack2.getItem() == Items.BANNER) {
					itemStack = itemStack2.copy();
					itemStack.count = 1;
					break;
				}
			}

			BannerBlockEntity.BannerPattern bannerPattern = this.method_8442(inventory);
			if (bannerPattern != null) {
				int j = 0;

				for (int k = 0; k < inventory.getInvSize(); k++) {
					ItemStack itemStack3 = inventory.getInvStack(k);
					if (itemStack3 != null && itemStack3.getItem() == Items.DYE) {
						j = itemStack3.getData();
						break;
					}
				}

				NbtCompound nbtCompound = itemStack.getSubNbt("BlockEntityTag", true);
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

		@Nullable
		@Override
		public ItemStack getOutput() {
			return null;
		}

		@Override
		public ItemStack[] getRemainders(CraftingInventory inventory) {
			ItemStack[] itemStacks = new ItemStack[inventory.getInvSize()];

			for (int i = 0; i < itemStacks.length; i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (itemStack != null && itemStack.getItem().isFood()) {
					itemStacks[i] = new ItemStack(itemStack.getItem().getRecipeRemainder());
				}
			}

			return itemStacks;
		}

		@Nullable
		private BannerBlockEntity.BannerPattern method_8442(CraftingInventory inventory) {
			for (BannerBlockEntity.BannerPattern bannerPattern : BannerBlockEntity.BannerPattern.values()) {
				if (bannerPattern.isCraftable()) {
					boolean bl = true;
					if (bannerPattern.hasIngredient()) {
						boolean bl2 = false;
						boolean bl3 = false;

						for (int k = 0; k < inventory.getInvSize() && bl; k++) {
							ItemStack itemStack = inventory.getInvStack(k);
							if (itemStack != null && itemStack.getItem() != Items.BANNER) {
								if (itemStack.getItem() == Items.DYE) {
									if (bl3) {
										bl = false;
										break;
									}

									bl3 = true;
								} else {
									if (bl2 || !itemStack.equalsIgnoreNbt(bannerPattern.getIngredient())) {
										bl = false;
										break;
									}

									bl2 = true;
								}
							}
						}

						if (!bl2) {
							bl = false;
						}
					} else if (inventory.getInvSize() == bannerPattern.getRecipe().length * bannerPattern.getRecipe()[0].length()) {
						int l = -1;

						for (int m = 0; m < inventory.getInvSize() && bl; m++) {
							int n = m / 3;
							int o = m % 3;
							ItemStack itemStack2 = inventory.getInvStack(m);
							if (itemStack2 != null && itemStack2.getItem() != Items.BANNER) {
								if (itemStack2.getItem() != Items.DYE) {
									bl = false;
									break;
								}

								if (l != -1 && l != itemStack2.getData()) {
									bl = false;
									break;
								}

								if (bannerPattern.getRecipe()[n].charAt(o) == ' ') {
									bl = false;
									break;
								}

								l = itemStack2.getData();
							} else if (bannerPattern.getRecipe()[n].charAt(o) != ' ') {
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
			ItemStack itemStack = null;
			ItemStack itemStack2 = null;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (itemStack3 != null) {
					if (itemStack3.getItem() != Items.BANNER) {
						return false;
					}

					if (itemStack != null && itemStack2 != null) {
						return false;
					}

					int j = BannerBlockEntity.getBase(itemStack3);
					boolean bl = BannerBlockEntity.getPatternCount(itemStack3) > 0;
					if (itemStack != null) {
						if (bl) {
							return false;
						}

						if (j != BannerBlockEntity.getBase(itemStack)) {
							return false;
						}

						itemStack2 = itemStack3;
					} else if (itemStack2 != null) {
						if (!bl) {
							return false;
						}

						if (j != BannerBlockEntity.getBase(itemStack2)) {
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

			return itemStack != null && itemStack2 != null;
		}

		@Nullable
		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (itemStack != null && BannerBlockEntity.getPatternCount(itemStack) > 0) {
					ItemStack itemStack2 = itemStack.copy();
					itemStack2.count = 1;
					return itemStack2;
				}
			}

			return null;
		}

		@Override
		public int getSize() {
			return 2;
		}

		@Nullable
		@Override
		public ItemStack getOutput() {
			return null;
		}

		@Override
		public ItemStack[] getRemainders(CraftingInventory inventory) {
			ItemStack[] itemStacks = new ItemStack[inventory.getInvSize()];

			for (int i = 0; i < itemStacks.length; i++) {
				ItemStack itemStack = inventory.getInvStack(i);
				if (itemStack != null) {
					if (itemStack.getItem().isFood()) {
						itemStacks[i] = new ItemStack(itemStack.getItem().getRecipeRemainder());
					} else if (itemStack.hasNbt() && BannerBlockEntity.getPatternCount(itemStack) > 0) {
						itemStacks[i] = itemStack.copy();
						itemStacks[i].count = 1;
					}
				}
			}

			return itemStacks;
		}
	}
}
