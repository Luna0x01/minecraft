package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FireworkRecipeType implements RecipeType {
	private ItemStack ingredient = ItemStack.EMPTY;

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		this.ingredient = ItemStack.EMPTY;
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int m = 0;
		int n = 0;

		for (int o = 0; o < inventory.getInvSize(); o++) {
			ItemStack itemStack = inventory.getInvStack(o);
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() == Items.GUNPOWDER) {
					j++;
				} else if (itemStack.getItem() == Items.FIREWORK_CHARGE) {
					l++;
				} else if (itemStack.getItem() == Items.DYE) {
					k++;
				} else if (itemStack.getItem() == Items.PAPER) {
					i++;
				} else if (itemStack.getItem() == Items.GLOWSTONE_DUST) {
					m++;
				} else if (itemStack.getItem() == Items.DIAMOND) {
					m++;
				} else if (itemStack.getItem() == Items.FIRE_CHARGE) {
					n++;
				} else if (itemStack.getItem() == Items.FEATHER) {
					n++;
				} else if (itemStack.getItem() == Items.GOLD_NUGGET) {
					n++;
				} else {
					if (itemStack.getItem() != Items.SKULL) {
						return false;
					}

					n++;
				}
			}
		}

		m += k + n;
		if (j > 3 || i > 1) {
			return false;
		} else if (j >= 1 && i == 1 && m == 0) {
			this.ingredient = new ItemStack(Items.FIREWORKS, 3);
			NbtCompound nbtCompound = new NbtCompound();
			if (l > 0) {
				NbtList nbtList = new NbtList();

				for (int p = 0; p < inventory.getInvSize(); p++) {
					ItemStack itemStack2 = inventory.getInvStack(p);
					if (itemStack2.getItem() == Items.FIREWORK_CHARGE && itemStack2.hasNbt() && itemStack2.getNbt().contains("Explosion", 10)) {
						nbtList.add(itemStack2.getNbt().getCompound("Explosion"));
					}
				}

				nbtCompound.put("Explosions", nbtList);
			}

			nbtCompound.putByte("Flight", (byte)j);
			NbtCompound nbtCompound2 = new NbtCompound();
			nbtCompound2.put("Fireworks", nbtCompound);
			this.ingredient.setNbt(nbtCompound2);
			return true;
		} else if (j == 1 && i == 0 && l == 0 && k > 0 && n <= 1) {
			this.ingredient = new ItemStack(Items.FIREWORK_CHARGE);
			NbtCompound nbtCompound3 = new NbtCompound();
			NbtCompound nbtCompound4 = new NbtCompound();
			byte b = 0;
			List<Integer> list = Lists.newArrayList();

			for (int q = 0; q < inventory.getInvSize(); q++) {
				ItemStack itemStack3 = inventory.getInvStack(q);
				if (!itemStack3.isEmpty()) {
					if (itemStack3.getItem() == Items.DYE) {
						list.add(DyeItem.COLORS[itemStack3.getData() & 15]);
					} else if (itemStack3.getItem() == Items.GLOWSTONE_DUST) {
						nbtCompound4.putBoolean("Flicker", true);
					} else if (itemStack3.getItem() == Items.DIAMOND) {
						nbtCompound4.putBoolean("Trail", true);
					} else if (itemStack3.getItem() == Items.FIRE_CHARGE) {
						b = 1;
					} else if (itemStack3.getItem() == Items.FEATHER) {
						b = 4;
					} else if (itemStack3.getItem() == Items.GOLD_NUGGET) {
						b = 2;
					} else if (itemStack3.getItem() == Items.SKULL) {
						b = 3;
					}
				}
			}

			int[] is = new int[list.size()];

			for (int r = 0; r < is.length; r++) {
				is[r] = (Integer)list.get(r);
			}

			nbtCompound4.putIntArray("Colors", is);
			nbtCompound4.putByte("Type", b);
			nbtCompound3.put("Explosion", nbtCompound4);
			this.ingredient.setNbt(nbtCompound3);
			return true;
		} else if (j == 0 && i == 0 && l == 1 && k > 0 && k == m) {
			List<Integer> list2 = Lists.newArrayList();

			for (int s = 0; s < inventory.getInvSize(); s++) {
				ItemStack itemStack4 = inventory.getInvStack(s);
				if (!itemStack4.isEmpty()) {
					if (itemStack4.getItem() == Items.DYE) {
						list2.add(DyeItem.COLORS[itemStack4.getData() & 15]);
					} else if (itemStack4.getItem() == Items.FIREWORK_CHARGE) {
						this.ingredient = itemStack4.copy();
						this.ingredient.setCount(1);
					}
				}
			}

			int[] js = new int[list2.size()];

			for (int t = 0; t < js.length; t++) {
				js[t] = (Integer)list2.get(t);
			}

			if (!this.ingredient.isEmpty() && this.ingredient.hasNbt()) {
				NbtCompound nbtCompound5 = this.ingredient.getNbt().getCompound("Explosion");
				if (nbtCompound5 == null) {
					return false;
				} else {
					nbtCompound5.putIntArray("FadeColors", js);
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public ItemStack getResult(CraftingInventory inventory) {
		return this.ingredient.copy();
	}

	@Override
	public ItemStack getOutput() {
		return this.ingredient;
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

	@Override
	public boolean method_14251() {
		return true;
	}

	@Override
	public boolean method_14250(int i, int j) {
		return i * j >= 1;
	}
}
