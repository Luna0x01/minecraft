package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3425 extends class_3415 {
	public class_3425(int i, Schema schema) {
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
						DSL.list(DSL.optionalFields("Palette", DSL.list(class_3402.field_16593.in(schema))))
					)
				)
		);
	}
}
