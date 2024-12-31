package net.minecraft.text;

import javax.annotation.Nullable;

public interface Nameable {
	Text method_15540();

	boolean hasCustomName();

	default Text getName() {
		return this.method_15540();
	}

	@Nullable
	Text method_15541();
}
