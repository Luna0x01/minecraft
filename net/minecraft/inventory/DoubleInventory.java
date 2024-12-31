package net.minecraft.inventory;

import net.minecraft.block.entity.LockableScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ChestScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DoubleInventory implements LockableScreenHandlerFactory {
	private String translationKey;
	private LockableScreenHandlerFactory mainInventory;
	private LockableScreenHandlerFactory secondaryInventory;

	public DoubleInventory(String string, LockableScreenHandlerFactory lockableScreenHandlerFactory, LockableScreenHandlerFactory lockableScreenHandlerFactory2) {
		this.translationKey = string;
		if (lockableScreenHandlerFactory == null) {
			lockableScreenHandlerFactory = lockableScreenHandlerFactory2;
		}

		if (lockableScreenHandlerFactory2 == null) {
			lockableScreenHandlerFactory2 = lockableScreenHandlerFactory;
		}

		this.mainInventory = lockableScreenHandlerFactory;
		this.secondaryInventory = lockableScreenHandlerFactory2;
		if (lockableScreenHandlerFactory.hasLock()) {
			lockableScreenHandlerFactory2.setLock(lockableScreenHandlerFactory.getLock());
		} else if (lockableScreenHandlerFactory2.hasLock()) {
			lockableScreenHandlerFactory.setLock(lockableScreenHandlerFactory2.getLock());
		}
	}

	@Override
	public int getInvSize() {
		return this.mainInventory.getInvSize() + this.secondaryInventory.getInvSize();
	}

	public boolean isPart(Inventory inventory) {
		return this.mainInventory == inventory || this.secondaryInventory == inventory;
	}

	@Override
	public String getTranslationKey() {
		if (this.mainInventory.hasCustomName()) {
			return this.mainInventory.getTranslationKey();
		} else {
			return this.secondaryInventory.hasCustomName() ? this.secondaryInventory.getTranslationKey() : this.translationKey;
		}
	}

	@Override
	public boolean hasCustomName() {
		return this.mainInventory.hasCustomName() || this.secondaryInventory.hasCustomName();
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}

	@Override
	public ItemStack getInvStack(int slot) {
		return slot >= this.mainInventory.getInvSize()
			? this.secondaryInventory.getInvStack(slot - this.mainInventory.getInvSize())
			: this.mainInventory.getInvStack(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		return slot >= this.mainInventory.getInvSize()
			? this.secondaryInventory.takeInvStack(slot - this.mainInventory.getInvSize(), amount)
			: this.mainInventory.takeInvStack(slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		return slot >= this.mainInventory.getInvSize()
			? this.secondaryInventory.removeInvStack(slot - this.mainInventory.getInvSize())
			: this.mainInventory.removeInvStack(slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		if (slot >= this.mainInventory.getInvSize()) {
			this.secondaryInventory.setInvStack(slot - this.mainInventory.getInvSize(), stack);
		} else {
			this.mainInventory.setInvStack(slot, stack);
		}
	}

	@Override
	public int getInvMaxStackAmount() {
		return this.mainInventory.getInvMaxStackAmount();
	}

	@Override
	public void markDirty() {
		this.mainInventory.markDirty();
		this.secondaryInventory.markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.mainInventory.canPlayerUseInv(player) && this.secondaryInventory.canPlayerUseInv(player);
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
		this.mainInventory.onInvOpen(player);
		this.secondaryInventory.onInvOpen(player);
	}

	@Override
	public void onInvClose(PlayerEntity player) {
		this.mainInventory.onInvClose(player);
		this.secondaryInventory.onInvClose(player);
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getProperty(int key) {
		return 0;
	}

	@Override
	public void setProperty(int id, int value) {
	}

	@Override
	public int getProperties() {
		return 0;
	}

	@Override
	public boolean hasLock() {
		return this.mainInventory.hasLock() || this.secondaryInventory.hasLock();
	}

	@Override
	public void setLock(ScreenHandlerLock lock) {
		this.mainInventory.setLock(lock);
		this.secondaryInventory.setLock(lock);
	}

	@Override
	public ScreenHandlerLock getLock() {
		return this.mainInventory.getLock();
	}

	@Override
	public String getId() {
		return this.mainInventory.getId();
	}

	@Override
	public ScreenHandler createScreenHandler(PlayerInventory inventory, PlayerEntity player) {
		return new ChestScreenHandler(inventory, this, player);
	}

	@Override
	public void clear() {
		this.mainInventory.clear();
		this.secondaryInventory.clear();
	}
}
