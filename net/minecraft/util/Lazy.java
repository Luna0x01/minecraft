package net.minecraft.util;

import java.util.function.Supplier;

public class Lazy<T> {
	private Supplier<T> field_22249;
	private T value;

	public Lazy(Supplier<T> supplier) {
		this.field_22249 = supplier;
	}

	public T get() {
		Supplier<T> supplier = this.field_22249;
		if (supplier != null) {
			this.value = (T)supplier.get();
			this.field_22249 = null;
		}

		return this.value;
	}
}
