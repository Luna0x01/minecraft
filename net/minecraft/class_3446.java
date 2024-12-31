package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3446 extends Schema {
	public class_3446(int i, Schema schema) {
		super(i, schema);
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		map.remove("EntityHorse");
		schema.register(
			map,
			"Horse",
			() -> DSL.optionalFields("ArmorItem", class_3402.field_16592.in(schema), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema))
		);
		schema.register(
			map,
			"Donkey",
			() -> DSL.optionalFields(
					"Items", DSL.list(class_3402.field_16592.in(schema)), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		schema.register(
			map,
			"Mule",
			() -> DSL.optionalFields(
					"Items", DSL.list(class_3402.field_16592.in(schema)), "SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)
				)
		);
		schema.register(map, "ZombieHorse", () -> DSL.optionalFields("SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)));
		schema.register(map, "SkeletonHorse", () -> DSL.optionalFields("SaddleItem", class_3402.field_16592.in(schema), class_3416.method_15287(schema)));
		return map;
	}
}
