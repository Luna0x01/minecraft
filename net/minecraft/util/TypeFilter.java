package net.minecraft.util;

import javax.annotation.Nullable;

public interface TypeFilter<B, T extends B> {
	static <B, T extends B> TypeFilter<B, T> instanceOf(Class<T> cls) {
		return new TypeFilter<B, T>() {
			@Nullable
			@Override
			public T downcast(B obj) {
				return (T)(cls.isInstance(obj) ? obj : null);
			}

			@Override
			public Class<? extends B> getBaseClass() {
				return cls;
			}
		};
	}

	@Nullable
	T downcast(B obj);

	Class<? extends B> getBaseClass();
}
