package net.minecraft.datafixer.schema;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema1801 extends IdentifierNormalizingSchema {
	public Schema1801(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_5283(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> Schema100.targetItems(schema));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_5283(schema, map, "minecraft:illager_beast");
		return map;
	}
}
