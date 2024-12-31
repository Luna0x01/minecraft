package net.minecraft.fluid;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.util.registry.Registry;

public class Fluids {
	public static final Fluid field_15906 = register("empty", new EmptyFluid());
	public static final BaseFluid FLOWING_WATER = register("flowing_water", new WaterFluid.Flowing());
	public static final BaseFluid WATER = register("water", new WaterFluid.Still());
	public static final BaseFluid FLOWING_LAVA = register("flowing_lava", new LavaFluid.Flowing());
	public static final BaseFluid LAVA = register("lava", new LavaFluid.Still());

	private static <T extends Fluid> T register(String string, T fluid) {
		return Registry.register(Registry.field_11154, string, fluid);
	}

	static {
		for (Fluid fluid : Registry.field_11154) {
			UnmodifiableIterator var2 = fluid.getStateManager().getStates().iterator();

			while (var2.hasNext()) {
				FluidState fluidState = (FluidState)var2.next();
				Fluid.STATE_IDS.add(fluidState);
			}
		}
	}
}
