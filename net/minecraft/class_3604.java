package net.minecraft;

import net.minecraft.util.math.BlockPos;

public interface class_3604<T> {
	boolean method_16417(BlockPos blockPos, T object);

	default void schedule(BlockPos blockPos, T object, int i) {
		this.method_16419(blockPos, object, i, class_3605.NORMAL);
	}

	void method_16419(BlockPos blockPos, T object, int i, class_3605 arg);

	boolean method_16420(BlockPos blockPos, T object);
}
