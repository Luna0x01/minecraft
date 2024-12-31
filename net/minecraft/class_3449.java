package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3449 extends class_3415 {
	public class_3449(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_15435(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> DSL.optionalFields("Items", DSL.list(class_3402.field_16592.in(schema))));
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		method_15435(schema, map, "minecraft:shulker_box");
		return map;
	}
}
