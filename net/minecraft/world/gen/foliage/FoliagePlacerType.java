package net.minecraft.world.gen.foliage;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class FoliagePlacerType<P extends FoliagePlacer> {
	public static final FoliagePlacerType<BlobFoliagePlacer> field_21299 = register("blob_foliage_placer", BlobFoliagePlacer::new);
	public static final FoliagePlacerType<SpruceFoliagePlacer> field_21300 = register("spruce_foliage_placer", SpruceFoliagePlacer::new);
	public static final FoliagePlacerType<PineFoliagePlacer> field_21301 = register("pine_foliage_placer", PineFoliagePlacer::new);
	public static final FoliagePlacerType<AcaciaFoliagePlacer> field_21302 = register("acacia_foliage_placer", AcaciaFoliagePlacer::new);
	private final Function<Dynamic<?>, P> deserializer;

	private static <P extends FoliagePlacer> FoliagePlacerType<P> register(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.field_21447, string, new FoliagePlacerType<>(function));
	}

	private FoliagePlacerType(Function<Dynamic<?>, P> function) {
		this.deserializer = function;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.deserializer.apply(dynamic);
	}
}
