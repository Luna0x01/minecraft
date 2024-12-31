package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3437 extends class_3415 {
	public class_3437(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_15384(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> class_3416.method_15287(schema));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_15384(schema, map, "minecraft:turtle");
		method_15384(schema, map, "minecraft:cod_mob");
		method_15384(schema, map, "minecraft:tropical_fish");
		method_15384(schema, map, "minecraft:salmon_mob");
		method_15384(schema, map, "minecraft:puffer_fish");
		method_15384(schema, map, "minecraft:phantom");
		method_15384(schema, map, "minecraft:dolphin");
		method_15384(schema, map, "minecraft:drowned");
		schema.register(map, "minecraft:trident", string -> DSL.optionalFields("inBlockState", class_3402.field_16593.in(schema)));
		return map;
	}
}
