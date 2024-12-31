package net.minecraft.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.class_2960;
import net.minecraft.class_3175;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;

public class PlayerInventory implements Inventory {
	public final DefaultedList<ItemStack> field_15082 = DefaultedList.ofSize(36, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> field_15083 = DefaultedList.ofSize(4, ItemStack.EMPTY);
	public final DefaultedList<ItemStack> field_15084 = DefaultedList.ofSize(1, ItemStack.EMPTY);
	private final List<DefaultedList<ItemStack>> field_15085 = ImmutableList.of(this.field_15082, this.field_15083, this.field_15084);
	public int selectedSlot;
	public PlayerEntity player;
	private ItemStack cursorStack = ItemStack.EMPTY;
	private int field_15624;

	public PlayerInventory(PlayerEntity playerEntity) {
		this.player = playerEntity;
	}

	public ItemStack getMainHandStack() {
		return method_13258(this.selectedSlot) ? this.field_15082.get(this.selectedSlot) : ItemStack.EMPTY;
	}

	public static int getHotbarSize() {
		return 9;
	}

	private boolean method_13251(ItemStack itemStack, ItemStack itemStack2) {
		return !itemStack.isEmpty()
			&& this.method_13254(itemStack, itemStack2)
			&& itemStack.isStackable()
			&& itemStack.getCount() < itemStack.getMaxCount()
			&& itemStack.getCount() < this.getInvMaxStackAmount();
	}

	private boolean method_13254(ItemStack itemStack, ItemStack itemStack2) {
		return itemStack.getItem() == itemStack2.getItem() && ItemStack.equalsIgnoreDamage(itemStack, itemStack2);
	}

	public int getEmptySlot() {
		for (int i = 0; i < this.field_15082.size(); i++) {
			if (this.field_15082.get(i).isEmpty()) {
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
				if (!this.field_15082.get(this.selectedSlot).isEmpty()) {
					int j = this.getEmptySlot();
					if (j != -1) {
						this.field_15082.set(j, this.field_15082.get(this.selectedSlot));
					}
				}

				this.field_15082.set(this.selectedSlot, itemStack);
			} else {
				this.method_13256(i);
			}
		}
	}

	public void method_13256(int i) {
		this.selectedSlot = this.method_13259();
		ItemStack itemStack = this.field_15082.get(this.selectedSlot);
		this.field_15082.set(this.selectedSlot, this.field_15082.get(i));
		this.field_15082.set(i, itemStack);
	}

	public static boolean method_13258(int i) {
		return i >= 0 && i < 9;
	}

	public int method_13253(ItemStack itemStack) {
		for (int i = 0; i < this.field_15082.size(); i++) {
			if (!this.field_15082.get(i).isEmpty() && this.method_13254(itemStack, this.field_15082.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public int method_14151(ItemStack itemStack) {
		for (int i = 0; i < this.field_15082.size(); i++) {
			ItemStack itemStack2 = this.field_15082.get(i);
			if (!this.field_15082.get(i).isEmpty()
				&& this.method_13254(itemStack, this.field_15082.get(i))
				&& !this.field_15082.get(i).isDamaged()
				&& !itemStack2.hasEnchantments()
				&& !itemStack2.hasCustomName()) {
				return i;
			}
		}

		return -1;
	}

	public int method_13259() {
		for (int i = 0; i < 9; i++) {
			int j = (this.selectedSlot + i) % 9;
			if (this.field_15082.get(j).isEmpty()) {
				return j;
			}
		}

		for (int k = 0; k < 9; k++) {
			int l = (this.selectedSlot + k) % 9;
			if (!this.field_15082.get(l).hasEnchantments()) {
				return l;
			}
		}

		return this.selectedSlot;
	}

	public void method_15920(double d) {
		if (d > 0.0) {
			d = 1.0;
		}

		if (d < 0.0) {
			d = -1.0;
		}

		this.selectedSlot = (int)((double)this.selectedSlot - d);

		while (this.selectedSlot < 0) {
			this.selectedSlot += 9;
		}

		while (this.selectedSlot >= 9) {
			this.selectedSlot -= 9;
		}
	}

	public int method_15922(Predicate<ItemStack> predicate, int i) {
		int j = 0;

		for (int k = 0; k < this.getInvSize(); k++) {
			ItemStack itemStack = this.getInvStack(k);
			if (!itemStack.isEmpty() && predicate.test(itemStack)) {
				int l = i <= 0 ? itemStack.getCount() : Math.min(i - j, itemStack.getCount());
				j += l;
				if (i != 0) {
					itemStack.decrement(l);
					if (itemStack.isEmpty()) {
						this.setInvStack(k, ItemStack.EMPTY);
					}

					if (i > 0 && j >= i) {
						return j;
					}
				}
			}
		}

		if (!this.cursorStack.isEmpty() && predicate.test(this.cursorStack)) {
			int m = i <= 0 ? this.cursorStack.getCount() : Math.min(i - j, this.cursorStack.getCount());
			j += m;
			if (i != 0) {
				this.cursorStack.decrement(m);
				if (this.cursorStack.isEmpty()) {
					this.cursorStack = ItemStack.EMPTY;
				}

				if (i > 0 && j >= i) {
					return j;
				}
			}
		}

		return j;
	}

	private int method_3140(ItemStack itemStack) {
		int i = this.getSlotWithItemStack(itemStack);
		if (i == -1) {
			i = this.getEmptySlot();
		}

		return i == -1 ? itemStack.getCount() : this.method_14152(i, itemStack);
	}

	private int method_14152(int i, ItemStack itemStack) {
		Item item = itemStack.getItem();
		int j = itemStack.getCount();
		ItemStack itemStack2 = this.getInvStack(i);
		if (itemStack2.isEmpty()) {
			itemStack2 = new ItemStack(item, 0);
			if (itemStack.hasNbt()) {
				itemStack2.setNbt(itemStack.getNbt().copy());
			}

			this.setInvStack(i, itemStack2);
		}

		int k = j;
		if (j > itemStack2.getMaxCount() - itemStack2.getCount()) {
			k = itemStack2.getMaxCount() - itemStack2.getCount();
		}

		if (k > this.getInvMaxStackAmount() - itemStack2.getCount()) {
			k = this.getInvMaxStackAmount() - itemStack2.getCount();
		}

		if (k == 0) {
			return j;
		} else {
			j -= k;
			itemStack2.increment(k);
			itemStack2.setPickupTick(5);
			return j;
		}
	}

	public int getSlotWithItemStack(ItemStack stack) {
		if (this.method_13251(this.getInvStack(this.selectedSlot), stack)) {
			return this.selectedSlot;
		} else if (this.method_13251(this.getInvStack(40), stack)) {
			return 40;
		} else {
			for (int i = 0; i < this.field_15082.size(); i++) {
				if (this.method_13251(this.field_15082.get(i), stack)) {
					return i;
				}
			}

			return -1;
		}
	}

	public void updateItems() {
		for (DefaultedList<ItemStack> defaultedList : this.field_15085) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (!defaultedList.get(i).isEmpty()) {
					defaultedList.get(i).inventoryTick(this.player.world, this.player, i, this.selectedSlot == i);
				}
			}
		}
	}

	public boolean insertStack(ItemStack itemStack) {
		return this.method_14150(-1, itemStack);
	}

	public boolean method_14150(int i, ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return false;
		} else {
			try {
				if (itemStack.isDamaged()) {
					if (i == -1) {
						i = this.getEmptySlot();
					}

					if (i >= 0) {
						this.field_15082.set(i, itemStack.copy());
						this.field_15082.get(i).setPickupTick(5);
						itemStack.setCount(0);
						return true;
					} else if (this.player.abilities.creativeMode) {
						itemStack.setCount(0);
						return true;
					} else {
						return false;
					}
				} else {
					int j;
					do {
						j = itemStack.getCount();
						if (i == -1) {
							itemStack.setCount(this.method_3140(itemStack));
						} else {
							itemStack.setCount(this.method_14152(i, itemStack));
						}
					} while (!itemStack.isEmpty() && itemStack.getCount() < j);

					if (itemStack.getCount() == j && this.player.abilities.creativeMode) {
						itemStack.setCount(0);
						return true;
					} else {
						return itemStack.getCount() < j;
					}
				}
			} catch (Throwable var6) {
				CrashReport crashReport = CrashReport.create(var6, "Adding item to inventory");
				CrashReportSection crashReportSection = crashReport.addElement("Item being added");
				crashReportSection.add("Item ID", Item.getRawId(itemStack.getItem()));
				crashReportSection.add("Item data", itemStack.getDamage());
				crashReportSection.add("Item name", (CrashCallable<String>)(() -> itemStack.getName().getString()));
				throw new CrashException(crashReport);
			}
		}
	}

	public void method_14149(World world, ItemStack itemStack) {
		if (!world.isClient) {
			while (!itemStack.isEmpty()) {
				int i = this.getSlotWithItemStack(itemStack);
				if (i == -1) {
					i = this.getEmptySlot();
				}

				if (i == -1) {
					this.player.dropItem(itemStack, false);
					break;
				}

				int j = itemStack.getMaxCount() - this.getInvStack(i).getCount();
				if (this.method_14150(i, itemStack.split(j))) {
					((ServerPlayerEntity)this.player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, i, this.getInvStack(i)));
				}
			}
		}
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.field_15085) {
			if (slot < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			slot -= defaultedList.size();
		}

		return list != null && !((ItemStack)list.get(slot)).isEmpty() ? class_2960.method_13926(list, slot, amount) : ItemStack.EMPTY;
	}

	public void method_13257(ItemStack itemStack) {
		for (DefaultedList<ItemStack> defaultedList : this.field_15085) {
			for (int i = 0; i < defaultedList.size(); i++) {
				if (defaultedList.get(i) == itemStack) {
					defaultedList.set(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

	@Override
	public ItemStack removeInvStack(int slot) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.field_15085) {
			if (slot < defaultedList2.size()) {
				defaultedList = defaultedList2;
				break;
			}

			slot -= defaultedList2.size();
		}

		if (defaultedList != null && !defaultedList.get(slot).isEmpty()) {
			ItemStack itemStack = defaultedList.get(slot);
			defaultedList.set(slot, ItemStack.EMPTY);
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInvStack(int slot, ItemStack stack) {
		DefaultedList<ItemStack> defaultedList = null;

		for (DefaultedList<ItemStack> defaultedList2 : this.field_15085) {
			if (slot < defaultedList2.size()) {
				defaultedList = defaultedList2;
				break;
			}

			slot -= defaultedList2.size();
		}

		if (defaultedList != null) {
			defaultedList.set(slot, stack);
		}
	}

	public float method_13252(BlockState blockState) {
		return this.field_15082.get(this.selectedSlot).getBlockBreakingSpeed(blockState);
	}

	public NbtList serialize(NbtList nbt) {
		for (int i = 0; i < this.field_15082.size(); i++) {
			if (!this.field_15082.get(i).isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				this.field_15082.get(i).toNbt(nbtCompound);
				nbt.add((NbtElement)nbtCompound);
			}
		}

		for (int j = 0; j < this.field_15083.size(); j++) {
			if (!this.field_15083.get(j).isEmpty()) {
				NbtCompound nbtCompound2 = new NbtCompound();
				nbtCompound2.putByte("Slot", (byte)(j + 100));
				this.field_15083.get(j).toNbt(nbtCompound2);
				nbt.add((NbtElement)nbtCompound2);
			}
		}

		for (int k = 0; k < this.field_15084.size(); k++) {
			if (!this.field_15084.get(k).isEmpty()) {
				NbtCompound nbtCompound3 = new NbtCompound();
				nbtCompound3.putByte("Slot", (byte)(k + 150));
				this.field_15084.get(k).toNbt(nbtCompound3);
				nbt.add((NbtElement)nbtCompound3);
			}
		}

		return nbt;
	}

	public void deserialize(NbtList nbtList) {
		this.field_15082.clear();
		this.field_15083.clear();
		this.field_15084.clear();

		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			ItemStack itemStack = ItemStack.from(nbtCompound);
			if (!itemStack.isEmpty()) {
				if (j >= 0 && j < this.field_15082.size()) {
					this.field_15082.set(j, itemStack);
				} else if (j >= 100 && j < this.field_15083.size() + 100) {
					this.field_15083.set(j - 100, itemStack);
				} else if (j >= 150 && j < this.field_15084.size() + 150) {
					this.field_15084.set(j - 150, itemStack);
				}
			}
		}
	}

	@Override
	public int getInvSize() {
		return this.field_15082.size() + this.field_15083.size() + this.field_15084.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemStack : this.field_15082) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemStack2 : this.field_15083) {
			if (!itemStack2.isEmpty()) {
				return false;
			}
		}

		for (ItemStack itemStack3 : this.field_15084) {
			if (!itemStack3.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getInvStack(int slot) {
		List<ItemStack> list = null;

		for (DefaultedList<ItemStack> defaultedList : this.field_15085) {
			if (slot < defaultedList.size()) {
				list = defaultedList;
				break;
			}

			slot -= defaultedList.size();
		}

		return list == null ? ItemStack.EMPTY : (ItemStack)list.get(slot);
	}

	@Override
	public Text method_15540() {
		return new TranslatableText("container.inventory");
	}

	@Nullable
	@Override
	public Text method_15541() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 64;
	}

	public boolean method_13255(BlockState blockState) {
		return this.getInvStack(this.selectedSlot).method_11396(blockState);
	}

	public ItemStack getArmor(int slot) {
		return this.field_15083.get(slot);
	}

	public void damageArmor(float armor) {
		if (!(armor <= 0.0F)) {
			armor /= 4.0F;
			if (armor < 1.0F) {
				armor = 1.0F;
			}

			for (int i = 0; i < this.field_15083.size(); i++) {
				ItemStack itemStack = this.field_15083.get(i);
				if (itemStack.getItem() instanceof ArmorItem) {
					itemStack.damage((int)armor, this.player);
				}
			}
		}
	}

	public void dropAll() {
		for (List<ItemStack> list : this.field_15085) {
			for (int i = 0; i < list.size(); i++) {
				ItemStack itemStack = (ItemStack)list.get(i);
				if (!itemStack.isEmpty()) {
					this.player.dropStack(itemStack, true, false);
					list.set(i, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public void markDirty() {
		this.field_15624++;
	}

	public int method_14153() {
		return this.field_15624;
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
		for (List<ItemStack> list : this.field_15085) {
			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && itemStack.equalsIgnoreNbt(stack)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean method_15923(Tag<Item> tag) {
		for (List<ItemStack> list : this.field_15085) {
			for (ItemStack itemStack : list) {
				if (!itemStack.isEmpty() && tag.contains(itemStack.getItem())) {
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
		for (List<ItemStack> list : this.field_15085) {
			list.clear();
		}
	}

	public void method_15921(class_3175 arg) {
		for (ItemStack itemStack : this.field_15082) {
			arg.method_14170(itemStack);
		}
	}
}
