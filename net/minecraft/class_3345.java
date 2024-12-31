package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CraftRecipeResponseS2CPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3345 {
	private final Logger LOGGER = LogManager.getLogger();
	private final class_3175 field_16358 = new class_3175();
	private ServerPlayerEntity player;
	private RecipeType field_16360;
	private boolean field_16361;
	private CraftingResultInventory field_16362;
	private CraftingInventory field_16363;
	private List<Slot> field_16364;

	public void method_14907(ServerPlayerEntity player, @Nullable RecipeType recipeType, boolean bl) {
		if (recipeType != null && player.method_14965().method_14987(recipeType)) {
			this.player = player;
			this.field_16360 = recipeType;
			this.field_16361 = bl;
			this.field_16364 = player.openScreenHandler.slots;
			ScreenHandler screenHandler = player.openScreenHandler;
			this.field_16362 = null;
			this.field_16363 = null;
			if (screenHandler instanceof CraftingScreenHandler) {
				this.field_16362 = ((CraftingScreenHandler)screenHandler).field_15644;
				this.field_16363 = ((CraftingScreenHandler)screenHandler).craftingInv;
			} else if (screenHandler instanceof PlayerScreenHandler) {
				this.field_16362 = ((PlayerScreenHandler)screenHandler).field_15649;
				this.field_16363 = ((PlayerScreenHandler)screenHandler).craftingInventory;
			}

			if (this.field_16362 != null && this.field_16363 != null) {
				if (this.method_14909() || player.isCreative()) {
					this.field_16358.method_14166();
					player.inventory.method_14148(this.field_16358, false);
					this.field_16363.method_14206(this.field_16358);
					if (this.field_16358.method_14172(recipeType, null)) {
						this.method_14908();
					} else {
						this.method_14903();
						player.networkHandler.sendPacket(new CraftRecipeResponseS2CPacket(player.openScreenHandler.syncId, recipeType));
					}

					player.inventory.markDirty();
				}
			}
		}
	}

	private void method_14903() {
		PlayerInventory playerInventory = this.player.inventory;

		for (int i = 0; i < this.field_16363.getInvSize(); i++) {
			ItemStack itemStack = this.field_16363.getInvStack(i);
			if (!itemStack.isEmpty()) {
				while (itemStack.getCount() > 0) {
					int j = playerInventory.getSlotWithItemStack(itemStack);
					if (j == -1) {
						j = playerInventory.getEmptySlot();
					}

					ItemStack itemStack2 = itemStack.copy();
					itemStack2.setCount(1);
					playerInventory.method_14150(j, itemStack2);
					this.field_16363.takeInvStack(i, 1);
				}
			}
		}

		this.field_16363.clear();
		this.field_16362.clear();
	}

	private void method_14908() {
		boolean bl = this.field_16360.matches(this.field_16363, this.player.world);
		int i = this.field_16358.method_14177(this.field_16360, null);
		if (bl) {
			boolean bl2 = true;

			for (int j = 0; j < this.field_16363.getInvSize(); j++) {
				ItemStack itemStack = this.field_16363.getInvStack(j);
				if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxCount()) > itemStack.getCount()) {
					bl2 = false;
				}
			}

			if (bl2) {
				return;
			}
		}

		int k = this.method_14905(i, bl);
		IntList intList = new IntArrayList();
		if (this.field_16358.method_14173(this.field_16360, intList, k)) {
			int l = k;
			IntListIterator var6 = intList.iterator();

			while (var6.hasNext()) {
				int m = (Integer)var6.next();
				int n = class_3175.method_14174(m).getMaxCount();
				if (n < l) {
					l = n;
				}
			}

			if (this.field_16358.method_14173(this.field_16360, intList, l)) {
				this.method_14903();
				this.method_14904(l, intList);
			}
		}
	}

	private int method_14905(int i, boolean bl) {
		int j = 1;
		if (this.field_16361) {
			j = i;
		} else if (bl) {
			j = 64;

			for (int k = 0; k < this.field_16363.getInvSize(); k++) {
				ItemStack itemStack = this.field_16363.getInvStack(k);
				if (!itemStack.isEmpty() && j > itemStack.getCount()) {
					j = itemStack.getCount();
				}
			}

			if (j < 64) {
				j++;
			}
		}

		return j;
	}

	private void method_14904(int i, IntList intList) {
		int j = this.field_16363.getWidth();
		int k = this.field_16363.getHeight();
		if (this.field_16360 instanceof ShapedRecipeType) {
			ShapedRecipeType shapedRecipeType = (ShapedRecipeType)this.field_16360;
			j = shapedRecipeType.method_14272();
			k = shapedRecipeType.method_14273();
		}

		int l = 1;
		Iterator<Integer> iterator = intList.iterator();

		for (int m = 0; m < this.field_16363.getWidth() && k != m; m++) {
			for (int n = 0; n < this.field_16363.getHeight(); n++) {
				if (j == n || !iterator.hasNext()) {
					l += this.field_16363.getWidth() - n;
					break;
				}

				Slot slot = (Slot)this.field_16364.get(l);
				ItemStack itemStack = class_3175.method_14174((Integer)iterator.next());
				if (itemStack.isEmpty()) {
					l++;
				} else {
					for (int o = 0; o < i; o++) {
						this.method_14906(slot, itemStack);
					}

					l++;
				}
			}

			if (!iterator.hasNext()) {
				break;
			}
		}
	}

	private void method_14906(Slot slot, ItemStack itemStack) {
		PlayerInventory playerInventory = this.player.inventory;
		int i = playerInventory.method_14151(itemStack);
		if (i != -1) {
			ItemStack itemStack2 = playerInventory.getInvStack(i).copy();
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getCount() > 1) {
					playerInventory.takeInvStack(i, 1);
				} else {
					playerInventory.removeInvStack(i);
				}

				itemStack2.setCount(1);
				if (slot.getStack().isEmpty()) {
					slot.setStack(itemStack2);
				} else {
					slot.getStack().increment(1);
				}
			}
		}
	}

	private boolean method_14909() {
		PlayerInventory playerInventory = this.player.inventory;

		for (int i = 0; i < this.field_16363.getInvSize(); i++) {
			ItemStack itemStack = this.field_16363.getInvStack(i);
			if (!itemStack.isEmpty()) {
				int j = playerInventory.getSlotWithItemStack(itemStack);
				if (j == -1) {
					j = playerInventory.getEmptySlot();
				}

				if (j == -1) {
					return false;
				}
			}
		}

		return true;
	}
}
