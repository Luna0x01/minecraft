package net.minecraft.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;

public abstract class ScreenHandler {
	public DefaultedList<ItemStack> field_15099 = DefaultedList.of();
	public List<Slot> slots = Lists.newArrayList();
	public int syncId;
	private short actionId;
	private int quickCraftStage = -1;
	private int quickCraftButton;
	private final Set<Slot> quickCraftSlots = Sets.newHashSet();
	protected List<ScreenHandlerListener> listeners = Lists.newArrayList();
	private final Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

	protected Slot addSlot(Slot slot) {
		slot.id = this.slots.size();
		this.slots.add(slot);
		this.field_15099.add(ItemStack.EMPTY);
		return slot;
	}

	public void addListener(ScreenHandlerListener listener) {
		if (this.listeners.contains(listener)) {
			throw new IllegalArgumentException("Listener already listening");
		} else {
			this.listeners.add(listener);
			listener.method_13643(this, this.method_13641());
			this.sendContentUpdates();
		}
	}

	public void removeListener(ScreenHandlerListener listener) {
		this.listeners.remove(listener);
	}

	public DefaultedList<ItemStack> method_13641() {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (int i = 0; i < this.slots.size(); i++) {
			defaultedList.add(((Slot)this.slots.get(i)).getStack());
		}

		return defaultedList;
	}

