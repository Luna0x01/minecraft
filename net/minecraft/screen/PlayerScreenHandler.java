package net.minecraft.screen;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.slot.CraftingResultSlot;
import net.minecraft.inventory.slot.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDispatcher;

public class PlayerScreenHandler extends ScreenHandler {
	private static final EquipmentSlot[] field_12272 = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public CraftingInventory craftingInventory = new CraftingInventory(this, 2, 2);
	public Inventory craftingResultInventory = new CraftingResultInventory();
	public boolean onServer;
	private final PlayerEntity owner;

	public PlayerScreenHandler(PlayerInventory playerInventory, boolean bl, PlayerEntity playerEntity) {
		this.onServer = bl;
		this.owner = playerEntity;
		this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftingInventory, this.craftingResultInventory, 0, 154, 28));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new Slot(this.craftingInventory, j + i * 2, 98 + j * 18, 18 + i * 18));
			}
		}

		for (int k = 0; k < 4; k++) {
			final EquipmentSlot equipmentSlot = field_12272[k];
			this.addSlot(new Slot(playerInventory, 36 + (3 - k), 8, 8 + k * 18) {
				@Override
				public int getMaxStackAmount() {
					return 1;
				}

				@Override
				public boolean canInsert(ItemStack stack) {
					return equipmentSlot == MobEntity.method_13083(stack);
				}

				@Override
				public boolean canTakeItems(PlayerEntity playerEntity) {
					ItemStack itemStack = this.getStack();
					return !itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack) ? false : super.canTakeItems(playerEntity);
				}

				@Nullable
				@Override
				public String getBackgroundSprite() {
					return ArmorItem.EMPTY[equipmentSlot.method_13032()];
				}
			});
		}

		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 9; m++) {
				this.addSlot(new Slot(playerInventory, m + (l + 1) * 9, 8 + m * 18, 84 + l * 18));
			}
		}

		for (int n = 0; n < 9; n++) {
			this.addSlot(new Slot(playerInventory, n, 8 + n * 18, 142));
		}

		this.addSlot(new Slot(playerInventory, 40, 77, 62) {
			@Nullable
			@Override
			public String getBackgroundSprite() {
				return "minecraft:items/empty_armor_slot_shield";
			}
		});
		this.onContentChanged(this.craftingInventory);
	}

	@Override
	public void onContentChanged(Inventory inventory) {
		this.craftingResultInventory.setInvStack(0, RecipeDispatcher.getInstance().matches(this.craftingInventory, this.owner.world));
	}

	@Override
	public void close(PlayerEntity player) {
		super.close(player);

		for (int i = 0; i < 4; i++) {
			ItemStack itemStack = this.craftingInventory.removeInvStack(i);
			if (!itemStack.isEmpty()) {
				player.dropItem(itemStack, false);
			}
		}

		this.craftingResultInventory.setInvStack(0, ItemStack.EMPTY);
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return true;
	}

	@Override
	public ItemStack transferSlot(PlayerEntity player, int invSlot) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(invSlot);
		if (slot != null && slot.hasStack()) {
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			EquipmentSlot equipmentSlot = MobEntity.method_13083(itemStack);
			if (invSlot == 0) {
				if (!this.insertItem(itemStack2, 9, 45, true)) {
					return ItemStack.EMPTY;
				}

				slot.onStackChanged(itemStack2, itemStack);
			} else if (invSlot >= 1 && invSlot < 5) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 5 && invSlot < 9) {
				if (!this.insertItem(itemStack2, 9, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - equipmentSlot.method_13032())).hasStack()) {
				int i = 8 - equipmentSlot.method_13032();
				if (!this.insertItem(itemStack2, i, i + 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasStack()) {
				if (!this.insertItem(itemStack2, 45, 46, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 9 && invSlot < 36) {
				if (!this.insertItem(itemStack2, 36, 45, false)) {
					return ItemStack.EMPTY;
				}
			} else if (invSlot >= 36 && invSlot < 45) {
				if (!this.insertItem(itemStack2, 9, 36, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.insertItem(itemStack2, 9, 45, false)) {
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}

			if (itemStack2.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemStack3 = slot.method_3298(player, itemStack2);
			if (invSlot == 0) {
				player.dropItem(itemStack3, false);
			}
		}

		return itemStack;
	}

	@Override
	public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
		return slot.inventory != this.craftingResultInventory && super.canInsertIntoSlot(stack, slot);
	}
}
