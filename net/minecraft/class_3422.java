package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3422 extends Schema {
	public class_3422(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_3402.field_16583,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", class_3402.field_16595.in(schema)),
					"Inventory",
					DSL.list(class_3402.field_16592.in(schema)),
					"EnderItems",
					DSL.list(class_3402.field_16592.in(schema))
				)
		);
		schema.registerType(
			true, class_3402.field_16595, () -> DSL.optionalFields("Passengers", DSL.list(class_3402.field_16595.in(schema)), class_3402.field_16596.in(schema))
		);
	}
}
