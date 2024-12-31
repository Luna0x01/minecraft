package net.minecraft.world;

import net.minecraft.nbt.NbtCompound;

public abstract class PersistentState {
	public final String id;
	private boolean dirty;

	public PersistentState(String string) {
		this.id = string;
	}

	public abstract void fromNbt(NbtCompound nbt);

	public abstract NbtCompound toNbt(NbtCompound nbt);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return this.dirty;
	}
}
