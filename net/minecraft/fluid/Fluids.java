package net.minecraft.fluid;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.Bootstrap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Fluids {
	private static final Set<Fluid> FLUIDS;
	public static final Fluid EMPTY;
	public static final FlowableFluid FLOWING_WATER;
	public static final FlowableFluid WATER;
	public static final FlowableFluid FLOWING_LAVA;
	public static final FlowableFluid LAVA;

	private static Fluid register(String id) {
		Fluid fluid = Registry.FLUID.get(new Identifier(id));
		if (!FLUIDS.add(fluid)) {
			throw new IllegalStateException("Invalid Fluid requested: " + id);
		} else {
			return fluid;
		}
	}

	static {
		if (!Bootstrap.isInitialized()) {
			throw new RuntimeException("Accessed Fluids before Bootstrap!");
		} else {
			FLUIDS = Sets.newHashSet(new Fluid[]{(Fluid)null});
			EMPTY = register("empty");
			FLOWING_WATER = (FlowableFluid)register("flowing_water");
			WATER = (FlowableFluid)register("water");
			FLOWING_LAVA = (FlowableFluid)register("flowing_lava");
			LAVA = (FlowableFluid)register("lava");
			FLUIDS.clear();
		}
	}
}
