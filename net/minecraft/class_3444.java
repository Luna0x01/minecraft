package net.minecraft;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3444 extends Schema {
	public class_3444(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_15390(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> class_3416.method_15287(schema));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_15390(schema, map, "WitherSkeleton");
		method_15390(schema, map, "Stray");
		return map;
	}
}
