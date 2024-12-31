package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3418 extends Schema {
	public class_3418(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(false, class_3402.field_16603, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(
			false,
			class_3402.field_16583,
			() -> DSL.optionalFields(
					"RootVehicle",
					DSL.optionalFields("Entity", class_3402.field_16595.in(schema)),
					"Inventory",
					DSL.list(class_3402.field_16592.in(schema)),
					"EnderItems",
					DSL.list(class_3402.field_16592.in(schema)),
					DSL.optionalFields(
						"ShoulderEntityLeft",
						class_3402.field_16595.in(schema),
						"ShoulderEntityRight",
						class_3402.field_16595.in(schema),
						"recipeBook",
						DSL.optionalFields("recipes", DSL.list(class_3402.field_16603.in(schema)), "toBeDisplayed", DSL.list(class_3402.field_16603.in(schema)))
					)
				)
		);
		schema.registerType(false, class_3402.field_16585, () -> DSL.compoundList(DSL.list(class_3402.field_16592.in(schema))));
	}
}
