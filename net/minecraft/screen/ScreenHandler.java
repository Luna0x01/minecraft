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
	private final Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

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

	@Nullable
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		Slot slot = (Slot)this.slots.get(invSlot);
		return slot != null ? slot.getStack() : null;
	}

	@Nullable
	public ItemStack method_3252(int i, int j, ItemAction itemAction, PlayerEntity playerEntity) {
		ItemStack itemStack = null;
		PlayerInventory playerInventory = playerEntity.inventory;
		if (itemAction == ItemAction.QUICK_CRAFT) {
			int k = this.quickCraftButton;
			this.quickCraftButton = unpackButtonId(j);
			if ((k != 1 || this.quickCraftButton != 2) && k != this.quickCraftButton) {
				this.endQuickCraft();
			} else if (playerInventory.getCursorStack() == null) {
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
					int l = playerInventory.getCursorStack().count;

					for (Slot slot2 : this.quickCraftSlots) {
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, playerInventory.getCursorStack(), true)
							&& slot2.canInsert(playerInventory.getCursorStack())
							&& playerInventory.getCursorStack().count >= this.quickCraftSlots.size()
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack3 = itemStack2.copy();
							int m = slot2.hasStack() ? slot2.getStack().count : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftStage, itemStack3, m);
							if (itemStack3.count > itemStack3.getMaxCount()) {
								itemStack3.count = itemStack3.getMaxCount();
							}

							if (itemStack3.count > slot2.getMaxStackAmount(itemStack3)) {
								itemStack3.count = slot2.getMaxStackAmount(itemStack3);
							}

							l -= itemStack3.count - m;
							slot2.setStack(itemStack3);
						}
					}

					itemStack2.count = l;
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
		} else if ((itemAction == ItemAction.PICKUP || itemAction == ItemAction.QUICK_MOVE) && (j == 0 || j == 1)) {
			if (i == -999) {
				if (playerInventory.getCursorStack() != null) {
					if (j == 0) {
						playerEntity.dropItem(playerInventory.getCursorStack(), true);
						playerInventory.setCursorStack(null);
					}

					if (j == 1) {
						playerEntity.dropItem(playerInventory.getCursorStack().split(1), true);
						if (playerInventory.getCursorStack().count == 0) {
							playerInventory.setCursorStack(null);
						}
					}
				}
			} else if (itemAction == ItemAction.QUICK_MOVE) {
				if (i < 0) {
					return null;
				}

				Slot slot3 = (Slot)this.slots.get(i);
				if (slot3 != null && slot3.canTakeItems(playerEntity)) {
					ItemStack itemStack4 = slot3.getStack();
					if (itemStack4 != null && itemStack4.count <= 0) {
						itemStack = itemStack4.copy();
						slot3.setStack(null);
					}

					ItemStack itemStack5 = this.transferSlot(playerEntity, i);
					if (itemStack5 != null) {
						Item item = itemStack5.getItem();
						itemStack = itemStack5.copy();
						if (slot3.getStack() != null && slot3.getStack().getItem() == item) {
							this.onSlotClick(i, j, true, playerEntity);
						}
					}
				}
			} else {
				if (i < 0) {
					return null;
				}

				Slot slot4 = (Slot)this.slots.get(i);
				if (slot4 != null) {
					ItemStack itemStack6 = slot4.getStack();
					ItemStack itemStack7 = playerInventory.getCursorStack();
					if (itemStack6 != null) {
						itemStack = itemStack6.copy();
					}

					if (itemStack6 == null) {
						if (itemStack7 != null && slot4.canInsert(itemStack7)) {
							int n = j == 0 ? itemStack7.count : 1;
							if (n > slot4.getMaxStackAmount(itemStack7)) {
								n = slot4.getMaxStackAmount(itemStack7);
							}

							slot4.setStack(itemStack7.split(n));
							if (itemStack7.count == 0) {
								playerInventory.setCursorStack(null);
							}
						}
					} else if (slot4.canTakeItems(playerEntity)) {
						if (itemStack7 == null) {
							if (itemStack6.count > 0) {
								int o = j == 0 ? itemStack6.count : (itemStack6.count + 1) / 2;
								playerInventory.setCursorStack(slot4.takeStack(o));
								if (itemStack6.count <= 0) {
									slot4.setStack(null);
								}

								slot4.onTakeItem(playerEntity, playerInventory.getCursorStack());
							} else {
								slot4.setStack(null);
								playerInventory.setCursorStack(null);
							}
						} else if (slot4.canInsert(itemStack7)) {
							if (itemStack6.getItem() == itemStack7.getItem() && itemStack6.getData() == itemStack7.getData() && ItemStack.equalsIgnoreDamage(itemStack6, itemStack7)
								)
							 {
								int p = j == 0 ? itemStack7.count : 1;
								if (p > slot4.getMaxStackAmount(itemStack7) - itemStack6.count) {
									p = slot4.getMaxStackAmount(itemStack7) - itemStack6.count;
								}

								if (p > itemStack7.getMaxCount() - itemStack6.count) {
									p = itemStack7.getMaxCount() - itemStack6.count;
								}

								itemStack7.split(p);
								if (itemStack7.count == 0) {
									playerInventory.setCursorStack(null);
								}

								itemStack6.count += p;
							} else if (itemStack7.count <= slot4.getMaxStackAmount(itemStack7)) {
								slot4.setStack(itemStack7);
								playerInventory.setCursorStack(itemStack6);
							}
						} else if (itemStack6.getItem() == itemStack7.getItem()
							&& itemStack7.getMaxCount() > 1
							&& (!itemStack6.isUnbreakable() || itemStack6.getData() == itemStack7.getData())
							&& ItemStack.equalsIgnoreDamage(itemStack6, itemStack7)) {
							int q = itemStack6.count;
							if (q > 0 && q + itemStack7.count <= itemStack7.getMaxCount()) {
								itemStack7.count += q;
								itemStack6 = slot4.takeStack(q);
								if (itemStack6.count == 0) {
									slot4.setStack(null);
								}

								slot4.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						}
					}

					slot4.markDirty();
				}
			}
		} else if (itemAction == ItemAction.SWAP && j >= 0 && j < 9) {
			Slot slot5 = (Slot)this.slots.get(i);
			ItemStack itemStack8 = playerInventory.getInvStack(j);
			if (itemStack8 != null && itemStack8.count <= 0) {
				itemStack8 = null;
				playerInventory.setInvStack(j, null);
			}

			ItemStack itemStack9 = slot5.getStack();
			if (itemStack8 != null || itemStack9 != null) {
				if (itemStack8 == null) {
					if (slot5.canTakeItems(playerEntity)) {
						playerInventory.setInvStack(j, itemStack9);
						slot5.setStack(null);
						slot5.onTakeItem(playerEntity, itemStack9);
					}
				} else if (itemStack9 == null) {
					if (slot5.canInsert(itemStack8)) {
						int r = slot5.getMaxStackAmount(itemStack8);
						if (itemStack8.count > r) {
							slot5.setStack(itemStack8.split(r));
						} else {
							slot5.setStack(itemStack8);
							playerInventory.setInvStack(j, null);
						}
					}
				} else if (slot5.canTakeItems(playerEntity) && slot5.canInsert(itemStack8)) {
					int s = slot5.getMaxStackAmount(itemStack8);
					if (itemStack8.count > s) {
						slot5.setStack(itemStack8.split(s));
						slot5.onTakeItem(playerEntity, itemStack9);
						if (!playerInventory.insertStack(itemStack9)) {
							playerEntity.dropItem(itemStack9, true);
						}
					} else {
						slot5.setStack(itemStack8);
						playerInventory.setInvStack(j, itemStack9);
						slot5.onTakeItem(playerEntity, itemStack9);
					}
				}
			}
		} else if (itemAction == ItemAction.CLONE && playerEntity.abilities.creativeMode && playerInventory.getCursorStack() == null && i >= 0) {
			Slot slot6 = (Slot)this.slots.get(i);
			if (slot6 != null && slot6.hasStack()) {
				if (slot6.getStack().count > 0) {
					ItemStack itemStack10 = slot6.getStack().copy();
					itemStack10.count = itemStack10.getMaxCount();
					playerInventory.setCursorStack(itemStack10);
				} else {
					slot6.setStack(null);
				}
			}
		} else if (itemAction == ItemAction.THROW && playerInventory.getCursorStack() == null && i >= 0) {
			Slot slot7 = (Slot)this.slots.get(i);
			if (slot7 != null && slot7.hasStack() && slot7.canTakeItems(playerEntity)) {
				ItemStack itemStack11 = slot7.takeStack(j == 0 ? 1 : slot7.getStack().count);
				slot7.onTakeItem(playerEntity, itemStack11);
				playerEntity.dropItem(itemStack11, true);
			}
		} else if (itemAction == ItemAction.PICKUP_ALL && i >= 0) {
			Slot slot8 = (Slot)this.slots.get(i);
			ItemStack itemStack12 = playerInventory.getCursorStack();
			if (itemStack12 != null && (slot8 == null || !slot8.hasStack() || !slot8.canTakeItems(playerEntity))) {
				int t = j == 0 ? 0 : this.slots.size() - 1;
				int u = j == 0 ? 1 : -1;

				for (int v = 0; v < 2; v++) {
					for (int w = t; w >= 0 && w < this.slots.size() && itemStack12.count < itemStack12.getMaxCount(); w += u) {
						Slot slot9 = (Slot)this.slots.get(w);
						if (slot9.hasStack()
							&& canInsertItemIntoSlot(slot9, itemStack12, true)
							&& slot9.canTakeItems(playerEntity)
							&& this.canInsertIntoSlot(itemStack12, slot9)
							&& (v != 0 || slot9.getStack().count != slot9.getStack().getMaxCount())) {
							int x = Math.min(itemStack12.getMaxCount() - itemStack12.count, slot9.getStack().count);
							ItemStack itemStack13 = slot9.takeStack(x);
							itemStack12.count += x;
							if (itemStack13.count <= 0) {
								slot9.setStack(null);
							}

							slot9.onTakeItem(playerEntity, itemStack13);
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
				if (itemStack != null && method_11349(stack, itemStack)) {
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

	private static boolean method_11349(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack2.getItem() == itemStack.getItem()
			&& (!itemStack.isUnbreakable() || itemStack.getData() == itemStack2.getData())
			&& ItemStack.equalsIgnoreDamage(itemStack, itemStack2);
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
