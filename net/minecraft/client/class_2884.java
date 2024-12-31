package net.minecraft.client;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

public class class_2884 implements class_2880 {
	final Iterable<class_2880> field_13590;

	public class_2884(Iterable<class_2880> iterable) {
		this.field_13590 = iterable;
	}

	@Override
	public Predicate<BlockState> method_12379(StateManager stateManager) {
		return Predicates.or(Iterables.transform(this.field_13590, new Function<class_2880, Predicate<BlockState>>() {
			@Nullable
			public Predicate<BlockState> apply(@Nullable class_2880 arg) {
				return arg == null ? null : arg.method_12379(stateManager);
			}
		}));
	}
}
