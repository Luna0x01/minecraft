package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3436 extends class_3415 {
	public class_3436(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_3402.field_16584,
			() -> DSL.fields(
					"Level",
					DSL.optionalFields(
						"Entities",
						DSL.list(class_3402.field_16595.in(schema)),
						"TileEntities",
						DSL.list(class_3402.field_16591.in(schema)),
						"TileTicks",
						DSL.list(DSL.fields("i", class_3402.field_16597.in(schema))),
						"Sections",
						DSL.list(DSL.optionalFields("Palette", DSL.list(class_3402.field_16593.in(schema)))),
						"Structures",
						DSL.optionalFields("Starts", DSL.compoundList(class_3402.field_16600.in(schema)))
					)
				)
		);
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
					),
					"biome",
					class_3402.field_16604.in(schema)
				)
		);
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		map.put("DUMMY", DSL::remainder);
		return map;
	}
}
