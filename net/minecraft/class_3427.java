package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3427 extends class_3415 {
	public class_3427(int i, Schema schema) {
		super(i, schema);
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		schema.registerSimple(map, "minecraft:egg");
		schema.registerSimple(map, "minecraft:ender_pearl");
		schema.registerSimple(map, "minecraft:fireball");
		schema.register(map, "minecraft:potion", string -> DSL.optionalFields("Potion", class_3402.field_16592.in(schema)));
		schema.registerSimple(map, "minecraft:small_fireball");
		schema.registerSimple(map, "minecraft:snowball");
		schema.registerSimple(map, "minecraft:wither_skull");
		schema.registerSimple(map, "minecraft:xp_bottle");
		schema.register(map, "minecraft:arrow", () -> DSL.optionalFields("inBlockState", class_3402.field_16593.in(schema)));
		schema.register(map, "minecraft:enderman", () -> DSL.optionalFields("carriedBlockState", class_3402.field_16593.in(schema), class_3416.method_15287(schema)));
		schema.register(
			map,
			"minecraft:falling_block",
			() -> DSL.optionalFields("BlockState", class_3402.field_16593.in(schema), "TileEntityData", class_3402.field_16591.in(schema))
		);
		schema.register(map, "minecraft:spectral_arrow", () -> DSL.optionalFields("inBlockState", class_3402.field_16593.in(schema)));
		schema.register(
			map,
			"minecraft:chest_minecart",
			() -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		schema.register(map, "minecraft:commandblock_minecart", () -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		schema.register(map, "minecraft:furnace_minecart", () -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		schema.register(
			map,
			"minecraft:hopper_minecart",
			() -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema), "Items", DSL.list(class_3402.field_16592.in(schema)))
		);
		schema.register(map, "minecraft:minecart", () -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		schema.register(
			map, "minecraft:spawner_minecart", () -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema), class_3402.field_16599.in(schema))
		);
		schema.register(map, "minecraft:tnt_minecart", () -> DSL.optionalFields("DisplayState", class_3402.field_16593.in(schema)));
		return map;
	}
}
