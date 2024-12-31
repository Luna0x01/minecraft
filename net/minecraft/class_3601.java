package net.minecraft;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

public interface class_3601 {
	@Nullable
	class_4070 method_16399();

	@Nullable
	default <T extends PersistentState> T method_16398(DimensionType dimensionType, Function<String, T> function, String string) {
		class_4070 lv = this.method_16399();
		return lv == null ? null : lv.method_17977(dimensionType, function, string);
	}

	default void method_16397(DimensionType dimensionType, String string, PersistentState persistentState) {
		class_4070 lv = this.method_16399();
		if (lv != null) {
			lv.method_17976(dimensionType, string, persistentState);
		}
	}

	default int method_16396(DimensionType dimensionType, String string) {
		return this.method_16399().method_270(dimensionType, string);
	}
}
