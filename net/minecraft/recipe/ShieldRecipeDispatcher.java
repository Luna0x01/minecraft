package net.minecraft.recipe;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

public class ShieldRecipeDispatcher {
	public void method_11444(RecipeDispatcher recipeDispatcher) {
		recipeDispatcher.registerShapedRecipe(new ItemStack(Items.SHIELD), "WoW", "WWW", " W ", 'W', Blocks.PLANKS, 'o', Items.IRON_INGOT);
		recipeDispatcher.addRecipeType(new ShieldRecipeDispatcher.DecorationRecipeType());
	}

	static class DecorationRecipeType implements RecipeType {
		private DecorationRecipeType() {
		}

		@Override
		public boolean matches(CraftingInventory inventory, World world) {
			ItemStack itemStack = null;
			ItemStack itemStack2 = null;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack3 = inventory.getInvStack(i);
				if (itemStack3 != null) {
					if (itemStack3.getItem() == Items.BANNER) {
						if (itemStack2 != null) {
							return false;
						}

						itemStack2 = itemStack3;
					} else {
						if (itemStack3.getItem() != Items.SHIELD) {
							return false;
						}

						if (itemStack != null) {
							return false;
						}

						if (itemStack3.getSubNbt("BlockEntityTag", false) != null) {
							return false;
						}

						itemStack = itemStack3;
					}
				}
			}

			return itemStack != null && itemStack2 != null;
		}

		@Nullable
		@Override
		public ItemStack getResult(CraftingInventory inventory) {
			ItemStack itemStack = null;

			for (int i = 0; i < inventory.getInvSize(); i++) {
				ItemStack itemStack2 = inventory.getInvStack(i);
				if (itemStack2 != null && itemStack2.getItem() == Items.BANNER) {
					itemStack = itemStack2;
				}
			}

			ItemStack itemStack3 = new ItemStack(Items.SHIELD, 1, 0);
			DyeColor dyeColor;
			NbtCompound nbtCompound;
			if (itemStack.hasNbt()) {
				nbtCompound = itemStack.getNbt().copy();
				dyeColor = DyeColor.getById(BannerBlockEntity.getBase(itemStack));
			} else {
				nbtCompound = new NbtCompound();
				dyeColor = DyeColor.getById(itemStack.getDamage());
			}

			itemStack3.setNbt(nbtCompound);
			BannerBlockEntity.method_11644(itemStack3, dyeColor);
			return itemStack3;
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
				if (itemStack != null && itemStack.getItem().isFood()) {
					itemStacks[i] = new ItemStack(itemStack.getItem().getRecipeRemainder());
				}
			}

			return itemStacks;
		}
	}
}
