package net.minecraft;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public final class class_3762 {
	public static <T> class_3761<T> method_16948(class_3761<T> arg) {
		return new class_3762.class_3763<>(arg);
	}

	public static <T> class_3761<T> method_16950(class_3761<? super T>... args) {
		return new class_3762.class_3764<>(method_16949(args));
	}

	private static <T> List<T> method_16949(T... objects) {
		return method_16951(Arrays.asList(objects));
	}

	private static <T> List<T> method_16951(Iterable<T> iterable) {
		List<T> list = Lists.newArrayList();

		for (T object : iterable) {
			list.add(Preconditions.checkNotNull(object));
		}

		return list;
	}

	static class class_3763<T> implements class_3761<T> {
		private final class_3761<T> field_18701;

		class_3763(class_3761<T> arg) {
			this.field_18701 = (class_3761<T>)Preconditions.checkNotNull(arg);
		}

		@Override
		public boolean test(@Nullable T object, BlockView blockView, BlockPos blockPos) {
			return !this.field_18701.test(object, blockView, blockPos);
		}
	}

	static class class_3764<T> implements class_3761<T> {
		private final List<? extends class_3761<? super T>> field_18702;

		private class_3764(List<? extends class_3761<? super T>> list) {
			this.field_18702 = list;
		}

		@Override
		public boolean test(@Nullable T object, BlockView blockView, BlockPos blockPos) {
			for (int i = 0; i < this.field_18702.size(); i++) {
				if (((class_3761)this.field_18702.get(i)).test(object, blockView, blockPos)) {
					return true;
				}
			}

			return false;
		}
	}
}
