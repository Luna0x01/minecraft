package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.fluid.FluidState;

public enum class_4079 {
	NEVER(fluidState -> false),
	SOURCE_ONLY(FluidState::isStill),
	ALWAYS(fluidState -> !fluidState.isEmpty());

	public final Predicate<FluidState> field_19815;

	private class_4079(Predicate<FluidState> predicate) {
		this.field_19815 = predicate;
	}
}
