package net.minecraft.inventory;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Immutable
public class ContainerLock {
	public static final ContainerLock EMPTY = new ContainerLock("");
	private final String key;

	public ContainerLock(String string) {
		this.key = string;
	}

	public boolean canOpen(ItemStack itemStack) {
		return this.key.isEmpty() || !itemStack.isEmpty() && itemStack.hasCustomName() && this.key.equals(itemStack.getName().getString());
	}

	public void toTag(CompoundTag compoundTag) {
		if (!this.key.isEmpty()) {
			compoundTag.putString("Lock", this.key);
		}
	}

	public static ContainerLock fromTag(CompoundTag compoundTag) {
		return compoundTag.contains("Lock", 8) ? new ContainerLock(compoundTag.getString("Lock")) : EMPTY;
	}
}
