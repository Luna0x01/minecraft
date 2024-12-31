package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class StateProviderType<P extends StateProvider> {
	public static final StateProviderType<SimpleStateProvider> field_21305 = register("simple_state_provider", SimpleStateProvider::new);
	public static final StateProviderType<WeightedStateProvider> field_21306 = register("weighted_state_provider", WeightedStateProvider::new);
	public static final StateProviderType<PlainsFlowerStateProvider> field_21307 = register("plain_flower_provider", PlainsFlowerStateProvider::new);
	public static final StateProviderType<ForestFlowerStateProvider> field_21308 = register("forest_flower_provider", ForestFlowerStateProvider::new);
	private final Function<Dynamic<?>, P> configDeserializer;

	private static <P extends StateProvider> StateProviderType<P> register(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.field_21445, string, new StateProviderType<>(function));
	}

	private StateProviderType(Function<Dynamic<?>, P> function) {
		this.configDeserializer = function;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.configDeserializer.apply(dynamic);
	}
}
