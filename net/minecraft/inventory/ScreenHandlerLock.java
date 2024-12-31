package net.minecraft.inventory;

import net.minecraft.nbt.NbtCompound;

public class ScreenHandlerLock {
	public static final ScreenHandlerLock NONE = new ScreenHandlerLock("");
	private final String key;

	public ScreenHandlerLock(String string) {
		this.key = string;
	}

	public boolean hasLock() {
		return this.key == null || this.key.isEmpty();
	}

	public String getKey() {
		return this.key;
	}

	public void toNbt(NbtCompound nbt) {
		nbt.putString("Lock", this.key);
	}

	public static ScreenHandlerLock fromNbt(NbtCompound nbt) {
		if (nbt.contains("Lock", 8)) {
			String string = nbt.getString("Lock");
			return new ScreenHandlerLock(string);
		} else {
			return NONE;
		}
	}
}
