package net.minecraft.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class Container {
	private final DefaultedList<ItemStack> stackList = DefaultedList.of();
	public final List<Slot> slotList = Lists.newArrayList();
	private final List<Property> properties = Lists.newArrayList();
	@Nullable
	private final ContainerType<?> type;
	public final int syncId;
	private short actionId;
	private int quickCraftStage = -1;
	private int quickCraftButton;
	private final Set<Slot> quickCraftSlots = Sets.newHashSet();
	private final List<ContainerListener> listeners = Lists.newArrayList();
	private final Set<PlayerEntity> restrictedPlayers = Sets.newHashSet();

	protected Container(@Nullable ContainerType<?> containerType, int i) {
		this.type = containerType;
		this.syncId = i;
	}

	protected static boolean canUse(BlockContext blockContext, PlayerEntity playerEntity, Block block) {
		return blockContext.run(
			(world, blockPos) -> world.getBlockState(blockPos).getBlock() != block
					? false
					: playerEntity.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= 64.0,
			true
		);
	}

	public ContainerType<?> getType() {
		if (this.type == null) {
			throw new UnsupportedOperationException("Unable to construct this menu by type");
		} else {
			return this.type;
		}
	}

	protected static void checkContainerSize(Inventory inventory, int i) {
		int j = inventory.getInvSize();
		if (j < i) {
			throw new IllegalArgumentException("Container size " + j + " is smaller than expected " + i);
		}
	}

	protected static void checkContainerDataCount(PropertyDelegate propertyDelegate, int i) {
		int j = propertyDelegate.size();
		if (j < i) {
			throw new IllegalArgumentException("Container data count " + j + " is smaller than expected " + i);
		}
	}

	protected Slot addSlot(Slot slot) {
		slot.id = this.slotList.size();
		this.slotList.add(slot);
		this.stackList.add(ItemStack.EMPTY);
		return slot;
	}

	protected Property addProperty(Property property) {
		this.properties.add(property);
		return property;
	}

	protected void addProperties(PropertyDelegate propertyDelegate) {
		for (int i = 0; i < propertyDelegate.size(); i++) {
			this.addProperty(Property.create(propertyDelegate, i));
		}
	}

	public void addListener(ContainerListener containerListener) {
		if (!this.listeners.contains(containerListener)) {
			this.listeners.add(containerListener);
			containerListener.onContainerRegistered(this, this.getStacks());
			this.sendContentUpdates();
		}
	}

	public void removeListener(ContainerListener containerListener) {
		this.listeners.remove(containerListener);
	}

	public DefaultedList<ItemStack> getStacks() {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (int i = 0; i < this.slotList.size(); i++) {
			defaultedList.add(((Slot)this.slotList.get(i)).getStack());
		}

		return defaultedList;
	}

	public void sendContentUpdates() {
		for (int i = 0; i < this.slotList.size(); i++) {
			ItemStack itemStack = ((Slot)this.slotList.get(i)).getStack();
			ItemStack itemStack2 = this.stackList.get(i);
			if (!ItemStack.areEqualIgnoreDamage(itemStack2, itemStack)) {
				itemStack2 = itemStack.isEmpty() ? ItemStack.EMPTY : itemStack.copy();
				this.stackList.set(i, itemStack2);

				for (ContainerListener containerListener : this.listeners) {
					containerListener.onContainerSlotUpdate(this, i, itemStack2);
				}
			}
		}

		for (int j = 0; j < this.properties.size(); j++) {
			Property property = (Property)this.properties.get(j);
			if (property.detectChanges()) {
				for (ContainerListener containerListener2 : this.listeners) {
					containerListener2.onContainerPropertyUpdate(this, j, property.get());
				}
			}
		}
	}

	public boolean onButtonClick(PlayerEntity playerEntity, int i) {
		return false;
	}

	public Slot getSlot(int i) {
		return (Slot)this.slotList.get(i);
	}

	public ItemStack transferSlot(PlayerEntity playerEntity, int i) {
		Slot slot = (Slot)this.slotList.get(i);
		return slot != null ? slot.getStack() : ItemStack.EMPTY;
	}

	public ItemStack onSlotClick(int i, int j, SlotActionType slotActionType, PlayerEntity playerEntity) {
		ItemStack itemStack = ItemStack.EMPTY;
		PlayerInventory playerInventory = playerEntity.inventory;
		if (slotActionType == SlotActionType.field_7789) {
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
				Slot slot = (Slot)this.slotList.get(i);
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
		} else if ((slotActionType == SlotActionType.field_7790 || slotActionType == SlotActionType.field_7794) && (j == 0 || j == 1)) {
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
			} else if (slotActionType == SlotActionType.field_7794) {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot3 = (Slot)this.slotList.get(i);
				if (slot3 == null || !slot3.canTakeItems(playerEntity)) {
					return ItemStack.EMPTY;
				}

				for (ItemStack itemStack6 = this.transferSlot(playerEntity, i);
					!itemStack6.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot3.getStack(), itemStack6);
					itemStack6 = this.transferSlot(playerEntity, i)
				) {
					itemStack = itemStack6.copy();
				}
			} else {
				if (i < 0) {
					return ItemStack.EMPTY;
				}

				Slot slot4 = (Slot)this.slotList.get(i);
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

								slot4.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						} else if (slot4.canInsert(itemStack8)) {
							if (canStacksCombine(itemStack7, itemStack8)) {
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
						} else if (itemStack8.getMaxCount() > 1 && canStacksCombine(itemStack7, itemStack8) && !itemStack7.isEmpty()) {
							int r = itemStack7.getCount();
							if (r + itemStack8.getCount() <= itemStack8.getMaxCount()) {
								itemStack8.increment(r);
								itemStack7 = slot4.takeStack(r);
								if (itemStack7.isEmpty()) {
									slot4.setStack(ItemStack.EMPTY);
								}

								slot4.onTakeItem(playerEntity, playerInventory.getCursorStack());
							}
						}
					}

					slot4.markDirty();
				}
			}
		} else if (slotActionType == SlotActionType.field_7791 && j >= 0 && j < 9) {
			Slot slot5 = (Slot)this.slotList.get(i);
			ItemStack itemStack9 = playerInventory.getInvStack(j);
			ItemStack itemStack10 = slot5.getStack();
			if (!itemStack9.isEmpty() || !itemStack10.isEmpty()) {
				if (itemStack9.isEmpty()) {
					if (slot5.canTakeItems(playerEntity)) {
						playerInventory.setInvStack(j, itemStack10);
						slot5.onTake(itemStack10.getCount());
						slot5.setStack(ItemStack.EMPTY);
						slot5.onTakeItem(playerEntity, itemStack10);
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
						slot5.onTakeItem(playerEntity, itemStack10);
						if (!playerInventory.insertStack(itemStack10)) {
							playerEntity.dropItem(itemStack10, true);
						}
					} else {
						slot5.setStack(itemStack9);
						playerInventory.setInvStack(j, itemStack10);
						slot5.onTakeItem(playerEntity, itemStack10);
					}
				}
			}
		} else if (slotActionType == SlotActionType.field_7796 && playerEntity.abilities.creativeMode && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot6 = (Slot)this.slotList.get(i);
			if (slot6 != null && slot6.hasStack()) {
				ItemStack itemStack11 = slot6.getStack().copy();
				itemStack11.setCount(itemStack11.getMaxCount());
				playerInventory.setCursorStack(itemStack11);
			}
		} else if (slotActionType == SlotActionType.field_7795 && playerInventory.getCursorStack().isEmpty() && i >= 0) {
			Slot slot7 = (Slot)this.slotList.get(i);
			if (slot7 != null && slot7.hasStack() && slot7.canTakeItems(playerEntity)) {
				ItemStack itemStack12 = slot7.takeStack(j == 0 ? 1 : slot7.getStack().getCount());
				slot7.onTakeItem(playerEntity, itemStack12);
				playerEntity.dropItem(itemStack12, true);
			}
		} else if (slotActionType == SlotActionType.field_7793 && i >= 0) {
			Slot slot8 = (Slot)this.slotList.get(i);
			ItemStack itemStack13 = playerInventory.getCursorStack();
			if (!itemStack13.isEmpty() && (slot8 == null || !slot8.hasStack() || !slot8.canTakeItems(playerEntity))) {
				int u = j == 0 ? 0 : this.slotList.size() - 1;
				int v = j == 0 ? 1 : -1;

				for (int w = 0; w < 2; w++) {
					for (int x = u; x >= 0 && x < this.slotList.size() && itemStack13.getCount() < itemStack13.getMaxCount(); x += v) {
						Slot slot9 = (Slot)this.slotList.get(x);
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

								slot9.onTakeItem(playerEntity, itemStack15);
							}
						}
					}
				}
			}

			this.sendContentUpdates();
		}

		return itemStack;
	}

	public static boolean canStacksCombine(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.areTagsEqual(itemStack, itemStack2);
	}

	public boolean canInsertIntoSlot(ItemStack itemStack, Slot slot) {
		return true;
	}

	public void close(PlayerEntity playerEntity) {
		PlayerInventory playerInventory = playerEntity.inventory;
		if (!playerInventory.getCursorStack().isEmpty()) {
			playerEntity.dropItem(playerInventory.getCursorStack(), false);
			playerInventory.setCursorStack(ItemStack.EMPTY);
		}
	}

	protected void dropInventory(PlayerEntity playerEntity, World world, Inventory inventory) {
		if (!playerEntity.isAlive() || playerEntity instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerEntity).method_14239()) {
			for (int i = 0; i < inventory.getInvSize(); i++) {
				playerEntity.dropItem(inventory.removeInvStack(i), false);
			}
		} else {
			for (int j = 0; j < inventory.getInvSize(); j++) {
				playerEntity.inventory.offerOrDrop(world, inventory.removeInvStack(j));
			}
		}
	}

	public void onContentChanged(Inventory inventory) {
		this.sendContentUpdates();
	}

	public void setStackInSlot(int i, ItemStack itemStack) {
		this.getSlot(i).setStack(itemStack);
	}

	public void updateSlotStacks(List<ItemStack> list) {
		for (int i = 0; i < list.size(); i++) {
			this.getSlot(i).setStack((ItemStack)list.get(i));
		}
	}

	public void setProperties(int i, int j) {
		((Property)this.properties.get(i)).set(j);
	}

	public short getNextActionId(PlayerInventory playerInventory) {
		this.actionId++;
		return this.actionId;
	}

	public boolean isRestricted(PlayerEntity playerEntity) {
		return !this.restrictedPlayers.contains(playerEntity);
	}

	public void setPlayerRestriction(PlayerEntity playerEntity, boolean bl) {
		if (bl) {
			this.restrictedPlayers.remove(playerEntity);
		} else {
			this.restrictedPlayers.add(playerEntity);
		}
	}

	public abstract boolean canUse(PlayerEntity playerEntity);

	protected boolean insertItem(ItemStack itemStack, int i, int j, boolean bl) {
		boolean bl2 = false;
		int k = i;
		if (bl) {
			k = j - 1;
		}

		if (itemStack.isStackable()) {
			while (!itemStack.isEmpty() && (bl ? k >= i : k < j)) {
				Slot slot = (Slot)this.slotList.get(k);
				ItemStack itemStack2 = slot.getStack();
				if (!itemStack2.isEmpty() && canStacksCombine(itemStack, itemStack2)) {
					int l = itemStack2.getCount() + itemStack.getCount();
					if (l <= itemStack.getMaxCount()) {
						itemStack.setCount(0);
						itemStack2.setCount(l);
						slot.markDirty();
						bl2 = true;
					} else if (itemStack2.getCount() < itemStack.getMaxCount()) {
						itemStack.decrement(itemStack.getMaxCount() - itemStack2.getCount());
						itemStack2.setCount(itemStack.getMaxCount());
						slot.markDirty();
						bl2 = true;
					}
				}

				if (bl) {
					k--;
				} else {
					k++;
				}
			}
		}

		if (!itemStack.isEmpty()) {
			if (bl) {
				k = j - 1;
			} else {
				k = i;
			}

			while (bl ? k >= i : k < j) {
				Slot slot2 = (Slot)this.slotList.get(k);
				ItemStack itemStack3 = slot2.getStack();
				if (itemStack3.isEmpty() && slot2.canInsert(itemStack)) {
					if (itemStack.getCount() > slot2.getMaxStackAmount()) {
						slot2.setStack(itemStack.split(slot2.getMaxStackAmount()));
					} else {
						slot2.setStack(itemStack.split(itemStack.getCount()));
					}

					slot2.markDirty();
					bl2 = true;
					break;
				}

				if (bl) {
					k--;
				} else {
					k++;
				}
			}
		}

		return bl2;
	}

	public static int unpackQuickCraftStage(int i) {
		return i >> 2 & 3;
	}

	public static int unpackButtonId(int i) {
		return i & 3;
	}

	public static int packClickData(int i, int j) {
		return i & 3 | (j & 3) << 2;
	}

	public static boolean shouldQuickCraftContinue(int i, PlayerEntity playerEntity) {
		if (i == 0) {
			return true;
		} else {
			return i == 1 ? true : i == 2 && playerEntity.abilities.creativeMode;
		}
	}

	protected void endQuickCraft() {
		this.quickCraftButton = 0;
		this.quickCraftSlots.clear();
	}

	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack itemStack, boolean bl) {
		boolean bl2 = slot == null || !slot.hasStack();
		return !bl2 && itemStack.isItemEqualIgnoreDamage(slot.getStack()) && ItemStack.areTagsEqual(slot.getStack(), itemStack)
			? slot.getStack().getCount() + (bl ? 0 : itemStack.getCount()) <= itemStack.getMaxCount()
			: bl2;
	}

	public static void calculateStackSize(Set<Slot> set, int i, ItemStack itemStack, int j) {
		switch (i) {
			case 0:
				itemStack.setCount(MathHelper.floor((float)itemStack.getCount() / (float)set.size()));
				break;
			case 1:
				itemStack.setCount(1);
				break;
			case 2:
				itemStack.setCount(itemStack.getItem().getMaxCount());
		}

		itemStack.increment(j);
	}

	public boolean canInsertIntoSlot(Slot slot) {
		return true;
	}

	public static int calculateComparatorOutput(@Nullable BlockEntity blockEntity) {
		return blockEntity instanceof Inventory ? calculateComparatorOutput((Inventory)blockEntity) : 0;
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
