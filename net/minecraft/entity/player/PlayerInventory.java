package net.minecraft.entity.player;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.block.BlockState;
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
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class PlayerInventory implements Inventory {
	public final ItemStack[] main = new ItemStack[36];
	public final ItemStack[] armor = new ItemStack[4];
	public final ItemStack[] field_14790 = new ItemStack[1];
	private final ItemStack[][] field_14791 = new ItemStack[][]{this.main, this.armor, this.field_14790};
	public int selectedSlot;
	public PlayerEntity player;
	private ItemStack cursorStack;
	public boolean dirty;

	public PlayerInventory(PlayerEntity playerEntity) {
		this.player = playerEntity;
	}

	@Nullable
	public ItemStack getMainHandStack() {
		return method_13258(this.selectedSlot) ? this.main[this.selectedSlot] : null;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private boolean method_13251(@Nullable ItemStack itemStack, ItemStack itemStack2) {
		return itemStack != null
			&& this.method_13254(itemStack, itemStack2)
			&& itemStack.isStackable()
			&& itemStack.count < itemStack.getMaxCount()
			&& itemStack.count < this.getInvMaxStackAmount();
	}

	private boolean method_13254(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem()
			&& (!itemStack.isUnbreakable() || itemStack.getData() == itemStack2.getData())
			&& ItemStack.equalsIgnoreDamage(itemStack, itemStack2);
	}

	public int getEmptySlot() {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] == null) {
				return i;
			}
		}

		return -1;
	}

	public void method_13250(ItemStack itemStack) {
		int i = this.method_13253(itemStack);
		if (method_13258(i)) {
			this.selectedSlot = i;
		} else {
			if (i == -1) {
				this.selectedSlot = this.method_13259();
				if (this.main[this.selectedSlot] != null) {
					int j = this.getEmptySlot();
					if (j != -1) {
						this.main[j] = this.main[this.selectedSlot];
					}
				}

				this.main[this.selectedSlot] = itemStack;
			} else {
				this.method_13256(i);
			}
		}
	}

	public void method_13256(int i) {
		this.selectedSlot = this.method_13259();
		ItemStack itemStack = this.main[this.selectedSlot];
		this.main[this.selectedSlot] = this.main[i];
		this.main[i] = itemStack;
	}

	public static boolean method_13258(int i) {
		return i >= 0 && i < 9;
	}

	public int method_13253(ItemStack itemStack) {
		for (int i = 0; i < this.main.length; i++) {
			if (this.main[i] != null && this.method_13254(itemStack, this.main[i])) {
				return i;
			}
		}

		return -1;
	}

	public int method_13259() {
		for (int i = 0; i < 9; i++) {
			int j = (this.selectedSlot + i) % 9;
			if (this.main[j] == null) {
				return j;
			}
		}

		for (int k = 0; k < 9; k++) {
			int l = (this.selectedSlot + k) % 9;
			if (!this.main[l].hasEnchantments()) {
				return l;
			}
		}

		return this.selectedSlot;
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

	public int method_11232(@Nullable Item item, int i, int j, @Nullable NbtCompound nbt) {
		int k = 0;

		for (int l = 0; l < this.getInvSize(); l++) {
			ItemStack itemStack = this.getInvStack(l);
			if (itemStack != null
				&& (item == null || itemStack.getItem() == item)
				&& (i <= -1 || itemStack.getData() == i)
				&& (nbt == null || NbtHelper.matches(nbt, itemStack.getNbt(), true))) {
				int m = j <= 0 ? itemStack.count : Math.min(j - k, itemStack.count);
				k += m;
				if (j != 0) {
					itemStack.count -= m;
					if (itemStack.count == 0) {
						this.setInvStack(l, null);
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

			if (nbt != null && !NbtHelper.matches(nbt, this.cursorStack.getNbt(), true)) {
				return k;
			}

			int n = j <= 0 ? this.cursorStack.count : Math.min(j - k, this.cursorStack.count);
			k += n;
			if (j != 0) {
				this.cursorStack.count -= n;
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
		if (j == -1) {
			j = this.getEmptySlot();
		}

		if (j == -1) {
			return i;
		} else {
			ItemStack itemStack2 = this.getInvStack(j);
			if (itemStack2 == null) {
				itemStack2 = new ItemStack(item, 0, itemStack.getData());
				if (itemStack.hasNbt()) {
					itemStack2.setNbt(itemStack.getNbt().copy());
				}

				this.setInvStack(j, itemStack2);
			}

			int k = i;
			if (i > itemStack2.getMaxCount() - itemStack2.count) {
				k = itemStack2.getMaxCount() - itemStack2.count;
			}

			if (k > this.getInvMaxStackAmount() - itemStack2.count) {
				k = this.getInvMaxStackAmount() - itemStack2.count;
			}

			if (k == 0) {
				return i;
			} else {
				i -= k;
				itemStack2.count += k;
				itemStack2.pickupTick = 5;
				return i;
			}
		}
	}

	private int getSlotWithItemStack(ItemStack stack) {
		if (this.method_13251(this.getInvStack(this.selectedSlot), stack)) {
			return this.selectedSlot;
		} else if (this.method_13251(this.getInvStack(40), stack)) {
			return 40;
		} else {
			for (int i = 0; i < this.main.length; i++) {
				if (this.method_13251(this.main[i], stack)) {
					return i;
				}
			}

			return -1;
		}
	}

	public void updateItems() {
		for (ItemStack[] itemStacks2 : this.field_14791) {
			for (int k = 0; k < itemStacks2.length; k++) {
				if (itemStacks2[k] != null) {
					itemStacks2[k].inventoryTick(this.player.world, this.player, k, this.selectedSlot == k);
				}
			}
		}
	}

	public boolean insertStack(@Nullable ItemStack itemStack) {
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
				crashReportSection.add("Item name", new CrashCallable<String>() {
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

	@Nullable
	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		ItemStack[] itemStacks = null;

		for (ItemStack[] itemStacks3 : this.field_14791) {
			if (slot < itemStacks3.length) {
				itemStacks = itemStacks3;
				break;
			}

			slot -= itemStacks3.length;
		}

		return itemStacks != null && itemStacks[slot] != null ? class_2960.method_12933(itemStacks, slot, amount) : null;
	}

	public void method_13257(ItemStack itemStack) {
		for (ItemStack[] itemStacks2 : this.field_14791) {
			for (int k = 0; k < itemStacks2.length; k++) {
				if (itemStacks2[k] == itemStack) {
					itemStacks2[k] = null;
					break;
				}
			}
		}
	}

	@Nullable
	@Override
	public ItemStack removeInvStack(int slot) {
		ItemStack[] itemStacks = null;

		for (ItemStack[] itemStacks3 : this.field_14791) {
			if (slot < itemStacks3.length) {
				itemStacks = itemStacks3;
				break;
			}

			slot -= itemStacks3.length;
		}

		if (itemStacks != null && itemStacks[slot] != null) {
			ItemStack itemStack = itemStacks[slot];
			itemStacks[slot] = null;
			return itemStack;
		} else {
			return null;
		}
	}

	@Override
	public void setInvStack(int slot, @Nullable ItemStack stack) {
		ItemStack[] itemStacks = null;

		for (ItemStack[] itemStacks3 : this.field_14791) {
			if (slot < itemStacks3.length) {
				itemStacks = itemStacks3;
				break;
			}

			slot -= itemStacks3.length;
		}

		if (itemStacks != null) {
			itemStacks[slot] = stack;
		}
	}

	public float method_13252(BlockState blockState) {
		float f = 1.0F;
		if (this.main[this.selectedSlot] != null) {
			f *= this.main[this.selectedSlot].getBlockBreakingSpeed(blockState);
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

		for (int k = 0; k < this.field_14790.length; k++) {
			if (this.field_14790[k] != null) {
				NbtCompound nbtCompound3 = new NbtCompound();
				nbtCompound3.putByte("Slot", (byte)(k + 150));
				this.field_14790[k].toNbt(nbtCompound3);
				nbt.add(nbtCompound3);
			}
		}

		return nbt;
	}

	public void deserialize(NbtList nbtList) {
		Arrays.fill(this.main, null);
		Arrays.fill(this.armor, null);
		Arrays.fill(this.field_14790, null);

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			ItemStack itemStack = ItemStack.fromNbt(nbtCompound);
			if (itemStack != null) {
				if (j >= 0 && j < this.main.length) {
					this.main[j] = itemStack;
				} else if (j >= 100 && j < this.armor.length + 100) {
					this.armor[j - 100] = itemStack;
				} else if (j >= 150 && j < this.field_14790.length + 150) {
					this.field_14790[j - 150] = itemStack;
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.main.length + this.armor.length + this.field_14790.length;
	}

	@Nullable
	@Override
	public ItemStack getInvStack(int slot) {
		ItemStack[] itemStacks = null;

		for (ItemStack[] itemStacks3 : this.field_14791) {
			if (slot < itemStacks3.length) {
				itemStacks = itemStacks3;
				break;
			}

			slot -= itemStacks3.length;
		}

		return itemStacks == null ? null : itemStacks[slot];
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

	public boolean method_13255(BlockState blockState) {
		if (blockState.getMaterial().doesBlockMovement()) {
			return true;
		} else {
			ItemStack itemStack = this.getInvStack(this.selectedSlot);
			return itemStack != null ? itemStack.method_11396(blockState) : false;
		}
	}

	public ItemStack getArmor(int slot) {
		return this.armor[slot];
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
		for (ItemStack[] itemStacks2 : this.field_14791) {
			for (int k = 0; k < itemStacks2.length; k++) {
				if (itemStacks2[k] != null) {
					this.player.dropStack(itemStacks2[k], true, false);
					itemStacks2[k] = null;
				}
			}
		}
	}

	@Override
	public void markDirty() {
		this.dirty = true;
	}

	public void setCursorStack(@Nullable ItemStack stack) {
		this.cursorStack = stack;
	}

	@Nullable
	public ItemStack getCursorStack() {
		return this.cursorStack;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return this.player.removed ? false : !(player.squaredDistanceTo(this.player) > 64.0);
	}

	public boolean contains(ItemStack stack) {
		for (ItemStack[] itemStacks2 : this.field_14791) {
			for (ItemStack itemStack : itemStacks2) {
				if (itemStack != null && itemStack.equalsIgnoreNbt(stack)) {
					return true;
				}
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
		for (int i = 0; i < this.getInvSize(); i++) {
			this.setInvStack(i, inventory.getInvStack(i));
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
		for (ItemStack[] itemStacks2 : this.field_14791) {
			for (int k = 0; k < itemStacks2.length; k++) {
				itemStacks2[k] = null;
			}
		}
	}
}
