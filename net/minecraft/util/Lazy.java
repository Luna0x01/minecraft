package net.minecraft.util;

public abstract class Lazy<T> {
	private T value;
	private boolean initialized;

	public T get() {
		if (!this.initialized) {
			this.initialized = true;
			this.value = this.create();
		}

		return this.value;
	}

	protected abstract T create();
}
