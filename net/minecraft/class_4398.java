package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.CraftRecipeResponseS2CPacket;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_4398 implements class_4397<Integer> {
	protected static final Logger field_21652 = LogManager.getLogger();
	protected final class_3175 field_21653 = new class_3175();
	protected PlayerInventory field_21654;
	protected class_3536 field_21655;

	public void method_20435(ServerPlayerEntity serverPlayerEntity, @Nullable RecipeType recipeType, boolean bl) {
		if (recipeType != null && serverPlayerEntity.method_14965().method_21399(recipeType)) {
			this.field_21654 = serverPlayerEntity.inventory;
			this.field_21655 = (class_3536)serverPlayerEntity.openScreenHandler;
			if (this.method_20437() || serverPlayerEntity.isCreative()) {
				this.field_21653.method_14166();
				serverPlayerEntity.inventory.method_15921(this.field_21653);
				this.field_21655.method_15978(this.field_21653);
				if (this.field_21653.method_14172(recipeType, null)) {
					this.method_20434(recipeType, bl);
				} else {
					this.method_20431();
					serverPlayerEntity.networkHandler.sendPacket(new CraftRecipeResponseS2CPacket(serverPlayerEntity.openScreenHandler.syncId, recipeType));
				}

				serverPlayerEntity.inventory.markDirty();
			}
		}
	}

	protected void method_20431() {
		for (int i = 0; i < this.field_21655.method_15982() * this.field_21655.method_15983() + 1; i++) {
			if (i != this.field_21655.method_15981() || !(this.field_21655 instanceof CraftingScreenHandler) && !(this.field_21655 instanceof PlayerScreenHandler)) {
				this.method_20432(i);
			}
		}

		this.field_21655.method_15980();
	}

	protected void method_20432(int i) {
		ItemStack itemStack = this.field_21655.getSlot(i).getStack();
		if (!itemStack.isEmpty()) {
			for (; itemStack.getCount() > 0; this.field_21655.getSlot(i).takeStack(1)) {
				int j = this.field_21654.getSlotWithItemStack(itemStack);
				if (j == -1) {
					j = this.field_21654.getEmptySlot();
				}

				ItemStack itemStack2 = itemStack.copy();
				itemStack2.setCount(1);
				if (!this.field_21654.method_14150(j, itemStack2)) {
					field_21652.error("Can't find any space for item in the inventory");
				}
			}
		}
	}

	protected void method_20434(RecipeType recipeType, boolean bl) {
		boolean bl2 = this.field_21655.method_15979(recipeType);
		int i = this.field_21653.method_14177(recipeType, null);
		if (bl2) {
			for (int j = 0; j < this.field_21655.method_15983() * this.field_21655.method_15982() + 1; j++) {
				if (j != this.field_21655.method_15981()) {
					ItemStack itemStack = this.field_21655.getSlot(j).getStack();
					if (!itemStack.isEmpty() && Math.min(i, itemStack.getMaxCount()) < itemStack.getCount() + 1) {
						return;
					}
				}
			}
		}

		int k = this.method_20436(bl, i, bl2);
		IntList intList = new IntArrayList();
		if (this.field_21653.method_14173(recipeType, intList, k)) {
			int l = k;
			IntListIterator var8 = intList.iterator();

			while (var8.hasNext()) {
				int m = (Integer)var8.next();
				int n = class_3175.method_14174(m).getMaxCount();
				if (n < l) {
					l = n;
				}
			}

			if (this.field_21653.method_14173(recipeType, intList, l)) {
				this.method_20431();
				this.method_20429(this.field_21655.method_15982(), this.field_21655.method_15983(), this.field_21655.method_15981(), recipeType, intList.iterator(), l);
			}
		}
	}

	@Override
	public void method_20430(Iterator<Integer> iterator, int i, int j, int k, int l) {
		Slot slot = this.field_21655.getSlot(i);
		ItemStack itemStack = class_3175.method_14174((Integer)iterator.next());
		if (!itemStack.isEmpty()) {
			for (int m = 0; m < j; m++) {
				this.method_20433(slot, itemStack);
			}
		}
	}

	protected int method_20436(boolean bl, int i, boolean bl2) {
		int j = 1;
		if (bl) {
			j = i;
		} else if (bl2) {
			j = 64;

			for (int k = 0; k < this.field_21655.method_15982() * this.field_21655.method_15983() + 1; k++) {
				if (k != this.field_21655.method_15981()) {
					ItemStack itemStack = this.field_21655.getSlot(k).getStack();
					if (!itemStack.isEmpty() && j > itemStack.getCount()) {
						j = itemStack.getCount();
					}
				}
			}

			if (j < 64) {
				j++;
			}
		}

		return j;
	}

	protected void method_20433(Slot slot, ItemStack itemStack) {
		int i = this.field_21654.method_14151(itemStack);
		if (i != -1) {
			ItemStack itemStack2 = this.field_21654.getInvStack(i).copy();
			if (!itemStack2.isEmpty()) {
				if (itemStack2.getCount() > 1) {
					this.field_21654.takeInvStack(i, 1);
				} else {
					this.field_21654.removeInvStack(i);
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

	private boolean method_20437() {
		List<ItemStack> list = Lists.newArrayList();
		int i = this.method_20438();

		for (int j = 0; j < this.field_21655.method_15982() * this.field_21655.method_15983() + 1; j++) {
			if (j != this.field_21655.method_15981()) {
				ItemStack itemStack = this.field_21655.getSlot(j).getStack().copy();
				if (!itemStack.isEmpty()) {
					int k = this.field_21654.getSlotWithItemStack(itemStack);
					if (k == -1 && list.size() <= i) {
						for (ItemStack itemStack2 : list) {
							if (itemStack2.equalsIgnoreNbt(itemStack)
								&& itemStack2.getCount() != itemStack2.getMaxCount()
								&& itemStack2.getCount() + itemStack.getCount() <= itemStack2.getMaxCount()) {
								itemStack2.increment(itemStack.getCount());
								itemStack.setCount(0);
								break;
							}
						}

						if (!itemStack.isEmpty()) {
							if (list.size() >= i) {
								return false;
							}

							list.add(itemStack);
						}
					} else if (k == -1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private int method_20438() {
		int i = 0;

		for (ItemStack itemStack : this.field_21654.field_15082) {
			if (itemStack.isEmpty()) {
				i++;
			}
		}

		return i;
	}
}
