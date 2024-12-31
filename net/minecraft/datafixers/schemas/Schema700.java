package net.minecraft.datafixers.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class Schema700 extends Schema {
	public Schema700(int i, Schema schema) {
		super(i, schema);
	}

	protected static void method_5288(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
		schema.register(map, string, () -> Schema100.method_5196(schema));
	}

	public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
		method_5288(schema, map, "ElderGuardian");
		return map;
	}
}
