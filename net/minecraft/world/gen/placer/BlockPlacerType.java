package net.minecraft.world.gen.placer;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class BlockPlacerType<P extends BlockPlacer> {
	public static final BlockPlacerType<SimpleBlockPlacer> field_21223 = register("simple_block_placer", SimpleBlockPlacer::new);
	public static final BlockPlacerType<DoublePlantPlacer> field_21224 = register("double_plant_placer", DoublePlantPlacer::new);
	public static final BlockPlacerType<ColumnPlacer> field_21225 = register("column_placer", ColumnPlacer::new);
	private final Function<Dynamic<?>, P> deserializer;

	private static <P extends BlockPlacer> BlockPlacerType<P> register(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.field_21446, string, new BlockPlacerType<>(function));
	}

	private BlockPlacerType(Function<Dynamic<?>, P> function) {
		this.deserializer = function;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.deserializer.apply(dynamic);
	}
}
