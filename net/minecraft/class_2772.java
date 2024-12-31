package net.minecraft;

import net.minecraft.world.PersistentState;

public class class_2772 implements Runnable {
	private final PersistentState field_13094;

	public class_2772(PersistentState persistentState) {
		this.field_13094 = persistentState;
	}

	public void run() {
		this.field_13094.markDirty();
	}
}
