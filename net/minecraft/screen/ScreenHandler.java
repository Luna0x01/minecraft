package net.minecraft.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class ScreenHandler {
	public List<ItemStack> trackedStacks = Lists.newArrayList();
	public List<Slot> slots = Lists.newArrayList();
	public int syncId;
	private short actionId;
	private int quickCraftStage = -1;
	private int quickCraftButton;
	private final Set<Slot> quickCraftSlots = Sets.newHashSet();
	protected List<ScreenHandlerListener> listeners = Lists.newArrayList();
	private Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

	protected Slot addSlot(Slot slot) {
		slot.id = this.slots.size();
		this.slots.add(slot);
		this.trackedStacks.add(null);
		return slot;
	}

	public void addListener(ScreenHandlerListener listener) {
		if (this.listeners.contains(listener)) {
			throw new IllegalArgumentException("Listener already listening");
		} else {
			this.listeners.add(listener);
			listener.updateScreenHandler(this, this.getStacks());
			this.sendContentUpdates();
		}
	}

	public void removeListener(ScreenHandlerListener listener) {
		this.listeners.remove(listener);
	}

	public List<ItemStack> getStacks() {
		List<ItemStack> list = Lists.newArrayList();

		for (int i = 0; i < this.slots.size(); i++) {
			list.add(((Slot)this.slots.get(i)).getStack());
		}

		return list;
	}

	public void sendContentUpdates() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = ((Slot)this.slots.get(i)).getStack();
			ItemStack itemStack2 = (ItemStack)this.trackedStacks.get(i);
			if (!ItemStack.equalsAll(itemStack2, itemStack)) {
				itemStack2 = itemStack == null ? null : itemStack.copy();
				this.trackedStacks.set(i, itemStack2);

				for (int j = 0; j < this.listeners.size(); j++) {
					((ScreenHandlerListener)this.listeners.get(j)).onScreenHandlerSlotUpdate(this, i, itemStack2);
				}
			}
		}
	}

	public boolean onButtonClick(PlayerEntity player, int id) {
		return false;
	}

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
		return slot != null ? slot.getStack() : null;
	}

	public ItemStack onSlotClick(int slotId, int clickData, int actionType, PlayerEntity player) {
		ItemStack itemStack = null;
		PlayerInventory playerInventory = player.inventory;
		if (actionType == 5) {
			int i = this.quickCraftButton;
			this.quickCraftButton = unpackButtonId(clickData);
			if ((i != 1 || this.quickCraftButton != 2) && i != this.quickCraftButton) {
				this.endQuickCraft();
			} else if (playerInventory.getCursorStack() == null) {
				this.endQuickCraft();
			} else if (this.quickCraftButton == 0) {
				this.quickCraftStage = unpackQuickCraftStage(clickData);
				if (shouldQuickCraftContinue(this.quickCraftStage, player)) {
					this.quickCraftButton = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftButton == 1) {
				Slot slot = (Slot)this.slots.get(slotId);
				if (slot != null
					&& canInsertItemIntoSlot(slot, playerInventory.getCursorStack(), true)
					&& slot.canInsert(playerInventory.getCursorStack())
					&& playerInventory.getCursorStack().count > this.quickCraftSlots.size()
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftButton == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					ItemStack itemStack2 = playerInventory.getCursorStack().copy();
					int j = playerInventory.getCursorStack().count;

					for (Slot slot2 : this.quickCraftSlots) {
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, playerInventory.getCursorStack(), true)
							&& slot2.canInsert(playerInventory.getCursorStack())
							&& playerInventory.getCursorStack().count >= this.quickCraftSlots.size()
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack3 = itemStack2.copy();
							int k = slot2.hasStack() ? slot2.getStack().count : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack3, k);
							if (itemStack3.count > itemStack3.getMaxCount()) {
								itemStack3.count = itemStack3.getMaxCount();
							}

							if (itemStack3.count > slot2.getMaxStackAmount(itemStack3)) {
								itemStack3.count = slot2.getMaxStackAmount(itemStack3);
							}

							j -= itemStack3.count - k;
							slot2.setStack(itemStack3);
						}
					}

					itemStack2.count = j;
					if (itemStack2.count <= 0) {
						itemStack2 = null;
					}

					playerInventory.setCursorStack(itemStack2);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftButton != 0) {
			this.endQuickCraft();
		} else if ((actionType == 0 || actionType == 1) && (clickData == 0 || clickData == 1)) {
			if (slotId == -999) {
				if (playerInventory.getCursorStack() != null) {
					if (clickData == 0) {
						player.dropItem(playerInventory.getCursorStack(), true);
						playerInventory.setCursorStack(null);
					}

					if (clickData == 1) {
						player.dropItem(playerInventory.getCursorStack().split(1), true);
						if (playerInventory.getCursorStack().count == 0) {
							playerInventory.setCursorStack(null);
						}
					}
				}
			} else if (actionType == 1) {
				if (slotId < 0) {
					return null;
				}

				Slot slot3 = (Slot)this.slots.get(slotId);
				if (slot3 != null && slot3.canTakeItems(player)) {
					ItemStack itemStack4 = this.transferSlot(player, slotId);
					if (itemStack4 != null) {
						Item item = itemStack4.getItem();
						itemStack = itemStack4.copy();
						if (slot3.getStack() != null && slot3.getStack().getItem() == item) {
							this.onSlotClick(slotId, clickData, true, player);
						}
					}
				}
			} else {
				if (slotId < 0) {
					return null;
				}

				Slot slot4 = (Slot)this.slots.get(slotId);
				if (slot4 != null) {
					ItemStack itemStack5 = slot4.getStack();
					ItemStack itemStack6 = playerInventory.getCursorStack();
					if (itemStack5 != null) {
						itemStack = itemStack5.copy();
					}

					if (itemStack5 == null) {
						if (itemStack6 != null && slot4.canInsert(itemStack6)) {
							int l = clickData == 0 ? itemStack6.count : 1;
							if (l > slot4.getMaxStackAmount(itemStack6)) {
								l = slot4.getMaxStackAmount(itemStack6);
							}

							if (itemStack6.count >= l) {
								slot4.setStack(itemStack6.split(l));
							}

							if (itemStack6.count == 0) {
								playerInventory.setCursorStack(null);
							}
						}
					} else if (slot4.canTakeItems(player)) {
						if (itemStack6 == null) {
							int m = clickData == 0 ? itemStack5.count : (itemStack5.count + 1) / 2;
							ItemStack itemStack7 = slot4.takeStack(m);
							playerInventory.setCursorStack(itemStack7);
							if (itemStack5.count == 0) {
								slot4.setStack(null);
							}

							slot4.onTakeItem(player, playerInventory.getCursorStack());
						} else if (slot4.canInsert(itemStack6)) {
							if (itemStack5.getItem() == itemStack6.getItem() && itemStack5.getData() == itemStack6.getData() && ItemStack.equalsIgnoreDamage(itemStack5, itemStack6)
								)
							 {
								int n = clickData == 0 ? itemStack6.count : 1;
								if (n > slot4.getMaxStackAmount(itemStack6) - itemStack5.count) {
									n = slot4.getMaxStackAmount(itemStack6) - itemStack5.count;
								}

								if (n > itemStack6.getMaxCount() - itemStack5.count) {
									n = itemStack6.getMaxCount() - itemStack5.count;
								}

								itemStack6.split(n);
								if (itemStack6.count == 0) {
									playerInventory.setCursorStack(null);
								}

								itemStack5.count += n;
							} else if (itemStack6.count <= slot4.getMaxStackAmount(itemStack6)) {
								slot4.setStack(itemStack6);
								playerInventory.setCursorStack(itemStack5);
							}
						} else if (itemStack5.getItem() == itemStack6.getItem()
							&& itemStack6.getMaxCount() > 1
							&& (!itemStack5.isUnbreakable() || itemStack5.getData() == itemStack6.getData())
							&& ItemStack.equalsIgnoreDamage(itemStack5, itemStack6)) {
							int o = itemStack5.count;
							if (o > 0 && o + itemStack6.count <= itemStack6.getMaxCount()) {
								itemStack6.count += o;
								itemStack5 = slot4.takeStack(o);
								if (itemStack5.count == 0) {
									slot4.setStack(null);
								}

								slot4.onTakeItem(player, playerInventory.getCursorStack());
							}
						}
					}

					slot4.markDirty();
				}
			}
		} else if (actionType == 2 && clickData >= 0 && clickData < 9) {
			Slot slot5 = (Slot)this.slots.get(slotId);
			if (slot5.canTakeItems(player)) {
				ItemStack itemStack8 = playerInventory.getInvStack(clickData);
				boolean bl = itemStack8 == null || slot5.inventory == playerInventory && slot5.canInsert(itemStack8);
				int p = -1;
				if (!bl) {
					p = playerInventory.getEmptySlot();
					bl |= p > -1;
				}

				if (slot5.hasStack() && bl) {
					ItemStack itemStack9 = slot5.getStack();
					playerInventory.setInvStack(clickData, itemStack9.copy());
					if ((slot5.inventory != playerInventory || !slot5.canInsert(itemStack8)) && itemStack8 != null) {
						if (p > -1) {
							playerInventory.insertStack(itemStack8);
							slot5.takeStack(itemStack9.count);
							slot5.setStack(null);
							slot5.onTakeItem(player, itemStack9);
						}
					} else {
						slot5.takeStack(itemStack9.count);
						slot5.setStack(itemStack8);
						slot5.onTakeItem(player, itemStack9);
					}
				} else if (!slot5.hasStack() && itemStack8 != null && slot5.canInsert(itemStack8)) {
					playerInventory.setInvStack(clickData, null);
					slot5.setStack(itemStack8);
				}
			}
		} else if (actionType == 3 && player.abilities.creativeMode && playerInventory.getCursorStack() == null && slotId >= 0) {
			Slot slot6 = (Slot)this.slots.get(slotId);
			if (slot6 != null && slot6.hasStack()) {
				ItemStack itemStack10 = slot6.getStack().copy();
				itemStack10.count = itemStack10.getMaxCount();
				playerInventory.setCursorStack(itemStack10);
			}
		} else if (actionType == 4 && playerInventory.getCursorStack() == null && slotId >= 0) {
			Slot slot7 = (Slot)this.slots.get(slotId);
			if (slot7 != null && slot7.hasStack() && slot7.canTakeItems(player)) {
				ItemStack itemStack11 = slot7.takeStack(clickData == 0 ? 1 : slot7.getStack().count);
				slot7.onTakeItem(player, itemStack11);
				player.dropItem(itemStack11, true);
			}
		} else if (actionType == 6 && slotId >= 0) {
			Slot slot8 = (Slot)this.slots.get(slotId);
			ItemStack itemStack12 = playerInventory.getCursorStack();
			if (itemStack12 != null && (slot8 == null || !slot8.hasStack() || !slot8.canTakeItems(player))) {
				int q = clickData == 0 ? 0 : this.slots.size() - 1;
				int r = clickData == 0 ? 1 : -1;

				for (int s = 0; s < 2; s++) {
					for (int t = q; t >= 0 && t < this.slots.size() && itemStack12.count < itemStack12.getMaxCount(); t += r) {
						Slot slot9 = (Slot)this.slots.get(t);
						if (slot9.hasStack()
							&& canInsertItemIntoSlot(slot9, itemStack12, true)
							&& slot9.canTakeItems(player)
							&& this.canInsertIntoSlot(itemStack12, slot9)
							&& (s != 0 || slot9.getStack().count != slot9.getStack().getMaxCount())) {
							int u = Math.min(itemStack12.getMaxCount() - itemStack12.count, slot9.getStack().count);
							ItemStack itemStack13 = slot9.takeStack(u);
							itemStack12.count += u;
							if (itemStack13.count <= 0) {
								slot9.setStack(null);
							}

							slot9.onTakeItem(player, itemStack13);
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
		this.onSlotClick(slotId, clickData, 1, player);
	}

	public void close(PlayerEntity player) {
		PlayerInventory playerInventory = player.inventory;
		if (playerInventory.getCursorStack() != null) {
			player.dropItem(playerInventory.getCursorStack(), false);
			playerInventory.setCursorStack(null);
		}
	}

	public void onContentChanged(Inventory inventory) {
		this.sendContentUpdates();
	}

	public void setStackInSlot(int slot, ItemStack stack) {
		this.getSlot(slot).setStack(stack);
	}

	public void updateSlotStacks(ItemStack[] stacks) {
		for (int i = 0; i < stacks.length; i++) {
			this.getSlot(i).setStack(stacks[i]);
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
			while (stack.count > 0 && (!fromLast && i < endIndex || fromLast && i >= startIndex)) {
				Slot slot = (Slot)this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				if (itemStack != null
					&& itemStack.getItem() == stack.getItem()
					&& (!stack.isUnbreakable() || stack.getData() == itemStack.getData())
					&& ItemStack.equalsIgnoreDamage(stack, itemStack)) {
					int j = itemStack.count + stack.count;
					if (j <= stack.getMaxCount()) {
						stack.count = 0;
						itemStack.count = j;
						slot.markDirty();
						bl = true;
					} else if (itemStack.count < stack.getMaxCount()) {
						stack.count = stack.count - (stack.getMaxCount() - itemStack.count);
						itemStack.count = stack.getMaxCount();
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

		if (stack.count > 0) {
			if (fromLast) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (!fromLast && i < endIndex || fromLast && i >= startIndex) {
				Slot slot2 = (Slot)this.slots.get(i);
				ItemStack itemStack2 = slot2.getStack();
				if (itemStack2 == null) {
					slot2.setStack(stack.copy());
					slot2.markDirty();
					stack.count = 0;
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

	public static boolean canInsertItemIntoSlot(Slot slot, ItemStack stack, boolean bl) {
		boolean bl2 = slot == null || !slot.hasStack();
		if (slot != null && slot.hasStack() && stack != null && stack.equalsIgnoreNbt(slot.getStack()) && ItemStack.equalsIgnoreDamage(slot.getStack(), stack)) {
			int var10002 = bl ? 0 : stack.count;
			bl2 |= slot.getStack().count + var10002 <= stack.getMaxCount();
		}

		return bl2;
	}

	public static void calculateStackSize(Set<Slot> slots, int mode, ItemStack stack, int stackSize) {
		switch (mode) {
			case 0:
				stack.count = MathHelper.floor((float)stack.count / (float)slots.size());
				break;
			case 1:
				stack.count = 1;
				break;
			case 2:
				stack.count = stack.getItem().getMaxCount();
		}

		stack.count += stackSize;
	}

	public boolean canInsertIntoSlot(Slot slot) {
		return true;
	}

	public static int calculateComparatorOutput(BlockEntity entity) {
		return entity instanceof Inventory ? calculateComparatorOutput((Inventory)entity) : 0;
	}

	public static int calculateComparatorOutput(Inventory inventory) {
		if (inventory == null) {
			return 0;
		} else {
			int i = 0;
			float f = 0.0F;

			for (int j = 0; j < inventory.getInvSize(); j++) {
				ItemStack itemStack = inventory.getInvStack(j);
				if (itemStack != null) {
					f += (float)itemStack.count / (float)Math.min(inventory.getInvMaxStackAmount(), itemStack.getMaxCount());
					i++;
				}
			}

			f /= (float)inventory.getInvSize();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}
}