	public void sendContentUpdates() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = ((Slot)this.slots.get(i)).getStack();
			ItemStack itemStack2 = this.field_15099.get(i);
			if (!ItemStack.equalsAll(itemStack2, itemStack)) {
				itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
				this.field_15099.set(i, itemStack2);

				for (int j = 0; j < this.listeners.size(); j++) {
					((ScreenHandlerListener)this.listeners.get(j)).onScreenHandlerSlotUpdate(this, i, itemStack2);
				}
			}
		}
	}

	public boolean onButtonClick(PlayerEntity player, int id) {
		return false;
	}

	@Nullable
	public Slot getSlot(Inventory inventory, int index) {
		for (int i = 0; i < this.slots.size(); i++) {
			Slot slot = (Slot)this.slots.get(i);
			if (slot.equals(inventory, index)) {
				return slot;
			}
		}

		return null;
	}

	public Slot getSlot(int index) {
		return (Slot)this.slots.get(index);
	}

	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		Slot slot = (Slot)this.slots.get(invSlot);
		return slot != null ? slot.getStack() : ItemStack.EMPTY;
	}

	public ItemStack method_3252(int i, int j, ItemAction itemAction, PlayerEntity playerEntity) {
		ItemStack itemStack = ItemStack.EMPTY;
		PlayerInventory playerInventory = playerEntity.inventory;
		if (itemAction == ItemAction.QUICK_CRAFT) {
			int k = this.quickCraftButton;
			this.quickCraftButton = unpackButtonId(j);
			if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
				this.endQuickCraft();
			} else if (playerInventory.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftButton == 0) {
				this.quickCraftStage = unpackQuickCraftStage(j);
				if (shouldQuickCraftContinue(this.quickCraftStage, playerEntity)) {
					this.quickCraftButton = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftButton == 1) {
				Slot slot = (Slot)this.slots.get(i);
				ItemStack itemStack2 = playerInventory.getCursorStack();
				if (slot != null
					&& canInsertItemIntoSlot(slot, itemStack2, true)
					&& slot.canInsert(itemStack2)
					&& (this.quickCraftStage == 2 || itemStack2.getCount() > this.quickCraftSlots.size())
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftButton == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					ItemStack itemStack3 = playerInventory.getCursorStack().copy();
					int l = playerInventory.getCursorStack().getCount();

					for (Slot slot2 : this.quickCraftSlots) {
						ItemStack itemStack4 = playerInventory.getCursorStack();
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, itemStack4, true)
							&& slot2.canInsert(itemStack4)
							&& (this.quickCraftStage == 2 || itemStack4.getCount() >= this.quickCraftSlots.size())
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack5 = itemStack3.copy();
							int m = slot2.hasStack() ? slot2.getStack().getCount() : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack5, m);
							int n = Math.min(itemStack5.getMaxCount(), slot2.getMaxStackAmount(itemStack5));
							if (itemStack5.getCount() > n) {
								itemStack5.setCount(n);
							}

							l -= itemStack5.getCount() - m;
							slot2.setStack(itemStack5);
						}
					}

					itemStack3.setCount(l);
					playerInventory.setCursorStack(itemStack3);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftButton != 0) {
			this.endQuickCraft();
		} else if ((itemAction == ItemAction.PICKUP || itemAction == ItemAction.QUICK_MOVE) && (j == 0 || j == 1)) {
			if (i == -999) {
				if (!playerInventory.getCursorStack().isEmpty()) {
					if (j == 0) {
						playerEntity.dropItem(playerInventory.getCursorStack(), true);
						playerInventory.setCursorStack(ItemStack.EMPTY);
					}

					if (j == 1) {
						playerEntity.dropItem(playerInventory.getCursorStack().split(1), true);
					}
				}
			} else if (itemAction == ItemAction.QUICK_MOVE) {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot3 = (Slot)this.slots.get(i);
				if (slot3 != null && slot3.canTakeItems(playerEntity)) {
					ItemStack itemStack6 = this.transferSlot(playerEntity, i);
					if (!itemStack6.isEmpty()) {
						Item item = itemStack6.getItem();
						itemStack = itemStack6.copy();
						if (slot3.getStack().getItem() == item) {
							this.onSlotClick(i, j, true, playerEntity);
						}
					}
				}
			} else {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot4 = (Slot)this.slots.get(i);
				if (slot4 != null) {
					ItemStack itemStack7 = slot4.getStack();
					ItemStack itemStack8 = playerInventory.getCursorStack();
					if (!itemStack7.isEmpty()) {
						itemStack = itemStack7.copy();
					}

					if (itemStack7.isEmpty()) {
						if (!itemStack8.isEmpty() && slot4.canInsert(itemStack8)) {
							int o = j == 0 ? itemStack8.getCount() : 1;
							if (o > slot4.getMaxStackAmount(itemStack8)) {
								o = slot4.getMaxStackAmount(itemStack8);
							}

							slot4.setStack(itemStack8.split(o));
						}
					} else if (slot4.canTakeItems(playerEntity)) {
						if (itemStack8.isEmpty()) {
							if (itemStack7.isEmpty()) {
								slot4.setStack(ItemStack.EMPTY);
								playerInventory.setCursorStack(ItemStack.EMPTY);
							} else {
								int p = j == 0 ? itemStack7.getCount() : (itemStack7.getCount() + 1) / 2;
								playerInventory.setCursorStack(slot4.takeStack(p));
								if (itemStack7.isEmpty()) {
									slot4.setStack(ItemStack.EMPTY);
								}

								slot4.method_3298(playerEntity, playerInventory.getCursorStack());
							}
						} else if (slot4.canInsert(itemStack8)) {
							if (itemStack7.getItem() == itemStack8.getItem() && itemStack7.getData() == itemStack8.getData() && ItemStack.equalsIgnoreDamage(itemStack7, itemStack8)
								)
							 {
								int q = j == 0 ? itemStack8.getCount() : 1;
								if (q > slot4.getMaxStackAmount(itemStack8) - itemStack7.getCount()) {
									q = slot4.getMaxStackAmount(itemStack8) - itemStack7.getCount();
								}

								if (q > itemStack8.getMaxCount() - itemStack7.getCount()) {
									q = itemStack8.getMaxCount() - itemStack7.getCount();
								}

								itemStack8.decrement(q);
								itemStack7.increment(q);
							} else if (itemStack8.getCount() <= slot4.getMaxStackAmount(itemStack8)) {
								slot4.setStack(itemStack8);
								playerInventory.setCursorStack(itemStack7);
							}
						} else if (itemStack7.getItem() == itemStack8.getItem()
							&& itemStack8.getMaxCount() > 1
							&& (!itemStack7.isUnbreakable() || itemStack7.getData() == itemStack8.getData())
							&& ItemStack.equalsIgnoreDamage(itemStack7, itemStack8)
							&& !itemStack7.isEmpty()) {
							int r = itemStack7.getCount();
							if (r + itemStack8.getCount() <= itemStack8.getMaxCount()) {
								itemStack8.increment(r);
								itemStack7 = slot4.takeStack(r);
								if (itemStack7.isEmpty()) {
									slot4.setStack(ItemStack.EMPTY);
								}

								slot4.method_3298(playerEntity, playerInventory.getCursorStack());
							}
						}
					}

					slot4.markDirty();
				}
			}
		} else if (itemAction == ItemAction.SWAP && j >= 0 && j < 9) {
			Slot slot5 = (Slot)this.slots.get(i);
			ItemStack itemStack9 = playerInventory.getInvStack(j);
			ItemStack itemStack10 = slot5.getStack();
			if (!itemStack9.isEmpty() || !itemStack10.isEmpty()) {
				if (itemStack9.isEmpty()) {
					if (slot5.canTakeItems(playerEntity)) {
						playerInventory.setInvStack(j, itemStack10);
						slot5.method_13644(itemStack10.getCount());
						slot5.setStack(ItemStack.EMPTY);
						slot5.method_3298(playerEntity, itemStack10);
					}
				} else if (itemStack10.isEmpty()) {
					if (slot5.canInsert(itemStack9)) {
						int s = slot5.getMaxStackAmount(itemStack9);
						if (itemStack9.getCount() > s) {
							slot5.setStack(itemStack9.split(s));
						} else {
							slot5.setStack(itemStack9);
							playerInventory.setInvStack(j, ItemStack.EMPTY);
						}
					}
				} else if (slot5.canTakeItems(playerEntity) && slot5.canInsert(itemStack9)) {
					int t = slot5.getMaxStackAmount(itemStack9);
					if (itemStack9.getCount() > t) {
						slot5.setStack(itemStack9.split(t));
						slot5.method_3298(playerEntity, itemStack10);
						if (!playerInventory.insertStack(itemStack10)) {
							playerEntity.dropItem(itemStack10, true);
						}
					} else {
						slot5.setStack(itemStack9);
						playerInventory.setInvStack(j, itemStack10);
						slot5.method_3298(playerEntity, itemStack10);
					}
				}
			}
		} else if (itemAction == ItemAction.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot6 = (Slot)this.slots.get(i);
			if (slot6 != null && slot6.hasStack()) {
				ItemStack itemStack11 = slot6.getStack().copy();
				itemStack11.setCount(itemStack11.getMaxCount());
				playerInventory.setCursorStack(itemStack11);
			}
		} else if (itemAction == ItemAction.THROW && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot7 = (Slot)this.slots.get(i);
			if (slot7 != null && slot7.hasStack() && slot7.canTakeItems(playerEntity)) {
				ItemStack itemStack12 = slot7.takeStack(j == 0 ? 1 : slot7.getStack().getCount());
				slot7.method_3298(playerEntity, itemStack12);
				playerEntity.dropItem(itemStack12, true);
			}
		} else if (itemAction == ItemAction.PICKUP_ALL && i >= 0) {
			Slot slot8 = (Slot)this.slots.get(i);
			ItemStack itemStack13 = playerInventory.getCursorStack();
			if (!itemStack13.isEmpty() && (slot8 == null || !slot8.hasStack() || !slot8.canTakeItems(playerEntity))) {
				int u = j == 0 ? 0 : this.slots.size() - 1;
				int v = j == 0 ? 1 : -1;

				for (int w = 0; w < 2; w++) {
					for (int x = u; x >= 0 && x < this.slots.size() && itemStack13.getCount() < itemStack13.getMaxCount(); x += v) {
						Slot slot9 = (Slot)this.slots.get(x);
						if (slot9.hasStack() && canInsertItemIntoSlot(slot9, itemStack13, true) && slot9.canTakeItems(playerEntity) && this.canInsertIntoSlot(itemStack13, slot9)
							)
						 {
							ItemStack itemStack14 = slot9.getStack();
							if (w != 0 || itemStack14.getCount() != itemStack14.getMaxCount()) {
								int y = Math.min(itemStack13.getMaxCount() - itemStack13.getCount(), itemStack14.getCount());
								ItemStack itemStack15 = slot9.takeStack(y);
								itemStack13.increment(y);
								if (itemStack15.isEmpty()) {
									slot9.setStack(ItemStack.EMPTY);
								}

								slot9.method_3298(playerEntity, itemStack15);
							}
						}
					}
				}
			}

			this.sendContentUpdates();
		}

		return itemStack;
	}

	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}

	protected void onSlotClick(int slotId, int clickData, boolean bl, PlayerEntity player) {
		this.method_3252(slotId, clickData, ItemAction.QUICK_MOVE, player);
	}

	public void close(PlayerEntity player) {
		PlayerInventory playerInventory = player.inventory;
		if (!playerInventory.getCursorStack().isEmpty()) {
			player.dropItem(playerInventory.getCursorStack(), false);
			playerInventory.setCursorStack(ItemStack.EMPTY);
		}
	}

	public void onContentChanged(Inventory inventory) {
		this.sendContentUpdates();
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		this.getSlot(slot).setStack(stack);
	}

	public void method_13642(List<ItemStack> list) {
		for (int i = 0; i < list.size(); i++) {
			this.getSlot(i).setStack((ItemStack)list.get(i));
		}
	}

	public void setProperty(int id, int value) {
	}

	public short getNextActionId(PlayerInventory playerInventory) {
		this.actionId++;
		return this.actionId;
	}

	public boolean isNotRestricted(PlayerEntity player) {
		return !this.restrictedPlayers.contains(player);
	}

	public void setPlayerRestriction(PlayerEntity player, boolean unrestricted) {
		if (unrestricted) {
			this.restrictedPlayers.remove(player);
		} else {
			this.restrictedPlayers.add(player);
		}
	}

	public abstract boolean canUse(PlayerEntity player);

	protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
		boolean bl = false;
		int i = startIndex;
		if (fromLast) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (!stack.isEmpty() && (fromLast ? i >= startIndex : i < endIndex)) {
				Slot slot = (Slot)this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				if (!itemStack.isEmpty()
					&& itemStack.getItem() == stack.getItem()
					&& (!stack.isUnbreakable() || stack.getData() == itemStack.getData())
					&& ItemStack.equalsIgnoreDamage(stack, itemStack)) {
					int j = itemStack.getCount() + stack.getCount();
					if (j <= stack.getMaxCount()) {
						stack.setCount(0);
						itemStack.setCount(j);
						slot.markDirty();
						bl = true;
					} else if (itemStack.getCount() < stack.getMaxCount()) {
						stack.decrement(stack.getMaxCount() - itemStack.getCount());
						itemStack.setCount(stack.getMaxCount());
						slot.markDirty();
						bl = true;
					}
				}

				if (fromLast) {
					i--;
				} else {
					i++;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (fromLast ? i >= startIndex : i < endIndex) {
				Slot slot2 = (Slot)this.slots.get(i);
				ItemStack itemStack2 = slot2.getStack();
				if (itemStack2.isEmpty() && slot2.canInsert(stack)) {
					if (stack.getCount() > slot2.getMaxStackAmount()) {
						slot2.setStack(stack.split(slot2.getMaxStackAmount()));
					} else {
						slot2.setStack(stack.split(stack.getCount()));
					}

					slot2.markDirty();
					bl = true;
					break;
				}

				if (fromLast) {
					i--;
				} else {
					i++;
				}
			}
		}

		return bl;
	}

	public static int unpackQuickCraftStage(int clickData) {
		return clickData >> 2 & 3;
	}

	public static int unpackButtonId(int clickData) {
		return clickData & 3;
	}

	public static int packClickData(int buttonId, int quickCraftStage) {
		return buttonId & 3 | (quickCraftStage & 3) << 2;
	}

	public static boolean shouldQuickCraftContinue(int i, PlayerEntity player) {
		if (i == 0) {
			return true;
		} else {
			return i == 1 ? true : i == 2 && player.abilities.creativeMode;
		}
	}

	protected void endQuickCraft() {
		this.quickCraftButton = 0;
		this.quickCraftSlots.clear();
	}

	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean bl) {
		boolean bl2 = slot == null || !slot.hasStack();
		return !bl2 && stack.equalsIgnoreNbt(slot.getStack()) && ItemStack.equalsIgnoreDamage(slot.getStack(), stack)
			? slot.getStack().getCount() + (bl ? 0 : stack.getCount()) <= stack.getMaxCount()
			: bl2;
	}

	public static void calculateStackSize(Set<Slot> slots, int mode, ItemStack stack, int stackSize) {
		switch (mode) {
			case 0:
				stack.setCount(MathHelper.floor((float)stack.getCount() / (float)slots.size()));
				break;
			case 1:
				stack.setCount(1);
				break;
			case 2:
				stack.setCount(stack.getItem().getMaxCount());
		}

		stack.increment(stackSize);
	}

	public boolean canInsertIntoSlot(Slot slot) {
		return true;
	}

	public static int calculateComparatorOutput(@Nullable BlockEntity entity) {
		return entity instanceof Inventory ? calculateComparatorOutput((Inventory)entity) : 0;
	}

	public static int calculateComparatorOutput(@Nullable Inventory inventory) {
		if (inventory == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack = inventory.getInvStack(j);
				if (!itemStack.isEmpty()) {
					f += (float)itemStack.getCount() / (float)Math.min(inventory.getInvMaxStackAmount(), itemStack.getMaxCount());
					i++;
				}
			}

			f /= (float)inventory.getInvSize();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
