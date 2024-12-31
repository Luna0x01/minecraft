package net.minecraft;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class class_3421 extends class_3415 {
	public class_3421(int i, Schema schema) {
		super(i, schema);
	}

	public Map<String, Supplier<TypeTemplate>> registerBlockEntities(Schema schema) {
		Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
		schema.registerSimple(map, "minecraft:bed");
		return map;
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map2) {
		super.registerTypes(schema, map, map2);
		schema.registerType(
			false,
			class_3402.field_16590,
			() -> DSL.optionalFields(
					"minecraft:adventure/adventuring_time",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16604.in(schema), DSL.constType(DSL.string()))),
					"minecraft:adventure/kill_a_mob",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.string()))),
					"minecraft:adventure/kill_all_mobs",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.string()))),
					"minecraft:husbandry/bred_all_animals",
					DSL.optionalFields("criteria", DSL.compoundList(class_3402.field_16594.in(schema), DSL.constType(DSL.string())))
				)
		);
		schema.registerType(false, class_3402.field_16604, () -> DSL.constType(DSL.namespacedString()));
		schema.registerType(false, class_3402.field_16594, () -> DSL.constType(DSL.namespacedString()));
	}
}
