package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema1451v7 extends IdentifierNormalizingSchema {
	public Schema1451v7(int i, Schema schema) {
		super(i, schema);
	}

	public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
		super.registerTypes(schema, entityTypes, blockEntityTypes);
		schema.registerType(
			false,
			TypeReferences.STRUCTURE_FEATURE,
			() -> DSL.optionalFields(
					"Children",
					DSL.list(
						DSL.optionalFields(
							"CA",
							TypeReferences.BLOCK_STATE.in(schema),
							"CB",
							TypeReferences.BLOCK_STATE.in(schema),
							"CC",
							TypeReferences.BLOCK_STATE.in(schema),
							"CD",
							TypeReferences.BLOCK_STATE.in(schema)
						)
					)
				)
		);
	}
}
