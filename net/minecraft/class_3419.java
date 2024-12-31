package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3419 extends Schema {
	public class_3419(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true,
			class_3402.field_16599,
			() -> DSL.optionalFields(
					"SpawnPotentials", DSL.list(DSL.fields("Entity", class_3402.field_16595.in(schema))), "SpawnData", class_3402.field_16595.in(schema)
				)
		);
	}
}
