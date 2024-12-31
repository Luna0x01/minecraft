package net.minecraft.client;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;

public interface class_2880 {
	class_2880 field_13576 = new class_2880() {
		@Override
		public Predicate<BlockState> method_12379(StateManager stateManager) {
			return new Predicate<BlockState>() {
				public boolean apply(@Nullable BlockState blockState) {
					return true;
				}
			};
		}
	};
	class_2880 field_13577 = new class_2880() {
		@Override
		public Predicate<BlockState> method_12379(StateManager stateManager) {
			return new Predicate<BlockState>() {
				public boolean apply(@Nullable BlockState blockState) {
					return false;
				}
			};
		}
	};

	Predicate<BlockState> method_12379(StateManager stateManager);
}
