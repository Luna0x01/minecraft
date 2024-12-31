package net.minecraft;

import java.util.function.Function;
import net.minecraft.util.math.BlockPos;

public class class_4442<T> implements class_3604<T> {
	private final Function<BlockPos, class_3604<T>> field_21861;

	public class_4442(Function<BlockPos, class_3604<T>> function) {
		this.field_21861 = function;
	}

	@Override
	public boolean method_16417(BlockPos blockPos, T object) {
		return ((class_3604)this.field_21861.apply(blockPos)).method_16417(blockPos, object);
	}

	@Override
	public void method_16419(BlockPos blockPos, T object, int i, class_3605 arg) {
		((class_3604)this.field_21861.apply(blockPos)).method_16419(blockPos, object, i, arg);
	}

	@Override
	public boolean method_16420(BlockPos blockPos, T object) {
		return false;
	}
}
