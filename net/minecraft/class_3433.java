package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3433 extends class_3415 {
	public class_3433(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		Supplier<TypeTemplate> supplier = () -> DSL.compoundList(class_3402.field_16598.in(schema), DSL.constType(DSL.intType()));
		schema.registerType(
			false,
			class_3402.field_16588,
			() -> DSL.optionalFields(
					"stats",
					DSL.optionalFields(
						"minecraft:mined",
						DSL.compoundList(class_3402.field_16597.in(schema), DSL.constType(DSL.intType())),
						"minecraft:crafted",
						(TypeTemplate)supplier.get(),
						"minecraft:used",
						(TypeTemplate)supplier.get(),
						"minecraft:broken",
						(TypeTemplate)supplier.get(),
						"minecraft:picked_up",
						(TypeTemplate)supplier.get(),
						DSL.optionalFields(
							"minecraft:dropped",
							(TypeTemplate)supplier.get(),
							"minecraft:killed",
							DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.intType())),
							"minecraft:killed_by",
							DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.intType())),
							"minecraft:custom",
							DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType()))
						)
					)
				)
		);
	}
}
