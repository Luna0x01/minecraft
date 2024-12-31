package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3434 extends class_3415 {
	public class_3434(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_3402.field_16600,
			() -> DSL.optionalFields(
					"Children",
					DSL.list(
						DSL.optionalFields(
							"CA",
							class_3402.field_16593.in(schema),
							"CB",
							class_3402.field_16593.in(schema),
							"CC",
							class_3402.field_16593.in(schema),
							"CD",
							class_3402.field_16593.in(schema)
						)
					)
				)
		);
	}
}
