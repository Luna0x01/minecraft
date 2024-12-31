package net.minecraft.entity.player;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class PlayerInventory implements Inventory {
	public ItemStack[] main = new ItemStack[36];
	public ItemStack[] armor = new ItemStack[4];
	public int selectedSlot;
	public PlayerEntity player;
	private ItemStack cursorStack;
	public boolean dirty;

	public PlayerInventory(PlayerEntity playerEntity) {
		this.player = playerEntity;
	}

	public ItemStack getMainHandStack() {
		return this.selectedSlot < 9 && this.selectedSlot >= 0 ? this.main[this.selectedSlot] : null;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private int getSlotWithItem(Item item) {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null && this.main[i].getItem() == item) {
				return i;
			}
		}

		return -1;
	}

	private int getSlotWithItem(Item item, int data) {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null && this.main[i].getItem() == item && this.main[i].getData() == data) {
				return i;
			}
		}

		return -1;
	}

	private int getSlotWithItemStack(ItemStack stack) {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null
				&& this.main[i].getItem() == stack.getItem()
				&& this.main[i].isStackable()
				&& this.main[i].count < this.main[i].getMaxCount()
				&& this.main[i].count < this.getInvMaxStackAmount()
				&& (!this.main[i].isUnbreakable() || this.main[i].getData() == stack.getData())
				&& ItemStack.equalsIgnoreDamage(this.main[i], stack)) {
				return i;
			}
		}

		return -1;
	}

	public int getEmptySlot() {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] == null) {
				return i;
			}
		}

		return -1;
	}

	public void addPickBlock(Item item, int data, boolean hasData, boolean isInCreative) {
		ItemStack itemStack = this.getMainHandStack();
		int i = hasData ? this.getSlotWithItem(item, data) : this.getSlotWithItem(item);
		if (i >= 0 && i < 9) {
			this.selectedSlot = i;
		} else if (isInCreative && item != null) {
			int j = this.getEmptySlot();
			if (j >= 0 && j < 9) {
				this.selectedSlot = j;
			}

			if (itemStack == null || !itemStack.isEnchantable() || this.getSlotWithItem(itemStack.getItem(), itemStack.getDamage()) != this.selectedSlot) {
				int k = this.getSlotWithItem(item, data);
				int l;
				if (k >= 0) {
					l = this.main[k].count;
					this.main[k] = this.main[this.selectedSlot];
				} else {
					l = 1;
				}

				this.main[this.selectedSlot] = new ItemStack(item, l, data);
			}
		}
	}

	public void scrollInHotbar(int scrollAmount) {
		if (scrollAmount > 0) {
			scrollAmount = 1;
		}

		if (scrollAmount < 0) {
			scrollAmount = -1;
		}

		this.selectedSlot -= scrollAmount;

		while (this.selectedSlot < 0) {
			this.selectedSlot += 9;
		}

		while (this.selectedSlot >= 9) {
			this.selectedSlot -= 9;
		}
	}

	public int method_11232(Item item, int i, int j, NbtCompound nbt) {
		int k = 0;

		for (int l = 0; l < this.main.length; l++) {
			ItemStack itemStack = this.main[l];
			if (itemStack != null
				&& (item == null || itemStack.getItem() == item)
				&& (i <= -1 || itemStack.getData() == i)
				&& (nbt == null || NbtHelper.matches(nbt, itemStack.getNbt(), true))) {
				int m = j <= 0 ? itemStack.count : Math.min(j - k, itemStack.count);
				k += m;
				if (j != 0) {
					this.main[l].count -= m;
					if (this.main[l].count == 0) {
						this.main[l] = null;
					}

					if (j > 0 && k >= j) {
						return k;
					}
				}
			}
		}

		for (int n = 0; n < this.armor.length; n++) {
			ItemStack itemStack2 = this.armor[n];
			if (itemStack2 != null
				&& (item == null || itemStack2.getItem() == item)
				&& (i <= -1 || itemStack2.getData() == i)
				&& (nbt == null || NbtHelper.matches(nbt, itemStack2.getNbt(), false))) {
				int o = j <= 0 ? itemStack2.count : Math.min(j - k, itemStack2.count);
				k += o;
				if (j != 0) {
					this.armor[n].count -= o;
					if (this.armor[n].count == 0) {
						this.armor[n] = null;
					}

					if (j > 0 && k >= j) {
						return k;
					}
				}
			}
		}

		if (this.cursorStack != null) {
			if (item != null && this.cursorStack.getItem() != item) {
				return k;
			}

			if (i > -1 && this.cursorStack.getData() != i) {
				return k;
			}

			if (nbt != null && !NbtHelper.matches(nbt, this.cursorStack.getNbt(), false)) {
				return k;
			}

			int p = j <= 0 ? this.cursorStack.count : Math.min(j - k, this.cursorStack.count);
			k += p;
			if (j != 0) {
				this.cursorStack.count -= p;
				if (this.cursorStack.count == 0) {
					this.cursorStack = null;
				}

				if (j > 0 && k >= j) {
					return k;
				}
			}
		}

		return k;
	}

	private int method_3140(ItemStack itemStack) {
		Item item = itemStack.getItem();
		int i = itemStack.count;
		int j = this.getSlotWithItemStack(itemStack);
		if (j < 0) {
			j = this.getEmptySlot();
		}

		if (j < 0) {
			return i;
		} else {
			if (this.main[j] == null) {
				this.main[j] = new ItemStack(item, 0, itemStack.getData());
				if (itemStack.hasNbt()) {
					this.main[j].setNbt((NbtCompound)itemStack.getNbt().copy());
				}
			}

			int k = i;
			if (i > this.main[j].getMaxCount() - this.main[j].count) {
				k = this.main[j].getMaxCount() - this.main[j].count;
			}

			if (k > this.getInvMaxStackAmount() - this.main[j].count) {
				k = this.getInvMaxStackAmount() - this.main[j].count;
			}

			if (k == 0) {
				return i;
			} else {
				i -= k;
				this.main[j].count += k;
				this.main[j].pickupTick = 5;
				return i;
			}
		}
	}

	public void updateItems() {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null) {
				this.main[i].inventoryTick(this.player.world, this.player, i, this.selectedSlot == i);
			}
		}
	}

	public boolean useItem(Item item) {
		int i = this.getSlotWithItem(item);
		if (i < 0) {
			return false;
		} else {
			if (--this.main[i].count <= 0) {
				this.main[i] = null;
			}

			return true;
		}
	}

	public boolean containsItem(Item item) {
		int i = this.getSlotWithItem(item);
		return i >= 0;
	}

	public boolean insertStack(ItemStack itemStack) {
		if (itemStack != null && itemStack.count != 0 && itemStack.getItem() != null) {
			try {
				if (itemStack.isDamaged()) {
					int j = this.getEmptySlot();
					if (j >= 0) {
						this.main[j] = ItemStack.copyOf(itemStack);
						this.main[j].pickupTick = 5;
						itemStack.count = 0;
						return true;
					} else if (this.player.abilities.creativeMode) {
						itemStack.count = 0;
						return true;
					} else {
						return false;
					}
				} else {
					int i;
					do {
						i = itemStack.count;
						itemStack.count = this.method_3140(itemStack);
					} while (itemStack.count > 0 && itemStack.count < i);

					if (itemStack.count == i && this.player.abilities.creativeMode) {
						itemStack.count = 0;
						return true;
					} else {
						return itemStack.count < i;
					}
				}
			} catch (Throwable var5) {
				CrashReport crashReport = CrashReport.create(var5, "Adding item to inventory");
				CrashReportSection crashReportSection = crashReport.addElement("Item being added");
				crashReportSection.add("Item ID", Item.getRawId(itemStack.getItem()));
				crashReportSection.add("Item data", itemStack.getData());
				crashReportSection.add("Item name", new Callable<String>() {
					public String call() throws Exception {
						return itemStack.getCustomName();
					}
				});
				throw new CrashException(crashReport);
			}
		} else {
			return false;
		}
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack[] itemStacks = this.main;
		if (slot >= this.main.length) {
			itemStacks = this.armor;
			slot -= this.main.length;
		}

		if (itemStacks[slot] != null) {
			if (itemStacks[slot].count <= amount) {
				ItemStack itemStack = itemStacks[slot];
				itemStacks[slot] = null;
				return itemStack;
			} else {
				ItemStack itemStack2 = itemStacks[slot].split(amount);
				if (itemStacks[slot].count == 0) {
					itemStacks[slot] = null;
				}

				return itemStack2;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack[] itemStacks = this.main;
		if (slot >= this.main.length) {
			itemStacks = this.armor;
			slot -= this.main.length;
		}

		if (itemStacks[slot] != null) {
			ItemStack itemStack = itemStacks[slot];
			itemStacks[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		ItemStack[] itemStacks = this.main;
		if (slot >= itemStacks.length) {
			slot -= itemStacks.length;
			itemStacks = this.armor;
		}

		itemStacks[slot] = stack;
	}

	public float getMiningSpeed(Block block) {
		float f = 1.0F;
		if (this.main[this.selectedSlot] != null) {
			f *= this.main[this.selectedSlot].getMiningSpeedMultiplier(block);
		}

		return f;
	}

	public NbtList serialize(NbtList nbt) {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				this.main[i].toNbt(nbtCompound);
				nbt.add(nbtCompound);
			}
		}

		for (int j = 0; j < this.armor.length; j++) {
			if (this.armor[j] != null) {
				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound2.putByte("Slot", (byte)(j + 100));
				this.armor[j].toNbt(nbtCompound2);
				nbt.add(nbtCompound2);
			}
		}

		return nbt;
	}

	public void deserialize(NbtList nbtList) {
		this.main = new ItemStack[36];
		this.armor = new ItemStack[4];

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
			if (itemStack != null) {
				if (j >= 0 && j < this.main.length) {
					this.main[j] = itemStack;
				}

				if (j >= 100 && j < this.armor.length + 100) {
					this.armor[j - 100] = itemStack;
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.main.length + 4;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		ItemStack[] itemStacks = this.main;
		if (slot >= itemStacks.length) {
			slot -= itemStacks.length;
			itemStacks = this.armor;
		}

		return itemStacks[slot];
	}

	@Override
	public String getTranslationKey() {
		return "container.inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public Text getName() {
		return (Text)(this.hasCustomName() ? new LiteralText(this.getTranslationKey()) : new TranslatableText(this.getTranslationKey()));
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	public boolean canToolBreak(Block block) {
		if (block.getMaterial().doesBlockMovement()) {
			return true;
		} else {
			ItemStack itemStack = this.getInvStack(this.selectedSlot);
			return itemStack != null ? itemStack.isEffectiveOn(block) : false;
		}
	}

	public ItemStack getArmor(int slot) {
		return this.armor[slot];
	}

	public int getArmorProtectionValue() {
		int i = 0;

		for (int j = 0; j < this.armor.length; j++) {
			if (this.armor[j] != null && this.armor[j].getItem() instanceof ArmorItem) {
				int k = ((ArmorItem)this.armor[j].getItem()).protection;
				i += k;
			}
		}

		return i;
	}

	public void damageArmor(float armor) {
		armor /= 4.0F;
		if (armor < 1.0F) {
			armor = 1.0F;
		}

		for (int i = 0; i < this.armor.length; i++) {
			if (this.armor[i] != null && this.armor[i].getItem() instanceof ArmorItem) {
				this.armor[i].damage((int)armor, this.player);
				if (this.armor[i].count == 0) {
					this.armor[i] = null;
				}
			}
		}
	}

	public void dropAll() {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null) {
				this.player.dropStack(this.main[i], true, false);
				this.main[i] = null;
			}
		}

		for (int j = 0; j < this.armor.length; j++) {
			if (this.armor[j] != null) {
				this.player.dropStack(this.armor[j], true, false);
				this.armor[j] = null;
			}
		}
	}

	@Override
	public void markDirty() {
		this.dirty = true;
	}

	public void setCursorStack(ItemStack stack) {
		this.cursorStack = stack;
	}

	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.player.removed ? false : !(player.squaredDistanceTo(this.player) > 64.0);
	}

	public boolean contains(ItemStack stack) {
		for (int i = 0; i < this.armor.length; i++) {
			if (this.armor[i] != null && this.armor[i].equalsIgnoreNbt(stack)) {
				return true;
			}
		}

		for (int j = 0; j < this.main.length; j++) {
			if (this.main[j] != null && this.main[j].equalsIgnoreNbt(stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void onInvOpen(PlayerEntity player) {
	}

	@Override
	public void onInvClose(PlayerEntity player) {
	}

	@Override
	public boolean isValidInvStack(int slot, ItemStack stack) {
		return true;
	}

	public void copy(PlayerInventory inventory) {
		for (int i = 0; i < this.main.length; i++) {
			this.main[i] = ItemStack.copyOf(inventory.main[i]);
		}

		for (int j = 0; j < this.armor.length; j++) {
			this.armor[j] = ItemStack.copyOf(inventory.armor[j]);
		}

		this.selectedSlot = inventory.selectedSlot;
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
	public void clear() {
		for (int i = 0; i < this.main.length; i++) {
			this.main[i] = null;
		}

		for (int j = 0; j < this.armor.length; j++) {
			this.armor[j] = null;
		}
	}
}
