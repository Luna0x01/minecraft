package net.minecraft.inventory;

import javax.annotation.concurrent.Immutable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

@Immutable
public class ContainerLock {
	public static final ContainerLock EMPTY = new ContainerLock("");
	private final String key;

	public ContainerLock(String key) {
		this.key = key;
	}

	public boolean canOpen(ItemStack stack) {
		return this.key.isEmpty() || !stack.isEmpty() && stack.hasCustomName() && this.key.equals(stack.getName().getString());
	}

	public void toTag(CompoundTag tag) {
		if (!this.key.isEmpty()) {
			tag.putString("Lock", this.key);
		}
	}

	public static ContainerLock fromTag(CompoundTag tag) {
		return tag.contains("Lock", 8) ? new ContainerLock(tag.getString("Lock")) : EMPTY;
	}
}
