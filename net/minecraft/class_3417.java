package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.function.Supplier;

public class class_3417 extends Schema {
	public class_3417(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			true,
			class_3402.field_16592,
			() -> DSL.hook(
					DSL.optionalFields(
						"id",
						class_3402.field_16598.in(schema),
						"tag",
						DSL.optionalFields(
							"EntityTag",
							class_3402.field_16595.in(schema),
							"BlockEntityTag",
							class_3402.field_16591.in(schema),
							"CanDestroy",
							DSL.list(class_3402.field_16597.in(schema)),
							"CanPlaceOn",
							DSL.list(class_3402.field_16597.in(schema))
						)
					),
					class_3450.field_16625,
					HookFunction.IDENTITY
				)
		);
	}
}
