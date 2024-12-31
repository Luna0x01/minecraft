package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.property.Property;

public class class_4025 extends class_3755<Fluid, FluidState> implements FluidState {
	public class_4025(Fluid fluid, ImmutableMap<Property<?>, Comparable<?>> immutableMap) {
		super(fluid, immutableMap);
	}

	@Override
	public Fluid getFluid() {
		return this.field_18688;
	}
}
