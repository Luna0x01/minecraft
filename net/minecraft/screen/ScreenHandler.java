package net.minecraft.screen;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public abstract class ScreenHandler {
	public static final int EMPTY_SPACE_SLOT_INDEX = -999;
	public static final int field_30731 = 0;
	public static final int field_30732 = 1;
	public static final int field_30733 = 2;
	public static final int field_30734 = 0;
	public static final int field_30735 = 1;
	public static final int field_30736 = 2;
	public static final int field_30737 = Integer.MAX_VALUE;
	private final DefaultedList<ItemStack> trackedStacks = DefaultedList.of();
	public final DefaultedList<Slot> slots = DefaultedList.of();
	private final List<Property> properties = Lists.newArrayList();
	private ItemStack cursorStack = ItemStack.EMPTY;
	private final DefaultedList<ItemStack> previousTrackedStacks = DefaultedList.of();
	private final IntList trackedPropertyValues = new IntArrayList();
	private ItemStack previousCursorStack = ItemStack.EMPTY;
	private int revision;
	@Nullable
	private final ScreenHandlerType<?> type;
	public final int syncId;
	private int quickCraftButton = -1;
	private int quickCraftStage;
	private final Set<Slot> quickCraftSlots = Sets.newHashSet();
	private final List<ScreenHandlerListener> listeners = Lists.newArrayList();
	@Nullable
	private ScreenHandlerSyncHandler syncHandler;
	private boolean disableSync;

	protected ScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
		this.type = type;
		this.syncId = syncId;
	}

	protected static boolean canUse(ScreenHandlerContext context, PlayerEntity player, Block block) {
		return context.get(
			(world, pos) -> !world.getBlockState(pos).isOf(block)
					? false
					: player.squaredDistanceTo((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0,
			true
		);
	}

	public ScreenHandlerType<?> getType() {
		if (this.type == null) {
			throw new UnsupportedOperationException("Unable to construct this menu by type");
		} else {
			return this.type;
		}
	}

	protected static void checkSize(Inventory inventory, int expectedSize) {
		int i = inventory.size();
		if (i < expectedSize) {
			throw new IllegalArgumentException("Container size " + i + " is smaller than expected " + expectedSize);
		}
	}

	protected static void checkDataCount(PropertyDelegate data, int expectedCount) {
		int i = data.size();
		if (i < expectedCount) {
			throw new IllegalArgumentException("Container data count " + i + " is smaller than expected " + expectedCount);
		}
	}

	protected Slot addSlot(Slot slot) {
		slot.id = this.slots.size();
		this.slots.add(slot);
		this.trackedStacks.add(ItemStack.EMPTY);
		this.previousTrackedStacks.add(ItemStack.EMPTY);
		return slot;
	}

	protected Property addProperty(Property property) {
		this.properties.add(property);
		this.trackedPropertyValues.add(0);
		return property;
	}

	protected void addProperties(PropertyDelegate propertyDelegate) {
		for (int i = 0; i < propertyDelegate.size(); i++) {
			this.addProperty(Property.create(propertyDelegate, i));
		}
	}

	public void addListener(ScreenHandlerListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
			this.sendContentUpdates();
		}
	}

	public void updateSyncHandler(ScreenHandlerSyncHandler handler) {
		this.syncHandler = handler;
		this.syncState();
	}

	public void syncState() {
		int i = 0;

		for (int j = this.slots.size(); i < j; i++) {
			this.previousTrackedStacks.set(i, this.slots.get(i).getStack().copy());
		}

		this.previousCursorStack = this.getCursorStack().copy();
		i = 0;

		for (int l = this.properties.size(); i < l; i++) {
			this.trackedPropertyValues.set(i, ((Property)this.properties.get(i)).get());
		}

		if (this.syncHandler != null) {
			this.syncHandler.updateState(this, this.previousTrackedStacks, this.previousCursorStack, this.trackedPropertyValues.toIntArray());
		}
	}

	public void removeListener(ScreenHandlerListener listener) {
		this.listeners.remove(listener);
	}

	public DefaultedList<ItemStack> getStacks() {
		DefaultedList<ItemStack> defaultedList = DefaultedList.of();

		for (Slot slot : this.slots) {
			defaultedList.add(slot.getStack());
		}

		return defaultedList;
	}

	public void sendContentUpdates() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = this.slots.get(i).getStack();
			Supplier<ItemStack> supplier = Suppliers.memoize(itemStack::copy);
			this.updateTrackedSlot(i, itemStack, supplier);
			this.checkSlotUpdates(i, itemStack, supplier);
		}

		this.checkCursorStackUpdates();

		for (int j = 0; j < this.properties.size(); j++) {
			Property property = (Property)this.properties.get(j);
			int k = property.get();
			if (property.hasChanged()) {
				this.notifyPropertyUpdate(j, k);
			}

			this.checkPropertyUpdates(j, k);
		}
	}

	public void updateToClient() {
		for (int i = 0; i < this.slots.size(); i++) {
			ItemStack itemStack = this.slots.get(i).getStack();
			this.updateTrackedSlot(i, itemStack, itemStack::copy);
		}

		for (int j = 0; j < this.properties.size(); j++) {
			Property property = (Property)this.properties.get(j);
			if (property.hasChanged()) {
				this.notifyPropertyUpdate(j, property.get());
			}
		}

		this.syncState();
	}

	private void notifyPropertyUpdate(int index, int value) {
		for (ScreenHandlerListener screenHandlerListener : this.listeners) {
			screenHandlerListener.onPropertyUpdate(this, index, value);
		}
	}

	private void updateTrackedSlot(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
		ItemStack itemStack = this.trackedStacks.get(slot);
		if (!ItemStack.areEqual(itemStack, stack)) {
			ItemStack itemStack2 = (ItemStack)copySupplier.get();
			this.trackedStacks.set(slot, itemStack2);

			for (ScreenHandlerListener screenHandlerListener : this.listeners) {
				screenHandlerListener.onSlotUpdate(this, slot, itemStack2);
			}
		}
	}

	private void checkSlotUpdates(int slot, ItemStack stack, Supplier<ItemStack> copySupplier) {
		if (!this.disableSync) {
			ItemStack itemStack = this.previousTrackedStacks.get(slot);
			if (!ItemStack.areEqual(itemStack, stack)) {
				ItemStack itemStack2 = (ItemStack)copySupplier.get();
				this.previousTrackedStacks.set(slot, itemStack2);
				if (this.syncHandler != null) {
					this.syncHandler.updateSlot(this, slot, itemStack2);
				}
			}
		}
	}

	private void checkPropertyUpdates(int id, int value) {
		if (!this.disableSync) {
			int i = this.trackedPropertyValues.getInt(id);
			if (i != value) {
				this.trackedPropertyValues.set(id, value);
				if (this.syncHandler != null) {
					this.syncHandler.updateProperty(this, id, value);
				}
			}
		}
	}

	private void checkCursorStackUpdates() {
		if (!this.disableSync) {
			if (!ItemStack.areEqual(this.getCursorStack(), this.previousCursorStack)) {
				this.previousCursorStack = this.getCursorStack().copy();
				if (this.syncHandler != null) {
					this.syncHandler.updateCursorStack(this, this.previousCursorStack);
				}
			}
		}
	}

	public void setPreviousTrackedSlot(int slot, ItemStack stack) {
		this.previousTrackedStacks.set(slot, stack.copy());
	}

	public void setPreviousTrackedSlotMutable(int slot, ItemStack stack) {
		this.previousTrackedStacks.set(slot, stack);
	}

	public void setPreviousCursorStack(ItemStack stack) {
		this.previousCursorStack = stack.copy();
	}

	public boolean onButtonClick(PlayerEntity player, int id) {
		return false;
	}

	public Slot getSlot(int index) {
		return this.slots.get(index);
	}

	public ItemStack transferSlot(PlayerEntity player, int index) {
		return this.slots.get(index).getStack();
	}

	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		try {
			this.internalOnSlotClick(slotIndex, button, actionType, player);
		} catch (Exception var8) {
			CrashReport crashReport = CrashReport.create(var8, "Container click");
			CrashReportSection crashReportSection = crashReport.addElement("Click info");
			crashReportSection.add("Menu Type", (CrashCallable<String>)(() -> this.type != null ? Registry.SCREEN_HANDLER.getId(this.type).toString() : "<no type>"));
			crashReportSection.add("Menu Class", (CrashCallable<String>)(() -> this.getClass().getCanonicalName()));
			crashReportSection.add("Slot Count", this.slots.size());
			crashReportSection.add("Slot", slotIndex);
			crashReportSection.add("Button", button);
			crashReportSection.add("Type", actionType);
			throw new CrashException(crashReport);
		}
	}

	private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		PlayerInventory playerInventory = player.getInventory();
		if (actionType == SlotActionType.QUICK_CRAFT) {
			int i = this.quickCraftStage;
			this.quickCraftStage = unpackQuickCraftStage(button);
			if ((i != 1 || this.quickCraftStage != 2) && i != this.quickCraftStage) {
				this.endQuickCraft();
			} else if (this.getCursorStack().isEmpty()) {
				this.endQuickCraft();
			} else if (this.quickCraftStage == 0) {
				this.quickCraftButton = unpackQuickCraftButton(button);
				if (shouldQuickCraftContinue(this.quickCraftButton, player)) {
					this.quickCraftStage = 1;
					this.quickCraftSlots.clear();
				} else {
					this.endQuickCraft();
				}
			} else if (this.quickCraftStage == 1) {
				Slot slot = this.slots.get(slotIndex);
				ItemStack itemStack = this.getCursorStack();
				if (canInsertItemIntoSlot(slot, itemStack, true)
					&& slot.canInsert(itemStack)
					&& (this.quickCraftButton == 2 || itemStack.getCount() > this.quickCraftSlots.size())
					&& this.canInsertIntoSlot(slot)) {
					this.quickCraftSlots.add(slot);
				}
			} else if (this.quickCraftStage == 2) {
				if (!this.quickCraftSlots.isEmpty()) {
					if (this.quickCraftSlots.size() == 1) {
						int j = ((Slot)this.quickCraftSlots.iterator().next()).id;
						this.endQuickCraft();
						this.internalOnSlotClick(j, this.quickCraftButton, SlotActionType.PICKUP, player);
						return;
					}

					ItemStack itemStack2 = this.getCursorStack().copy();
					int k = this.getCursorStack().getCount();

					for (Slot slot2 : this.quickCraftSlots) {
						ItemStack itemStack3 = this.getCursorStack();
						if (slot2 != null
							&& canInsertItemIntoSlot(slot2, itemStack3, true)
							&& slot2.canInsert(itemStack3)
							&& (this.quickCraftButton == 2 || itemStack3.getCount() >= this.quickCraftSlots.size())
							&& this.canInsertIntoSlot(slot2)) {
							ItemStack itemStack4 = itemStack2.copy();
							int l = slot2.hasStack() ? slot2.getStack().getCount() : 0;
							calculateStackSize(this.quickCraftSlots, this.quickCraftButton, itemStack4, l);
							int m = Math.min(itemStack4.getMaxCount(), slot2.getMaxItemCount(itemStack4));
							if (itemStack4.getCount() > m) {
								itemStack4.setCount(m);
							}

							k -= itemStack4.getCount() - l;
							slot2.setStack(itemStack4);
						}
					}

					itemStack2.setCount(k);
					this.setCursorStack(itemStack2);
				}

				this.endQuickCraft();
			} else {
				this.endQuickCraft();
			}
		} else if (this.quickCraftStage != 0) {
			this.endQuickCraft();
		} else if ((actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE) && (button == 0 || button == 1)) {
			ClickType clickType = button == 0 ? ClickType.LEFT : ClickType.RIGHT;
			if (slotIndex == -999) {
				if (!this.getCursorStack().isEmpty()) {
					if (clickType == ClickType.LEFT) {
						player.dropItem(this.getCursorStack(), true);
						this.setCursorStack(ItemStack.EMPTY);
					} else {
						player.dropItem(this.getCursorStack().split(1), true);
					}
				}
			} else if (actionType == SlotActionType.QUICK_MOVE) {
				if (slotIndex < 0) {
					return;
				}

				Slot slot3 = this.slots.get(slotIndex);
				if (!slot3.canTakeItems(player)) {
					return;
				}

				ItemStack itemStack5 = this.transferSlot(player, slotIndex);

				while (!itemStack5.isEmpty() && ItemStack.areItemsEqualIgnoreDamage(slot3.getStack(), itemStack5)) {
					itemStack5 = this.transferSlot(player, slotIndex);
				}
			} else {
				if (slotIndex < 0) {
					return;
				}

				Slot slot4 = this.slots.get(slotIndex);
				ItemStack itemStack6 = slot4.getStack();
				ItemStack itemStack7 = this.getCursorStack();
				player.onPickupSlotClick(itemStack7, slot4.getStack(), clickType);
				if (!itemStack7.onStackClicked(slot4, clickType, player) && !itemStack6.onClicked(itemStack7, slot4, clickType, player, this.getCursorStackReference())) {
					if (itemStack6.isEmpty()) {
						if (!itemStack7.isEmpty()) {
							int n = clickType == ClickType.LEFT ? itemStack7.getCount() : 1;
							this.setCursorStack(slot4.insertStack(itemStack7, n));
						}
					} else if (slot4.canTakeItems(player)) {
						if (itemStack7.isEmpty()) {
							int o = clickType == ClickType.LEFT ? itemStack6.getCount() : (itemStack6.getCount() + 1) / 2;
							Optional<ItemStack> optional = slot4.tryTakeStackRange(o, Integer.MAX_VALUE, player);
							optional.ifPresent(stack -> {
								this.setCursorStack(stack);
								slot4.onTakeItem(player, stack);
							});
						} else if (slot4.canInsert(itemStack7)) {
							if (ItemStack.canCombine(itemStack6, itemStack7)) {
								int p = clickType == ClickType.LEFT ? itemStack7.getCount() : 1;
								this.setCursorStack(slot4.insertStack(itemStack7, p));
							} else if (itemStack7.getCount() <= slot4.getMaxItemCount(itemStack7)) {
								slot4.setStack(itemStack7);
								this.setCursorStack(itemStack6);
							}
						} else if (ItemStack.canCombine(itemStack6, itemStack7)) {
							Optional<ItemStack> optional2 = slot4.tryTakeStackRange(itemStack6.getCount(), itemStack7.getMaxCount() - itemStack7.getCount(), player);
							optional2.ifPresent(stack -> {
								itemStack7.increment(stack.getCount());
								slot4.onTakeItem(player, stack);
							});
						}
					}
				}

				slot4.markDirty();
			}
		} else if (actionType == SlotActionType.SWAP) {
			Slot slot5 = this.slots.get(slotIndex);
			ItemStack itemStack8 = playerInventory.getStack(button);
			ItemStack itemStack9 = slot5.getStack();
			if (!itemStack8.isEmpty() || !itemStack9.isEmpty()) {
				if (itemStack8.isEmpty()) {
					if (slot5.canTakeItems(player)) {
						playerInventory.setStack(button, itemStack9);
						slot5.onTake(itemStack9.getCount());
						slot5.setStack(ItemStack.EMPTY);
						slot5.onTakeItem(player, itemStack9);
					}
				} else if (itemStack9.isEmpty()) {
					if (slot5.canInsert(itemStack8)) {
						int q = slot5.getMaxItemCount(itemStack8);
						if (itemStack8.getCount() > q) {
							slot5.setStack(itemStack8.split(q));
						} else {
							slot5.setStack(itemStack8);
							playerInventory.setStack(button, ItemStack.EMPTY);
						}
					}
				} else if (slot5.canTakeItems(player) && slot5.canInsert(itemStack8)) {
					int r = slot5.getMaxItemCount(itemStack8);
					if (itemStack8.getCount() > r) {
						slot5.setStack(itemStack8.split(r));
						slot5.onTakeItem(player, itemStack9);
						if (!playerInventory.insertStack(itemStack9)) {
							player.dropItem(itemStack9, true);
						}
					} else {
						slot5.setStack(itemStack8);
						playerInventory.setStack(button, itemStack9);
						slot5.onTakeItem(player, itemStack9);
					}
				}
			}
		} else if (actionType == SlotActionType.CLONE && player.getAbilities().creativeMode && this.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot6 = this.slots.get(slotIndex);
			if (slot6.hasStack()) {
				ItemStack itemStack10 = slot6.getStack().copy();
				itemStack10.setCount(itemStack10.getMaxCount());
				this.setCursorStack(itemStack10);
			}
		} else if (actionType == SlotActionType.THROW && this.getCursorStack().isEmpty() && slotIndex >= 0) {
			Slot slot7 = this.slots.get(slotIndex);
			int s = button == 0 ? 1 : slot7.getStack().getCount();
			ItemStack itemStack11 = slot7.takeStackRange(s, Integer.MAX_VALUE, player);
			player.dropItem(itemStack11, true);
		} else if (actionType == SlotActionType.PICKUP_ALL && slotIndex >= 0) {
			Slot slot8 = this.slots.get(slotIndex);
			ItemStack itemStack12 = this.getCursorStack();
			if (!itemStack12.isEmpty() && (!slot8.hasStack() || !slot8.canTakeItems(player))) {
				int t = button == 0 ? 0 : this.slots.size() - 1;
				int u = button == 0 ? 1 : -1;

				for (int v = 0; v < 2; v++) {
					for (int w = t; w >= 0 && w < this.slots.size() && itemStack12.getCount() < itemStack12.getMaxCount(); w += u) {
						Slot slot9 = this.slots.get(w);
						if (slot9.hasStack() && canInsertItemIntoSlot(slot9, itemStack12, true) && slot9.canTakeItems(player) && this.canInsertIntoSlot(itemStack12, slot9)) {
							ItemStack itemStack13 = slot9.getStack();
							if (v != 0 || itemStack13.getCount() != itemStack13.getMaxCount()) {
								ItemStack itemStack14 = slot9.takeStackRange(itemStack13.getCount(), itemStack12.getMaxCount() - itemStack12.getCount(), player);
								itemStack12.increment(itemStack14.getCount());
							}
						}
					}
				}
			}
		}
	}

	private StackReference getCursorStackReference() {
		return new StackReference() {
			@Override
			public ItemStack get() {
				return ScreenHandler.this.getCursorStack();
			}

			@Override
			public boolean set(ItemStack stack) {
				ScreenHandler.this.setCursorStack(stack);
				return true;
			}
		};
	}

	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return true;
	}

	public void close(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			ItemStack itemStack = this.getCursorStack();
			if (!itemStack.isEmpty()) {
				if (player.isAlive() && !((ServerPlayerEntity)player).isDisconnected()) {
					player.getInventory().offerOrDrop(itemStack);
				} else {
					player.dropItem(itemStack, false);
				}

				this.setCursorStack(ItemStack.EMPTY);
			}
		}
	}

	protected void dropInventory(PlayerEntity player, Inventory inventory) {
		if (!player.isAlive() || player instanceof ServerPlayerEntity && ((ServerPlayerEntity)player).isDisconnected()) {
			for (int i = 0; i < inventory.size(); i++) {
				player.dropItem(inventory.removeStack(i), false);
			}
		} else {
			for (int j = 0; j < inventory.size(); j++) {
				PlayerInventory playerInventory = player.getInventory();
				if (playerInventory.player instanceof ServerPlayerEntity) {
					playerInventory.offerOrDrop(inventory.removeStack(j));
				}
			}
		}
	}

	public void onContentChanged(Inventory inventory) {
		this.sendContentUpdates();
	}

	public void setStackInSlot(int slot, int revision, ItemStack stack) {
		this.getSlot(slot).setStack(stack);
		this.revision = revision;
	}

	public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
		for (int i = 0; i < stacks.size(); i++) {
			this.getSlot(i).setStack((ItemStack)stacks.get(i));
		}

		this.cursorStack = cursorStack;
		this.revision = revision;
	}

	public void setProperty(int id, int value) {
		((Property)this.properties.get(id)).set(value);
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
				Slot slot = this.slots.get(i);
				ItemStack itemStack = slot.getStack();
				if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
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
				Slot slot2 = this.slots.get(i);
				ItemStack itemStack2 = slot2.getStack();
				if (itemStack2.isEmpty() && slot2.canInsert(stack)) {
					if (stack.getCount() > slot2.getMaxItemCount()) {
						slot2.setStack(stack.split(slot2.getMaxItemCount()));
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

	public static int unpackQuickCraftButton(int quickCraftData) {
		return quickCraftData >> 2 & 3;
	}

	public static int unpackQuickCraftStage(int quickCraftData) {
		return quickCraftData & 3;
	}

	public static int packQuickCraftData(int quickCraftStage, int buttonId) {
		return quickCraftStage & 3 | (buttonId & 3) << 2;
	}

	public static boolean shouldQuickCraftContinue(int stage, PlayerEntity player) {
		if (stage == 0) {
			return true;
		} else {
			return stage == 1 ? true : stage == 2 && player.getAbilities().creativeMode;
		}
	}

	protected void endQuickCraft() {
		this.quickCraftStage = 0;
		this.quickCraftSlots.clear();
	}

	public static boolean canInsertItemIntoSlot(@Nullable Slot slot, ItemStack stack, boolean allowOverflow) {
		boolean bl = slot == null || !slot.hasStack();
		return !bl && ItemStack.canCombine(stack, slot.getStack()) ? slot.getStack().getCount() + (allowOverflow ? 0 : stack.getCount()) <= stack.getMaxCount() : bl;
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

			for (int j = 0; j < inventory.size(); j++) {
				ItemStack itemStack = inventory.getStack(j);
				if (!itemStack.isEmpty()) {
					f += (float)itemStack.getCount() / (float)Math.min(inventory.getMaxCountPerStack(), itemStack.getMaxCount());
					i++;
				}
			}

			f /= (float)inventory.size();
			return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
		}
	}

	public void setCursorStack(ItemStack stack) {
		this.cursorStack = stack;
	}

	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	public void disableSyncing() {
		this.disableSync = true;
	}

	public void enableSyncing() {
		this.disableSync = false;
	}

	public void copySharedSlots(ScreenHandler handler) {
		Table<Inventory, Integer, Integer> table = HashBasedTable.create();

		for (int i = 0; i < handler.slots.size(); i++) {
			Slot slot = handler.slots.get(i);
			table.put(slot.inventory, slot.getIndex(), i);
		}

		for (int j = 0; j < this.slots.size(); j++) {
			Slot slot2 = this.slots.get(j);
			Integer integer = (Integer)table.get(slot2.inventory, slot2.getIndex());
			if (integer != null) {
				this.trackedStacks.set(j, handler.trackedStacks.get(integer));
				this.previousTrackedStacks.set(j, handler.previousTrackedStacks.get(integer));
			}
		}
	}

	public OptionalInt getSlotIndex(Inventory inventory, int index) {
		for (int i = 0; i < this.slots.size(); i++) {
			Slot slot = this.slots.get(i);
			if (slot.inventory == inventory && index == slot.getIndex()) {
				return OptionalInt.of(i);
			}
		}

		return OptionalInt.empty();
	}

	public int getRevision() {
		return this.revision;
	}

	public int nextRevision() {
		this.revision = this.revision + 1 & 32767;
		return this.revision;
	}
}
